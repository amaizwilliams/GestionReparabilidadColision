package gestion.reparabilidad.colision.modelo;

import gestion.reparabilidad.colision.modelo.Enums.Accion;
import gestion.reparabilidad.colision.modelo.Enums.Gravedad;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

/* gravedad solo aplica cuando accion=REPARACION; con SUSTITUCION debe quedar NULL. */
@Entity
@Table(
        name = "valoracion_detalle",
        check = @CheckConstraint(
                name = "ck_detalle_gravedad",
                constraint = "(accion = 'REPARACION' AND gravedad IS NOT NULL) " +
                        "OR (accion = 'SUSTITUCION' AND gravedad IS NULL)"
        )
)
@Getter
@Setter
@NoArgsConstructor
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
}
