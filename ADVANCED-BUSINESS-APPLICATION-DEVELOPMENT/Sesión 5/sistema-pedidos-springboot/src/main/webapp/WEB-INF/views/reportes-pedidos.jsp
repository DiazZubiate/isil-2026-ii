<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reportes de pedidos - ISIL</title>
    <c:url var="cssUrl" value="/assets/css/app.css"/>
    <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
<div class="contenedor">
    <header class="cabecera">
        <div>
            <p class="eyebrow">PA3 · Spring MVC · Spring JDBC · Spring Security</p>
            <h1>Consulta y supervisión de pedidos</h1>
            <p class="subtitulo">
                La vista reutiliza la misma capa Service que los endpoints JSON y consulta datos locales mediante Spring JDBC.
            </p>
        </div>
        <div class="acciones-cabecera">
            <c:url var="pedidosUrl" value="/pedidos"/>
            <a class="boton-secundario" href="${pedidosUrl}">Volver a pedidos</a>
            <c:url var="logoutUrl" value="/logout"/>
            <form method="post" action="${logoutUrl}">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                <button type="submit">Cerrar sesión</button>
            </form>
        </div>
    </header>

    <c:if test="${not empty error}">
        <div class="alerta error">
            <strong>Solicitud inválida:</strong> <c:out value="${error}"/>
        </div>
    </c:if>

    <section class="resumen-grid">
        <article class="tarjeta indicador">
            <span>Total de pedidos</span>
            <strong><c:out value="${resumen.totalPedidos}"/></strong>
        </article>
        <article class="tarjeta indicador">
            <span>Total de unidades</span>
            <strong><c:out value="${resumen.totalUnidades}"/></strong>
        </article>
        <article class="tarjeta indicador">
            <span>Importe acumulado</span>
            <strong>S/ <fmt:formatNumber value="${resumen.importeAcumulado}" minFractionDigits="2" maxFractionDigits="2"/></strong>
        </article>
        <article class="tarjeta indicador">
            <span>Ticket promedio</span>
            <strong>S/ <fmt:formatNumber value="${resumen.ticketPromedio}" minFractionDigits="2" maxFractionDigits="2"/></strong>
        </article>
    </section>

    <section class="tarjeta">
        <h2>Búsqueda avanzada</h2>
        <p class="ayuda">Filtra por coincidencia de cliente y total del pedido mayor o igual al monto indicado.</p>

        <c:url var="reportesUrl" value="/reportes/pedidos"/>
        <form method="get" action="${reportesUrl}" class="form-grid reporte-form">
            <label>
                Cliente
                <input name="cliente"
                       required
                       maxlength="120"
                       placeholder="Ej. Ana"
                       value="${fn:escapeXml(clienteIngresado)}">
            </label>
            <label>
                Total mínimo
                <input name="totalMinimo"
                       type="number"
                       min="0"
                       step="0.01"
                       required
                       placeholder="0.00"
                       value="${totalMinimoIngresado}">
            </label>
            <button type="submit">Buscar</button>
        </form>

        <div class="tabla-scroll espacio-superior">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Cliente</th>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Total</th>
                    <th>Fecha</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="pedido" items="${pedidosEncontrados}">
                    <tr>
                        <td><c:out value="${pedido.id}"/></td>
                        <td><c:out value="${pedido.cliente}"/></td>
                        <td><c:out value="${pedido.productoNombre}"/></td>
                        <td><c:out value="${pedido.cantidad}"/></td>
                        <td>S/ <fmt:formatNumber value="${pedido.total}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        <td><c:out value="${pedido.fecha}"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty pedidosEncontrados}">
                    <tr><td colspan="6" class="sin-datos">No hay resultados para mostrar.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </section>

    <section class="tarjeta">
        <h2>Clientes frecuentes</h2>
        <p class="ayuda">Consolida pedidos por cliente usando <code>GROUP BY</code> y filtra el mínimo con <code>HAVING</code>.</p>

        <form method="get" action="${reportesUrl}" class="form-grid reporte-form compacto">
            <label>
                Mínimo de pedidos
                <input name="minPedidos"
                       type="number"
                       min="1"
                       required
                       placeholder="2"
                       value="${minPedidosIngresado}">
            </label>
            <button type="submit">Consolidar</button>
        </form>

        <div class="tabla-scroll espacio-superior">
            <table>
                <thead>
                <tr>
                    <th>Cliente</th>
                    <th>Total pedidos</th>
                    <th>Total unidades</th>
                    <th>Importe acumulado</th>
                    <th>Ticket promedio</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${clientesFrecuentes}">
                    <tr>
                        <td><c:out value="${item.cliente}"/></td>
                        <td><c:out value="${item.totalPedidos}"/></td>
                        <td><c:out value="${item.totalUnidades}"/></td>
                        <td>S/ <fmt:formatNumber value="${item.importeAcumulado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        <td>S/ <fmt:formatNumber value="${item.ticketPromedio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty clientesFrecuentes}">
                    <tr><td colspan="5" class="sin-datos">No hay clientes que cumplan el umbral seleccionado.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </section>

    <section class="tarjeta">
        <h2>Endpoints JSON equivalentes</h2>
        <ul class="rutas">
            <li><code>GET /api/v1/pedidos/busqueda-avanzada?cliente=Ana&amp;totalMinimo=100</code></li>
            <li><code>GET /api/v1/pedidos/clientes-frecuentes?minPedidos=2</code></li>
        </ul>
    </section>
</div>
</body>
</html>
