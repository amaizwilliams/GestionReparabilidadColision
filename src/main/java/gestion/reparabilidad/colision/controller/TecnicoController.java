package gestion.reparabilidad.colision.controller;

import gestion.reparabilidad.colision.modelo.Tecnico;
import gestion.reparabilidad.colision.service.TecnicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tecnico")
@CrossOrigin(origins = "*")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @GetMapping
    public ResponseEntity<List<Tecnico>> listar() {
        return ResponseEntity.ok(tecnicoService.getAllTecnico());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tecnico> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(tecnicoService.getTecnicoById(id));
    }

    @PostMapping
    public ResponseEntity<Tecnico> crearTecnico(@RequestBody Tecnico tecnico) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tecnicoService.saveTecnico(tecnico));
    }

    @PutMapping("/{id}")
    public Tecnico actualizar(@PathVariable Long id, @RequestBody Tecnico tecnico) {
        return ResponseEntity.ok(tecnicoService.updateTecnico(id, tecnico)).getBody();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tecnicoService.deleteTecnicoById(id);
        return ResponseEntity.noContent().build();
    }
}
