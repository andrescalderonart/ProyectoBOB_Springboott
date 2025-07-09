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
    public List<Avance> buscarPorIdAvance(Integer idAvance) {
        return avanceDao.findByIdAvance(idAvance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avance> buscarPorIdUsuario(String idUsuario) {
        return avanceDao.findByIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avance> buscarPorIdObra(Integer idObra) {
        return avanceDao.findByIdObra(idObra);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avance> buscarPorIdMatriz(Integer idMatriz) {
        return avanceDao.findByIdMatriz(idMatriz);
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
    public List<Avance> buscarPorUsuarioYFecha(String idUsuario, String fecha) {
        return avanceDao.findByIdUsuarioAndFecha(idUsuario, fecha);
    }


    public List<Matriz> listarMateriales() {
        return (List<Matriz>) mateDao.findAll();
    }



}
