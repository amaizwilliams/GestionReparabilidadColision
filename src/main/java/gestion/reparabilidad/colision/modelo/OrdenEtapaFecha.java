package gestion.reparabilidad.colision.modelo;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orden_etapa_fecha")

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

    @ManyToOne
    @JoinColumn(name = "id_tecnico", referencedColumnName = "id_tecnico")
    private Tecnico tecnico;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "completada", nullable = false)
    private Boolean completada = false;

    public OrdenEtapaFecha(Long idOrdenEtapaFecha, OrdenReparacion ordenReparacion, Etapas etapa, Tecnico tecnico, LocalDateTime fechaInicio, LocalDateTime fechaFin, Boolean completada) {
        this.idOrdenEtapaFecha = idOrdenEtapaFecha;
        this.ordenReparacion = ordenReparacion;
        this.etapa = etapa;
        this.tecnico = tecnico;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.completada = completada;
    }

    public OrdenEtapaFecha() {
    }

    public Long getIdOrdenEtapaFecha() {
        return idOrdenEtapaFecha;
    }

    public void setIdOrdenEtapaFecha(Long idOrdenEtapaFecha) {
        this.idOrdenEtapaFecha = idOrdenEtapaFecha;
    }

    public OrdenReparacion getOrdenReparacion() {
        return ordenReparacion;
    }

    public void setOrdenReparacion(OrdenReparacion ordenReparacion) {
        this.ordenReparacion = ordenReparacion;
    }

    public Etapas getEtapa() {
        return etapa;
    }

    public void setEtapa(Etapas etapa) {
        this.etapa = etapa;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getCompletada() {
        return completada;
    }

    public void setCompletada(Boolean completada) {
        this.completada = completada;
    }
}
