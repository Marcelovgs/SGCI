package com.sgci.controller;

import com.sgci.dto.DadosCadastroEquipamento;
import com.sgci.model.Equipamento;
import com.sgci.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/equipamentos")
@PreAuthorize("hasRole('ROLE_ADMIN')") // <-- Trava de segurança: só ADMIN mexe aqui
public class EquipamentoController {

    @Autowired
    private EquipamentoRepository repository;

    @PostMapping
    public ResponseEntity<Equipamento> cadastrarEquipamento(@RequestBody DadosCadastroEquipamento dados) {
        var equipamento = new Equipamento();
        equipamento.setTag(dados.tag());
        equipamento.setTipo(dados.tipo());
        equipamento.setLocalizacao(dados.localizacao());
        repository.save(equipamento);
        return ResponseEntity.ok(equipamento);
    }

    // TODO: Adicionar métodos GET, PUT, DELETE aqui depois.
}