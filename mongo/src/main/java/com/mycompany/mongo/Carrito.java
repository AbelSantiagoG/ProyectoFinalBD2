/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mongo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Usuario
 */
public class Carrito {
    private static Connection conexion;

    public Carrito(Connection conexion1) {
        this.conexion = conexion1;
    }
    
    public static void agregarProductoAlCarrito() {
        Scanner scanner = new Scanner(System.in);  // Crear un escáner para leer entradas desde consola

        // Pedir los datos al usuario
        System.out.print("Ingresa el ID del usuario: ");
        int usuarioId = scanner.nextInt();  // Leer el usuarioId desde consola

        System.out.print("Ingresa el ID del producto: ");
        int productoId = scanner.nextInt();  // Leer el productoId desde consola

        System.out.print("Ingresa la cantidad: ");
        int cantidad = scanner.nextInt();  // Leer la cantidad desde consola

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.agregar_producto_al_carrito(?, ?, ?)");

            // Establecer los parámetros para la llamada al procedimiento
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, productoId);
            stmt.setInt(3, cantidad);

            // Ejecutar el procedimiento
            stmt.execute();
            System.out.println("Producto agregado al carrito exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al agregar el producto al carrito: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                // No es necesario cerrar la conexión aquí si la conexión se maneja globalmente
                // if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    public static void eliminarProductoDelCarrito() {
        Scanner scanner = new Scanner(System.in);  // Crear un escáner para leer entradas desde consola

        // Pedir los datos al usuario
        System.out.print("Ingresa el ID del carrito: ");
        int carritoId = scanner.nextInt();  // Leer el carritoId desde consola

        System.out.print("Ingresa el ID del producto a eliminar: ");
        int productoId = scanner.nextInt();  // Leer el productoId desde consola

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.eliminar_producto_del_carrito(?, ?)");

            // Establecer los parámetros para la llamada al procedimiento
            stmt.setInt(1, carritoId);
            stmt.setInt(2, productoId);

            // Ejecutar el procedimiento
            stmt.execute();
            System.out.println("Producto con ID " + productoId + " eliminado del carrito con ID " + carritoId + " exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar el producto del carrito: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                // No es necesario cerrar la conexión aquí si la conexión se maneja globalmente
                // if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    public static void obtenerProductosEnCarrito() { 
        Scanner scanner = new Scanner(System.in);  // Crear un escáner para leer entradas desde consola

        // Pedir el carritoId al usuario
        System.out.print("Ingresa el ID del carrito: ");
        int carritoId = scanner.nextInt();  // Leer el carritoId desde consola

        CallableStatement stmt = null;
        ResultSet rs = null;
        try {
            // Llamada a la función en PostgreSQL
            stmt = conexion.prepareCall("SELECT * FROM compraya.obtener_productos_en_carrito(?)");

            // Establecer el parámetro para la función
            stmt.setInt(1, carritoId);

            // Ejecutar la función y obtener el resultado
            rs = stmt.executeQuery();

            // Procesar los resultados
            while (rs.next()) {
                int productoId = rs.getInt("producto_id");
                String nombreProducto = rs.getString("nombre_producto");
                int cantidad = rs.getInt("cantidad");
                double totalProducto = rs.getDouble("total");

                // Mostrar los resultados
                System.out.println("Producto ID: " + productoId + ", Nombre: " + nombreProducto + ", Cantidad: " + cantidad + ", Total: " + totalProducto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos del carrito: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                // No es necesario cerrar la conexión aquí si la conexión se maneja globalmente
                // if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
     public static void vaciarCarrito() {
        Scanner scanner = new Scanner(System.in);  // Crear un escáner para leer entradas desde consola

        // Pedir el ID del carrito al usuario
        System.out.print("Ingresa el ID del carrito que deseas vaciar: ");
        int carritoId = scanner.nextInt();  // Leer el carritoId desde consola

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.vaciar_carrito(?)");

            // Establecer el parámetro para la llamada al procedimiento
            stmt.setInt(1, carritoId);

            // Ejecutar el procedimiento
            stmt.execute();
            System.out.println("El carrito con ID " + carritoId + " ha sido vaciado exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al vaciar el carrito: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                // No es necesario cerrar la conexión aquí si la conexión se maneja globalmente
                // if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
