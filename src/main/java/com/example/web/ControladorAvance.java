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

import java.util.List;

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
           // @RequestParam(required = false) String obraName,
           @RequestParam(required = false) Integer idObraTexto,
           @RequestParam(required = false) Integer idObraSelect,
            @RequestParam(required = false) Integer idUsuario,
            @RequestParam(required = false) Integer idMatriz,
            @RequestParam(required = false) String fecha,
            Model model){
        //En vez de cargar la lista entera al principio sólo declaro la variable
        List<Avance> avances;

        //Necesito cargar presupuestos para mostrar nombres de obra
        List<Presupuesto> presupuestos = presupuestoServicio.listaPresupuesto();
        model.addAttribute("presupuestos",presupuestos);

//Este if es para las búsquedas por ID

        //Para saber cuál idObra
        Integer idObra = idObraTexto != null ? idObraTexto : idObraSelect;

        if (idUsuario != null && fecha != null) {
            avances = avanceServicio.buscarPorUsuarioYFecha(idUsuario, fecha);
        }
        else if (idObra != null) {
            avances = avanceServicio.buscarPorIdObra(idObra);
        }
        else if (idUsuario != null) {
            avances = avanceServicio.buscarPorIdUsuario(idUsuario);
        }
        else if (idMatriz != null) {
            avances = avanceServicio.buscarPorIdMatriz(idMatriz);
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
            @RequestParam Integer idUsuario,
            @RequestParam Integer idObra,
            @RequestParam String fecha,
            @RequestParam Integer idMatriz,
            @RequestParam Double cantidad) {

        Avance avance = new Avance();
        avance.setIdUsuario(idUsuario);
        avance.setIdObra(idObra);
        avance.setFecha(fecha);
        avance.setIdMatriz(idMatriz);
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
    @GetMapping("/borrar/{idAvance}")
    public String borrarAvance(Avance avance) {
        avanceServicio.borrar(avance);
        return "redirect:/avances/inicioAvances";
    }

    //funcionalidad para guardar cambios
    @PostMapping("/actualizar/{idAvance}")
    public String actualizarPresupuesto(
        @PathVariable Integer idAvance,
        @ModelAttribute Avance avance,
        @RequestParam Double cantidad,
        @RequestParam Integer idUsuario,
        @RequestParam Integer idObra,
        @RequestParam String fecha,
        BindingResult result,
        @RequestParam Integer idMatriz,
        Model model) {
        if (result.hasErrors()) {
            return "redirect:/avances/cambiar/" + idAvance;
        }

        avance.setIdUsuario(idUsuario);
        avance.setIdObra(idObra);
        avance.setFecha(fecha);
        avance.setIdMatriz(idMatriz);
        avance.setCantidad(cantidad);


        avanceServicio.actualizar(avance);
        return "redirect:/avances/inicioAvances";
    }

    //Ver detalle (sólo lectura)
    @GetMapping("/detalle/{idAvance}")
    public String detalleAvance(@PathVariable Integer idAvance, Model model) {
        Avance avance = avanceServicio.localizarAvance(idAvance);
        List<Matriz> matriz = matrizServicio.listarElementos();
        List<Presupuesto> presupuestos = presupuestoServicio.listaPresupuesto();

        model.addAttribute("avance", avance);
        model.addAttribute("actividad", avanceServicio.localizarAvance(idAvance));
        model.addAttribute("presupuestos",presupuestos);
        model.addAttribute("matriz", matriz);
        model.addAttribute("Editando", false); // ← This forces VIEW mode
        return "avances/verAvances";
    }




    //Materiales (para el manejo de la matriz)
    @Autowired
    private MatrizServicio matrizServicio;



}