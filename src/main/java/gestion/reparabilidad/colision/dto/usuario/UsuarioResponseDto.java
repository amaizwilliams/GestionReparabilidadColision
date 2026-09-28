package gestion.reparabilidad.colision.dto.usuario;

import gestion.reparabilidad.colision.modelo.Enums.RolUsuario;

public record UsuarioResponseDto(
        Long idUsuario,
        String nombre,
        String email,
        RolUsuario rol,
        Long idTecnico,
        Boolean activo
) {
}
