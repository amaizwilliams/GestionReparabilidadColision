package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long idVehiculo;

    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_persona", nullable = false)
    private Cliente cliente;

    @Column(name = "placa", length = 6, nullable = false, unique = true)
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

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "vehiculo")
    private List<OrdenReparacion> ordenesReparacion = new ArrayList<>();

    public void setOrdenesReparacion(List<OrdenReparacion> ordenesReparacion) {
        this.ordenesReparacion = ordenesReparacion;
    }
}
