package com.sgci.dto;

import jakarta.validation.constraints.NotBlank;

// DTO para receber os dados de criação/atualização de um equipamento.
public record DadosCadastroEquipamento(
        @NotBlank
        String tag,
        @NotBlank
        String tipo,
        @NotBlank
        String localizacao
) {}