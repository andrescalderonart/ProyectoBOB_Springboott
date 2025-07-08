package com.example.servicio;

import com.example.domain.Matriz;
import com.example.domain.Presupuesto;

import java.util.List;

public interface PresupuestoServicio {

    public List<Presupuesto> listaPresupuesto();

    public void salvar(Presupuesto presu);

    void actualizar(Presupuesto presu);

    public void borrar(Presupuesto presu);

    Presupuesto localizarPresupuesto(Integer entryId);

    List<Presupuesto> findByObraName(String obraName);
    List<Presupuesto> findByObraNameContaining(String obraName);
    List<Presupuesto> findByObraNameIgnoreCase(String obraName);

    public List<Matriz> listarMateriales();
}
