package com.example.web;

import com.example.domain.Matriz;
import com.example.domain.Presupuesto;
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
@RequestMapping("/presupuestos")

public class ControladorPresupuestos
{
    //Presupuesto
    @Autowired
    private PresupuestoServicio presupuestoServicio;
    //Acá están los métodos para presupuestos
    @GetMapping("/inicioPresupuestos")
    public String inicioPresu(Model model){
        List<Presupuesto> presupuestos = presupuestoServicio.listaPresupuesto();
        model.addAttribute("presupuestos",presupuestos);
        return "presupuestos/inicioPresupuestos";
    }

    //Agregar nuevo presupuesto
    @GetMapping("/agregarPresupuesto")
    public String formAnexarPresupuesto(Model model){
        model.addAttribute("presupuesto", new Presupuesto());
        model.addAttribute("matriz", matrizServicio.listarElementos());
        return "presupuestos/agregarPresupuesto";
    }

    //Función de guardado
    @PostMapping("/salvar")
    public String salvarPresupuesto(

            @RequestParam String obraName,
            @RequestParam List<Integer>actividadIds,
            @RequestParam List<Double> cantidades) {


        // Convert to Map<Integer, Double> for JSON storage
        Map<Integer, Double> activiValues = new HashMap<>();
        for (int i = 0; i < actividadIds.size(); i++) {
            activiValues.put(actividadIds.get(i), cantidades.get(i));
        }

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setObraName(obraName);
        presupuesto.setActiviValues(activiValues);

        presupuestoServicio.salvar(presupuesto);
        return "redirect:/presupuestos/inicioPresupuestos";
    }

    //Función y forma de editado
    @GetMapping("/cambiar/{id_obra}")
    public String cambiarPresupuesto(@PathVariable Integer id_obra, Model model) {
        Presupuesto presupuesto = presupuestoServicio.localizarPresupuesto(id_obra);

        // Create a list of activity IDs and quantities for editing
        List<Integer> actividadIds = new ArrayList<>();
        List<Double> cantidades = new ArrayList<>();

        // Create a Map of Material to Quantity
        Map<Matriz, Double> listActividades = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : presupuesto.getActiviValues().entrySet()) {
            Matriz material = matrizServicio.obtenerPorId(entry.getKey());
            listActividades.put(material, entry.getValue());
            actividadIds.add(entry.getKey());
            cantidades.add(entry.getValue());
        }
        model.addAttribute("presupuesto", presupuesto);
        model.addAttribute("listActividades", presupuestoServicio.listaPresupuesto());
        model.addAttribute("Editando", true); // ← This forces EDIT mode
        model.addAttribute("matriz", matrizServicio.listarElementos());
        //Map<Integer, Double> actividades = presupuesto.getActiviValues();
        model.addAttribute("actividadIds", actividadIds);
        model.addAttribute("cantidades", cantidades);

        return "presupuestos/verPresupuestos";
    }

    //borrar
    @GetMapping("/borrar/{id_obra}")
    public String borrarPresupuesto(Presupuesto presupuesto) {
        presupuestoServicio.borrar(presupuesto);
        return "redirect:/presupuestos/inicioPresupuestos";
    }

    //funcionalidad para guardar cambios
    @PostMapping("/actualizar/{id_obra}")
    public String actualizarPresupuesto(
        @PathVariable Integer id_obra,
        @RequestParam String obraName,
        @ModelAttribute Presupuesto presupuesto,
        BindingResult result,
        @RequestParam List<Integer> actividadIds,
        @RequestParam List<Double> cantidades,
        Model model) {
        if (result.hasErrors() || actividadIds.isEmpty()) {
            return "redirect:/presupuestos/cambiar/" + id_obra;
        }

        Map<Integer, Double> activiValues = new HashMap<>();
        for (int i = 0; i < actividadIds.size(); i++) {
            activiValues.put(actividadIds.get(i), cantidades.get(i));
        }


        presupuesto.setObraName(obraName);
        presupuesto.setActiviValues(activiValues);

        presupuestoServicio.actualizar(presupuesto);
        return "redirect:/presupuestos/inicioPresupuestos";
    }

    //Ver presupuesto en detalle (sólo lectura)
    @GetMapping("/detalle/{id_obra}")
    public String detallePresupuesto(@PathVariable Integer id_obra, Model model) {
        Presupuesto presupuesto = presupuestoServicio.localizarPresupuesto(id_obra);

        // Create a Map of Material to Quantity
        Map<Matriz, Double> listActividades = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : presupuesto.getActiviValues().entrySet()) {
            Matriz material = matrizServicio.obtenerPorId(entry.getKey());
            listActividades.put(material, entry.getValue());
        }

        model.addAttribute("presupuesto", presupuesto);
        model.addAttribute("listActividades", listActividades);
        model.addAttribute("Editando", false); // ← This forces VIEW mode
        return "presupuestos/verPresupuestos";
    }


    //Materiales (para el manejo de la matriz)
    @Autowired
    private MatrizServicio matrizServicio;

    //Funcionalidad del filtro
    @GetMapping("/filtroPr")
    public String filtroPre(
            @RequestParam(value = "tipoBusqueda", required = false) String tipoBusqueda,
            @RequestParam(value = "valorBusqueda", required = false) String valorBusqueda,
            Integer id_obra,
            Model model) {

        List<Presupuesto> presupuestos = new ArrayList<>(); // Initialize with empty list
        Presupuesto presupuesto = null; // Initialize as null
        String error = null;

        if (tipoBusqueda != null && valorBusqueda != null && !valorBusqueda.isEmpty()) {
            switch (tipoBusqueda) {
                case "idObra":
                    presupuesto = presupuestoServicio.localizarPresupuesto(Integer.parseInt(valorBusqueda));
                    break;
                case "obraName":
                    presupuestos = presupuestoServicio.findByObraNameContaining(valorBusqueda);
                    break;

                default:
                    presupuestos = presupuestoServicio.listaPresupuesto();
            }
        } else {
            presupuestos = presupuestoServicio.listaPresupuesto();
        }

        model.addAttribute("presupuestos", presupuestos);
        model.addAttribute("presupuesto", presupuesto);

        if (error != null) {
            model.addAttribute("error", error);
        }

        return "presupuestos/inicioPresupuestos";
    }

}