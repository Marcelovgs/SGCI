package com.sgci.enums;

// Ciclo de vida de um chamado. Mantém o status consistente.
public enum StatusChamado {
    ABERTO,
    EM_ANDAMENTO,
    AGUARDANDO_PECA,
    CONCLUIDO,
    CANCELADO
}