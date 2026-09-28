package gestion.reparabilidad.colision.dto;
import gestion.reparabilidad.colision.modelo.Enums.Especialidad;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class ClienteRequestDto {

    @NotBlank(message = "El documento es obligatorio")
    @Pattern(regexp = "^\\d{6,10}$", message = "El documento debe tener solo números, entre 6 y 10 dígitos")
    private String documento;

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
    
}
