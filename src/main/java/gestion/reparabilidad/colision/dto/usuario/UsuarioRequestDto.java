package gestion.reparabilidad.colision.dto.usuario;

import gestion.reparabilidad.colision.modelo.Enums.RolUsuario;
import jakarta.validation.constraints.*;

public record UsuarioRequestDto(
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @NotNull(message = "El rol es obligatorio")
        RolUsuario rol,

        @Positive(message = "El ID del técnico debe ser un número positivo")
        Long idTecnico,

        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
                message = "La contraseña debe contener al menos una mayúscula, una minúscula y un número"
        )
        String password
) {
}
