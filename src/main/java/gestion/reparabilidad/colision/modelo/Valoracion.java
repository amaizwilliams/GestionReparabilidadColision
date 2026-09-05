package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "valoracion")
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion")
    private Long idValoracion;

    @OneToOne
    @JoinColumn(name = "id_orden_reparacion", referencedColumnName = "id_orden_reparacion", nullable = false, unique = true)
    private OrdenReparacion ordenReparacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "descripcion_general", length = 500)
    private String descripcionGeneral;

    @Column(name = "costo_estimado", precision = 12, scale = 2)
    private BigDecimal costoEstimado;

    @Column(name = "cargada_cesvi", nullable = false)
    private boolean cargadaCesvi = false;

    @Column(name = "cargada_cesvi_at")
    private LocalDateTime cargadaCesviAt;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "valoracion")
    private List<ValoracionDetalle> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "valoracion")
    private List<ValoracionImagen> imagenes = new ArrayList<>();

    public Valoracion() {
    }

    public Valoracion(Long idValoracion, gestion.reparabilidad.colision.modelo.OrdenReparacion ordenReparacion, gestion.reparabilidad.colision.modelo.Usuario usuario, String descripcionGeneral, BigDecimal costoEstimado, boolean cargadaCesvi, LocalDateTime cargadaCesviAt, LocalDateTime createAt, LocalDateTime updateAt, List<ValoracionDetalle> detalles, List<ValoracionImagen> imagenes) {
        this.idValoracion = idValoracion;
        this.ordenReparacion = ordenReparacion;
        this.usuario = usuario;
        this.descripcionGeneral = descripcionGeneral;
        this.costoEstimado = costoEstimado;
        this.cargadaCesvi = cargadaCesvi;
        this.cargadaCesviAt = cargadaCesviAt;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.detalles = detalles;
        this.imagenes = imagenes;
    }


    public Long getIdValoracion() {
        return idValoracion;
    }

    public void setIdValoracion(Long idValoracion) {
        this.idValoracion = idValoracion;
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

    public String getDescripcionGeneral() {
        return descripcionGeneral;
    }

    public void setDescripcionGeneral(String descripcionGeneral) {
        this.descripcionGeneral = descripcionGeneral;
    }

    public BigDecimal getCostoEstimado() {
        return costoEstimado;
    }

    public void setCostoEstimado(BigDecimal costoEstimado) {
        this.costoEstimado = costoEstimado;
    }

    public boolean isCargadaCesvi() {
        return cargadaCesvi;
    }

    public void setCargadaCesvi(boolean cargadaCesvi) {
        this.cargadaCesvi = cargadaCesvi;
    }

    public LocalDateTime getCargadaCesviAt() {
        return cargadaCesviAt;
    }

    public void setCargadaCesviAt(LocalDateTime cargadaCesviAt) {
        this.cargadaCesviAt = cargadaCesviAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public List<ValoracionDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<ValoracionDetalle> detalles) {
        this.detalles = detalles;
    }

    public List<ValoracionImagen> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ValoracionImagen> imagenes) {
        this.imagenes = imagenes;
    }
}
