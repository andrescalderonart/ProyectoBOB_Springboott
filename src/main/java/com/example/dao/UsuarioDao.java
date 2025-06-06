package com.example.dao;

import com.example.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioDao extends JpaRepository <Usuario, Integer> {
    Usuario findBynombreUsuario(String username);
}
