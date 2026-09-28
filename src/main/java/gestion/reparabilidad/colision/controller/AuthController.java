package gestion.reparabilidad.colision.controller;

import gestion.reparabilidad.colision.dto.LoginRequest;
import gestion.reparabilidad.colision.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Llama al servicio que verifica el booleano
        boolean esValido = authService.verificarAutenticacion(loginRequest.email(), loginRequest.password());

        if (esValido) {
            // Si es true, el login es exitoso. Aquí podrías generar un Token JWT.
            return ResponseEntity.ok("Autenticación exitosa. Bienvenido.");
        } else {
            // Si es false, rechazamos la petición inmediatamente
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas.");
        }
    }
}
