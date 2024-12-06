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
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Scanner;

/**
 *
 * @author Usuario
 */
public class Auditoría {
    private static Connection conexion;

    public Auditoría(Connection conexion1) {
        this.conexion = conexion1;
    }
    
    public static void buscarAuditoriaPorUsuarioProducto() {
        Scanner scanner = new Scanner(System.in);

        // Pedir los parámetros por consola
        System.out.print("Introduce el nombre de usuario (puede ser parcial): ");
        String nombreUsuario = scanner.nextLine();

        System.out.print("Introduce el nombre del producto (puede ser parcial): ");
        String nombreProducto = scanner.nextLine();

        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            // Preparar la llamada a la función que devuelve una tabla
            stmt = conexion.prepareCall("{ ? = call compraya.buscar_auditoria_por_usuario_producto(?, ?) }");

            // Registrar el parámetro de salida de la función (es una tabla, pero la gestionamos como un ResultSet)
            stmt.registerOutParameter(1, Types.REF_CURSOR);

            // Establecer los parámetros de entrada
            stmt.setString(2, nombreUsuario);  // nombre_usuario_input
            stmt.setString(3, nombreProducto);  // nombre_producto_input

            // Ejecutar la función
            stmt.execute();

            // Obtener el resultado (el cursor de salida)
            rs = (ResultSet) stmt.getObject(1);

            // Imprimir los resultados de la auditoría
            while (rs.next()) {
                int auditoriaId = rs.getInt("auditoria_id");
                String accion = rs.getString("accion");
                int usuarioId = rs.getInt("usuario_id");
                int facturaId = rs.getInt("factura_id");
                Timestamp fecha = rs.getTimestamp("fecha");
                String detalle = rs.getString("detalle");
                String nombreUsuarioResult = rs.getString("nombre_usuario");
                String nombreProductoResult = rs.getString("nombre_producto");

                // Mostrar la información
                System.out.println("Auditoria ID: " + auditoriaId);
                System.out.println("Acción: " + accion);
                System.out.println("Usuario ID: " + usuarioId);
                System.out.println("Factura ID: " + facturaId);
                System.out.println("Fecha: " + fecha);
                System.out.println("Detalle: " + detalle);
                System.out.println("Nombre Usuario: " + nombreUsuarioResult);
                System.out.println("Nombre Producto: " + nombreProductoResult);
                System.out.println("---------------");
            }

        } catch (SQLException e) {
            System.err.println("Error al ejecutar la función buscar_auditoria_por_usuario_producto: " + e.getMessage());
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
