package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "etapas")
public class Etapas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etapa")
    private Long idEtapa;

    @Column(name = "nombre_etapa", length = 100, nullable = false)
    private String nombre;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    //constructores

    public Etapas(Long idEtapa, String nombre, Integer orden, String descripcion) {
        this.idEtapa = idEtapa;
        this.nombre = nombre;
        this.orden = orden;
        this.descripcion = descripcion;
    }

    public Etapas() {
    }

    //getters and setters

    public Long getIdEtapa() {
        return idEtapa;
    }

    public void setIdEtapa(Long idEtapa) {
        this.idEtapa = idEtapa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
