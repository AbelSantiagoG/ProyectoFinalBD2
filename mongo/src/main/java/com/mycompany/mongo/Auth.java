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
public class Auth {
    private static Connection conexion;

    public Auth(Connection conexion) {
        this.conexion = conexion;
    }
    
    public static void register() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Registro de Usuario");
        System.out.print("Numero de documento: ");
        String numeroDocumento = scanner.nextLine();

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Contrasenia: ");
        String contrasenia = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Celular: ");
        String celular = scanner.nextLine();

        System.out.print("Puntos (ingrese 0 si no aplica): ");
        int puntos = Integer.parseInt(scanner.nextLine());

        System.out.print("Rol (ingrese 0 si no aplica): ");
        int rol = Integer.parseInt(scanner.nextLine());

        CallableStatement register = null;
        try {
            register = conexion.prepareCall(" CALL compraya.crear_usuario(?, ?, ?, ?, ?, ?, ?) ");

            register.setString(1, numeroDocumento);
            register.setString(2, nombre);
            register.setString(3, contrasenia);
            register.setString(4, email);
            register.setString(5, celular);
            register.setInt(6, puntos);
            register.setInt(7, rol);

            register.execute();
            System.out.println("Usuario registrado exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al registrar el usuario: " );
        } finally {
            try {
                if (register != null) register.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
    }

    public static void login() {
        Scanner scanner = new Scanner(System.in);

        // Solicitar datos de login
        System.out.println("Login de Usuario");
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Contrasenia: ");
        String contrasenia = scanner.nextLine();

        PreparedStatement loginStmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT compraya.login_usuario(?, ?)";

            // Preparar el statement
            loginStmt = conexion.prepareStatement(query);

            loginStmt.setString(1, email);
            loginStmt.setString(2, contrasenia);

            rs = loginStmt.executeQuery();

            if (rs.next()) {
                String numeroDocumento = rs.getString(1); 
                System.out.println("Login exitoso. Usuario con documento: " + numeroDocumento);
            } else {
                System.out.println("Error en el login. Revisa tus credenciales.");
            }

        } catch (SQLException e) {
            System.err.println("Error al iniciar sesion: " );
        } finally {
            try {
                if (rs != null) rs.close();
                if (loginStmt != null) loginStmt.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
    }
}
