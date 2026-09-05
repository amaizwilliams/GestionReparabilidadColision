package gestion.reparabilidad.colision.modelo;

import gestion.reparabilidad.colision.modelo.Enums.Estado;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden_reparacion")
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

    @OneToMany(mappedBy = "ordenReparacion")
    private List<HistorialEtapas> historialEtapas = new ArrayList<>();

    @OneToMany(mappedBy = "ordenReparacion")
    private List<OrdenEtapaFecha> ordenEtapaFechas = new ArrayList<>();

    @OneToOne(mappedBy = "ordenReparacion")
    private Valoracion valoracion;

    public OrdenReparacion(Long idOrdenReparacion, Vehiculo vehiculo, Cliente cliente, Usuario usuarioCreador, Tecnico tecnicoResponsable, Etapas etapaActual, Estado estado, LocalDate fechaIngresoCotizar, LocalDate fechaIngresoReparacion, LocalDate fechaDeEntregaEstimada, LocalDate fechaDeEntregaReal, Byte diasEstimadoEntrega, LocalDateTime createAt, LocalDateTime updateAt, List<HistorialEtapas> historialEtapas, List<OrdenEtapaFecha> ordenEtapaFechas, Valoracion valoracion) {
        this.idOrdenReparacion = idOrdenReparacion;
        this.vehiculo = vehiculo;
        this.cliente = cliente;
        this.usuarioCreador = usuarioCreador;
        this.tecnicoResponsable = tecnicoResponsable;
        this.etapaActual = etapaActual;
        this.estado = estado;
        this.fechaIngresoCotizar = fechaIngresoCotizar;
        this.fechaIngresoReparacion = fechaIngresoReparacion;
        this.fechaDeEntregaEstimada = fechaDeEntregaEstimada;
        this.fechaDeEntregaReal = fechaDeEntregaReal;
        this.diasEstimadoEntrega = diasEstimadoEntrega;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.historialEtapas = historialEtapas;
        this.ordenEtapaFechas = ordenEtapaFechas;
        this.valoracion = valoracion;
    }

    public OrdenReparacion() {
    }

    public Long getIdOrdenReparacion() {
        return idOrdenReparacion;
    }

    public void setIdOrdenReparacion(Long idOrdenReparacion) {
        this.idOrdenReparacion = idOrdenReparacion;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(Usuario usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public Tecnico getTecnicoResponsable() {
        return tecnicoResponsable;
    }

    public void setTecnicoResponsable(Tecnico tecnicoResponsable) {
        this.tecnicoResponsable = tecnicoResponsable;
    }

    public Etapas getEtapaActual() {
        return etapaActual;
    }

    public void setEtapaActual(Etapas etapaActual) {
        this.etapaActual = etapaActual;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public LocalDate getFechaIngresoCotizar() {
        return fechaIngresoCotizar;
    }

    public void setFechaIngresoCotizar(LocalDate fechaIngresoCotizar) {
        this.fechaIngresoCotizar = fechaIngresoCotizar;
    }

    public LocalDate getFechaIngresoReparacion() {
        return fechaIngresoReparacion;
    }

    public void setFechaIngresoReparacion(LocalDate fechaIngresoReparacion) {
        this.fechaIngresoReparacion = fechaIngresoReparacion;
    }

    public LocalDate getFechaDeEntregaEstimada() {
        return fechaDeEntregaEstimada;
    }

    public void setFechaDeEntregaEstimada(LocalDate fechaDeEntregaEstimada) {
        this.fechaDeEntregaEstimada = fechaDeEntregaEstimada;
    }

    public LocalDate getFechaDeEntregaReal() {
        return fechaDeEntregaReal;
    }

    public void setFechaDeEntregaReal(LocalDate fechaDeEntregaReal) {
        this.fechaDeEntregaReal = fechaDeEntregaReal;
    }

    public Byte getDiasEstimadoEntrega() {
        return diasEstimadoEntrega;
    }

    public void setDiasEstimadoEntrega(Byte diasEstimadoEntrega) {
        this.diasEstimadoEntrega = diasEstimadoEntrega;
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

    public List<HistorialEtapas> getHistorialEtapas() {
        return historialEtapas;
    }

    public void setHistorialEtapas(List<HistorialEtapas> historialEtapas) {
        this.historialEtapas = historialEtapas;
    }

    public List<OrdenEtapaFecha> getOrdenEtapaFechas() {
        return ordenEtapaFechas;
    }

    public void setOrdenEtapaFechas(List<OrdenEtapaFecha> ordenEtapaFechas) {
        this.ordenEtapaFechas = ordenEtapaFechas;
    }

    public Valoracion getValoracion() {
        return valoracion;
    }

    public void setValoracion(Valoracion valoracion) {
        this.valoracion = valoracion;
    }
}
