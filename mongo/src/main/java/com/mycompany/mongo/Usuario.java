/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mongo;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
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
public class Usuario {
    private static Connection conexion;

    public Usuario(Connection conexion) {
        this.conexion = conexion;
    }
    
    public static void eliminarUsuario(int usuarioId) {
        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall(" CALL compraya.eliminar_usuario(?) ");

            stmt.setInt(1, usuarioId);

            stmt.execute();
            System.out.println("Usuario con ID " + usuarioId + " eliminado exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar el usuario: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    public static void obtenerTodosLosUsuarios() {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Prepara la consulta para ejecutar la función
            String sql = "SELECT * FROM compraya.obtener_todos_los_usuarios()";
            stmt = conexion.prepareStatement(sql);

            // Ejecuta la consulta y obtiene los resultados
            rs = stmt.executeQuery();

            // Procesa los resultados
            System.out.println("Usuarios:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String numeroDocumento = rs.getString("numero_documento");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");
                String celular = rs.getString("celular");
                int puntos = rs.getInt("puntos");

                System.out.printf("ID: %d, Documento: %s, Nombre: %s, Email: %s, Celular: %s, Puntos: %d%n",
                        id, numeroDocumento, nombre, email, celular, puntos);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los usuarios: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
