package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

    @Entity
    @Table(name = "vehiculo")
    public class Vehiculo {
        @Id
        @Column(length = 10,  nullable = false)
        private long idVehiculo;
        @Column(length = 6,  nullable = false)
        private String placa;
        @Column(length = 100, nullable = false )
        private String marca;
        @Column(length = 4, nullable = false)
        private String modelo;
        /*private String anio;*/
        @Column(length = 50, nullable = false)
        private String color;
        @Column(length = 100, nullable = false)
        private String vin;

        /*private long idCliente;*/

    }

