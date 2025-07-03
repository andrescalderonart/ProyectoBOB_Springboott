package com.example.servicio;

import com.example.dao.PermisoDao;
import com.example.domain.Permiso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermisoServicioImpl implements PermisoServicio {

    @Autowired
    private PermisoDao permisoDao;


    @Override
    public List<Permiso> listarPermisos(){
        return permisoDao.findAll();
    }

    @Override
    public void guardar(Permiso permiso) {

    }

    @Override
    public Permiso buscarPorNombre(String nombre) {
        return null;
    }
}
