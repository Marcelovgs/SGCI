package com.sgci.controller;
import com.sgci.dto.AdicionarAtualizacaoDTO;
import com.sgci.dto.CriarChamadoDTO;
import com.sgci.dto.DetalhesChamadoDTO;
import com.sgci.dto.FecharChamadoDTO;
import com.sgci.model.Usuario;
import com.sgci.service.ChamadosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    @Autowired
    private ChamadosService chamadosService;

    // Endpoint para criar um novo chamado.
    @PostMapping
    @Transactional
    public ResponseEntity<DetalhesChamadoDTO> criarChamado(@RequestBody @Valid CriarChamadoDTO dados, @AuthenticationPrincipal Usuario solicitante, UriComponentsBuilder uriBuilder) {
        var novoChamado = chamadosService.criar(dados, solicitante);
        var uri = uriBuilder.path("/chamados/{id}").buildAndExpand(novoChamado.getId()).toUri();
        return ResponseEntity.created(uri).body(new DetalhesChamadoDTO(novoChamado));
    }

    // Endpoint para detalhar um chamado específico pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<DetalhesChamadoDTO> detalharChamado(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        var dto = chamadosService.detalharChamado(id, usuarioLogado);
        return ResponseEntity.ok(dto);
    }


    @GetMapping
    public ResponseEntity<Page<DetalhesChamadoDTO>> listarChamados(
            @AuthenticationPrincipal Usuario usuarioLogado,
            // Lembrete: @PageableDefault define um padrão se o frontend não mandar nada.
            //padrão é 10 itens por página, ordenados pela data de abertura.
            @PageableDefault(size = 10, sort = {"dataAbertura"}) Pageable paginacao) {

        // A lógica de verdade fica no service, o controller só repassa e devolve o OK.
        var paginaDeChamados = chamadosService.listarChamados(usuarioLogado, paginacao);
        return ResponseEntity.ok(paginaDeChamados);
    }

    // Endpoint antigo de busca por tag. Posso manter ou remover depois.
    @GetMapping("/busca")
    public ResponseEntity<?> buscarChamadoAbertoPorTagEquipamento(@RequestParam String tag) {
        var chamadoDTO = chamadosService.buscarChamadoAbertoPorTag(tag);
        if (chamadoDTO.isPresent()) {
            return ResponseEntity.ok(chamadoDTO.get());
        }
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/atribuir")
    @Transactional
    public ResponseEntity<DetalhesChamadoDTO> atribuirChamado(@PathVariable Long id, @AuthenticationPrincipal Usuario tecnicoLogado) {
        // O controller só orquestra, a lógica pesada fica no service.
        var dto = chamadosService.atribuirChamado(id, tecnicoLogado);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/atualizacoes")
    @Transactional
    public ResponseEntity<DetalhesChamadoDTO> adicionarAtualizacao(@PathVariable Long id, @RequestBody @Valid AdicionarAtualizacaoDTO dados, @AuthenticationPrincipal Usuario autor) {
        var dto = chamadosService.adicionarAtualizacao(id, dados, autor);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/fechar")
    @Transactional
    public ResponseEntity<DetalhesChamadoDTO> fecharChamado(@PathVariable Long id, @RequestBody @Valid FecharChamadoDTO dados, @AuthenticationPrincipal Usuario tecnicoLogado) {
        var dto = chamadosService.fecharChamado(id, dados, tecnicoLogado);
        return ResponseEntity.ok(dto);
    }
}