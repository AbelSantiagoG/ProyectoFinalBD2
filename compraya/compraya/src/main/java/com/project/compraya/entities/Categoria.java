/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.entities;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 *
 * @author ASUS
 */
@Entity
@Table(name = "categorias")
public class Categoria implements Serializable{
    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "categoriaSeqGen")
    @SequenceGenerator( name = "categoriaSeqGen", sequenceName = "categoriaSecuencia",  allocationSize = 1)
    private Long id;
    
    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    public Categoria() {
    }

    public Categoria(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
    
    
    }

