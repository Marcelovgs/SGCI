package com.sgci.controller;
import com.sgci.dto.DadosCadastroEquipamento;
import com.sgci.model.Equipamento;
import com.sgci.repository.EquipamentoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/equipamentos")
@PreAuthorize("hasRole('ADMIN')") // Trava de segurança para a classe inteira: só ADMIN mexe aqui (Floki Risada)
public class EquipamentoController {

    @Autowired
    private EquipamentoRepository repository;

    @PostMapping
    @Transactional
    // Lembrete: Mudei o retorno pra 201 Created, que é o padrão pra criação de recursos.
    public ResponseEntity<Equipamento> cadastrarEquipamento(@RequestBody @Valid DadosCadastroEquipamento dados, UriComponentsBuilder uriBuilder) {
        var equipamento = new Equipamento();
        equipamento.setTag(dados.tag());
        equipamento.setTipo(dados.tipo());
        equipamento.setLocalizacao(dados.localizacao());
        repository.save(equipamento);

        var uri = uriBuilder.path("/equipamentos/{id}").buildAndExpand(equipamento.getId()).toUri();
        return ResponseEntity.created(uri).body(equipamento);
    }

    // >> MÉTODO DE LISTAGEM ADICIONADO AQUI <<
    @GetMapping
    public ResponseEntity<Page<Equipamento>> listarEquipamentos(@PageableDefault(size = 10, sort = {"tag"}) Pageable paginacao) {
        var pagina = repository.findAll(paginacao);
        return ResponseEntity.ok(pagina);
    }
}