/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.proyectofinal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 *
 * @author Usuario
 */
public class Main {
    public static void main(String[] args) {
        String contrasena= "elpepe1234";
        try {
            Connection conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres",contrasena);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
