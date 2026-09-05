package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modulo")
@Getter
@Setter
@NoArgsConstructor
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modulo")
    private Long idModulo;

    @Column(name = "codigo", length = 255, nullable = false, unique = true)
    private String codigo;

    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "modulo")
    private List<RolModulo> rolesModulo = new ArrayList<>();
}
