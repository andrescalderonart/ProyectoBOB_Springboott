package com.example.web;

import com.example.domain.Individuo;
import com.example.servicio.IndividuoServicio;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.Authentication;


import java.io.IOException;
import java.util.List;

@Controller
public class Controlador {

    @Autowired
    private IndividuoServicio individuoServicio;

    // Método para la página de inicio (raíz de la aplicación)
    @GetMapping("/")
    public String inicio(Model model) {
        List<Individuo> individuos = individuoServicio.listaIndividuos();
        model.addAttribute("individuos", individuos);
        return "index"; // Retorna la vista 'index.html'
    }

    // Método para la página de login
    @GetMapping("/login")
    public String inicioLogin() { // Cambié el nombre del método para evitar conflictos
        return "login"; // Retorna la vista 'login.html'
    }

    // Método para la página de agregar un nuevo individuo
    @GetMapping("/anexar")
    public String anexar(Individuo individuo) {
        return "agregar"; // Retorna la vista 'agregar.html'
    }

    // Método para guardar un individuo (POST)
    @PostMapping("/salvar")
    public String salvar(@Valid Individuo individuo, Errors errores) {
        if (errores.hasErrors()) {
            return "Cambiar"; // Si hay errores, vuelve a la vista 'Cambiar' (revisa si esto es lo que quieres)
        }
        individuoServicio.salvar(individuo);
        return "redirect:/indice"; // Redirige a la nueva página de índice después de salvar
    }

    // Método para cambiar (editar) un individuo
    @GetMapping("/cambiar/{id_individuo}")
    public String Cambiar(Individuo individuo, Model model) {
        individuo = individuoServicio.localizarIndividuo(individuo);
        model.addAttribute("individuo", individuo);
        return "cambiar"; // Retorna la vista 'cambiar.html'
    }

    // Método para borrar un individuo
    @GetMapping("/borrar/{id_individuo}")
    public String Borrar(Individuo individuo) {
        individuoServicio.borrar(individuo);
        return "redirect:/indice"; // Redirige a la nueva página de índice después de borrar
    }

    // *******************************************************************
    // NUEVO MÉTODO: Página de índice/dashboard para usuarios autenticados
    // *******************************************************************
    @GetMapping("/indice")
    public String mostrarIndice(Model model) {
        List<Individuo> individuos = individuoServicio.listaIndividuos(); // Puedes cargar datos específicos para esta página
        model.addAttribute("individuos", individuos);
        return "indice"; // Asegúrate de tener un archivo 'indice.html' en tu carpeta de vistas
    }

    // *******************************************************************
    // NUEVOS MÉTODOS: Páginas específicas para otros roles (ejemplos)
    // Asegúrate de crear las vistas 'secretaria.html' y 'vendedor.html'
    // *******************************************************************
    @GetMapping("/secretaria")
    public String mostrarSecretaria() {
        return "secretaria"; // Retorna la vista 'secretaria.html'
    }

    @GetMapping("/vendedor")
    public String mostrarVendedor() {
        return "vendedor"; // Retorna la vista 'vendedor.html'
    }

    @GetMapping ("/dashboardADMIN")
    public String ADMIN(){
        return "dashboardADMIN";
    }

    // Método para redirigir según el perfil del usuario después del login
    @GetMapping("/redirigir")
    public String redirigirSegunPerfil(Authentication auth) {
        String rol = auth.getAuthorities().iterator().next().getAuthority();
        switch (rol) {
            case "ROLE_ADMINISTRACION":
                return "redirect:/dashboardADMIN"; // Redirige al índice específico para administradores
            case "ROLE_SECRETARIA":
                return "redirect:/secretaria"; // Redirige a la página de secretaria
            case "ROLE_VENDEDOR":
                return "redirect:/vendedor"; // Redirige a la página de vendedor
            default:
                // Si el rol no coincide con ninguno de los anteriores,
                // redirige a una página por defecto (ej. /indice)
                return "redirect:/indice"; // <-- ¡CAMBIO AQUÍ! Ya no redirige a la raíz
        }
    }



    @GetMapping("/exportarExcel")

    public void exportarExcel(HttpServletResponse response) throws IOException{
        response.setContentType("aplication/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=individuos.xlsx");

        List<Individuo> lista = individuoServicio.listaIndividuos();

        Workbook workbook = new XSSFWorkbook();
        Sheet hoja = workbook.createSheet("Individuos");


        Row header = hoja.createRow(0);
        header.createCell(0).setCellValue("Nombre");
        header.createCell(1).setCellValue("Apellido");
        header.createCell(2).setCellValue("Edad");
        header.createCell(3).setCellValue("Correo");
        header.createCell(4).setCellValue("Telefono");

        int fila = 1;
        for (Individuo ind : lista){
            Row row = hoja.createRow(fila++);
            row.createCell(0).setCellValue(ind.getNombre());
            row.createCell(1).setCellValue(ind.getApellido());
            row.createCell(2).setCellValue(ind.getEdad());
            row.createCell(3).setCellValue(ind.getCorreo());
            row.createCell(4).setCellValue(ind.getTelefono());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }


}