package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/* La URL debe ser publica (https): la API de WhatsApp la descarga para adjuntarla. */
@Entity
@Table(name = "imagen_observacion")
@Getter
@Setter
@NoArgsConstructor
public class ImagenObservacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagenes_observacion")
    private Long idImagenesObservacion;

    @ManyToOne
    @JoinColumn(name = "id_observacion", referencedColumnName = "id_observacion", nullable = false)
    private Observacion observacion;

    @Column(name = "url", length = 255, nullable = false)
    private String url;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @PrePersist
    protected void alCrear() {
        this.createAt = LocalDateTime.now();
    }
}
