# 🏟️ Teams Microservice — Servicio-TeamReadbull

Microservicio encargado de la gestión de equipos dentro de la plataforma **TechCup Fútbol**. Permite crear equipos, administrar jugadores, manejar solicitudes de ingreso y sincronizar el estado del equipo con los torneos activos.

---

## 🛠️ Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.x |
| PostgreSQL | — |
| MapStruct | — |
| Lombok | — |
| JaCoCo | 0.8.12 |
| JUnit 5 + Mockito | — |

---

## ⚙️ Variables de entorno

Antes de correr el servicio, define las siguientes variables de entorno:

| Variable | Descripción |
|---|---|
| `DB_URL` | URL de conexión a PostgreSQL (`jdbc:postgresql://host:5432/db`) |
| `DB_USERNAME` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `JWT_SECRET` | Llave secreta para validación de tokens JWT |
| `JWT_EXPIRATION` | Tiempo de expiración del JWT en milisegundos |
| `TOURNAMENT_SERVICE_URL` | URL base del microservicio de torneos |

El servicio corre por defecto en el puerto **8082**.

---

## 🚀 Correr el proyecto

```bash
# Compilar y correr tests
mvn verify -Dspring.profiles.active=test

# Correr la aplicación
mvn spring-boot:run
```

---

## 🧪 Cobertura de tests (JaCoCo)

El proyecto tiene configurado JaCoCo con los siguientes umbrales mínimos:

| Métrica | Mínimo requerido |
|---|---|
| Líneas cubiertas | **80%** |
| Ramas cubiertas | **75%** |

Para ver el reporte visual de cobertura tras ejecutar `mvn verify`:

```
target/site/jacoco/index.html
```

Para SonarQube, el reporte XML se genera automáticamente en:

```
target/site/jacoco/jacoco.xml
```

---

## 📦 Estructura del proyecto

```
src/
├── main/java/com/microservice/Servicio_TeamReadbull/
│   ├── config/             # Configuración de seguridad y beans
│   ├── controllers/        # TeamController — endpoints REST
│   ├── dto/
│   │   ├── Request/        # TeamRequestDTO
│   │   └── Response/       # TeamResponseDTO
│   ├── exception/          # ResourceNotFoundException, UnauthorizedException
│   ├── mappers/            # TeamMapper (MapStruct)
│   ├── model/              # Entidad Team + Notification pattern
│   ├── repository/         # TeamRepository (JPA)
│   └── service/            # TeamService — lógica de negocio
└── test/
    └── java/com/microservice/Servicio_TeamReadbull/
        ├── controllers/    # TeamControllerTest
        ├── exception/      # ExceptionTest
        ├── mappers/        # TeamMapperTest
        ├── model/          # TeamModelTest
        └── service/        # TeamServiceFullTest
```

---

## 📡 API — Endpoints

Base URL: `/api/teams`

### Equipos

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| `POST` | `/api/teams` | Crear un equipo nuevo | `201 Created` |
| `GET` | `/api/teams` | Listar todos los equipos | `200 OK` |
| `GET` | `/api/teams/{id}` | Obtener equipo por ID | `200 OK` |
| `PUT` | `/api/teams/{id}/tournament-status` | Actualizar estado del torneo del equipo | `200 OK` |
| `DELETE` | `/api/teams/{id}` | Eliminar equipo | `204 No Content` |

### Jugadores

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| `POST` | `/api/teams/{teamId}/players/{playerId}` | Agregar jugador al equipo | `200 OK` |
| `DELETE` | `/api/teams/{teamId}/players/{playerId}` | Eliminar jugador del equipo | `200 OK` |

### Solicitudes de ingreso

| Método | Ruta | Descripción | Header requerido | Respuesta |
|---|---|---|---|---|
| `POST` | `/api/teams/{teamId}/solicitudes` | Jugador envía solicitud de ingreso | `X-User-Id` | `204 No Content` |
| `POST` | `/api/teams/join` | Unirse a equipo por código | `X-User-Id` | `204 No Content` |
| `GET` | `/api/teams/{teamId}/solicitudes` | Ver solicitudes pendientes (solo capitán) | `X-User-Id` | `200 OK` |
| `POST` | `/api/teams/{teamId}/solicitudes/{playerId}/accept` | Aceptar solicitud (solo capitán) | `X-User-Id` | `204 No Content` |
| `POST` | `/api/teams/{teamId}/solicitudes/{playerId}/reject` | Rechazar solicitud (solo capitán) | `X-User-Id` | `204 No Content` |

---

## 🏗️ Modelo — `Team`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | Identificador autogenerado |
| `name` | `String` | Nombre único del equipo |
| `idCaptain` | `Long` | ID del jugador capitán (no modificable) |
| `idTournament` | `Long` | ID del torneo asociado (nullable) |
| `tournamentStatus` | `enum` | `NONE` / `DRAFT` / `ACTIVE` / `IN_PROGRESS` / `FINISHED` |
| `players` | `List<Long>` | IDs de jugadores en el equipo |
| `currentPlayers` | `int` | Cantidad actual de jugadores |
| `code` | `String` | Código único de 8 caracteres para unirse (autogenerado) |
| `colors` | `String` | Colores del equipo |
| `photo` | `String` | URL o nombre de la foto del equipo |
| `requests` | `List<Long>` | IDs de jugadores con solicitud pendiente |

**Reglas de negocio:**
- Un equipo requiere entre **7 y 12 jugadores** para ser válido.
- No se puede modificar ni eliminar jugadores mientras el equipo esté en un torneo `ACTIVE` o `IN_PROGRESS`.
- El capitán **no puede ser eliminado** del equipo.
- Un jugador no puede pertenecer a más de un equipo simultáneamente.
- El código de equipo se genera automáticamente al crear el equipo y no es modificable.

---

## 🔗 Servicios relacionados

| Servicio | Puerto por defecto |
|---|---|
| Player Service | `8081` |
| Teams Service (este) | `8082` |
| Tournament Service | Variable de entorno |
