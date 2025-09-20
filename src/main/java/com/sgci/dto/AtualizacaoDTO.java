package com.sgci.dto;

import com.sgci.model.AtualizacaoChamado;
import java.time.LocalDateTime;

// Lembrete: DTO para formatar cada linha do histórico de atualizações.
public record AtualizacaoDTO(Long id, String descricao, LocalDateTime data, AutorDTO autor) {
    public AtualizacaoDTO(AtualizacaoChamado atualizacao) {
        this(
                atualizacao.getId(),
                atualizacao.getDescricao(),
                atualizacao.getData(),
                new AutorDTO(atualizacao.getAutor())
        );
    }
}