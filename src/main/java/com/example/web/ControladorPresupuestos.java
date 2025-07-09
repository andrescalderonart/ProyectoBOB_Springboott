package com.example.web;

import com.example.domain.Matriz;
import com.example.domain.Presupuesto;
import com.example.servicio.MatrizServicio;
import com.example.servicio.PresupuestoServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/presupuestos")
public class ControladorPresupuestos {

    @Autowired
    private PresupuestoServicio presupuestoServicio;

    @Autowired
    private MatrizServicio matrizServicio;

    // Página de inicio
    @GetMapping("/inicioPresupuestos")
    public String inicioPresu(Model model, Authentication auth, HttpSession session) {
        String rol = auth.getAuthorities().iterator().next().getAuthority();

        switch (rol) {
            case "ROLE_ADMINISTRADOR":
                session.setAttribute("dashboardOrigen", "/dashboardADMIN");
                break;
            case "ROLE_OPERATIVO":
                session.setAttribute("dashboardOrigen", "/dashboardOPERA");
                break;
            case "ROLE_SUPERVISOR":
                session.setAttribute("dashboardOrigen", "/dashboardSUPER");
                break;
            default:
                session.setAttribute("dashboardOrigen", "/login?error=sin-permisos");
                break;
        }

        List<Presupuesto> presupuestos = presupuestoServicio.listaPresupuesto();
        model.addAttribute("presupuestos", presupuestos);
        return "presupuestos/inicioPresupuestos";
    }

    // Formulario para agregar nuevo presupuesto
    @GetMapping("/agregarPresupuesto")
    public String formAnexarPresupuesto(Model model) {
        model.addAttribute("presupuesto", new Presupuesto());
        model.addAttribute("matriz", matrizServicio.listarElementos());
        return "presupuestos/agregarPresupuesto";
    }

    // Guardar nuevo presupuesto
    @PostMapping("/salvar")
    public String salvarPresupuesto(
            @RequestParam String obraName,
            @RequestParam List<Integer> actividadIds,
            @RequestParam List<Double> cantidades) {

        if (actividadIds.size() != cantidades.size()) {
            throw new IllegalArgumentException("Cantidad de actividades y cantidades no coinciden.");
        }

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

    // Editar presupuesto existente
    @GetMapping("/cambiar/{id_obra}")
    public String cambiarPresupuesto(@PathVariable Integer id_obra, Model model) {
        Presupuesto presupuesto = presupuestoServicio.localizarPresupuesto(id_obra);

        List<Integer> actividadIds = new ArrayList<>();
        List<Double> cantidades = new ArrayList<>();
        Map<Matriz, Double> listActividades = new HashMap<>();

        for (Map.Entry<Integer, Double> entry : presupuesto.getActiviValues().entrySet()) {
            Matriz material = matrizServicio.obtenerPorId(entry.getKey());
            listActividades.put(material, entry.getValue());
            actividadIds.add(entry.getKey());
            cantidades.add(entry.getValue());
        }

        model.addAttribute("presupuesto", presupuesto);
        model.addAttribute("listActividades", presupuestoServicio.listaPresupuesto());
        model.addAttribute("Editando", true);
        model.addAttribute("matriz", matrizServicio.listarElementos());
        model.addAttribute("actividadIds", actividadIds);
        model.addAttribute("cantidades", cantidades);

        return "presupuestos/verPresupuestos";
    }

    // Eliminar presupuesto
    @GetMapping("/borrar/{id_obra}")
    public String borrarPresupuesto(Presupuesto presupuesto) {
        presupuestoServicio.borrar(presupuesto);
        return "redirect:/presupuestos/inicioPresupuestos";
    }

    // Guardar cambios del presupuesto editado
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

    // Ver detalles del presupuesto
    @GetMapping("/detalle/{id_obra}")
    public String detallePresupuesto(@PathVariable Integer id_obra, Model model) {
        Presupuesto presupuesto = presupuestoServicio.localizarPresupuesto(id_obra);

        Map<Matriz, Double> listActividades = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : presupuesto.getActiviValues().entrySet()) {
            Matriz material = matrizServicio.obtenerPorId(entry.getKey());
            listActividades.put(material, entry.getValue());
        }

        model.addAttribute("presupuesto", presupuesto);
        model.addAttribute("listActividades", listActividades);
        model.addAttribute("Editando", false);
        return "presupuestos/verPresupuestos";
    }

    // Filtro de presupuestos
    @GetMapping("/filtroPr")
    public String filtroPre(
            @RequestParam(value = "tipoBusqueda", required = false) String tipoBusqueda,
            @RequestParam(value = "valorBusqueda", required = false) String valorBusqueda,
            Integer id_obra,
            Model model) {

        List<Presupuesto> presupuestos = new ArrayList<>();
        Presupuesto presupuesto = null;

        if (tipoBusqueda != null && valorBusqueda != null && !valorBusqueda.isEmpty()) {
            switch (tipoBusqueda) {
                case "idObra":
                    try {
                        presupuesto = presupuestoServicio.localizarPresupuesto(Integer.parseInt(valorBusqueda));
                    } catch (NumberFormatException e) {
                        model.addAttribute("error", "ID inválido");
                    }
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
        return "presupuestos/inicioPresupuestos";
    }
}
