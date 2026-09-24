package gestion.reparabilidad.colision.service;

import gestion.reparabilidad.colision.exception.RecursoNoEncontradoException;
import gestion.reparabilidad.colision.modelo.Tecnico;
import gestion.reparabilidad.colision.repository.TecnicoRepository;
import org.springframework.http.ResponseEntity;
import gestion.reparabilidad.colision.dto.TecnicoRequestDto;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import java.util.List;

@Service
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;

    public TecnicoService(TecnicoRepository tecnicoRepository) {
        this.tecnicoRepository = tecnicoRepository;
    }

    public List<Tecnico> getAllTecnico() {
        return tecnicoRepository.findAll();
    }

    public Tecnico getTecnicoById(Long id) {
        return tecnicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el técnico con id " + id));
    }

    public Tecnico saveTecnico(TecnicoRequestDto request) {
        Tecnico tecnico = new Tecnico();
        tecnico.setDocumento(request.getDocumento().trim());
        tecnico.setNombreCompleto(request.getNombreCompleto().trim());
        tecnico.setCelular(request.getCelular());
        tecnico.setCorreo(request.getCorreo());
        tecnico.setEspecialidad(request.getEspecialidad());
        LocalDateTime ahora = LocalDateTime.now();
        tecnico.setActivo(true);
        tecnico.setCreateAt(ahora);
        tecnico.setUpdateAt(ahora);
        return tecnicoRepository.save(tecnico);
    }

    public Tecnico updateTecnico(Long id, Tecnico datos) {
        Tecnico tecnico = getTecnicoById(id);
        tecnico.setNombreCompleto(datos.getNombreCompleto());
        tecnico.setCelular(datos.getCelular());
        tecnico.setCorreo(datos.getCorreo());
        tecnico.setEspecialidad(datos.getEspecialidad());
        tecnico.setActivo(datos.getActivo());

        return tecnicoRepository.save(tecnico);
    }

    public void deleteTecnicoById(Long id) {
        Tecnico tecnico = getTecnicoById(id);
        tecnicoRepository.delete(tecnico);
    }


}
