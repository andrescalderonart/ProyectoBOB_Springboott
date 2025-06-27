package com.example.dao;


import com.example.domain.Inventario;
import org.springframework.data.repository.CrudRepository;

public interface InventarioDao extends CrudRepository<Inventario, Long> {

}
