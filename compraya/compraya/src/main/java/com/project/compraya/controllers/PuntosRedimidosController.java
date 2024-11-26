/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.controllers;

import com.project.compraya.entities.PuntosRedimidos;
import com.project.compraya.repositories.PuntosRedimidosRepository;
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
@RequestMapping("/api/puntosRedimidos")
public class PuntosRedimidosController {
    @Autowired
    private PuntosRedimidosRepository puntosRedimidosRepository;

    @GetMapping("/get")
    public List<PuntosRedimidos> getPuntosRedimidos() {
        return puntosRedimidosRepository.findAll();
    }
}
