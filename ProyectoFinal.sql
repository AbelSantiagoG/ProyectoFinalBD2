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



create table usuario (
    id int primary key default nextval('usuarioSecuencia'), 
    numero_documento varchar(15) unique not null,
    nombre varchar(50) not null,
    contrasenia varchar(50) not null,
    email varchar(100) not null,
    celular varchar(20),
    puntos int
);

create table categoria (
    id int primary key default nextval('categoriaSecuencia'),
    nombre varchar(50) not null unique
);

create table producto (
    id int primary key default nextval('productoSecuencia'),
    nombre varchar(40) unique not null,
    descripcion varchar(100),
    precio numeric not null,
    imagen varchar(100),
    descuento int,
    categoria_id int references categorias(id)
);


create table inventario(
	id int primary key default nextval('inventarioSecuencia'),
	cantidad_disponible int,
	referencia_compra varchar(30) unique not null,
	producto_id int references producto(id)
);


create table venta(
	id int primary key default nextval('ventaSecuencia'),
	carrito_id int references carrito(id),
	producto_id int references producto(id)
);


create table carrito(
	id int primary key default nextval('carritoSecuencia'),
	cantidad int ,
	total numeric,
);


create table factura(
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

create table punto_redimido(
	id int primary key default nextval('puntosRedimidosSecuencia'),
	cantidad int,
	fecha_redencion date,
	usuario_id int references usuario(id)
);

create table punto_ganado(
	id int primary key default nextval('puntosGanadosSecuencia'),
	cantidad int,
	fecha_ganacia date,
	motivo varchar(50) not null,
	referencia varchar(20),
	usuario_id int references usuario(id)
);


create table informe (
    id int primary key default nextval('informeSecuencia'),
    tipo tipo_informe not null,
    fecha date not null,
    datos_json json not null
);


create table documento_auditoria (
    id int primary key default nextval('audotiraSecuencia'),
    fecha date not null,
    cantidad int not null,
    total numeric not null,
    producto_id int references productos(id),
    cliente_id int references clientes(id)
);




