package com.example.servicio;

import com.example.domain.Matriz;
import java.util.List;

public interface MatrizServicio {

    List<Matriz> listarElementos();

    Matriz obtenerPorId(Integer id_m);

    void guardar(Matriz material);

    void eliminar(Matriz material);

    // Optional: If you need to search materials
    List<Matriz> buscarPorNombre(String nombre);
}
