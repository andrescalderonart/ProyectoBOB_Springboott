package com.example.servicio;

import com.example.dao.AvanceDao;
import com.example.dao.MatrizDao;
import com.example.dao.PersupuestoDao;
import com.example.domain.Avance;
import com.example.domain.Matriz;
import com.example.domain.Presupuesto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvanceServicioImp implements AvanceServicio{

    @Autowired
    private AvanceDao avanceDao;

    @Autowired
    private MatrizDao mateDao;

    @Override
    @Transactional(readOnly = true)
    public List<Avance> listaAvance() {
        return (List<Avance>) avanceDao.findAll();
    }

    @Override
    @Transactional
    public void salvar(Avance avance) {
        avanceDao.save(avance);
    }

    @Override
    @Transactional
    public void borrar(Avance avance) {
        avanceDao.delete(avance);
    }

    @Override
    @Transactional
    public void actualizar(Avance avance) {
        avanceDao.save(avance);
    }

    @Override
    @Transactional(readOnly = true)

    public Avance localizarAvance(Integer entryId) {
        return avanceDao.findById(entryId).orElse(null);
    }
    public List<Matriz> listarMateriales() {
        return (List<Matriz>) mateDao.findAll();
    }



}