package com.project.compraya.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.compraya.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Métodos personalizados si los necesitas
}
