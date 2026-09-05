package gestion.reparabilidad.colision.modelo;

import gestion.reparabilidad.colision.modelo.Enums.Repuestos;
import gestion.reparabilidad.colision.modelo.Enums.Ubicacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * Tabla compartida por las dos rutas de ingreso: el modal del backlog al mover la
 * tarjeta y la ficha de detalle de la orden.
 */
@Entity
@Table(name = "observacion")
@Getter
@Setter
@NoArgsConstructor
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

    @Column(name = "nota", length = 255, nullable = false)
    private String nota;

    /* true = el servicio de notificaciones envia la nota y sus fotos por WhatsApp. */
    @Column(name = "visible_cliente", nullable = false)
    private boolean visibleCliente = true;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "observacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagenObservacion> imagenes = new ArrayList<>();

    @PrePersist
    protected void alCrear() {
        this.createAt = LocalDateTime.now();
    }
}
