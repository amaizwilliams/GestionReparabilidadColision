package gestion.reparabilidad.colision.service;

import gestion.reparabilidad.colision.modelo.Usuario;
import gestion.reparabilidad.colision.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean verificarAutenticacion(String email, String passwordPlana) {
        // Buscar al usuario en MariaDB por su email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            return passwordEncoder.matches(passwordPlana, usuario.getPasswordHash());
        }

        return false; // El usuario no existe
    }
}
