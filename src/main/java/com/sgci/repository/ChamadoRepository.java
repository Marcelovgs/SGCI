package com.sgci.repository;

import com.sgci.enums.StatusChamado;
import com.sgci.model.Chamado;
import com.sgci.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {
    // Método que busca o chamado mais recente (Top 1) para um dado equipamento e status.
    Optional<Chamado> findTopByEquipamentoAndStatusOrderByDataAberturaDesc(Equipamento equipamento, StatusChamado status);
}