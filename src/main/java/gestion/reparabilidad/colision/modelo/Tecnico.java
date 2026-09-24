package gestion.reparabilidad.colision.modelo;

import gestion.reparabilidad.colision.modelo.Enums.Especialidad;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "tecnico")
@PrimaryKeyJoinColumn(name = "id_persona")
public class Tecnico extends Persona {

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "especialidad", length = 20, nullable = false)
    private Especialidad especialidad;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @OneToOne(mappedBy = "tecnico")
    private Usuario usuario;

    //contructores

    public Tecnico() {
    }

    public Tecnico(String documento, String nombreCompleto, String celular, String correo,Especialidad especialidad, Boolean activo, LocalDateTime createAt, LocalDateTime updateAt) {
        super(documento, nombreCompleto, celular, correo);
        this.especialidad = especialidad;
        this.activo = activo;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    //getters and setters

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
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
