package com.example.web;

import com.example.domain.Individuo;
import com.example.servicio.IndividuoServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class Controlador {

    @Autowired
    private IndividuoServicio individuoServicio;


    @GetMapping("/")
    public String inicio(Model model) {

        List<Individuo> individuos = individuoServicio.listaIndividuos();
        model.addAttribute("individuos", individuos);
        return "indice";
    }

    @RequestMapping("/auth")
    public class AuthController {

        @GetMapping("/login")
        public String showLogin() {
            return "login";
        }

        @GetMapping("/anexar")
        public String anexar(Individuo individuo) {
            return "agregar";

        }

        @PostMapping("/salvar")
        public String salvar(@Valid Individuo individuo, Errors errores) {
            if (errores.hasErrors()) {
                return "Cambiar";
            }
            individuoServicio.salvar(individuo);
            return "redirect:/";
        }

        @GetMapping("/cambiar/{id_individuo}")
        public String Cambiar(Individuo individuo, Model model) {
            individuo = individuoServicio.localizarIndividuo(individuo);
            model.addAttribute("individuo", individuo);
            return "cambiar";
        }

        @GetMapping("/borrar/{id_individuo}")
        public String Borrar(Individuo individuo) {
            individuoServicio.borrar(individuo);
            return "redirect:/";
        }

        @GetMapping("/redirigir")
        public String redirigirSegunPerfil(Authentication auth) {
            String rol = auth.getAuthorities().iterator().next().getAuthority();
            switch (rol) {
                case "ROLE_ADMINISTRACION":
                    return "redirect:/";
                case "ROLE_SECRETARIA":
                    return "redirect:/secretaria";
                case "ROLE_VENDEDOR":
                    return "redirect:/vendedor";
                default:
                    return "redirect:/";
            }
        }
    }
}