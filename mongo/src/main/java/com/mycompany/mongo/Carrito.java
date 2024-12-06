/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mongo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Usuario
 */
public class Carrito {
    private static Connection conexion;

    public Carrito(Connection conexion) {
        this.conexion = conexion;
    }
    
    public static void agregarProductoAlCarrito(int usuarioId, int productoId, int cantidad) {
        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.agregar_producto_al_carrito(?, ?, ?)");

            stmt.setInt(1, usuarioId);
            stmt.setInt(2, productoId);
            stmt.setInt(3, cantidad);

            stmt.execute();
            System.out.println("Producto agregado al carrito exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al agregar el producto al carrito: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    public static void eliminarProductoDelCarrito(int carritoId, int productoId) {
        CallableStatement stmt = null;
        try {
            // Llamada al procedimiento almacenado en PostgreSQL
            stmt = conexion.prepareCall("CALL compraya.eliminar_producto_del_carrito(?, ?)");

            // Establecer los parámetros para el procedimiento
            stmt.setInt(1, carritoId);
            stmt.setInt(2, productoId);

            // Ejecutar el procedimiento
            stmt.execute();
            System.out.println("Producto con ID " + productoId + " eliminado del carrito con ID " + carritoId + " exitosamente.");

        } catch (SQLException e) {
            // Manejo de excepciones
            System.err.println("Error al eliminar el producto del carrito: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    public static void obtenerProductosEnCarrito(int carritoId) {
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
                double totalProducto = rs.getDouble("total_producto");

                // Mostrar los resultados
                System.out.println("Producto ID: " + productoId + ", Nombre: " + nombreProducto + ", Cantidad: " + cantidad + ", Total: " + totalProducto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos del carrito: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    public static void vaciarCarrito(int carritoId) {
        CallableStatement stmt = null;
        try {
            // Preparar la llamada al procedimiento
            stmt = conexion.prepareCall("CALL compraya.vaciar_carrito(?)");

            // Establecer el parámetro del procedimiento
            stmt.setInt(1, carritoId);

            // Ejecutar el procedimiento
            stmt.execute();
            System.out.println("El carrito con ID " + carritoId + " ha sido vaciado exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al vaciar el carrito: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
