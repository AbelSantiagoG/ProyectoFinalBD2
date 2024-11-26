/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "carritos")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carrito_generator")
    @SequenceGenerator(name = "carrito_generator", sequenceName = "carritoSecuencia", allocationSize = 1)
    private Long id;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}

