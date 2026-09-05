package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehiculo", indexes = @Index(name = "idx_vehiculo_placa", columnList = "placa"))
@Getter
@Setter
@NoArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long idVehiculo;

    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "placa", length = 255, nullable = false)
    private String placa;

    @Column(name = "marca", length = 255, nullable = false)
    private String marca;

    @Column(name = "modelo", length = 255, nullable = false)
    private String modelo;

    @Column(name = "anio", nullable = false)
    private Short anio;

    @Column(name = "color", length = 255)
    private String color;

    @Column(name = "vin", length = 255)
    private String vin;

    @OneToMany(mappedBy = "vehiculo")
    private List<OrdenReparacion> ordenesReparacion = new ArrayList<>();
}
