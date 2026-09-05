package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tecnico")
public class Tecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tecnico")
    private Long idTecnico;

    @Column(name = "documento", length = 10, nullable = false, unique = true)
    private String documento;

    @Column(name = "nombre_completo", length = 200, nullable = false)
    private String nombreCompleto;

    @Column(name = "celular", length = 10, nullable = false)
    private String celular;

    @Column(name = "correo", length = 50)
    private String correo;

    @Column(name = "especialidad", length = 100)
    private String especialidad;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @OneToOne(mappedBy = "tecnico")
    private Usuario usuario;

    public Tecnico(Long idTecnico, String documento, String nombreCompleto, String celular, String correo, String especialidad, boolean activo, LocalDateTime createAt, LocalDateTime updateAt, Usuario usuario) {
        this.idTecnico = idTecnico;
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.celular = celular;
        this.correo = correo;
        this.especialidad = especialidad;
        this.activo = activo;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.usuario = usuario;
    }

    public Tecnico() {
    }

    public Long getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(Long idTecnico) {
        this.idTecnico = idTecnico;
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

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
