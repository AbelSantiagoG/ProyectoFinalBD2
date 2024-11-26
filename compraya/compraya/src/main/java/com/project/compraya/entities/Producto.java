/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.entities;

import jakarta.persistence.*;

/**
 *
 * @author ASUS
 */
@Entity
@Table( name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,  generator = "prodcutoSqGen")
    @SequenceGenerator( name = "productoSeqGen", sequenceName = "productoSecuencia", allocationSize = 1) 
    private Long id;
    
    
    @Column (name = "nombre", nullable = false, unique = true, length = 40)
    private String nombre;

    @Column(name = "description" , length = 100)
    private String description;
    
    @Column(name = "precio", nullable = false)
    private float precio;
    
    @Column(name = "imagen", length = 100)
    private String name;
    
    @Column(name = "descuento")
    private int descuento;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public Producto() {
    }

    public Producto(Long id, String nombre, String description, float precio, String name, int descuento, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.description = description;
        this.precio = precio;
        this.name = name;
        this.descuento = descuento;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDescuento() {
        return descuento;
    }

    public void setDescuento(int descuento) {
        this.descuento = descuento;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    
    }
