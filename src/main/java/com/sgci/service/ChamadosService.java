package com.sgci.service;

import com.sgci.dto.AdicionarAtualizacaoDTO;
import com.sgci.dto.CriarChamadoDTO;
import com.sgci.dto.DetalhesChamadoDTO;
import com.sgci.dto.FecharChamadoDTO;
import com.sgci.enums.Role;
import com.sgci.enums.StatusChamado;
import com.sgci.model.AtualizacaoChamado;
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

import java.time.LocalDateTime;
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

    @Transactional
    public DetalhesChamadoDTO atribuirChamado(Long idChamado, Usuario tecnicoLogado) {
        // Busco o chamado no banco. Se não existir, lança erro.
        var chamado = chamadoRepository.findById(idChamado)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado"));

        // Regras de negócio: checar se o chamado já não tem técnico e se está ABERTO.
        if (chamado.getTecnico() != null) {
            throw new IllegalStateException("Este chamado já foi atribuído a outro técnico.");
        }
        if (chamado.getStatus() != StatusChamado.ABERTO) {
            throw new IllegalStateException("Este chamado não está com status ABERTO e não pode ser atribuído.");
        }

        // Se passou nas validações, eu atribuo o técnico logado e mudo o status.
        chamado.setTecnico(tecnicoLogado);
        chamado.setStatus(StatusChamado.EM_ANDAMENTO);

        //  adicionei uma atualização no histórico do chamado para registrar a ação.
        var atualizacao = new AtualizacaoChamado();
        atualizacao.setDescricao("Chamado atribuído ao técnico: " + tecnicoLogado.getNome());
        atualizacao.setChamado(chamado);
        atualizacao.setAutor(tecnicoLogado);
        chamado.getAtualizacoes().add(atualizacao);

        // O save() aqui é mais pra ser explícito, já que o @Transactional cuidaria disso.
        // Mas como não busquei a entidade de novo, é melhor garantir.
        chamadoRepository.save(chamado);

        return new DetalhesChamadoDTO(chamado);
    }

    @Transactional
    public DetalhesChamadoDTO adicionarAtualizacao(Long idChamado, AdicionarAtualizacaoDTO dados, Usuario autor) {
        // Busco o chamado no banco. Se não existir, lança erro.
        var chamado = chamadoRepository.findById(idChamado)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado"));

        // Regra de negócio: Apenas o técnico atribuído ao chamado (ou um admin) pode adicionar atualizações.
        // Lembrete: Adicionar a lógica para o admin depois, por enquanto só o técnico.
        if (chamado.getTecnico() == null || !chamado.getTecnico().equals(autor)) {
            throw new AccessDeniedException("Acesso negado: você não é o técnico atribuído a este chamado.");
        }

        // Crio a nova entidade de atualização.
        var novaAtualizacao = new AtualizacaoChamado();
        novaAtualizacao.setDescricao(dados.descricao());
        novaAtualizacao.setChamado(chamado);
        novaAtualizacao.setAutor(autor);

        // Adiciono a nova atualização à lista de atualizações do chamado.
        // Como a relação tem Cascade.ALL, o JPA vai salvar a nova atualização junto.
        chamado.getAtualizacoes().add(novaAtualizacao);

        // Salvo o chamado para garantir que a atualização seja persistida.
        chamadoRepository.save(chamado);

        return new DetalhesChamadoDTO(chamado);
    }

    @Transactional
    public DetalhesChamadoDTO fecharChamado(Long idChamado, FecharChamadoDTO dados, Usuario tecnicoLogado) {
        // Busco o chamado no banco.
        var chamado = chamadoRepository.findById(idChamado)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado"));

        // Regra de negócio: Apenas o técnico atribuído pode fechar o chamado.
        if (chamado.getTecnico() == null || !chamado.getTecnico().equals(tecnicoLogado)) {
            throw new AccessDeniedException("Acesso negado: você não é o técnico responsável por este chamado.");
        }

        // Regra de negócio: Só posso fechar um chamado que está EM_ANDAMENTO.
        if (chamado.getStatus() != StatusChamado.EM_ANDAMENTO) {
            throw new IllegalStateException("Apenas chamados com status EM_ANDAMENTO podem ser fechados.");
        }

        // Se passou nas validações, atualizo o chamado.
        chamado.setStatus(StatusChamado.CONCLUIDO);
        chamado.setDataConclusao(LocalDateTime.now());

        // Adiciono a nota de resolução como a última atualização no histórico.
        var atualizacaoFinal = new AtualizacaoChamado();
        atualizacaoFinal.setDescricao("Chamado concluído. Resolução: " + dados.notaDeResolucao());
        atualizacaoFinal.setChamado(chamado);
        atualizacaoFinal.setAutor(tecnicoLogado);
        chamado.getAtualizacoes().add(atualizacaoFinal);

        chamadoRepository.save(chamado);

        return new DetalhesChamadoDTO(chamado);
    }
}