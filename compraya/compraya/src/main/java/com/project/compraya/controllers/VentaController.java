/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.controllers;

import com.project.compraya.entities.Venta;
import com.project.compraya.repositories.VentaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    @Autowired
    private VentaRepository ventaRepository;

    @GetMapping("/get")
    public List<Venta> getFacturas() {
        return ventaRepository.findAll();
    }
}
