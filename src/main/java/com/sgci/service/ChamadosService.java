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

    // Lógica da busca por tag que fizemos antes.
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

    // ========================================================================
    // >> MÉTODO NOVO ADICIONADO AQUI <<
    // ========================================================================
    @Transactional(readOnly = true)
    public DetalhesChamadoDTO detalharChamado(Long id, Usuario usuarioLogado) {
        // Lembrete: Primeiro, busco o chamado. Se não existir, o orElseThrow já retorna um 404 (com o TratadorDeErros).
        var chamado = chamadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado"));

        // Lembrete: Esta é a regra de negócio de segurança.
        // Se quem está logado for um SOLICITANTE, preciso checar se ele é o dono do chamado.
        if (usuarioLogado.getRole() == Role.ROLE_SOLICITANTE && !chamado.getSolicitante().equals(usuarioLogado)) {
            // Se não for o dono, lanço uma exceção de acesso negado. O TratadorDeErros vai transformar isso em 403.
            throw new AccessDeniedException("Acesso negado: Este chamado não pertence a você.");
        }

        // Se o usuário for o dono (ou for TECNICO/ADMIN), a verificação passa e eu retorno o DTO.
        return new DetalhesChamadoDTO(chamado);
    }
}