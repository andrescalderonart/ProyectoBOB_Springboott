package com.example.servicio;


import com.example.dao.InventarioDao;
import com.example.domain.Inventario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventarioServicioImp implements InventarioServicio{

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
        // Para actualizar, simplemente guardamos el objeto modificado
        inventarioDao.save(inventario);
    }

    @Override
    @Transactional
    public void borrarInv(Inventario inventario) {
        inventarioDao.delete(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public Inventario localizarInventario(Inventario inventario) {
        return inventarioDao.findById(inventario.getId_inventario()).orElse(null);
    }
}
