package com.sgci.controller;
import com.sgci.dto.DadosCadastroEquipamento;
import com.sgci.enums.StatusChamado;
import com.sgci.enums.StatusEquipamento;
import com.sgci.model.Equipamento;
import com.sgci.repository.ChamadoRepository;
import com.sgci.repository.EquipamentoRepository;
import com.sgci.service.EquipamentoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/equipamentos")
public class EquipamentoController {

    @Autowired
    private EquipamentoRepository repository;

    @Autowired
    private EquipamentoService equipamentoService;


    // Lembrete: Preciso injetar o ChamadoRepository para usá-lo na verificação de exclusão.
    @Autowired
    private ChamadoRepository chamadoRepository;

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Equipamento> cadastrarEquipamento(@RequestBody @Valid DadosCadastroEquipamento dados, UriComponentsBuilder uriBuilder) {
        var equipamento = new Equipamento();
        equipamento.setTag(dados.tag());
        equipamento.setTipo(dados.tipo());
        equipamento.setLocalizacao(dados.localizacao());
        repository.save(equipamento);
        var uri = uriBuilder.path("/equipamentos/{id}").buildAndExpand(equipamento.getId()).toUri();
        return ResponseEntity.created(uri).body(equipamento);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Equipamento>> listarEquipamentos(@RequestParam(required = false) StatusEquipamento status) {
        List<Equipamento> lista;
        Sort sort = Sort.by("tag");

        if (status == null) {
            lista = repository.findAllByStatus(StatusEquipamento.ATIVO, sort);
        } else {
            lista = repository.findAllByStatus(status, sort);
        }
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}/inativar")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> inativarEquipamento(@PathVariable Long id) {
        var equipamento = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Equipamento não encontrado"));
        equipamento.setStatus(StatusEquipamento.INATIVO);
        repository.save(equipamento);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirEquipamento(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<StatusChamado> statusAtivos = List.of(StatusChamado.ABERTO, StatusChamado.EM_ANDAMENTO);

        if (chamadoRepository.existsByEquipamentoIdAndStatusIn(id, statusAtivos)) {
            throw new IllegalStateException("Não é possível excluir: equipamento possui chamados abertos ou em andamento.");
        }

        equipamentoService.excluirEquipamento(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Equipamento> atualizarEquipamento(@PathVariable Long id, @RequestBody @Valid DadosCadastroEquipamento dados) {
        var equipamento = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Equipamento não encontrado"));
        equipamento.setTag(dados.tag());
        equipamento.setTipo(dados.tipo());
        equipamento.setLocalizacao(dados.localizacao());
        repository.save(equipamento);
        return ResponseEntity.ok(equipamento);
    }
}