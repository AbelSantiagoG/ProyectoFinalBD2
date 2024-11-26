/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.controllers;

import com.project.compraya.repositories.FacturaRepository;
import java.util.List;
import com.project.compraya.entities.Factura;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author ASUS
 */


@RestController
@RequestMapping("/api/facturas")
public class FacturaController {
    @Autowired
    private FacturaRepository facturaRepository;

    @GetMapping("/get")
    public List<Factura> getFacturas() {
        return facturaRepository.findAll();
    }
}
