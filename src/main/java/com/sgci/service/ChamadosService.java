package com.sgci.service;
import com.sgci.model.Chamado;
import com.sgci.dto.AdicionarAtualizacaoDTO;
import com.sgci.dto.CriarChamadoDTO;
import com.sgci.dto.DetalhesChamadoDTO;
import com.sgci.dto.FecharChamadoDTO;
import com.sgci.enums.Role;
import com.sgci.enums.StatusChamado;
import com.sgci.model.AtualizacaoChamado;
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
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class ChamadosService {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

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

    @Transactional(readOnly = true)
    public Optional<DetalhesChamadoDTO> buscarChamadoAbertoPorTag(String tag) {
        var equipamentoOptional = equipamentoRepository.findByTag(tag);
        if (equipamentoOptional.isEmpty()) {
            return Optional.empty();
        }


        // Lembrete: Chamar o método com o nome mais curto que defini no repositório.
        var chamadoOptional = chamadoRepository.findChamadoAbertoPorEquipamento(
                equipamentoOptional.get(),
                StatusChamado.ABERTO
        );

        return chamadoOptional.map(DetalhesChamadoDTO::new);
    }

    @Transactional(readOnly = true)
    public DetalhesChamadoDTO detalharChamado(Long id, Usuario usuarioLogado) {
        var chamado = chamadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado"));

        if (usuarioLogado.getRole() == Role.ROLE_SOLICITANTE && !chamado.getSolicitante().equals(usuarioLogado)) {
            throw new AccessDeniedException("Acesso negado: Este chamado não pertence a você.");
        }

        return new DetalhesChamadoDTO(chamado);
    }

    @Transactional(readOnly = true)
    public Page<DetalhesChamadoDTO> listarChamados(Usuario usuarioLogado, Pageable paginacao) {
        if (usuarioLogado.getRole() == Role.ROLE_SOLICITANTE) {
            return chamadoRepository.findBySolicitante(usuarioLogado, paginacao)
                    .map(DetalhesChamadoDTO::new);
        }

        return chamadoRepository.findAll(paginacao)
                .map(DetalhesChamadoDTO::new);
    }

    @Transactional
    public DetalhesChamadoDTO atribuirChamado(Long idChamado, Usuario tecnicoLogado) {
        var chamado = chamadoRepository.findById(idChamado)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado"));

        if (chamado.getTecnico() != null) {
            throw new IllegalStateException("Este chamado já foi atribuído a outro técnico.");
        }
        if (chamado.getStatus() != StatusChamado.ABERTO) {
            throw new IllegalStateException("Este chamado não está com status ABERTO e não pode ser atribuído.");
        }

        chamado.setTecnico(tecnicoLogado);
        chamado.setStatus(StatusChamado.EM_ANDAMENTO);

        var atualizacao = new AtualizacaoChamado();
        atualizacao.setDescricao("Chamado atribuído ao técnico: " + tecnicoLogado.getNome());
        atualizacao.setChamado(chamado);
        atualizacao.setAutor(tecnicoLogado);
        chamado.getAtualizacoes().add(atualizacao);

        chamadoRepository.save(chamado);
        return new DetalhesChamadoDTO(chamado);
    }

    @Transactional
    public DetalhesChamadoDTO adicionarAtualizacao(Long idChamado, AdicionarAtualizacaoDTO dados, Usuario autor) {
        var chamado = chamadoRepository.findById(idChamado)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado"));

        if (chamado.getTecnico() == null || !chamado.getTecnico().equals(autor)) {
            throw new AccessDeniedException("Acesso negado: você não é o técnico atribuído a este chamado.");
        }

        var novaAtualizacao = new AtualizacaoChamado();
        novaAtualizacao.setDescricao(dados.descricao());
        novaAtualizacao.setChamado(chamado);
        novaAtualizacao.setAutor(autor);

        chamado.getAtualizacoes().add(novaAtualizacao);
        chamadoRepository.save(chamado);

        return new DetalhesChamadoDTO(chamado);
    }

    @Transactional
    public DetalhesChamadoDTO fecharChamado(Long idChamado, FecharChamadoDTO dados, Usuario tecnicoLogado) {
        var chamado = chamadoRepository.findById(idChamado)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado"));

        if (chamado.getTecnico() == null || !chamado.getTecnico().equals(tecnicoLogado)) {
            throw new AccessDeniedException("Acesso negado: você não é o técnico responsável por este chamado.");
        }

        if (chamado.getStatus() != StatusChamado.EM_ANDAMENTO) {
            throw new IllegalStateException("Apenas chamados com status EM_ANDAMENTO podem ser fechados.");
        }

        chamado.setStatus(StatusChamado.CONCLUIDO);
        chamado.setDataConclusao(LocalDateTime.now());

        var atualizacaoFinal = new AtualizacaoChamado();
        atualizacaoFinal.setDescricao("Chamado concluído. Resolução: " + dados.notaDeResolucao());
        atualizacaoFinal.setChamado(chamado);
        atualizacaoFinal.setAutor(tecnicoLogado);
        chamado.getAtualizacoes().add(atualizacaoFinal);

        chamadoRepository.save(chamado);
        return new DetalhesChamadoDTO(chamado);
    }
}