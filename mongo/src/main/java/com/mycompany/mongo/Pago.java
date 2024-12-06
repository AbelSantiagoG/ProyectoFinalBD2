/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mongo;

import java.sql.Connection;
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
public class Pago {
    private static Connection conexion;

    public Pago(Connection conexion1) {
        this.conexion = conexion1;
    }
    
    public void registrarPagoPuntos() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el ID del carrito: ");
        int carritoId = scanner.nextInt();

        System.out.print("Ingrese los puntos a usar: ");
        int puntosUsados = scanner.nextInt();

        // Preparar la llamada al procedimiento en PostgreSQL
        try (CallableStatement stmt = conexion.prepareCall(" call compraya.registrar_pago_puntos(?, ?) ")) {
            stmt.setInt(1, carritoId);  // ID del carrito
            stmt.setInt(2, puntosUsados); // Puntos a usar

            // Ejecutar el procedimiento
            stmt.execute();

            System.out.println("Pago con puntos realizado correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al realizar el pago con puntos: " + e.getMessage());
        }
    }




    public void registrarPagoEfectivo() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el ID del carrito: ");
        int carritoId = scanner.nextInt();

        System.out.print("Ingrese el monto de efectivo a usar: ");
         BigDecimal efectivoUsado = scanner.nextBigDecimal();

        // Preparar la llamada al procedimiento en PostgreSQL
        try (CallableStatement stmt = conexion.prepareCall(" call compraya.registrar_pago_efectivo(?, ?) ")) {
            stmt.setInt(1, carritoId);  // ID del carrito
            stmt.setBigDecimal(2, efectivoUsado);  // Monto de efectivo

            // Ejecutar el procedimiento
            stmt.execute();

            System.out.println("Pago con efectivo realizado correctamente.");

            // Opcional: Actualizar el estado del carrito en la UI o la variable local
        } catch (SQLException e) {
            System.err.println("Error al realizar el pago con efectivo: " + e.getMessage());
        }
    }


    
    public void registrarPagoMixto() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el ID del carrito: ");
        int carritoId = scanner.nextInt();

        System.out.print("Ingrese los puntos a usar: ");
        int puntosUsados = scanner.nextInt();

        System.out.print("Ingrese el monto de efectivo a usar: ");
        BigDecimal efectivoUsado = scanner.nextBigDecimal();

        // Preparar la llamada al procedimiento en PostgreSQL
        try (CallableStatement stmt = conexion.prepareCall(" call compraya.registrar_pago_mixto(?, ?, CAST(? AS numeric)) ")) {
            stmt.setInt(1, carritoId);  // ID del carrito
            stmt.setInt(2, puntosUsados);  // Puntos a usar
            stmt.setBigDecimal(3, efectivoUsado);  // Monto de efectivo

            // Ejecutar el procedimiento
            stmt.execute();

            System.out.println("Pago mixto realizado correctamente.");

            // Opcional: Actualizar el estado del carrito en la UI o la variable local
        } catch (SQLException e) {
            System.err.println("Error al realizar el pago mixto: " + e.getMessage());
        }
    }





}
