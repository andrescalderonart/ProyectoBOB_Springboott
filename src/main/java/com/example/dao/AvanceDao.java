package com.example.dao;

import com.example.domain.Avance;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AvanceDao extends CrudRepository<Avance, Integer> {

    // Find by exact ID matches
    List<Avance> findByIdAvance(Integer idAvance);
    List<Avance> findByIdUsuario(String idUsuario);
    List<Avance> findByIdObra(Integer idObra);
    List<Avance> findByIdMatriz(Integer idMatriz);

    // Find by fecha (exact and partial match)
    List<Avance> findByFecha(String fecha);
    List<Avance> findByFechaContaining(String fecha);

    // Combined queries using existing fields
    List<Avance> findByIdUsuarioAndFecha(String idUsuario, String fecha);



}
