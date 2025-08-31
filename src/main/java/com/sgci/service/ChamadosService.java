package com.sgci.service;

import com.sgci.dto.CriarChamadoDTO;
import com.sgci.dto.DetalhesChamadoDTO;
import com.sgci.enums.StatusChamado;
import com.sgci.model.Chamado;
import com.sgci.model.Usuario;
import com.sgci.repository.ChamadoRepository;
import com.sgci.repository.EquipamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class ChamadosService {

    // Preciso injetar os repositórios para acessar o banco.
    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    // Lógica para criar um chamado. Precisa ser transacional pra garantir a consistência do banco.
    @Transactional
    public Chamado criar(CriarChamadoDTO dados, Usuario solicitante) {
        // Primeiro, preciso validar se o equipamento informado existe.
        var equipamento = equipamentoRepository.findById(dados.equipamentoId())
                .orElseThrow(() -> new EntityNotFoundException("Equipamento não encontrado!"));

        // Crio a nova entidade Chamado com os dados recebidos.
        var novoChamado = new Chamado();
        novoChamado.setTitulo(dados.titulo());
        novoChamado.setDescricao(dados.descricao());
        novoChamado.setPrioridade(dados.prioridade());
        novoChamado.setEquipamento(equipamento);
        novoChamado.setSolicitante(solicitante);
        novoChamado.setStatus(StatusChamado.ABERTO); // Status inicial é sempre ABERTO.

        // Salvo no banco.
        return chamadoRepository.save(novoChamado);
    }

    // Lógica da busca que fiz antes.
    @Transactional(readOnly = true)
    public Optional<DetalhesChamadoDTO> buscarChamadoAbertoPorTag(String tag) {
        var equipamentoOptional = equipamentoRepository.findByTag(tag);
        if (equipamentoOptional.isEmpty()) {
            return Optional.empty();
        }
        var chamadoOptional = chamadoRepository.findTopByEquipamentoAndStatusOrderByDataAberturaDesc(
                equipamentoOptional.get(),
                StatusChamado.ABERTO
        );
        return chamadoOptional.map(DetalhesChamadoDTO::new);
    }
}