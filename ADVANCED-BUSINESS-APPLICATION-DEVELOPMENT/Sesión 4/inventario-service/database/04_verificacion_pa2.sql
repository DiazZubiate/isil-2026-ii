-- Solo consultas SELECT: no altera InventarioDB.
USE InventarioDB;
GO
SELECT codigo, nombre, stock, version
FROM dbo.producto_inventario
WHERE codigo = 'LAP-001';
GO
SELECT codigo, nombre, stock, version
FROM dbo.producto_inventario
ORDER BY codigo;
GO
-- Registrar 3 capturas: ANTES (por ej. 10), RESERVA (8), DEVOLUCION (10).
-- El stock real puede variar: comparar diferencia, no un numero absoluto.
-- La columna version cambia en operaciones actualizadas por JPA.
