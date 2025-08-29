package com.sgci.repository;

import com.sgci.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    // Método para buscar um equipamento pela sua tag única.
    Optional<Equipamento> findByTag(String tag);
}