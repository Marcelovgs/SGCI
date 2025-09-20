package com.sgci.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistrarUsuarioDTO(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String password
) {}