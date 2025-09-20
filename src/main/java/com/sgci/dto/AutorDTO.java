package com.sgci.dto;

import com.sgci.model.Usuario;

// Lembrete: DTO reutilizável para não expor a entidade Usuario inteira.
// Só o que eu preciso: id e nome.
public record AutorDTO(Long id, String nome) {
    public AutorDTO(Usuario autor) {
        this(autor.getId(), autor.getNome());
    }
}