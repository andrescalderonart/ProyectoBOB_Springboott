package com.example.servicio;

import com.example.domain.Material;
import com.example.domain.Presupuesto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PresupuestoServicio {

    public List<Presupuesto> listaPresupuesto();

    public void salvar(Presupuesto presu);

    public void borrar(Presupuesto presu);

    Presupuesto localizarPresupuesto(Presupuesto presu);

    public List<Material> listarMateriales();
}
