package com.sgci.repository;

import com.sgci.enums.StatusChamado;
import com.sgci.model.Chamado;
import com.sgci.model.Equipamento;
import com.sgci.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    Page<Chamado> findBySolicitante(Usuario solicitante, Pageable pageable);

    @Query("SELECT c FROM Chamado c WHERE c.equipamento = :equipamento AND c.status = :status ORDER BY c.dataAbertura DESC LIMIT 1")
    Optional<Chamado> findChamadoAbertoPorEquipamento(@Param("equipamento") Equipamento equipamento, @Param("status") StatusChamado status);

    List<Chamado> findAllByEquipamentoId(Long equipamentoId);


    boolean existsByEquipamentoIdAndStatusIn(Long equipamentoId, List<StatusChamado> statuses);
}