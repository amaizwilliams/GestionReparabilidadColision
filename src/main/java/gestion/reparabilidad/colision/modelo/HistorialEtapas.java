package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_etapas")
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

    //constructores

    public HistorialEtapas(Long idHistorialEtapas, OrdenReparacion ordenReparacion, Etapas etapa, Usuario usuario, LocalDateTime fechaCambio) {
        this.idHistorialEtapas = idHistorialEtapas;
        this.ordenReparacion = ordenReparacion;
        this.etapa = etapa;
        this.usuario = usuario;
        this.fechaCambio = fechaCambio;
    }

    public HistorialEtapas() {
    }

    //getters and setter

    public Long getIdHistorialEtapas() {
        return idHistorialEtapas;
    }

    public void setIdHistorialEtapas(Long idHistorialEtapas) {
        this.idHistorialEtapas = idHistorialEtapas;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
}
