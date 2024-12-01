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
    precio numeric not null,
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

create table carritos(
	id int primary key default nextval('carritoSecuencia'),
	cantidad int,
	total numeric
);


create table ventas(
	id int primary key default nextval('ventaSecuencia'),
	carrito_id int references carritos(id),
	producto_id int references productos(id)
);



create table facturas(
	id int primary key default nextval('facturaSecuencia'),
	codigo varchar(30) unique not null,
	fecha date,
	subtotal int,
	total int,
	impuesto int,
	estado estado_factura,
	cliente_id int references usuarios(id),
	carrito_id int references carritos(id)
);

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
    producto_id INT REFERENCES producto(id),
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
----------------------------------------------------Funcionalidades----------------------------------------------------
----------------------------------- Usuarios -----------------------------------

----------Crear usuario----------

----------Crear usuario----------
CREATE OR REPLACE PROCEDURE crear_usuario(
    numero_documento_input VARCHAR,
    nombre_input VARCHAR,
    contrasenia_input VARCHAR,
    email_input VARCHAR,
    celular_input VARCHAR,
    puntos_input INT DEFAULT 0,
    rol_input int 
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

    INSERT INTO usuarios (
        numero_documento, nombre, contrasenia, email, celular, puntos, rol
    ) VALUES (
        numero_documento_input, nombre_input, contrasenia_input, email_input, celular_input, puntos_input, rol_input
    );

    RAISE NOTICE 'Usuario creado exitosamente: %', nombre_input;
END;
$$;

----------Login----------

CREATE OR REPLACE FUNCTION login_usuario(email_input VARCHAR, contrasenia_input VARCHAR)
RETURNS TEXT AS $$
DECLARE
    contrasenia_actual VARCHAR;
BEGIN
    SELECT contrasenia INTO contrasenia_actual
    FROM usuarios
    WHERE email = email_input;

    IF contrasenia_actual IS NULL THEN
        RETURN 'El usuario no existe';
    ELSIF contrasenia_actual = contrasenia_input THEN
        RETURN 'Login exitoso';
    ELSE
        RETURN 'Contraseña incorrecta';
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
    puntos_input INT,
    rol_input int
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM compraya.usuario WHERE id = usuario_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El usuario con ID % no existe.', usuario_id_input;
    END IF;

    IF EXISTS (
        SELECT 1 FROM compraya.usuario 
        WHERE numero_documento = numero_documento_input AND id != usuario_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El número de documento % ya está registrado para otro usuario.', numero_documento_input;
    END IF;

    IF EXISTS (
        SELECT 1 FROM compraya.usuario 
        WHERE email = email_input AND id != usuario_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El correo electrónico % ya está registrado para otro usuario.', email_input;
    END IF;

    UPDATE compraya.usuario
    SET 
        numero_documento = numero_documento_input,
        nombre = nombre_input,
        contrasenia = contrasenia_input,
        email = email_input,
        celular = celular_input,
        puntos = puntos_input,
        rol = rol_input	
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

----------Obtener todos los usuarios----------

CREATE OR REPLACE PROCEDURE obtener_todos_los_usuarios()
LANGUAGE plpgsql AS $$
DECLARE
    usuario_record RECORD;
BEGIN
    FOR usuario_record IN 
        SELECT id, numero_documento, nombre, email, celular, puntos
        FROM usuario
    LOOP
        RAISE NOTICE 'ID: %, Documento: %, Nombre: %, Email: %, Celular: %, Puntos: %',
            usuario_record.id,
            usuario_record.numero_documento,
            usuario_record.nombre,
            usuario_record.email,
            usuario_record.celular,
            usuario_record.puntos;
    END LOOP;
END;
$$;



----------------------------------- Productos -----------------------------------

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

----------Obtener todos los productos----------

CREATE OR REPLACE PROCEDURE obtener_todos_los_productos()
LANGUAGE plpgsql AS $$
DECLARE
    producto_record RECORD;
BEGIN
    FOR producto_record IN 
        SELECT id, nombre, descripcion, precio, imagen, descuento, categoria_id
        FROM producto
    LOOP
        RAISE NOTICE 'ID: %, Nombre: %, Descripción: %, Precio: %, Imagen: %, Descuento: %, Categoría ID: %',
            producto_record.id,
            producto_record.nombre,
            producto_record.descripcion,
            producto_record.precio,
            producto_record.imagen,
            producto_record.descuento,
            producto_record.categoria_id;
    END LOOP;
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
        SELECT 1 FROM producto WHERE id = producto_id_input
    ) THEN
        RAISE EXCEPTION 'Error: El producto con ID % no existe.', producto_id_input;
    END IF;

    IF fecha_inicio_input > fecha_fin_input THEN
        RAISE EXCEPTION 'Error: La fecha de inicio no puede ser mayor que la fecha de fin.';
    END IF;

    INSERT INTO descuento_dia (producto_id, fecha_inicio, fecha_fin, descuento_porcentaje)
    VALUES (producto_id_input, fecha_inicio_input, fecha_fin_input, descuento_porcentaje_input);

    RAISE NOTICE 'Descuento del % % agregado para el producto con ID % entre % y %.',
        descuento_porcentaje_input, '%', producto_id_input, fecha_inicio_input, fecha_fin_input;
END;
$$;


----------Productos que tengan descuento x fecha----------

CREATE OR REPLACE FUNCTION obtener_productos_descuento(fecha_actual DATE)
RETURNS TABLE(
    id INT,
    nombre VARCHAR,
    descripcion VARCHAR,
    precio_original NUMERIC,
    precio_descuento NUMERIC,
    descuento_aplicado INT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.id,
        p.nombre,
        p.descripcion,
        p.precio AS precio_original,
        p.precio - (p.precio * d.descuento_porcentaje / 100) AS precio_descuento,
        d.descuento_porcentaje AS descuento_aplicado
    FROM producto p
    JOIN descuento_dia d ON p.id = d.producto_id
    WHERE fecha_actual BETWEEN d.fecha_inicio AND d.fecha_fin;
END;
$$ LANGUAGE plpgsql;

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
        COUNT(v.producto_id) AS cantidad_vendida
    FROM 
        producto p
    JOIN 
        venta v ON p.id = v.producto_id
    GROUP BY 
        p.id, p.nombre
    ORDER BY 
        cantidad_vendida DESC;
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

    INSERT INTO venta (carrito_id, producto_id)
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

    UPDATE venta
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

    DELETE FROM venta WHERE id = p_venta_id;
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
        venta v
    JOIN 
        producto p ON v.producto_id = p.id
    WHERE 
        p_carrito_id IS NULL OR v.carrito_id = p_carrito_id;
END;
$$ LANGUAGE plpgsql;


----------Crear una factura en el momento en que se haga una venta----------

CREATE OR REPLACE FUNCTION generar_factura_venta()
RETURNS TRIGGER AS $$
DECLARE
    nuevo_carrito RECORD;
    subtotal NUMERIC;
    impuesto NUMERIC;
    total NUMERIC;
BEGIN
    SELECT * INTO nuevo_carrito 
    FROM carrito 
    WHERE id = NEW.carrito_id;

    IF nuevo_carrito IS NULL THEN
        RAISE EXCEPTION 'Carrito asociado a la venta no encontrado.';
    END IF;

    subtotal := nuevo_carrito.total;
    impuesto := subtotal * 0.19; 
    total := subtotal + impuesto;

    INSERT INTO factura (
        codigo, 
        fecha, 
        subtotal, 
        total, 
        impuesto, 
        estado, 
        cliente_id, 
        carrito_id
    ) VALUES (
        CONCAT('FAC-', nextval('facturaSecuencia')),
        CURRENT_DATE,
        subtotal,
        total,
        impuesto,
        'pendiente',
        (SELECT cliente_id FROM carrito WHERE id = NEW.carrito_id), 
        NEW.carrito_id
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

----------------------------------- Carrito -----------------------------------

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
    SELECT id INTO v_carrito_id
    FROM carritos
    WHERE id = p_usuario_id AND carrito_id IS NULL; 

    IF NOT FOUND THEN
        INSERT INTO carritos (usuario_id, cantidad, total)
        VALUES (p_usuario_id, 0, 0)
        RETURNING id INTO v_carrito_id;
    END IF;

    SELECT COUNT(*) INTO v_producto_en_carrito
    FROM ventas
    WHERE carrito_id = v_carrito_id
    AND producto_id = p_producto_id;

    IF v_producto_en_carrito > 0 THEN
        UPDATE ventas
        SET cantidad = cantidad + p_cantidad
        WHERE carrito_id = v_carrito_id
        AND producto_id = p_producto_id;
    ELSE
        INSERT INTO ventas (carrito_id, producto_id, cantidad)
        VALUES (v_carrito_id, p_producto_id, p_cantidad);
    END IF;

    UPDATE carritos
    SET total = (SELECT SUM(p.precio * v.cantidad) FROM ventas v
                 JOIN productos p ON v.producto_id = p.id
                 WHERE v.carrito_id = v_carrito_id)
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
    DELETE FROM ventas
    WHERE carrito_id = p_carrito_id
    AND producto_id = p_producto_id;

    UPDATE carritos
    SET total = (SELECT SUM(p.precio * v.cantidad) FROM ventas v
                 JOIN productos p ON v.producto_id = p.id
                 WHERE v.carrito_id = p_carrito_id)
    WHERE id = p_carrito_id;
    
    COMMIT;
END;
$$;

----------Ver productos del carrito----------

CREATE OR REPLACE FUNCTION obtener_productos_carrito(
    p_carrito_id INT
)
RETURNS TABLE(producto_nombre VARCHAR, cantidad INT, total_producto NUMERIC) AS $$
BEGIN
    RETURN QUERY
    SELECT p.nombre, v.cantidad, p.precio * v.cantidad
    FROM ventas v
    JOIN productos p ON v.producto_id = p.id
    WHERE v.carrito_id = p_carrito_id;
END;
$$ LANGUAGE plpgsql;

----------Vaciar carrito----------

CREATE OR REPLACE PROCEDURE vaciar_carrito(
    p_carrito_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM ventas WHERE carrito_id = p_carrito_id;

    UPDATE carritos
    SET total = 0
    WHERE id = p_carrito_id;

    COMMIT;
END;
$$;


----------------------------------- Historiales -----------------------------------
----Punto 13----

CREATE OR REPLACE FUNCTION trigger_historial_puntos()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO historial_puntos (usuario_id, cantidad, fecha, motivo, venta_id)
    VALUES (
        NEW.usuario_id,
        NEW.cantidad,
        COALESCE(NEW.fecha_ganacia, NEW.fecha_redencion),
        NEW.motivo,
        NULL -- Opcional: referencia a ventas si aplica
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

----Punto 15----

CREATE OR REPLACE FUNCTION trigger_historial_compras()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO historial_compras (cliente_id, fecha, total_efectivo, puntos_redimidos, carrito_id, factura_id)
    VALUES (
        NEW.cliente_id,
        NEW.fecha,
        NEW.total,
        NULL, -- Si puntos redimidos se calculan en otro lado, ajusta aquí.
        NEW.carrito_id,
        NEW.id
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;



----------Triggers----------
----Trigger 1----
CREATE TRIGGER crear_factura_tras_venta
AFTER INSERT ON venta
FOR EACH ROW
EXECUTE FUNCTION generar_factura_venta();

----Punto 13----
CREATE TRIGGER trigger_puntos_ganados
AFTER INSERT ON puntos_ganados
FOR EACH ROW
EXECUTE FUNCTION trigger_historial_puntos();

CREATE TRIGGER trigger_puntos_redimidos
AFTER INSERT ON puntos_redimidos
FOR EACH ROW
EXECUTE FUNCTION trigger_historial_puntos();

----Punto 15----

CREATE TRIGGER trigger_facturas
AFTER INSERT ON facturas
FOR EACH ROW
EXECUTE FUNCTION trigger_historial_compras();


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
    FROM facturas f
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
        FROM ventas v
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


