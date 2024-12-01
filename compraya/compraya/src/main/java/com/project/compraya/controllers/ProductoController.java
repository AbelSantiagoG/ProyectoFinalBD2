/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.controllers;

import com.project.compraya.entities.Producto;
import com.project.compraya.repositories.ProductoRepository;
import jakarta.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        @RequestParam double precio,
        @RequestParam String imagen,
        @RequestParam int descuento,
        @RequestParam int categoriaId) {

    // Procedimiento SQL para llamar al procedimiento "crear_producto" en PostgreSQL
    String sql = "CALL compraya.crear_producto(?, ?, ?, ?, ?, ?)";

    // Intentamos ejecutar el procedimiento con los parámetros proporcionados
    try {
        jdbcTemplate.update(sql, nombre, descripcion, precio, imagen, descuento, categoriaId);
        return "Producto creado exitosamente.";
    } catch (Exception e) {
        return "Error al crear el producto: " + e.getMessage();
    }
}


}
