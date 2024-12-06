--Creación de entidades
create type estado_factura as enum('PAGADA', 'PENDIENTE', 'ANULADA');
create type tipo_informe as enum('USUARIOS', 'INVENTARIO', 'VENTAS');
CREATE EXTENSION IF NOT EXISTS xml2;


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

create sequence descuentoDia
start with 1
increment by 1
no maxvalue;

create sequence historialPuntos
start with 1
increment by 1
no maxvalue;

create sequence historialCompras
start with 1
increment by 1
no maxvalue;

CREATE TABLE sesiones_usuario (
    numero_documento VARCHAR PRIMARY KEY,
    fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_actividad TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


create table usuarios (
    id int primary key default nextval('usuarioSecuencia'), 
    numero_documento varchar(15) unique not null,
    nombre varchar(50) not null,
    contrasenia varchar(50) not null,
    email varchar(100) unique not null,
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
    precio decimal not null,
    imagen varchar(100),
    descuento int,
    categoria_id int references categorias(id)
);

create table inventarios(
	id int primary key default nextval('inventarioSecuencia'),
	cantidad_disponible int,
	referencia_compra varchar(30) unique not null,
	producto_id int references productos(id)
);


alter table carritos add column total_efectivo numeric default 0;
alter table carritos add column total_antes_pago numeric default 0;



create table carritos(
	id int primary key default nextval('carritoSecuencia'),
	cantidad int,
	total numeric,
	usuario_id INT REFERENCES compraya.usuarios(id) UNIQUE
);

select * from ventas;

create table ventas(
	id int primary key default nextval('ventaSecuencia'),
	cantidad INT DEFAULT 0,
	carrito_id int references carritos(id),
	producto_id int references productos(id)
);

CREATE TABLE compraya.facturas (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(30) UNIQUE NOT NULL,
    fecha DATE,
    subtotal NUMERIC,
    total NUMERIC,
    impuesto NUMERIC,
    estado VARCHAR(20),
    cliente_id INT REFERENCES compraya.usuarios(id),
    carrito_id INT REFERENCES compraya.carritos(id)
);

ALTER TABLE compraya.facturas
ADD COLUMN impuesto NUMERIC;


create table puntos_redimidos(
	id int primary key default nextval('puntosRedimidosSecuencia'),
	cantidad int,
	fecha_redencion date,
	usuario_id int references usuarios(id)
);

create table puntos_ganados(
	id int primary key default nextval('puntosGanadosSecuencia'),
	cantidad int,
	fecha_ganacia date,
	motivo varchar(50) not null,
	referencia varchar(20),
	usuario_id int references usuarios(id)
);


create table informes (
    id int primary key default nextval('informeSecuencia'),
    tipo tipo_informe not null,
    fecha date not null,
    datos_json json not null
);

CREATE TABLE descuentos_dia (
    id int primary key default nextval('descuentoDia'),
    producto_id INT REFERENCES productos(id),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    descuento_porcentaje INT NOT NULL CHECK (descuento_porcentaje >= 0 AND descuento_porcentaje <= 100)
);

CREATE TABLE historial_puntos (
    id int primary key default nextval('historialPuntos'),
    usuario_id INT NOT NULL REFERENCES usuarios(id),
    cantidad INT NOT NULL,
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    motivo VARCHAR(100) NOT NULL,
    venta_id INT REFERENCES ventas(id)
);

CREATE TABLE historial_compras (
    id int primary key default nextval('historialCompras'),
    cliente_id INT NOT NULL REFERENCES usuarios(id),
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    total_efectivo NUMERIC NOT NULL,
    puntos_redimidos INT,
    carrito_id INT NOT NULL REFERENCES carritos(id),
    factura_id INT NOT NULL REFERENCES facturas(id)
);

CREATE TABLE auditorias (
    id SERIAL PRIMARY KEY,
    accion VARCHAR(50) NOT NULL,
    usuario_id INT NOT NULL,
    factura_id INT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    detalle TEXT
);


----------------------------------------------------Funcionalidades----------------------------------------------------
----------------------------------- Usuarios -----------------------------------

----------Crear usuario (register)----------
CREATE OR REPLACE PROCEDURE crear_usuario(
    numero_documento_input VARCHAR,
    nombre_input VARCHAR,
    contrasenia_input VARCHAR,
    email_input VARCHAR,
    celular_input VARCHAR,
    puntos_input INT DEFAULT 0,
    rol_input INT DEFAULT 0 
)
LANGUAGE plpgsql AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM compraya.usuarios WHERE numero_documento = numero_documento_input
    ) THEN
        RAISE EXCEPTION 'Error: El número de documento % ya está registrado.', numero_documento_input;
    END IF;

    IF EXISTS (
        SELECT 1 FROM compraya.usuarios WHERE email = email_input
    ) THEN
        RAISE EXCEPTION 'Error: El correo electrónico % ya está registrado.', email_input;
    END IF;

    INSERT INTO compraya.usuarios (
        numero_documento, nombre, contrasenia, email, celular, puntos, rol
    ) VALUES (
        numero_documento_input, nombre_input, contrasenia_input, email_input, celular_input, puntos_input, rol_input
    );

    RAISE NOTICE 'Usuario creado exitosamente: %', nombre_input;
END;
$$;


----------Login----------

CREATE OR REPLACE FUNCTION login_usuario(email_input VARCHAR, contrasenia_input VARCHAR)
RETURNS VARCHAR AS $$
DECLARE
    numero_documento_actual VARCHAR;
    contrasenia_actual VARCHAR;
BEGIN
    -- Verificar si el usuario existe
    SELECT contrasenia, numero_documento
    INTO contrasenia_actual, numero_documento_actual
    FROM compraya.usuarios
    WHERE email = email_input;

    -- Si no se encuentra el usuario, lanzar una excepción
    IF contrasenia_actual IS NULL THEN
        RAISE EXCEPTION 'El usuario con el email % no existe', email_input;
    END IF;

    -- Verificar si la contraseña es correcta
    IF contrasenia_actual = contrasenia_input THEN
        -- Llamar al procedimiento para iniciar sesión
        CALL compraya.iniciar_sesion(numero_documento_actual);

        -- Retornar el número de documento para la sesión
        RETURN numero_documento_actual;
    ELSE
        -- Si la contraseña es incorrecta, lanzar una excepción
        RAISE EXCEPTION 'Contraseña incorrecta para el usuario %', email_input;
    END IF;
END;
$$ LANGUAGE plpgsql;



----------Establecer sesión----------

CREATE OR REPLACE PROCEDURE iniciar_sesion(
    _numero_documento VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    -- Insertar o actualizar la sesión del usuario
    INSERT INTO compraya.sesiones_usuario (numero_documento, fecha_inicio, ultima_actividad)
    VALUES (_numero_documento, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (numero_documento)
    DO UPDATE SET ultima_actividad = CURRENT_TIMESTAMP;
END;
$$;


----------¿Usuario logueado?----------

CREATE OR REPLACE FUNCTION es_usuario_logueado()
RETURNS BOOLEAN AS $$
DECLARE
    numero_documento_actual VARCHAR;
BEGIN
    -- Verifica si existe una sesión activa en la tabla sesiones_usuario
    SELECT numero_documento INTO numero_documento_actual
    FROM compraya.sesiones_usuario
    LIMIT 1;

    RETURN numero_documento_actual IS NOT NULL;
END;
$$ LANGUAGE plpgsql;

----------Rol del usuario logueado----------

CREATE OR REPLACE FUNCTION obtener_rol_usuario()
RETURNS INTEGER AS $$
DECLARE
    numero_documento_actual VARCHAR;
    rol_usuario_actual INTEGER;
BEGIN
    -- Recuperar el número de documento del usuario desde la tabla de sesiones
    SELECT numero_documento INTO numero_documento_actual
    FROM compraya.sesiones_usuario
    LIMIT 1;

    IF numero_documento_actual IS NULL THEN
        RAISE EXCEPTION 'Error: No hay un usuario logueado.';
    END IF;

    -- Obtener el rol del usuario
    SELECT rol INTO rol_usuario_actual
    FROM compraya.usuarios
    WHERE numero_documento = numero_documento_actual;

    RETURN rol_usuario_actual;
END;
$$ LANGUAGE plpgsql;



----------Actualizar usuario----------

CREATE OR REPLACE PROCEDURE modificar_usuario(
    usuario_id_input INT,
    numero_documento_input VARCHAR DEFAULT NULL,
    nombre_input VARCHAR DEFAULT NULL,
    contrasenia_input VARCHAR DEFAULT NULL,
    email_input VARCHAR DEFAULT NULL,
    celular_input VARCHAR DEFAULT NULL,
    puntos_input INT DEFAULT NULL,
    rol_input INT DEFAULT NULL
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT es_usuario_logueado() THEN
        RAISE EXCEPTION 'Error: No hay un usuario logueado.';
    END IF;

    IF obtener_rol_usuario() != 1 THEN
        RAISE EXCEPTION 'Error: No tienes permisos para modificar usuarios.';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM compraya.usuario WHERE id = usuario_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El usuario con ID % no existe.', usuario_id_input;
    END IF;

    IF numero_documento_input IS NOT NULL AND EXISTS (
        SELECT 1 FROM compraya.usuarios 
        WHERE numero_documento = numero_documento_input AND id != usuario_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El número de documento % ya está registrado para otro usuario.', numero_documento_input;
    END IF;

    IF email_input IS NOT NULL AND EXISTS (
        SELECT 1 FROM compraya.usuarios 
        WHERE email = email_input AND id != usuario_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El correo electrónico % ya está registrado para otro usuario.', email_input;
    END IF;

    UPDATE compraya.usuarios
    SET 
        numero_documento = COALESCE(numero_documento_input, numero_documento),
        nombre = COALESCE(nombre_input, nombre),
        contrasenia = COALESCE(contrasenia_input, contrasenia),
        email = COALESCE(email_input, email),
        celular = COALESCE(celular_input, celular),
        puntos = COALESCE(puntos_input, puntos),
        rol = COALESCE(rol_input, rol)
    WHERE id = usuario_id_input;

    RAISE NOTICE 'Usuario con ID % modificado exitosamente.', usuario_id_input;
END;
$$;

----------Modificar usuario logueado----------
CREATE OR REPLACE PROCEDURE modificar_usuario_logueado(
    numero_documento_input VARCHAR DEFAULT NULL,
    nombre_input VARCHAR DEFAULT NULL,
    contrasenia_input VARCHAR DEFAULT NULL,
    email_input VARCHAR DEFAULT NULL,
    celular_input VARCHAR DEFAULT NULL,
    puntos_input INT DEFAULT NULL,
    rol_input INT DEFAULT NULL
)
LANGUAGE plpgsql AS $$
DECLARE
    usuario_id INT;
BEGIN
    -- Obtener el número de documento del usuario logueado
    SELECT numero_documento INTO numero_documento_input
    FROM compraya.sesiones_usuario
    ORDER BY ultima_actividad DESC
    LIMIT 1;  -- Obtenemos el último usuario logueado

    -- Verificar si se encontró un número de documento (es decir, si hay una sesión activa)
    IF numero_documento_input IS NULL THEN
        RAISE EXCEPTION 'Error: No hay un usuario logueado.';
    END IF;

    -- Obtener el usuario_id a partir del numero_documento
    SELECT id INTO usuario_id
    FROM compraya.usuarios
    WHERE numero_documento = numero_documento_input;

    -- Verificar si el usuario existe
    IF usuario_id IS NULL THEN
        RAISE EXCEPTION 'Error: Usuario no encontrado.';
    END IF;

    -- Actualizar los datos del usuario
    UPDATE compraya.usuarios
    SET 
        numero_documento = COALESCE(numero_documento_input, numero_documento),
        nombre = COALESCE(nombre_input, nombre),
        contrasenia = COALESCE(contrasenia_input, contrasenia),
        email = COALESCE(email_input, email),
        celular = COALESCE(celular_input, celular),
        puntos = COALESCE(puntos_input, puntos),
        rol = COALESCE(rol_input, rol)
    WHERE id = usuario_id;

    RAISE NOTICE 'Usuario con ID % modificado exitosamente.', usuario_id;
END;
$$;



----------Eliminar usuario----------

CREATE OR REPLACE PROCEDURE eliminar_usuario(usuario_id_input INT)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM compraya.usuarios WHERE id = usuario_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El usuario con ID % no existe.', usuario_id_input;
    END IF;

    DELETE FROM compraya.usuarios
    WHERE id = usuario_id_input;

    RAISE NOTICE 'Usuario con ID % eliminado exitosamente.', usuario_id_input;
END;
$$;

----------Eliminar usuario logueado----------

CREATE OR REPLACE PROCEDURE eliminar_usuario_logueado(numero_documento_input VARCHAR)
LANGUAGE plpgsql AS $$
DECLARE
    usuario_logueado_id INT;
    numero_documento_logueado VARCHAR;
    v_carrito_id INT;
BEGIN
    -- Obtener el número de documento del usuario logueado
    SELECT numero_documento INTO numero_documento_logueado
    FROM compraya.sesiones_usuario
    ORDER BY ultima_actividad DESC
    LIMIT 1;

    -- Verificar si hay un usuario logueado
    IF numero_documento_logueado IS NULL THEN
        RAISE EXCEPTION 'Error: No hay un usuario logueado.';
    END IF;

    -- Verificar si el número de documento del usuario logueado es el mismo que el proporcionado
    IF numero_documento_logueado != numero_documento_input THEN
        RAISE EXCEPTION 'Error: No puedes eliminar a otro usuario. Solo puedes eliminar tu propia cuenta.';
    END IF;

    -- Verificar si el usuario con ese número de documento existe
    IF NOT EXISTS (
        SELECT 1 FROM compraya.usuarios WHERE numero_documento = numero_documento_input
    ) THEN
        RAISE EXCEPTION 'Error: El usuario con número de documento % no existe.', numero_documento_input;
    END IF;

    -- Obtener el id del carrito del usuario
    SELECT c.id INTO v_carrito_id
    FROM compraya.carritos c
    WHERE c.usuario_id = (SELECT u.id FROM compraya.usuarios u WHERE u.numero_documento = numero_documento_input)
    LIMIT 1;

    -- Verificar si existe el carrito
    IF v_carrito_id IS NOT NULL THEN
        -- Eliminar las ventas asociadas al carrito
        DELETE FROM compraya.ventas v WHERE v.carrito_id = v_carrito_id;

        -- Eliminar el carrito
        DELETE FROM compraya.carritos c WHERE c.id = v_carrito_id;
    END IF;

    -- Eliminar al usuario
    DELETE FROM compraya.usuarios u WHERE u.numero_documento = numero_documento_input;

    RAISE NOTICE 'Usuario con número de documento % eliminado exitosamente.', numero_documento_input;
END;
$$;


----------Obtener todos los usuarios----------

CREATE OR REPLACE FUNCTION obtener_todos_los_usuarios()
RETURNS TABLE (
    id INT,
    numero_documento VARCHAR,
    nombre VARCHAR,
    email VARCHAR,
    celular VARCHAR,
    puntos INT
) AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        u.id, 
        u.numero_documento, 
        u.nombre, 
        u.email, 
        u.celular, 
        u.puntos
    FROM compraya.usuarios u;
END;
$$ LANGUAGE plpgsql;




----------------------------------- Productos -----------------------------------

----------Crear producto (Solo administrador)----------

CREATE OR REPLACE PROCEDURE crear_producto(
    nombre_input VARCHAR,
    descripcion_input VARCHAR,
    precio_input DECIMAL,
    imagen_input VARCHAR,
    descuento_input INT,
    categoria_id_input INT,
    cantidad_inventario_input INT  -- Nuevo parámetro para la cantidad de inventario
)
LANGUAGE plpgsql AS $$
DECLARE
    nuevo_producto_id INT;  -- Variable para almacenar el ID del nuevo producto
BEGIN
    -- Verificar si hay un usuario logueado
    IF NOT compraya.es_usuario_logueado() THEN
        RAISE EXCEPTION 'Error: No hay un usuario logueado.';
    END IF;

    -- Verificar si el usuario tiene permisos (rol 1: administrador)
    IF compraya.obtener_rol_usuario() != 1 THEN
        RAISE EXCEPTION 'Error: No tienes permisos para crear productos.';
    END IF;

    -- Verificar si el producto ya existe en la base de datos
    IF EXISTS (
        SELECT 1 FROM compraya.productos WHERE nombre = nombre_input
    ) THEN
        RAISE EXCEPTION 'Error: El producto con nombre % ya está registrado.', nombre_input;
    END IF;

    -- Verificar si la categoría existe
    IF NOT EXISTS (
        SELECT 1 FROM compraya.categorias WHERE id = categoria_id_input
    ) THEN
        RAISE EXCEPTION 'Error: La categoría con ID % no existe.', categoria_id_input;
    END IF;

    -- Insertar el nuevo producto en la tabla 'productos' y obtener su ID
    INSERT INTO compraya.productos (
        nombre, descripcion, precio, imagen, descuento, categoria_id
    ) 
    VALUES (
        nombre_input, descripcion_input, precio_input, imagen_input, descuento_input, categoria_id_input
    )
    RETURNING id INTO nuevo_producto_id;  -- Obtener el ID del nuevo producto

    -- Insertar el inventario para el nuevo producto
    INSERT INTO compraya.inventarios (
        cantidad_disponible, referencia_compra, producto_id
    )
    VALUES (
        cantidad_inventario_input, 'REF_' || nuevo_producto_id, nuevo_producto_id
    );

    -- Notificar que el producto se ha creado exitosamente
    RAISE NOTICE 'Producto % creado exitosamente con inventario.', nombre_input;
END;
$$;





----------Actualizar producto (Solo administrador)----------

CREATE OR REPLACE PROCEDURE modificar_producto(IN producto_id_input integer, IN nombre_input character varying DEFAULT NULL::character varying, IN descripcion_input character varying DEFAULT NULL::character varying, IN precio_input numeric DEFAULT NULL::numeric, IN imagen_input character varying DEFAULT NULL::character varying, IN descuento_input integer DEFAULT NULL::integer, IN categoria_id_input integer DEFAULT NULL::integer)
 LANGUAGE plpgsql
AS $procedure$
BEGIN
    IF NOT compraya.es_usuario_logueado() THEN
        RAISE EXCEPTION 'Error: No hay un usuario logueado.';
    END IF;

    IF compraya.obtener_rol_usuario() != 1 THEN
        RAISE EXCEPTION 'Error: No tienes permisos para modificar productos.';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM compraya.productos WHERE id = producto_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El producto con ID % no existe.', producto_id_input;
    END IF;

    IF categoria_id_input IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM compraya.categorias WHERE id = categoria_id_input
    ) THEN
        RAISE EXCEPTION 'Error: La categoría con ID % no existe.', categoria_id_input;
    END IF;

    UPDATE compraya.productos
    SET
        nombre = COALESCE(nombre_input, nombre),
        descripcion = COALESCE(descripcion_input, descripcion),
        precio = COALESCE(precio_input, precio),
        imagen = COALESCE(imagen_input, imagen),
        descuento = COALESCE(descuento_input, descuento),
        categoria_id = COALESCE(categoria_id_input, categoria_id)
    WHERE id = producto_id_input;

    RAISE NOTICE 'Producto con ID % modificado exitosamente.', producto_id_input;
END;
$procedure$;


----------Eliminar producto (Solo administrador)----------

CREATE OR REPLACE PROCEDURE eliminar_producto(IN producto_id_input INTEGER)
LANGUAGE plpgsql AS $$
BEGIN
    -- Verificar si hay un usuario logueado
    IF NOT compraya.es_usuario_logueado() THEN
        RAISE EXCEPTION 'Error: No hay un usuario logueado.';
    END IF;

    -- Verificar si el usuario tiene el rol adecuado
    IF compraya.obtener_rol_usuario() != 1 THEN
        RAISE EXCEPTION 'Error: No tienes permisos para eliminar productos.';
    END IF;

    -- Verificar si el producto existe
    IF NOT EXISTS (
        SELECT 1 FROM compraya.productos WHERE id = producto_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El producto con ID % no existe.', producto_id_input;
    END IF;

    -- Eliminar el producto
    DELETE FROM compraya.productos
    WHERE id = producto_id_input;

    RAISE NOTICE 'Producto con ID % eliminado exitosamente.', producto_id_input;
END;
$$;


----------Obtener todos los productos----------

CREATE OR REPLACE FUNCTION compraya.obtener_todos_los_productos()
 RETURNS SETOF compraya.productos
 LANGUAGE plpgsql
AS $function$ 
DECLARE
    producto_cursor CURSOR FOR
        SELECT id, nombre, descripcion, precio, imagen, descuento, categoria_id
        FROM compraya.productos;
    producto_record compraya.productos%ROWTYPE;
BEGIN
    OPEN producto_cursor;

    LOOP
        FETCH producto_cursor INTO producto_record;
        EXIT WHEN NOT FOUND;

        -- Retorna cada fila una a una
        RETURN NEXT producto_record;
    END LOOP;

    CLOSE producto_cursor;
    RETURN;
END;
$function$;

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
    FROM compraya.productos p
    WHERE 
        (categoria_id_input IS NULL OR p.categoria_id = categoria_id_input) AND
        (precio_min IS NULL OR p.precio >= precio_min) AND
        (precio_max IS NULL OR p.precio <= precio_max) AND
        (descuento_min IS NULL OR p.descuento >= descuento_min) AND
        (descuento_max IS NULL OR p.descuento <= descuento_max);
END;
$$ LANGUAGE plpgsql;



----------Agregar un descuento a un producto----------

CREATE OR REPLACE PROCEDURE agregar_descuento_dia(
    producto_id_input INT,
    fecha_inicio_input DATE,
    fecha_fin_input DATE,
    descuento_porcentaje_input INT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM compraya.productos WHERE id = producto_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El producto con ID % no existe.', producto_id_input;
    END IF;

    IF fecha_inicio_input > fecha_fin_input THEN
        RAISE EXCEPTION 'Error: La fecha de inicio no puede ser mayor que la fecha de fin.';
    END IF;

    INSERT INTO compraya.descuentos_dia (producto_id, fecha_inicio, fecha_fin, descuento_porcentaje)
    VALUES (producto_id_input, fecha_inicio_input, fecha_fin_input, descuento_porcentaje_input);

    RAISE NOTICE 'Descuento del % % agregado para el producto con ID % entre % y %.',
        descuento_porcentaje_input, '%', producto_id_input, fecha_inicio_input, fecha_fin_input;
END;
$$;


----------Productos que tengan descuento x fecha----------

CREATE OR REPLACE FUNCTION obtener_productos_descuento(fecha_actual date)
 RETURNS TABLE(id integer, nombre character varying, descripcion character varying, precio_original numeric, precio_descuento numeric, descuento_aplicado integer)
 LANGUAGE plpgsql
AS $function$
BEGIN
    RETURN QUERY
    SELECT 
        p.id,
        p.nombre,
        p.descripcion,
        p.precio AS precio_original,
        p.precio - (p.precio * d.descuento_porcentaje / 100) AS precio_descuento,
        d.descuento_porcentaje AS descuento_aplicado
    FROM compraya.productos p
    JOIN compraya.descuentos_dia d ON p.id = d.producto_id
    WHERE fecha_actual BETWEEN d.fecha_inicio AND d.fecha_fin;
END;
$function$;

----------Productos más vendidos----------

CREATE OR REPLACE FUNCTION obtener_productos_mas_vendidos()
RETURNS TABLE(
    producto_id INT,
    nombre VARCHAR,
    cantidad_vendida INT  
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.id AS producto_id,
        p.nombre,
        COUNT(v.producto_id)::INT AS cantidad_vendida 
    FROM 
        compraya.productos p
    JOIN 
        compraya.ventas v ON p.id = v.producto_id
    GROUP BY 
        p.id, p.nombre
    ORDER BY 
        cantidad_vendida DESC;
END;
$$ LANGUAGE plpgsql;


----------------------------------- Inventarios -----------------------------------
----------Crear inventario----------
CREATE OR REPLACE PROCEDURE crear_inventario(
    producto_id_input INT,
    cantidad_disponible_input INT,
    referencia_compra_input VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM compraya.productos WHERE id = producto_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El producto con ID % no existe.', producto_id_input;
    END IF;

    INSERT INTO compraya.inventarios (producto_id, cantidad_disponible, referencia_compra)
    VALUES (producto_id_input, cantidad_disponible_input, referencia_compra_input);

    RAISE NOTICE 'Inventario creado exitosamente para el producto con ID %.', producto_id_input;
END;
$$;

----------Actualizar cantidad inventario----------
CREATE OR REPLACE PROCEDURE actualizar_inventario(
    producto_id_input INT,
    cantidad_disponible_input INT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM compraya.inventarios WHERE producto_id = producto_id_input
    ) THEN
        RAISE EXCEPTION 'Error: No existe inventario para el producto con ID %.', producto_id_input;
    END IF;

    UPDATE compraya.inventarios
    SET cantidad_disponible = cantidad_disponible_input
    WHERE producto_id = producto_id_input;

    RAISE NOTICE 'Cantidad de inventario actualizada para el producto con ID %.', producto_id_input;
END;
$$;

----------Consultar inventario----------
CREATE OR REPLACE FUNCTION consultar_inventario(producto_id_input INT)
RETURNS TABLE(id INT, cantidad_disponible INT, referencia_compra VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT
        inventarios.id,  -- Aseguramos que se refiere a la columna id de la tabla 'inventarios'
        inventarios.cantidad_disponible,
        inventarios.referencia_compra
    FROM compraya.inventarios
    WHERE inventarios.producto_id = producto_id_input;
END;
$$ LANGUAGE plpgsql;


----------Reducir inventario----------
CREATE OR REPLACE PROCEDURE reducir_inventario(
    producto_id_input INT,
    cantidad_a_reducir INT
)
LANGUAGE plpgsql AS $$
DECLARE
    cantidad_actual INT;
BEGIN
    SELECT cantidad_disponible INTO cantidad_actual
    FROM compraya.inventarios
    WHERE producto_id = producto_id_input;

    IF cantidad_actual IS NULL THEN
        RAISE EXCEPTION 'Error: No existe inventario para el producto con ID %.', producto_id_input;
    END IF;

    IF cantidad_actual < cantidad_a_reducir THEN
        RAISE EXCEPTION 'Error: Cantidad insuficiente en inventario. Disponible: %, Solicitado: %.',
            cantidad_actual, cantidad_a_reducir;
    END IF;

    UPDATE compraya.inventarios
    SET cantidad_disponible = cantidad_actual - cantidad_a_reducir
    WHERE producto_id = producto_id_input;

    RAISE NOTICE 'Inventario actualizado. Nueva cantidad disponible: %', cantidad_actual - cantidad_a_reducir;
END;
$$;

----------Eliminar inventario----------
CREATE OR REPLACE PROCEDURE eliminar_inventario(producto_id_input INT)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM compraya.inventarios WHERE producto_id = producto_id_input
    ) THEN
        RAISE EXCEPTION 'Error: No existe inventario para el producto con ID %.', producto_id_input;
    END IF;

    DELETE FROM compraya.inventarios
    WHERE producto_id = producto_id_input;

    RAISE NOTICE 'Inventario eliminado para el producto con ID %.', producto_id_input;
END;
$$;

----------Verificar disponibilidad----------
CREATE OR REPLACE FUNCTION verificar_disponibilidad(producto_id_input INT, cantidad_solicitada INT)
RETURNS BOOLEAN AS $$
DECLARE
    cantidad_disponible INT;
BEGIN
    -- Obtener la cantidad disponible del producto en inventario
    SELECT inventarios.cantidad_disponible
    INTO cantidad_disponible
    FROM compraya.inventarios
    WHERE inventarios.producto_id = producto_id_input;

    -- Verificar si hay suficiente cantidad
    IF cantidad_disponible >= cantidad_solicitada THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;



----------------------------------- Venta -----------------------------------

----------Crear venta----------

CREATE OR REPLACE PROCEDURE crear_venta(
    p_carrito_id INT,
    p_producto_id INT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM carrito WHERE id = p_carrito_id) THEN
        RAISE EXCEPTION 'El carrito con ID % no existe.', p_carrito_id;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM producto WHERE id = p_producto_id) THEN
        RAISE EXCEPTION 'El producto con ID % no existe.', p_producto_id;
    END IF;

    INSERT INTO compraya.ventas (carrito_id, producto_id)
    VALUES (p_carrito_id, p_producto_id);
END;
$$;


----------Actualizar una venta----------

CREATE OR REPLACE PROCEDURE actualizar_venta(
    p_venta_id INT,
    p_carrito_id INT,
    p_producto_id INT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM venta WHERE id = p_venta_id) THEN
        RAISE EXCEPTION 'La venta con ID % no existe.', p_venta_id;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM carrito WHERE id = p_carrito_id) THEN
        RAISE EXCEPTION 'El carrito con ID % no existe.', p_carrito_id;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM producto WHERE id = p_producto_id) THEN
        RAISE EXCEPTION 'El producto con ID % no existe.', p_producto_id;
    END IF;

    UPDATE compraya.ventas
    SET carrito_id = p_carrito_id,
        producto_id = p_producto_id
    WHERE id = p_venta_id;
END;
$$;


----------Eliminar una venta----------

CREATE OR REPLACE PROCEDURE eliminar_venta(
    p_venta_id INT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM venta WHERE id = p_venta_id) THEN
        RAISE EXCEPTION 'La venta con ID % no existe.', p_venta_id;
    END IF;

    DELETE FROM compraya.ventas WHERE id = p_venta_id;
END;
$$;

----------Obtener ventas----------

CREATE OR REPLACE FUNCTION listar_ventas(p_carrito_id INT DEFAULT NULL)
RETURNS TABLE(
    venta_id INT,
    carrito_id INT,
    producto_id INT,
    producto_nombre VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        v.id AS venta_id,
        v.carrito_id,
        v.producto_id,
        p.nombre AS producto_nombre
    FROM 
        compraya.ventas v
    JOIN 
        producto p ON v.producto_id = p.id
    WHERE 
        p_carrito_id IS NULL OR v.carrito_id = p_carrito_id;
END;
$$ LANGUAGE plpgsql;


----------Crear una factura en el momento en que se haga una venta----------


CREATE OR REPLACE FUNCTION generar_factura_venta(p_carrito_id INT, p_cliente_id INT)
RETURNS TRIGGER AS $$
DECLARE
    nuevo_carrito RECORD;
    subtotal NUMERIC;
    impuesto NUMERIC;
    total NUMERIC;
BEGIN
    -- Seleccionamos el carrito basado en el carrito_id
    SELECT * INTO nuevo_carrito 
    FROM compraya.carritos 
    WHERE id = p_carrito_id;

    IF nuevo_carrito IS NULL THEN
        RAISE EXCEPTION 'Carrito asociado a la venta no encontrado.';
    END IF;

    -- Calculamos el subtotal, impuesto y total
    subtotal := nuevo_carrito.total;
    impuesto := subtotal * 0.19; 
    total := subtotal + impuesto;

    -- Validar cliente_id recibido como parámetro
    IF p_cliente_id IS NULL THEN
        RAISE EXCEPTION 'Cliente no encontrado para el carrito con ID %', p_carrito_id;
    END IF;

    -- Insertar la factura en la tabla facturas
    INSERT INTO compraya.facturas (
        codigo, 
        fecha, 
        subtotal, 
        total, 
        impuesto, 
        estado, 
        cliente_id, 
        carrito_id
    ) VALUES (
        CONCAT('FAC-', nextval('compraya.facturaSecuencia')),
        CURRENT_DATE,
        subtotal,
        total,
        impuesto,
        'pendiente',
        p_cliente_id,  -- Usamos el cliente_id pasado como parámetro
        p_carrito_id
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;







--------------------------------------------------------Realizar pago--------------------------------------------------------
---------------------Solo puntos---------------------

CREATE OR REPLACE PROCEDURE registrar_pago_puntos(
    carrito_id_input INT,
    puntos_usados_input INT
)
LANGUAGE plpgsql AS $$ 
DECLARE
    total_carrito NUMERIC;
    puntos_usuario INT;
    usuario_id INT;
BEGIN
    -- Obtener el total del carrito y el ID del usuario
    SELECT total, usuario_id INTO total_carrito, usuario_id
    FROM compraya.carritos
    WHERE id = carrito_id_input;

    -- Verificar si el carrito existe
    IF total_carrito IS NULL THEN
        RAISE EXCEPTION 'Error: El carrito con ID % no existe.', carrito_id_input;
    END IF;

    -- Obtener los puntos disponibles del usuario
    SELECT puntos INTO puntos_usuario
    FROM compraya.usuarios
    WHERE id = usuario_id;

    -- Validar que el usuario tenga suficientes puntos
    IF puntos_usados_input > puntos_usuario THEN
        RAISE EXCEPTION 'Error: No tienes suficientes puntos para completar el pago.';
    END IF;

    -- Validar que los puntos no sean mayores que el total del carrito
    IF puntos_usados_input > total_carrito THEN
        RAISE EXCEPTION 'Error: Los puntos utilizados no pueden ser mayores al total del carrito.';
    END IF;

    -- Actualizar los puntos del usuario
    UPDATE compraya.usuarios
    SET puntos = puntos - puntos_usados_input
    WHERE id = usuario_id;

    -- Actualizar el estado del carrito a "pagado"
    UPDATE compraya.carritos
    SET total = total_carrito - puntos_usados_input  -- Descontamos los puntos del total
    WHERE id = carrito_id_input;

    -- Vaciar el carrito eliminando los productos
    DELETE FROM compraya.ventas
    WHERE carrito_id = carrito_id_input;

    -- Llamar a la función para generar la factura después de registrar el pago
    PERFORM compraya.generar_factura_venta(carrito_id_input);

    RAISE NOTICE 'Pago registrado exitosamente con puntos para el carrito % y carrito vaciado. Factura generada.', carrito_id_input;
END;
$$;





---------------------Solo efectivo---------------------
CREATE OR REPLACE PROCEDURE registrar_pago_efectivo(
    carrito_id_input INT,
    efectivo_usado_input NUMERIC
)
LANGUAGE plpgsql AS $$ 
DECLARE
    total_carrito NUMERIC;
BEGIN
    -- Obtener el total del carrito
    SELECT total INTO total_carrito
    FROM compraya.carritos
    WHERE id = carrito_id_input;

    -- Verificar si el carrito existe
    IF total_carrito IS NULL THEN
        RAISE EXCEPTION 'Error: El carrito con ID % no existe.', carrito_id_input;
    END IF;

    -- Validar que el monto de efectivo cubra el total del carrito
    IF efectivo_usado_input != total_carrito THEN
        RAISE EXCEPTION 'Error: El monto de efectivo no coincide con el total del carrito.';
    END IF;

    -- Guardar el total del carrito antes del pago en la columna total_antes_pago
    UPDATE compraya.carritos
    SET total_antes_pago = total,  -- Guardamos el total original
        total = 0,  -- El total del carrito se pone a 0 después de realizar el pago
        total_efectivo = efectivo_usado_input  -- Registramos el monto del pago en efectivo
    WHERE id = carrito_id_input;

    -- Vaciar el carrito eliminando los productos
    DELETE FROM compraya.ventas
    WHERE carrito_id = carrito_id_input;

    -- Llamar a la función de generar la factura
    PERFORM compraya.generar_factura_venta(carrito_id_input);

    RAISE NOTICE 'Pago registrado exitosamente con efectivo para el carrito % y carrito vaciado.', carrito_id_input;
END;
$$;





---------------------Ambos---------------------
CREATE OR REPLACE PROCEDURE registrar_pago_mixto(
    carrito_id_input INT,
    puntos_usados_input INT,
    efectivo_usado_input NUMERIC
)
LANGUAGE plpgsql AS $$ 
DECLARE
    total_carrito NUMERIC;
    puntos_usuario INT;
    usuario_id INT;
BEGIN
    -- Obtener el total del carrito y el ID del usuario
    SELECT total, usuario_id INTO total_carrito, usuario_id
    FROM compraya.carritos
    WHERE id = carrito_id_input;

    -- Verificar si el carrito existe
    IF total_carrito IS NULL THEN
        RAISE EXCEPTION 'Error: El carrito con ID % no existe.', carrito_id_input;
    END IF;

    -- Obtener los puntos disponibles del usuario
    SELECT puntos INTO puntos_usuario
    FROM compraya.usuarios
    WHERE id = usuario_id;

    -- Validar que la suma de puntos y efectivo cubra el total del carrito
    IF puntos_usados_input + efectivo_usado_input != total_carrito THEN
        RAISE EXCEPTION 'Error: El total de puntos y efectivo no coincide con el monto del carrito.';
    END IF;

    -- Validar que los puntos no sean mayores que el total del carrito
    IF puntos_usados_input > total_carrito THEN
        RAISE EXCEPTION 'Error: Los puntos utilizados no pueden ser mayores al total del carrito.';
    END IF;

    -- Actualizar los puntos del usuario
    UPDATE compraya.usuarios
    SET puntos = puntos - puntos_usados_input
    WHERE id = usuario_id;

    -- Actualizar el estado del carrito a "pagado"
    UPDATE compraya.carritos
    SET total = total_carrito - puntos_usados_input  -- Descontamos los puntos del total
    WHERE id = carrito_id_input;

    -- Vaciar el carrito eliminando los productos
    DELETE FROM compraya.ventas
    WHERE carrito_id = carrito_id_input;

    -- Llamar a la función para generar la factura después de registrar el pago
    PERFORM compraya.generar_factura_venta(carrito_id_input);

    RAISE NOTICE 'Pago registrado exitosamente con puntos y efectivo para el carrito % y carrito vaciado. Factura generada.', carrito_id_input;
END;
$$;


----------------------------------- Carrito -----------------------------------
----------Crear carrito----------

CREATE OR REPLACE PROCEDURE crear_carrito()
LANGUAGE plpgsql
AS $$
DECLARE
    carrito_existente INT;
    numero_documento VARCHAR;
    usuario_id INT;
BEGIN
    -- Obtener el número de documento del usuario logueado
    SELECT numero_documento INTO numero_documento
    FROM sesiones_usuario
    ORDER BY ultima_actividad DESC
    LIMIT 1;  -- Obtenemos el último usuario logueado

    -- Verificar si se encontró un número de documento (es decir, si hay una sesión activa)
    IF numero_documento IS NULL THEN
        RAISE EXCEPTION 'Error: No hay un usuario logueado.';
    END IF;

    -- Obtener el usuario_id a partir del numero_documento
    SELECT id INTO usuario_id
    FROM compraya.usuarios
    WHERE numero_documento = numero_documento;

    -- Verificar si el usuario existe
    IF usuario_id IS NULL THEN
        RAISE EXCEPTION 'Error: Usuario no encontrado.';
    END IF;

    -- Verificar si el usuario ya tiene un carrito
    SELECT id INTO carrito_existente
    FROM compraya.carritos
    WHERE usuario_id = usuario_id;

    IF carrito_existente IS NOT NULL THEN
        RAISE NOTICE 'El usuario con número de documento % ya tiene un carrito (Carrito ID: %)', numero_documento, carrito_existente;
    ELSE
        -- Crear un nuevo carrito para el usuario con total_efectivo y total_antes_pago a 0
        INSERT INTO compraya.carritos (cantidad, total, usuario_id, total_efectivo, total_antes_pago)
        VALUES (0, 0, usuario_id, 0, 0);

        RAISE NOTICE 'Carrito creado exitosamente para el usuario con número de documento %', numero_documento;
    END IF;
END;
$$;


CREATE OR REPLACE FUNCTION crear_carrito_automatically()
RETURNS TRIGGER AS $$
BEGIN
    CALL compraya.crear_carrito(NEW.id);

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


----------Agregar producto al carrito----------

CREATE OR REPLACE PROCEDURE agregar_producto_al_carrito(
    p_usuario_id INT,
    p_producto_id INT,
    p_cantidad INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_carrito_id INT;
    v_producto_en_carrito INT;
BEGIN
    -- Paso 1: Buscar el carrito asociado al usuario
    SELECT id INTO v_carrito_id
    FROM compraya.carritos
    WHERE usuario_id = p_usuario_id;

    -- Paso 2: Si no existe carrito para este usuario, creamos uno
    IF NOT FOUND THEN
        INSERT INTO compraya.carritos (usuario_id, cantidad, total)
        VALUES (p_usuario_id, 0, 0)
        RETURNING id INTO v_carrito_id;
    END IF;

    -- Paso 3: Verificar si el producto ya está en el carrito
    SELECT COUNT(*) INTO v_producto_en_carrito
    FROM compraya.ventas
    WHERE carrito_id = v_carrito_id
    AND producto_id = p_producto_id;

    -- Paso 4: Si el producto ya está en el carrito, actualizamos la cantidad
    IF v_producto_en_carrito > 0 THEN
        UPDATE compraya.ventas
        SET cantidad = cantidad + p_cantidad
        WHERE carrito_id = v_carrito_id
        AND producto_id = p_producto_id;
    ELSE
        -- Si no está en el carrito, agregamos el producto con la cantidad
        INSERT INTO compraya.ventas (carrito_id, producto_id, cantidad)
        VALUES (v_carrito_id, p_producto_id, p_cantidad);
    END IF;

    -- Paso 5: Actualizar el total del carrito
    UPDATE compraya.carritos
    SET total = (
        SELECT COALESCE(SUM(p.precio * v.cantidad), 0) 
        FROM compraya.ventas v
        JOIN compraya.productos p ON v.producto_id = p.id
        WHERE v.carrito_id = v_carrito_id
    )
    WHERE id = v_carrito_id;

    -- Paso 6: Actualizar la cantidad total en la tabla carritos
    UPDATE compraya.carritos
    SET cantidad = (
        SELECT COALESCE(SUM(v.cantidad), 0) 
        FROM compraya.ventas v
        WHERE v.carrito_id = v_carrito_id
    )
    WHERE id = v_carrito_id;

    COMMIT;
END;
$$;



----------Eliminar producto del carrito----------

CREATE OR REPLACE PROCEDURE eliminar_producto_del_carrito(
    p_carrito_id INT,
    p_producto_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Eliminar el producto del carrito
    DELETE FROM compraya.ventas
    WHERE carrito_id = p_carrito_id
    AND producto_id = p_producto_id;

    -- Actualizar el total del carrito y las columnas adicionales
    UPDATE compraya.carritos
    SET total = (SELECT SUM(p.precio * v.cantidad) 
                 FROM compraya.ventas v
                 JOIN compraya.productos p ON v.producto_id = p.id
                 WHERE v.carrito_id = p_carrito_id),
        total_antes_pago = (SELECT SUM(p.precio * v.cantidad) 
                            FROM compraya.ventas v
                            JOIN compraya.productos p ON v.producto_id = p.id
                            WHERE v.carrito_id = p_carrito_id)
    WHERE id = p_carrito_id;
    
    COMMIT;
END;
$$;


----------Ver productos del carrito----------

CREATE OR REPLACE FUNCTION obtener_productos_en_carrito(p_carrito_id INT)
RETURNS TABLE (
    producto_id INT,
    nombre_producto VARCHAR,
    cantidad INT,
    total NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        v.producto_id,
        p.nombre AS nombre_producto,
        c.cantidad,
        c.total
    FROM 
        compraya.ventas v
    JOIN 
        compraya.productos p ON v.producto_id = p.id
    JOIN 
        compraya.carritos c ON v.carrito_id = c.id
    WHERE 
        v.carrito_id = p_carrito_id;
END;
$$ LANGUAGE plpgsql;


----------Vaciar carrito----------

CREATE OR REPLACE PROCEDURE vaciar_carrito(
    p_carrito_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM compraya.ventas WHERE carrito_id = p_carrito_id;

    UPDATE compraya.carritos
    SET total = 0
    WHERE id = p_carrito_id;

    COMMIT;
END;
$$;


----------------------------------- Historiales -----------------------------------
----Punto 13----

CREATE OR REPLACE FUNCTION trigger_historial_compras()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO compraya.historial_compras (cliente_id, fecha, total_efectivo, puntos_redimidos, carrito_id, factura_id)
    VALUES (
        NEW.cliente_id,
        NEW.fecha,
        NEW.total,  -- El total de la factura
        0,  -- Si no estás usando puntos, puedes ponerlo a 0
        NEW.carrito_id,
        NEW.id
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION compraya.trigger_historial_puntos_redimidos()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
BEGIN
    -- Si la tabla origen no tiene `venta_id`, omítelo
    INSERT INTO compraya.historial_puntos (usuario_id, cantidad, fecha, motivo)
    VALUES (
        NEW.usuario_id,
        NEW.cantidad,
        NEW.fecha_redencion,  -- O `fecha_redencion` dependiendo de la tabla
		''    
);
    RETURN NEW;
END;
$function$
;


CREATE OR REPLACE FUNCTION compraya.mostrar_historial_puntos(id_usuario_p INT)
 RETURNS TABLE(usuario_id integer, cantidad integer, fecha date, motivo character varying, venta_id integer)
 LANGUAGE plpgsql
AS $function$
BEGIN
    RETURN QUERY
    SELECT hp.usuario_id, hp.cantidad, hp.fecha, hp.motivo, hp.venta_id
    FROM compraya.historial_puntos hp
	where hp.usuario_id = id_usuario_p
    ORDER BY hp.fecha DESC;
END;
$function$
;



----Punto 15----

CREATE OR REPLACE FUNCTION trigger_historial_compras()
RETURNS TRIGGER AS $$
DECLARE
    total_efectivo NUMERIC := 0;
    puntos_redimidos INT := 0;
BEGIN
    -- Aquí obtenemos el monto pagado en efectivo y los puntos redimidos directamente del carrito
    SELECT total INTO total_efectivo
    FROM compraya.carritos
    WHERE id = NEW.carrito_id;

    -- Obtener los puntos redimidos desde la tabla carritos
    SELECT puntos INTO puntos_redimidos
    FROM compraya.carritos
    WHERE id = NEW.carrito_id;

    -- Insertamos los datos en la tabla historial_compras
    INSERT INTO compraya.historial_compras (
        cliente_id, 
        fecha, 
        total_efectivo, 
        puntos_redimidos, 
        carrito_id, 
        factura_id
    )
    VALUES (
        NEW.cliente_id, 
        NEW.fecha, 
        total_efectivo,  -- Usamos el valor calculado para el pago en efectivo
        puntos_redimidos,  -- Usamos el valor calculado para los puntos redimidos
        NEW.carrito_id,
        NEW.id
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION mostrar_historial_compras(p_cliente_id INT)
RETURNS TABLE (
    id INT,
    fecha DATE,
    total_efectivo NUMERIC,
    puntos_redimidos INT,
    carrito_id INT,
    factura_id INT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        hc.id,
        hc.fecha,
        hc.total_efectivo,
        hc.puntos_redimidos,
        hc.carrito_id,
        hc.factura_id
    FROM compraya.historial_compras hc
    WHERE hc.cliente_id = p_cliente_id
    ORDER BY hc.fecha DESC;
END;
$$ LANGUAGE plpgsql;


----------------------------------- Informe pdf y excel -----------------------------------

CREATE OR REPLACE FUNCTION obtener_informe_compras_puntos(usuario_id_input INT)
RETURNS TABLE (
    fecha DATE,
    total_efectivo NUMERIC,
    puntos_redimidos INT,
    puntos_acumulados INT,
    motivo VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        hc.fecha,
        hc.total_efectivo,
        hc.puntos_redimidos,
        COALESCE(pg.cantidad, 0) AS puntos_acumulados,
        pg.motivo
    FROM compraya.historial_compras hc
    LEFT JOIN compraya.historial_puntos pg ON hc.cliente_id = pg.usuario_id AND hc.factura_id = pg.venta_id
    WHERE hc.cliente_id = usuario_id_input
    ORDER BY hc.fecha DESC;
END;
$$ LANGUAGE plpgsql;


----------------------------------- Auditoría -----------------------------------

----------Crear auditoría después de una factura----------

CREATE OR REPLACE FUNCTION registrar_auditoria_factura()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO compraya.auditorias (accion, usuario_id, factura_id, detalle)
    VALUES (
        'CREACIÓN DE FACTURA', 
        NEW.cliente_id,  -- Asumiendo que cliente_id es el usuario que crea la factura
        NEW.id,  -- ID de la nueva factura
        'Factura generada con un total de: ' || NEW.total
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

----------Búsqueda de auditoría por usuario y producto----------

CREATE OR REPLACE FUNCTION buscar_auditoria_por_usuario_producto(
    nombre_usuario_input VARCHAR,
    nombre_producto_input VARCHAR
)
RETURNS TABLE (
    auditoria_id INT,
    accion VARCHAR,
    usuario_id INT,
    factura_id INT,
    fecha TIMESTAMP,
    detalle TEXT,
    nombre_usuario VARCHAR,
    nombre_producto VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        a.id AS auditoria_id,
        a.accion,
        a.usuario_id,
        a.factura_id,
        a.fecha,
        a.detalle,
        u.nombre AS nombre_usuario,
        p.nombre AS nombre_producto
    FROM compraya.auditorias a
    LEFT JOIN usuarios u ON a.usuario_id = u.id
    LEFT JOIN facturas f ON a.factura_id = f.id
    LEFT JOIN ventas v ON f.carrito_id = v.carrito_id
    LEFT JOIN productos p ON v.producto_id = p.id
    WHERE
        (nombre_usuario_input IS NULL OR u.nombre ILIKE '%' || nombre_usuario_input || '%')
        AND (nombre_producto_input IS NULL OR p.nombre ILIKE '%' || nombre_producto_input || '%')
    ORDER BY a.fecha DESC;
END;
$$ LANGUAGE plpgsql;

----------Todas las auditorías----------

CREATE OR REPLACE PROCEDURE ver_todas_auditorias()
LANGUAGE plpgsql AS $$
BEGIN
    -- Imprimir todas las auditorías de la tabla
    RAISE NOTICE 'Listado de todas las auditorías:';
    
    -- Seleccionar todas las auditorías
    FOR record IN
        SELECT id, accion, usuario_id, factura_id, fecha, detalle
        FROM compraya.auditorias
    LOOP
        -- Mostrar los resultados uno por uno
        RAISE NOTICE 'ID: %, Acción: %, Usuario ID: %, Factura ID: %, Fecha: %, Detalle: %', 
            record.id, record.accion, record.usuario_id, record.factura_id, record.fecha, record.detalle;
    END LOOP;
END;
$$;



----------Triggers----------
----Trigger 1----
CREATE TRIGGER crear_factura_tras_venta
AFTER INSERT ON compraya.ventas
FOR EACH ROW
EXECUTE FUNCTION generar_factura_venta();




----Punto 13----
CREATE TRIGGER trigger_puntos_ganados
AFTER INSERT ON compraya.puntos_ganados
FOR EACH ROW
EXECUTE FUNCTION trigger_historial_puntos_ganados();

CREATE TRIGGER trigger_puntos_redimidos
AFTER INSERT ON compraya.puntos_redimidos
FOR EACH ROW
EXECUTE FUNCTION trigger_historial_puntos_redimidos();


----Punto 15----

CREATE TRIGGER trigger_facturas
AFTER INSERT ON compraya.facturas
FOR EACH ROW
EXECUTE FUNCTION trigger_historial_compras();

----Punto 18----
CREATE TRIGGER trigger_auditoria_factura
AFTER INSERT ON compraya.facturas
FOR EACH ROW
EXECUTE FUNCTION registrar_auditoria_factura();

--Crear carrito después de un register--
CREATE TRIGGER trigger_crear_carrito
AFTER INSERT ON compraya.usuarios
FOR EACH ROW
EXECUTE FUNCTION compraya.crear_carrito_automatically();





----------------------------------- Factura -----------------------------------
----------XML----------

CREATE OR REPLACE PROCEDURE generar_factura_xml(
    p_factura_id INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_factura RECORD;
    v_productos RECORD;
    v_xml XML;
BEGIN
    -- Obtener los datos de la factura
    SELECT f.codigo, f.fecha, f.subtotal, f.total, f.impuesto, f.estado, u.nombre
    INTO v_factura
    FROM compraya.facturas f
    JOIN usuarios u ON f.cliente_id = u.id
    WHERE f.id = p_factura_id;

    -- Iniciar el XML
    v_xml := '<?xml version="1.0" encoding="UTF-8"?>' ||
             '<factura>' ||
             '<codigo>' || v_factura.codigo || '</codigo>' ||
             '<fecha>' || v_factura.fecha || '</fecha>' ||
             '<subtotal>' || v_factura.subtotal || '</subtotal>' ||
             '<total>' || v_factura.total || '</total>' ||
             '<impuesto>' || v_factura.impuesto || '</impuesto>' ||
             '<estado>' || v_factura.estado || '</estado>' ||
             '<cliente>' ||
             '<nombre>' || v_factura.nombre || '</nombre>' ||
             '</cliente>' ||
             '<productos>';

    -- Obtener los productos asociados a la factura
    FOR v_productos IN
        SELECT p.nombre, v.cantidad, p.precio, (p.precio * v.cantidad) AS total_producto
        FROM compraya.ventas v
        JOIN productos p ON v.producto_id = p.id
        WHERE v.carrito_id = (SELECT carrito_id FROM facturas WHERE id = p_factura_id)
    LOOP
        v_xml := v_xml ||
                 '<producto>' ||
                 '<nombre>' || v_productos.nombre || '</nombre>' ||
                 '<cantidad>' || v_productos.cantidad || '</cantidad>' ||
                 '<precio>' || v_productos.precio || '</precio>' ||
                 '<total>' || v_productos.total_producto || '</total>' ||
                 '</producto>';
    END LOOP;

    -- Cerrar el XML
    v_xml := v_xml || '</productos>' || '</factura>';

    -- Guardar el archivo XML en el sistema de archivos o retornar como texto
    -- Para guardar en el sistema de archivos, sería necesario un procedimiento específico
    -- que dependa de los permisos y configuraciones del servidor de base de datos.
    -- En este caso, simplemente retornamos el XML como texto.

    RAISE NOTICE '%', v_xml;  -- Aquí puedes manejar el XML como desees, por ejemplo, devolverlo o almacenarlo en un archivo.

    -- Opcionalmente, almacenar el XML en alguna tabla para rastrear las facturas generadas
    -- INSERT INTO historial_facturas (factura_id, xml) VALUES (p_factura_id, v_xml);

END;
$$;

select * from carritos c ;

update carritos set usuario_id = 2 where id = 2;




-- informes
CREATE OR REPLACE PROCEDURE guardar_historial_puntos_json(id_usuario_p INT)
LANGUAGE plpgsql
AS $$
DECLARE
    historial RECORD;
    datos_json JSON;
BEGIN
    -- Validar si el usuario existe antes de proceder
    IF NOT EXISTS (SELECT 1 FROM compraya.usuarios WHERE id = id_usuario_p) THEN
        RAISE EXCEPTION 'El usuario con ID % no existe.', id_usuario_p;
    END IF;

    -- Iterar sobre los datos del historial de puntos del usuario
    FOR historial IN 
        SELECT usuario_id, cantidad, fecha, motivo, venta_id
        FROM compraya.historial_puntos
        WHERE usuario_id = id_usuario_p
    LOOP
        -- Crear el objeto JSON con los datos obtenidos
        datos_json := json_build_object(
            'usuario_id', historial.usuario_id,
            'cantidad', historial.cantidad,
            'fecha', historial.fecha,
            'motivo', historial.motivo,
            'venta_id', historial.venta_id
        );

        -- Insertar el JSON en la tabla informes
        INSERT INTO compraya.informes (tipo, fecha, datos_json)
        VALUES ('USUARIOS', CURRENT_DATE, datos_json);
    END LOOP;

    -- Confirmar éxito (opcional)
    RAISE NOTICE 'Historial de puntos guardado correctamente para usuario con ID %', id_usuario_p;
END;
$$;


CREATE OR REPLACE PROCEDURE guardar_historial_compras_json(id_usuario_p INT)
LANGUAGE plpgsql
AS $$
DECLARE
    compra RECORD;
    datos_json JSON;
BEGIN
    -- Validar si el usuario existe antes de proceder
    IF NOT EXISTS (SELECT 1 FROM compraya.usuarios WHERE id = id_usuario_p) THEN
        RAISE EXCEPTION 'El usuario con ID % no existe.', id_usuario_p;
    END IF;

    -- Iterar sobre los datos del historial de compras del usuario
    FOR compra IN 
        SELECT cliente_id, fecha, total_efectivo, puntos_redimidos, carrito_id, factura_id
        FROM compraya.historial_compras
        WHERE cliente_id = id_usuario_p
    LOOP
        -- Crear el objeto JSON con los datos obtenidos
        datos_json := json_build_object(
            'cliente_id', compra.cliente_id,
            'fecha', compra.fecha,
            'total_efectivo', compra.total_efectivo,
            'puntos_redimidos', compra.puntos_redimidos,
            'carrito_id', compra.carrito_id,
            'factura_id', compra.factura_id
        );

        -- Insertar el JSON en la tabla informes
        INSERT INTO compraya.informes (tipo, fecha, datos_json)
        VALUES ('INVENTARIO', CURRENT_DATE, datos_json);
    END LOOP;

    -- Confirmar éxito (opcional)
    RAISE NOTICE 'Historial de compras guardado correctamente para usuario con ID %', id_usuario_p;
END;
$$;


CALL guardar_historial_compras_json(1); -- Cambia 1 por el ID del usuario correspondiente.




CREATE OR REPLACE FUNCTION mostrar_informes_creados()
RETURNS TABLE(id INT, tipo tipo_informe, fecha DATE, datos_json JSONB)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        informes.id,       
        informes.tipo,
        informes.fecha,
        informes.datos_json::JSONB  -- Convertir JSON a JSONB
    FROM compraya.informes;
END;
$$;




SELECT * FROM mostrar_informes_creados();




------------Registros para pruebitas------------

INSERT INTO usuarios (numero_documento, nombre, contrasenia, email, celular, puntos, rol) VALUES
('1234567890', 'Juan Perez', 'password1', 'juan.perez@gmail.com', '3001234567', 50, 0),
('0987654321', 'Maria Gomez', 'password2', 'maria.gomez@gmail.com', '3107654321', 40, 0),
('1122334455', 'Luis Alvarez', 'password3', 'luis.alvarez@gmail.com', '3201122334', 60, 0),
('5566778899', 'Ana Morales', 'password4', 'ana.morales@gmail.com', '3015566778', 30, 0),
('3344556677', 'Carlos Diaz', 'password5', 'carlos.diaz@gmail.com', '3113344556', 70, 0),
('2233445566', 'Laura Lopez', 'password6', 'laura.lopez@gmail.com', '3022233445', 80, 0),
('9988776655', 'Felipe Herrera', 'password7', 'felipe.herrera@gmail.com', '3129988776', 90, 0),
('8877665544', 'Sofia Martinez', 'password8', 'sofia.martinez@gmail.com', '3038877665', 20, 0),
('7766554433', 'Andres Torres', 'password9', 'andres.torres@gmail.com', '3137766554', 10, 0),
('1', 'admin', '12345678', 'admin@gmail.com', '305', 999999, 1);

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

drop table usuarios;
drop table facturas;
drop table puntos_redimidos ;
drop table puntos_ganados ;

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


CALL crear_usuario(
    '5544332211', 
    'Pedro Ramirez', 
    'password10', 
    'pedro.ramirez@gmail.com', 
    '3045544332', 
    25
);

select login_usuario(
	'pedro.ramirez@gmail.com',
	'password10'
);

select * from usuarios;
select * from productos;
select * from inventarios;
select * from carritos;
select * from categorias;
select * from ventas;
select * from facturas;


drop table productos;
drop table inventarios;
drop table ventas;
drop table historial_puntos;
drop table descuentos_dia;

INSERT INTO compraya.inventarios (cantidad_disponible, referencia_compra, producto_id) 
VALUES 
(ROUND(random() * 100) + 1, 'REF_' || 2 || '_', 2),
(ROUND(random() * 100) + 1, 'REF_' || 14 || '_', 14),
(ROUND(random() * 100) + 1, 'REF_' || 15 || '_', 15),
(ROUND(random() * 100) + 1, 'REF_' || 16 || '_', 16),
(ROUND(random() * 100) + 1, 'REF_' || 17 || '_', 17),
(ROUND(random() * 100) + 1, 'REF_' || 20 || '_', 20),
(ROUND(random() * 100) + 1, 'REF_' || 21 || '_', 21),
(ROUND(random() * 100) + 1, 'REF_' || 22 || '_', 22);


ALTER TABLE compraya.carritos
ADD COLUMN total_efectivo NUMERIC DEFAULT 0;

SELECT * FROM consultar_inventario(14);
