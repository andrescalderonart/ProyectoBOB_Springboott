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

    public List<Matriz> listarMateriales();
}
