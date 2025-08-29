package com.sgci.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    // Preciso liberar o acesso pro meu front em React que vai rodar na porta 3000.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Libera pra todos os endpoints
                .allowedOrigins("http://localhost:3000") // Permite só do meu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}