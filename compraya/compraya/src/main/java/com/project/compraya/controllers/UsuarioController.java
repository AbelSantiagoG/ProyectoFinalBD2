/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.controllers;
import com.project.compraya.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.compraya.entities.Usuario;

/**
 *
 * @author ASUS
 */
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String contrasenia) {

        String sql = "SELECT compraya.login_usuario(?, ?)";


        String resultado = jdbcTemplate.queryForObject(sql, new Object[]{email, contrasenia}, String.class);


        return resultado;
    }

    
    @GetMapping("/get")
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
    
    @PostMapping("/create")    
    public void crearUsuario(String numeroDocumento, String nombre, String contrasenia, String email, String celular, int rol, Integer puntos) {
        String sql = "CALL crear_usuario(?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, 
            numeroDocumento, 
            nombre, 
            contrasenia, 
            email, 
            celular, 
            rol, 
            (puntos != null) ? puntos : 0 
        );
    }
    
    @PutMapping("/{id}")    
    public void actualizarUsuario(
            @PathVariable Integer id, 
            String numeroDocumento, 
            String nombre, 
            String contrasenia,         
            String email, 
            String celular, 
            Integer puntos) {
        
        String sql = "CALL compraya.modificar_usuario(?,?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            id,
            numeroDocumento, 
            nombre, 
            contrasenia, 
            email, 
            celular, 
            (puntos != null) ? puntos : 0  // Manejar valor por defecto en Java si es nulo
        );
    }
    
        @DeleteMapping("/{id}")    
    public void eliminarUsuario(
            @PathVariable Integer id) {
        
        String sql = "CALL compraya.eliminar_usuario(?)";

        jdbcTemplate.update(sql,
            id );
    }



}
