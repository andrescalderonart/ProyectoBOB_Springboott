package com.example.servicio;

import com.example.domain.Inventario;
import java.util.List;

public interface InventarioServicio {

    List<Inventario> listaInventarios();

    void guardarInv(Inventario inventario);

    void cambiarInv(Inventario inventario);

    void borrarInv(Inventario inventario);

    Inventario localizarInventarioPorId(Long id);

    //Metodo de busqueda para el de modificar inventario
    List<Inventario> buscarPorNombreGestor(String nombreGestor);
    List<Inventario> buscarPorNombreObra(String nombreObra);
    List<Inventario> buscarPorFecha(String fecha);
}