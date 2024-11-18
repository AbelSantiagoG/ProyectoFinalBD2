--Creación de entidades
create type estado as enum('pagada', 'pendiente', 'en proceso');
create type tipo_informe as enum('mensual', 'semanal', 'diario', 'anual', 'personalizado');
create type tipo_movimiento as enum('compra', 'venta');
create type tipo_pago as enum('efectivo', 'tarjeta');


create table departamentos (
    id serial primary key,
    nombre varchar(50) not null
);

create table ciudades (
    id serial primary key,
    nombre varchar(50) not null,
    departamento_id int references departamentos(id)
);


create table clientes (
    id serial primary key,
    numero_documento varchar(15) unique not null,
    nombre varchar(50) not null,
    apellidos varchar(50) not null,
    direccion varchar(100) not null,
    telefono varchar(20),
    ciudad_id int references ciudades(id)
);


create table impuestos (
    id serial primary key,
    nombre varchar(50) not null,
    porcentaje numeric not null check (porcentaje >= 0)
);

create table categorias (
    id serial primary key,
    nombre varchar(50) not null unique,
    descripcion varchar(100)
);

create table productos (
    id serial primary key,
    codigo varchar(20) unique not null,
    descripcion varchar(100),
    precio_venta numeric not null,
    medida varchar(50),
    stock int,
    impuesto_id int references impuestos(id),
    categoria_id int references categorias(id)
);


create table producto_impuestos (
    producto_id int references productos(id) on delete cascade,
    impuesto_id int references impuestos(id) on delete cascade,
    primary key (producto_id, impuesto_id)
);


create table facturas (
    id serial primary key,
    fecha date not null,
    subtotal numeric,
    total_impuestos numeric,
    total numeric,
    estado estado not null,
    metodo_pago tipo_pago not null,
    cliente_id int references clientes(id)
);

create table detalle_facturas (
    id serial primary key,
    cantidad int not null,
    valor_total numeric not null,
    descuento numeric,
    producto_id int references productos(id),
    factura_id int references facturas(id)
);


create table inventarios (
    id serial primary key,
    fecha date not null,
    movimiento tipo_movimiento not null,
    entrada numeric,
    salida numeric,
    observaciones varchar(100),
    producto_id int references productos(id)
);


create table informes (
    id serial primary key,
    tipo tipo_informe not null,
    fecha date not null,
    datos_json json not null
);

create table documento_auditorias (
    id serial primary key,
    fecha date not null,
    cantidad int not null,
    total numeric not null,
    producto_id int references productos(id),
    cliente_id int references clientes(id)
);

-----------------------------------------Funcionalidades-----------------------------------------
------------#1 Clientes------------

CREATE OR REPLACE PROCEDURE crear_cliente(
    p_documento varchar,
    p_nombre varchar,
    p_apellidos varchar,
    p_direccion varchar,
    p_telefono varchar,
    p_ciudad_id int
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO clientes (numero_documento, nombre, apellidos, direccion, telefono, ciudad_id) 
	VALUES (p_documento, p_nombre, p_apellidos, p_direccion, p_telefono, p_ciudad_id);
END;
$$;

CREATE OR REPLACE PROCEDURE modificar_cliente(
    p_id int,
    p_documento varchar,
    p_nombre varchar,
    p_apellidos varchar,
    p_direccion varchar,
    p_telefono varchar,
    p_ciudad_id int
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE clientes
    SET 
        numero_documento = p_documento,
        nombre = p_nombre,
        apellidos = p_apellidos,
        direccion = p_direccion,
        telefono = p_telefono,
        ciudad_id = p_ciudad_id
    WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE eliminar_cliente(
    p_id int
)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM clientes
    WHERE id = p_id;
END;
$$;

------------#2 Productos------------

CREATE OR REPLACE PROCEDURE crear_producto(
    p_codigo varchar,
    p_descripcion varchar,
    p_precio_venta numeric,
    p_medida varchar,
    p_stock int,
    p_impuesto_id int,
    p_categoria_id int
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO productos (codigo, descripcion, precio_venta, medida, stock, impuesto_id, categoria_id) 
	VALUES (p_codigo, p_descripcion, p_precio_venta, p_medida, p_stock, p_impuesto_id, p_categoria_id);
END;
$$;

CREATE OR REPLACE PROCEDURE modificar_producto(
    p_id int,
    p_codigo varchar,
    p_descripcion varchar,
    p_precio_venta numeric,
    p_medida varchar,
    p_stock int,
    p_impuesto_id int,
    p_categoria_id int
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE productos
    SET 
        codigo = p_codigo,
        descripcion = p_descripcion,
        precio_venta = p_precio_venta,
        medida = p_medida,
        stock = p_stock,
        impuesto_id = p_impuesto_id,
        categoria_id = p_categoria_id
    WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE eliminar_producto(
    p_id int
)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM productos
    WHERE id = p_id;
END;
$$;

--#2.1 Productos --> categorías

CREATE OR REPLACE FUNCTION productos_por_categoria(
    p_categoria_id int
)
RETURNS TABLE (
    producto_id int,
    codigo varchar,
    descripcion varchar,
    precio_venta numeric,
    medida varchar,
    stock int
) 
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        id, codigo, descripcion, precio_venta, medida, stock
    FROM productos
    WHERE categoria_id = p_categoria_id;
END;
$$;

--#2.2 Productos --> stock

CREATE OR REPLACE PROCEDURE agregar_stock(
    p_producto_id int,
    p_cantidad int
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE productos
    SET stock = stock + p_cantidad
    WHERE id = p_producto_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'El producto con ID % no existe', p_producto_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE quitar_stock(
    p_producto_id int,
    p_cantidad int
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE productos
    SET stock = stock - p_cantidad
    WHERE id = p_producto_id AND stock >= p_cantidad;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'El producto con ID % no existe o no hay suficiente stock', p_producto_id;
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION consultar_stock(
    p_producto_id int
)
RETURNS int
LANGUAGE plpgsql
AS $$
DECLARE
    v_stock int;
BEGIN
    SELECT stock INTO v_stock FROM productos WHERE id = p_producto_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'El producto con ID % no existe.', p_producto_id;
    END IF;

    RETURN v_stock;
END;
$$;

--#2.3 Productos --> búsqueda

CREATE OR REPLACE FUNCTION productos_por_nombre(
    p_nombre varchar
)
RETURNS TABLE (
    producto_id int,
    codigo varchar,
    descripcion varchar,
    precio_venta numeric,
    medida varchar,
    stock int,
    categoria_nombre varchar
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        p.id AS producto_id,
        p.codigo,
        p.descripcion,
        p.precio_venta,
        p.medida,
        p.stock,
        c.nombre AS categoria_nombre
    FROM productos p
    LEFT JOIN categorias c ON p.categoria_id = c.id
    WHERE p.descripcion ILIKE '%' || p_nombre || '%';
END;
$$;

CREATE OR REPLACE FUNCTION productos_por_codigo(
    p_codigo varchar
)
RETURNS TABLE (
    producto_id int,
    codigo varchar,
    descripcion varchar,
    precio_venta numeric,
    medida varchar,
    stock int,
    categoria_nombre varchar
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        p.id AS producto_id,
        p.codigo,
        p.descripcion,
        p.precio_venta,
        p.medida,
        p.stock,
        c.nombre AS categoria_nombre
    FROM productos p
    LEFT JOIN categorias c ON p.categoria_id = c.id
    WHERE p.codigo ILIKE '%' || p_codigo || '%';
END;
$$;

CREATE OR REPLACE FUNCTION productos_por_categoria(
    p_categoria_id int
)
RETURNS TABLE (
    producto_id int,
    codigo varchar,
    descripcion varchar,
    precio_venta numeric,
    medida varchar,
    stock int,
    categoria_nombre varchar
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        p.id AS producto_id,
        p.codigo,
        p.descripcion,
        p.precio_venta,
        p.medida,
        p.stock,
        c.nombre AS categoria_nombre
    FROM productos p
    LEFT JOIN categorias c ON p.categoria_id = c.id
    WHERE p.categoria_id = p_categoria_id;
END;
$$;

------------#3 Impuestos------------

CREATE OR REPLACE PROCEDURE crear_impuesto(
    p_nombre varchar,
    p_porcentaje numeric
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO impuestos (nombre, porcentaje)
    VALUES (p_nombre, p_porcentaje);
END;
$$;

CREATE OR REPLACE PROCEDURE modificar_impuesto(
    p_id int,
    p_nombre varchar,
    p_porcentaje numeric
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE impuestos
    SET nombre = p_nombre,
        porcentaje = p_porcentaje
    WHERE id = p_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'El impuesto con ID % no existe', p_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE eliminar_impuesto(
    p_id int
)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM impuestos
    WHERE id = p_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'El impuesto con ID % no existe', p_id;
    END IF;
END;
$$;

