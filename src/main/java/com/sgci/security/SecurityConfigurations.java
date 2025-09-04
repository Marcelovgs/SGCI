package com.sgci.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Lembrete: Habilitei a segurança por método pra usar o @PreAuthorize.
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(req -> {
                    // Lembrete: As regras de URL ficam aqui. As de método ficam nos controllers.

                    // Rotas Públicas
                    req.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/auth/registrar").permitAll();

                    // Regras para Chamados
                    req.requestMatchers(HttpMethod.POST, "/chamados").hasRole("SOLICITANTE");
                    req.requestMatchers(HttpMethod.PUT, "/chamados/{id}/atribuir").hasRole("TECNICO");
                    req.requestMatchers(HttpMethod.POST, "/chamados/{id}/atualizacoes").hasAnyRole("TECNICO", "ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/chamados/{id}/fechar").hasAnyRole("TECNICO", "ADMIN");

                    // A regra para /equipamentos/** foi removida daqui, pois já está protegida pelo @PreAuthorize no controller.

                    // Qualquer outra requisição que não foi especificada acima precisa de autenticação.
                    req.anyRequest().authenticated();

                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}