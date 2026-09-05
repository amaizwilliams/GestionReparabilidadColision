package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "orden_etapa_fecha",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_orden_etapa",
                columnNames = {"id_orden_reparacion", "id_etapa"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class OrdenEtapaFecha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden_etapa_fecha")
    private Long idOrdenEtapaFecha;

    @ManyToOne
    @JoinColumn(name = "id_orden_reparacion", referencedColumnName = "id_orden_reparacion", nullable = false)
    private OrdenReparacion ordenReparacion;

    @ManyToOne
    @JoinColumn(name = "id_etapa", referencedColumnName = "id_etapa", nullable = false)
    private Etapas etapa;

    /* Tecnico que trabajo ESTA etapa puntual: base del futuro sistema de pagos. */
    @ManyToOne
    @JoinColumn(name = "id_tecnico", referencedColumnName = "id_tecnico")
    private Tecnico tecnico;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    /* La marca cambiarEtapa() al cerrar la etapa anterior. Nunca se fija desde la UI. */
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "completada", nullable = false)
    private Boolean completada = false;

    public void marcarFechaInicio() {
        this.fechaInicio = LocalDateTime.now();
    }

    public void marcarFechaCompletada() {
        this.completada = true;
    }

    public void asignarTecnicoEtapa(Tecnico tecnico) {
        this.tecnico = tecnico;
    }
}
