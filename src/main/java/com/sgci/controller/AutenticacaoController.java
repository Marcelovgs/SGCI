package com.sgci.controller;
import com.sgci.dto.LoginDTO;
import com.sgci.dto.RegistrarUsuarioDTO;
import com.sgci.dto.TokenDTO;
import com.sgci.enums.Role;
import com.sgci.model.Usuario;
import com.sgci.repository.UsuarioRepository;
import com.sgci.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    // ADICIONEI ESTAS DUAS INJEÇÕES QUE ESTAVAM FALTANDO PQ SOU BURRO E NAO APRENDO.
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity efetuarLogin(@RequestBody @Valid LoginDTO dados) {
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.password());
            var authentication = authenticationManager.authenticate(authenticationToken);
            var usuario = (Usuario) authentication.getPrincipal();

            var tokenJWT = tokenService.gerarToken(usuario);

            // token E a role.
            var tokenResponse = new TokenDTO(tokenJWT, usuario.getRole().name());

            return ResponseEntity.ok(tokenResponse);

        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Falha na autenticação: email ou senha inválidos.");
        }
    }

    @PostMapping("/registrar")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public void registrar(@RequestBody @Valid RegistrarUsuarioDTO dados) {
        if (usuarioRepository.findByEmail(dados.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado!");
        }

        var novoUsuario = new Usuario();
        novoUsuario.setNome(dados.nome());
        novoUsuario.setEmail(dados.email());
        novoUsuario.setPassword(passwordEncoder.encode(dados.password()));
        novoUsuario.setRole(Role.ROLE_SOLICITANTE);

        usuarioRepository.save(novoUsuario);
    }
}