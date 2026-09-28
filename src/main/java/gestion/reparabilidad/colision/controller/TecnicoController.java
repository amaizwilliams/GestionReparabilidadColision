package gestion.reparabilidad.colision.controller;

import gestion.reparabilidad.colision.dto.TecnicoRequestDto;
import gestion.reparabilidad.colision.modelo.Tecnico;
import gestion.reparabilidad.colision.service.TecnicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import gestion.reparabilidad.colision.dto.TecnicoUpdateDto;

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

    @GetMapping("/desactivados")
    public ResponseEntity<List<Tecnico>> listarTecnicosDesactivados() {
        return ResponseEntity.ok(tecnicoService.getAllTecnicoDesactivado());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Tecnico> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(tecnicoService.getTecnicoById(id));
    }

    @PostMapping
    public ResponseEntity<Tecnico> crearTecnico(@Valid @RequestBody TecnicoRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tecnicoService.saveTecnico(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tecnico> actualizar(@PathVariable Long id, @Valid @RequestBody TecnicoUpdateDto datos) {
        return ResponseEntity.ok(tecnicoService.updateTecnico(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tecnicoService.deleteTecnicoById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Tecnico> reactivar(@PathVariable Long id){
        return ResponseEntity.ok(tecnicoService.reactivarTecnico(id));
    }

}
