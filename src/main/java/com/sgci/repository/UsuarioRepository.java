package com.sgci.repository;

import com.sgci.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método que o Spring Security vai usar pra encontrar o usuário pelo email.
    Optional<UserDetails> findByEmail(String email);
}