/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mongo;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;
import java.sql.PreparedStatement;
/**
 *
 * @author Usuario
 */
public class Producto {
    private static Connection conexion;

    public Producto(Connection conexion1) {
        this.conexion = conexion1;
    }
    
    public static void crearProducto() {
        Scanner scanner = new Scanner(System.in);

        // Solicitar los parámetros al usuario
        System.out.print("Ingrese el nombre del producto: ");
        String nombre = scanner.nextLine();

        System.out.print("Ingrese la descripción del producto: ");
        String descripcion = scanner.nextLine();

        System.out.print("Ingrese el precio del producto: ");
        BigDecimal precio = scanner.nextBigDecimal();

        scanner.nextLine();  // Consumir la nueva línea pendiente

        System.out.print("Ingrese la imagen del producto: ");
        String imagen = scanner.nextLine();

        System.out.print("Ingrese el descuento del producto: ");
        int descuento = scanner.nextInt();

        System.out.print("Ingrese el ID de la categoría: ");
        int categoriaId = scanner.nextInt();

        // Solicitar la cantidad de inventario
        System.out.print("Ingrese la cantidad de inventario para este producto: ");
        int cantidadInventario = scanner.nextInt();

        CallableStatement stmt = null;
        try {
            // Modificar la llamada para incluir el parámetro de cantidad de inventario
            stmt = conexion.prepareCall("CALL compraya.crear_producto(?, ?, CAST(? AS numeric), ?, ?, ?, ?)");

            // Establecer los parámetros del procedimiento
            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.setBigDecimal(3, precio);  // Cambiar a setBigDecimal
            stmt.setString(4, imagen);
            stmt.setInt(5, descuento);
            stmt.setInt(6, categoriaId);
            stmt.setInt(7, cantidadInventario);  // Agregar cantidad de inventario

            stmt.execute();
            System.out.println("Producto creado exitosamente con inventario.");

        } catch (SQLException e) {
            System.err.println("Error al crear el producto: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
//                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    
    public static void modificarProducto() {
     
        Scanner scanner = new Scanner(System.in);

        // Solicitar los parámetros al usuario
        System.out.print("Ingrese el ID del producto a modificar: ");
        int productoId = scanner.nextInt();

        scanner.nextLine();  // Consumir la nueva línea pendiente

        System.out.print("Ingrese el nuevo nombre del producto (o deje vacío para no modificarlo): ");
        String nombre = scanner.nextLine();

        System.out.print("Ingrese la nueva descripción del producto (o deje vacío para no modificarlo): ");
        String descripcion = scanner.nextLine();

        System.out.print("Ingrese el nuevo precio del producto (o deje vacío para no modificarlo): ");
        Double precio = scanner.hasNextDouble() ? scanner.nextDouble() : null;

        scanner.nextLine();  // Consumir la nueva línea pendiente

        System.out.print("Ingrese la nueva imagen del producto (o deje vacío para no modificarlo): ");
        String imagen = scanner.nextLine();

        System.out.print("Ingrese el nuevo descuento del producto (o deje vacío para no modificarlo): ");
        Integer descuento = scanner.hasNextInt() ? scanner.nextInt() : null;

        System.out.print("Ingrese el nuevo ID de categoría (o deje vacío para no modificarlo): ");
        Integer categoriaId = scanner.hasNextInt() ? scanner.nextInt() : null;

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.modificar_producto(?,?, ?, CAST(? AS numeric), ?, ?, ?)");

            stmt.setInt(1, productoId);

            if (nombre != null && !nombre.isEmpty()) {
                stmt.setString(2, nombre);
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }

            if (descripcion != null && !descripcion.isEmpty()) {
                stmt.setString(3, descripcion);
            } else {
                stmt.setNull(3, java.sql.Types.VARCHAR);
            }

            if (precio != null) {
                stmt.setBigDecimal(4, new BigDecimal(precio));
            } else {
                stmt.setNull(4, java.sql.Types.DOUBLE);
            }

            if (imagen != null && !imagen.isEmpty()) {
                stmt.setString(5, imagen);
            } else {
                stmt.setNull(5, java.sql.Types.VARCHAR);
            }

            if (descuento != null) {
                stmt.setInt(6, descuento);
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            if (categoriaId != null) {
                stmt.setInt(7, categoriaId);
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            stmt.execute();
            System.out.println("Producto con ID " + productoId + " modificado exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al modificar el producto: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
     
    public static void eliminarProducto() {
        
        Scanner scanner = new Scanner(System.in);

        // Solicitar el ID del producto a eliminar
        System.out.print("Ingrese el ID del producto a eliminar: ");
        int productoId = scanner.nextInt();

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.eliminar_producto(?)");

            // Establecer el parámetro del procedimiento
            stmt.setInt(1, productoId);

            stmt.execute();
            System.out.println("Producto eliminado exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar el producto: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }   
        
    public static void obtenerTodosLosProductos() {
        
        CallableStatement stmt = null;
        try 
            {
             stmt = conexion.prepareCall("SELECT * FROM compraya.obtener_todos_los_productos()");
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                BigDecimal precio = rs.getBigDecimal("precio");
                String imagen = rs.getString("imagen");
                BigDecimal descuento = rs.getBigDecimal("descuento");
                int categoriaId = rs.getInt("categoria_id");

                System.out.println("ID: " + id +
                                   ", Nombre: " + nombre +
                                   ", Descripción: " + descripcion +
                                   ", Precio: " + precio +
                                   ", Imagen: " + imagen +
                                   ", Descuento: " + descuento +
                                   ", Categoría ID: " + categoriaId);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
    }
     
   public static void filtrar_productos() {
        
        Scanner scanner = new Scanner(System.in);

        // Solicitar los parámetros al usuario
        System.out.print("Ingrese el ID de categoría: ");
        int categoriaId = scanner.nextInt();

        System.out.print("Ingrese el precio mínimo: ");
        BigDecimal precioMin = scanner.nextBigDecimal();

        System.out.print("Ingrese el precio máximo: ");
        BigDecimal precioMax = scanner.nextBigDecimal();

        System.out.print("Ingrese el descuento mínimo: ");
        int descuentoMin = scanner.nextInt();

        System.out.print("Ingrese el descuento máximo: ");
        int descuentoMax = scanner.nextInt();

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("SELECT * FROM compraya.filtrar_productos(?, CAST(? AS numeric), CAST(? AS numeric), ?, ?)");

            // Asignar los parámetros
            stmt.setInt(1, categoriaId);
            stmt.setBigDecimal(2, precioMin);
            stmt.setBigDecimal(3, precioMax);
            stmt.setInt(4, descuentoMin);
            stmt.setInt(5, descuentoMax);

            // Ejecutar la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Imprimir los resultados
                    System.out.println("ID: " + rs.getInt("id") +
                            ", Nombre: " + rs.getString("nombre") +
                            ", Descripción: " + rs.getString("descripcion") +
                            ", Precio: " + rs.getBigDecimal("precio") +
                            ", Imagen: " + rs.getString("imagen") +
                            ", Descuento: " + rs.getInt("descuento") +
                            ", Categoría ID: " + rs.getInt("categoria_id"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al filtrar productos: " + e.getMessage());
        }
    }
    
    
    public static void obtener_productos_descuento() {
     
        Scanner scanner = new Scanner(System.in);

        // Solicitar la fecha actual al usuario
        System.out.print("Ingrese la fecha actual (en formato yyyy-mm-dd): ");
        String fechaStr = scanner.nextLine();
        Date fechaActual = Date.valueOf(fechaStr);  // Convertir la cadena a Date

        CallableStatement stmt = null;
        try {
            // Preparar la llamada a la función almacenada
            stmt = conexion.prepareCall("SELECT * FROM compraya.obtener_productos_descuento(?)");
            stmt.setDate(1, fechaActual); // Pasar la fecha como java.sql.Date

            // Ejecutar la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                boolean found = false; // Verificar si hay resultados
                while (rs.next()) {
                    found = true; // Se encontraron resultados
                    // Imprimir los resultados
                    System.out.println("ID: " + rs.getInt("id") +
                            ", Nombre: " + rs.getString("nombre") +
                            ", Descripción: " + rs.getString("descripcion") +
                            ", Precio Original: " + rs.getBigDecimal("precio_original") +
                            ", Precio con Descuento: " + rs.getBigDecimal("precio_descuento") +
                            ", Descuento Aplicado: " + rs.getInt("descuento_aplicado"));
                }
                if (!found) {
                    System.out.println("No se encontraron productos con descuento para la fecha: " + fechaActual);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los productos con descuento: " + e.getMessage());
        }
    }
    
    public static void obtenerProductosMasVendidos() {
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            // Llamamos a la función compraya.obtener_productos_mas_vendidos()
            stmt = conexion.prepareCall("SELECT * FROM compraya.obtener_productos_mas_vendidos()");

            // Ejecutamos la consulta
            rs = stmt.executeQuery();

            // Procesamos los resultados
            while (rs.next()) {
                int productoId = rs.getInt("producto_id");
                String nombre = rs.getString("nombre");
                int cantidadVendida = rs.getInt("cantidad_vendida");

                // Mostramos los resultados
                System.out.println("Producto ID: " + productoId + ", Nombre: " + nombre + ", Cantidad Vendida: " + cantidadVendida);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los productos más vendidos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    
}
