package com.example.servicio;

import com.example.dao.MatrizDao;
import com.example.dao.PersupuestoDao;
import com.example.domain.Matriz;
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
    private MatrizDao mateDao;

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

    @Override
    @Transactional
    public void actualizar(Presupuesto presu) {
        presuDao.save(presu);
    }

    @Override
    @Transactional(readOnly = true)

    public Presupuesto localizarPresupuesto(Integer entryId) {
        return presuDao.findById(entryId).orElse(null);
    }

    public List<Presupuesto> findByObraName(String obraName) {
        return presuDao.findByObraName(obraName);
    }
    public List<Presupuesto> findByObraNameContaining(String obraName) {
        return presuDao.findByObraNameContaining(obraName);
    }
    public List<Presupuesto> findByObraNameIgnoreCase(String obraName) {
        return presuDao.findByObraNameIgnoreCase(obraName);
    }


    public List<Matriz> listarMateriales() {
        return (List<Matriz>) mateDao.findAll();
    }



}