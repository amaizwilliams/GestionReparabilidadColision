package gestion.reparabilidad.colision.service;
import gestion.reparabilidad.colision.dto.ClienteRequestDto;
import gestion.reparabilidad.colision.modelo.Cliente;
import gestion.reparabilidad.colision.modelo.Tecnico;
import gestion.reparabilidad.colision.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

import gestion.reparabilidad.colision.exception.RecursoNoEncontradoException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente saveCliente(ClienteRequestDto request){
        Cliente cliente = new Cliente();
        cliente.setDocumento(request.getDocumento().trim());
        cliente.setNombreCompleto(request.getNombreCompleto().trim());
        cliente.setCelular(request.getCelular());
        cliente.setCorreo(request.getCorreo());
        LocalDateTime ahora = LocalDateTime.now();
        cliente.setActivo(true);
        cliente.setCreateAt(ahora);
        cliente.setUpdateAt(ahora);
        return clienteRepository.save(cliente);
    }

    public Cliente getClienteById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + id));
    }


    public List<Cliente> getAllCliente() {
        return clienteRepository.findByActivoTrue();
    }

    public List<Cliente> getAllClienteDesactivado() {
        return clienteRepository.findByActivoFalse();
    }

    public void deleteClienteById(Long id) {
        Cliente cliente = getClienteById(id);
        cliente.setActivo(false);
        cliente.setUpdateAt(LocalDateTime.now());
        clienteRepository.save(cliente);
    }

    public Cliente reactivarCliente(Long id){
        Cliente cliente = getClienteById(id);
        cliente.setActivo(true);
        cliente.setUpdateAt(LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

}
