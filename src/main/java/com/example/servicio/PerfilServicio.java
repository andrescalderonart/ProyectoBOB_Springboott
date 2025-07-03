package com.example.servicio;

import com.example.domain.Perfil;

import java.util.List;

public interface PerfilServicio {
    Perfil buscarPorId(Integer id);
    void guardar(Perfil perfil);
    List<Perfil> listarPerfiles();
}

