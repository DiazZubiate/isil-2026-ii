<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Supervisión y Trazabilidad de Pedidos - PA4</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
    <style>
        .cards { display: flex; gap: 15px; margin-bottom: 20px; }
        .card { border: 1px solid #ccc; padding: 15px; border-radius: 6px; flex: 1; background: #f9f9f9; }
        .alert-ok { background: #d4edda; color: #155724; padding: 10px; margin-bottom: 15px; border-radius: 4px; }
        .alert-err { background: #f8d7da; color: #721c24; padding: 10px; margin-bottom: 15px; border-radius: 4px; }
        .alert-warn { background: #fff3cd; color: #856404; padding: 10px; margin-bottom: 15px; border-radius: 4px; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #2c3e50; color: white; }
    </style>
</head>
<body>
<div class="container">
    <h1>Módulo de Supervisión y Revisión de Pedidos</h1>
    <p>
        <a href="${pageContext.request.contextPath}/pedidos">Volver a Gestión de Pedidos</a> |
        <a href="${pageContext.request.contextPath}/supervision/pedidos?estado=PENDIENTE">Ver Pendientes</a> |
        <a href="${pageContext.request.contextPath}/supervision/pedidos?estado=REVISADO">Ver Revisados</a>
    </p>

    <c:if test="${not empty mensajeExito}">
        <div class="alert-ok">${mensajeExito}</div>
    </c:if>
    <c:if test="${not empty mensajeError}">
        <div class="alert-err">${mensajeError}</div>
    </c:if>
    <c:if test="${not empty mensajeAdvertencia}">
        <div class="alert-warn"><strong>Aviso:</strong> ${mensajeAdvertencia}</div>
    </c:if>

    <div class="cards">
        <div class="card"><strong>Total Pedidos:</strong><br>${estadisticas.totalPedidos}</div>
        <div class="card"><strong>Total Revisados:</strong><br>${estadisticas.totalRevisados}</div>
        <div class="card"><strong>Total Pendientes:</strong><br>${estadisticas.totalPendientes}</div>
        <div class="card"><strong>% Revisado:</strong><br>${estadisticas.porcentajeRevisado}%</div>
    </div>

    <h3>Listado de Pedidos (${estadoActual})</h3>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Cliente</th>
            <th>Producto</th>
            <th>Cantidad</th>
            <th>Total</th>
            <th>Estado</th>
            <th>Observación / Acción</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty pedidos}">
                <tr><td colspan="7">No hay pedidos para mostrar en esta vista.</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="p" items="${pedidos}">
                    <tr>
                        <td>${p.id}</td>
                        <td>${p.cliente}</td>
                        <td>${p.producto}</td>
                        <td>${p.cantidad}</td>
                        <td>S/ ${p.total}</td>
                        <td><strong>${p.revisado ? 'REVISADO' : 'PENDIENTE'}</strong></td>
                        <td>
                            <c:choose>
                                <c:when test="${p.revisado}">
                                    <span>${p.observacion} (${p.fechaRevision})</span>
                                </c:when>
                                <c:otherwise>
                                    <form method="post" action="${pageContext.request.contextPath}/supervision/pedidos/${p.id}/revisar">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <input type="text" name="observacion" placeholder="Ingrese observación..." required/>
                                        <button type="submit">Marcar Revisado</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</div>
</body>
</html>
