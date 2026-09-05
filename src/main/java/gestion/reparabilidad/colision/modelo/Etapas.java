package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "etapas")
@Getter
@Setter
@NoArgsConstructor
public class Etapas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etapa")
    private Long idEtapa;

    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    /* Valor de negocio reordenable por el admin: no lleva @GeneratedValue. */
    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "descripcion", length = 255)
    private String descripcion;
}
