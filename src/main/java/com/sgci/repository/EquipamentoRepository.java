package com.sgci.repository;
import com.sgci.enums.StatusEquipamento;
import java.util.List;
import org.springframework.data.domain.Sort;
import com.sgci.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Lembrete: Esta interface estende JpaRepository, o que dá a ela
// todos os métodos de CRUD (save, findById, findAll, deleteById, etc.) automaticamente.
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    // Lembrete: Método customizado que usei na tela de novo chamado para
    // verificar se já existe um chamado aberto para um equipamento.
    Optional<Equipamento> findByTag(String tag);

    // Lembrete: Método para buscar todos os equipamentos com um status específico.
    List<Equipamento> findAllByStatus(StatusEquipamento status, Sort sort);

}