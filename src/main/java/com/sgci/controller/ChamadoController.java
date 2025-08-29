package com.sgci.controller;

import com.sgci.service.ChamadosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; // Import necessário
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController; // Import necessário

@RestController // Preciso dizer pro Spring que esta é uma classe de Controller REST.
@RequestMapping("/chamados") // Defino o caminho base para todos os endpoints deste controller.
public class ChamadoController {

    // Agora o Spring gerencia o Controller, então o @Autowired vai funcionar.
    @Autowired
    private ChamadosService chamadosService;

    // A URL final para este método será: GET /chamados/busca?tag=...
    @GetMapping("/busca")
    public ResponseEntity<?> buscarChamadoAbertoPorTagEquipamento(@RequestParam String tag) {
        var chamadoDTO = chamadosService.buscarChamadoAbertoPorTag(tag);

        if (chamadoDTO.isPresent()) {
            return ResponseEntity.ok(chamadoDTO.get());
        }

        return ResponseEntity.noContent().build();
    }

    // TODO: Adicionar os outros endpoints (POST para criar, GET para listar, etc.)
}