/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mongo;

import java.sql.Connection;
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
public class VentaPuntos {
    private static Connection conexion;

    public VentaPuntos(Connection conexion) {
        this.conexion = conexion;
    }
    
    public static void guardarVentasJson(int idUsuario) {
        String sql = " CALL compraya.guardar_historial_compras_json(?) ";
        try  {
            CallableStatement callableStatement = conexion.prepareCall(sql);
            callableStatement.setInt(1, idUsuario);
            callableStatement.execute();
            
            System.out.println("Ventas guardadas en formato JSON para el usuario con ID: " + idUsuario);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al guardar las ventas en JSON: " + e.getMessage());
        }
    }
        
    public static void guardarPuntosJson(int idUsuario) {
        String sql = " CALL compraya.guardar_historial_puntos_json(?) ";
        try  {
            CallableStatement callableStatement = conexion.prepareCall(sql);
            callableStatement.setInt(1, idUsuario);
            callableStatement.execute();
            
            System.out.println("Puntos guardados en formato JSON para el usuario con ID: " + idUsuario);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al guardar los puntos en JSON: " + e.getMessage());
        }
    }
    
    
    public static void insertarPuntosRedimidos(int cantidad, Date fechaRedencion, int usuarioId) {
        String sql = "CALL compraya.insertar_puntos_redimidos(?, ?, ?)";
        
        try  {
            CallableStatement stmt = conexion.prepareCall(sql);
            // Establecer los parámetros
            stmt.setInt(1, cantidad);
            stmt.setDate(2, fechaRedencion);
            stmt.setInt(3, usuarioId);
            
            // Ejecutar el procedimiento
            stmt.executeUpdate();
            System.out.println("Puntos redimidos insertados correctamente.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void insertarPuntosGanados(int cantidad, Date fechaGanacia, String motivo, String referencia, int usuarioId) {
        String sql = "CALL compraya.insertar_puntos_ganados(?, ?, ?, ?, ?)";
        
        try  {
            CallableStatement stmt = conexion.prepareCall(sql);
            // Establecer los parámetros
            stmt.setInt(1, cantidad);
            stmt.setDate(2, fechaGanacia);
            stmt.setString(3, motivo);
            stmt.setString(4, referencia);
            stmt.setInt(5, usuarioId);
            
            // Ejecutar el procedimiento
            stmt.executeUpdate();
            System.out.println("Puntos ganados insertados correctamente.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public static void crearVenta(int carritoId, int productoId) {
        // SQL para llamar al procedimiento almacenado
        String sql = "CALL compraya.crear_venta(?, ?)";  // Llamada al procedimiento almacenado

        try {
            CallableStatement stmt = conexion.prepareCall(sql);
            // Configurar los parámetros de entrada
            stmt.setInt(1, carritoId);
            stmt.setInt(2, productoId);

            // Ejecutar el procedimiento almacenado
            stmt.execute();

            System.out.println("Venta creada correctamente.");

        } catch (SQLException e) {
            // Manejo de excepciones, podría ser que el carrito o producto no existan
            System.err.println("Error al crear la venta: " + e.getMessage());
        }
    }
}
