/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.controllers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api") // Prefijo común para todas las rutas
public class HelloWorldController {
    // Define un endpoint para manejar solicitudes GET
    @GetMapping("/hello")
    public String sayHello() {
        return "¡Hola, mundo!";
    }
}
