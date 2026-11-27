# isil-2026-ii
DESARROLLO DE APLICACIONES EMPRESARIALES AVANZADO
# Sistema de Pedidos - Módulo de Revisión y Trazabilidad (PA4)

Evolución individual del Sistema de Pedidos desarrollado para el curso **Desarrollo de Aplicaciones Empresariales Avanzado (2026-20)**. En esta entrega implementé el flujo transaccional de revisión de pedidos utilizando **Spring JDBC (`@Transactional`)**, **Spring MVC + JSP** y dos nuevos recursos REST en el microservicio `pedidos-reportes-service` consumidos mediante **`RestClient`** con tolerancia a fallos.

---

## 1. Requisitos del Entorno

* **Java Development Kit (JDK):** 17 o 21
* **Gestor de dependencias:** Apache Maven 3.9+ (o wrapper `./mvnw` / `mvnw.cmd`)
* **Base de datos:** MySQL 8.0+
* **Servidor de aplicaciones (para inventario):** Payara / WildFly / GlassFish (Jakarta EE)

---

## 2. Arquitectura y Puertos de Ejecución

La solución opera con tres servicios independientes:

| Servicio | Ruta en el Repositorio | Puerto | Tecnología Principal |
| :--- | :--- | :---: | :--- |
| **sistema-pedidos-springboot** | `ADVANCED-BUSINESS-APPLICATION-DEVELOPMENT/Sesión 5/sistema-pedidos-springboot` | `8081` | Spring Boot, Spring MVC, JSP, Spring Security, Spring JDBC, RestClient |
| **inventario-service** | `ADVANCED-BUSINESS-APPLICATION-DEVELOPMENT/Sesión 4/inventario-service` | `8082` | Jakarta EE, JAX-RS, JPA |
| **pedidos-reportes-service** | `ADVANCED-BUSINESS-APPLICATION-DEVELOPMENT/Sesión 12/pedidos-reportes-service` | `8083` | Spring Boot REST, Spring JDBC |

### Orden de Arranque Recomendado
1. Iniciar el servidor **MySQL** y ejecutar los scripts de base de datos.
2. Levantar **`inventario-service`** en el puerto **8082** (para permitir el registro de pedidos nuevos con validación de stock).
3. Levantar **`pedidos-reportes-service`** en el puerto **8083**.
4. Levantar **`sistema-pedidos-springboot`** en el puerto **8081**.

---

## 3. Variables de Entorno

Para evitar exponer credenciales reales en el repositorio, las conexiones a base de datos y URLs de servicios se externalizan mediante variables de entorno (o en `application.properties`):

```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/db_pedidos?useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=tu_password_mysql
REPORTES_SERVICE_BASE_URL=http://localhost:8083
INVENTARIO_SERVICE_BASE_URL=http://localhost:8082
