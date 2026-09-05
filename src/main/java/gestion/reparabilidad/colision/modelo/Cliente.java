package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "documento", length = 255, nullable = false)
    private String documento;

    @Column(name = "nombre_completo", length = 255, nullable = false)
    private String nombreCompleto;

    @Column(name = "celular", length = 255, nullable = false)
    private String celular;

    @Column(name = "correo", length = 255)
    private String correo;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "cliente")
    private List<Vehiculo> vehiculos = new ArrayList<>();

    @PrePersist
    protected void alCrear() {
        this.createAt = LocalDateTime.now();
        this.updateAt = this.createAt;
    }

    @PreUpdate
    protected void alActualizar() {
        this.updateAt = LocalDateTime.now();
    }
}
