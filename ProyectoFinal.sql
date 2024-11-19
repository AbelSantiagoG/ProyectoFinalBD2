--Creación de entidades
create type estado_factura as enum('pagada', 'pendiente', 'en proceso');
create type tipo_informe as enum('mensual', 'semanal', 'diario', 'anual', 'personalizado');
create type tipo_pago as enum('efectivo', 'tarjeta');


create sequence usuarioSecuencia
start with 1
increment by 1
no maxvalue;

create sequence categoriaSecuencia
start with 1
increment by 1
no maxvalue;

create sequence productoSecuencia
start with 1
increment by 1
no maxvalue;

create sequence inventarioSecuencia
start with 1
increment by 1
no maxvalue;

create sequence ventaSecuencia
start with 1
increment by 1
no maxvalue;

create sequence carritoSecuencia
start with 1
increment by 1
no maxvalue;

create sequence facturaSecuencia
start with 1
increment by 1
no maxvalue;

create sequence puntosRedimidosSecuencia
start with 1
increment by 1
no maxvalue;

create sequence puntosGanadosSecuencia
start with 1
increment by 1
no maxvalue;

create sequence informeSecuencia
start with 1
increment by 1
no maxvalue;

create sequence auditoriaSecuencia
start with 1
increment by 1
no maxvalue;



create table usuarios (
    id int primary key default nextval('usuarioSecuencia'), 
    numero_documento varchar(15) unique not null,
    nombre varchar(50) not null,
    contrasenia varchar(50) not null,
    email varchar(100) not null,
    celular varchar(20),
    puntos int,
	rol int
);

create table categorias (
    id int primary key default nextval('categoriaSecuencia'),
    nombre varchar(50) not null unique
);

create table productos (
    id int primary key default nextval('productoSecuencia'),
    nombre varchar(40) unique not null,
    descripcion varchar(100),
    precio numeric not null,
    imagen varchar(100),
    descuento int,
    categoria_id int references categorias(id)
);


create table inventarios(
	id int primary key default nextval('inventarioSecuencia'),
	cantidad_disponible int,
	referencia_compra varchar(30) unique not null,
	producto_id int references producto(id)
);


create table ventas(
	id int primary key default nextval('ventaSecuencia'),
	carrito_id int references carrito(id),
	producto_id int references producto(id)
);


create table carritos(
	id int primary key default nextval('carritoSecuencia'),
	cantidad int ,
	total numeric,
);


create table facturas(
	id int primary key default nextval('facturaSecuencia'),
	codigo varchar(30) unique not null,
	fecha date,
	subtotal int,
	total int,
	impuesto int,
	estado estado_factura,
	cliente_id int references ususario(id),
	carrito_id int references carrito(id)
);

create table puntos_redimidos(
	id int primary key default nextval('puntosRedimidosSecuencia'),
	cantidad int,
	fecha_redencion date,
	usuario_id int references usuario(id)
);

create table puntos_ganados(
	id int primary key default nextval('puntosGanadosSecuencia'),
	cantidad int,
	fecha_ganacia date,
	motivo varchar(50) not null,
	referencia varchar(20),
	usuario_id int references usuario(id)
);


create table informes (
    id int primary key default nextval('informeSecuencia'),
    tipo tipo_informe not null,
    fecha date not null,
    datos_json json not null
);


create table documentos_auditoria (
    id int primary key default nextval('audotiraSecuencia'),
    fecha date not null,
    cantidad int not null,
    total numeric not null,
    producto_id int references productos(id),
    cliente_id int references clientes(id)
);

----------------------------------------------------Funcionalidades----------------------------------------------------
-----------------------------------1. Usuarios-----------------------------------

----------Login----------

CREATE OR REPLACE PROCEDURE crear_usuario(
    numero_documento_input VARCHAR,
    nombre_input VARCHAR,
    contrasenia_input VARCHAR,
    email_input VARCHAR,
    celular_input VARCHAR,
    puntos_input INT DEFAULT 0
)
LANGUAGE plpgsql AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM usuario WHERE numero_documento = numero_documento_input
    ) THEN
        RAISE EXCEPTION 'Error: El número de documento % ya está registrado.', numero_documento_input;
    END IF;

    IF EXISTS (
        SELECT 1 FROM usuario WHERE email = email_input
    ) THEN
        RAISE EXCEPTION 'Error: El correo electrónico % ya está registrado.', email_input;
    END IF;

    INSERT INTO usuario (
        numero_documento, nombre, contrasenia, email, celular, puntos
    ) VALUES (
        numero_documento_input, nombre_input, contrasenia_input, email_input, celular_input, puntos_input
    );

    RAISE NOTICE 'Usuario creado exitosamente: %', nombre_input;
END;
$$;

----------Crear usuario----------

CREATE OR REPLACE FUNCTION login_usuario(numero_documento_input VARCHAR, contrasenia_input VARCHAR)
RETURNS TEXT AS $$
DECLARE
    contrasenia_actual VARCHAR;
BEGIN
    SELECT contrasenia INTO contrasenia_actual
    FROM usuario
    WHERE numero_documento = numero_documento_input;

    IF contrasenia_actual IS NULL THEN
        RETURN 'El usuario no existe.';
    ELSIF contrasenia_actual = contrasenia_input THEN
        RETURN 'Login exitoso.';
    ELSE
        RETURN 'Contraseña incorrecta.';
    END IF;
END;
$$ LANGUAGE plpgsql;

----------Actualizar usuario----------

CREATE OR REPLACE PROCEDURE modificar_usuario(
    usuario_id_input INT,
    numero_documento_input VARCHAR,
    nombre_input VARCHAR,
    contrasenia_input VARCHAR,
    email_input VARCHAR,
    celular_input VARCHAR,
    puntos_input INT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM usuario WHERE id = usuario_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El usuario con ID % no existe.', usuario_id_input;
    END IF;

    IF EXISTS (
        SELECT 1 FROM usuario 
        WHERE numero_documento = numero_documento_input AND id != usuario_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El número de documento % ya está registrado para otro usuario.', numero_documento_input;
    END IF;

    IF EXISTS (
        SELECT 1 FROM usuario 
        WHERE email = email_input AND id != usuario_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El correo electrónico % ya está registrado para otro usuario.', email_input;
    END IF;

    UPDATE usuario
    SET 
        numero_documento = numero_documento_input,
        nombre = nombre_input,
        contrasenia = contrasenia_input,
        email = email_input,
        celular = celular_input,
        puntos = puntos_input
    WHERE id = usuario_id_input;

    RAISE NOTICE 'Usuario con ID % modificado exitosamente.', usuario_id_input;
END;
$$;

----------Eliminar usuario----------

CREATE OR REPLACE PROCEDURE eliminar_usuario(usuario_id_input INT)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM usuario WHERE id = usuario_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El usuario con ID % no existe.', usuario_id_input;
    END IF;

    DELETE FROM usuario
    WHERE id = usuario_id_input;

    RAISE NOTICE 'Usuario con ID % eliminado exitosamente.', usuario_id_input;
END;
$$;



-----------------------------------2. Productos-----------------------------------

----------Crear producto----------

CREATE OR REPLACE PROCEDURE crear_producto(
    nombre_input VARCHAR,
    descripcion_input VARCHAR,
    precio_input NUMERIC,
    imagen_input VARCHAR,
    descuento_input INT,
    categoria_id_input INT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM producto WHERE nombre = nombre_input
    ) THEN
        RAISE EXCEPTION 'Error: El producto con nombre % ya está registrado.', nombre_input;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM categoria WHERE id = categoria_id_input
    ) THEN
        RAISE EXCEPTION 'Error: La categoría con ID % no existe.', categoria_id_input;
    END IF;

    INSERT INTO producto (
        nombre, descripcion, precio, imagen, descuento, categoria_id
    ) VALUES (
        nombre_input, descripcion_input, precio_input, imagen_input, descuento_input, categoria_id_input
    );

    RAISE NOTICE 'Producto % creado exitosamente.', nombre_input;
END;
$$;

----------Actualizar producto----------

CREATE OR REPLACE PROCEDURE modificar_producto(
    producto_id_input INT,
    nombre_input VARCHAR,
    descripcion_input VARCHAR,
    precio_input NUMERIC,
    imagen_input VARCHAR,
    descuento_input INT,
    categoria_id_input INT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM producto WHERE id = producto_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El producto con ID % no existe.', producto_id_input;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM categoria WHERE id = categoria_id_input
    ) THEN
        RAISE EXCEPTION 'Error: La categoría con ID % no existe.', categoria_id_input;
    END IF;

    UPDATE producto
    SET
        nombre = nombre_input,
        descripcion = descripcion_input,
        precio = precio_input,
        imagen = imagen_input,
        descuento = descuento_input,
        categoria_id = categoria_id_input
    WHERE id = producto_id_input;

    RAISE NOTICE 'Producto con ID % modificado exitosamente.', producto_id_input;
END;
$$;

----------Eliminar producto----------

CREATE OR REPLACE PROCEDURE eliminar_producto(producto_id_input INT)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM producto WHERE id = producto_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El producto con ID % no existe.', producto_id_input;
    END IF;

    DELETE FROM producto
    WHERE id = producto_id_input;

    RAISE NOTICE 'Producto con ID % eliminado exitosamente.', producto_id_input;
END;
$$;

----------Categorización de productos----------

CREATE OR REPLACE FUNCTION filtrar_productos(
    categoria_id_input INT DEFAULT NULL,
    precio_min NUMERIC DEFAULT NULL,
    precio_max NUMERIC DEFAULT NULL,
    descuento_min INT DEFAULT NULL,
    descuento_max INT DEFAULT NULL
)
RETURNS TABLE(
    id INT,
    nombre VARCHAR,
    descripcion VARCHAR,
    precio NUMERIC,
    imagen VARCHAR,
    descuento INT,
    categoria_id INT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.id, p.nombre, p.descripcion, p.precio, p.imagen, p.descuento, p.categoria_id
    FROM producto p
    WHERE 
        (categoria_id_input IS NULL OR p.categoria_id = categoria_id_input) AND
        (precio_min IS NULL OR p.precio >= precio_min) AND
        (precio_max IS NULL OR p.precio <= precio_max) AND
        (descuento_min IS NULL OR p.descuento >= descuento_min) AND
        (descuento_max IS NULL OR p.descuento <= descuento_max);
END;
$$ LANGUAGE plpgsql;








------------Registros para pruebitas------------
INSERT INTO usuarios (numero_documento, nombre, contrasenia, email, celular, puntos, rol) VALUES
('1234567890', 'Juan Perez', 'password1', 'juan.perez@gmail.com', '3001234567', 50, 1),
('0987654321', 'Maria Gomez', 'password2', 'maria.gomez@gmail.com', '3107654321', 40, 1),
('1122334455', 'Luis Alvarez', 'password3', 'luis.alvarez@gmail.com', '3201122334', 60, 1),
('5566778899', 'Ana Morales', 'password4', 'ana.morales@gmail.com', '3015566778', 30, 1),
('3344556677', 'Carlos Diaz', 'password5', 'carlos.diaz@gmail.com', '3113344556', 70, 1),
('2233445566', 'Laura Lopez', 'password6', 'laura.lopez@gmail.com', '3022233445', 80, 1),
('9988776655', 'Felipe Herrera', 'password7', 'felipe.herrera@gmail.com', '3129988776', 90, 1),
('8877665544', 'Sofia Martinez', 'password8', 'sofia.martinez@gmail.com', '3038877665', 20, 1),
('7766554433', 'Andres Torres', 'password9', 'andres.torres@gmail.com', '3137766554', 10, 1),
('1', 'admin', '12345678', 'admin@gmail.com', '305', 999999, 2);

INSERT INTO categorias (nombre) VALUES
('Electrónica'),
('Hogar'),
('Deportes'),
('Juguetes'),
('Libros'),
('Ropa'),
('Alimentos'),
('Bebidas'),
('Salud'),
('Automóviles');

INSERT INTO productos (nombre, descripcion, precio, imagen, descuento, categoria_id) VALUES
('Smartphone', 'Teléfono de última generación', 700, 'img1.jpg', 10, 1),
('Laptop', 'Computadora portátil', 1200, 'img2.jpg', 15, 1),
('Sofá', 'Sofá cómodo de 3 plazas', 500, 'img3.jpg', 20, 2),
('Bicicleta', 'Bicicleta de montaña', 300, 'img4.jpg', 5, 3),
('Muñeca', 'Muñeca de colección', 40, 'img5.jpg', 10, 4),
('Libro', 'Novela bestseller', 15, 'img6.jpg', 0, 5),
('Camisa', 'Camisa formal', 30, 'img7.jpg', 25, 6),
('Pizza', 'Pizza grande de pepperoni', 10, 'img8.jpg', 0, 7),
('Cerveza', 'Paquete de 6 cervezas', 12, 'img9.jpg', 5, 8),
('Vitaminas', 'Complemento vitamínico', 20, 'img10.jpg', 10, 9);

INSERT INTO inventarios (cantidad_disponible, referencia_compra, producto_id) VALUES
(50, 'REF001', 1),
(30, 'REF002', 2),
(20, 'REF003', 3),
(40, 'REF004', 4),
(100, 'REF005', 5),
(200, 'REF006', 6),
(300, 'REF007', 7),
(150, 'REF008', 8),
(80, 'REF009', 9),
(60, 'REF010', 10);

INSERT INTO carritos (cantidad, total) VALUES
(1, 700),
(2, 1400),
(1, 500),
(3, 900),
(1, 40),
(4, 60),
(2, 60),
(6, 72),
(1, 20),
(5, 100);

INSERT INTO ventas (carrito_id, producto_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8),
(9, 9),
(10, 10);

INSERT INTO facturas (codigo, fecha, subtotal, total, impuesto, estado, cliente_id, carrito_id) VALUES
('FAC001', '2024-11-01', 700, 770, 70, 'PAGADA', 1, 1),
('FAC002', '2024-11-02', 1400, 1540, 140, 'PENDIENTE', 2, 2),
('FAC003', '2024-11-03', 500, 550, 50, 'PAGADA', 3, 3),
('FAC004', '2024-11-04', 900, 990, 90, 'ANULADA', 4, 4),
('FAC005', '2024-11-05', 40, 44, 4, 'PAGADA', 5, 5),
('FAC006', '2024-11-06', 60, 66, 6, 'PENDIENTE', 6, 6),
('FAC007', '2024-11-07', 60, 66, 6, 'ANULADA', 7, 7),
('FAC008', '2024-11-08', 72, 79, 7, 'PAGADA', 8, 8),
('FAC009', '2024-11-09', 20, 22, 2, 'PENDIENTE', 9, 9),
('FAC010', '2024-11-10', 100, 110, 10, 'PAGADA', 10, 10);

INSERT INTO puntos_redimidos (cantidad, fecha_redencion, usuario_id) VALUES
(10, '2024-11-01', 1),
(20, '2024-11-02', 2),
(5, '2024-11-03', 3),
(15, '2024-11-04', 4),
(25, '2024-11-05', 5),
(10, '2024-11-06', 6),
(30, '2024-11-07', 7),
(5, '2024-11-08', 8),
(20, '2024-11-09', 9),
(15, '2024-11-10', 10);

INSERT INTO puntos_ganados (cantidad, fecha_ganacia, motivo, referencia, usuario_id) VALUES
(10, '2024-11-01', 'Compra', 'FAC001', 1),
(20, '2024-11-02', 'Promoción', 'FAC002', 2),
(5, '2024-11-03', 'Compra', 'FAC003', 3),
(15, '2024-11-04', 'Promoción', 'FAC004', 4),
(25, '2024-11-05', 'Compra', 'FAC005', 5),
(10, '2024-11-06', 'Promoción', 'FAC006', 6),
(30, '2024-11-07', 'Compra', 'FAC007', 7),
(5, '2024-11-08', 'Promoción', 'FAC008', 8),
(20, '2024-11-09', 'Compra', 'FAC009', 9),
(15, '2024-11-10', 'Promoción', 'FAC010', 10);

INSERT INTO informes (tipo, fecha, datos_json) VALUES
('VENTAS', '2024-11-01', '{"ventas_totales": 770, "productos_vendidos": 1}'),
('INVENTARIO', '2024-11-02', '{"producto_id": 2, "stock_restante": 30}'),
('USUARIOS', '2024-11-03', '{"nuevos_usuarios": 5, "total_usuarios": 10}'),
('VENTAS', '2024-11-04', '{"ventas_totales": 990, "productos_vendidos": 3}'),
('INVENTARIO', '2024-11-05', '{"producto_id": 3, "stock_restante": 20}'),
('USUARIOS', '2024-11-06', '{"nuevos_usuarios": 3, "total_usuarios": 13}'),
('VENTAS', '2024-11-07', '{"ventas_totales": 66, "productos_vendidos": 2}'),
('INVENTARIO', '2024-11-08', '{"producto_id": 8, "stock_restante": 150}'),
('USUARIOS', '2024-11-09', '{"nuevos_usuarios": 4, "total_usuarios": 17}'),
('VENTAS', '2024-11-10', '{"ventas_totales": 110, "productos_vendidos": 5}');

INSERT INTO documentos_auditoria (fecha, cantidad, total, producto_id, cliente_id) VALUES
('2024-11-01', 1, 770, 1, 1),
('2024-11-02', 2, 1540, 2, 2),
('2024-11-03', 3, 550, 3, 3),
('2024-11-04', 4, 990, 4, 4),
('2024-11-05', 1, 44, 5, 5),
('2024-11-06', 6, 66, 6, 6),
('2024-11-07', 2, 66, 7, 7),
('2024-11-08', 6, 79, 8, 8),
('2024-11-09', 1, 22, 9, 9),
('2024-11-10', 5, 110, 10, 10);
