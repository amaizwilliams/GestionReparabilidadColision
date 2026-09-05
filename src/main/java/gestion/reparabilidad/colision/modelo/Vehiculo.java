package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long idVehiculo;

    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "placa", length = 6, nullable = false)
    private String placa;

    @Column(name = "marca", length = 20, nullable = false)
    private String marca;

    @Column(name = "modelo", length = 10, nullable = false)
    private String modelo;

    @Column(name = "anio", nullable = false)
    private Short anio;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "vin", length = 100)
    private String vin;

    @OneToMany(mappedBy = "vehiculo")
    private List<OrdenReparacion> ordenesReparacion = new ArrayList<>();


    public Vehiculo() {
    }

    public Vehiculo(Long idVehiculo, Cliente cliente, String placa, String marca, String modelo, Short anio, String color, String vin, List<gestion.reparabilidad.colision.modelo.OrdenReparacion> ordenesReparacion) {
        this.idVehiculo = idVehiculo;
        this.cliente = cliente;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
        this.vin = vin;
        this.ordenesReparacion = ordenesReparacion;
    }

    public Long getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Long idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Short getAnio() {
        return anio;
    }

    public void setAnio(Short anio) {
        this.anio = anio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public List<OrdenReparacion> getOrdenesReparacion() {
        return ordenesReparacion;
    }

    public void setOrdenesReparacion(List<OrdenReparacion> ordenesReparacion) {
        this.ordenesReparacion = ordenesReparacion;
    }
}
