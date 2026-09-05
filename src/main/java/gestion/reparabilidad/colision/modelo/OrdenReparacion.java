package gestion.reparabilidad.colision.modelo;

import gestion.reparabilidad.colision.modelo.Enums.Estado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden_reparacion")
@Getter
@Setter
@NoArgsConstructor
public class OrdenReparacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden_reparacion")
    private Long idOrdenReparacion;

    @ManyToOne
    @JoinColumn(name = "id_vehiculo", referencedColumnName = "id_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_usuario_creador", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuarioCreador;

    @ManyToOne
    @JoinColumn(name = "id_tecnico_responsable", referencedColumnName = "id_tecnico")
    private Tecnico tecnicoResponsable;

    @ManyToOne
    @JoinColumn(name = "id_etapa_actual", referencedColumnName = "id_etapa", nullable = false)
    private Etapas etapaActual;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "estado", length = 20, nullable = false)
    private Estado estado = Estado.ACTIVA;

    @Column(name = "fecha_ingreso_cotizar", nullable = false)
    private LocalDate fechaIngresoCotizar;

    @Column(name = "fecha_ingreso_reparacion")
    private LocalDate fechaIngresoReparacion;

    @Column(name = "fecha_de_entrega_estimada")
    private LocalDate fechaDeEntregaEstimada;

    @Column(name = "fecha_de_entrega_real")
    private LocalDate fechaDeEntregaReal;

    @Column(name = "dias_estimado_entrega")
    private Byte diasEstimadoEntrega;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "ordenReparacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialEtapas> historialEtapas = new ArrayList<>();

    @OneToMany(mappedBy = "ordenReparacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenEtapaFecha> ordenEtapaFechas = new ArrayList<>();

    @OneToOne(mappedBy = "ordenReparacion")
    private Valoracion valoracion;

    @PrePersist
    protected void alCrear() {
        this.createAt = LocalDateTime.now();
        this.updateAt = this.createAt;
    }

    @PreUpdate
    protected void alActualizar() {
        this.updateAt = LocalDateTime.now();
    }

    /*
     * Mueve la OT a otra etapa dentro del agregado: actualiza etapaActual, cierra la
     * fecha de la etapa anterior, abre la de la nueva e inserta el registro de historial.
     * El @Transactional que garantiza que todo esto viaje junto va en el Service que
     * invoca este metodo, no aca.
     */
    public void cambiarEtapa(Etapas nuevaEtapa, Usuario usuario, String comentario) {
        LocalDateTime ahora = LocalDateTime.now();

        OrdenEtapaFecha enCurso = etapaEnCurso();
        if (enCurso != null) {
            enCurso.setFechaFin(ahora);
            enCurso.setCompletada(true);
        }

        this.etapaActual = nuevaEtapa;

        /* UNIQUE(idOrdenReparacion, idEtapa): si la etapa ya se recorrio, se reabre esa fila. */
        OrdenEtapaFecha destino = buscarEtapaFecha(nuevaEtapa);
        if (destino == null) {
            destino = new OrdenEtapaFecha();
            destino.setOrdenReparacion(this);
            destino.setEtapa(nuevaEtapa);
            this.ordenEtapaFechas.add(destino);
        }
        destino.setFechaInicio(ahora);
        destino.setFechaFin(null);
        destino.setCompletada(false);

        HistorialEtapas registro = new HistorialEtapas();
        registro.setOrdenReparacion(this);
        registro.setEtapa(nuevaEtapa);
        registro.setUsuario(usuario);
        registro.setFechaCambio(ahora);
        registro.setComentario(comentario);
        this.historialEtapas.add(registro);
    }

    /* Tecnico responsable general de la OT, distinto del tecnico de cada etapa puntual. */
    public void asignarTecnico(Tecnico tecnico) {
        this.tecnicoResponsable = tecnico;
    }

    public int calcularDiasEnTaller() {
        LocalDate fin = (this.fechaDeEntregaReal != null) ? this.fechaDeEntregaReal : LocalDate.now();
        return (int) ChronoUnit.DAYS.between(this.fechaIngresoCotizar, fin);
    }

    private OrdenEtapaFecha etapaEnCurso() {
        for (OrdenEtapaFecha fila : this.ordenEtapaFechas) {
            if (fila.getFechaFin() == null) {
                return fila;
            }
        }
        return null;
    }

    private OrdenEtapaFecha buscarEtapaFecha(Etapas etapa) {
        for (OrdenEtapaFecha fila : this.ordenEtapaFechas) {
            if (fila.getEtapa() != null && fila.getEtapa().equals(etapa)) {
                return fila;
            }
        }
        return null;
    }
}
