package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import org.springframework.data.jpa.provider.QueryComment;


@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @Column(name = "id_usuario", length = 10, nullable = false)
    private long idUsuario;
    @Column(name = "name_usuario", length = 50, nullable = false)
    private String nombre;
    @Column(name = "email_usuario", length = 50, nullable = false)
    private String email;
    @OneToOne
    @JoinColumn(name = "id_tecnico", referencedColumnName = "id_tecnico", nullable = true)
    private Tecnico tecnico;
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", length = 20, nullable = false)
    private RolUsuario rol;
    @Column(name = "pass_word_hash", length = 20,nullable = false)
    private String passWordHash;
    @Column(name = "activo", nullable = false)
    private boolean activo;




}
