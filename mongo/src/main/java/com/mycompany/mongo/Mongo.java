/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mongo;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.PreparedStatement;
import javax.lang.model.util.Types;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Usuario
 */
public class Mongo {
    public static Connection conexion;
    static Auth auth = new Auth(conexion);
    
    public static void main(String[] args) {
        try {
            initConnection();
            
            mostrarMenu();
            
        } catch (Exception e) {
            System.err.println("Error en la aplicacion: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
    
    public static void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("Menu de opciones:");
            System.out.println("1. Iniciar sesion");
            System.out.println("2. Registrarse");
            System.out.println("3. Salir");
            System.out.print("Selecciona una opcion (1, 2, 3): ");

            int opcion = scanner.nextInt();  // Leer la opción del usuario

            switch (opcion) {
                case 1:
                    auth.login();  // Llamar al método para iniciar sesión
                    break;
                case 2:
                    auth.register();    
                    break;
                case 3:
                    System.out.println("Hasta luego");
                    salir = true;    
                    break;
                default:
                    System.out.println("Opcion no valida. Por favor, elige una opcion entre 1 y 3.");
            }
        }

        scanner.close(); 
    }
    
    
    public static void initConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "elpepe1234");
        System.out.println("Conexion exitosa");
    }

    public static void closeConnection() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexion cerrada");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
