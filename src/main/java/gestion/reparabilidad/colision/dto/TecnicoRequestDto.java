package gestion.reparabilidad.colision.dto;

import gestion.reparabilidad.colision.modelo.Enums.Especialidad;
import jakarta.validation.constraints.*;

public class TecnicoRequestDto {

    /*
    ESTA VRGA LA DEJE YO MISMO, NO ESTOY HACIENDO VIBE CODING, ESTOY APRENDIENDO....

    Cómo leer las anotaciones:
    - @NotBlank: rechaza null, "" y "   ". Solo aplica a String.
    - @Pattern: la expresión regular tiene que cumplirse completa. \\d{6,10} = entre 6 y 10 dígitos. \\p{L} = cualquier letra, incluidas tildes y ñ (Pérez, Muñoz), algo que [a-zA-Z] no cubre. Ese patrón es el que rechaza el "1234" que tienes guardado.
    - En Java la barra invertida se escribe doble dentro de un String (\\d), porque \d solo no compila.
    */

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

    @NotNull(message = "La especialidad es obligatoria")
    private Especialidad especialidad;

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }
}

