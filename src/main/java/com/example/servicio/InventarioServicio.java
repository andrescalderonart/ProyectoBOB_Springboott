package com.example.servicio;


import com.example.domain.Inventario;

import java.util.List;

public interface InventarioServicio {

    public List<Inventario> listaInventarios();

    public void guardarInv(Inventario inventario);

    public void cambiarInv(Inventario inventario);

    public void borrarInv(Inventario inventario);

    public Inventario localizarInventario(Inventario inventario);

}
