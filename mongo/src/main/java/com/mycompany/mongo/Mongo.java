/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mongo;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;

/**
 *
 * @author Usuario
 */
public class Mongo {
    public static Connection conexion;
    
    public static void main(String[] args) {
        try {
            initConnection();
            
            List<Informe> informes = obtenerInformeComprasPuntos(1);

            generarPDF("InformeCompras.pdf", informes);
            
            generarExcel("InformeCompras.xlsx", informes);
            
        } catch (Exception e) {
            System.err.println("Error en la aplicacion: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
    
    public static class Informe {
        public Date fecha;
        public BigDecimal totalEfectivo;
        public int puntosRedimidos;
        public int puntosAcumulados;
        public String motivo;

        public Informe(Date fecha, BigDecimal totalEfectivo, int puntosRedimidos, int puntosAcumulados, String motivo) {
            this.fecha = fecha;
            this.totalEfectivo = totalEfectivo;
            this.puntosRedimidos = puntosRedimidos;
            this.puntosAcumulados = puntosAcumulados;
            this.motivo = motivo;
        }
    }
    
    public static List<Informe> obtenerInformeComprasPuntos(int usuarioId) {
        List<Informe> informes = new ArrayList<>();
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            // Llamada a la función en PostgreSQL
            stmt = conexion.prepareCall("SELECT * FROM compraya.obtener_informe_compras_puntos(?)");

            // Establecer el parámetro de entrada
            stmt.setInt(1, usuarioId);

            // Ejecutar y procesar los resultados
            rs = stmt.executeQuery();
            while (rs.next()) {
                Date fecha = rs.getDate("fecha");
                BigDecimal totalEfectivo = rs.getBigDecimal("total_efectivo");
                int puntosRedimidos = rs.getInt("puntos_redimidos");
                int puntosAcumulados = rs.getInt("puntos_acumulados");
                String motivo = rs.getString("motivo");

                informes.add(new Informe(fecha, totalEfectivo, puntosRedimidos, puntosAcumulados, motivo));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el informe de compras: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        return informes;
    }
    
    public static void generarPDF(String archivoSalida, List<Informe> informes) {
        try {
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(archivoSalida));

            documento.open();
            documento.add(new Paragraph("Informe de Compras e Historial de Puntos"));
            documento.add(new Paragraph(" "));

            // Crear tabla para el informe
            PdfPTable tabla = new PdfPTable(5);
            tabla.addCell("Fecha");
            tabla.addCell("Total Efectivo");
            tabla.addCell("Puntos Redimidos");
            tabla.addCell("Puntos Acumulados");
            tabla.addCell("Motivo");

            // Llenar la tabla con datos
            for (Informe informe : informes) {
                tabla.addCell(informe.fecha.toString());
                tabla.addCell(String.valueOf(informe.totalEfectivo));
                tabla.addCell(String.valueOf(informe.puntosRedimidos));
                tabla.addCell(String.valueOf(informe.puntosAcumulados));
                tabla.addCell(informe.motivo);
            }

            documento.add(tabla);
            documento.close();
            System.out.println("Informe generado en " + archivoSalida);

        } catch (Exception e) {
            System.err.println("Error al generar el PDF: " + e.getMessage());
        }
    }
    
    public static void generarExcel(String archivoSalida, List<Informe> informes) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Informe de Compras");

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] columnas = {"Fecha", "Total Efectivo", "Puntos Redimidos", "Puntos Acumulados", "Motivo"};
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);

            CellStyle style = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = workbook.createFont(); // Referencia explícita
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        // Llenar las filas con los datos
        int rowNum = 1;
        for (Informe informe : informes) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(informe.fecha);
            row.createCell(1).setCellValue(informe.totalEfectivo.doubleValue());
            row.createCell(2).setCellValue(informe.puntosRedimidos);
            row.createCell(3).setCellValue(informe.puntosAcumulados);
            row.createCell(4).setCellValue(informe.motivo);
        }

        // Ajustar el tamaño de las columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Guardar el archivo Excel
        try (FileOutputStream fileOut = new FileOutputStream(archivoSalida)) {
            workbook.write(fileOut);
            System.out.println("Archivo Excel creado exitosamente: " + archivoSalida);
        } catch (IOException e) {
            System.err.println("Error al crear el archivo Excel: " + e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el workbook: " + e.getMessage());
            }
        }
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