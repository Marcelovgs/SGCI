package com.sgci.service;

import com.sgci.dto.CriarChamadoDTO;
import com.sgci.dto.DetalhesChamadoDTO;
import com.sgci.enums.Role;
import com.sgci.enums.StatusChamado;
import com.sgci.model.Chamado;
import com.sgci.model.Usuario;
import com.sgci.repository.ChamadoRepository;
import com.sgci.repository.EquipamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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

    // Lógica para criar um chamado.
    @Transactional
    public Chamado criar(CriarChamadoDTO dados, Usuario solicitante) {
        var equipamento = equipamentoRepository.findById(dados.equipamentoId())
                .orElseThrow(() -> new EntityNotFoundException("Equipamento não encontrado!"));

        var novoChamado = new Chamado();
        novoChamado.setTitulo(dados.titulo());
        novoChamado.setDescricao(dados.descricao());
        novoChamado.setPrioridade(dados.prioridade());
        novoChamado.setEquipamento(equipamento);
        novoChamado.setSolicitante(solicitante);
        novoChamado.setStatus(StatusChamado.ABERTO);

        return chamadoRepository.save(novoChamado);
    }

    // Lógica da busca por tag que fiz antes.
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

    // Lógica para detalhar um chamado específico.
    @Transactional(readOnly = true)
    public DetalhesChamadoDTO detalharChamado(Long id, Usuario usuarioLogado) {
        var chamado = chamadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado"));

        if (usuarioLogado.getRole() == Role.ROLE_SOLICITANTE && !chamado.getSolicitante().equals(usuarioLogado)) {
            throw new AccessDeniedException("Acesso negado: Este chamado não pertence a você.");
        }

        return new DetalhesChamadoDTO(chamado);
    }

    // Lógica da listagem "inteligente".
    @Transactional(readOnly = true)
    public Page<DetalhesChamadoDTO> listarChamados(Usuario usuarioLogado, Pageable paginacao) {
        if (usuarioLogado.getRole() == Role.ROLE_SOLICITANTE) {
            // Se for SOLICITANTE, busca só os dele.
            return chamadoRepository.findBySolicitante(usuarioLogado, paginacao)
                    .map(DetalhesChamadoDTO::new);
        }

        // Se for TECNICO ou ADMIN, pode ver tudo.
        return chamadoRepository.findAll(paginacao)
                .map(DetalhesChamadoDTO::new);
    }
}