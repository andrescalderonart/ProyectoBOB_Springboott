// src/main/java/com/example/dao/ContratistaDao.java
package com.example.dao;

import com.example.domain.Contratista;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContratistaDao extends JpaRepository<Contratista, Long> {

        boolean existsByInformacionComercial_NitRut(String nitRut);
        boolean existsByInformacionComercial_NitRutAndIdContratistaNot(String nitRut, Long idContratista);

        Contratista findByInformacionComercial_NitRut(String nitRut);

        @EntityGraph(attributePaths = {"idPersona","informacionComercial"})
        List<Contratista> findAll();

        @EntityGraph(attributePaths = {"idPersona","informacionComercial"})
        @Query("select c from Contratista c")
        List<Contratista> findAllWithRels();

        @Query("""
           select distinct c
           from Contratista c
           left join fetch c.idPersona per
           left join fetch c.informacionComercial ic
           """)
        List<Contratista> findAllJoinFetch();
}
