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
- **`Cliente` es la excepción a la simetría atributo/columna**: sus columnas llevan sufijo de tabla (`documento_cliente`, `nombre_cliente`, `celular_cliente`, `correo_cliente`) mientras los atributos Java siguen siendo `documento`, `nombreCompleto`, `celular`, `correo`. Ninguna otra entidad usa ese sufijo (`Tecnico` tiene `documento`, `nombre_completo`, `celular`, `correo` sin sufijo). Si aparece una inconsistencia al escribir queries nativas o el DDL, es por esto. Pendiente decidir si se unifica en un sentido o en el otro.
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
| `Cliente` | `documento_cliente` | 10 |
| `Cliente` | `nombre_cliente` | 100 |
| `Cliente` | `celular_cliente` | 10 |
| `Cliente` | `correo_cliente` | 100 |
| `Tecnico` | `documento` | 10 |
| `Tecnico` | `nombre_completo` | 200 |
| `Tecnico` | `celular` | 10 |
| `Tecnico` | `correo` | 50 |
| `Tecnico` | `especialidad` | 100 |
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
- `celular` en 10 asume el móvil colombiano **sin prefijo internacional** (`3001234567`). Esto es incompatible con guardar E.164 (`+573001234567`, 13 caracteres), que es lo que pide la API de WhatsApp. **Pendiente:** o se sube a `length = 15` y se guarda en E.164, o el Service antepone `+57` al construir el mensaje. Aplica a `Cliente.celular` y `Tecnico.celular`.
- `documento` en 10 cubre cédula colombiana; no alcanza para NIT con dígito de verificación (`900123456-7`, 11 caracteres). Confirmar si el taller factura a empresas.
- `Vehiculo.placa` en 6 es exacto para placa colombiana (`ABC123` / `ABC12D`), sin guiones. El Service debe normalizar (mayúsculas, sin espacios ni guión) antes de guardar, o el INSERT se trunca.
- `Vehiculo.modelo` en 10 — si "modelo" es la línea del vehículo (`Sandero Stepway`) se queda corto; si es el año/versión corta, está bien. Verificar contra el uso real.
- `ValoracionImagen.url` en 2083 es el límite práctico de URL de los navegadores. Nota: MySQL con `utf8mb4` **no permite indexar** una columna así completa (2083 × 4 bytes supera el límite de índice), así que no se le puede poner UNIQUE sin prefijo de índice.
- Columnas `String` que hoy **no** declaran `length` (quedan en el default 255): `Usuario.nombre`, `Usuario.email`, `Usuario.passwordHash`, `Notificaciones.eventoDisparador`, `Notificaciones.canal`, `Notificaciones.contenidoEnviado`, `Notificaciones.errorDetalle`. Rompen la convención de "siempre `length` explícito" — pendiente asignarles ancho. Ojo con `passwordHash`: un hash BCrypt son 60 caracteres, Argon2 puede pasar de 95.

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
- **CHECK `ck_detalle_gravedad`** en `ValoracionDetalle` (`@Table(check = @CheckConstraint(...))` de JPA 3.2). La regla sigue vigente en el negocio: `gravedad` solo aplica con `accion = REPARACION`; con `SUSTITUCION` debe quedar `NULL`. Hoy nada lo impide a nivel de base de datos.
- **INDEX `idx_vehiculo_placa`** en `Vehiculo.placa`. La búsqueda por placa (`getVehiculoByPlaca`, `getOrdenReparacionByPlaca`) es la consulta más frecuente del sistema y hoy hace full scan. Conviene reponer el índice — no era UNIQUE, y no debe serlo (un vehículo puede volver al taller y las placas se reasignan).

### Otros
- `orden` en `Etapas` es `Integer` y **no** lleva `@GeneratedValue` — es un valor de negocio reordenable manualmente (admin puede insertar/reordenar etapas), no una secuencia autogenerada.
- `fechaFin` en etapas nunca se marca manualmente desde la UI — la asigna el sistema al cerrar la etapa anterior (ahora desde el Service, ya no desde `cambiarEtapa()` en la entidad).
- `OrdenReparacion.diasEstimadoEntrega` es `Byte` (TINYINT): máximo 127 días. Si una reparación puede pasar de eso, hay que subirlo a `Short`.
- Se quitaron los comentarios explicativos que tenían varias entidades (`Valoracion`, `ValoracionDetalle`, `ValoracionImagen`, `PlantillaMensaje`). Las reglas que documentaban quedaron recogidas en este archivo.

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
- **Modelo anémico**: las entidades no llevan comportamiento. Todo el flujo de cambio de etapa, marcado de CESVI y estado de notificaciones vive en la capa de servicio.

### Flujo de etapas (Kanban)
```
Ingreso a cotizar → Desarme → Latonería → Bancada → Electromecánica
→ Pintura → Armado → Control de calidad → Listo para entregar → Entregado
```

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
| `Modulo` | ✅ Completa | PK Long+IDENTITY, `codigo` UNIQUE |
| `RolModulo` | ✅ Completa | `@ManyToOne` a `Modulo`, enum `rol` reutilizado |
| `Cliente` | ✅ Completa | Columnas con sufijo `_cliente`; `documento` (10), `nombreCompleto` (100), `celular` (10), `correo` (100) |
| `Tecnico` | ✅ Completa | Tabla `tecnico`, `documento` UNIQUE (10), `nombreCompleto` (200), `correo` (50) / `especialidad` (100) nullable |
| `Vehiculo` | ✅ Completa | `@ManyToOne` a `Cliente`; `anio` Short NOT NULL; `placa` (6), `marca` (20), `modelo` (10), `vin` (100). **INDEX en `placa` eliminado** |
| `Etapas` | ✅ Completa | `orden` Integer sin autoincrement; `nombre_etapa` (100) |
| `OrdenReparacion` | ✅ Completa | 5 FKs + enum `Estado` + 4 fechas `LocalDate` + `diasEstimadoEntrega` Byte. **Sin métodos de negocio** |
| `HistorialEtapas` | ✅ Completa | Log append-only: solo INSERT, nunca UPDATE |
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

La capa de modelo (`gestion.reparabilidad.colision.modelo`) tiene sus **17 entidades + 7 enums** escritas y el proyecto **compila** (`./mvnw compile` en verde). Tras el refactor del 2026-09-05 quedaron estos huecos que hay que cerrar **antes** de arrancar repositorios y servicios:

0. **Deuda abierta por el refactor (prioridad alta):**
   - `Usuario` no tiene constructores ni accesores — escribirlos a mano como en el resto de entidades. Compila hoy solo porque nada la consume todavía; cualquier Service o DTO que la use no va a compilar.
   - Asignar `length` explícito a las columnas que quedaron sin él (`Usuario.nombre`, `Usuario.email`, `Usuario.passwordHash`, y `eventoDisparador`, `canal`, `contenidoEnviado`, `errorDetalle` en `Notificaciones`).
   - Decidir cómo se llenan `createAt`/`updateAt` ahora que no hay callbacks — son `nullable = false` y hoy nada los asigna.
   - Reponer (o mover al Service, explícitamente) el CHECK `ck_detalle_gravedad` y el índice sobre `Vehiculo.placa`.
   - Resolver el conflicto de `celular` `length = 10` vs. formato E.164 que exige WhatsApp.
   - Decidir si se unifica el nombrado de columnas de `Cliente` (sufijo `_cliente`) con el del resto del modelo.
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
8. **Ficha de Orden de Trabajo** — pendiente de analizar.
9. Documentación formal (Reglas de Negocio numeradas, Escenarios, Historias de Usuario, criterios de aceptación).
10. Fase futura: rol de técnico de campo con acceso web/móvil; cálculo de pagos por etapa sobre `OrdenEtapaFecha.idTecnico`.
11. Nombre final del proyecto — aún abierto.

### Decisiones que quedaron abiertas en el modelo

- `contenidoEnviado` (`Notificaciones`) y `contenidoTemplate` (`PlantillaMensaje`) quedaron en `VARCHAR(255)` porque así los define la guía, pero un mensaje de WhatsApp puede pasarse de 255 y MySQL lo cortaría o lanzaría error de truncado. Evaluar subirlos a `TEXT` (`@Column(columnDefinition = "TEXT")`).
- Solo se mapearon las relaciones inversas (`@OneToMany`) que hacen falta hoy: `Cliente.vehiculos`, `Vehiculo.ordenesReparacion`, `Modulo.rolesModulo`, `Tecnico.usuario`, `OrdenReparacion.{historialEtapas, ordenEtapaFechas, valoracion}`, `Valoracion.{detalles, imagenes}` y `Observacion.imagenes`. Las demás (ej. `Cliente → Notificaciones`) se consultan por repositorio.
- Las longitudes nuevas son más estrictas que las de la guía: MySQL **trunca o lanza error de truncado** cuando el dato entrante se pasa. Falta definir la validación en la capa de entrada (Bean Validation `@Size`/`@Pattern` en los DTOs) para que el error se detecte antes de llegar a la base.

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