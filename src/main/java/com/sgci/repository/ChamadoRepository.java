package com.sgci.repository;

import com.sgci.enums.StatusChamado;
import com.sgci.model.Chamado;
import com.sgci.model.Equipamento;
import com.sgci.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    // Método para a busca inteligente de chamados abertos em um equipamento.
    // O nome longo demai se ta doido
    Optional<Chamado> findTopByEquipamentoAndStatusOrderByDataAberturaDesc(Equipamento equipamento, StatusChamado status);

    // Método para a listagem de chamados de um solicitante específico.
    // Precisa retornar um 'Page' pra funcionar com a paginação.
    Page<Chamado> findBySolicitante(Usuario solicitante, Pageable pageable);

}