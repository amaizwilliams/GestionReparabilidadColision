package gestion.reparabilidad.colision.controller;

import gestion.reparabilidad.colision.dto.usuario.UsuarioRequestDto;
import gestion.reparabilidad.colision.dto.usuario.UsuarioResponseDto;
import gestion.reparabilidad.colision.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuario() {
        return ResponseEntity.ok(usuarioService.getAllUsuario());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDto> crearUsuario(@Valid @RequestBody UsuarioRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.saveUsuario(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDto requestDto) {
        return ResponseEntity.ok(usuarioService.updateUsuario(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuarioLogically(id);
        return ResponseEntity.noContent().build();
    }


}
