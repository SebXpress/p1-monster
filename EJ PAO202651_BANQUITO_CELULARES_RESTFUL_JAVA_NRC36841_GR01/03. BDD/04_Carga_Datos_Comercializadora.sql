/*==============================================================*/
/* COMERCIALIZADORA - Carga de datos: catálogo de celulares      */
/* (sin facturas: se generan desde las aplicaciones)             */
/*==============================================================*/
USE comercializadora;
SET NAMES utf8mb4;

INSERT INTO CELULAR (MARCA, MODELO, DESCRIPCION, PRECIO, ESTADO) VALUES
('Samsung',  'Galaxy A15',        '6.5" 128 GB, 4 GB RAM, cámara 50 MP',            199.00, 'ACTIVO'),
('Xiaomi',   'Redmi Note 13',     '6.67" AMOLED 256 GB, 8 GB RAM',                  279.00, 'ACTIVO'),
('Motorola', 'Moto G84',          '6.5" pOLED 256 GB, 12 GB RAM',                   329.00, 'ACTIVO'),
('Samsung',  'Galaxy A55',        '6.6" Super AMOLED 256 GB, 8 GB RAM',             449.00, 'ACTIVO'),
('Honor',    'Magic 6 Lite',      '6.78" AMOLED 256 GB, batería 5300 mAh',          399.00, 'ACTIVO'),
('Xiaomi',   '14T',               '6.67" 512 GB, cámara Leica 50 MP',               699.00, 'ACTIVO'),
('Apple',    'iPhone 15',         '6.1" 128 GB, chip A16 Bionic',                   899.00, 'ACTIVO'),
('Samsung',  'Galaxy S24',        '6.2" 256 GB, Galaxy AI',                         999.00, 'ACTIVO'),
('Apple',    'iPhone 16 Pro',     '6.3" 256 GB, chip A18 Pro',                     1399.00, 'ACTIVO'),
('Samsung',  'Galaxy S25 Ultra',  '6.9" 512 GB, S Pen, cámara 200 MP',             1599.00, 'ACTIVO'),
('Nokia',    'C32',               '6.5" 64 GB, modelo descontinuado',                89.00, 'INACTIVO');
