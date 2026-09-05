package gestion.reparabilidad.colision.modelo;

import gestion.reparabilidad.colision.modelo.Enums.Repuestos;
import gestion.reparabilidad.colision.modelo.Enums.Ubicacion;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "observacion")
public class Observacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_observacion")
    private Long idObservacion;

    @ManyToOne
    @JoinColumn(name = "id_orden_reparacion", referencedColumnName = "id_orden_reparacion", nullable = false)
    private OrdenReparacion ordenReparacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_etapa", referencedColumnName = "id_etapa")
    private Etapas etapa;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "ubicacion", length = 20, nullable = false)
    private Ubicacion ubicacion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "estado_repuestos", length = 20)
    private Repuestos estadoRepuestos;

    @Column(name = "nota", length = 500, nullable = false)
    private String nota;

    @Column(name = "visible_cliente", nullable = false)
    private boolean visibleCliente = true;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "observacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagenObservacion> imagenes = new ArrayList<>();

    public Observacion(Long idObservacion, OrdenReparacion ordenReparacion, Usuario usuario, Etapas etapa, Ubicacion ubicacion, Repuestos estadoRepuestos, String nota, boolean visibleCliente, LocalDateTime createAt, List<ImagenObservacion> imagenes) {
        this.idObservacion = idObservacion;
        this.ordenReparacion = ordenReparacion;
        this.usuario = usuario;
        this.etapa = etapa;
        this.ubicacion = ubicacion;
        this.estadoRepuestos = estadoRepuestos;
        this.nota = nota;
        this.visibleCliente = visibleCliente;
        this.createAt = createAt;
        this.imagenes = imagenes;
    }

    public Observacion() {
    }

    public Long getIdObservacion() {
        return idObservacion;
    }

    public void setIdObservacion(Long idObservacion) {
        this.idObservacion = idObservacion;
    }

    public OrdenReparacion getOrdenReparacion() {
        return ordenReparacion;
    }

    public void setOrdenReparacion(OrdenReparacion ordenReparacion) {
        this.ordenReparacion = ordenReparacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Etapas getEtapa() {
        return etapa;
    }

    public void setEtapa(Etapas etapa) {
        this.etapa = etapa;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Repuestos getEstadoRepuestos() {
        return estadoRepuestos;
    }

    public void setEstadoRepuestos(Repuestos estadoRepuestos) {
        this.estadoRepuestos = estadoRepuestos;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public boolean isVisibleCliente() {
        return visibleCliente;
    }

    public void setVisibleCliente(boolean visibleCliente) {
        this.visibleCliente = visibleCliente;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public List<ImagenObservacion> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ImagenObservacion> imagenes) {
        this.imagenes = imagenes;
    }
}
