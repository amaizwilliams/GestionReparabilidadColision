package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "persona", indexes = @Index(name = "idx_persona_documento", columnList = "documento"))
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona", nullable = false)
    private Long idPersona;

    @Column(name = "documento", length = 10, nullable = false)
    private String documento;

    @Column(name = "nombre_completo", length = 200, nullable = false)
    private String nombreCompleto;

    @Column(name = "celular", length = 16, nullable = false)
    private String celular;

    @Column(name = "correo", length = 100)
    private String correo;
}
