package com.sgci.dto;

import jakarta.validation.constraints.NotBlank;

public record FecharChamadoDTO(
        @NotBlank(message = "A nota de resolução é obrigatória para fechar o chamado.")
        String notaDeResolucao
) {}