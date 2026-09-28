package gestion.reparabilidad.colision.service;

import gestion.reparabilidad.colision.dto.VehiculoRequestDto;
import gestion.reparabilidad.colision.exception.RecursoNoEncontradoException;
import gestion.reparabilidad.colision.modelo.Cliente;
import gestion.reparabilidad.colision.modelo.Vehiculo;
import gestion.reparabilidad.colision.repository.ClienteRepository;
import gestion.reparabilidad.colision.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository, ClienteRepository clienteRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    public Vehiculo saveVehiculo(VehiculoRequestDto request){
        // 1. buscar el cliente dueño; 404 si no existe
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el cliente con id " + request.getIdCliente()));
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setCliente(cliente);
        vehiculo.setPlaca(request.getPlaca().trim().toUpperCase());
        vehiculo.setMarca(request.getMarca().trim());
        vehiculo.setModelo(request.getModelo().trim());
        vehiculo.setAnio(request.getAnio());
        vehiculo.setColor(request.getColor());
        vehiculo.setVin(request.getVin());
        LocalDateTime ahora = LocalDateTime.now();
        vehiculo.setActivo(true);
        vehiculo.setCreateAt(ahora);
        vehiculo.setUpdateAt(ahora);
        return vehiculoRepository.save(vehiculo);
    }

    public Vehiculo getVehiculoById(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el vehículo con id " + id));
    }

    public List<Vehiculo> getAllVehiculo() {
        return vehiculoRepository.findByActivoTrue();
    }

    public List<Vehiculo> getAllVehiculoDesactivado() {
        return vehiculoRepository.findByActivoFalse();
    }

    public void deleteVehiculoById(Long id) {
        Vehiculo vehiculo = getVehiculoById(id);
        vehiculo.setActivo(false);
        vehiculo.setUpdateAt(LocalDateTime.now());
        vehiculoRepository.save(vehiculo);
    }

    public Vehiculo reactivarVehiculo(Long id) {
        Vehiculo vehiculo = getVehiculoById(id);
        vehiculo.setActivo(true);
        vehiculo.setUpdateAt(LocalDateTime.now());
        return vehiculoRepository.save(vehiculo);
    }


}
