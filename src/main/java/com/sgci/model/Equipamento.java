package com.sgci.model;
import com.sgci.enums.StatusEquipamento;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipamentos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class Equipamento {
    // Entidade para as câmeras, sensores, etc.
    // A 'tag' é o identificador físico, precisa ser único.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tag;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String localizacao;

    @Enumerated(EnumType.STRING)
    private StatusEquipamento status = StatusEquipamento.ATIVO;
}