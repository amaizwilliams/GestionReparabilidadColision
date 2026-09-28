package gestion.reparabilidad.colision.modelo;

import gestion.reparabilidad.colision.modelo.Enums.EstadoEnvio;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
@Entity
@Table(name = "notificaciones")
public class Notificaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @ManyToOne
    @JoinColumn(name = "id_orden_reparacion", referencedColumnName = "id_orden_reparacion", nullable = false)
    private OrdenReparacion ordenReparacion;

    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_persona", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_observacion", referencedColumnName = "id_observacion")
    private Observacion observacion;

    @Column(name = "evento_disparador", nullable = false)
    private String eventoDisparador;

    @Column(name = "canal", nullable = false)
    private String canal = "WHATSAPP";

    @Column(name = "contenido_enviado", nullable = false)
    private String contenidoEnviado;

    @Column(name = "imagenes_enviadas", columnDefinition = "JSON")
    private String imagenesEnviadas;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "estado_envio", length = 20, nullable = false)
    private EstadoEnvio estadoEnvio = EstadoEnvio.PENDIENTE;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "error_detalle")
    private String errorDetalle;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    public Notificaciones(Long idNotificacion, OrdenReparacion ordenReparacion, Cliente cliente, Observacion observacion, String eventoDisparador, String canal, String contenidoEnviado, String imagenesEnviadas, EstadoEnvio estadoEnvio, LocalDateTime fechaEnvio, String errorDetalle, LocalDateTime createAt) {
        this.idNotificacion = idNotificacion;
        this.ordenReparacion = ordenReparacion;
        this.cliente = cliente;
        this.observacion = observacion;
        this.eventoDisparador = eventoDisparador;
        this.canal = canal;
        this.contenidoEnviado = contenidoEnviado;
        this.imagenesEnviadas = imagenesEnviadas;
        this.estadoEnvio = estadoEnvio;
        this.fechaEnvio = fechaEnvio;
        this.errorDetalle = errorDetalle;
        this.createAt = createAt;
    }

    public Notificaciones() {
    }

    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(Long idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public OrdenReparacion getOrdenReparacion() {
        return ordenReparacion;
    }

    public void setOrdenReparacion(OrdenReparacion ordenReparacion) {
        this.ordenReparacion = ordenReparacion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Observacion getObservacion() {
        return observacion;
    }

    public void setObservacion(Observacion observacion) {
        this.observacion = observacion;
    }

    public String getEventoDisparador() {
        return eventoDisparador;
    }

    public void setEventoDisparador(String eventoDisparador) {
        this.eventoDisparador = eventoDisparador;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getContenidoEnviado() {
        return contenidoEnviado;
    }

    public void setContenidoEnviado(String contenidoEnviado) {
        this.contenidoEnviado = contenidoEnviado;
    }

    public String getImagenesEnviadas() {
        return imagenesEnviadas;
    }

    public void setImagenesEnviadas(String imagenesEnviadas) {
        this.imagenesEnviadas = imagenesEnviadas;
    }

    public EstadoEnvio getEstadoEnvio() {
        return estadoEnvio;
    }

    public void setEstadoEnvio(EstadoEnvio estadoEnvio) {
        this.estadoEnvio = estadoEnvio;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getErrorDetalle() {
        return errorDetalle;
    }

    public void setErrorDetalle(String errorDetalle) {
        this.errorDetalle = errorDetalle;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
