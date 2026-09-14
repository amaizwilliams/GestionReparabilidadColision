package gestion.reparabilidad.colision.modelo;

import gestion.reparabilidad.colision.modelo.Enums.Accion;
import gestion.reparabilidad.colision.modelo.Enums.Gravedad;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "valoracion_detalle")
public class ValoracionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion_detalle")
    private Long idValoracionDetalle;

    @ManyToOne
    @JoinColumn(name = "id_valoracion", referencedColumnName = "id_valoracion", nullable = false)
    private Valoracion valoracion;

    @Column(name = "pieza", length = 255, nullable = false)
    private String pieza;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "accion", length = 20, nullable = false)
    private Accion accion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "gravedad", length = 20)
    private Gravedad gravedad;

    @Column(name = "observacion", length = 255)
    private String observacion;

    @Column(name = "costo", precision = 12, scale = 2)
    private BigDecimal costo;


    public ValoracionDetalle() {
    }

    public ValoracionDetalle(Long idValoracionDetalle, gestion.reparabilidad.colision.modelo.Valoracion valoracion, String pieza, Accion accion, Gravedad gravedad, String observacion, BigDecimal costo) {
        this.idValoracionDetalle = idValoracionDetalle;
        this.valoracion = valoracion;
        this.pieza = pieza;
        this.accion = accion;
        this.gravedad = gravedad;
        this.observacion = observacion;
        this.costo = costo;
    }


    public Long getIdValoracionDetalle() {
        return idValoracionDetalle;
    }

    public void setIdValoracionDetalle(Long idValoracionDetalle) {
        this.idValoracionDetalle = idValoracionDetalle;
    }

    public Valoracion getValoracion() {
        return valoracion;
    }

    public void setValoracion(Valoracion valoracion) {
        this.valoracion = valoracion;
    }

    public String getPieza() {
        return pieza;
    }

    public void setPieza(String pieza) {
        this.pieza = pieza;
    }

    public Accion getAccion() {
        return accion;
    }

    public void setAccion(Accion accion) {
        this.accion = accion;
    }

    public Gravedad getGravedad() {
        return gravedad;
    }

    public void setGravedad(Gravedad gravedad) {
        this.gravedad = gravedad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }
}
