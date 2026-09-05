package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/* La URL debe ser publica (https): WhatsApp y CESVI la descargan, no aceptan archivos directos. */
@Entity
@Table(name = "valoracion_imagen")
@Getter
@Setter
@NoArgsConstructor
public class ValoracionImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion_imagen")
    private Long idValoracionImagen;

    @ManyToOne
    @JoinColumn(name = "id_valoracion", referencedColumnName = "id_valoracion", nullable = false)
    private Valoracion valoracion;

    @Column(name = "url", length = 255, nullable = false)
    private String url;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @PrePersist
    protected void alCrear() {
        this.createAt = LocalDateTime.now();
    }
}
