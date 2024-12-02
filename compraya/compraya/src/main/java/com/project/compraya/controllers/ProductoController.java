/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.controllers;

import com.project.compraya.entities.Producto;
import com.project.compraya.repositories.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;
            
    @GetMapping("/get")
    public List<Producto> getFacturas() {
        return productoRepository.findAll();
    }
    
@PostMapping("/create")
public String crearProducto(
        @RequestParam String nombre,
        @RequestParam String descripcion,
        @RequestParam BigDecimal precio,
        @RequestParam String imagen,
        @RequestParam int descuento,
        @RequestParam int categoriaId) {

    System.out.println("Parámetros:");
    System.out.println("Nombre: " + nombre);
    System.out.println("Descripción: " + descripcion);
    System.out.println("Precio: " + precio);
    System.out.println("Imagen: " + imagen);
    System.out.println("Descuento: " + descuento);
    System.out.println("Categoría ID: " + categoriaId);


    String sql = "CALL compraya.crear_producto(?, ?, ?, ?, ?, ?)";

    try {
        jdbcTemplate.update(sql, nombre, descripcion, precio, imagen, descuento, categoriaId);
        return "Producto creado exitosamente.";
    } catch (Exception e) {
        e.printStackTrace(); 
        return "Error al crear el producto: " + e.getMessage();
    }
    
}

@PutMapping("/{productoId}")
public String modificarProducto(
        @PathVariable Integer productoId,  // Obtener el ID del producto desde la ruta
        @RequestParam String nombre,
        @RequestParam String descripcion,
        @RequestParam BigDecimal precio,
        @RequestParam String imagen,
        @RequestParam int descuento,
        @RequestParam int categoriaId) {

    String sql = "CALL compraya.modificar_producto(?, ?, ?, ?, ?, ?, ?)";

    try {
        // Ejecutar el procedimiento almacenado
        jdbcTemplate.update(sql,
                productoId,       
                nombre,          
                descripcion,      
                precio,          
                imagen,          
                descuento,         
                categoriaId     
        );
        
        return "Producto modificado exitosamente.";
    } catch (Exception e) {
        // Manejo de errores
        e.printStackTrace();
        return "Error al modificar el producto: " + e.getMessage();
    }
}


@DeleteMapping("/{productoId}")
public String eliminarProducto(@PathVariable Integer productoId) {

    String sql = "CALL compraya.eliminar_producto(?)";

    try {
        jdbcTemplate.update(sql, productoId);
        return "Producto eliminado exitosamente.";
    } catch (Exception e) {
        e.printStackTrace();
        return "Error al eliminar el producto: " + e.getMessage();
    }
}










}
