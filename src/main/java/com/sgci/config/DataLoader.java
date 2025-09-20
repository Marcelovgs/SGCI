package com.sgci.config;

import com.sgci.model.Equipamento;
import com.sgci.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// @Component diz ao Spring para gerenciar esta classe e executá-la.
@Component
public class DataLoader implements CommandLineRunner {

    // Preciso do repositório para salvar os dados no banco.
    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verificação: só executa a carga se a tabela de equipamentos estiver vazia.
        // Isso impede que 500 novas câmeras sejam criadas toda vez que você reiniciar a aplicação.
        if (equipamentoRepository.count() == 0) {
            System.out.println("Nenhum equipamento encontrado. Realizando carga inicial...");

            for (int i = 1; i <= 500; i++) {
                Equipamento equipamento = new Equipamento();

                // Formata o número para ter 3 dígitos (001, 002, ..., 123)
                String tagNumerica = String.format("%03d", i);

                equipamento.setTag("CAM-" + tagNumerica);
                equipamento.setTipo("Câmera Dome IP 5MP");
                equipamento.setLocalizacao("Pátio " + ((i % 4) + 1) + " - Setor " + (char)('A' + (i % 10)));

                equipamentoRepository.save(equipamento);
            }
            System.out.println("Carga inicial de 500 câmeras concluída.");
        } else {
            System.out.println("Equipamentos já cadastrados. Carga inicial não necessária.");
        }
    }
}