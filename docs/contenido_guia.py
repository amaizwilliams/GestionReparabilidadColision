# -*- coding: utf-8 -*-
"""
Contenido de la guia de referencia. Se separa del layout (generar_guia.py)
para que corregir un dato del modelo no obligue a tocar el codigo de dibujo.

Los valores de la columna "Long." salen de las anotaciones @Column reales de
gestion.reparabilidad.colision.modelo. Si cambia una longitud en una entidad,
se cambia aqui y se regenera el PDF.
"""

from reportlab.lib.units import cm
from reportlab.platypus import PageBreak, Paragraph, Spacer

from generar_guia import (
    A_ATTR, ANCHO, E_CUERPO, E_ETIQUETA, E_PORTADA_P, E_PORTADA_S,
    E_PORTADA_T, E_REGLA, aviso, clase, seccion, tabla,
)

FECHA = "5 de septiembre de 2026"


def portada():
    return [
        Spacer(1, 4.2 * cm),
        Paragraph("Sistema de gestión", E_PORTADA_T),
        Paragraph("Taller de colisión de vehículos", E_PORTADA_T),
        Spacer(1, 0.9 * cm),
        Paragraph("Guía de referencia para codificación", E_PORTADA_S),
        Paragraph("Clases · Atributos · Tipos de datos · Métodos · Reglas de negocio",
                  E_PORTADA_S),
        Spacer(1, 0.6 * cm),
        Paragraph("Stack: React + Spring Boot + MySQL", E_PORTADA_P),
        Spacer(1, 2.2 * cm),
        aviso(
            "Versión sincronizada con el código &mdash; " + FECHA,
            ["Las longitudes de las columnas <b>VARCHAR</b> ya no son 255 genérico: "
             "se ajustaron al dato real del taller. Esta guía refleja lo que está "
             "anotado hoy en las entidades JPA.",
             "Los recuadros como este marcan dónde la versión anterior de la guía "
             "quedó desactualizada, para que se note qué cambió y por qué.",
             "Regla de precedencia: <b>manda el código</b>. Si una entidad y esta "
             "guía difieren, se corrige la guía (y se regenera desde "
             "<b>docs/generar_guia.py</b>), no la entidad."]),
        Spacer(1, 1.4 * cm),
        Paragraph("Convenciones usadas en este documento", E_ETIQUETA),
        Paragraph(
            "[0..1] = campo nullable (opcional) &nbsp;|&nbsp; PK = llave primaria "
            "&nbsp;|&nbsp; FK = llave foránea &nbsp;|&nbsp; UNIQUE = restricción de "
            "unicidad<br/>Los tipos Long/Int/Short son Java; la columna MySQL muestra "
            "el equivalente en el esquema.<br/>La columna <b>Nombre</b> usa el nombre "
            "del atributo Java; cuando la columna SQL no coincide, se indica entre "
            "paréntesis.", E_CUERPO),
        PageBreak(),
    ]


def cambios_globales():
    fl = seccion("Cambios de esta versión")
    fl.append(Paragraph(
        "Refactor del modelo del " + FECHA + ". Afecta a las 17 entidades, así que "
        "se documenta una sola vez aquí en vez de repetirlo en cada clase.", E_CUERPO))
    fl.append(Spacer(1, 6))
    fl.append(tabla(
        ["Qué cambió", "Antes", "Ahora"],
        [["Longitudes VARCHAR",
          "255 para todo campo de texto",
          "Ajustadas al dato real (ver cada clase). Ej: placa 6, documento 10, "
          "celular 10, nota 500, url de imagen 2083"],
         ["Lombok",
          "@Getter / @Setter / @NoArgsConstructor",
          "Sin Lombok. Cada entidad escribe a mano constructor vacío, constructor "
          "completo, getters y setters"],
         ["Timestamps",
          "@PrePersist / @PreUpdate en la entidad",
          "Callbacks eliminados. createAt / updateAt los asigna el Service (siguen "
          "siendo NOT NULL)"],
         ["Métodos de negocio",
          "cambiarEtapa(), asignarTecnico(), calcularDiasEnTaller(), "
          "marcarCargadaCesvi(), marcarEnviada()/marcarFallida() dentro de la entidad",
          "Entidades anémicas: no tienen comportamiento. Toda la lógica vive en la "
          "capa de servicio"],
         ["CHECK de gravedad",
          "CHECK ck_detalle_gravedad en el DDL de ValoracionDetalle",
          "Eliminado del esquema. La regla sigue vigente pero la valida el Service"],
         ["Índice de placa",
          "INDEX idx_vehiculo_placa",
          "Eliminado. Pendiente reponerlo: la búsqueda por placa es la consulta más "
          "frecuente del sistema"],
         ["Cascadas",
          "Valoracion.detalles y Valoracion.imagenes con CASCADE ALL + orphanRemoval",
          "Sin cascada: el Service borra los hijos por su propio repositorio. Sólo "
          "Observacion.imagenes conserva la cascada"],
         ["Columnas de Cliente",
          "documento, nombre_completo, celular, correo",
          "Con sufijo de tabla: documento_cliente, nombre_cliente, celular_cliente, "
          "correo_cliente (los atributos Java no cambian)"]],
        [3.2 * cm, 6.4 * cm, 8.8 * cm]))
    fl.append(Spacer(1, 12))
    fl.append(Paragraph("PENDIENTES QUE DEJÓ EL REFACTOR", E_ETIQUETA))
    for r in [
        "<b>Usuario</b> se quedó sin constructores, getters ni setters: se le quitó "
        "Lombok y no se repusieron. Compila porque todavía nada la consume.",
        "Faltan longitudes explícitas en <b>Usuario</b> (nombre, email, passwordHash) "
        "y en <b>Notificaciones</b> (eventoDisparador, canal, contenidoEnviado, "
        "errorDetalle). Quedan en el default 255.",
        "Nadie asigna createAt / updateAt hoy: hay que decidir entre hacerlo en el "
        "Service, reponer los callbacks JPA o usar auditoría de Spring Data.",
        "<b>celular</b> con length 10 no admite el formato E.164 (+573001234567, "
        "13 caracteres) que exige la API de WhatsApp.",
        "Las longitudes nuevas son estrictas: MySQL trunca o falla si el dato entra "
        "más largo. Falta validación con Bean Validation (@Size / @Pattern) en los DTOs.",
    ]:
        fl.append(Paragraph("&rarr; " + r, E_REGLA))
    fl.append(PageBreak())
    return fl


def tipos_de_datos():
    fl = seccion("Tipos de datos — Java &rarr; MySQL")
    fl.append(Paragraph(
        "Referencia rápida para mapear los tipos del modelo Java al esquema MySQL al "
        "crear las entidades JPA y las migraciones.", E_CUERPO))
    fl.append(Spacer(1, 6))
    fl.append(tabla(
        ["Tipo Java", "MySQL", "Long./Prec.", "Uso típico"],
        [["Long", "BIGINT", "20", "Llaves primarias y foráneas"],
         ["Integer", "INT", "11", "Enteros medianos (orden de etapa)"],
         ["Short", "SMALLINT", "6", "Enteros pequeños (año del vehículo)"],
         ["Byte", "TINYINT", "3", "Valores muy pequeños (días estimados, máx. 127)"],
         ["boolean", "BOOLEAN", "1", "true/false &rarr; TINYINT(1) en MySQL"],
         ["String", "VARCHAR", "según campo",
          "Ya no se usa 255 por defecto: cada columna declara su ancho real"],
         ["LocalDate", "DATE", "—", "Solo fecha: yyyy-MM-dd"],
         ["LocalDateTime", "DATETIME", "—", "Fecha + hora: yyyy-MM-dd HH:mm:ss"],
         ["BigDecimal", "DECIMAL", "12,2", "Montos y costos"],
         ["byte[]", "BLOB", "—", "Archivos binarios (no se usa hoy)"],
         ["String (JSON)", "JSON", "—", "Arrays JSON como imagenesEnviadas"]],
        [3.4 * cm, 3.0 * cm, 3.0 * cm, 9.0 * cm]))
    fl.append(Spacer(1, 10))
    fl.append(aviso("Sobre las longitudes", [
        "Los <b>enums</b> van siempre con <b>length = 20</b> y "
        "<b>@JdbcTypeCode(SqlTypes.VARCHAR)</b>. Sin ese JdbcTypeCode, Hibernate 7 "
        "genera un tipo ENUM(...) nativo de MySQL en vez de VARCHAR(20), lo que "
        "obliga a un ALTER TABLE cada vez que se agrega un valor al enum.",
        "<b>documento</b> y <b>celular</b> son String aunque parezcan numéricos: un "
        "documento puede llevar ceros a la izquierda o dígito de verificación.",
        "Ninguna columna guarda imágenes como BLOB. Se guarda la URL pública https "
        "porque WhatsApp y CESVI descargan el archivo, no lo reciben."]))
    fl.append(PageBreak())
    return fl


def enumeraciones():
    fl = seccion("Enumeraciones")
    fl.append(Paragraph(
        "Tipos enum usados como atributos en las clases. Viven en el subpaquete "
        "<b>modelo.Enums</b> y son clases Java puras, sin anotaciones. La anotación "
        "va en la entidad que los usa: @Enumerated(EnumType.STRING) + "
        "@JdbcTypeCode(SqlTypes.VARCHAR) + @Column(length = 20).", E_CUERPO))
    fl.append(Spacer(1, 6))
    fl.append(tabla(
        ["Enum", "Valores", "Descripción y uso"],
        [["RolUsuario", "ADMIN | ASESOR | GERENTE | TECNICO",
          "Controla qué módulos ve cada usuario. GERENTE: sin admin de usuarios. "
          "ASESOR: operación diaria."],
         ["Estado", "ACTIVA | CERRADA | CANCELADA",
          "Estado de la OrdenReparacion. Un boolean no alcanza para este caso."],
         ["EstadoEnvio", "PENDIENTE | ENVIADO | FALLIDO",
          "Estado del mensaje en Notificaciones. Permite reintentos y auditoría."],
         ["Accion", "REPARACION | SUSTITUCION",
          "Qué se hace con la pieza en ValoracionDetalle."],
         ["Gravedad", "LEVE | MEDIO | FUERTE",
          "Solo aplica si Accion = REPARACION. Si es SUSTITUCION, este campo es NULL."],
         ["Ubicacion", "EN_TALLER | FUERA_DE_TALLER",
          "Ubicación del vehículo al momento de la Observacion."],
         ["Repuestos", "COMPLETOS | PENDIENTES",
          "Estado de los repuestos al dejar la Observacion."]],
        [3.0 * cm, 5.4 * cm, 10.0 * cm]))
    fl.append(PageBreak())
    return fl


def nota_metodos():
    return [aviso("Sobre las tablas de MÉTODOS", [
        "Las firmas que se listan en cada clase describen la <b>API de la capa de "
        "Service/Repository</b>, no métodos de la entidad JPA.",
        "Tras el refactor, <b>ninguna entidad tiene métodos de negocio</b>. Lo que "
        "antes vivía en OrdenReparacion, Valoracion y Notificaciones se reescribe en "
        "el Service correspondiente."]), Spacer(1, 10)]


# --------------------------------------------------------------- catalogos ---
def catalogos():
    fl = seccion("Catálogos")
    fl += nota_metodos()

    fl += clase(
        "Cliente",
        "Propietario del vehículo. Contiene todos sus datos de contacto directamente "
        "(Opción A: sin clase Persona).",
        [["idCliente", "Long", "BIGINT", "20", "PK", "Identificador único autoincremental"],
         ["documento<br/>(documento_cliente)", "String", "VARCHAR", "10", "NOT NULL",
          "Cédula del cliente. 10 no alcanza para NIT con dígito de verificación"],
         ["nombreCompleto<br/>(nombre_cliente)", "String", "VARCHAR", "100", "NOT NULL",
          "Nombre y apellidos completos"],
         ["celular<br/>(celular_cliente)", "String", "VARCHAR", "10", "NOT NULL",
          "Móvil colombiano sin prefijo, ej: 3001234567"],
         ["correo<br/>(correo_cliente)", "String", "VARCHAR", "100", "[0..1]",
          "Correo electrónico, nullable"],
         ["createAt", "LocalDateTime", "DATETIME", "—", "NOT NULL",
          "Fecha de creación. La asigna el Service"],
         ["updateAt", "LocalDateTime", "DATETIME", "—", "NOT NULL",
          "Última modificación. La asigna el Service"]],
        [["createCliente(Cliente)", "Cliente", "Crea nuevo cliente"],
         ["updateCliente(Cliente)", "Cliente", "Actualiza datos del cliente"],
         ["getClienteById(idCliente)", "Cliente", "Busca por ID"],
         ["getClienteByDocumento(documento)", "Cliente", "Busca por cédula/NIT"],
         ["getAllCliente()", "List&lt;Cliente&gt;", "Lista todos los clientes"],
         ["buscarPorPlaca(placa)", "Cliente", "Busca cliente dueño de la placa"],
         ["deleteClienteById(id)", "void", "Elimina cliente por ID"]],
        relaciones=[
            "Cliente 1 &rarr; N Vehiculo &nbsp;(mapeada: Cliente.vehiculos)",
            "Cliente 1 &rarr; N OrdenReparacion &nbsp;(sólo el lado dueño)",
            "Cliente 1 &rarr; N Notificaciones (receptor de mensajes) &nbsp;"
            "(sólo el lado dueño; se consulta por repositorio)"],
        cambios=("Cambios respecto a la versión anterior", [
            "Longitudes: documento 255 &rarr; <b>10</b>, nombreCompleto 255 &rarr; "
            "<b>100</b>, celular 255 &rarr; <b>10</b>, correo 255 &rarr; <b>100</b>.",
            "Las columnas SQL llevan sufijo <b>_cliente</b>. Es la única entidad del "
            "modelo con ese criterio; el resto usa el nombre pelado.",
            "<b>celular</b> ya no guarda formato E.164 (+57...): con length 10 no cabe. "
            "El Service tiene que anteponer el prefijo al armar el mensaje de WhatsApp, "
            "o hay que subir la columna a 15."]))

    fl.append(PageBreak())

    fl += clase(
        "Tecnico",
        "Trabajador del taller. Tabla independiente (Opción A). Puede estar vinculado a "
        "un Usuario si necesita login. En el MVP no tiene acceso web.",
        [["idTecnico", "Long", "BIGINT", "20", "PK", "Identificador único autoincremental"],
         ["documento", "String", "VARCHAR", "10", "NOT NULL, UNIQUE", "Cédula del técnico"],
         ["nombreCompleto", "String", "VARCHAR", "200", "NOT NULL", "Nombre y apellidos"],
         ["celular", "String", "VARCHAR", "10", "NOT NULL", "Teléfono de contacto"],
         ["correo", "String", "VARCHAR", "50", "[0..1]", "Correo electrónico, nullable"],
         ["especialidad", "String", "VARCHAR", "100", "[0..1]",
          "Ej: Pintura, Latonería, Electromecánica"],
         ["activo", "boolean", "BOOLEAN", "1", "NOT NULL", "false = técnico desactivado"],
         ["createAt", "LocalDateTime", "DATETIME", "—", "NOT NULL", "La asigna el Service"],
         ["updateAt", "LocalDateTime", "DATETIME", "—", "NOT NULL", "La asigna el Service"]],
        [["createTecnico(Tecnico)", "Tecnico", "Crea técnico"],
         ["getTecnicoById(id)", "Tecnico", "Busca por ID"],
         ["updateTecnico(Tecnico)", "Tecnico", "Actualiza datos"],
         ["getAllTecnico()", "List&lt;Tecnico&gt;", "Lista todos los técnicos"],
         ["getEtapasCompletadas(desde, hasta)", "List&lt;OrdenEtapaFecha&gt;",
          "Base para el cálculo de pagos futuros"]],
        relaciones=[
            "Tecnico referenciado desde OrdenReparacion.tecnicoResponsable (nullable)",
            "Tecnico referenciado desde OrdenEtapaFecha.tecnico (quién trabajó cada etapa)",
            "Tecnico 1 &rarr; 1 Usuario &nbsp;(mapeada: Tecnico.usuario, opcional)"],
        cambios=("Cambios respecto a la versión anterior", [
            "Longitudes: documento 255 &rarr; <b>10</b>, nombreCompleto 255 &rarr; "
            "<b>200</b>, celular 255 &rarr; <b>10</b>, correo 255 &rarr; <b>50</b>, "
            "especialidad 255 &rarr; <b>100</b>.",
            "Las columnas <b>no</b> llevan sufijo de tabla, a diferencia de Cliente.",
            "El nombre de la tabla es <b>tecnico</b> (en una versión anterior decía "
            "&laquo;tenico&raquo;)."]))

    fl.append(PageBreak())

    fl += clase(
        "Vehiculo",
        "Vehículo registrado en el taller. La placa es el campo de búsqueda principal "
        "de todo el sistema.",
        [["idVehiculo", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["cliente<br/>(id_cliente)", "Long", "BIGINT", "20", "FK NOT NULL",
          "Referencia al propietario"],
         ["placa", "String", "VARCHAR", "6", "NOT NULL",
          "Placa colombiana sin guion: ABC123 o ABC12D. Ya no lleva INDEX"],
         ["marca", "String", "VARCHAR", "20", "NOT NULL", "Marca del vehículo"],
         ["modelo", "String", "VARCHAR", "10", "NOT NULL", "Línea o modelo"],
         ["anio", "Short", "SMALLINT", "6", "NOT NULL", "Año de fabricación"],
         ["color", "String", "VARCHAR", "50", "[0..1]", "Color del vehículo"],
         ["vin", "String", "VARCHAR", "100", "[0..1]",
          "Número de identificación vehicular"]],
        [["createVehiculo(Vehiculo)", "Vehiculo", "Registra vehículo nuevo"],
         ["getVehiculoById(idVehiculo)", "Vehiculo", "Busca por ID"],
         ["getVehiculoByPlaca(placa)", "Vehiculo", "Busca por placa — uso frecuente"],
         ["updateVehiculo(Vehiculo)", "Vehiculo", "Actualiza datos del vehículo"],
         ["getAllVehiculo()", "List&lt;Vehiculo&gt;", "Lista todos los vehículos"],
         ["deleteVehiculoById(id)", "void", "Elimina vehículo"]],
        reglas=[
            "El Service debe normalizar la placa (mayúsculas, sin espacios ni guion) "
            "antes de guardar: con length 6 cualquier caracter extra se trunca.",
            "La placa <b>no</b> es UNIQUE: un vehículo puede volver al taller y las "
            "placas se reasignan."],
        relaciones=[
            "Vehiculo N &rarr; 1 Cliente",
            "Vehiculo 1 &rarr; N OrdenReparacion &nbsp;(mapeada: Vehiculo.ordenesReparacion)"],
        cambios=("Cambios respecto a la versión anterior", [
            "Longitudes: placa 255 &rarr; <b>6</b>, marca 255 &rarr; <b>20</b>, "
            "modelo 255 &rarr; <b>10</b>, color 255 &rarr; <b>50</b>, vin 255 &rarr; "
            "<b>100</b>.",
            "<b>Se eliminó el índice idx_vehiculo_placa.</b> Hoy la búsqueda por placa "
            "hace full scan. Conviene reponerlo (como INDEX, no UNIQUE).",
            "<b>modelo</b> con length 10 se queda corto si aquí va la línea completa "
            "del vehículo (ej: &laquo;Sandero Stepway&raquo;). Verificar el uso real."]))

    fl.append(PageBreak())

    fl += clase(
        "Etapas",
        "Catálogo configurable del flujo de reparación. NO se hardcodea en el backend. "
        "Flujo: Ingreso a cotizar &rarr; Desarme &rarr; Latonería &rarr; Bancada &rarr; "
        "Electromecánica &rarr; Pintura &rarr; Armado &rarr; Control de calidad &rarr; "
        "Listo para entregar &rarr; Entregado.",
        [["idEtapa", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["nombre<br/>(nombre_etapa)", "String", "VARCHAR", "100", "NOT NULL",
          "Nombre de la etapa, ej: Desarme"],
         ["orden", "Integer", "INT", "11", "NOT NULL",
          "Posición en el flujo (1, 2, 3…). Sin @GeneratedValue: es un valor de "
          "negocio reordenable por el admin"],
         ["descripcion", "String", "VARCHAR", "255", "[0..1]",
          "Descripción opcional de la etapa"]],
        [["createEtapa(Etapas)", "Etapas", "Crea nueva etapa en el catálogo"],
         ["getEtapaById(idEtapa)", "Etapas", "Busca por ID"],
         ["updateEtapa(Etapas)", "Etapas", "Actualiza datos de la etapa"],
         ["getAllEtapa()", "List&lt;Etapas&gt;", "Lista todas las etapas ordenadas"],
         ["deleteEtapa(idEtapa)", "void", "Elimina etapa del catálogo"]],
        relaciones=[
            "Etapas referenciada desde OrdenReparacion.etapaActual",
            "Etapas referenciada desde HistorialEtapas.etapa",
            "Etapas referenciada desde OrdenEtapaFecha.etapa",
            "Etapas referenciada desde Observacion.etapa (nullable)"],
        cambios=("Cambios respecto a la versión anterior", [
            "La clase se llama <b>Etapas</b> (plural), no Etapa.",
            "La columna del nombre es <b>nombre_etapa</b>, con length 255 &rarr; <b>100</b>.",
            "<b>orden</b> es Integer (wrapper), no int primitivo."]))

    fl.append(PageBreak())

    fl += clase(
        "Usuario",
        "Cuenta de acceso al sistema. Vinculada opcionalmente a un Técnico si ese "
        "técnico también opera la plataforma.",
        [["idUsuario", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["nombre", "String", "VARCHAR", "255 *", "NOT NULL",
          "Nombre del usuario del sistema. Sin length explícito"],
         ["email", "String", "VARCHAR", "255 *", "NOT NULL, UNIQUE",
          "Correo para login. Sin length explícito"],
         ["rol", "RolUsuario", "VARCHAR", "20", "NOT NULL",
          "ADMIN | ASESOR | GERENTE | TECNICO"],
         ["tecnico<br/>(id_tecnico)", "Long", "BIGINT", "20", "FK [0..1]",
          "Si el usuario es también técnico del taller"],
         ["passwordHash", "String", "VARCHAR", "255 *", "NOT NULL",
          "Contraseña encriptada (bcrypt). Sin length explícito"],
         ["activo", "boolean", "BOOLEAN", "1", "NOT NULL",
          "false = usuario desactivado. Default true"]],
        [["createUsuario(Usuario)", "Usuario", "Crea cuenta de usuario"],
         ["getUsuarioById(idUsuario)", "Usuario", "Busca por ID"],
         ["updateUsuario(Usuario)", "Usuario", "Actualiza datos"],
         ["getAllUsuario()", "List&lt;Usuario&gt;", "Lista todos los usuarios"],
         ["deleteUsuarioById(id)", "void", "Elimina usuario"],
         ["getModulosPermitidos(rol)", "List&lt;Modulo&gt;",
          "Módulos a los que tiene acceso según su rol"]],
        relaciones=[
            "Usuario usa &rarr; RolUsuario (enum)",
            "Usuario 1 &rarr; 1 Tecnico (opcional, lado dueño de la FK)",
            "Usuario referenciado desde OrdenReparacion, HistorialEtapas, Observacion, "
            "Valoracion"],
        cambios=("Atención: esta clase está incompleta", [
            "<b>Usuario no tiene constructores, getters ni setters.</b> Se le quitó "
            "Lombok y no se repusieron a mano como en el resto del modelo. Compila "
            "hoy sólo porque ninguna clase la consume todavía.",
            "(*) <b>nombre</b>, <b>email</b> y <b>passwordHash</b> no declaran length, "
            "así que quedan en el default 255 de JPA. Rompe la convención del proyecto "
            "de siempre declararlo.",
            "Ojo con <b>passwordHash</b>: un hash BCrypt son 60 caracteres y Argon2 "
            "puede pasar de 95, así que 255 sobra — pero conviene fijarlo explícito."]))

    fl.append(PageBreak())
    return fl


# ---------------------------------------------------------- control acceso ---
def control_de_acceso():
    fl = seccion("Control de acceso")

    fl += clase(
        "Modulo",
        "Catálogo de pantallas/secciones del sistema. Agregar una restricción de acceso "
        "nueva es sólo una fila aquí, sin redespliegue.",
        [["idModulo", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["codigo", "String", "VARCHAR", "10", "NOT NULL, UNIQUE",
          "Clave programática: BACKLOG, VALORACION, DETALLE_OT…"],
         ["nombre", "String", "VARCHAR", "100", "NOT NULL",
          "Nombre visible en la interfaz"]],
        [["createModulo(Modulo)", "Modulo", "Crea módulo"],
         ["getModuloById(idModulo)", "Modulo", "Busca por ID"],
         ["updateModulo(Modulo)", "Modulo", "Actualiza datos"],
         ["getAllModulo()", "List&lt;Modulo&gt;", "Lista todos los módulos"],
         ["deleteModuloById(idModulo)", "void", "Elimina módulo"]],
        relaciones=["Modulo 1 &rarr; N RolModulo &nbsp;(mapeada: Modulo.rolesModulo)"],
        cambios=("Cambios respecto a la versión anterior", [
            "Longitudes: codigo 255 &rarr; <b>10</b>, nombre 255 &rarr; <b>100</b>.",
            "Con 10 caracteres, códigos como <b>DETALLE_OT</b> caben justo. Un código "
            "más descriptivo (ej: VALORACION_DETALLE) ya no entra."]))

    fl += clase(
        "RolModulo",
        "Asignación de permisos: qué rol puede acceder a qué módulo. Spring Security "
        "consulta esta tabla (cacheada) vía JWT para autorizar cada request.",
        [["idRolModulo", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["rol", "RolUsuario", "VARCHAR", "20", "NOT NULL",
          "Rol al que se asigna el permiso"],
         ["modulo<br/>(id_modulo)", "Long", "BIGINT", "20", "FK NOT NULL",
          "Módulo al que se otorga acceso"]],
        [["createRolModulo(RolModulo)", "RolModulo", "Asigna acceso"],
         ["getModuloByRol(rol)", "List&lt;Modulo&gt;", "Módulos permitidos para el rol"],
         ["deleteRolModulo(idRolModulo)", "void", "Revoca acceso"]],
        relaciones=["RolModulo N &rarr; 1 Modulo",
                    "RolModulo usa &rarr; RolUsuario (enum)"])

    return fl


# --------------------------------------------------------- nucleo operativo --
def nucleo_operativo():
    fl = seccion("Núcleo operativo")

    fl += clase(
        "OrdenReparacion",
        "Tabla central del sistema. Toda la trazabilidad parte de aquí. Contiene el "
        "puntero rápido a la etapa actual y todas las fechas clave del ciclo de vida.",
        [["idOrdenReparacion", "Long", "BIGINT", "20", "PK", "Consecutivo autoincremental"],
         ["vehiculo<br/>(id_vehiculo)", "Long", "BIGINT", "20", "FK NOT NULL",
          "Vehículo en reparación"],
         ["cliente<br/>(id_cliente)", "Long", "BIGINT", "20", "FK NOT NULL",
          "Propietario del vehículo"],
         ["usuarioCreador<br/>(id_usuario_creador)", "Long", "BIGINT", "20",
          "FK NOT NULL", "Usuario que abrió la orden"],
         ["tecnicoResponsable<br/>(id_tecnico_responsable)", "Long", "BIGINT", "20",
          "FK [0..1]", "Técnico responsable general de la OT"],
         ["etapaActual<br/>(id_etapa_actual)", "Long", "BIGINT", "20", "FK NOT NULL",
          "Puntero rápido a la etapa actual (backlog)"],
         ["estado", "Estado", "VARCHAR", "20", "NOT NULL",
          "ACTIVA | CERRADA | CANCELADA. Default ACTIVA"],
         ["fechaIngresoCotizar", "LocalDate", "DATE", "—", "NOT NULL",
          "Fecha en que entra a valoración"],
         ["fechaIngresoReparacion", "LocalDate", "DATE", "—", "[0..1]",
          "Fecha de inicio de reparación"],
         ["fechaDeEntregaEstimada", "LocalDate", "DATE", "—", "[0..1]",
          "Fecha estimada de entrega al cliente"],
         ["fechaDeEntregaReal", "LocalDate", "DATE", "—", "[0..1]", "Fecha real de entrega"],
         ["diasEstimadoEntrega", "Byte", "TINYINT", "3", "[0..1]",
          "Días estimados para completar. Máximo 127"],
         ["createAt", "LocalDateTime", "DATETIME", "—", "NOT NULL", "La asigna el Service"],
         ["updateAt", "LocalDateTime", "DATETIME", "—", "NOT NULL", "La asigna el Service"]],
        [["createOrdenReparacion(OrdenReparacion)", "OrdenReparacion", "Abre nueva orden"],
         ["getOrdenReparacionById(id)", "OrdenReparacion", "Busca por ID"],
         ["getOrdenReparacionByPlaca(placa)", "OrdenReparacion",
          "Busca OT activa por placa — usado en la búsqueda del sistema"],
         ["updateOrdenReparacion(OrdenReparacion)", "OrdenReparacion",
          "Actualiza datos generales"],
         ["getAllOrdenReparacion()", "List&lt;OrdenReparacion&gt;",
          "Lista todas las órdenes"],
         ["deleteOrdenReparacionById(id)", "void", "Elimina orden"],
         ["cambiarEtapa(idOrden, idEtapa, idUsuario)", "void",
          "<b>En el Service, @Transactional.</b> Actualiza etapaActual, inserta en "
          "HistorialEtapas y cierra/abre fechas en OrdenEtapaFecha"],
         ["asignarTecnico(idOrden, idTecnico)", "void",
          "<b>En el Service.</b> Asigna técnico responsable general"],
         ["calcularDiasEnTaller(idOrden)", "int",
          "<b>En el Service.</b> Días transcurridos desde fechaIngresoCotizar"]],
        reglas=[
            "El cambio de etapa siempre va en una sola transacción: actualiza "
            "etapaActual + inserta HistorialEtapas + cierra fechaFin de la etapa "
            "anterior + abre fechaInicio de la etapa nueva.",
            "fechaFin de una etapa = fechaInicio de la etapa siguiente. NUNCA se marca "
            "manualmente desde la UI.",
            "diasEstimadoEntrega es Byte: si una reparación puede pasar de 127 días, "
            "hay que subir el tipo a Short."],
        relaciones=[
            "OrdenReparacion N &rarr; 1 Vehiculo",
            "OrdenReparacion N &rarr; 1 Cliente",
            "OrdenReparacion N &rarr; 1 Usuario (usuarioCreador)",
            "OrdenReparacion N &rarr; 1 Tecnico (tecnicoResponsable, nullable)",
            "OrdenReparacion N &rarr; 1 Etapas (etapaActual)",
            "OrdenReparacion 1 &rarr; N HistorialEtapas &nbsp;(mapeada)",
            "OrdenReparacion 1 &rarr; N OrdenEtapaFecha &nbsp;(mapeada)",
            "OrdenReparacion 1 &rarr; 1 Valoracion (UNIQUE) &nbsp;(mapeada)",
            "OrdenReparacion 1 &rarr; N Observacion &nbsp;(sólo el lado dueño)",
            "OrdenReparacion 1 &rarr; N Notificaciones &nbsp;(sólo el lado dueño)"],
        cambios=("Cambios respecto a la versión anterior", [
            "<b>Los tres métodos de negocio salieron de la entidad.</b> cambiarEtapa(), "
            "asignarTecnico() y calcularDiasEnTaller() se reescriben en el Service; la "
            "entidad quedó sólo con campos y accesores.",
            "Las FK se mapean como objetos (Vehiculo, Cliente, Usuario, Tecnico, Etapas) "
            "con @JoinColumn, no como Long. La columna SQL va entre paréntesis."]))

    fl.append(PageBreak())

    fl += clase(
        "HistorialEtapas",
        "Log de auditoría inmutable. Cada arrastre en el backlog inserta aquí. Es el "
        "gatillo que dispara las notificaciones automáticas al cliente. Sólo se insertan "
        "registros, nunca se actualizan.",
        [["idHistorialEtapas", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["ordenReparacion<br/>(id_orden_reparacion)", "Long", "BIGINT", "20",
          "FK NOT NULL", "Orden a la que pertenece"],
         ["etapa<br/>(id_etapa)", "Long", "BIGINT", "20", "FK NOT NULL", "A qué etapa se movió"],
         ["usuario<br/>(id_usuario)", "Long", "BIGINT", "20", "FK NOT NULL",
          "Quién movió la tarjeta"],
         ["fechaCambio", "LocalDateTime", "DATETIME", "—", "NOT NULL",
          "Momento exacto del cambio — necesita hora, no sólo fecha"]],
        [["createHistorialEtapas(HistorialEtapas)", "HistorialEtapas",
          "Inserta registro (sólo insert, nunca update)"],
         ["getHistorialEtapasById(id)", "HistorialEtapas", "Busca por ID"],
         ["getAllByOrden(idOrdenReparacion)", "List&lt;HistorialEtapas&gt;",
          "Historial completo de movimientos de una orden"]],
        relaciones=["HistorialEtapas N &rarr; 1 OrdenReparacion",
                    "HistorialEtapas N &rarr; 1 Etapas",
                    "HistorialEtapas N &rarr; 1 Usuario"],
        cambios=("Cambios respecto a la versión anterior", [
            "<b>El campo comentario no existe en la entidad.</b> La versión anterior de "
            "esta guía lo listaba como VARCHAR(255) [0..1], pero nunca se implementó: "
            "la observación al mover la tarjeta se guarda en la tabla "
            "<b>Observacion</b>, que es la que usa el modal del backlog."]))

    fl += clase(
        "OrdenEtapaFecha",
        "Timeline por etapa. Una fila por etapa por orden (UNIQUE). Alimenta la vista de "
        "&laquo;Proceso de reparación&raquo;. fechaFin es AUTOMÁTICA: la marca el sistema "
        "al pasar a la siguiente etapa, nunca manualmente.",
        [["idOrdenEtapaFecha", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["ordenReparacion<br/>(id_orden_reparacion)", "Long", "BIGINT", "20",
          "FK NOT NULL", "Orden a la que pertenece"],
         ["etapa<br/>(id_etapa)", "Long", "BIGINT", "20", "FK NOT NULL", "Etapa del timeline"],
         ["tecnico<br/>(id_tecnico)", "Long", "BIGINT", "20", "FK [0..1]",
          "Técnico que trabajó ESTA etapa puntual"],
         ["fechaInicio", "LocalDateTime", "DATETIME", "—", "[0..1]",
          "Se marca al entrar a la etapa (o al dejar observación sin fecha)"],
         ["fechaFin", "LocalDateTime", "DATETIME", "—", "[0..1]",
          "Se marca AUTOMÁTICAMENTE al pasar a la siguiente etapa"],
         ["completada", "Boolean", "BOOLEAN", "1", "NOT NULL",
          "true = etapa finalizada. Default false"]],
        [["createOrdenEtapaFecha(OrdenEtapaFecha)", "OrdenEtapaFecha",
          "Crea fila de seguimiento"],
         ["updateOrdenEtapaFecha(OrdenEtapaFecha)", "OrdenEtapaFecha",
          "Actualiza fechas o técnico"],
         ["getOrdenEtapaFechaById(id)", "OrdenEtapaFecha", "Busca por ID"],
         ["getAllByOrden(idOrdenReparacion)", "List&lt;OrdenEtapaFecha&gt;",
          "Timeline completo de la orden"],
         ["marcarFechaInicio(idOrden, idEtapa)", "void", "Marca fechaInicio = NOW()"],
         ["marcarFechaCompletada(idOrden, idEtapa)", "void", "Marca completada = true"],
         ["asignarTecnicoEtapa(id, idTecnico)", "void",
          "Asigna técnico a esta etapa puntual"]],
        reglas=[
            "UNIQUE(id_orden_reparacion, id_etapa) — una sola fila por etapa por orden.",
            "fechaFin NUNCA se marca desde la UI. La marca el Service al cerrar la "
            "etapa anterior.",
            "El técnico se asigna aquí (no en OrdenReparacion): es la base del futuro "
            "sistema de pagos por trabajo realizado."],
        relaciones=["OrdenEtapaFecha N &rarr; 1 OrdenReparacion",
                    "OrdenEtapaFecha N &rarr; 1 Etapas",
                    "OrdenEtapaFecha N &rarr; 1 Tecnico (nullable)"])

    fl.append(PageBreak())
    return fl


# ------------------------------------------------------------- valoracion ----
def valoracion():
    fl = seccion("Valoración")

    fl += clase(
        "Valoracion",
        "Hoja de valoración de daños. Una sola por orden (UNIQUE). Se edita, nunca se "
        "duplica. cargadaCesvi registra si ya fue subida a CESVI Colombia "
        "(plataforma externa).",
        [["idValoracion", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["ordenReparacion<br/>(id_orden_reparacion)", "Long", "BIGINT", "20",
          "FK NOT NULL, UNIQUE", "Sólo una valoración por orden"],
         ["usuario<br/>(id_usuario)", "Long", "BIGINT", "20", "FK NOT NULL",
          "Quién elaboró la valoración"],
         ["descripcionGeneral", "String", "VARCHAR", "500", "[0..1]",
          "Descripción general del siniestro"],
         ["costoEstimado", "BigDecimal", "DECIMAL", "12,2", "[0..1]", "Costo estimado total"],
         ["cargadaCesvi", "boolean", "BOOLEAN", "1", "NOT NULL",
          "true = ya subida a CESVI Colombia. Default false"],
         ["cargadaCesviAt", "LocalDateTime", "DATETIME", "—", "[0..1]",
          "Momento en que se marcó como cargada en CESVI"],
         ["createAt", "LocalDateTime", "DATETIME", "—", "NOT NULL", "La asigna el Service"],
         ["updateAt", "LocalDateTime", "DATETIME", "—", "NOT NULL", "La asigna el Service"]],
        [["createValoracion(Valoracion)", "Valoracion",
          "Crea valoración (sólo si no existe para esa orden)"],
         ["updateValoracion(Valoracion)", "Valoracion", "Edita valoración existente"],
         ["getValoracionByOrden(idOrden)", "Valoracion",
          "Busca la valoración de una orden — retorna null si no existe"],
         ["cargarValoracion(idValoracion)", "void",
          "Marca la valoración como lista (disponible en la ficha de la orden)"],
         ["marcarCargadaCesvi(idValoracion)", "void",
          "<b>En el Service.</b> El usuario confirma que ya subió las fotos/hoja a CESVI"],
         ["generarHojaPdf(idValoracion)", "byte[]",
          "Genera PDF al vuelo — no se guarda en disco"]],
        reglas=[
            "UNIQUE(id_orden_reparacion) — un solo registro por orden. El endpoint usa "
            "UPSERT.",
            "El PDF y el ZIP de fotos se generan al vuelo con cada descarga, nunca se "
            "guardan como archivos fijos.",
            "cargadaCesvi se cambia manualmente desde el botón en la lista de "
            "valoraciones, no automáticamente.",
            "<b>Sin cascada:</b> al borrar una valoración, el Service debe borrar "
            "primero sus ValoracionDetalle y ValoracionImagen por su propio repositorio."],
        relaciones=[
            "Valoracion 1 &rarr; 1 OrdenReparacion (UNIQUE, lado dueño de la FK)",
            "Valoracion 1 &rarr; N ValoracionDetalle &nbsp;(mapeada, sin cascada)",
            "Valoracion 1 &rarr; N ValoracionImagen &nbsp;(mapeada, sin cascada)",
            "Valoracion N &rarr; 1 Usuario"],
        cambios=("Cambios respecto a la versión anterior", [
            "descripcionGeneral 255 &rarr; <b>500</b>.",
            "<b>Se quitó CascadeType.ALL y orphanRemoval</b> de detalles e imagenes. "
            "Quitar un hijo de la lista en memoria ya no lo borra de la base.",
            "<b>marcarCargadaCesvi() salió de la entidad</b> y pasa al Service."]))

    fl += clase(
        "ValoracionDetalle",
        "Cada pieza evaluada en la valoración. gravedad es nullable cuando "
        "accion = SUSTITUCION.",
        [["idValoracionDetalle", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["valoracion<br/>(id_valoracion)", "Long", "BIGINT", "20", "FK NOT NULL",
          "Valoración a la que pertenece"],
         ["pieza", "String", "VARCHAR", "255", "NOT NULL",
          "Ej: Puerta delantera derecha"],
         ["accion", "Accion", "VARCHAR", "20", "NOT NULL", "REPARACION | SUSTITUCION"],
         ["gravedad", "Gravedad", "VARCHAR", "20", "[0..1]",
          "LEVE | MEDIO | FUERTE — sólo si accion = REPARACION"],
         ["observacion", "String", "VARCHAR", "255", "[0..1]", "Nota específica por pieza"],
         ["costo", "BigDecimal", "DECIMAL", "12,2", "[0..1]",
          "Costo individual de esa pieza"]],
        [["createValoracionDetalle(ValoracionDetalle)", "ValoracionDetalle",
          "Agrega pieza a la valoración"],
         ["getAllByIdValoracion(idValoracion)", "List&lt;ValoracionDetalle&gt;",
          "Lista piezas de una valoración"],
         ["updateValoracionDetalle(ValoracionDetalle)", "ValoracionDetalle",
          "Actualiza la pieza"],
         ["deleteValoracionDetalleById(id)", "void", "Elimina la pieza"]],
        reglas=[
            "Regla: (accion = REPARACION AND gravedad IS NOT NULL) OR "
            "(accion = SUSTITUCION AND gravedad IS NULL).",
            "<b>Esa regla ya no la impone la base de datos</b>: la tiene que validar el "
            "Service antes de guardar.",
            "En el frontend: el selector de gravedad se oculta cuando la acción es "
            "SUSTITUCION."],
        relaciones=["ValoracionDetalle N &rarr; 1 Valoracion (sin cascade delete)"],
        cambios=("Cambios respecto a la versión anterior", [
            "<b>Se eliminó el CHECK ck_detalle_gravedad</b> del @Table. El DDL ya no "
            "impide guardar una SUSTITUCION con gravedad, ni una REPARACION sin ella.",
            "El cascade delete desde Valoracion también se quitó."]))

    fl += clase(
        "ValoracionImagen",
        "Fotos de soporte de la valoración. Las URLs deben ser públicas (https) para que "
        "la API de WhatsApp y CESVI puedan descargarlas.",
        [["idValoracionImagen", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["valoracion<br/>(id_valoracion)", "Long", "BIGINT", "20", "FK NOT NULL",
          "Valoración a la que pertenece"],
         ["url", "String", "VARCHAR", "2083", "NOT NULL", "URL pública https del archivo"],
         ["descripcion", "String", "VARCHAR", "300", "[0..1]",
          "Descripción opcional de la foto"],
         ["createAt", "LocalDateTime", "DATETIME", "—", "NOT NULL", "La asigna el Service"]],
        [["createValoracionImagen(ValoracionImagen)", "ValoracionImagen", "Registra la foto"],
         ["getAllByIdValoracion(idValoracion)", "List&lt;ValoracionImagen&gt;",
          "Lista las fotos de una valoración"],
         ["deleteByIdValoracionImagen(id)", "void", "Elimina la foto"],
         ["descargarTodasZip(idValoracion)", "byte[]",
          "Genera ZIP al vuelo con todas las fotos — para subir a CESVI"]],
        relaciones=["ValoracionImagen N &rarr; 1 Valoracion (sin cascade delete)"],
        cambios=("Cambios respecto a la versión anterior", [
            "url 255 &rarr; <b>2083</b> (el límite práctico de URL de los navegadores) "
            "y descripcion 255 &rarr; <b>300</b>.",
            "Con utf8mb4, MySQL <b>no puede indexar</b> una columna de 2083 completa: "
            "no se le puede poner UNIQUE sin usar prefijo de índice.",
            "El cascade delete desde Valoracion se quitó."]))

    fl.append(PageBreak())
    return fl


# ----------------------------------------------------------- observaciones ---
def observaciones():
    fl = seccion("Observaciones")

    fl += clase(
        "Observacion",
        "Observaciones estructuradas (inspirado en Orbika). Se ingresan desde dos "
        "puntos: el modal inline del backlog (al mover la tarjeta) o la ficha de "
        "detalle. Ambas rutas escriben en esta misma tabla.",
        [["idObservacion", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["ordenReparacion<br/>(id_orden_reparacion)", "Long", "BIGINT", "20",
          "FK NOT NULL", "Orden a la que pertenece"],
         ["usuario<br/>(id_usuario)", "Long", "BIGINT", "20", "FK NOT NULL",
          "Quién dejó la observación"],
         ["etapa<br/>(id_etapa)", "Long", "BIGINT", "20", "FK [0..1]",
          "Etapa al momento de la nota"],
         ["ubicacion", "Ubicacion", "VARCHAR", "20", "NOT NULL",
          "EN_TALLER | FUERA_DE_TALLER"],
         ["estadoRepuestos", "Repuestos", "VARCHAR", "20", "[0..1]",
          "COMPLETOS | PENDIENTES"],
         ["nota", "String", "VARCHAR", "500", "NOT NULL", "Texto de la observación"],
         ["visibleCliente", "boolean", "BOOLEAN", "1", "NOT NULL",
          "true = se notifica al cliente por WhatsApp. Default true"],
         ["createAt", "LocalDateTime", "DATETIME", "—", "NOT NULL", "La asigna el Service"]],
        [["createObservacion(Observacion)", "Observacion",
          "Crea observación. Si la etapa no tiene fechaInicio, la marca automáticamente "
          "(misma transacción)"],
         ["updateObservacion(Observacion)", "Observacion", "Actualiza la observación"],
         ["getObservacionById(id)", "Observacion", "Busca por ID"],
         ["getAllObservacion(idOrdenReparacion)", "List&lt;Observacion&gt;",
          "Historial de observaciones de la orden"],
         ["deleteObservacionById(id)", "void", "Elimina la observación"]],
        reglas=[
            "Si visibleCliente = true, el servicio de notificaciones envía el mensaje + "
            "las fotos adjuntas por WhatsApp.",
            "Si la etapa referenciada tiene fechaInicio = NULL al crear la observación, "
            "el Service la marca con NOW() en la misma transacción.",
            "ubicacion y estadoRepuestos viven aquí, no en OrdenReparacion: son el "
            "estado <i>al momento de dejar la nota</i>."],
        relaciones=[
            "Observacion N &rarr; 1 OrdenReparacion",
            "Observacion N &rarr; 1 Usuario",
            "Observacion N &rarr; 1 Etapas (nullable)",
            "Observacion 1 &rarr; N ImagenObservacion &nbsp;(mapeada, CON cascada)",
            "Observacion 1 &rarr; N Notificaciones (como origen)"],
        cambios=("Cambios respecto a la versión anterior", [
            "nota 255 &rarr; <b>500</b>.",
            "Es la <b>única relación del modelo que conserva</b> "
            "cascade = ALL + orphanRemoval (hacia sus imágenes)."]))

    fl += clase(
        "ImagenObservacion",
        "Fotos adjuntas a una observación. Se envían al cliente junto con el texto por "
        "WhatsApp. La URL debe ser pública (https).",
        [["idImagenesObservacion", "Long", "BIGINT", "20", "PK",
          "Identificador único. El nombre va en plural, según la guía original"],
         ["observacion<br/>(id_observacion)", "Long", "BIGINT", "20", "FK NOT NULL",
          "Observación a la que pertenece"],
         ["url", "String", "VARCHAR", "255", "NOT NULL", "URL pública https"],
         ["createAt", "LocalDateTime", "DATETIME", "—", "NOT NULL", "La asigna el Service"]],
        [["createImagenObservacion(ImagenObservacion)", "ImagenObservacion",
          "Registra la foto"],
         ["getAllByObservacion(idObservacion)", "List&lt;ImagenObservacion&gt;",
          "Lista las fotos de una observación"],
         ["deleteImagenById(id)", "void", "Elimina la foto"]],
        relaciones=["ImagenObservacion N &rarr; 1 Observacion (CASCADE DELETE)"],
        cambios=("Nota de consistencia", [
            "<b>url</b> aquí quedó en 255, mientras que ValoracionImagen.url subió a "
            "2083. Las dos guardan el mismo tipo de dato (URL pública https), así que "
            "conviene igualarlas."]))

    fl.append(PageBreak())
    return fl


# ---------------------------------------------------------- notificaciones ---
def notificaciones():
    fl = seccion("Notificaciones")

    fl += clase(
        "Notificaciones",
        "Log de todos los mensajes enviados por WhatsApp. Permite debug, reintentos y "
        "auditoría de qué llegó al cliente y cuándo.",
        [["idNotificacion", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["ordenReparacion<br/>(id_orden_reparacion)", "Long", "BIGINT", "20",
          "FK NOT NULL", "Orden a la que pertenece"],
         ["cliente<br/>(id_cliente)", "Long", "BIGINT", "20", "FK NOT NULL",
          "Receptor del mensaje"],
         ["observacion<br/>(id_observacion)", "Long", "BIGINT", "20", "FK [0..1]",
          "Origen del mensaje si aplica"],
         ["eventoDisparador", "String", "VARCHAR", "255 *", "NOT NULL",
          "CAMBIO_ETAPA | NUEVA_OBSERVACION. Sin length explícito"],
         ["canal", "String", "VARCHAR", "255 *", "NOT NULL",
          "WHATSAPP. Default en la entidad. Sin length explícito"],
         ["contenidoEnviado", "String", "VARCHAR", "255 *", "NOT NULL",
          "Texto exacto enviado. Sin length explícito"],
         ["imagenesEnviadas", "String (JSON)", "JSON", "—", "[0..1]",
          "Array JSON de URLs de fotos enviadas"],
         ["estadoEnvio", "EstadoEnvio", "VARCHAR", "20", "NOT NULL",
          "PENDIENTE | ENVIADO | FALLIDO. Default PENDIENTE"],
         ["fechaEnvio", "LocalDateTime", "DATETIME", "—", "[0..1]",
          "Momento del envío efectivo"],
         ["errorDetalle", "String", "VARCHAR", "255 *", "[0..1]",
          "Detalle del error si estadoEnvio = FALLIDO. Sin length explícito"],
         ["createAt", "LocalDateTime", "DATETIME", "—", "NOT NULL", "La asigna el Service"]],
        [["createNotificacion(Notificaciones)", "Notificaciones", "Registra el mensaje"],
         ["getNotificacionesById(id)", "Notificaciones", "Busca por ID"],
         ["getAllByOrden(idOrdenReparacion)", "List&lt;Notificaciones&gt;",
          "Mensajes de una orden"],
         ["reintentarEnvio(idNotificacion)", "void",
          "Reintenta el envío si estadoEnvio = FALLIDO"],
         ["marcarEnviada(idNotificacion)", "void",
          "<b>En el Service.</b> estadoEnvio = ENVIADO + fechaEnvio = NOW()"],
         ["marcarFallida(idNotificacion, error)", "void",
          "<b>En el Service.</b> estadoEnvio = FALLIDO + errorDetalle"]],
        reglas=[
            "El listener de notificaciones es @Async: un fallo en WhatsApp no rompe la "
            "operación principal ni hace rollback de la transacción.",
            "La API de WhatsApp (Twilio/Meta) necesita una URL pública https para las "
            "imágenes — no acepta archivos directos.",
            "Un mensaje con 3 fotos genera: 1 mensaje de texto + 3 mensajes de media "
            "(una foto cada uno)."],
        relaciones=["Notificaciones N &rarr; 1 OrdenReparacion",
                    "Notificaciones N &rarr; 1 Cliente",
                    "Notificaciones N &rarr; 1 Observacion (nullable)"],
        cambios=("Cambios respecto a la versión anterior", [
            "<b>marcarEnviada() y marcarFallida() salieron de la entidad</b> y pasan al "
            "Service.",
            "(*) Cuatro columnas quedaron <b>sin length explícito</b> (255 por default). "
            "<b>contenidoEnviado</b> es el caso urgente: un mensaje de WhatsApp se pasa "
            "fácil de 255 caracteres y MySQL lo truncaría. Evaluar TEXT."]))

    fl += clase(
        "PlantillaMensaje",
        "Plantillas de texto para los mensajes automáticos. Soportan variables "
        "interpoladas como {{cliente}}, {{placa}}, {{etapa}}.",
        [["idPlantillaMensaje", "Long", "BIGINT", "20", "PK", "Identificador único"],
         ["evento", "String", "VARCHAR", "255", "NOT NULL",
          "CAMBIO_ETAPA | NUEVA_OBSERVACION"],
         ["canal", "String", "VARCHAR", "255", "NOT NULL",
          "Canal de envío. Default WHATSAPP en la entidad"],
         ["contenidoTemplate", "String", "VARCHAR", "255", "NOT NULL",
          "Ej: Hola {{cliente}}, tu vehículo {{placa}} pasó a {{etapa}}."],
         ["activo", "boolean", "BOOLEAN", "1", "NOT NULL",
          "false = plantilla desactivada. Default true"]],
        [["createPlantilla(PlantillaMensaje)", "PlantillaMensaje", "Crea la plantilla"],
         ["getPlantillaByEvento(evento)", "PlantillaMensaje",
          "Usada por el listener para construir el mensaje"],
         ["updatePlantilla(PlantillaMensaje)", "PlantillaMensaje", "Edita la plantilla"],
         ["getAllPlantilla()", "List&lt;PlantillaMensaje&gt;", "Lista todas las plantillas"]],
        relaciones=["PlantillaMensaje consultada por el servicio de notificaciones "
                    "(sin FK directa)"],
        cambios=("Pendiente", [
            "<b>contenidoTemplate</b> sigue en 255, igual que en la guía original. Una "
            "plantilla con variables interpoladas se pasa de ahí con facilidad: "
            "evaluar subirla a TEXT."]))

    return fl


def construir_contenido():
    fl = []
    fl += portada()
    fl += cambios_globales()
    fl += tipos_de_datos()
    fl += enumeraciones()
    fl += catalogos()
    fl += control_de_acceso()
    fl += nucleo_operativo()
    fl += valoracion()
    fl += observaciones()
    fl += notificaciones()
    return fl
