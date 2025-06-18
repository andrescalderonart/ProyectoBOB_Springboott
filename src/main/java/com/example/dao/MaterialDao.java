package com.example.dao;

import com.example.domain.Material;
import org.springframework.data.repository.CrudRepository;
import java.util.List;


public interface MaterialDao extends CrudRepository<Material, Integer> {

    List<Material> findAllByOrderByNombreAsc();

}
