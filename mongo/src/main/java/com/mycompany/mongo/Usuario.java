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
import java.sql.Statement;


/**
 *
 * @author Usuario
 */
public class Usuario {
    private static Connection conexion;

    public Usuario(Connection conexion1) {
        this.conexion = conexion1;
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
    
    public static void modificarUsuario() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce el ID del usuario: ");
        int usuarioId = scanner.nextInt();
        scanner.nextLine();  // Consumir la nueva línea pendiente

        System.out.print("Introduce el número de documento (o presiona Enter para no modificar): ");
        String numeroDocumento = scanner.nextLine();

        System.out.print("Introduce el nombre (o presiona Enter para no modificar): ");
        String nombre = scanner.nextLine();

        System.out.print("Introduce la contraseña (o presiona Enter para no modificar): ");
        String contrasenia = scanner.nextLine();

        System.out.print("Introduce el email (o presiona Enter para no modificar): ");
        String email = scanner.nextLine();

        System.out.print("Introduce el celular (o presiona Enter para no modificar): ");
        String celular = scanner.nextLine();

        System.out.print("Introduce los puntos (o presiona Enter para no modificar): ");
        String puntos = scanner.nextLine();

        System.out.print("Introduce el rol (o presiona Enter para no modificar): ");
        String rol = scanner.nextLine();

        CallableStatement stmt = null;
        try {
            String query = "CALL compraya.modificar_usuario(?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conexion.prepareCall(query);

            stmt.setInt(1, usuarioId);

            if (numeroDocumento.isEmpty()) {
                stmt.setNull(2, Types.VARCHAR);
            } else {
                stmt.setString(2, numeroDocumento);
            }

            if (nombre.isEmpty()) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, nombre);
            }

            if (contrasenia.isEmpty()) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(4, contrasenia);
            }

            if (email.isEmpty()) {
                stmt.setNull(5, Types.VARCHAR);
            } else {
                stmt.setString(5, email);
            }

            if (celular.isEmpty()) {
                stmt.setNull(6, Types.VARCHAR);
            } else {
                stmt.setString(6, celular);
            }

            if (puntos.isEmpty()) {
                stmt.setNull(7, Types.INTEGER);
            } else {
                stmt.setInt(7, Integer.parseInt(puntos));
            }

            if (rol.isEmpty()) {
                stmt.setNull(8, Types.INTEGER);
            } else {
                stmt.setInt(8, Integer.parseInt(rol));
            }

            stmt.execute();
            System.out.println("Usuario modificado exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al modificar usuario: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    public static void modificarUsuarioLogueado() {
        Scanner scanner = new Scanner(System.in);

        String numeroDocumento = obtenerNumeroDocumentoLogueado();
        if (numeroDocumento == null) {
            System.out.println("No hay un usuario logueado.");
            return;
        }

        System.out.print("Introduce el nombre (o presiona Enter para no modificar): ");
        String nombre = scanner.nextLine();

        System.out.print("Introduce la contraseña (o presiona Enter para no modificar): ");
        String contrasenia = scanner.nextLine();

        System.out.print("Introduce el email (o presiona Enter para no modificar): ");
        String email = scanner.nextLine();

        System.out.print("Introduce el celular (o presiona Enter para no modificar): ");
        String celular = scanner.nextLine();

        System.out.print("Introduce los puntos (o presiona Enter para no modificar): ");
        String puntos = scanner.nextLine();

        System.out.print("Introduce el rol (o presiona Enter para no modificar): ");
        String rol = scanner.nextLine();

        CallableStatement stmt = null;
        try {
            String query = "CALL compraya.modificar_usuario_logueado(?, ?, ?, ?, ?, ?, ?)";
            stmt = conexion.prepareCall(query);

            stmt.setString(1, numeroDocumento);  

            if (nombre.isEmpty()) {
                stmt.setNull(2, Types.VARCHAR);
            } else {
                stmt.setString(2, nombre);
            }

            if (contrasenia.isEmpty()) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, contrasenia);
            }

            if (email.isEmpty()) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(4, email);
            }

            if (celular.isEmpty()) {
                stmt.setNull(5, Types.VARCHAR);
            } else {
                stmt.setString(5, celular);
            }

            if (puntos.isEmpty()) {
                stmt.setNull(6, Types.INTEGER);
            } else {
                stmt.setInt(6, Integer.parseInt(puntos));
            }

            if (rol.isEmpty()) {
                stmt.setNull(7, Types.INTEGER);
            } else {
                stmt.setInt(7, Integer.parseInt(rol));
            }

            // Ejecutar el procedimiento
            stmt.execute();
            System.out.println("Usuario modificado exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al modificar usuario: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    private static String obtenerNumeroDocumentoLogueado() {
        String numeroDocumento = null;

        try {
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT numero_documento FROM compraya.sesiones_usuario ORDER BY ultima_actividad DESC LIMIT 1");

            if (rs.next()) {
                numeroDocumento = rs.getString("numero_documento");
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener el número de documento del usuario logueado: " + e.getMessage());
        }

        return numeroDocumento;
    }
    
    public static void eliminarUsuario() throws SQLException {
        restablecerConexion(); // Asegúrate de que la conexión esté activa

        String numeroDocumento = obtenerNumeroDocumentoLogueado();
        if (numeroDocumento == null) {
            System.err.println("No se puede eliminar usuario: no hay usuario logueado.");
            return;
        }

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.eliminar_usuario_logueado(?)");
            stmt.setString(1, numeroDocumento);

            stmt.execute();
            System.out.println("Cuenta eliminada exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar cuenta: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar el CallableStatement: " + e.getMessage());
            }
        }
    }
    
    public static void restablecerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            try {
                // Configura estos valores según tu base de datos
                String url = "jdbc:postgresql://localhost:5432/postgres";
                String user = "postgres";
                String password = "elpepe1234";

                conexion = DriverManager.getConnection(url, user, password);
                System.out.println("Conexión restablecida exitosamente.");
            } catch (SQLException e) {
                System.err.println("Error al restablecer la conexión: " + e.getMessage());
            }
        }
    }


    
}
