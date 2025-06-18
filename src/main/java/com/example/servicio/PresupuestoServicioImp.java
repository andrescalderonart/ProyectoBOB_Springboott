package com.example.servicio;

import com.example.dao.MaterialDao;
import com.example.dao.PersupuestoDao;
import com.example.domain.Material;
import com.example.domain.Presupuesto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PresupuestoServicioImp implements PresupuestoServicio{

    @Autowired
    private PersupuestoDao presuDao;

    @Autowired
    private MaterialDao mateDao;

    @Override
    @Transactional(readOnly = true)
    public List<Presupuesto> listaPresupuesto() {
        return (List<Presupuesto>) presuDao.findAll();
    }

    @Override
    @Transactional
    public void salvar(Presupuesto presu) {
        presuDao.save(presu);
    }

    @Override
    @Transactional
    public void borrar(Presupuesto presu) {
        presuDao.delete(presu);
    }

    @Transactional(readOnly = true)
    @Override
    public Presupuesto localizarPresupuesto(Presupuesto presu) {
        return presuDao.findById(presu.getEntryId()).orElse(null);
    }
    public List<Material> listarMateriales() {
        return (List<Material>) mateDao.findAll();
    }
}
