package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/* Una sola valoracion por orden: se edita, nunca se duplica. */
@Entity
@Table(name = "valoracion")
@Getter
@Setter
@NoArgsConstructor
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion")
    private Long idValoracion;

    @OneToOne
    @JoinColumn(name = "id_orden_reparacion", referencedColumnName = "id_orden_reparacion",
            nullable = false, unique = true)
    private OrdenReparacion ordenReparacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "descripcion_general", length = 255)
    private String descripcionGeneral;

    @Column(name = "costo_estimado", precision = 12, scale = 2)
    private BigDecimal costoEstimado;

    /* Se marca manualmente desde la UI cuando el usuario ya subio la hoja a CESVI Colombia. */
    @Column(name = "cargada_cesvi", nullable = false)
    private boolean cargadaCesvi = false;

    @Column(name = "cargada_cesvi_at")
    private LocalDateTime cargadaCesviAt;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "valoracion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValoracionDetalle> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "valoracion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValoracionImagen> imagenes = new ArrayList<>();

    @PrePersist
    protected void alCrear() {
        this.createAt = LocalDateTime.now();
        this.updateAt = this.createAt;
    }

    @PreUpdate
    protected void alActualizar() {
        this.updateAt = LocalDateTime.now();
    }

    public void marcarCargadaCesvi() {
        this.cargadaCesvi = true;
        this.cargadaCesviAt = LocalDateTime.now();
    }
}
