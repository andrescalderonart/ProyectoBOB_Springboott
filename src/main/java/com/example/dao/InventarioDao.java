package com.example.dao;


import com.example.domain.Inventario;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDate;
import java.util.List;

public interface InventarioDao extends CrudRepository<Inventario, Long> {

    List<Inventario> findByNombreGestorContainingIgnoreCase(String nombreGestor);
    List<Inventario> findByNombreobraContainingIgnoreCase(String nombreobra);
    List<Inventario> findByFecha(LocalDate fecha);

}
