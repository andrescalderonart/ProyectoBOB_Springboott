package com.example.web;

import com.example.domain.Individuo;
import com.example.domain.Presupuesto;
import com.example.servicio.IndividuoServicio;
import com.example.servicio.PresupuestoServicio;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class Controlador {

    @Autowired
    private IndividuoServicio individuoServicio;

    //Presupuesto
    @Autowired
    private PresupuestoServicio presupuestoServicio;

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
    public String salvar(@Valid Individuo individuo, Errors errores) {
        if (errores.hasErrors()){
            return "Cambiar";
        }
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

    //Acá están los métodos para presupuestos
    @GetMapping("/presupuestos")
    public String inicioPresu(Model model){
        List<Presupuesto> presupuestos = presupuestoServicio.listaPresupuesto();
        model.addAttribute("presupuestos",presupuestos);
        return "presupuestos/indice";
    }

    @GetMapping("/presupuestos/anexar")
    public String anexarPresupuesto(Model model){
        model.addAttribute("presupuesto", new Presupuesto());
        model.addAttribute("materiales", presupuestoServicio.listarMateriales());
        return "presupuestos/agregarPresupuesto";
    }

    @PostMapping("/presupuestos/salvar")
    public String salvarPresupuesto(
            Errors errores,
            @RequestParam String obra,
            @RequestParam List<Integer>materialIds,
            @RequestParam List<Double> quantities) {
        if (errores.hasErrors()) {
            return "presupuestos/agregarPresupuesto";
        }

        // Convert to Map<Integer, Double> for JSON storage
        Map<Integer, Double> materialValues = new HashMap<>();
        for (int i = 0; i < materialIds.size(); i++) {
            materialValues.put(materialIds.get(i), quantities.get(i));
        }

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setEntryName(obra);
        presupuesto.setMaterialValues(materialValues);

        presupuestoServicio.salvar(presupuesto);
        return "redirect:/presupuestos";
    }

    @GetMapping("/presupuestos/cambiar/{entryId}")
    public String cambiarPresupuesto(Presupuesto presupuesto, Model model) {
        presupuesto = presupuestoServicio.localizarPresupuesto(presupuesto);
        model.addAttribute("presupuesto", presupuesto);
        return "presupuestos/cambiar";
    }

    @GetMapping("/presupuestos/borrar/{entryId}")
    public String borrarPresupuesto(Presupuesto presupuesto) {
        presupuestoServicio.borrar(presupuesto);
        return "redirect:/presupuestos";
    }


    //sigue lo que había
    @GetMapping("/redirigir")
    public String redirigirSegunPerfil(Authentication auth){
        String rol = auth.getAuthorities().iterator().next().getAuthority();
        switch (rol){
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

    @GetMapping("/login")
    public String mostrarLogin(){
        return "login";
    }
    @GetMapping("/secretaria")
    public String mostrarSecretaria(){
        return "secretaria";
    }
    @GetMapping("/vendedor")
    public String mostrarVendedor(){
        return "vendedor";
    }
    @GetMapping("/exportarExcel")
    public void exportarExcel(HttpServletResponse response)throws IOException{
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=individuos.xlsx");

        List<Individuo> list = individuoServicio.listaIndividuos();

        Workbook workbook = new XSSFWorkbook();
        Sheet hoja = workbook.createSheet("individuos");

        Row header = hoja.createRow(0);
        header.createCell(0).setCellValue("Nombre");
        header.createCell(1).setCellValue("Apellido");
        header.createCell(2).setCellValue("Edad");
        header.createCell(3).setCellValue("Correo");
        header.createCell(4).setCellValue("Telefono");

        int fila = 1;
        for(Individuo ind : list){
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
