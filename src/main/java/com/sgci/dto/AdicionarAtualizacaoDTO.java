package com.sgci.dto;

import jakarta.validation.constraints.NotBlank;

public record AdicionarAtualizacaoDTO(
        @NotBlank(message = "A descrição não pode estar em branco")
        String descricao
) {}