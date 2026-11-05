/*==============================================================*/
/* COMERCIALIZADORA DE TELÉFONOS CELULARES - Script DDL          */
/* Motor: MariaDB 10.4+ / MySQL 8                                */
/* Grupo 1 - NRC 36841 - Arquitectura de Software ESPE           */
/* Facturación sin inventario: CELULAR es solo el catálogo de    */
/* teléfonos disponibles para la venta.                          */
/* Uso: 3) este script  4) 04_Carga_Datos_Comercializadora.sql   */
/*==============================================================*/

-- DROP: elimina la base si existe
DROP DATABASE IF EXISTS comercializadora;

-- CREATE: crea la base de datos
CREATE DATABASE comercializadora CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE comercializadora;

/*==============================================================*/
/* CREATE TABLE                                                  */
/*==============================================================*/
CREATE TABLE CELULAR (
  COD_CELULAR INT           NOT NULL AUTO_INCREMENT,
  MARCA       VARCHAR(40)   NOT NULL,
  MODELO      VARCHAR(60)   NOT NULL,
  DESCRIPCION VARCHAR(200)  NULL,
  PRECIO      NUMERIC(10,2) NOT NULL,
  ESTADO      VARCHAR(10)   NOT NULL DEFAULT 'ACTIVO',
  CONSTRAINT PK_CELULAR PRIMARY KEY (COD_CELULAR),
  CONSTRAINT UQ_CELULAR_MODELO UNIQUE (MARCA, MODELO),
  CONSTRAINT CK_CELULAR_PRECIO CHECK (PRECIO > 0),
  CONSTRAINT CK_CELULAR_ESTADO CHECK (ESTADO IN ('ACTIVO', 'INACTIVO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE CLIENTE (
  CEDULA    VARCHAR(10)  NOT NULL,
  NOMBRE    VARCHAR(100) NOT NULL,
  DIRECCION VARCHAR(150) NULL,
  TELEFONO  VARCHAR(15)  NULL,
  CORREO    VARCHAR(100) NULL,
  CONSTRAINT PK_CLIENTE PRIMARY KEY (CEDULA)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE FACTURA (
  NUM_FACTURA INT           NOT NULL AUTO_INCREMENT,
  FECHA       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CEDULA      VARCHAR(10)   NOT NULL,
  FORMA_PAGO  VARCHAR(10)   NOT NULL,
  SUBTOTAL    NUMERIC(10,2) NOT NULL,
  DESCUENTO   NUMERIC(10,2) NOT NULL DEFAULT 0,
  TOTAL       NUMERIC(10,2) NOT NULL,
  COD_CREDITO INT           NULL,
  NUM_CUOTAS  INT           NULL,
  VALOR_CUOTA NUMERIC(10,2) NULL,
  CONSTRAINT PK_FACTURA PRIMARY KEY (NUM_FACTURA),
  CONSTRAINT CK_FACTURA_FORMA_PAGO CHECK (FORMA_PAGO IN ('EFECTIVO', 'CREDITO')),
  CONSTRAINT CK_FACTURA_CUOTAS CHECK (NUM_CUOTAS IS NULL OR NUM_CUOTAS BETWEEN 3 AND 18)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE DETALLE_FACTURA (
  NUM_FACTURA     INT           NOT NULL,
  LINEA           INT           NOT NULL,
  COD_CELULAR     INT           NOT NULL,
  CANTIDAD        INT           NOT NULL,
  PRECIO_UNITARIO NUMERIC(10,2) NOT NULL,
  SUBTOTAL        NUMERIC(10,2) NOT NULL,
  CONSTRAINT PK_DETALLE_FACTURA PRIMARY KEY (NUM_FACTURA, LINEA),
  CONSTRAINT CK_DETALLE_CANTIDAD CHECK (CANTIDAD > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*==============================================================*/
/* ALTER TABLE: claves foráneas                                  */
/*==============================================================*/
ALTER TABLE FACTURA
  ADD CONSTRAINT FK_FACTURA_CLIENTE FOREIGN KEY (CEDULA) REFERENCES CLIENTE (CEDULA);

ALTER TABLE DETALLE_FACTURA
  ADD CONSTRAINT FK_DETALLE_FACTURA FOREIGN KEY (NUM_FACTURA) REFERENCES FACTURA (NUM_FACTURA);

ALTER TABLE DETALLE_FACTURA
  ADD CONSTRAINT FK_DETALLE_CELULAR FOREIGN KEY (COD_CELULAR) REFERENCES CELULAR (COD_CELULAR);
