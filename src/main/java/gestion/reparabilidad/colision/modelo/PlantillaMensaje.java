package gestion.reparabilidad.colision.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Plantillas con variables interpoladas: {{cliente}}, {{placa}}, {{etapa}}. */
@Entity
@Table(name = "plantilla_mensaje")
@Getter
@Setter
@NoArgsConstructor
public class PlantillaMensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plantilla_mensaje")
    private Long idPlantillaMensaje;

    @Column(name = "evento", length = 255, nullable = false)
    private String evento;

    @Column(name = "canal", length = 255, nullable = false)
    private String canal = "WHATSAPP";

    @Column(name = "contenido_template", length = 255, nullable = false)
    private String contenidoTemplate;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;
}
