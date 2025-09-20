package com.sgci.dto;
import com.sgci.enums.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO para receber os dados crus do frontend pra criar um novo chamado.
public record CriarChamadoDTO(
        @NotBlank
        String titulo,

        @NotBlank
        String descricao,

        @NotNull
        Prioridade prioridade,

        @NotNull
        Long equipamentoId
) {}