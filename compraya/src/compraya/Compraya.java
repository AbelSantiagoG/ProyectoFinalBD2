/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package compraya;

import java.io.StringReader;
import java.io.StringWriter;
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
            //crearProducto("Papas 2", "Papas", new BigDecimal(10000), "imagen.jpg", 10, 1);
            //eliminarProducto(2);
            // Modificar el producto con ID 2
            //modificarProducto(2,"Papas Fritas","Papas con sal",12000.0, "nueva_imagen.jpg",15,2 );
            //filtrar_productos(1, new BigDecimal("10000.00"), new BigDecimal("50000.00"), 10, 50);     
            // Convertir una fecha específica a java.sql.Date
            
            //Date fechaActual = Date.valueOf("2024-12-05");
            // Llamar al método con la fecha
            //obtener_productos_descuento(fechaActual);
      
            // Llamar a la función para generar el XML de una factura
            //            int facturaId = 1; // Cambia este valor según el ID de la factura
            //            String facturaXML = obtenerFacturaXML(facturaId);
            //
            //            // Verificar y formatear el XML
            //            if (facturaXML != null) {
            //                String xmlBonito = formatearXML(facturaXML);
            //                System.out.println(xmlBonito); // Mostrar el XML formateado
            //            } else {
            //                System.out.println("No se pudo generar el XML para la factura con ID " + facturaId);
            //            }
        obtenerTodosLosProductos();
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


    public static void crearProducto() {
        login();
        Scanner scanner = new Scanner(System.in);

        // Solicitar los parámetros al usuario
        System.out.print("Ingrese el nombre del producto: ");
        String nombre = scanner.nextLine();

        System.out.print("Ingrese la descripción del producto: ");
        String descripcion = scanner.nextLine();

        System.out.print("Ingrese el precio del producto: ");
        BigDecimal precio = scanner.nextBigDecimal();

        scanner.nextLine();  // Consumir la nueva línea pendiente

        System.out.print("Ingrese la imagen del producto: ");
        String imagen = scanner.nextLine();

        System.out.print("Ingrese el descuento del producto: ");
        int descuento = scanner.nextInt();

        System.out.print("Ingrese el ID de la categoría: ");
        int categoriaId = scanner.nextInt();

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.crear_producto(?, ?, CAST(? AS numeric), ?, ?, ?)");

            // Establecer los parámetros del procedimiento
            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.setBigDecimal(3, precio);  // Cambiar a setBigDecimal
            stmt.setString(4, imagen);
            stmt.setInt(5, descuento);
            stmt.setInt(6, categoriaId);

            stmt.execute();
            System.out.println("Producto creado exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al crear el producto: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    public static void modificarProducto() {
        login();
        Scanner scanner = new Scanner(System.in);

        // Solicitar los parámetros al usuario
        System.out.print("Ingrese el ID del producto a modificar: ");
        int productoId = scanner.nextInt();

        scanner.nextLine();  // Consumir la nueva línea pendiente

        System.out.print("Ingrese el nuevo nombre del producto (o deje vacío para no modificarlo): ");
        String nombre = scanner.nextLine();

        System.out.print("Ingrese la nueva descripción del producto (o deje vacío para no modificarlo): ");
        String descripcion = scanner.nextLine();

        System.out.print("Ingrese el nuevo precio del producto (o deje vacío para no modificarlo): ");
        Double precio = scanner.hasNextDouble() ? scanner.nextDouble() : null;

        scanner.nextLine();  // Consumir la nueva línea pendiente

        System.out.print("Ingrese la nueva imagen del producto (o deje vacío para no modificarlo): ");
        String imagen = scanner.nextLine();

        System.out.print("Ingrese el nuevo descuento del producto (o deje vacío para no modificarlo): ");
        Integer descuento = scanner.hasNextInt() ? scanner.nextInt() : null;

        System.out.print("Ingrese el nuevo ID de categoría (o deje vacío para no modificarlo): ");
        Integer categoriaId = scanner.hasNextInt() ? scanner.nextInt() : null;

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.modificar_producto(?,?, ?, CAST(? AS numeric), ?, ?, ?)");

            stmt.setInt(1, productoId);

            if (nombre != null && !nombre.isEmpty()) {
                stmt.setString(2, nombre);
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }

            if (descripcion != null && !descripcion.isEmpty()) {
                stmt.setString(3, descripcion);
            } else {
                stmt.setNull(3, java.sql.Types.VARCHAR);
            }

            if (precio != null) {
                stmt.setBigDecimal(4, new BigDecimal(precio));
            } else {
                stmt.setNull(4, java.sql.Types.DOUBLE);
            }

            if (imagen != null && !imagen.isEmpty()) {
                stmt.setString(5, imagen);
            } else {
                stmt.setNull(5, java.sql.Types.VARCHAR);
            }

            if (descuento != null) {
                stmt.setInt(6, descuento);
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            if (categoriaId != null) {
                stmt.setInt(7, categoriaId);
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            stmt.execute();
            System.out.println("Producto con ID " + productoId + " modificado exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al modificar el producto: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
     
    public static void eliminarProducto() {
        login();
        Scanner scanner = new Scanner(System.in);

        // Solicitar el ID del producto a eliminar
        System.out.print("Ingrese el ID del producto a eliminar: ");
        int productoId = scanner.nextInt();

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("CALL compraya.eliminar_producto(?)");

            // Establecer el parámetro del procedimiento
            stmt.setInt(1, productoId);

            stmt.execute();
            System.out.println("Producto eliminado exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar el producto: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }   
        
    public static void obtenerTodosLosProductos() {
        login();
        CallableStatement stmt = null;
        try 
            {
             stmt = conexion.prepareCall("SELECT * FROM compraya.obtener_todos_los_productos()");
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                BigDecimal precio = rs.getBigDecimal("precio");
                String imagen = rs.getString("imagen");
                BigDecimal descuento = rs.getBigDecimal("descuento");
                int categoriaId = rs.getInt("categoria_id");

                System.out.println("ID: " + id +
                                   ", Nombre: " + nombre +
                                   ", Descripción: " + descripcion +
                                   ", Precio: " + precio +
                                   ", Imagen: " + imagen +
                                   ", Descuento: " + descuento +
                                   ", Categoría ID: " + categoriaId);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
    }
     
   public static void filtrar_productos() {
        login();
        Scanner scanner = new Scanner(System.in);

        // Solicitar los parámetros al usuario
        System.out.print("Ingrese el ID de categoría: ");
        int categoriaId = scanner.nextInt();

        System.out.print("Ingrese el precio mínimo: ");
        BigDecimal precioMin = scanner.nextBigDecimal();

        System.out.print("Ingrese el precio máximo: ");
        BigDecimal precioMax = scanner.nextBigDecimal();

        System.out.print("Ingrese el descuento mínimo: ");
        int descuentoMin = scanner.nextInt();

        System.out.print("Ingrese el descuento máximo: ");
        int descuentoMax = scanner.nextInt();

        CallableStatement stmt = null;
        try {
            stmt = conexion.prepareCall("SELECT * FROM compraya.filtrar_productos(?, CAST(? AS numeric), CAST(? AS numeric), ?, ?)");

            // Asignar los parámetros
            stmt.setInt(1, categoriaId);
            stmt.setBigDecimal(2, precioMin);
            stmt.setBigDecimal(3, precioMax);
            stmt.setInt(4, descuentoMin);
            stmt.setInt(5, descuentoMax);

            // Ejecutar la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Imprimir los resultados
                    System.out.println("ID: " + rs.getInt("id") +
                            ", Nombre: " + rs.getString("nombre") +
                            ", Descripción: " + rs.getString("descripcion") +
                            ", Precio: " + rs.getBigDecimal("precio") +
                            ", Imagen: " + rs.getString("imagen") +
                            ", Descuento: " + rs.getInt("descuento") +
                            ", Categoría ID: " + rs.getInt("categoria_id"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al filtrar productos: " + e.getMessage());
        }
    }
    
    
    public static void obtener_productos_descuento() {
        login();
        Scanner scanner = new Scanner(System.in);

        // Solicitar la fecha actual al usuario
        System.out.print("Ingrese la fecha actual (en formato yyyy-mm-dd): ");
        String fechaStr = scanner.nextLine();
        Date fechaActual = Date.valueOf(fechaStr);  // Convertir la cadena a Date

        CallableStatement stmt = null;
        try {
            // Preparar la llamada a la función almacenada
            stmt = conexion.prepareCall("SELECT * FROM compraya.obtener_productos_descuento(?)");
            stmt.setDate(1, fechaActual); // Pasar la fecha como java.sql.Date

            // Ejecutar la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                boolean found = false; // Verificar si hay resultados
                while (rs.next()) {
                    found = true; // Se encontraron resultados
                    // Imprimir los resultados
                    System.out.println("ID: " + rs.getInt("id") +
                            ", Nombre: " + rs.getString("nombre") +
                            ", Descripción: " + rs.getString("descripcion") +
                            ", Precio Original: " + rs.getBigDecimal("precio_original") +
                            ", Precio con Descuento: " + rs.getBigDecimal("precio_descuento") +
                            ", Descuento Aplicado: " + rs.getInt("descuento_aplicado"));
                }
                if (!found) {
                    System.out.println("No se encontraron productos con descuento para la fecha: " + fechaActual);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los productos con descuento: " + e.getMessage());
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
    
    public static String obtenerFacturaXML(int facturaId) {
        login();
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
        

    public static String formatearXML(String xml) {
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
    public static void initConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "andres2003");
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
