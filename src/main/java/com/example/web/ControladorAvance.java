package com.example.web;

import com.example.domain.Avance;
import com.example.domain.Matriz;
import com.example.domain.Presupuesto;
import com.example.servicio.AvanceServicio;
import com.example.servicio.MatrizServicio;
import com.example.servicio.PresupuestoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/avances")

public class ControladorAvance
{
    //
    @Autowired
    private AvanceServicio avanceServicio;

    @Autowired
    private PresupuestoServicio presupuestoServicio;
    //Acá están los métodos
    @GetMapping("/inicioAvances")
    public String inicioAvance(Model model){
        List<Avance> avances = avanceServicio.listaAvance();
        List<Presupuesto> presupuestos = presupuestoServicio.listaPresupuesto();
        model.addAttribute("avances",avances);
        model.addAttribute("presupuestos",presupuestos);
        return "avances/inicioAvances";
    }

    //Agregar nuevo
    @GetMapping("/agregarAvances")
    public String formAnexarAvance(Model model){
        model.addAttribute("avance", new Avance());
        model.addAttribute("matriz", matrizServicio.listarElementos());
        return "avances/agregarAvance";
    }

    //Función de guardado
    @PostMapping("/salvar")
    public String salvarAvance(
            @RequestParam Integer id_usuario,
            @RequestParam Integer id_obra,
            @RequestParam String fecha,
            @RequestParam Integer id_matriz,
            @RequestParam Double cantidad) {

        Avance avance = new Avance();
        avance.setId_usuario(id_usuario);
        avance.setId_obra(id_obra);
        avance.setFecha(fecha);
        avance.setId_matriz(id_matriz);
        avance.setCantidad(cantidad);


        avanceServicio.salvar(avance);
        return "redirect:/avances/inicioAvances";
    }

    //Función y forma de editado
    @GetMapping("/cambiar/{id_avance}")
    public String cambiarAvance(@PathVariable Integer id_avance, Model model) {
        Avance avance = avanceServicio.localizarAvance(id_avance);

        model.addAttribute("avance", avance);
        model.addAttribute("Actividad", avanceServicio.localizarAvance(id_avance));
        model.addAttribute("Editando", true); // ← This forces EDIT mode
        model.addAttribute("matriz", matrizServicio.listarElementos());


        return "avances/verAvances";
    }

    //borrar
    @GetMapping("/borrar/{id_avance}")
    public String borrarAvance(Avance avance) {
        avanceServicio.borrar(avance);
        return "redirect:/avances/inicioAvances";
    }

    //funcionalidad para guardar cambios
    @PostMapping("/actualizar/{id_avance}")
    public String actualizarPresupuesto(
        @PathVariable Integer id_avance,
        @ModelAttribute Avance avance,
        @RequestParam Double cantidad,
        @RequestParam Integer id_usuario,
        @RequestParam Integer id_obra,
        @RequestParam String fecha,
        BindingResult result,
        @RequestParam Integer id_matriz,
        Model model) {
        if (result.hasErrors()) {
            return "redirect:/avances/cambiar/" + id_avance;
        }

        avance.setId_usuario(id_usuario);
        avance.setId_obra(id_obra);
        avance.setFecha(fecha);
        avance.setId_matriz(id_matriz);
        avance.setCantidad(cantidad);


        avanceServicio.actualizar(avance);
        return "redirect:/avances/inicioAvances";
    }

    //Ver detalle (sólo lectura)
    @GetMapping("/detalle/{id_avance}")
    public String detalleAvance(@PathVariable Integer id_avance, Model model) {
        Avance avance = avanceServicio.localizarAvance(id_avance);



        model.addAttribute("avance", avance);
        model.addAttribute("Actividad", avanceServicio.localizarAvance(id_avance));
        model.addAttribute("Editando", false); // ← This forces VIEW mode
        return "avances/verAvances";
    }


    //Materiales (para el manejo de la matriz)
    @Autowired
    private MatrizServicio matrizServicio;

}