package com.project.compraya.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "usuarios") // Opcional: especifica el nombre de la tabla en la base de datos
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estrategia de generación de ID
    private Long id;

    @Column(name = "numero_documento", unique = true, nullable = false, length = 15)
    private String numeroDocumento;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "contrasenia", nullable = false, length = 50)
    private String contrasenia;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "celular", length = 20)
    private String celular;

    @Column(name = "puntos")
    private Integer puntos;

    @Column(name = "rol")
    private Integer rol;

    // Constructor por defecto requerido por JPA
    public Usuario() {
    }

    // Constructor con parámetros (opcional)
    public Usuario(String numeroDocumento, String nombre, String contrasenia, String email, String celular, Integer puntos, Integer rol) {
        this.numeroDocumento = numeroDocumento;
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.email = email;
        this.celular = celular;
        this.puntos = puntos;
        this.rol = rol;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public Integer getRol() {
        return rol;
    }

    public void setRol(Integer rol) {
        this.rol = rol;
    }

    // Métodos `equals` y `hashCode` para garantizar la comparación correcta de objetos

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) &&
               Objects.equals(numeroDocumento, usuario.numeroDocumento) &&
               Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numeroDocumento, email);
    }

    // Método `toString` para imprimir el objeto Usuario (opcional)
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", celular='" + celular + '\'' +
                ", puntos=" + puntos +
                ", rol=" + rol +
                '}';
    }
}
