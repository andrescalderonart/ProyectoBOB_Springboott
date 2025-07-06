package com.example.servicio;

import com.example.dao.InventarioDao;
import com.example.domain.Inventario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@Service
public class InventarioServicioImp implements InventarioServicio {

    @Autowired
    private InventarioDao inventarioDao;

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> listaInventarios() {
        return (List<Inventario>) inventarioDao.findAll();
    }

    @Override
    @Transactional
    public void guardarInv(Inventario inventario) {
        inventarioDao.save(inventario);
    }

    @Override
    @Transactional
    public void cambiarInv(Inventario inventario) {
        inventarioDao.save(inventario);
    }

    @Override
    @Transactional
    public void borrarInv(Inventario inventario) {
        inventarioDao.delete(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public Inventario localizarInventarioPorId(Long id) {
        return inventarioDao.findById(id).orElse(null);
    }

    // Métodos de búsqueda implementados
    @Override
    @Transactional(readOnly = true)
    public List<Inventario> buscarPorNombreGestor(String nombreGestor) {
        return inventarioDao.findByNombreGestorContainingIgnoreCase(nombreGestor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> buscarPorNombreObra(String nombreObra) {
        return inventarioDao.findByNombreobraContainingIgnoreCase(nombreObra);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> buscarPorFecha(String fecha) {
        try {
            // Convertir String a LocalDate
            LocalDate fechaBusqueda = LocalDate.parse(fecha);
            return inventarioDao.findByFecha(fechaBusqueda);
        } catch (DateTimeParseException e) {
            System.err.println("Formato de fecha inválido: " + fecha);
            return Collections.emptyList();
        }
    }
}