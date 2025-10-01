package com.example.dao;

import com.example.domain.Apu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;


public interface APUDao extends JpaRepository<Apu, Long> {

    List<Apu> findByNombreAPUContainingIgnoreCase(String nombreAPU);
    // Si tus SecondaryTables necesitan estar presentes, con esta consulta te aseguras de traer todo
    @Query("""
           SELECT a FROM Apu a
           LEFT JOIN FETCH a.apusObraList
           LEFT JOIN FETCH a.materialesApus
           """)
    List<Apu> findAllWithRels();
}