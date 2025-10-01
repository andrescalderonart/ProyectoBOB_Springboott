// src/main/java/com/example/servicio/ContratistaServicio.java
package com.example.servicio;

import com.example.domain.Contratista;

import java.util.List;
import java.util.Optional;

public interface ContratistaServicio {
    List<Contratista> listar();
    Optional<Contratista> buscarPorId(Long id);
    Contratista guardar(Contratista contratista); // crea/actualiza
    void eliminar(Long id);
}
