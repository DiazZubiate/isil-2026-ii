ALTER TABLE pedido 
ADD COLUMN revisado BOOLEAN NOT NULL DEFAULT FALSE;


CREATE TABLE pedido_revision (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    observacion VARCHAR(500) NOT NULL,
    fecha_revision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_revision_pedido 
        FOREIGN KEY (pedido_id) REFERENCES pedido(id)
);
