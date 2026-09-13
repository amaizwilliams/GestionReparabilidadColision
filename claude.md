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
- **El sufijo de tabla en las columnas de `Cliente` (`documento_cliente`, `nombre_cliente`, …) ya no existe.** Al subir esos campos a `Persona` quedaron como `documento`, `nombre_completo`, `celular`, `correo` en la tabla `persona`, que es el mismo nombrado que ya usaba `Tecnico`. Todo el modelo tiene ahora simetría atributo/columna. Cualquier query nativa vieja que apunte a `cliente.nombre_cliente` está rota — esas columnas viven en `persona`.
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
- Enums confirmados: `RolUsuario` (ADMIN, ASESOR, GERENTE, TECNICO), `Estado` (ACTIVA, CERRADA, CANCELADA), `EstadoEnvio` (PENDIENTE, ENVIADO, FALLIDO), `Accion` (REPARACION, SUSTITUCION), `Gravedad` (LEVE, MEDIO, FUERTE), `Ubicacion` (EN_TALLER, FUERA_DE_TALLER), `Repuestos` (COMPLETOS, PENDIENTES), `Especialidad` (ARMADOR, LATONERO, MECANICO, ALISTADOR, PINTOR, CONTROL_CALIDAD), `UbicacionActual` (EN_TALLER, FUERA_DE_TALLER).
- **Nota:** `UbicacionActual` y `Ubicacion` tienen los mismos valores pero son enums distintos — `Ubicacion` se usa en `Observacion` (histórico al dejar la nota), `UbicacionActual` se usa en `OrdenReparacion` (fuente de verdad actual). Pendiente evaluar si se unifican en uno solo.

### Tipos primitivos: no usarlos nunca en entidades
- **Todo atributo de entidad usa el wrapper, nunca el primitivo**: `Boolean` y no `boolean`, `Integer` y no `int`, `Long` y no `long`. Aplica también a los parámetros de constructores y a la firma de getters/setters.
- El motivo es el mismo que ya justificaba `Long` en las PKs: un primitivo no puede representar "sin valor". Un `boolean` arranca en `false`, que es indistinguible de un `false` real guardado a propósito, y revienta con `NullPointerException` al leer un `NULL` de la base.
- **Convención de nombre del getter:** con `Boolean` el getter es `getActivo()`, no `isActivo()`. El prefijo `is` es la convención JavaBeans solo para el primitivo `boolean`; dejarlo con el wrapper confunde a herramientas que se apoyan en esa convención.
- Corregidos el 2026-09-06: `Observacion.visibleCliente`, `PlantillaMensaje.activo`, `Usuario.activo`, `Valoracion.cargadaCesvi` y `Tecnico.activo`. Hoy no queda ningún primitivo en el paquete `modelo`.

### Getters/setters y constructores (ya no se usa Lombok)
- **Las entidades ya NO usan Lombok.** Se retiraron `@Getter`, `@Setter`, `@NoArgsConstructor` y sus `import lombok.*` de todo el paquete `modelo`. Cada entidad declara a mano:
  - un **constructor vacío** (obligatorio para JPA/Hibernate),
  - un **constructor con todos los campos**,
  - **getters y setters explícitos** para cada atributo.
- Motivo: el código generado por Lombok no se ve en el archivo, y Adrian revisa las clases línea por línea. Con los accesores escritos, lo que está en la clase es exactamente lo que existe.
- La dependencia de Lombok **sigue declarada en el `pom.xml`** (con `annotationProcessorPaths`), pero ninguna clase la usa hoy. Si se confirma que no se va a volver a usar, se puede quitar del `pom.xml`.
- Al **crear o corregir una entidad, escribir los accesores a mano** — no reintroducir Lombok sin acordarlo antes.
- Sigue vigente: **no generar `toString()`, `equals()` ni `hashCode()`** en entidades JPA. Recorren las relaciones y disparan carga lazy o recursión infinita entre los dos lados de una relación bidireccional.
- Los constructores completos que genera el IDE a veces salen con el tipo totalmente calificado (`gestion.reparabilidad.colision.modelo.Valoracion valoracion`). Compila igual, pero al revisar conviene dejarlo como nombre simple.

### Timestamps de auditoría
- **Los callbacks `@PrePersist` / `@PreUpdate` se eliminaron de todas las entidades.** Hoy ninguna clase del paquete `modelo` los tiene.
- Consecuencia directa: `createAt` y `updateAt` están declarados `nullable = false`, así que **la capa de servicio es responsable de asignarlos** antes de guardar. Si un Service persiste una entidad sin llenarlos, MySQL rechaza el INSERT con violación de NOT NULL.
- Al escribir los Services hay que decidir explícitamente entre tres opciones y aplicarla igual en todas las entidades:
  1. asignar `LocalDateTime.now()` a mano en cada Service (lo que aplica hoy por defecto),
  2. reponer los callbacks JPA en las entidades,
  3. usar auditoría de Spring Data (`@CreatedDate` / `@LastModifiedDate` + `@EntityListeners(AuditingEntityListener.class)` + `@EnableJpaAuditing`).
- Entidades afectadas con `createAt` + `updateAt`: `Cliente`, `Tecnico`, `OrdenReparacion`, `Valoracion`. Solo con `createAt`: `Observacion`, `ImagenObservacion`, `ValoracionImagen`, `Notificaciones`.

### Longitudes de String
- **Todas las columnas `String` declaran su `length` explícito**, aunque 255 sea el default de JPA y el DDL salga idéntico sin él.
- La razón es que las entidades son la única fuente de verdad del esquema: con `ddl-auto`, lo que está anotado en la clase **es** la tabla. Si el ancho de una columna no se ve en la entidad, hay que ir a buscarlo a un archivo generado, y eso rompe la revisión línea por línea.
- **Ya no se usa `VARCHAR(255)` genérico.** La guía asignaba 255 a todo campo de texto; se ajustaron los anchos al dato real del dominio para que MySQL no reserve de más y para que la columna documente por sí sola qué se espera guardar. **Ante esta diferencia gana lo que está en la entidad, no la guía.**
- Las columnas de enum siguen en `length = 20`.

Longitudes vigentes por entidad (estado actual del código):

| Entidad | Columna | length |
|---|---|---|
| `Persona` | `documento` | 10 |
| `Persona` | `nombre_completo` | 200 |
| `Persona` | `celular` | 16 |
| `Persona` | `correo` | 100 |
| `Tecnico` | `especialidad` | 20 (enum `Especialidad`) |
| `Vehiculo` | `placa` | 6 |
| `Vehiculo` | `marca` | 20 |
| `Vehiculo` | `modelo` | 10 |
| `Vehiculo` | `color` | 50 |
| `Vehiculo` | `vin` | 100 |
| `Etapas` | `nombre_etapa` | 100 |
| `Etapas` | `descripcion` | 255 |
| `Modulo` | `codigo` | 10 |
| `Modulo` | `nombre` | 100 |
| `Observacion` | `nota` | 500 |
| `ImagenObservacion` | `url` | 255 |
| `Valoracion` | `descripcion_general` | 500 |
| `ValoracionDetalle` | `pieza` | 255 |
| `ValoracionDetalle` | `observacion` | 255 |
| `ValoracionImagen` | `url` | 2083 |
| `ValoracionImagen` | `descripcion` | 300 |
| `PlantillaMensaje` | `evento`, `canal`, `contenido_template` | 255 |

Notas sobre decisiones puntuales:
- `celular` está en **16** y guarda **E.164 con el `+` incluido** (`+573045649705`), que es el formato que exige la API de WhatsApp. **Resuelto** — antes estaba en 10, que solo cabía el móvil nacional (`3045649705`) y truncaba el prefijo.
  - Son 16 y no 15: E.164 permite hasta **15 dígitos** *más* el signo `+`, o sea 16 caracteres. Con 15 un número internacional de largo máximo se truncaría. (Una nota anterior de este archivo proponía 15; era incorrecta.)
  - Colombia usa 13 de esos 16 (`+57` + 10 dígitos); el margen es para números internacionales.
  - Vive en un solo sitio, `Persona.celular`, así que aplica por igual a `Cliente` y `Tecnico`.
  - **Pendiente en la capa de entrada:** el Service o el DTO debe normalizar a E.164 antes de guardar (anteponer `+57` si llega un móvil nacional de 10 dígitos, quitar espacios y guiones). MySQL trunca sin avisar si entra algo más largo.
- `documento` en 10 cubre cédula colombiana. **Resuelto:** el MVP solo maneja personas naturales con cédula — no se necesita NIT. El `length = 10` se queda.
- `Vehiculo.placa` en 6 es exacto para placa colombiana (`ABC123` / `ABC12D`), sin guiones. El Service debe normalizar (mayúsculas, sin espacios ni guión) antes de guardar, o el INSERT se trunca.
- `Vehiculo.modelo` en 10 — si "modelo" es la línea del vehículo (`Sandero Stepway`) se queda corto; si es el año/versión corta, está bien. Verificar contra el uso real.
- `ValoracionImagen.url` en 2083 es el límite práctico de URL de los navegadores. Nota: MySQL con `utf8mb4` **no permite indexar** una columna así completa (2083 × 4 bytes supera el límite de índice), así que no se le puede poner UNIQUE sin prefijo de índice.
- Columnas `String` que hoy **no** declaran `length` (quedan en el default 255): `Usuario.nombre`, `Usuario.email`, `Usuario.passwordHash`, `Notificaciones.eventoDisparador`, `Notificaciones.canal`, `Notificaciones.contenidoEnviado`, `Notificaciones.errorDetalle`. Rompen la convención de "siempre `length` explícito" — pendiente asignarles ancho. Ojo con `passwordHash`: un hash BCrypt son 60 caracteres, Argon2 puede pasar de 95.

### Relaciones JPA
- Nunca mapear una FK como tipo primitivo (`long idCliente`). Siempre usar el objeto de la entidad relacionada + `@JoinColumn`:
  ```java
  @ManyToOne
  @JoinColumn(name = "id_cliente", referencedColumnName = "id_persona", nullable = false)
  private Cliente cliente;
  ```
- El lado que tiene la FK físicamente en la base de datos es el dueño de la relación (lleva `@JoinColumn`). El otro lado usa `mappedBy` apuntando al nombre exacto del atributo (no de la columna, no de la clase).
- `nullable` en `@JoinColumn` debe reflejar la cardinalidad `[0..1]` vs obligatoria de la guía.
- Relaciones inversas (`@OneToMany` desde el lado "padre") son opcionales — solo agregarlas si de verdad se necesita navegar en ese sentido desde el código.
- Cardinalidad `1 → N` (ej: Cliente → Vehiculo, Vehiculo → OrdenReparacion) es `@ManyToOne`/`@OneToMany`, no `@OneToOne`. Confundir esto ha sido un error recurrente — verificar siempre contra la guía antes de escribir la relación.
- **Cascadas**: se retiraron `cascade = CascadeType.ALL` y `orphanRemoval = true` de `Valoracion.detalles` y `Valoracion.imagenes`; ahora son `@OneToMany(mappedBy = "valoracion")` a secas. El Service debe guardar y borrar los `ValoracionDetalle` / `ValoracionImagen` **explícitamente por su propio repositorio** — quitarlos de la lista en memoria ya no los borra de la base. La única relación que conserva cascada es `Observacion.imagenes` (`cascade = ALL`, `orphanRemoval = true`).

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

### Lógica de negocio: las entidades son POJOs
- **Se eliminaron todos los métodos de negocio de las entidades.** Ya no existen `OrdenReparacion.cambiarEtapa()`, `asignarTecnico()`, `calcularDiasEnTaller()`, `Valoracion.marcarCargadaCesvi()`, ni `Notificaciones.marcarEnviada()` / `marcarFallida()`.
- El modelo quedó como **entidades anémicas**: solo campos, anotaciones JPA, constructores y accesores. Toda la lógica pasa a la capa de servicio, incluida la que antes vivía en el agregado `OrdenReparacion`.
- Esto cambia lo que decía antes este archivo ("el único método que vive en la entidad es la lógica de agregado de `OrdenReparacion`"): **ya no hay ninguno**.

### Validaciones que ya no están en el esquema
Se retiraron restricciones que antes generaba el DDL. Ahora **son responsabilidad del Service** (o hay que reponerlas conscientemente):
- **INDEX `idx_persona_documento`** (no único, agregado el 2026-09-06) sobre `Persona.documento`. Es de rendimiento, no de restricción: el Service consulta por documento en cada alta para detectar duplicados, y sin él esa consulta hace full scan.
- **UNIQUE sobre `Persona.documento`** (quitado el 2026-09-06). La base **ya no impide** documentos repetidos. El Service tiene que garantizar, antes de cada alta:
  - que no exista otro **`Cliente`** con ese `documento`;
  - que no exista otro **`Tecnico`** con ese `documento`;
  - permitiendo explícitamente un `Cliente` y un `Tecnico` que compartan documento (son la misma persona en dos roles).
  La consulta es contra la subtabla, no contra `persona` a secas: `SELECT ... FROM cliente c JOIN persona p ON p.id_persona = c.id_persona WHERE p.documento = ?`. Consultar solo `persona` daría falso positivo cuando la persona ya existe en el otro rol.
  **Límite conocido:** un chequeo en el Service es *check-then-insert* y no es atómico — dos altas simultáneas del mismo documento pueden pasar ambas. Sin índice único en la base no hay forma de cerrarlo del todo; si aparece el problema, tocará serializar esa operación o replantear el modelo.
- **CHECK `ck_detalle_gravedad`** en `ValoracionDetalle` (`@Table(check = @CheckConstraint(...))` de JPA 3.2). La regla sigue vigente en el negocio: `gravedad` solo aplica con `accion = REPARACION`; con `SUSTITUCION` debe quedar `NULL`. Hoy nada lo impide a nivel de base de datos.
- ~~INDEX `idx_vehiculo_placa`~~ — **resuelto el 2026-09-06, y con un cambio de criterio**: `Vehiculo.placa` es ahora **UNIQUE** (`unique = true`), no un índice simple. La restricción trae su propio índice, así que resuelve de una vez el rendimiento y la integridad.
  - Constraint en la base: `uk_vehiculo_placa`. Medido: la búsqueda por placa pasó de `type=ALL` (50.000 filas, ~24,7 ms) a `type=const` sobre el índice (~1,7 ms).
  - **Esto revierte la nota anterior** que decía "no debe ser UNIQUE porque las placas se reasignan". Se aceptó el UNIQUE a cambio de impedir vehículos duplicados, que es el problema real y frecuente en el mostrador.
  - **No limita las órdenes de reparación** — confusión fácil y ya verificada: las órdenes cuelgan de `id_vehiculo`, no de la placa. Un vehículo con placa única puede tener N órdenes (`@OneToMany`). Se comprobó con 3 órdenes sobre el mismo vehículo.
  - **Riesgo asumido — reasignación de placa:** si años después esa placa pasa a otro carro, no se puede crear una segunda fila. Reutilizar la existente cambiando `marca`/`modelo`/`vin` **corrompe el historial**, porque las órdenes viejas siguen apuntando a esa fila y pasarían a describir un vehículo distinto. Si el caso aparece, hay que resolverlo aparte (dar de baja la fila con una marca de estado, o versionar el vehículo), no editándola.

### Otros
- `orden` en `Etapas` es `Integer` y **no** lleva `@GeneratedValue` — es un valor de negocio reordenable manualmente (admin puede insertar/reordenar etapas), no una secuencia autogenerada.
- `fechaFin` en etapas nunca se marca manualmente desde la UI — la asigna el sistema al cerrar la etapa anterior (ahora desde el Service, ya no desde `cambiarEtapa()` en la entidad).
- `OrdenReparacion.diasEstimadoEntrega` es `Byte` (TINYINT): máximo 127 días. Si una reparación puede pasar de eso, hay que subirlo a `Short`.
- Se quitaron los comentarios explicativos que tenían varias entidades (`Valoracion`, `ValoracionDetalle`, `ValoracionImagen`, `PlantillaMensaje`). Las reglas que documentaban quedaron recogidas en este archivo.

---

## Modelo de dominio — decisiones confirmadas

- **`Cliente`** y **`Tecnico`** heredan de una superclase abstracta **`Persona`** con estrategia **`InheritanceType.JOINED`**. Esto revierte la decisión anterior ("Opción A: clases independientes").
  - `persona` es una tabla física real con la PK `id_persona` (`Long` + `IDENTITY`) y los cuatro campos compartidos: `documento`, `nombre_completo`, `celular`, `correo`.
  - `cliente` y `tecnico` son tablas propias cuya PK **es también FK** a `persona.id_persona`, declarada con `@PrimaryKeyJoinColumn(name = "id_persona")`. No se usó `SINGLE_TABLE` (llenaría de columnas nullable) ni `TABLE_PER_CLASS` (rompe el `IDENTITY` y obliga a UNION en las consultas polimórficas).
  - Consecuencia: **`Cliente` y `Tecnico` ya no tienen PK propia.** Desaparecieron `idCliente` / `idTecnico` y sus accesores; el id se lee con `getIdPersona()` heredado.
  - `documento` **NO lleva UNIQUE** (decisión del 2026-09-06). Se quitó a propósito para permitir que una misma persona física sea `Cliente` y `Tecnico` a la vez — el caso del dueño-técnico que además lleva su carro al taller.
    - Un UNIQUE sobre `persona.documento` es global y no distingue subtipo, así que bloqueaba **tres** casos a la vez: dos clientes con el mismo documento, dos técnicos con el mismo documento, y el cliente+técnico que sí se quiere permitir. No hay forma de conservar los dos primeros y soltar el tercero con un solo índice.
    - Tampoco se puede poner un UNIQUE por subtipo: con `JOINED`, `documento` vive solo en `persona`; las tablas `cliente` y `tecnico` no tienen esa columna, así que no hay dónde indexarla por subtipo sin denormalizar. **Por eso la validación en el Service no es un atajo: es la única opción bajo este modelo.**
    - Una persona con doble rol se representa como **dos filas en `persona`** (una con su fila en `cliente`, otra con su fila en `tecnico`), no como una sola fila con ambas subtablas colgando. Eso último MySQL lo acepta, pero deja el subtipo ambiguo en `JOINED` y el ORM solo ve uno de los dos roles — **no hacerlo nunca**.
    - Contrapartida asumida: nombre, celular y correo quedan duplicados en las dos filas y pueden desincronizarse si alguien actualiza solo una.
  - Las longitudes se unificaron hacia arriba al fusionar: `nombre_completo` queda en 200 (era 100 en `Cliente`, 200 en `Tecnico`) y `correo` en 100 (era 100 en `Cliente`, 50 en `Tecnico`). Al unificar nunca se recorta longitud.
  - Las FKs que apuntan a `Cliente`/`Tecnico` conservan su nombre de columna local (`id_cliente`, `id_tecnico`, `id_tecnico_responsable`) pero su **`referencedColumnName` es ahora `id_persona`**, porque es el nombre real de la PK en las tablas `cliente` y `tecnico`. Aplica a `Vehiculo`, `Usuario`, `OrdenReparacion` (×2), `OrdenEtapaFecha` y `Notificaciones`.
- La asignación de técnico vive **exclusivamente a nivel de etapa** (`OrdenEtapaFecha.idTecnico`). **Se eliminó `OrdenReparacion.idTecnicoResponsable`** — ya no existe un "técnico responsable general" de la OT; toda la asignación es por etapa.
- **Se agregó `OrdenReparacion.idUsuarioCreador`** (FK a `Usuario`): se llena automáticamente una sola vez al crear la orden. Es el usuario que registró la OT. Es distinto de "Asesor de la orden" que se muestra en la pestaña Información (este último se deriva del último `HistorialEtapas.idUsuario`, no es un campo almacenado).
- **`ubicacion`** y **`estadoRepuestos`** viven en `Observacion` (son el estado *al momento de dejar la nota*). **Además**, `OrdenReparacion` ahora tiene un campo `ubicacionActual` (`UbicacionActual`: `EN_TALLER` | `FUERA_DE_TALLER`) como **fuente de verdad** para el Dashboard de Alertas. Se sincroniza automáticamente en tres casos: (1) al registrar una `Observacion` con ubicación, (2) al marcar `fechaIngresoReparacion` → `EN_TALLER`, (3) al pasar a la etapa "Entregado" → `FUERA_DE_TALLER`.
- Rol **RECEPCIONISTA** fue renombrado a **ASESOR**.
- **`TECNICO`** no tiene login al sistema web en el MVP — existe solo como entidad de catálogo para asignación en etapas y futuro cálculo de pagos. (`Usuario.idTecnico` permite vincular un técnico a un usuario *si* ese técnico sí necesita login, ej. rol GERENTE/ADMIN que también es técnico).
- **CESVI** se trackea de forma independiente vía `Valoracion.cargadaCesvi` (boolean) + `cargadaCesviAt`. Se marca manualmente desde la UI, no automáticamente.
- **Control de acceso data-driven**: `Modulo` + `RolModulo` en vez de reglas hardcodeadas en Spring Security. Spring Security consulta `RolModulo` (cacheada) vía JWT en cada request.
- **Modal de cambio de etapa**: selector de técnico obligatorio (FK a `Tecnico`), observación opcional (tabla compartida `Observacion`), notificación WhatsApp opcional (`visibleCliente`), notificación asíncrona que nunca bloquea ni hace rollback de la transacción principal.
- **Modelo anémico**: las entidades no llevan comportamiento. Todo el flujo de cambio de etapa, marcado de CESVI y estado de notificaciones vive en la capa de servicio.

### Flujo de etapas (Kanban)
```
[Asignado] → Desarme → Latonería → Bancada → Electromecánica
→ Pintura → Armado → Control de calidad → Listo para entregar → Entregado
```
- **"Asignado" no es una etapa real** del catálogo `Etapas` — es un estado calculado que aparece como primera columna del Kanban. Muestra órdenes con `fechaIngresoReparacion` marcada pero `idEtapaActual` todavía `NULL`. Al mover la tarjeta de "Asignado" a "Desarme", se crea el primer registro real de etapa.
- **"Ingreso a cotizar" ya no es una etapa del Kanban** — es una fecha del ciclo de vida (`fechaIngresoCotizar`) que se marca desde la pestaña Reparación o al crear una valoración.
- **Movimiento libre hacia adelante** (puede saltar etapas), pero **bloqueado hacia etapas ya visitadas** (validación contra `HistorialEtapas` completo). Las columnas ya visitadas se muestran visualmente deshabilitadas al arrastrar.
- **Técnico obligatorio** al mover tarjeta: el modal de cambio de etapa exige seleccionar un técnico (del catálogo `Tecnico`, no texto libre) antes de guardar.
- **`Vehiculo.idCliente` es de una sola escritura**: se asigna al registrar el vehículo por primera vez y nunca se actualiza después. El cliente real de cada reparación puntual se lee siempre de `OrdenReparacion.idCliente`.

### Fechas del ciclo de vida (no son etapas)
Las siguientes fechas se marcan desde la pestaña "Reparación" de la Ficha de Orden, como fichas visuales separadas de las etapas. No son parte del flujo Kanban:
- **`fechaIngresoCotizar`** — se puede marcar también al crear una valoración.
- **`fechaIngresoReparacion`** — al marcarse, actualiza `ubicacionActual = EN_TALLER` y la orden aparece en "Asignado".
- **`diasEstimadoEntrega`** — se marca una sola vez (no editable después). Genera `fechaDeEntregaEstimada = fechaIngresoReparacion + diasEstimadoEntrega`.

### Alertas de entrega (Dashboard)
- **Críticos**: superaron el umbral de días-en-etapa (umbral individual por etapa, requiere nuevo atributo en `Etapas`).
- **Vencidos**: `fechaDeEntregaEstimada` ya pasó y la orden no está Entregada.
- **Próximos a vencer**: ≤3 días para `fechaDeEntregaEstimada` (constante en código para el MVP).
- **En proceso**: activas dentro de plazo.
- Una orden puede estar en varias alertas simultáneamente, **excepto** que Vencida siempre excluye a Crítica.
- **Color de tarjeta** en el Kanban (misma lógica): rojo = Crítico O Vencido, naranja = Próximo a vencer, normal = ninguno.

### Movimientos Recientes (Dashboard)
Muestra los eventos de las últimas 24h (solo 5 visibles + botón "Ver más"): creación de orden, cambio de etapa, valoración cargada, nueva observación, marcado de fecha, marcado de CESVI. Excluye "notificación enviada" como evento independiente.

### Carga por Etapa (Dashboard)
Solo muestra 6 etapas: Latonería, Pintura, Desarme, Electromecánica, Armado, Control de calidad. Excluye Ingreso a cotizar, Asignado, Listo para entregar, Entregado.

---

## Estado actual de las entidades

| Entidad | Estado | Notas |
|---|---|---|
| `Usuario` | 🔶 En progreso | `email` UNIQUE, `passwordHash` (no `passWordHash`), `@OneToOne` opcional a `Tecnico`. **Le faltan constructores, getters y setters** — se le quitó Lombok sin reponerlos |
| `RolUsuario` (enum) | ✅ Completo | ADMIN, ASESOR, GERENTE, TECNICO |
| `Estado` (enum) | ✅ Completo | ACTIVA, CERRADA, CANCELADA |
| `EstadoEnvio` (enum) | ✅ Completo | PENDIENTE, ENVIADO, FALLIDO |
| `Accion` (enum) | ✅ Completo | REPARACION, SUSTITUCION |
| `Gravedad` (enum) | ✅ Completo | LEVE, MEDIO, FUERTE |
| `Ubicacion` (enum) | ✅ Completo | EN_TALLER, FUERA_DE_TALLER |
| `Repuestos` (enum) | ✅ Completo | COMPLETOS, PENDIENTES |
| `Especialidad` (enum) | ✅ Completo | ARMADOR, LATONERO, MECANICO, ALISTADOR, PINTOR, CONTROL_CALIDAD |
| `UbicacionActual` (enum) | ✅ Completo | EN_TALLER, FUERA_DE_TALLER (nota: duplica valores de `Ubicacion` — pendiente evaluar unificación) |
| `Modulo` | ✅ Completa | PK Long+IDENTITY, `codigo` UNIQUE |
| `RolModulo` | ✅ Completa | `@ManyToOne` a `Modulo`, enum `rol` reutilizado |
| `Persona` | ✅ Completa | **Nueva.** Abstracta, `@Inheritance(JOINED)`, tabla `persona`. PK `idPersona`; `documento` (10, **sin UNIQUE** — se valida en el Service), `nombreCompleto` (200), `celular` (16, E.164), `correo` (100) nullable |
| `Cliente` | ✅ Completa | `extends Persona` + `@PrimaryKeyJoinColumn(name = "id_persona")`. Solo conserva `createAt`, `updateAt` y `@OneToMany vehiculos`. **Sin `idCliente`** — el id se hereda |
| `Tecnico` | 🔶 Pendiente cambio | `extends Persona` + `@PrimaryKeyJoinColumn(name = "id_persona")`. `especialidad` **ahora es enum `Especialidad`** (ARMADOR, LATONERO, MECANICO, ALISTADOR, PINTOR, CONTROL_CALIDAD), ya no texto libre. `activo`, `createAt`, `updateAt` y `@OneToOne usuario`. **Sin `idTecnico`** — el id se hereda |
| `Vehiculo` | 🔶 Pendiente cambio | `@ManyToOne` a `Cliente`; `anio` Short **nullable** (cambio del 2026-09-11); `placa` (6) **UNIQUE** (`uk_vehiculo_placa`), `marca` (20), `modelo` (10), `vin` (100) nullable. **`idCliente` es de una sola escritura** — se asigna al crear y no se modifica después |
| `Etapas` | 🔶 Pendiente cambio | `orden` Integer sin autoincrement; `nombre_etapa` (100). **Falta agregar atributo de días límite** para el cálculo de alertas "Críticos" por etapa |
| `OrdenReparacion` | 🔶 Pendiente cambio | Cambios del 2026-09-11: **se eliminó `idTecnicoResponsable`**, **se agregó `idUsuarioCreador`** (FK a `Usuario`, se llena auto al crear), **se agregó `ubicacionActual`** (enum `UbicacionActual`). `cambiarEtapa(idEtapa, idUsuario, idTecnico)`. **Sin métodos de negocio** |
| `HistorialEtapas` | 🔶 Pendiente cambio | Log append-only. **Se eliminó el campo `comentario`** — las notas viven en `Observacion` (ruta única) |
| `OrdenEtapaFecha` | ✅ Completa | `UNIQUE(id_orden_reparacion, id_etapa)` + flag `completada` |
| `Valoracion` | ✅ Completa | `@OneToOne` con `id_orden_reparacion` UNIQUE; `descripcionGeneral` (500). **Sin cascada** en `detalles`/`imagenes`, sin `marcarCargadaCesvi()` |
| `ValoracionDetalle` | ✅ Completa | **CHECK `ck_detalle_gravedad` eliminado** — la regla pasa al Service |
| `ValoracionImagen` | ✅ Completa | `url` pública https (no BLOB), `length = 2083`; `descripcion` (300) |
| `Observacion` | ✅ Completa | Tabla compartida por el modal del backlog y la ficha de detalle; `nota` (500); única con cascada a `imagenes` |
| `ImagenObservacion` | ✅ Completa | PK `idImagenesObservacion` (plural, según la guía) |
| `Notificaciones` | ✅ Completa | `imagenesEnviadas` como columna `JSON`. **Sin helpers `marcarEnviada()` / `marcarFallida()`**; varias columnas sin `length` |
| `PlantillaMensaje` | ✅ Completa | Sin FK: la consulta el servicio de notificaciones por `evento` |

Leyenda: ✅ revisada/correcta · 🔶 en progreso · ⏳ pendiente

**Transversal a toda la tabla (refactor del 2026-09-05):** ninguna entidad usa Lombok, ninguna tiene `@PrePersist`/`@PreUpdate` y ninguna tiene métodos de negocio. Todas llevan constructor vacío + constructor completo + getters/setters a mano, **salvo `Usuario`**, que perdió las anotaciones de Lombok y quedó sin ningún accesor (ver Pendientes).

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

La capa de modelo (`gestion.reparabilidad.colision.modelo`) tiene sus **18 entidades (17 concretas + la abstracta `Persona`) + 7 enums** escritas y el proyecto **compila** (`./mvnw compile` en verde). Tras el refactor del 2026-09-05 quedaron estos huecos que hay que cerrar **antes** de arrancar repositorios y servicios:

0. **Deuda abierta por el refactor (prioridad alta):**
   - `Usuario` no tiene constructores ni accesores — escribirlos a mano como en el resto de entidades. Compila hoy solo porque nada la consume todavía; cualquier Service o DTO que la use no va a compilar.
   - Asignar `length` explícito a las columnas que quedaron sin él (`Usuario.nombre`, `Usuario.email`, `Usuario.passwordHash`, y `eventoDisparador`, `canal`, `contenidoEnviado`, `errorDetalle` en `Notificaciones`).
   - Decidir cómo se llenan `createAt`/`updateAt` ahora que no hay callbacks — son `nullable = false` y hoy nada los asigna.
   - Reponer (o mover al Service, explícitamente) el CHECK `ck_detalle_gravedad`. ~~y el índice sobre `Vehiculo.placa`~~ — **hecho**: quedó como UNIQUE `uk_vehiculo_placa`.
   - ~~Resolver el conflicto de `celular` `length = 10` vs. formato E.164~~ — **resuelto**: `Persona.celular` quedó en 16 y guarda E.164 con `+`. Queda pendiente la normalización en el Service/DTO.
   - ~~Decidir si se unifica el nombrado de columnas de `Cliente` (sufijo `_cliente`)~~ — **resuelto** por el refactor a `Persona`: esas columnas viven ahora en `persona` sin sufijo.
   - ~~Confirmar el UNIQUE de `Persona.documento`~~ — **decidido el 2026-09-06: se quitó.** Queda la deuda de implementar la validación equivalente en el Service (ver "Validaciones que ya no están en el esquema").
   - **Qué hace y qué no hace `ddl-auto=update`** (verificado contra MySQL en este proyecto):
     - **Sí** crea tablas y columnas que faltan, **sí** crea las FKs, y **sí ensancha** una columna existente (se comprobó `alter table persona modify column celular varchar(16) not null` al subir `celular` de 10 a 16).
     - **Sí** crea un índice declarado con `@Index` que falte (verificado: `create index idx_persona_documento` sobre la tabla `persona` ya existente).
     - **No** agrega un `UNIQUE` a una tabla ya creada (verificado: `unique = true` en `Vehiculo.placa` no produjo ningún DDL; hubo que ejecutar `ALTER TABLE vehiculo ADD CONSTRAINT uk_vehiculo_placa UNIQUE (placa)` a mano).
     - **No** borra ni renombra nada, y **no** angosta una columna. Una columna que se quita de la entidad se queda en la tabla; un `length` que se reduce no se aplica.
     - Ojo: Hibernate ejecuta el schema update **antes** de que Tomcat falle por el puerto ocupado. Un arranque que "falla" con `Port 8080 was already in use` **ya modificó la base**. No asumir que un arranque fallido dejó el esquema intacto.
     - Sobre una base **vacía** (o recién borrada) `update` equivale a un `create` limpio: emite el `CREATE TABLE` completo sin residuos.
     - Por eso, tras un refactor que **quita o renombra** columnas, la única forma limpia con `update` es borrar la base y dejar que se regenere (funciona porque hay `createDatabaseIfNotExist=true` en la URL). Cuando haya datos reales, esto deja de ser viable y toca Flyway/Liquibase.
   - Confirmar si se quita Lombok del `pom.xml`, ya que ninguna clase lo usa.
   - Volver a correr la validación sin base de datos (`./mvnw test` con el `application.properties` de prueba) para regenerar `target/schema-modelo.sql` y revisar el DDL con las longitudes nuevas.
   - Igualar `ImagenObservacion.url` (255) con `ValoracionImagen.url` (2083): guardan el mismo tipo de dato.
   - `Modulo.codigo` con `length = 10` deja justo a códigos como `DETALLE_OT`; uno más descriptivo ya no entra.

1. **Capa de repositorios**: interfaces `JpaRepository` con los finders que pide la guía (`getVehiculoByPlaca`, `getOrdenReparacionByPlaca`, `getValoracionByOrden`, `getPlantillaByEvento`, `getModuloByRol`, `getEtapasCompletadas(desde, hasta)`).
2. **Capa de servicios**: los métodos CRUD que la guía lista dentro de cada clase son de Service/Repository, **no** de la entidad. Ahora *toda* la lógica va ahí — las entidades quedaron sin comportamiento.
3. `@Transactional` sobre el Service que hace el cambio de etapa — la guía exige que actualizar `etapaActual` + insertar `HistorialEtapas` + cerrar/abrir `OrdenEtapaFecha` viajen en una sola transacción. La lógica que estaba en `OrdenReparacion.cambiarEtapa()` hay que reescribirla en ese Service.
4. Regla pendiente de `Observacion`: si la etapa referenciada tiene `fechaInicio = NULL` al crear la observación, el Service debe marcarla con `NOW()` en la misma transacción.
5. Listener `@Async` de notificaciones WhatsApp + interpolación de `PlantillaMensaje` (`{{cliente}}`, `{{placa}}`, `{{etapa}}`). Un fallo de WhatsApp nunca debe romper la operación principal.
6. Configuración de MySQL en `application.properties` (hoy solo tiene `spring.application.name`) y decidir migraciones (Flyway/Liquibase) vs `ddl-auto`.
7. Módulo de **Valoración**: export PDF de la hoja y ZIP de fotos generados al vuelo para CESVI.
8. **Ficha de Orden de Trabajo** — **analizada el 2026-09-11**: 5 pestañas (Información, Reparación, Valoración, Observaciones, Fotografías). Ver sección "Módulos analizados" más abajo.
9. Documentación formal (Reglas de Negocio numeradas, Escenarios, Historias de Usuario, criterios de aceptación).
10. Fase futura: rol de técnico de campo con acceso web/móvil; cálculo de pagos por etapa sobre `OrdenEtapaFecha.idTecnico`.
11. Nombre final del proyecto — aún abierto.

### Decisiones que quedaron abiertas en el modelo

- `contenidoEnviado` (`Notificaciones`) y `contenidoTemplate` (`PlantillaMensaje`) quedaron en `VARCHAR(255)` porque así los define la guía, pero un mensaje de WhatsApp puede pasarse de 255 y MySQL lo cortaría o lanzaría error de truncado. Evaluar subirlos a `TEXT` (`@Column(columnDefinition = "TEXT")`).
- Solo se mapearon las relaciones inversas (`@OneToMany`) que hacen falta hoy: `Cliente.vehiculos`, `Vehiculo.ordenesReparacion`, `Modulo.rolesModulo`, `Tecnico.usuario`, `OrdenReparacion.{historialEtapas, ordenEtapaFechas, valoracion}`, `Valoracion.{detalles, imagenes}` y `Observacion.imagenes`. Las demás (ej. `Cliente → Notificaciones`) se consultan por repositorio.
- Las longitudes nuevas son más estrictas que las de la guía: MySQL **trunca o lanza error de truncado** cuando el dato entrante se pasa. Falta definir la validación en la capa de entrada (Bean Validation `@Size`/`@Pattern` en los DTOs) para que el error se detecte antes de llegar a la base.

---

## Módulos analizados (sesiones de análisis de requisitos, 2026-09-10/11)

La documentación formal (RN, EC, HU, criterios de aceptación) se elaborará al cerrar todos los módulos. Esta sección recoge las decisiones confirmadas por módulo.

### Dashboard de Alertas
- 6 tarjetas clicables: Vehículos en Taller, Fuera del Taller, Críticos, Vencidos, Próximos a Vencer, En Proceso. Clic en la tarjeta navega a lista filtrada (no hay botón "Ver en backlog").
- "Vehículos en Taller" muestra dato adicional "+X ingresos hoy"; los demás sub-datos del prototipo fueron errores de maqueta.
- Solapamiento de alertas permitido, excepto Vencida siempre excluye Crítica.
- Umbral de Críticos: individual por etapa (requiere atributo en `Etapas`). Próximos a vencer: 3 días fijos (constante en código para MVP).
- Movimientos Recientes: 6 tipos de evento, últimas 24h, 5 visibles + "Ver más".
- Carga por Etapa: solo 6 etapas (Latonería, Pintura, Desarme, Electromecánica, Armado, Control de calidad).
- Acceso: todos los roles con acceso al módulo pueden ver el dashboard.

### Backlog Kanban
- Columna calculada "Asignado" (no etapa real) + 9 etapas del catálogo (Desarme → Entregado).
- Movimiento libre hacia adelante, bloqueado hacia etapas ya visitadas (validación contra `HistorialEtapas`, Opción A). Columnas bloqueadas se muestran visualmente deshabilitadas al arrastrar (Opción B UX).
- Tarjeta muestra: placa, marca/modelo/año, cliente, "X días aquí" (con color de alerta), número de OT. Color: rojo (crítico/vencido), naranja (próximo a vencer), normal.
- Modal de cambio de etapa: técnico obligatorio (del catálogo `Tecnico`, FK), nota opcional (crea `Observacion` con `ubicacion=EN_TALLER`), checkbox notificar al cliente. Un solo botón "Guardar". La nota se guarda en la misma tabla `Observacion` que la pestaña Observaciones — ruta única.
- Notificación: si hay nota + checkbox marcado, se envía un solo mensaje combinado (cambio de etapa + nota). Sin límite de mensajes. El campo `visibleCliente` de la `Observacion` creada refleja el estado del checkbox.
- `cambiarEtapa()` en dos fases: (1) transaccional (BD): actualizar `idEtapaActual` + insertar `HistorialEtapas` + cerrar/abrir `OrdenEtapaFecha` con técnico + crear `Observacion` si hay nota; (2) asíncrona: envío WhatsApp (`@Async`, fallo no hace rollback).
- Acceso: ADMIN, ASESOR, GERENTE. TECNICO no tiene acceso al módulo.

### Ficha de Orden de Trabajo (5 pestañas)

**Pestaña Información:**
- 4 bloques: Vehículo (marca, modelo, año, placa, color), Cliente (nombre, documento, celular, correo — leído desde `OrdenReparacion.idCliente`), Etapa actual (etapa, técnico de etapa, "desde", asesor de la orden = último `HistorialEtapas.idUsuario`), Entrega (ingreso, estimada, días en taller, estado).
- "Días en taller" = desde `fechaIngresoReparacion` hasta hoy. Si no hay `fechaIngresoReparacion`, se muestra vacío/0.

**Pestaña Reparación:**
- 3 fichas superiores (fechas del ciclo de vida): Ingreso a cotizar, Ingreso al taller, Días estimados de entrega. Se marcan una sola vez, sin edición posterior, con confirmación previa. Solo tienen botón de calendario (marca fecha), excepto "Días estimados" que es un campo numérico sin botón de calendario.
- `diasEstimadoEntrega` se marca después de `fechaIngresoReparacion`. No es editable después. `fechaDeEntregaEstimada = fechaIngresoReparacion + diasEstimadoEntrega`.
- 9 fichas de etapas reales (Desarme → Entregado): muestran nombre, técnico asignado, fecha, estado visual (verde completada, naranja en curso, gris pendiente). Solo tienen botón de lápiz → abre el mismo modal de cambio de etapa del Kanban (técnico obligatorio + nota opcional + notificar opcional).

**Pestaña Valoración:**
- Placeholder — se definirá con el módulo de Valoración completo.
- Debe mostrar la hoja de valoración, permitir edición, y descargar PDF + ZIP de fotos.

**Pestaña Observaciones:**
- Lista única que mezcla observaciones de todos los orígenes (Kanban, pestaña Reparación, esta misma pestaña) sin distinción visual de origen.
- Formulario de nueva observación: selector de etapa (solo etapas ya visitadas, según `HistorialEtapas`), texto de nota, adjuntar imágenes (sin límite), checkbox notificar al cliente. `ubicacion` se asigna automáticamente como `EN_TALLER`.
- Se muestra: autor, fecha/hora, badge "ENVIADO AL CLIENTE CON X FOTOS" si `visibleCliente=true`, texto de la nota.

**Pestaña Fotografías:**
- Solo lectura — no se pueden agregar fotos desde aquí (el botón "+ Agregar" del prototipo queda descartado).
- Muestra todas las fotos de la orden: las de Valoración y las de Observaciones.
- Cada foto indica su origen: "Valoración" o el nombre de la etapa a la que pertenece la observación.

### Nueva Orden
- Acceso: todos los roles excepto TECNICO.
- Formulario progresivo (un solo formulario que se va revelando, no pantallas separadas):
  1. Campo placa → auto-búsqueda al completar 6 caracteres.
  2. Si placa existe: muestra datos del vehículo y cliente vinculado. Pregunta "¿Es quien trae el vehículo?". Si "Sí" → formulario completo, solo falta confirmar. Si "No" → revela campo Documento.
  3. Si placa no existe: revela campo Documento directamente.
  4. Documento → auto-búsqueda. Si existe: muestra nombre (solo lectura). Si no existe: revela campos de cliente nuevo (nombre, celular, correo).
  5. Si placa no existía: campos de vehículo nuevo (marca, modelo, año, color). VIN y año son opcionales.
  6. Un solo botón "Crear orden".
- 4 combinaciones según existencia de placa/documento (qué se crea: Persona+Cliente, Vehiculo, OrdenReparacion).
- `Vehiculo.placa` es UNIQUE. `Vehiculo.idCliente` es de una sola escritura (se asigna al crear el vehículo, nunca se actualiza — el cliente de cada reparación se lee de `OrdenReparacion.idCliente`).
- Estado inicial: `Estado.ACTIVA`. Numeración: correlativo simple desde 1, sin prefijo (el `idOrdenReparacion` del AUTO_INCREMENT es el número de OT).
- `documento` solo acepta cédula colombiana (length 10, sin NIT).
- `idUsuarioCreador` se llena automáticamente con el usuario que crea la orden.

### Módulos pendientes de análisis
- **Valoración**: módulo completo (incluye pestaña dentro de la Ficha y sección propia del menú lateral). Export PDF + ZIP para CESVI.
- **Técnicos**: pantalla de gestión del catálogo (CRUD).
- **Clientes**: marcado como "PRÓXIMO" en el menú — fuera del MVP.

---

## Cómo trabajar en este proyecto (para Claude Code)

- La guía de referencia **está en la raíz del repo**: `GuiaDiagranaUML.pdf` (21 páginas, con atributos, tipos, restricciones, métodos y reglas de negocio de cada clase). Para leerla: `pip install pypdf` y extraer el texto con `pypdf.PdfReader`.
- **La guía se regenera desde código, no se edita a mano.** Su fuente vive en `docs/`:
  - `docs/contenido_guia.py` — los datos (una función por sección, con las tablas de atributos y métodos de cada clase).
  - `docs/generar_guia.py` — el layout (estilos, tablas, recuadros, pie de página).
  ```bash
  pip install reportlab
  python docs/generar_guia.py   # reescribe GuiaDiagranaUML.pdf en la raíz
  ```
  El PDF original venía de ReportLab pero sin fuente en el repo, así que cada corrección obligaba a rehacerlo entero. Al cambiar una longitud o una regla, se edita `docs/contenido_guia.py` y se regenera.
- **Precedencia entre documentos:** manda el **código**. Antes la guía era la fuente de verdad, pero desde el refactor del 2026-09-05 las longitudes de columna se ajustaron a la necesidad real del taller y ya no coinciden con el `VARCHAR(255)` original. Si una entidad y la guía difieren, se corrige la guía (regenerándola) y este archivo, no la entidad.
- Los tres documentos (`CLAUDE.md`, `GuiaDiagranaUML.pdf`, entidades JPA) están sincronizados a 2026-09-05. Al cambiar el modelo hay que actualizar los tres.
- Antes de generar o corregir una entidad, contrastar contra la guía — no asumir nombres de campos ni cardinalidades sin verificar. **Excepción: las longitudes de columna.** Se ajustaron a la necesidad real del taller y ya no coinciden con el `VARCHAR(255)` de la guía; ahí manda la tabla de la sección "Longitudes de String" y el código.
- **No reintroducir Lombok** en las entidades: los getters, setters y constructores se escriben a mano (ver sección correspondiente).
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