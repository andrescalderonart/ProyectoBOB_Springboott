package com.example.web;

import com.example.domain.Avance;
import com.example.domain.Matriz;
import com.example.domain.Presupuesto;
import com.example.servicio.AvanceServicio;
import com.example.servicio.MatrizServicio;
import com.example.servicio.PresupuestoServicio;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
           // @RequestParam(required = false) String obraName,
           @RequestParam(required = false) Integer idObraTexto,
           @RequestParam(required = false) Integer idObraSelect,
            @RequestParam(required = false) String idUsuario,
            @RequestParam(required = false) Integer idMatriz,
            @RequestParam(required = false) String fecha,
            Model model){


        //Necesito cargar presupuestos para mostrar nombres de obra
        List<Presupuesto> presupuestos = presupuestoServicio.listaPresupuesto();
        model.addAttribute("presupuestos",presupuestos);

//Este if es para las búsquedas por ID

// Start with all avances
        List<Avance> avances = avanceServicio.listaAvance();

        // Apply filters in a more flexible way
        if (idObraSelect != null) {
            avances = avanceServicio.buscarPorIdObra(idObraSelect);
        }
        if (idObraTexto != null) {
            avances = avanceServicio.buscarPorIdObra(idObraTexto);
        }
        if (idUsuario != null && !idUsuario.isEmpty()) {
            avances = avances.stream()
                    .filter(a -> a.getIdUsuario().equals(idUsuario))
                    .collect(Collectors.toList());
        }
        if (fecha != null && !fecha.isEmpty()) {
            avances = avances.stream()
                    .filter(a -> a.getFecha().contains(fecha))
                    .collect(Collectors.toList());
        }

        // Add the filtered results and parameters back to the model
        model.addAttribute("avances", avances);
        model.addAttribute("idObraSelect", idObraSelect);
        model.addAttribute("idObraTexto", idObraTexto);
        model.addAttribute("idUsuario", idUsuario);
        model.addAttribute("fecha", fecha);

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
            Authentication auth, // Add this parameter to get the logged-in user
            @RequestParam String idUsuario,
            @RequestParam Integer idObra,
            @RequestParam String fecha,
            @RequestParam Integer idMatriz,
            @RequestParam Double cantidad) {

        // Get the username from the authentication object
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // For now, let's just print it to verify it works
        System.out.println("Logged in username: " + username);


        Avance avance = new Avance();
        avance.setIdUsuario(username);
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
        Authentication auth, // Add this parameter to get the logged-in user
        @PathVariable Integer idAvance,
        @ModelAttribute Avance avance,
        @RequestParam Double cantidad,
        @RequestParam String idUsuario,
        @RequestParam Integer idObra,
        @RequestParam String fecha,
        BindingResult result,
        @RequestParam Integer idMatriz,
        Model model) {
        if (result.hasErrors()) {
            return "redirect:/avances/cambiar/" + idAvance;
        }

        // Get the username from the authentication object
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        avance.setIdUsuario(username);
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