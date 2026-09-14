package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "valoracion_imagen")
public class ValoracionImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion_imagen")
    private Long idValoracionImagen;

    @ManyToOne
    @JoinColumn(name = "id_valoracion", referencedColumnName = "id_valoracion", nullable = false)
    private Valoracion valoracion;

    @Column(name = "url", length = 2083, nullable = false)
    private String url;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    public ValoracionImagen() {
    }

    public ValoracionImagen(Long idValoracionImagen, gestion.reparabilidad.colision.modelo.Valoracion valoracion, String url, String descripcion, LocalDateTime createAt) {
        this.idValoracionImagen = idValoracionImagen;
        this.valoracion = valoracion;
        this.url = url;
        this.descripcion = descripcion;
        this.createAt = createAt;
    }

    public Long getIdValoracionImagen() {
        return idValoracionImagen;
    }

    public void setIdValoracionImagen(Long idValoracionImagen) {
        this.idValoracionImagen = idValoracionImagen;
    }

    public Valoracion getValoracion() {
        return valoracion;
    }

    public void setValoracion(Valoracion valoracion) {
        this.valoracion = valoracion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
