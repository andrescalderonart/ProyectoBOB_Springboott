package com.example.dao;

import com.example.domain.Presupuesto;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface PersupuestoDao extends CrudRepository<Presupuesto, Integer> {

    // Find budgets by exact work name match (case-sensitive)
    List<Presupuesto> findByObraName(String obraName);

    // Find budgets by work name containing the given string (case-sensitive)
    List<Presupuesto> findByObraNameContaining(String obraName);

    // Find budgets by exact work name match (case-insensitive)
    List<Presupuesto> findByObraNameIgnoreCase(String obraName);

    // Find budgets by work name containing the given string (case-insensitive)
    List<Presupuesto> findByObraNameContainingIgnoreCase(String obraName);

}