/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.entities;

import jakarta.persistence.*;  // Para JPA
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "puntos_redimidos")
public class PuntosRedimidos implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "puntosRedimidosSeqGen")
    @SequenceGenerator(name = "puntosRedimidosSeqGen", sequenceName = "puntosRedimidosSecuencia", allocationSize = 1)
    private Long id;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "fecha_redencion", nullable = false)
    private LocalDate fechaRedencion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Constructor vacío (requerido por JPA)
    public PuntosRedimidos() {
    }

    // Constructor con parámetros
    public PuntosRedimidos(Integer cantidad, LocalDate fechaRedencion, Usuario usuario) {
        this.cantidad = cantidad;
        this.fechaRedencion = fechaRedencion;
        this.usuario = usuario;
    }

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

    public LocalDate getFechaRedencion() {
        return fechaRedencion;
    }

    public void setFechaRedencion(LocalDate fechaRedencion) {
        this.fechaRedencion = fechaRedencion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
