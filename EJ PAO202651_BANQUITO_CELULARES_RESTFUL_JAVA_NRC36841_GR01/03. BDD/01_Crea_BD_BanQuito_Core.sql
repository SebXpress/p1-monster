/*==============================================================*/
/* BANQUITO CORE - Script DDL                                    */
/* Motor: MariaDB 10.4+ / MySQL 8                                */
/* Grupo 1 - NRC 36841 - Arquitectura de Software ESPE           */
/* Modelo del CORE (CLIENTE, CUENTA, MOVIMIENTO) + módulo de     */
/* crédito (CREDITO, AMORTIZACION) en el mismo esquema.          */
/* Uso: 1) este script  2) 02_Carga_Datos_BanQuito_Core.sql      */
/*==============================================================*/

-- DROP: elimina la base si existe
DROP DATABASE IF EXISTS banquito_core;

-- CREATE: crea la base de datos
CREATE DATABASE banquito_core CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE banquito_core;

/*==============================================================*/
/* CREATE TABLE: modelo del CORE (tal como lo entrega el banco)  */
/*==============================================================*/
CREATE TABLE CLIENTE (
  COD_CLIENTE      INT          NOT NULL,
  CEDULA           VARCHAR(100) NOT NULL,
  NOMBRE           VARCHAR(100) NOT NULL,
  GENERO           VARCHAR(1)   NOT NULL,
  FECHA_NACIMIENTO DATE         NOT NULL,
  CONSTRAINT PK_CLIENTE PRIMARY KEY (COD_CLIENTE),
  CONSTRAINT UQ_CLIENTE_CEDULA UNIQUE (CEDULA),
  CONSTRAINT CK_CLIENTE_GENERO CHECK (GENERO IN ('M', 'F'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE CUENTA (
  NUM_CUENTA  VARCHAR(8)     NOT NULL,
  COD_CLIENTE INT            NOT NULL,
  SALDO       NUMERIC(10,2)  NOT NULL DEFAULT 0,
  CONSTRAINT PK_CUENTA PRIMARY KEY (NUM_CUENTA)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE MOVIMIENTO (
  COD_MOVIMIENTO INT           NOT NULL AUTO_INCREMENT,
  NUM_CUENTA     VARCHAR(8)    NOT NULL,
  TIPO           VARCHAR(3)    NOT NULL,
  VALOR          NUMERIC(10,2) NOT NULL,
  FECHA          DATE          NOT NULL,
  CONSTRAINT PK_MOVIMIENTO PRIMARY KEY (COD_MOVIMIENTO),
  CONSTRAINT CK_MOVIMIENTO_TIPO CHECK (TIPO IN ('DEP', 'RET')),
  CONSTRAINT CK_MOVIMIENTO_VALOR CHECK (VALOR > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*==============================================================*/
/* CREATE TABLE: módulo de crédito (solo datos del crédito y la  */
/* tabla de amortización, en el mismo esquema del CORE)          */
/*==============================================================*/
CREATE TABLE CREDITO (
  COD_CREDITO INT           NOT NULL AUTO_INCREMENT,
  COD_CLIENTE INT           NOT NULL,
  MONTO       NUMERIC(10,2) NOT NULL,
  NUM_CUOTAS  INT           NOT NULL,
  TASA_ANUAL  NUMERIC(5,2)  NOT NULL DEFAULT 16.50,
  CUOTA       NUMERIC(10,2) NOT NULL,
  FECHA       DATE          NOT NULL,
  ESTADO      VARCHAR(10)   NOT NULL DEFAULT 'ACTIVO',
  CONSTRAINT PK_CREDITO PRIMARY KEY (COD_CREDITO),
  CONSTRAINT CK_CREDITO_CUOTAS CHECK (NUM_CUOTAS BETWEEN 3 AND 18),
  CONSTRAINT CK_CREDITO_MONTO CHECK (MONTO > 0),
  CONSTRAINT CK_CREDITO_ESTADO CHECK (ESTADO IN ('ACTIVO', 'CANCELADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE AMORTIZACION (
  COD_CREDITO    INT           NOT NULL,
  NUM_CUOTA      INT           NOT NULL,
  VALOR_CUOTA    NUMERIC(10,2) NOT NULL,
  INTERES_PAGADO NUMERIC(10,2) NOT NULL,
  CAPITAL_PAGADO NUMERIC(10,2) NOT NULL,
  SALDO          NUMERIC(10,2) NOT NULL,
  CONSTRAINT PK_AMORTIZACION PRIMARY KEY (COD_CREDITO, NUM_CUOTA)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*==============================================================*/
/* ALTER TABLE: claves foráneas e índices                        */
/*==============================================================*/
ALTER TABLE CUENTA
  ADD CONSTRAINT FK_CUENTA_CLIENTE FOREIGN KEY (COD_CLIENTE) REFERENCES CLIENTE (COD_CLIENTE);

ALTER TABLE MOVIMIENTO
  ADD CONSTRAINT FK_MOVIMIENTO_CUENTA FOREIGN KEY (NUM_CUENTA) REFERENCES CUENTA (NUM_CUENTA);

ALTER TABLE CREDITO
  ADD CONSTRAINT FK_CREDITO_CLIENTE FOREIGN KEY (COD_CLIENTE) REFERENCES CLIENTE (COD_CLIENTE);

ALTER TABLE AMORTIZACION
  ADD CONSTRAINT FK_AMORTIZACION_CREDITO FOREIGN KEY (COD_CREDITO) REFERENCES CREDITO (COD_CREDITO);

ALTER TABLE MOVIMIENTO ADD INDEX IDX_MOVIMIENTO_FECHA (NUM_CUENTA, TIPO, FECHA);
ALTER TABLE CREDITO ADD INDEX IDX_CREDITO_ESTADO (COD_CLIENTE, ESTADO);
