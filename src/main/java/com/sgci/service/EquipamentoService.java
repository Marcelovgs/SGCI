package com.sgci.service;

import com.sgci.repository.ChamadoRepository;
import com.sgci.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipamentoService {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Transactional
    public void excluirEquipamento(Long id) {
        var chamadosVinculados = chamadoRepository.findAllByEquipamentoId(id);
        for (var chamado : chamadosVinculados) {
            chamado.setEquipamento(null);
        }
        equipamentoRepository.deleteById(id);
    }
}