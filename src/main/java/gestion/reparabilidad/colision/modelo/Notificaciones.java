package gestion.reparabilidad.colision.modelo;

import gestion.reparabilidad.colision.modelo.Enums.EstadoEnvio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/* Log de todos los mensajes de WhatsApp: habilita debug, reintentos y auditoria. */
@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
public class Notificaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @ManyToOne
    @JoinColumn(name = "id_orden_reparacion", referencedColumnName = "id_orden_reparacion", nullable = false)
    private OrdenReparacion ordenReparacion;

    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente", nullable = false)
    private Cliente cliente;

    /* Origen del mensaje cuando lo dispara una observacion; null si lo dispara un cambio de etapa. */
    @ManyToOne
    @JoinColumn(name = "id_observacion", referencedColumnName = "id_observacion")
    private Observacion observacion;

    @Column(name = "evento_disparador", nullable = false)
    private String eventoDisparador;

    @Column(name = "canal", nullable = false)
    private String canal = "WHATSAPP";

    @Column(name = "contenido_enviado", nullable = false)
    private String contenidoEnviado;

    /* Array JSON con las URLs de las fotos adjuntas al mensaje. */
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

    @PrePersist
    protected void alCrear() {
        this.createAt = LocalDateTime.now();
    }

    public void marcarEnviada() {
        this.estadoEnvio = EstadoEnvio.ENVIADO;
        this.fechaEnvio = LocalDateTime.now();
        this.errorDetalle = null;
    }

    public void marcarFallida(String errorDetalle) {
        this.estadoEnvio = EstadoEnvio.FALLIDO;
        this.errorDetalle = errorDetalle;
    }
}
