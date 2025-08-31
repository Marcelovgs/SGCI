package com.sgci.controller;
import com.sgci.model.Usuario;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.sgci.dto.CriarChamadoDTO;
import com.sgci.dto.DetalhesChamadoDTO;
import com.sgci.model.Usuario;
import com.sgci.service.ChamadosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/chamados") //
public class ChamadoController {

    @Autowired
    private ChamadosService chamadosService;

    @PostMapping
    @Transactional
    public ResponseEntity<DetalhesChamadoDTO> criarChamado(@RequestBody @Valid CriarChamadoDTO dados, @AuthenticationPrincipal Usuario solicitante, UriComponentsBuilder uriBuilder) {
        var novoChamado = chamadosService.criar(dados, solicitante);
        var uri = uriBuilder.path("/chamados/{id}").buildAndExpand(novoChamado.getId()).toUri();
        return ResponseEntity.created(uri).body(new DetalhesChamadoDTO(novoChamado));
    }

    @GetMapping("/busca")
    public ResponseEntity<?> buscarChamadoAbertoPorTagEquipamento(@RequestParam String tag) {
        var chamadoDTO = chamadosService.buscarChamadoAbertoPorTag(tag);
        if (chamadoDTO.isPresent()) {
            return ResponseEntity.ok(chamadoDTO.get());
        }
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<DetalhesChamadoDTO> detalharChamado(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        var dto = chamadosService.detalharChamado(id, usuarioLogado);
        return ResponseEntity.ok(dto);
    }
}