package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modulo")
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modulo")
    private Long idModulo;

    @Column(name = "codigo", length = 10, nullable = false, unique = true)
    private String codigo;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "modulo")
    private List<RolModulo> rolesModulo = new ArrayList<>();

    public Modulo(Long idModulo, String codigo, String nombre, List<RolModulo> rolesModulo) {
        this.idModulo = idModulo;
        this.codigo = codigo;
        this.nombre = nombre;
        this.rolesModulo = rolesModulo;
    }

    public Modulo() {
    }

    public Long getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(Long idModulo) {
        this.idModulo = idModulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<RolModulo> getRolesModulo() {
        return rolesModulo;
    }

    public void setRolesModulo(List<RolModulo> rolesModulo) {
        this.rolesModulo = rolesModulo;
    }
}
