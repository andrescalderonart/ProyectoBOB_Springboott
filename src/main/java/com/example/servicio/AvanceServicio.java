package com.example.servicio;

import com.example.domain.Avance;
import com.example.domain.Matriz;
import com.example.domain.Presupuesto;

import java.util.List;

public interface AvanceServicio {

    public List<Avance> listaAvance();

    public void salvar(Avance avance);

    void actualizar(Avance avance);

    public void borrar(Avance avance);

    Avance localizarAvance(Integer entryId);

    // New search methods
    List<Avance> buscarPorIdAvance(Integer id_avance);
    List<Avance> buscarPorIdUsuario(Integer id_usuario);
    List<Avance> buscarPorIdObra(Integer id_obra);
    List<Avance> buscarPorIdMatriz(Integer id_matriz);

    List<Avance> buscarPorFecha(String fecha);
    List<Avance> buscarPorFechaConteniendo(String fecha);



    // Combined search methods
    List<Avance> buscarPorUsuarioYFecha(Integer id_usuario, String fecha);



    public List<Matriz> listarMateriales();
}