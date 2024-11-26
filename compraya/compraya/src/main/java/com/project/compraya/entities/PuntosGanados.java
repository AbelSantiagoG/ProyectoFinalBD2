/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.compraya.entities;

import jakarta.persistence.*; 
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "puntos_ganados")
public class PuntosGanados implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "puntosGanadosSeqGen")
    @SequenceGenerator(name = "puntosGanadosSeqGen", sequenceName = "puntosGanadosSecuencia", allocationSize = 1)
    private Long id;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "fecha_ganacia", nullable = false)
    private LocalDate fechaGanacia;

    @Column(name = "motivo", nullable = false, length = 50)
    private String motivo;

    @Column(name = "referencia", length = 20)
    private String referencia;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Constructor vacío (requerido por JPA)
    public PuntosGanados() {
    }

    // Constructor con parámetros
    public PuntosGanados(Integer cantidad, LocalDate fechaGanacia, String motivo, String referencia, Usuario usuario) {
        this.cantidad = cantidad;
        this.fechaGanacia = fechaGanacia;
        this.motivo = motivo;
        this.referencia = referencia;
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

    public LocalDate getFechaGanacia() {
        return fechaGanacia;
    }

    public void setFechaGanacia(LocalDate fechaGanacia) {
        this.fechaGanacia = fechaGanacia;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
