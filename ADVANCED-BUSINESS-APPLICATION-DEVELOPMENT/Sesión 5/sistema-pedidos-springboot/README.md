# Sistema de Pedidos - Solución PA3

Proyecto académico para **Desarrollo de Aplicaciones Empresariales Avanzado - PA3 2026-20**.
La solución evoluciona el proyecto base sin reemplazar el CRUD JPA ni la integración HTTP con `inventario-service`.

## 1. Objetivo PA3

Implementar un módulo protegido de consulta y supervisión de pedidos que combine:

- Spring MVC + JSP.
- API REST/JSON.
- Spring Security con roles `OPERADOR` y `CONSULTA`.
- Spring JDBC para las nuevas consultas de reportes.
- MySQL en ejecución normal y H2 en pruebas.
- JUnit 5, Mockito, MockMvc y Spring Security Test.

## 2. Arquitectura

```text
Navegador / Postman
        |
        v
Spring Security Filter Chain
- autenticación
- autorización
- CSRF
        |
        v
DispatcherServlet
        |
        +-----------------------------+
        |                             |
        v                             v
PedidoReporteWebController     PedidoReporteController
@Controller                    @RestController
JSP + Model                    JSON
        |                             |
        +--------------+--------------+
                       v
              PedidoReporteService
                       |
                       v
        PedidoReporteJdbcRepository
        NamedParameterJdbcTemplate
                       |
                       v
                     MySQL

Flujo transaccional existente:
PedidoController / PedidoApiController
        |
        v
PedidoService -> PedidoRepository (Spring Data JPA) -> MySQL
        |
        +-> InventarioRestClient -> inventario-service (Jakarta EE)
```

La coexistencia es intencional: **JPA** se mantiene para la operación transaccional existente y **Spring JDBC** se usa para las consultas explícitas de PA3.

## 3. Funcionalidades nuevas PA3

### 3.1 Búsqueda avanzada

Ruta REST:

```http
GET /sistema-pedidos/api/v1/pedidos/busqueda-avanzada?cliente=Ana&totalMinimo=100
```

SQL ejecutado mediante `NamedParameterJdbcTemplate`:

```sql
SELECT ...
FROM pedido
WHERE LOWER(cliente) LIKE LOWER(:cliente)
  AND total >= :totalMinimo
ORDER BY id DESC;
```

Reglas de Service:

- `cliente` no puede ser nulo ni vacío.
- `totalMinimo` es obligatorio.
- `totalMinimo >= 0`.

### 3.2 Clientes frecuentes

Ruta REST:

```http
GET /sistema-pedidos/api/v1/pedidos/clientes-frecuentes?minPedidos=2
```

Consulta:

```sql
SELECT
    cliente,
    COUNT(*) AS total_pedidos,
    SUM(cantidad) AS total_unidades,
    SUM(total) AS importe_acumulado,
    AVG(total) AS ticket_promedio
FROM pedido
GROUP BY cliente
HAVING COUNT(*) >= :minPedidos;
```

Retorna por cliente:

- `totalPedidos`
- `totalUnidades`
- `importeAcumulado`
- `ticketPromedio`

Regla de Service: `minPedidos >= 1`.

### 3.3 Vista MVC/JSP

```http
GET /sistema-pedidos/reportes/pedidos
```

La vista `reportes-pedidos.jsp` muestra:

- resumen general existente;
- formulario y resultados de búsqueda avanzada;
- formulario y consolidado de clientes frecuentes;
- enlaces de navegación al módulo de pedidos.

No contiene SQL ni reglas de negocio. El controlador MVC y el controlador REST reutilizan `PedidoReporteService`.

## 4. Seguridad

| Recurso | OPERADOR | CONSULTA | Anónimo |
|---|---:|---:|---:|
| `GET /pedidos` | Sí | Sí | Autenticación |
| `POST /pedidos` con CSRF | Sí | No (403) | No |
| `GET /reportes/**` | Sí | Sí | Autenticación |
| `GET /api/v1/pedidos/**` | Sí | Sí | Autenticación |
| `POST /pedidos` sin CSRF | 403 | 403 | 403/redirección según flujo |

CSRF **no se desactiva**. Los formularios JSP que usan POST conservan el token.

La aplicación mantiene `formLogin` para navegación web y habilita HTTP Basic para facilitar la prueba autenticada de endpoints GET desde Postman.

Usuarios de laboratorio:

| Usuario | Rol | Variable de contraseña |
|---|---|---|
| `operador` | `OPERADOR` | `PEDIDOS_OPERADOR_PASSWORD` |
| `consulta` | `CONSULTA` | `PEDIDOS_CONSULTA_PASSWORD` |

Los valores predeterminados del proyecto son únicamente para laboratorio local.

## 5. Requisitos

- JDK 21.
- Maven 3.9.x o Maven Wrapper.
- MySQL 8.x.
- `inventario-service` disponible en `http://localhost:8082/inventario-service` para registrar pedidos.
- Puerto 8081 disponible para `sistema-pedidos`.

## 6. Base de datos MySQL

Ejecutar con una cuenta administradora:

```text
Script_BD_usuario_MySql.sql
```

El script crea `pedidosdb` y el usuario de laboratorio `spedidos_app`. Cambiar la clave de ejemplo antes de usarla y pasarla por variables de entorno.

PowerShell:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/pedidosdb?serverTimezone=America/Lima&useSSL=false&allowPublicKeyRetrieval=true"
$env:DB_USERNAME="spedidos_app"
$env:DB_PASSWORD="<clave-local>"
$env:PEDIDOS_OPERADOR_PASSWORD="<clave-operador>"
$env:PEDIDOS_CONSULTA_PASSWORD="<clave-consulta>"
```

`application.properties` no requiere registrar una contraseña personal de MySQL.

## 7. Orden de arranque

1. Iniciar MySQL.
2. Ejecutar `Script_BD_usuario_MySql.sql` si es la primera vez.
3. Configurar las variables de entorno.
4. Iniciar `inventario-service` en el puerto 8082.
5. Desde la raíz de este proyecto ejecutar:

```bash
./mvnw spring-boot:run
```

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

6. Abrir:

```text
http://localhost:8081/sistema-pedidos/pedidos
http://localhost:8081/sistema-pedidos/reportes/pedidos
```

## 8. Cinco casos funcionales PA3

### Caso 1 - Cliente + total mínimo

1. Registrar por lo menos tres pedidos.
2. Asegurar que solo algunos correspondan al cliente y al monto mínimo elegidos.
3. Consultar la JSP y el endpoint REST.
4. Comparar con MySQL:

```sql
SELECT *
FROM pedido
WHERE LOWER(cliente) LIKE LOWER('%ana%')
  AND total >= 100
ORDER BY id DESC;
```

Resultado: JSP, JSON y MySQL deben coincidir.

### Caso 2 - Clientes frecuentes

1. Generar pedidos para dos o más clientes.
2. Ejecutar `minPedidos=2`.
3. Verificar `totalPedidos`, `totalUnidades`, `importeAcumulado` y `ticketPromedio`.
4. Contrastar en MySQL con `GROUP BY` y `HAVING`.

### Caso 3 - Parámetros inválidos

Probar:

```text
cliente=""
totalMinimo=-1
minPedidos=0
```

La API responde `400 Bad Request`. El repositorio no concatena esos valores dentro del SQL.

### Caso 4 - inventario-service indisponible

1. Registrar pedidos con ambos sistemas activos.
2. Detener `inventario-service`.
3. Intentar registrar un pedido: debe informarse la indisponibilidad de manera controlada.
4. Abrir `/reportes/pedidos`: los reportes continúan funcionando porque consultan la base local MySQL.
5. Restaurar `inventario-service`.

### Caso 5 - Roles y CSRF

- Anónimo a `/reportes/pedidos` -> autenticación.
- `consulta` -> puede consultar reportes.
- `consulta` + CSRF válido -> `POST /pedidos` rechazado con 403.
- `operador` sin CSRF -> `POST /pedidos` rechazado con 403.
- `operador` + CSRF válido -> puede registrar pedido.

## 9. Pruebas automatizadas nuevas PA3

Se añadieron **9 pruebas nuevas**:

- `PedidoReporteJdbcRepositoryPa3Test`
    - búsqueda avanzada con cliente + total mínimo;
    - `GROUP BY/HAVING` y agregados de clientes frecuentes.
- `PedidoReporteServiceImplPa3Test`
    - cliente vacío;
    - total mínimo negativo;
    - `minPedidos=0`;
    - en los tres casos se verifica que no se consulte el repositorio.
- `PedidoReportePa3IntegrationTest`
    - API búsqueda avanzada con rol CONSULTA;
    - parámetro inválido -> HTTP 400;
    - CONSULTA accede a JSP;
    - anónimo es redirigido a autenticación.

Ejecutar:

```bash
./mvnw test
```

Para la evidencia final adjuntar una captura o salida de consola donde se observe `BUILD SUCCESS` y el total de pruebas ejecutadas.

## 10. Archivos principales de PA3

Nuevos:

- `Script_BD_usuario_MySql.sql`
- `controller/PedidoReporteWebController.java`
- `controller/PedidoReporteApiExceptionHandler.java`
- `controller/dto/ClienteFrecuenteDto.java`
- `exception/ParametroReporteInvalidoException.java`
- `repository/projection/ClienteFrecuenteProjection.java`
- `service/domain/ClienteFrecuente.java`
- `WEB-INF/views/reportes-pedidos.jsp`
- tres clases de pruebas PA3.

Modificados:

- `PedidoReporteJdbcRepository.java`
- `PedidoReporteService.java`
- `PedidoReporteServiceImpl.java`
- `PedidoReporteController.java`
- `SecurityConfig.java`
- `pedidos.jsp`
- `app.css`
- `application.properties`
- `README.md`
- colección Postman.

No se requiere modificar `inventario-service` para la PA3.

## 11. Evidencias recomendadas para el informe

Conservar evidencia de:

1. JSP de búsqueda avanzada y consulta equivalente en MySQL.
2. JSP/JSON de clientes frecuentes y consulta `GROUP BY/HAVING` en MySQL.
3. HTTP 400 para los tres parámetros inválidos.
4. error controlado con `inventario-service` detenido + reportes aún operativos.
5. autenticación, rol CONSULTA, rol OPERADOR y rechazo por CSRF.
6. `mvn test` completo.
7. URL del repositorio, rama y hash del commit final.

## 12. Limitaciones académicas

- Usuarios en memoria; no se implementa persistencia de usuarios.
- No se incorpora JWT, OAuth2/OIDC ni un proveedor externo de identidad.
- La consulta avanzada usa coincidencia parcial de cliente mediante `LIKE`.
- El módulo de reportes es de lectura; la creación de pedidos permanece en el flujo transaccional existente.
