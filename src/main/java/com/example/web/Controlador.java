package com.example.web;

import com.example.dao.IndividuoDao;
import com.example.domain.Individuo;
import com.example.servicio.IndividuoServicio;
import com.example.servicio.IndividuoServicioImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
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

    @GetMapping("/anexar")
    public String anexar(Individuo individuo) {
        return "agregar";

    }

    @PostMapping("/salvar")
    public String salvar(Individuo individuo) {
        individuoServicio.salvar(individuo);
        return "redirect:/";
    }

    @GetMapping("/cambiar/{id_individuo}")
    public String Cambiar(Individuo individuo, Model model) {
        individuo = individuo = individuoServicio.localizarIndividuo(individuo);
        model.addAttribute("individuo", individuo);
        return "cambiar";
    }

    @GetMapping("/borrar/{id_individuo}")
    public String Borrar(Individuo individuo) {
        individuoServicio.borrar(individuo);
        return "redirect:/";
    }
}
