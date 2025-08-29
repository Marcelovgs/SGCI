package com.sgci.dto;

import com.sgci.enums.Prioridade;
import com.sgci.enums.StatusChamado;
import com.sgci.model.Chamado; // Importe a sua entidade Chamado

import java.time.LocalDateTime;

// Este DTO é o "rosto" do chamado
public record DetalhesChamadoDTO(
        Long id,
        String titulo,
        String descricao,         // <-- CAMPO ADICIONADO
        StatusChamado status,
        Prioridade prioridade,
        LocalDateTime dataAbertura,
        String nomeSolicitante,   // <-- CAMPO ADICIONADO
        String tagEquipamento
) {
    // Lembrete para mim mesmo: Este construtor é uma "fábrica" que transforma a
    // entidade completa (com senhas e dados sensíveis) em um DTO seguro para o frontend.
    public DetalhesChamadoDTO(Chamado chamado) {
        this(
                chamado.getId(),
                chamado.getTitulo(),
                chamado.getDescricao(), // Mapeando a descrição completa
                chamado.getStatus(),
                chamado.getPrioridade(),
                chamado.getDataAbertura(),
                chamado.getSolicitante().getNome(), // Mapeando o nome de quem abriu
                chamado.getEquipamento().getTag()
        );
    }
}