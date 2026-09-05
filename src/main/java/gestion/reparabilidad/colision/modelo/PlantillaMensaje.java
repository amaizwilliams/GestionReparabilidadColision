package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "plantilla_mensaje")
public class PlantillaMensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plantilla_mensaje")
    private Long idPlantillaMensaje;

    @Column(name = "evento", length = 255, nullable = false)
    private String evento;

    @Column(name = "canal", length = 255, nullable = false)
    private String canal = "WHATSAPP";

    @Column(name = "contenido_template", length = 255, nullable = false)
    private String contenidoTemplate;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    public PlantillaMensaje(Long idPlantillaMensaje, String evento, String canal, String contenidoTemplate, boolean activo) {
        this.idPlantillaMensaje = idPlantillaMensaje;
        this.evento = evento;
        this.canal = canal;
        this.contenidoTemplate = contenidoTemplate;
        this.activo = activo;
    }

    public PlantillaMensaje() {
    }

    public Long getIdPlantillaMensaje() {
        return idPlantillaMensaje;
    }

    public void setIdPlantillaMensaje(Long idPlantillaMensaje) {
        this.idPlantillaMensaje = idPlantillaMensaje;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getContenidoTemplate() {
        return contenidoTemplate;
    }

    public void setContenidoTemplate(String contenidoTemplate) {
        this.contenidoTemplate = contenidoTemplate;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
