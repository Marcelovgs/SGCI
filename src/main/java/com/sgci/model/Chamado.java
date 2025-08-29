package com.sgci.model;

import com.sgci.enums.Prioridade;
import com.sgci.enums.StatusChamado;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chamados")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class Chamado {
    // Entidade principal. Conecta tudo.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusChamado status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    private LocalDateTime dataAbertura = LocalDateTime.now();
    private LocalDateTime dataConclusao;

    // fetch=LAZY é importante pra performance, não carrega o usuário junto sempre.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnico; // Nulo no início.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    // 'mappedBy' diz que a classe AtualizacaoChamado gerencia a chave estrangeira.
    // Cascade ALL pra apagar as atualizações junto com o chamado.
    @OneToMany(mappedBy = "chamado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AtualizacaoChamado> atualizacoes = new ArrayList<>();
}