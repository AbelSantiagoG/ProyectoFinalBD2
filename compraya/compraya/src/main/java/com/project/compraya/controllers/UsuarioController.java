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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

        @Autowired
    private JdbcTemplate jdbcTemplate;

    // Endpoint para realizar login
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String contrasenia) {
        // Llama a la función almacenada login_usuario
        String sql = "SELECT compraya.login_usuario(?, ?)";

        // Ejecuta la función almacenada y obtiene el resultado
        String resultado = jdbcTemplate.queryForObject(sql, new Object[]{email, contrasenia}, String.class);

        // Devuelve el resultado obtenido de la función almacenada
        return resultado;
    }

    
    @GetMapping("/get")
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}
