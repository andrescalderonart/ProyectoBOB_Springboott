package com.example.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControladorDash {

    @GetMapping({"/dashboard", "/dashboard/"})
    public String mostrarDashboard(
            @RequestParam(name = "seccion", required = false, defaultValue = "inicio") String seccion,
            Model model) {

        model.addAttribute("seccionActiva", seccion);
        return "dashboardBOB";
    }

    // Opcional: Redirección desde la raíz
    @GetMapping("/")
    public String redirigirAlDashboard() {
        return "redirect:/dashboard";
    }
}