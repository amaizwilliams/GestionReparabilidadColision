package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;

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

    //contructores

    public Persona() {
    }

    public Persona(String documento, String nombreCompleto, String celular, String correo) {
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.celular = celular;
        this.correo = correo;
    }

    //getters and setters

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
