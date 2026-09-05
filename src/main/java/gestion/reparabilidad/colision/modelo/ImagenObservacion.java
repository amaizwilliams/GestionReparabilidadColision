package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "imagen_observacion")
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

    //constructores

    public ImagenObservacion(Long idImagenesObservacion, Observacion observacion, String url, LocalDateTime createAt) {
        this.idImagenesObservacion = idImagenesObservacion;
        this.observacion = observacion;
        this.url = url;
        this.createAt = createAt;
    }

    public ImagenObservacion() {
    }

    //getters and setters

    public Long getIdImagenesObservacion() {
        return idImagenesObservacion;
    }

    public void setIdImagenesObservacion(Long idImagenesObservacion) {
        this.idImagenesObservacion = idImagenesObservacion;
    }

    public Observacion getObservacion() {
        return observacion;
    }

    public void setObservacion(Observacion observacion) {
        this.observacion = observacion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
