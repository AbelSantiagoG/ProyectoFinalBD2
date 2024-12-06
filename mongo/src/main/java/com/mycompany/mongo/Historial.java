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
public class Historial {
    private static Connection conexion;

    public Historial(Connection conexion) {
        this.conexion = conexion;
    }
    
    public static void mostrarHistorialPuntos(int id) {
        String sql = "SELECT * FROM compraya.mostrar_historial_puntos(?)";  // Llamada a la función almacenada

        try {
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            // Recorrer el ResultSet y mostrar los datos
            while (rs.next()) {
                int usuarioId = rs.getInt("usuario_id");
                int cantidad = rs.getInt("cantidad");
                Date fecha = rs.getDate("fecha");
                String motivo = rs.getString("motivo");
                int ventaId = rs.getInt("venta_id");

                // Mostrar los datos
                System.out.println("Usuario ID: " + usuarioId + ", Cantidad: " + cantidad +
                        ", Fecha: " + fecha + ", Motivo: " + motivo + ", Venta ID: " + ventaId);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el historial de puntos: " + e.getMessage());
        }
    }
    
    
    public static void mostrarHistorialCompras(int clienteId) {
        String sql = "SELECT * FROM compraya.mostrar_historial_compras(?)";  // Llamada a la función SQL
        
        try {
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, clienteId);  // Establecemos el cliente_id como parámetro
            ResultSet rs = stmt.executeQuery();  // Ejecutamos la consulta

            // Imprimir encabezados
            System.out.println("ID | Fecha | Total Efectivo | Puntos Redimidos | Carrito ID | Factura ID");

            // Imprimir los resultados
            while (rs.next()) {
                int id = rs.getInt("id");
                Date fecha = rs.getDate("fecha");
                double totalEfectivo = rs.getDouble("total_efectivo");
                int puntosRedimidos = rs.getInt("puntos_redimidos");
                int carritoId = rs.getInt("carrito_id");
                int facturaId = rs.getInt("factura_id");

                // Imprimir cada fila
                System.out.printf("%d | %s | %.2f | %d | %d | %d\n", 
                                  id, fecha, totalEfectivo, puntosRedimidos, carritoId, facturaId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
