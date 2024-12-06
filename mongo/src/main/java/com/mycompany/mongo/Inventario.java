/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mongo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Usuario
 */
public class Inventario {
    private static Connection conexion;

    public Inventario(Connection conexion1) {
        this.conexion = conexion1;
    }
    
    public static void crearInventario() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce el ID del producto: ");
        int productoId = scanner.nextInt();

        System.out.print("Introduce la cantidad disponible: ");
        int cantidadDisponible = scanner.nextInt();

        scanner.nextLine();  // Limpiar el buffer

        System.out.print("Introduce la referencia de compra: ");
        String referenciaCompra = scanner.nextLine();

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.crear_inventario(?, ?, ?)");
            stmt.setInt(1, productoId);
            stmt.setInt(2, cantidadDisponible);
            stmt.setString(3, referenciaCompra);

            stmt.execute();
            System.out.println("Inventario creado exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al crear inventario: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar el CallableStatement: " + e.getMessage());
            }
        }
    }
    
    public static void actualizarInventario() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce el ID del producto: ");
        int productoId = scanner.nextInt();

        System.out.print("Introduce la nueva cantidad: ");
        int nuevaCantidad = scanner.nextInt();

        CallableStatement stmt = null;
        try {
            // Comprobar si la conexión está abierta
            if (conexion == null || conexion.isClosed()) {
                System.err.println("Error: La conexión está cerrada.");
                return;  // Salir si la conexión está cerrada
            }

            // Preparar el llamado al procedimiento almacenado
            stmt = conexion.prepareCall("CALL compraya.actualizar_inventario(?, ?)");

            // Establecer los parámetros del procedimiento
            stmt.setInt(1, productoId);
            stmt.setInt(2, nuevaCantidad);

            // Ejecutar el procedimiento almacenado
            stmt.execute();
            System.out.println("Inventario actualizado exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al actualizar inventario: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar el CallableStatement: " + e.getMessage());
            }
        }
    }


    public static void consultarInventario() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce el ID del producto: ");
        int productoId = scanner.nextInt();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Comprobar si la conexión está abierta
            if (conexion == null || conexion.isClosed()) {
                System.err.println("Error: La conexión está cerrada.");
                return;
            }

            // Preparar la consulta SELECT que llama a la función de PostgreSQL
            stmt = conexion.prepareStatement("SELECT * FROM compraya.consultar_inventario(?)");

            // Establecer el parámetro de la función
            stmt.setInt(1, productoId);

            // Ejecutar la consulta y obtener el resultado
            rs = stmt.executeQuery();

            // Imprimir los resultados de la consulta
            while (rs.next()) {
                int id = rs.getInt("id");
                int cantidadDisponible = rs.getInt("cantidad_disponible");
                String referenciaCompra = rs.getString("referencia_compra");

                System.out.println("ID: " + id);
                System.out.println("Cantidad disponible: " + cantidadDisponible);
                System.out.println("Referencia compra: " + referenciaCompra);
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar inventario: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    
    public static void reducirInventario() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce el ID del producto: ");
        int productoId = scanner.nextInt();

        System.out.print("Introduce la cantidad a reducir: ");
        int cantidadReducir = scanner.nextInt();

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.reducir_inventario(?, ?)");
            stmt.setInt(1, productoId);
            stmt.setInt(2, cantidadReducir);

            stmt.execute();
            System.out.println("Inventario reducido exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al reducir inventario: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar el CallableStatement: " + e.getMessage());
            }
        }
    }

    public static void eliminarInventario() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce el ID del producto: ");
        int productoId = scanner.nextInt();

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.eliminar_inventario(?)");
            stmt.setInt(1, productoId);

            stmt.execute();
            System.out.println("Inventario eliminado exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al eliminar inventario: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar el CallableStatement: " + e.getMessage());
            }
        }
    }
    
    public static boolean verificarDisponibilidad() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce el ID del producto: ");
        int productoId = scanner.nextInt();

        System.out.print("Introduce la cantidad solicitada: ");
        int cantidadSolicitada = scanner.nextInt();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Preparar la consulta SELECT que llama a la función de PostgreSQL
            stmt = conexion.prepareStatement("SELECT compraya.verificar_disponibilidad(?, ?)");

            // Establecer los parámetros de la función
            stmt.setInt(1, productoId);
            stmt.setInt(2, cantidadSolicitada);

            // Ejecutar la consulta y obtener el resultado
            rs = stmt.executeQuery();

            // Verificar si la disponibilidad es suficiente
            if (rs.next()) {
                return rs.getBoolean(1); // Devuelve el valor TRUE o FALSE de la función
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar disponibilidad: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return false; // Devuelve false si hubo algún problema al obtener los datos
    }



}
