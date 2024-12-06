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
public class Informe {
    private static Connection conexion;

    public Informe(Connection conexion) {
        this.conexion = conexion;
    }
    
    private static void mostrarJsonInforme(){
            try  {
                 String query = "SELECT * FROM compraya.mostrar_informes_creados();";
                 CallableStatement stmt = conexion.prepareCall(query);
                 ResultSet rs = stmt.executeQuery();
                // Iterar sobre los resultados
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String tipo = rs.getString("tipo");
                    Date fecha = rs.getDate("fecha");
                    String datosJson = rs.getString("datos_json");

                    // Imprimir los resultados de forma bonita
                    System.out.println("Informe ID: " + id);
                    System.out.println("Tipo: " + tipo);
                    System.out.println("Fecha: " + fecha);
                    // Formatear el JSON con la función nativa de PostgreSQL
                    System.out.println("Datos JSON: " + formatJson(datosJson));
                    System.out.println("---------------------------");
                }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static String formatJson(String json) {
        // Simple formato con saltos de línea para una visualización más bonita
        // Si prefieres una librería más robusta, puedes usar Gson o Jackson en lugar de esto
        return json.replace(",", ",\n").replace("{", "{\n").replace("}", "\n}");
    }
}
