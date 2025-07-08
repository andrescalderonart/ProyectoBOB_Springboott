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
import java.util.stream.Collectors;

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
    public String inicioAvance(
            @RequestParam(required = false) String obraName,
            @RequestParam(required = false) Integer id_obra,
            @RequestParam(required = false) Integer id_usuario,
            @RequestParam(required = false) Integer id_matriz,
            @RequestParam(required = false) String fecha,
            Model model){
        //En vez de cargar la lista entera al principio sólo declaro la variable
        List<Avance> avances;

        //Necesito cargar presupuestos para mostrar nombres de obra
        List<Presupuesto> presupuestos = presupuestoServicio.listaPresupuesto();
        model.addAttribute("presupuestos",presupuestos);

//Este if es para las búsqeudas por ID
        if (id_usuario != null && fecha != null) {
            avances = avanceServicio.buscarPorUsuarioYFecha(id_usuario, fecha);
        }
        else if (id_obra != null) {
            avances = avanceServicio.buscarPorIdObra(id_obra);
        }
        else if (id_usuario != null) {
            avances = avanceServicio.buscarPorIdUsuario(id_usuario);
        }
        else if (id_matriz != null) {
            avances = avanceServicio.buscarPorIdMatriz(id_matriz);
        }
        else if (fecha != null) {
            avances = avanceServicio.buscarPorFechaConteniendo(fecha);
        }
        else {
            // No filters - get all avances
            avances = avanceServicio.listaAvance();
        }



        model.addAttribute("avances",avances);

        return "avances/inicioAvances";
    }

    //Agregar nuevo
    @GetMapping("/agregarAvance")
    public String formAnexarAvance(Model model){
        List<Presupuesto> presupuestos = presupuestoServicio.listaPresupuesto();
        List<Matriz> matriz = matrizServicio.listarElementos();

        model.addAttribute("avance", new Avance());
        model.addAttribute("presupuestos",presupuestos);
        model.addAttribute("matriz", matriz);
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
        avance.setIdUsuario(id_usuario);
        avance.setIdObra(id_obra);
        avance.setFecha(fecha);
        avance.setIdMatriz(id_matriz);
        avance.setCantidad(cantidad);


        avanceServicio.salvar(avance);
        return "redirect:/avances/inicioAvances";
    }


    //Función y forma de editado
    /*@GetMapping("/cambiar/{id_avance}")
    public String cambiarAvance(@PathVariable Integer id_avance, Model model) {
        Avance avance = avanceServicio.localizarAvance(id_avance);

        model.addAttribute("avance", avance);
        model.addAttribute("Actividad", avanceServicio.localizarAvance(id_avance));
        model.addAttribute("Editando", true); // ← This forces EDIT mode
        model.addAttribute("matriz", matrizServicio.listarElementos());

        return "avances/verAvances";
    }*/


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

        avance.setIdUsuario(id_usuario);
        avance.setIdObra(id_obra);
        avance.setFecha(fecha);
        avance.setIdMatriz(id_matriz);
        avance.setCantidad(cantidad);


        avanceServicio.actualizar(avance);
        return "redirect:/avances/inicioAvances";
    }

    //Ver detalle (sólo lectura)
    @GetMapping("/detalle/{id_avance}")
    public String detalleAvance(@PathVariable Integer id_avance, Model model) {
        Avance avance = avanceServicio.localizarAvance(id_avance);
        List<Matriz> matriz = matrizServicio.listarElementos();
        List<Presupuesto> presupuestos = presupuestoServicio.listaPresupuesto();

        model.addAttribute("avance", avance);
        model.addAttribute("actividad", avanceServicio.localizarAvance(id_avance));
        model.addAttribute("presupuestos",presupuestos);
        model.addAttribute("matriz", matriz);
        model.addAttribute("Editando", false); // ← This forces VIEW mode
        return "avances/verAvances";
    }




    //Materiales (para el manejo de la matriz)
    @Autowired
    private MatrizServicio matrizServicio;



}