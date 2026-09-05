package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/* Log de auditoria inmutable: solo se insertan registros, nunca se actualizan. */
@Entity
@Table(name = "historial_etapas")
@Getter
@Setter
@NoArgsConstructor
public class HistorialEtapas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial_etapas")
    private Long idHistorialEtapas;

    @ManyToOne
    @JoinColumn(name = "id_orden_reparacion", referencedColumnName = "id_orden_reparacion", nullable = false)
    private OrdenReparacion ordenReparacion;

    @ManyToOne
    @JoinColumn(name = "id_etapa", referencedColumnName = "id_etapa", nullable = false)
    private Etapas etapa;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @Column(name = "comentario", length = 255)
    private String comentario;
}
