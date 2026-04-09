# Prueba Tecnica — Servicio de Empleados (Parameta S.A.S.)

Servicio Java que expone un endpoint **REST GET** para registrar empleados. La capa REST valida los datos, invoca un **servicio SOAP** (publicado por la misma aplicacion) y este ultimo persiste el empleado en **MySQL**. La respuesta REST incluye edad y tiempo de vinculacion calculados.

## Stack

- Java 17 + Spring Boot 3.2
- Spring Web (REST) + Bean Validation
- Apache CXF JAX-WS (SOAP server + cliente)
- Spring Data JPA + MySQL
- Docker + Docker Compose
- JUnit 5 + MockMvc + H2 (tests)

## Arquitectura

```
HTTP GET ──► EmpleadoController ──► EmpleadoService ──(JAX-WS over HTTP)──► EmpleadoSoapServiceImpl ──► EmpleadoRepository ──► MySQL
                                        │                                           ▲
                                        └── valida + calcula edad/tiempo            │
                                                                                    │
                                    http://localhost:8080/services/empleados ───────┘
```

El cliente SOAP **no** llama directamente al bean: la invocacion viaja por HTTP/SOAP real (CXF `JaxWsProxyFactoryBean`), por lo que el contrato SOAP se ejercita de extremo a extremo.

## Ejecutar con Docker (recomendado)

Un solo comando levanta MySQL + la aplicacion:

```bash
docker-compose up --build
```

- REST:  `http://localhost:8080/api/empleados`
- WSDL:  `http://localhost:8080/services/empleados?wsdl`
- MySQL: `localhost:3307` (usuario: `root`, password: `root`, base: `parameta`)

Para detener:
```bash
docker-compose down
```

## Ejecutar sin Docker

Requisitos: Java 17+, Maven 3.8+, MySQL 8 corriendo en `localhost:3306`.

```bash
mvn spring-boot:run
```

La base de datos y la tabla se crean automaticamente (`createDatabaseIfNotExist=true`, `ddl-auto: update`).

## Endpoint REST

`GET /api/empleados`

| Parametro | Tipo | Validacion |
|---|---|---|
| `nombres` | String | obligatorio |
| `apellidos` | String | obligatorio |
| `tipoDocumento` | String | obligatorio |
| `numeroDocumento` | String | obligatorio |
| `fechaNacimiento` | Date `yyyy-MM-dd` | obligatorio, pasada, mayor de edad |
| `fechaVinculacion` | Date `yyyy-MM-dd` | obligatorio, no futura |
| `cargo` | String | obligatorio |
| `salario` | Double | obligatorio, > 0 |

### Ejemplo

```bash
curl "http://localhost:8080/api/empleados?\
nombres=Ronald&\
apellidos=Cipagauta&\
tipoDocumento=CC&\
numeroDocumento=1023456789&\
fechaNacimiento=1990-05-10&\
fechaVinculacion=2020-01-15&\
cargo=Backend%20Developer&\
salario=5000000"
```

### Respuesta (200 OK)

```json
{
  "nombres": "Ronald",
  "apellidos": "Cipagauta",
  "tipoDocumento": "CC",
  "numeroDocumento": "1023456789",
  "fechaNacimiento": "1990-05-10",
  "fechaVinculacion": "2020-01-15",
  "cargo": "Backend Developer",
  "salario": 5000000.0,
  "edad":              { "anios": 35, "meses": 10, "dias": 30 },
  "tiempoVinculacion": { "anios":  6, "meses":  2, "dias": 25 }
}
```

### Errores (400 Bad Request)

```json
{
  "timestamp": "2026-04-09T12:40:39",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Errores de validacion",
  "detalles": ["nombres: nombres es obligatorio"]
}
```

## Coleccion Postman

Importar el archivo `postman/Parameta_Empleados.postman_collection.json` en Postman.

Incluye 7 requests listos para ejecutar:

| Request | Resultado esperado |
|---|---|
| Caso feliz — Registrar empleado valido | 200 + edad y tiempo de vinculacion |
| Error — Menor de edad | 400 |
| Error — Campos vacios | 400 + lista de campos faltantes |
| Error — Formato de fecha invalido | 400 |
| Error — Salario negativo | 400 |
| Error — Vinculacion anterior a nacimiento | 400 |
| WSDL — Verificar contrato SOAP | 200 + XML |

## Tests

```bash
mvn test
```

4 tests cubriendo: caso feliz, menor de edad, campos vacios, formato de fecha invalido. Usan H2 en memoria y mock del cliente SOAP.

## Estructura del proyecto

```
├── Dockerfile                    # Multi-stage build (Maven + JRE)
├── docker-compose.yml            # MySQL + App
├── pom.xml
├── postman/                      # Coleccion Postman importable
│   └── Parameta_Empleados.postman_collection.json
└── src/main/java/com/parameta/empleados/
    ├── EmpleadosApplication.java
    ├── domain/                   # Empleado (DTO JAXB), EmpleadoEntity (JPA), Mapper, Repository
    ├── rest/                     # Controller, Request (validaciones), Response (record), ExceptionHandler
    ├── service/                  # Reglas de negocio y orquestacion
    └── soap/                     # SEI, Implementacion, Config (endpoint CXF + cliente JAX-WS)
```

## Decisiones de diseno

- **Una sola aplicacion** publica REST y SOAP. Mas simple sin perder el ejercicio del contrato SOAP real.
- **DTO separado de entidad JPA**: `Empleado` (JAXB) y `EmpleadoEntity` (JPA) con `EmpleadoMapper` para la conversion. Aislamiento entre contrato SOAP y esquema de BD.
- **Capa de servicio**: la logica de negocio (mayor de edad, coherencia de fechas) vive en `EmpleadoService`, no en el controller.
- **Validaciones declarativas** (`@NotBlank`, `@Past`, `@Positive`) + reglas de negocio explicitas en el servicio.
- **`@Transactional`** en la operacion de escritura SOAP.
- **`Clock` inyectado** para facilitar testing de logica temporal.
- **`Period.between`** para calcular edad y tiempo de vinculacion con la API estandar de Java.
- **Docker Compose** para que el evaluador levante todo con un solo comando.

## Autor

Ronald Cipagauta
