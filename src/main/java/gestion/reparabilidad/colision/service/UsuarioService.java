package gestion.reparabilidad.colision.service;

import gestion.reparabilidad.colision.dto.usuario.UsuarioRequestDto;
import gestion.reparabilidad.colision.dto.usuario.UsuarioResponseDto;
import gestion.reparabilidad.colision.modelo.Usuario;
import gestion.reparabilidad.colision.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }



    public List<UsuarioResponseDto> getAllUsuario() {
        return usuarioRepository.findAll().stream()
                .filter(Usuario::getActivo) // Opcional: filtrar solo los activos
                .map(this::convertirAEntityADto)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDto getUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return convertirAEntityADto(usuario);
    }


    public UsuarioResponseDto saveUsuario(UsuarioRequestDto requestDto) {
        // Validación: El email debe ser único
        if (usuarioRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new RuntimeException("El email ya se encuentra registrado");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(requestDto.nombre());
        nuevoUsuario.setEmail(requestDto.email());
        nuevoUsuario.setRol(requestDto.rol());
        nuevoUsuario.setActivo(true);

        String passwordEncriptada = passwordEncoder.encode(requestDto.password());
        nuevoUsuario.setPasswordHash(passwordEncriptada);

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        return convertirAEntityADto(usuarioGuardado);
    }

    public UsuarioResponseDto updateUsuario(Long id, UsuarioRequestDto requestDto) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Validar que el nuevo email no pertenezca a otro usuario
        usuarioRepository.findByEmail(requestDto.email())
                .ifPresent(u -> {
                    if (!u.getIdUsuario().equals(id)) {
                        throw new RuntimeException("El email ya está en uso por otro usuario");
                    }
                });
        usuarioExistente.setEmail(requestDto.email());
        usuarioExistente.setNombre(requestDto.nombre());
        usuarioExistente.setRol(requestDto.rol());

        String passwordEncriptada = passwordEncoder.encode(requestDto.password());
        usuarioExistente.setPasswordHash(passwordEncriptada);

        Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
        return convertirAEntityADto(usuarioActualizado);
    }

    public void deleteUsuarioLogically(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        usuario.setActivo(false); // No borramos de la BD, solo inactivamos
        usuarioRepository.save(usuario);
    }


    // METODO AUXILIAR: Mapeo de Entidad a DTO Completo
    private UsuarioResponseDto convertirAEntityADto(Usuario usuario) {
        Long idTecnico = (usuario.getTecnico() != null) ? usuario.getTecnico().getIdPersona() : null;

        return new UsuarioResponseDto(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                idTecnico,
                usuario.getActivo()
        );
    }
}
