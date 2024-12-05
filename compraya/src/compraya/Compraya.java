/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package compraya;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.PreparedStatement;

/**
 *
 * @author Usuario
 */
public class Compraya {
    public static Connection conexion;

    public static void main(String[] args) {
        try {
            initConnection();
            
            //mostrarMenu();
            
            
        } catch (Exception e) {
            System.err.println("Error en la aplicacion: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    

    // Método para registrar un usuario
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


//    public static void crearProducto(String nombre, String descripcion, BigDecimal precio, String imagen, int descuento, int categoriaId) {
//        CallableStatement stmt = null;
//        try {
//            stmt = conexion.prepareCall(" CALL crear_producto(?, ?, ?, ?, ?, ?) ");
//
//            // Establecer los parámetros del procedimiento
//            stmt.setString(1, nombre);
//            stmt.setString(2, descripcion);
//            stmt.setBigDecimal(3, precio);  // Cambiar a setBigDecimal
//            stmt.setString(4, imagen);
//            stmt.setInt(5, descuento);
//            stmt.setInt(6, categoriaId);
//
//            stmt.execute();
//            System.out.println("Producto creado exitosamente.");
//
//        } catch (SQLException e) {
//            System.err.println("Error al crear el producto: " + e.getMessage());
//        } finally {
//            try {
//                if (stmt != null) stmt.close();
//                if (conexion != null) conexion.close();
//            } catch (SQLException e) {
//                System.err.println("Error al cerrar la conexión: " + e.getMessage());
//            }
//        }
//    }
    
//     public static void modificarProducto(int productoId, String nombre, String descripcion, Double precio, String imagen, Integer descuento, Integer categoriaId) {
//        CallableStatement stmt = null;
//        try {
//            // Preparar la llamada al procedimiento almacenado
//            stmt = conexion.prepareCall("{ CALL modificar_producto(?, ?, ?, ?, ?, ?, ?) }");
//
//            // Establecer los parámetros del procedimiento
//            stmt.setInt(1, productoId); // ID del producto (obligatorio)
//
//            // Parámetros opcionales, solo pasar los que no son null
//            if (nombre != null) {
//                stmt.setString(2, nombre); // Si el nombre es diferente de null, lo seteamos
//            } else {
//                stmt.setNull(2, java.sql.Types.VARCHAR); // Si es null, pasamos un valor nulo
//            }
//
//            if (descripcion != null) {
//                stmt.setString(3, descripcion); // Si la descripción es diferente de null, lo seteamos
//            } else {
//                stmt.setNull(3, java.sql.Types.VARCHAR); // Si es null, pasamos un valor nulo
//            }
//
//            if (precio != null) {
//                stmt.setDouble(4, precio); // Si el precio es diferente de null, lo seteamos
//            } else {
//                stmt.setNull(4, java.sql.Types.DOUBLE); // Si es null, pasamos un valor nulo
//            }
//
//            if (imagen != null) {
//                stmt.setString(5, imagen); // Si la imagen es diferente de null, lo seteamos
//            } else {
//                stmt.setNull(5, java.sql.Types.VARCHAR); // Si es null, pasamos un valor nulo
//            }
//
//            if (descuento != null) {
//                stmt.setInt(6, descuento); // Si el descuento es diferente de null, lo seteamos
//            } else {
//                stmt.setNull(6, java.sql.Types.INTEGER); // Si es null, pasamos un valor nulo
//            }
//
//            if (categoriaId != null) {
//                stmt.setInt(7, categoriaId); // Si el ID de categoría es diferente de null, lo seteamos
//            } else {
//                stmt.setNull(7, java.sql.Types.INTEGER); // Si es null, pasamos un valor nulo
//            }
//
//            // Ejecutar el procedimiento
//            stmt.execute();
//            System.out.println("Producto con ID " + productoId + " modificado exitosamente.");
//
//        } catch (SQLException e) {
//            System.err.println("Error al modificar el producto: " + e.getMessage());
//        } finally {
//            try {
//                // Cerrar los recursos
//                if (stmt != null) stmt.close();
//                if (conexion != null) conexion.close();
//            } catch (SQLException e) {
//                System.err.println("Error al cerrar la conexión: " + e.getMessage());
//            }
//        }
//    }
    
    
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
                    login();  // Llamar al método para iniciar sesión
                    break;
                case 2:
                    register();    
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
