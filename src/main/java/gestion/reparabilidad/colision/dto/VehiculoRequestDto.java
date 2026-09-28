package gestion.reparabilidad.colision.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class VehiculoRequestDto {

    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;

    @NotBlank(message = "El placa es obligatorio")
    @Pattern(regexp = "^[A-Za-z]{3}[0-9]{2}[0-9A-Za-z]$", message = "Placa inválida (ej: ABC123 o ABC12D)")
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 20)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 10)
    private String modelo;

    @NotNull(message = "El año es obligatorio")
    private Short anio;

    @Size(max = 50)
    private String color;

    @Size(max = 100)
    private String vin;
}
