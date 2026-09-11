package gestion.reparabilidad.colision.modelo;

import gestion.reparabilidad.colision.modelo.Enums.RolUsuario;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "rol_modulo")
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

    public RolModulo(Long idRolModulo, RolUsuario rol, Modulo modulo) {
        this.idRolModulo = idRolModulo;
        this.rol = rol;
        this.modulo = modulo;
    }

    public RolModulo() {
    }

    public Long getIdRolModulo() {
        return idRolModulo;
    }

    public void setIdRolModulo(Long idRolModulo) {
        this.idRolModulo = idRolModulo;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }
}
