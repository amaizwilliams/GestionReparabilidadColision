package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "documento_cliente", length = 10, nullable = false)
    private String documento;

    @Column(name = "nombre_cliente", length = 100, nullable = false)
    private String nombreCompleto;

    @Column(name = "celular_cliente", length = 10, nullable = false)
    private String celular;

    @Column(name = "correo_cliente", length = 100)
    private String correo;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "cliente")
    private List<Vehiculo> vehiculos = new ArrayList<>();

    //contructores

    public Cliente(Long idCliente, String documento, String nombreCompleto, String celular, String correo, LocalDateTime createAt, LocalDateTime updateAt, List<Vehiculo> vehiculos) {
        this.idCliente = idCliente;
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.celular = celular;
        this.correo = correo;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.vehiculos = vehiculos;
    }

    public Cliente() {
    }

    //getters and stters

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
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

    public List<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }
}
