package com.example.dao;

import com.example.domain.Matriz;
import org.springframework.data.repository.CrudRepository;
import java.util.List;


public interface MatrizDao extends CrudRepository<Matriz, Integer> {

    List<Matriz> findByNombreContainingIgnoreCase(String nombre);

}
