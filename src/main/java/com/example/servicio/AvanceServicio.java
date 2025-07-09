package com.example.servicio;

import com.example.domain.Avance;
import com.example.domain.Matriz;

import java.util.List;

public interface AvanceServicio {

    public List<Avance> listaAvance();

    public void salvar(Avance avance);

    void actualizar(Avance avance);

    public void borrar(Avance avance);

    Avance localizarAvance(Integer entryId);

    // New search methods
    List<Avance> buscarPorIdAvance(Integer idAvance);
    List<Avance> buscarPorIdUsuario(String idUsuario);
    List<Avance> buscarPorIdObra(Integer idObra);
    List<Avance> buscarPorIdMatriz(Integer idMatriz);

    List<Avance> buscarPorFecha(String fecha);
    List<Avance> buscarPorFechaConteniendo(String fecha);



    // Combined search methods
    List<Avance> buscarPorUsuarioYFecha(String id_usuario, String fecha);



    public List<Matriz> listarMateriales();
}
