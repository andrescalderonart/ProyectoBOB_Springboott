package com.example.web;


import com.example.domain.Individuo;
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

    @GetMapping("/")
    public String inventario(Model model){

        List<Inventario> inventarios = inventarioServicio.listaInventarios();
        model.addAttribute("inventarios", inventarios);
        return "inicioBOB";
    }

    @GetMapping("/crearInv")
    public String crearInv (Inventario inventario){
        return "inventario";
    }

    @PostMapping("/guardarInv")
    public String guardarInv(@Valid Inventario inventario, Errors errores) {
        if (errores.hasErrors()){
            return "guardarInv";
        }
        inventarioServicio.guadarInv(inventario);
        return "redirect:/";
    }
}
