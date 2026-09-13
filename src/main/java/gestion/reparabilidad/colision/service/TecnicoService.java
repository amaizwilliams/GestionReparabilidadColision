package gestion.reparabilidad.colision.service;

import gestion.reparabilidad.colision.modelo.Tecnico;
import gestion.reparabilidad.colision.repository.TecnicoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));
    }

    public Tecnico saveTecnico(Tecnico tecnico) {
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
