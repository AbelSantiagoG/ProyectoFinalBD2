/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.entities;

import jakarta.persistence.*;  // Para JPA
import java.io.Serializable;

@Entity
@Table(name = "inventarios")
public class Inventario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventarioSeqGen")
    @SequenceGenerator(name = "inventarioSeqGen", sequenceName = "inventarioSecuencia", allocationSize = 1)
    private Long id;

    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible;

    @Column(name = "referencia_compra", nullable = false, unique = true, length = 30)
    private String referenciaCompra;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Constructor vacío (requerido por JPA)
    public Inventario() {
    }

    // Constructor con parámetros
    public Inventario(Integer cantidadDisponible, String referenciaCompra, Producto producto) {
        this.cantidadDisponible = cantidadDisponible;
        this.referenciaCompra = referenciaCompra;
        this.producto = producto;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getReferenciaCompra() {
        return referenciaCompra;
    }

    public void setReferenciaCompra(String referenciaCompra) {
        this.referenciaCompra = referenciaCompra;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

}
