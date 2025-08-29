package com.sgci.service;

import com.sgci.dto.DetalhesChamadoDTO;
import com.sgci.enums.StatusChamado;
import com.sgci.repository.ChamadoRepository;
import com.sgci.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service // <-- PASSO 1: Anotação que resolve o erro "not a valid Spring bean"
@Transactional(readOnly = true) // Boa prática: a maioria dos métodos será de leitura
public class ChamadosService {

    // Lembrete para mim mesmo: preciso injetar os repositórios para acessar o banco.
    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    // PASSO 2: Implementação do método que o controller precisa.
    // O retorno como Optional<DTO> vai resolver os erros de isPresent() e get().
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

    // TODO: Implementar os outros métodos de negócio aqui depois.
    // Ex: criar, listar, atualizarStatus, etc.
}