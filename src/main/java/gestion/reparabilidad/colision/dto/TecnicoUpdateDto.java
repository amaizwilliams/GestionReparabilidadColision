package gestion.reparabilidad.colision.dto;

import gestion.reparabilidad.colision.modelo.Enums.Especialidad;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class TecnicoUpdateDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombreCompleto;

    @NotBlank(message = "El celular es obligatorio")
    @Pattern(regexp = "^\\+?[0-9 -]{10,20}$")
    private String celular;

    @Email
    @Size(max = 100)
    private String correo;

    @NotNull(message = "La especialidad es obligatoria")
    private Especialidad especialidad;

}
