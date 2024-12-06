/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mongo;

import java.io.StringReader;
import java.io.StringWriter;
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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author Usuario
 */
public class XML {
    private static Connection conexion;

    public XML(Connection conexion1) {
        this.conexion = conexion1;
    }
    
    public String obtenerFacturaXML(int facturaId) {
   
        String sql = "SELECT compraya.generar_factura_xml(?)"; // Llamada a la función almacenada
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            // Configurar el parámetro de entrada
            stmt.setInt(1, facturaId);

            // Ejecutar la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Retornar el XML como texto
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar la función almacenada: " + e.getMessage());
        }
        return null;
    }
 
    public String formatearXML(String xml) {
        try {
            // Crear un transformador para formatear el XML
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            
            // Configurar propiedades para el formateo
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // Convertir el XML en un formato bonito
            StreamSource source = new StreamSource(new StringReader(xml));
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);

            return writer.toString();
        } catch (TransformerException e) {
            System.err.println("Error al formatear el XML: " + e.getMessage());
            return xml; // Retorna el XML sin formato si ocurre un error
        }
    }
}
