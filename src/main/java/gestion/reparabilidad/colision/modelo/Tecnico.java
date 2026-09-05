package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tenico")
public class Tecnico {
    @Id
    @Column(name = "id_tecnico", length = 10, nullable = false)
    private long idTenico;
    @Column(name = "documento_tecnico",length = 10, nullable = false)
    private long documentoTenico;
    @Column(name = "nombre_tecnico", length = 100, nullable = false)
    private String nombreCompleto;
    @Column(name = "celular_tecnico", length = 10, nullable = false)
    private String celular;
    @Column(name = "correo_tecnico", length = 50, nullable = false)
    private String correo;
    @Column(name = "especialidad_tecnico", length = 50, nullable = false)
    private String especialidad;
    @Column(name = "activo", nullable = false)
    private boolean activo;
    @Column(name = "createAt", nullable = false)
    private LocalDateTime createAt;
    @Column(name = "updateAt", nullable = false)
    private LocalDateTime updateAt;
}
