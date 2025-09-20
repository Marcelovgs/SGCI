package com.sgci.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Este record cria automaticamente os métodos email() e password()
public record LoginDTO(
        @NotBlank @Email String email,
        @NotBlank String password
) {}