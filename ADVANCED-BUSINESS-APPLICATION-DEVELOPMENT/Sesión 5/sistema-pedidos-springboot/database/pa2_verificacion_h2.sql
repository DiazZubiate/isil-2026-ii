-- Ejecutar en H2 Console: http://localhost:8081/sistema-pedidos/h2-console
-- La base es H2 en memoria: se reinicia junto con Spring Boot.
-- Sustituir el ID de ejemplo por el ID real obtenido en GET /api/v1/orders.

SELECT ID, CLIENTE, PRODUCTO_CODIGO, CANTIDAD, ESTADO
FROM PEDIDO ORDER BY ID DESC;

SELECT ID, PEDIDO_ID, PRODUCTO_CODIGO, CANTIDAD_DEVUELTA, FECHA, MOTIVO
FROM ANULACION_PEDIDO ORDER BY ID DESC;

-- Casos 1 y 2: auditoria exactamente una vez por pedido anulado.
SELECT PEDIDO_ID, COUNT(*) AS TOTAL_ANULACIONES
FROM ANULACION_PEDIDO GROUP BY PEDIDO_ID;

-- Caso 4: comprobar que el pedido sigue REGISTRADO tras 503.
SELECT ID, ESTADO FROM PEDIDO WHERE ID = 2;
SELECT COUNT(*) AS TOTAL_ANULACIONES FROM ANULACION_PEDIDO WHERE PEDIDO_ID = 2;

-- Reto +3: tras fallo simulado no debe existir el nuevo pedido.
SELECT ID, CLIENTE, ESTADO FROM PEDIDO
WHERE CLIENTE = 'Cliente Compensacion PA2';
