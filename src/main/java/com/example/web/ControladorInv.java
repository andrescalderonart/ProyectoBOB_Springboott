package com.example.web;


import com.example.domain.Inventario;
import com.example.servicio.InventarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ControladorInv {

    @Autowired
    private InventarioServicio inventarioServicio;

    @GetMapping("/inventario")
    public String inventario(Model model){

        List<Inventario> inventarios = inventarioServicio.listaInventarios();
        model.addAttribute("inventarios", inventarios);
        return "indice";
    }

    @GetMapping("/crearInv")
    public String crearInv (Inventario inventario){
        return "inventario";
    }

    @PostMapping("/guardarInv")
    public String guardarInv(@Valid Inventario inventario, Errors errores) {
        if (errores.hasErrors()){
            return "inventario";
        }
        inventarioServicio.guardarInv(inventario);
        return "redirect:/inventario";
    }

    @GetMapping("/cambiarInv/{id_inventario}")
    public String cambiarInv(Inventario inventario, Model model) {
        inventario = inventarioServicio.localizarInventario(inventario);
        model.addAttribute("inventario", inventario);
        return "cambiarInv";
    }

    @GetMapping("/borrarInv/{id_inventario}")
    public String borrarInv(Inventario inventario) {
        inventarioServicio.borrarInv(inventario);
        return "redirect:/inventario";
    }
}
