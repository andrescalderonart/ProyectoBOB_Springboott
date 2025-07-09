package com.example.servicio;

import com.example.dao.AvanceDao;
import com.example.dao.MatrizDao;
import com.example.domain.Avance;
import com.example.domain.Matriz;
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

    // Implementation of new search methods
    @Override
    @Transactional(readOnly = true)
    public List<Avance> buscarPorIdAvance(Integer id_avance) {
        return avanceDao.findByIdAvance(id_avance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avance> buscarPorIdUsuario(Integer id_usuario) {
        return avanceDao.findByIdUsuario(id_usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avance> buscarPorIdObra(Integer id_obra) {
        return avanceDao.findByIdObra(id_obra);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avance> buscarPorIdMatriz(Integer id_matriz) {
        return avanceDao.findByIdMatriz(id_matriz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avance> buscarPorFecha(String fecha) {
        return avanceDao.findByFecha(fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avance> buscarPorFechaConteniendo(String fecha) {
        return avanceDao.findByFechaContaining(fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avance> buscarPorUsuarioYFecha(Integer id_usuario, String fecha) {
        return avanceDao.findByIdUsuarioAndFecha(id_usuario, fecha);
    }


    public List<Matriz> listarMateriales() {
        return (List<Matriz>) mateDao.findAll();
    }



}