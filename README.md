# Prueba Técnica — Servicio de Empleados (Parameta S.A.S.)

Servicio Java que expone un endpoint **REST GET** para registrar empleados. La capa REST valida los datos, invoca un **servicio SOAP** (publicado por la misma aplicación) y este último persiste el empleado en **MySQL**. La respuesta REST incluye edad y tiempo de vinculación calculados.

## Stack

- Java 17 + Spring Boot 3.2
- Spring Web (REST) + Bean Validation
- Apache CXF JAX-WS (SOAP server + cliente)
- Spring Data JPA + MySQL
- JUnit 5 + MockMvc + H2 (tests)

## Arquitectura (alto nivel)

```
HTTP GET ──► EmpleadoController ──(JAX-WS over HTTP)──► EmpleadoSoapServiceImpl ──► EmpleadoRepository ──► MySQL
                  │                                              ▲
                  └──── valida + calcula edad/tiempo             │
                                                                 │
              http://localhost:8080/services/empleados ──────────┘
```

El cliente SOAP **no** llama directamente al bean: la invocación viaja por HTTP/SOAP real (CXF `JaxWsProxyFactoryBean`), por lo que el contrato SOAP se ejercita de extremo a extremo.

## Configuración MySQL

Por defecto en [application.yml](src/main/resources/application.yml):

```yaml
url: jdbc:mysql://localhost:3306/parameta?createDatabaseIfNotExist=true
username: root
password: root
```

La tabla `empleados` se crea automáticamente (`ddl-auto: update`).

## Ejecutar

```bash
mvn spring-boot:run
```

- REST:  `http://localhost:8080/api/empleados`
- WSDL:  `http://localhost:8080/services/empleados?wsdl`

## Endpoint REST

`GET /api/empleados`

| Parámetro | Tipo | Validación |
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
apellidos=Perez&\
tipoDocumento=CC&\
numeroDocumento=1023456789&\
fechaNacimiento=1990-05-10&\
fechaVinculacion=2020-01-15&\
cargo=Backend%20Developer&\
salario=5000000"
```

### Respuesta

```json
{
  "nombres": "Ronald",
  "apellidos": "Perez",
  "tipoDocumento": "CC",
  "numeroDocumento": "1023456789",
  "fechaNacimiento": "1990-05-10",
  "fechaVinculacion": "2020-01-15",
  "cargo": "Backend Developer",
  "salario": 5000000.0,
  "edad":              { "anios": 35, "meses": 10, "dias": 29 },
  "tiempoVinculacion": { "anios":  6, "meses":  2, "dias": 24 }
}
```

### Errores (400)

```json
{
  "timestamp": "2026-04-08T10:15:30",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Errores de validación",
  "detalles": ["nombres: nombres es obligatorio"]
}
```

## Tests

```bash
mvn test
```

Cubren: caso feliz, menor de edad, campos vacíos, formato de fecha inválido. Usan H2 en memoria y un stub del cliente SOAP.

## Estructura

```
src/main/java/com/parameta/empleados
├── EmpleadosApplication.java
├── domain/        # Empleado (JPA + JAXB), Repository, LocalDateAdapter
├── rest/          # Controller, Request, Response, ExceptionHandler
└── soap/          # SEI, Impl, SoapConfig (endpoint + cliente CXF)
```

## Decisiones de diseño

- **Una sola aplicación** publica REST y SOAP. Más simple para la prueba sin perder el ejercicio del contrato SOAP real.
- **`Empleado` reutilizado** como entidad JPA y objeto JAXB para evitar mapeos triviales. En un sistema mayor se separarían.
- **Validaciones declarativas** (`@NotBlank`, `@Past`, etc.) más una regla de negocio explícita (mayor de edad) en el controller, donde es claramente visible.
- **`Period.between`** para edad y tiempo de vinculación: API estándar, sin librerías externas.

## Autor

Ronald
