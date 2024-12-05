/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compraya;

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
public class Abel {
    private static Connection conexiona;

    public Abel(Connection conexion1) {
        this.conexiona = conexion1;
    }

    public static void eliminarUsuario(int usuarioId) {
        CallableStatement stmt = null;
        try {
            stmt = conexiona.prepareCall(" CALL compraya.eliminar_usuario(?) ");

            stmt.setInt(1, usuarioId);

            stmt.execute();
            System.out.println("Usuario con ID " + usuarioId + " eliminado exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar el usuario: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexiona != null) conexiona.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    public static void obtenerTodosLosUsuarios() {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Prepara la consulta para ejecutar la función
            String sql = "SELECT * FROM compraya.obtener_todos_los_usuarios()";
            stmt = conexiona.prepareStatement(sql);

            // Ejecuta la consulta y obtiene los resultados
            rs = stmt.executeQuery();

            // Procesa los resultados
            System.out.println("Usuarios:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String numeroDocumento = rs.getString("numero_documento");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");
                String celular = rs.getString("celular");
                int puntos = rs.getInt("puntos");

                System.out.printf("ID: %d, Documento: %s, Nombre: %s, Email: %s, Celular: %s, Puntos: %d%n",
                        id, numeroDocumento, nombre, email, celular, puntos);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los usuarios: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conexiona != null) conexiona.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
    
    //INVENTARIOS
    
    public static void crearInventario(int productoId, int cantidadDisponible, String referenciaCompra) {
        CallableStatement stmt = null;
        try {
            stmt = conexiona.prepareCall("CALL compraya.crear_inventario(?, ?, ?)");
            stmt.setInt(1, productoId);
            stmt.setInt(2, cantidadDisponible);
            stmt.setString(3, referenciaCompra);

            stmt.execute();
            System.out.println("Inventario creado exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al crear inventario: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar el CallableStatement: " + e.getMessage());
            }
        }
    }
    
    public static void actualizarInventario(int productoId, int nuevaCantidad) {
        CallableStatement stmt = null;
        try {
            stmt = conexiona.prepareCall("CALL compraya.actualizar_inventario(?, ?)");
            stmt.setInt(1, productoId);
            stmt.setInt(2, nuevaCantidad);

            stmt.execute();
            System.out.println("Inventario actualizado exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al actualizar inventario: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar el CallableStatement: " + e.getMessage());
            }
        }
    }
    
    public static void consultarInventario(int productoId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Preparar la consulta SELECT que llama a la función de PostgreSQL
            stmt = conexiona.prepareStatement("SELECT * FROM compraya.consultar_inventario(?)");

            // Establecer el parámetro de la función
            stmt.setInt(1, productoId);

            // Ejecutar la consulta y obtener el resultado
            rs = stmt.executeQuery();

            // Imprimir los resultados de la consulta
            while (rs.next()) {
                int id = rs.getInt("id");
                int cantidadDisponible = rs.getInt("cantidad_disponible");
                String referenciaCompra = rs.getString("referencia_compra");

                System.out.println("ID: " + id);
                System.out.println("Cantidad disponible: " + cantidadDisponible);
                System.out.println("Referencia compra: " + referenciaCompra);
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar inventario: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    
    public static void reducirInventario(int productoId, int cantidadReducir) {
        CallableStatement stmt = null;
        try {
            stmt = conexiona.prepareCall("CALL compraya.reducir_inventario(?, ?)");
            stmt.setInt(1, productoId);
            stmt.setInt(2, cantidadReducir);

            stmt.execute();
            System.out.println("Inventario reducido exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al reducir inventario: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar el CallableStatement: " + e.getMessage());
            }
        }
    }
    
    public static void eliminarInventario(int productoId) {
        CallableStatement stmt = null;
        try {
            stmt = conexiona.prepareCall("CALL compraya.eliminar_inventario(?)");
            stmt.setInt(1, productoId);

            stmt.execute();
            System.out.println("Inventario eliminado exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al eliminar inventario: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar el CallableStatement: " + e.getMessage());
            }
        }
    }
    
    public static boolean verificarDisponibilidad(int productoId, int cantidadSolicitada) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Preparar la consulta SELECT que llama a la función de PostgreSQL
            stmt = conexiona.prepareStatement("SELECT compraya.verificar_disponibilidad(?, ?)");

            // Establecer los parámetros de la función
            stmt.setInt(1, productoId);
            stmt.setInt(2, cantidadSolicitada);

            // Ejecutar la consulta y obtener el resultado
            rs = stmt.executeQuery();

            // Verificar si la disponibilidad es suficiente
            if (rs.next()) {
                return rs.getBoolean(1); // Devuelve el valor TRUE o FALSE de la función
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar disponibilidad: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return false; // Devuelve false si hubo algún problema al obtener los datos
    }
    
    public static void obtenerProductosMasVendidos() {
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            // Llamamos a la función compraya.obtener_productos_mas_vendidos()
            stmt = conexiona.prepareCall("SELECT * FROM compraya.obtener_productos_mas_vendidos()");

            // Ejecutamos la consulta
            rs = stmt.executeQuery();

            // Procesamos los resultados
            while (rs.next()) {
                int productoId = rs.getInt("producto_id");
                String nombre = rs.getString("nombre");
                int cantidadVendida = rs.getInt("cantidad_vendida");

                // Mostramos los resultados
                System.out.println("Producto ID: " + productoId + ", Nombre: " + nombre + ", Cantidad Vendida: " + cantidadVendida);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los productos más vendidos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    
    //GESTIÓN DEL CARRITO
    public static void agregarProductoAlCarrito(int usuarioId, int productoId, int cantidad) {
        CallableStatement stmt = null;
        try {
            stmt = conexiona.prepareCall("CALL compraya.agregar_producto_al_carrito(?, ?, ?)");

            stmt.setInt(1, usuarioId);
            stmt.setInt(2, productoId);
            stmt.setInt(3, cantidad);

            stmt.execute();
            System.out.println("Producto agregado al carrito exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al agregar el producto al carrito: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexiona != null) conexiona.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }


}







     

