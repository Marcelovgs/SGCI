package com.sgci.dto;

import com.sgci.enums.Prioridade;
import com.sgci.enums.StatusChamado;
import com.sgci.model.Chamado;

import java.time.LocalDateTime;

// DTO pra devolver uma visão segura e formatada de um chamado para o frontend.
public record DetalhesChamadoDTO(
        Long id,
        String titulo,
        String descricao,
        StatusChamado status,
        Prioridade prioridade,
        LocalDateTime dataAbertura,
        String nomeSolicitante,
        String tagEquipamento
) {
    // Construtor que "traduz" a entidade para este DTO. Facilita muito lol.
    public DetalhesChamadoDTO(Chamado chamado) {
        this(
                chamado.getId(),
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getStatus(),
                chamado.getPrioridade(),
                chamado.getDataAbertura(),
                chamado.getSolicitante().getNome(),
                chamado.getEquipamento().getTag()
        );
    }
}