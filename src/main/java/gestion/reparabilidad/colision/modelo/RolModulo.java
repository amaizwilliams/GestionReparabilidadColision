package gestion.reparabilidad.colision.modelo;

import gestion.reparabilidad.colision.modelo.Enums.RolUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "rol_modulo")
@Getter
@Setter
@NoArgsConstructor
public class RolModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol_modulo")
    private Long idRolModulo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "rol", length = 20, nullable = false)
    private RolUsuario rol;

    @ManyToOne
    @JoinColumn(name = "id_modulo", referencedColumnName = "id_modulo", nullable = false)
    private Modulo modulo;
}
