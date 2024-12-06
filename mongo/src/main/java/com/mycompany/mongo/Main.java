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
public class Main {
    public static Connection conexion;
    public static Auth auth;
    public static Usuario user;
    public static Producto producto;
    public static Carrito carrito;
    public static Pago pago;
    public static Inventario inventario;
    public static Usuario usuario;
    
    public static void main(String[] args) {
        try {
            initConnection();
            
            auth = new Auth(conexion);
            user = new Usuario(conexion);
            producto = new Producto(conexion);
            carrito = new Carrito(conexion);
            pago = new Pago(conexion);
            inventario = new Inventario(conexion);
            usuario = new Usuario(conexion);
            
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
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrarse");
            System.out.println("3. Salir");
            System.out.print("Selecciona una opción (1, 2, 3): ");

            int opcion = scanner.nextInt();  // Leer la opción del usuario

            switch (opcion) {
                case 1:
                    if (auth.login()) {  // Si el login es exitoso
                        menuPrincipal();
                    }
                    break;
                case 2:
                    auth.register();    
                    break;
                case 3:
                    System.out.println("Hasta luego");
                    salir = true;    
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, elige una opción entre 1 y 3.");
            }
        }
        scanner.close(); 
    }

    public static void menuPrincipal() {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("Menu Principal:");
            System.out.println("1. Producto");
            System.out.println("2. Carrito");
            System.out.println("3. MongoDB");
            System.out.println("4. Inventario");
            System.out.println("5. Modificar cuenta Usuario");
            System.out.println("6. Eliminar Cuenta");
            System.out.println("7. Salir");
            System.out.print("Selecciona una opcion (1-7): ");

            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    menuProductos();
                    break;
                case 2:
                    menuCarrito();
                    break;
                case 3:
                    menuMongo();
                    break;
                case 4:
                    menuInventario();
                    break;
                case 5:
                    usuario.modificarUsuarioLogueado();
                    break;
                case 6:
                    usuario.eliminarUsuario();
                    break;
                case 7:
                    System.out.println("Hasta luego");
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida, selecciona entre 1 y 7.");
            }
        }
    }

    public static void menuProductos() {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("Menu Productos:");
            System.out.println("1. Eliminar Producto");
            System.out.println("2. Actualizar Producto");
            System.out.println("3. Ver Productos");
            System.out.println("4. Crear producto");
            System.out.println("5. Salir");
            System.out.print("Selecciona una opcion (1-5): ");
            
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    producto.eliminarProducto();
                    break;
                case 2:
                    producto.modificarProducto();
                    break;
                case 3:
                    menuVerProductos();
                    break;
                case 4:
                    producto.crearProducto();
                    break;
                case 5:
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida, selecciona entre 1 y 4.");
            }
        }
    }

    public static void menuVerProductos() {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("Ver Productos:");
            System.out.println("1. Ver todos los productos");
            System.out.println("2. Ver productos más vendidos");
            System.out.println("3. Ver productos por categoría");
            System.out.println("4. Productos con descuento por día");
            System.out.println("5. Salir");
            System.out.print("Selecciona una opcion (1-5): ");
            
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    producto.obtenerTodosLosProductos();
                    break;
                case 2:
                    producto.obtenerProductosMasVendidos();
                    break;
                case 3:
                    producto.filtrar_productos();
                    break;
                case 4:
                    producto.obtener_productos_descuento();
                    break;
                case 5:
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida, selecciona entre 1 y 5.");
            }
        }
    }

    public static void menuCarrito() {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("Menu Carrito:");
            System.out.println("1. Obtener productos del carrito");
            System.out.println("2. Agregar productos al carrito");
            System.out.println("3. Vaciar Carrito");
            System.out.println("4. Pagar");
            System.out.println("5. Eliminar Producto del carrito");
            System.out.println("6. Salir");
            System.out.print("Selecciona una opcion (1-6): ");
            
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    carrito.obtenerProductosEnCarrito();
                    break;
                case 2:
                    carrito.agregarProductoAlCarrito();
                    break;
                case 3:
                    carrito.vaciarCarrito();
                    break;
                case 4:
                    menuPagar();
                    break;
                case 5:
                    carrito.eliminarProductoDelCarrito();// Eliminar producto del carrito
                    break;
                case 6:
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida, selecciona entre 1 y 6.");
            }
        }
    }

    public static void menuPagar() {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("Menu Pagar:");
            System.out.println("1. Solo puntos");
            System.out.println("2. Solo dinero");
            System.out.println("3. Dinero y puntos");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opcion (1-4): ");
            
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    pago.registrarPagoPuntos();
                    break;
                case 2:
                    pago.registrarPagoEfectivo();
                    break;
                case 3:
                    pago.registrarPagoMixto();
                    break;
                case 4:
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida, selecciona entre 1 y 4.");
            }
        }
    }

    public static void menuMongo() {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("Menu MongoDB:");
            System.out.println("1. Ver auditorías");
            System.out.println("2. Auditoría por nombre de usuario y producto");
            System.out.println("3. Salir");
            System.out.print("Selecciona una opcion (1-3): ");
            
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    // Ver auditorías
                    break;
                case 2:
                    // Auditoría por nombre de usuario y producto
                    break;
                case 3:
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida, selecciona entre 1 y 3.");
            }
        }
    }

    public static void menuInventario() {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("Menu Inventario:");
            System.out.println("1. Ver inventario de un producto");
            System.out.println("2. Actualizar inventario de un producto");
            System.out.println("3. Reducir inventario de un producto");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opcion (1-3): ");
            
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    inventario.consultarInventario();// Ver inventario de un producto
                    break;
                case 2:
                    inventario.actualizarInventario();// Añadir inventario a un producto
                    break;
                case 3:
                    inventario.reducirInventario();
                    break;
                case 4:
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida, selecciona entre 1 y 3.");
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
