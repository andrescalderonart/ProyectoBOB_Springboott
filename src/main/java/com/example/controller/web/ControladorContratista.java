
package com.example.controller.web;

import com.example.dao.PersonaDao;
import com.example.domain.Contratista;
import com.example.servicio.ContratistaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contratistas")
public class ControladorContratista {

    private final ContratistaServicio contratistaServicio;
    private final PersonaDao personaDao;

    public ControladorContratista(ContratistaServicio contratistaServicio, PersonaDao personaDao) {
        this.contratistaServicio = contratistaServicio;
        this.personaDao = personaDao;
    }


    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("contratistas", contratistaServicio.listar());
        return "contratista/inicioContratista";
    }

    // Formulario crear - ahora será /contratistas/formulario
    @GetMapping("/formulario")
    public String formularioNuevo(Model model) {
        model.addAttribute("contratista", new Contratista());
        model.addAttribute("persona", personaDao.findAll());
        model.addAttribute("Editando", false);
        return "contratista/formulario";
    }


    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var c = contratistaServicio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Contratista no encontrado"));
        model.addAttribute("contratista", c);
        model.addAttribute("persona", personaDao.findAll());
        model.addAttribute("Editando", true);
        return "contratista/formulario";
    }


    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("contratista") Contratista form, Model model) {
        try {
            contratistaServicio.guardar(form);
            return "redirect:/contratistas"; // ← Actualizar redirect
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("persona", personaDao.findAll());
            model.addAttribute("Editando", form.getIdContratista() != null);
            return "contratista/formulario";
        }
    }


    @PostMapping("/borrar/{id}")
    public String borrar(@PathVariable Long id) {
        contratistaServicio.eliminar(id);
        return "redirect:/contratistas";
    }
}