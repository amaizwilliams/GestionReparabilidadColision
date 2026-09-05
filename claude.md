# CLAUDE.md

Este archivo le da contexto a Claude Code sobre el proyecto. Está vivo: se actualiza a medida que avanzan las decisiones, se crean clases nuevas o cambian reglas de negocio. Cuando algo relevante cambie en una sesión de trabajo, actualiza la sección correspondiente antes de cerrar.

---

## Descripción del proyecto

Sistema de gestión para un **taller de colisión de vehículos**. Administra el flujo interno de reparación (ingreso, valoración, kanban de etapas, entrega) y se integra con **CESVI Colombia**, plataforma externa de valoración de daños usada por aseguradoras.

Conceptualmente inspirado en **Orbika** (usado por Seguros Bolívar / Subocol), pero con funcionalidades adicionales ajustadas a la operación específica de este taller.

**Nombre del proyecto:** aún no definido. Preferencia por acrónimos técnicos en español (ej: SIGTAC, REPCOL, GESCOL), siguiendo convenciones de software colombiano (SIIGO, SISBÉN). Palabras clave del dominio: "reparabilidad", "colisión".

---

## Stack técnico

- **Frontend:** React (HTML/CSS/JavaScript)
- **Backend:** Java + Spring Boot
- **Base de datos:** MySQL
- **Diagramación:** draw.io (diagramas UML de clases y ER)
- **Integración externa:** CESVI Colombia (subida de valoraciones/fotos)
- **Notificaciones:** WhatsApp (vía API tipo Twilio/Meta), asíncronas (`@Async`)

Paquete base del backend: `gestion.reparabilidad.colision`
Paquete de entidades: `gestion.reparabilidad.colision.modelo`
Paquete de enums: `gestion.reparabilidad.colision.modelo.Enums`

---

## Convenciones de código establecidas

Estas reglas se han corregido repetidamente durante el desarrollo — **aplícalas siempre por defecto** al generar o revisar entidades JPA, sin que se tengan que repetir:

### Llaves primarias
- Tipo **`Long`** (wrapper), nunca `long` primitivo. Un `long` primitivo no puede representar "aún no tiene id" (usa `0` por defecto, que se puede confundir con un id real antes de persistir).
- Siempre con:
  ```java
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_x")
  private Long idX;
  ```
- `GenerationType.IDENTITY` porque MySQL lo soporta nativo (`AUTO_INCREMENT`) y es la estrategia usada consistentemente en todo el modelo.
- No usar `length` en el `@Column` de una PK tipo `Long` — ese atributo solo aplica a `String` (define `VARCHAR(n)`), en `Long` Hibernate lo ignora.

### Nombres de columnas
- **snake_case** en la base de datos (`id_cliente`, `nombre_completo`, `create_at`), **camelCase** en Java (`idCliente`, `nombreCompleto`, `createAt`). No mezclar (evitar columnas tipo `createAt` sin snake_case).
- Los nombres de atributos Java deben coincidir exactamente con los de la guía de referencia (`GuiaDiagranaUML.pdf`) — ej: es `nombreCompleto`, no `nombreCliente`; es `celular`, no `telefono`.
- Nunca declarar un atributo con mayúscula inicial (ej: `private Cliente Cliente;` es incorrecto — debe ser `private Cliente cliente;`).

### Enums
- Los enums viven en su propio subpaquete `modelo.Enums`, así que **cada entidad que use uno necesita su `import`** (ej. `import gestion.reparabilidad.colision.modelo.Enums.RolUsuario;`). Al mover un enum, revisar que el import quede en *todas* las entidades que lo usan — un refactor de IDE puede saltarse alguna y el error solo aparece al compilar.
- Los enums son clases Java puras, **sin ninguna anotación** de JPA/Spring. La anotación va en la entidad que los usa, no en el enum:
  ```java
  @Enumerated(EnumType.STRING)
  @Column(name = "rol", length = 20, nullable = false)
  private RolUsuario rol;
  ```
- Siempre `EnumType.STRING`, nunca el default (`ORDINAL`). Guardar el ordinal es frágil: si se reordena o inserta un valor en medio del enum, los datos ya guardados quedan apuntando al valor incorrecto.
- **Hibernate 7 ignora el `length` de una columna enum**: por defecto genera un tipo `ENUM('ADMIN','ASESOR',...)` nativo de MySQL, no un `VARCHAR(20)`. Eso obliga a un `ALTER TABLE` cada vez que se agrega un valor al enum. Para conservar `VARCHAR(20)` hay que forzarlo:
  ```java
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(name = "rol", length = 20, nullable = false)
  private RolUsuario rol;
  ```
  (imports: `org.hibernate.annotations.JdbcTypeCode` y `org.hibernate.type.SqlTypes`)
- Enums confirmados: `RolUsuario` (ADMIN, ASESOR, GERENTE, TECNICO), `Estado` (ACTIVA, CERRADA, CANCELADA), `EstadoEnvio` (PENDIENTE, ENVIADO, FALLIDO), `Accion` (REPARACION, SUSTITUCION), `Gravedad` (LEVE, MEDIO, FUERTE), `Ubicacion` (EN_TALLER, FUERA_DE_TALLER), `Repuestos` (COMPLETOS, PENDIENTES).

### Lombok
- Las entidades usan `@Getter`, `@Setter` y `@NoArgsConstructor` de Lombok (ya está en el `pom.xml` con `annotationProcessorPaths` configurado).
- **No usar `@Data`, `@ToString` ni `@EqualsAndHashCode` en entidades JPA**: generan `toString()`/`equals()` que recorren las relaciones y disparan carga lazy o recursión infinita entre los dos lados de una relación bidireccional.

### Timestamps de auditoría
- Las entidades con `createAt`/`updateAt` los llenan solas con callbacks JPA, no desde el Service:
  ```java
  @PrePersist
  protected void alCrear() {
      this.createAt = LocalDateTime.now();
      this.updateAt = this.createAt;
  }

  @PreUpdate
  protected void alActualizar() {
      this.updateAt = LocalDateTime.now();
  }
  ```
- Las entidades que solo tienen `createAt` (`Observacion`, `ImagenObservacion`, `ValoracionImagen`, `Notificaciones`) llevan únicamente el `@PrePersist`.

### Longitudes de String
- **Todas las columnas `String` declaran su `length` explícito**, aunque 255 sea el default de JPA y el DDL salga idéntico sin él.
- La razón es que las entidades son la única fuente de verdad del esquema: con `ddl-auto`, lo que está anotado en la clase **es** la tabla. Si el ancho de una columna no se ve en la entidad, hay que ir a buscarlo a un archivo generado, y eso rompe la revisión línea por línea.
- La guía asigna `VARCHAR(255)` a todos los campos de texto; las columnas de enum llevan `length = 20`.

### Relaciones JPA
- Nunca mapear una FK como tipo primitivo (`long idCliente`). Siempre usar el objeto de la entidad relacionada + `@JoinColumn`:
  ```java
  @ManyToOne
  @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente", nullable = false)
  private Cliente cliente;
  ```
- El lado que tiene la FK físicamente en la base de datos es el dueño de la relación (lleva `@JoinColumn`). El otro lado usa `mappedBy` apuntando al nombre exacto del atributo (no de la columna, no de la clase).
- `nullable` en `@JoinColumn` debe reflejar la cardinalidad `[0..1]` vs obligatoria de la guía.
- Relaciones inversas (`@OneToMany` desde el lado "padre") son opcionales — solo agregarlas si de verdad se necesita navegar en ese sentido desde el código.
- Cardinalidad `1 → N` (ej: Cliente → Vehiculo, Vehiculo → OrdenReparacion) es `@ManyToOne`/`@OneToMany`, no `@OneToOne`. Confundir esto ha sido un error recurrente — verificar siempre contra la guía antes de escribir la relación.

### Tipos de datos (mapeo Java → MySQL)
| Java | MySQL | Uso |
|---|---|---|
| Long | BIGINT(20) | PKs y FKs |
| Int | INT(11) | Enteros medianos |
| Short | SMALLINT(6) | Año, días |
| Byte | TINYINT(3) | Valores muy pequeños |
| boolean | BOOLEAN(1) | true/false |
| String | VARCHAR(255) | Ajustar longitud según campo específico |
| LocalDate | DATE | Solo fecha |
| LocalDateTime | DATETIME | Fecha + hora |
| BigDecimal | DECIMAL(12,2) | Montos y costos |
| byte[] | BLOB | Archivos binarios |
| String (JSON) | JSON/TEXT | Arrays JSON |

Cuidado con campos como `documento` (Cliente/Tecnico) o `celular`: aunque parezcan numéricos, van como `String` — un documento puede tener ceros a la izquierda o dígito de verificación con guión, y `celular` necesita formato E.164 (`+57...`).

### Otros
- `orden` en `Etapa` **no** lleva `@GeneratedValue` — es un valor de negocio reordenable manualmente (admin puede insertar/reordenar etapas), no una secuencia autogenerada.
- `fechaFin` en etapas nunca se marca manualmente desde la UI — la asigna el sistema (`cambiarEtapa()`) al cerrar la etapa anterior.

---

## Modelo de dominio — decisiones confirmadas

- **`Cliente`** y **`Tecnico`** son clases independientes, **sin** superclase compartida `Persona` (Opción A).
- La asignación de técnico vive a **nivel de etapa** (`OrdenEtapaFecha.idTecnico`), no en `OrdenReparacion` directamente. `OrdenReparacion.idTecnicoResponsable` es el responsable general de la OT (opcional), distinto del técnico que trabajó cada etapa puntual.
- **`ubicacion`** y **`estadoRepuestos`** viven en `Observacion`, no en `OrdenReparacion` (así lo define la guía: son el estado *al momento de dejar la nota*). La idea previa de un `ubicacionActual` en `OrdenReparacion` como fuente de verdad **no** está en la guía y no se implementó; si se quiere ese campo denormalizado hay que decidirlo y agregarlo aparte.
- Rol **RECEPCIONISTA** fue renombrado a **ASESOR**.
- **`TECNICO`** no tiene login al sistema web en el MVP — existe solo como entidad de catálogo para asignación en etapas y futuro cálculo de pagos. (`Usuario.idTecnico` permite vincular un técnico a un usuario *si* ese técnico sí necesita login, ej. rol GERENTE/ADMIN que también es técnico).
- **CESVI** se trackea de forma independiente vía `Valoracion.cargadaCesvi` (boolean) + `cargadaCesviAt`. Se marca manualmente desde la UI, no automáticamente.
- **Control de acceso data-driven**: `Modulo` + `RolModulo` en vez de reglas hardcodeadas en Spring Security. Spring Security consulta `RolModulo` (cacheada) vía JWT en cada request.
- **Modal de cambio de etapa**: selector de técnico obligatorio (FK a `Tecnico`), observación opcional (tabla compartida `Observacion`), notificación WhatsApp opcional (`visibleCliente`), notificación asíncrona que nunca bloquea ni hace rollback de la transacción principal.

### Flujo de etapas (Kanban)
```
Ingreso a cotizar → Desarme → Latonería → Bancada → Electromecánica
→ Pintura → Armado → Control de calidad → Listo para entregar → Entregado
```

---

## Estado actual de las entidades

| Entidad | Estado | Notas |
|---|---|---|
| `Usuario` | ✅ Completa | `email` UNIQUE, `passwordHash` (no `passWordHash`), `@OneToOne` opcional a `Tecnico` |
| `RolUsuario` (enum) | ✅ Completo | ADMIN, ASESOR, GERENTE, TECNICO |
| `Estado` (enum) | ✅ Completo | ACTIVA, CERRADA, CANCELADA |
| `EstadoEnvio` (enum) | ✅ Completo | PENDIENTE, ENVIADO, FALLIDO |
| `Accion` (enum) | ✅ Completo | REPARACION, SUSTITUCION |
| `Gravedad` (enum) | ✅ Completo | LEVE, MEDIO, FUERTE |
| `Ubicacion` (enum) | ✅ Completo | EN_TALLER, FUERA_DE_TALLER |
| `Repuestos` (enum) | ✅ Completo | COMPLETOS, PENDIENTES |
| `Modulo` | ✅ Completa | PK Long+IDENTITY, `codigo` UNIQUE |
| `RolModulo` | ✅ Completa | `@ManyToOne` a `Modulo`, enum `rol` reutilizado |
| `Cliente` | ✅ Completa | `documento` String NOT NULL, `nombreCompleto`, `celular` en E.164 |
| `Tecnico` | ✅ Completa | Tabla `tecnico` (antes decía `tenico`), `documento` UNIQUE, `correo`/`especialidad` nullable |
| `Vehiculo` | ✅ Completa | `@ManyToOne` a `Cliente`; `anio` Short NOT NULL; INDEX en `placa` (no UNIQUE) |
| `Etapas` | ✅ Completa | `orden` Integer sin autoincrement |
| `OrdenReparacion` | ✅ Completa | 5 FKs + enum `Estado` + 4 fechas `LocalDate` + `cambiarEtapa()`, `asignarTecnico()`, `calcularDiasEnTaller()` |
| `HistorialEtapas` | ✅ Completa | Log append-only: solo INSERT, nunca UPDATE |
| `OrdenEtapaFecha` | ✅ Completa | `UNIQUE(id_orden_reparacion, id_etapa)` + flag `completada` |
| `Valoracion` | ✅ Completa | `@OneToOne` con `id_orden_reparacion` UNIQUE — una sola valoración por orden |
| `ValoracionDetalle` | ✅ Completa | CHECK `ck_detalle_gravedad` vía `@Table(check = @CheckConstraint(...))` de JPA 3.2 |
| `ValoracionImagen` | ✅ Completa | `url` pública https (no BLOB): WhatsApp y CESVI la descargan |
| `Observacion` | ✅ Completa | Tabla compartida por el modal del backlog y la ficha de detalle |
| `ImagenObservacion` | ✅ Completa | PK `idImagenesObservacion` (plural, según la guía) |
| `Notificaciones` | ✅ Completa | `imagenesEnviadas` como columna `JSON`; helpers `marcarEnviada()` / `marcarFallida()` |
| `PlantillaMensaje` | ✅ Completa | Sin FK: la consulta el servicio de notificaciones por `evento` |

Leyenda: ✅ revisada/correcta · 🔶 en progreso · ⏳ pendiente

---

## Flujo de trabajo con Git

Se usa **git flow** (feature branches). Recordatorio operativo: `git flow feature finish` requiere working tree limpio — si aparece `Fatal: Working tree contains unstaged changes`, resolver con `git add . && git commit` (recomendado si el trabajo está listo) o `git stash` / `git stash pop` (si se quiere posponer el commit).

---

## Paleta de colores (para frontend / PDFs de referencia)

Escala de grises:
- `#F2F2F2` — RGB 242,242,242
- `#BFBFBF` — RGB 191,191,191
- `#595959` — RGB 89,89,89
- `#262626` — RGB 38,38,38
- `#0D0D0D` — RGB 13,13,13

---

## Pendientes / próximos módulos

La capa de modelo (`gestion.reparabilidad.colision.modelo`) está **completa**: 17 entidades + 7 enums, compilando y con el `EntityManagerFactory` construyendo sin errores. Lo que sigue:

1. **Capa de repositorios**: interfaces `JpaRepository` con los finders que pide la guía (`getVehiculoByPlaca`, `getOrdenReparacionByPlaca`, `getValoracionByOrden`, `getPlantillaByEvento`, `getModuloByRol`, `getEtapasCompletadas(desde, hasta)`).
2. **Capa de servicios**: los métodos CRUD que la guía lista dentro de cada clase son de Service/Repository, **no** de la entidad. El único que vive en la entidad es la lógica de agregado de `OrdenReparacion`.
3. `@Transactional` sobre el Service que llama a `OrdenReparacion.cambiarEtapa()` — la guía exige que actualizar `etapaActual` + insertar `HistorialEtapas` + cerrar/abrir `OrdenEtapaFecha` viajen en una sola transacción.
4. Regla pendiente de `Observacion`: si la etapa referenciada tiene `fechaInicio = NULL` al crear la observación, el Service debe marcarla con `NOW()` en la misma transacción.
5. Listener `@Async` de notificaciones WhatsApp + interpolación de `PlantillaMensaje` (`{{cliente}}`, `{{placa}}`, `{{etapa}}`). Un fallo de WhatsApp nunca debe romper la operación principal.
6. Configuración de MySQL en `application.properties` (hoy solo tiene `spring.application.name`) y decidir migraciones (Flyway/Liquibase) vs `ddl-auto`.
7. Módulo de **Valoración**: export PDF de la hoja y ZIP de fotos generados al vuelo para CESVI.
8. **Ficha de Orden de Trabajo** — pendiente de analizar.
9. Documentación formal (Reglas de Negocio numeradas, Escenarios, Historias de Usuario, criterios de aceptación).
10. Fase futura: rol de técnico de campo con acceso web/móvil; cálculo de pagos por etapa sobre `OrdenEtapaFecha.idTecnico`.
11. Nombre final del proyecto — aún abierto.

### Decisiones que quedaron abiertas en el modelo

- `contenidoEnviado` (`Notificaciones`) y `contenidoTemplate` (`PlantillaMensaje`) quedaron en `VARCHAR(255)` porque así los define la guía, pero un mensaje de WhatsApp puede pasarse de 255 y MySQL lo cortaría o lanzaría error de truncado. Evaluar subirlos a `TEXT` (`@Column(columnDefinition = "TEXT")`).
- Solo se mapearon las relaciones inversas (`@OneToMany`) que hacen falta hoy: `Cliente.vehiculos`, `Vehiculo.ordenesReparacion`, `Modulo.rolesModulo`, `Tecnico.usuario`, `OrdenReparacion.{historialEtapas, ordenEtapaFechas, valoracion}`, `Valoracion.{detalles, imagenes}` y `Observacion.imagenes`. Las demás (ej. `Cliente → Notificaciones`) se consultan por repositorio.

---

## Cómo trabajar en este proyecto (para Claude Code)

- La guía de referencia **está en la raíz del repo**: `GuiaDiagranaUML.pdf` (18 páginas, con atributos, tipos, restricciones, métodos y reglas de negocio de cada clase). Es la fuente de verdad: ante cualquier diferencia entre este archivo y la guía, **gana la guía**. Para leerla: `pip install pypdf` y extraer el texto con `pypdf.PdfReader`.
- Antes de generar o corregir una entidad, contrastar contra la guía — no asumir nombres de campos ni cardinalidades sin verificar.
- Los métodos CRUD que la guía lista dentro de cada clase (`createCliente`, `getAllVehiculo`, …) describen la **API del Service/Repository**, no métodos de la entidad JPA. No meterlos dentro de la entidad.
- **Validar el modelo sin base de datos**: `./mvnw test` con este `src/test/resources/application.properties` construye el `EntityManagerFactory` completo (detecta `mappedBy` mal escritos, FKs rotas, etc.) y exporta el DDL a `target/schema-modelo.sql`, todo sin conectarse a MySQL:
  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/validacion_modelo
  spring.datasource.username=validador
  spring.datasource.password=validador
  spring.datasource.hikari.initialization-fail-timeout=-1
  spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
  spring.jpa.hibernate.ddl-auto=none
  spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create
  spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=target/schema-modelo.sql
  spring.jpa.properties.hibernate.hbm2ddl.delimiter=;
  ```
- Seguir estrictamente las convenciones de código de la sección correspondiente; son errores que ya se han corregido varias veces y no deben repetirse.
- Adrian revisa activamente el código generado y corrige detalles finos (tipos de retorno, campos faltantes, tipos de enum incorrectos) — priorizar precisión y que el código sea fácil de revisar línea por línea.
- Actualizar la tabla de "Estado actual de las entidades" y la sección de "Pendientes" cada vez que se complete o corrija una clase.