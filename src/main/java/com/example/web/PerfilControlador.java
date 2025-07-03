package com.example.web;



import com.example.domain.Perfil;
import com.example.domain.Permiso;
import com.example.servicio.PerfilServicio;
import com.example.servicio.PermisoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/perfiles")
public class PerfilControlador {

    @Autowired
    private PerfilServicio perfilServicio;

    @Autowired
    private PermisoServicio permisoServicio;

    @GetMapping("/{id}/permisos")
    public String verPermisos(@PathVariable Integer id, Model model){
        Perfil perfil = perfilServicio.buscarPorId(id);
        List<Permiso> todos = permisoServicio.listarPermisos();

        model.addAttribute("perfil", perfil);
        model.addAttribute("todosPermisos", todos);
        return "asignar_permisos";
    }

    @PostMapping("/{id}/permisos")
    public String actualizarPermisos(@PathVariable Integer id,
                                     @RequestParam List<Long> permisosIds){
        Perfil perfil = perfilServicio.buscarPorId(id);
        List<Permiso> permisosSeleccionados = permisoServicio.listarPermisos()
                .stream()
                .filter(p -> permisosIds.contains(p.getId()))
                .toList();
        perfil.setPermisos(permisosSeleccionados);
        perfilServicio.guardar(perfil);

        return "redirect:/perfiles";

    }
}
