package com.example.servicio;

import com.example.dao.MatrizDao;
import com.example.domain.Matriz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatrizServicioImp implements MatrizServicio {

    @Autowired
    private MatrizDao matrizDao;

    @Override
    @Transactional(readOnly = true)
    public List<Matriz> listarElementos() {
        return (List<Matriz>) matrizDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Matriz obtenerPorId(Integer id_m) {
        return matrizDao.findById(id_m).orElse(null);
    }

    @Override
    @Transactional
    public void guardar(Matriz material) {
        matrizDao.save(material);
    }

    @Override
    @Transactional
    public void eliminar(Matriz material) {
        matrizDao.delete(material);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Matriz> buscarPorNombre(String nombre) {
        return matrizDao.findByNombreContainingIgnoreCase(nombre);
    }
}