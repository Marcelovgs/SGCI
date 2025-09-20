package com.sgci.dto;

import com.sgci.enums.Prioridade;
import com.sgci.enums.StatusChamado;
import com.sgci.model.Chamado;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record DetalhesChamadoDTO(
        Long id,
        String titulo,
        String descricao,
        StatusChamado status,
        Prioridade prioridade,
        LocalDateTime dataAbertura,
        String nomeSolicitante,
        String tagEquipamento,
        AutorDTO tecnico,
        List<AtualizacaoDTO> atualizacoes
) {
    public DetalhesChamadoDTO(Chamado chamado) {
        this(
                chamado.getId(),
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getStatus(),
                chamado.getPrioridade(),
                chamado.getDataAbertura(),
                chamado.getSolicitante().getNome(),
                // Lembrete: Se o equipamento for nulo (porque foi deletado),
                // eu exibo um texto padrão. Isso evita o NullPointerException.
                chamado.getEquipamento() != null ? chamado.getEquipamento().getTag() : "Equipamento Excluído",

                chamado.getTecnico() != null ? new AutorDTO(chamado.getTecnico()) : null,
                chamado.getAtualizacoes().stream().map(AtualizacaoDTO::new).collect(Collectors.toList())
        );
    }
}