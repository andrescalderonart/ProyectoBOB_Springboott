package com.example.servicio;

import com.example.dao.PerfilDao;
import com.example.domain.Perfil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerfilServicioImpl implements PerfilServicio {

    @Autowired
    private PerfilDao perfilDao;

    @Override
    public Perfil buscarPorId(Integer id) {
        return perfilDao.findById(id).orElse(null);
    }

    @Override
    public void guardar(Perfil perfil) {
        perfilDao.save(perfil);
    }

    @Override
    public List<Perfil> listarPerfiles() {
        return perfilDao.findAll();
    }
}
