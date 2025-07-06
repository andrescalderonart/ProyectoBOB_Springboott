package com.example.web;

import com.example.domain.Inventario;
import com.example.servicio.InventarioServicio;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class ControladorInv {

    @Autowired
    private InventarioServicio inventarioServicio;

    @GetMapping("/inventario")
    public String inventario(Model model) {
        List<Inventario> inventarios = inventarioServicio.listaInventarios();
        model.addAttribute("inventarios", inventarios);
        return "/inventario";
    }

    @GetMapping("/crearInv")
    public String crearInv(Inventario inventario) {
        return "crearInv";
    }

    @PostMapping("/guardarInv")
    public String guardarInv(@Valid Inventario inventario, Errors errores) {
        if (errores.hasErrors()) {
            // Determinar a qué vista regresar según si es nuevo registro o edición
            return (inventario.getId_inventario() == null) ? "crearInv" : "cambiarInv";
        }
        inventarioServicio.guardarInv(inventario);
        return "redirect:/inventario";
    }

    @GetMapping("/verInv")
    public String verInventario(Model model) {
        List<Inventario> inventarios = inventarioServicio.listaInventarios();
        model.addAttribute("inventarios", inventarios);
        return "verInv";
    }

    @GetMapping("/cambiarInv")
    public String cambiarInv(
            @RequestParam(value = "tipoBusqueda", required = false) String tipoBusqueda,
            @RequestParam(value = "valorBusqueda", required = false) String valorBusqueda,
            Model model) {

        List<Inventario> inventarios;
        String error = null;

        if (tipoBusqueda != null && valorBusqueda != null && !valorBusqueda.isEmpty()) {
            switch (tipoBusqueda) {
                case "gestor":
                    inventarios = inventarioServicio.buscarPorNombreGestor(valorBusqueda);
                    break;
                case "obra":
                    inventarios = inventarioServicio.buscarPorNombreObra(valorBusqueda);
                    break;
                case "fecha":
                    inventarios = inventarioServicio.buscarPorFecha(valorBusqueda);
                    if (inventarios.isEmpty()) {
                        error = "No se encontraron resultados o formato de fecha inválido (Use YYYY-MM-DD)";
                    }
                    break;
                default:
                    inventarios = inventarioServicio.listaInventarios();
            }
        } else {
            inventarios = inventarioServicio.listaInventarios();
        }

        model.addAttribute("inventarios", inventarios);
        model.addAttribute("inventario", new Inventario());
        if (error != null) {
            model.addAttribute("error", error);
        }

        return "cambiarInv";
    }

    @GetMapping("/cambiarInv/{id_inventario}")
    public String editarInventario(
            @PathVariable("id_inventario") Long id,
            Model model) {
        Inventario inventario = inventarioServicio.localizarInventarioPorId(id);
        model.addAttribute("inventario", inventario);

        List<Inventario> inventarios = inventarioServicio.listaInventarios();
        model.addAttribute("inventarios", inventarios);

        return "cambiarInv";
    }

    @GetMapping("/borrarInv")
    public String borrarInv(
            @RequestParam(value = "tipoBusqueda", required = false) String tipoBusqueda,
            @RequestParam(value = "valorBusqueda", required = false) String valorBusqueda,
            Model model) {

        List<Inventario> inventarios;

        if (tipoBusqueda != null && valorBusqueda != null && !valorBusqueda.isEmpty()) {
            switch (tipoBusqueda) {
                case "gestor":
                    inventarios = inventarioServicio.buscarPorNombreGestor(valorBusqueda);
                    break;
                case "obra":
                    inventarios = inventarioServicio.buscarPorNombreObra(valorBusqueda);
                    break;
                case "fecha":
                    inventarios = inventarioServicio.buscarPorFecha(valorBusqueda);
                    break;
                default:
                    inventarios = inventarioServicio.listaInventarios();
            }
        } else {
            inventarios = inventarioServicio.listaInventarios();
        }

        model.addAttribute("inventarios", inventarios);
        return "borrarInv";
    }

    @GetMapping("/borrarInv/{id_inventario}")
    public String borrarInventario(
            @PathVariable("id_inventario") Long id) {
        Inventario inventario = inventarioServicio.localizarInventarioPorId(id);
        inventarioServicio.borrarInv(inventario);
        return "redirect:/borrarInv";
    }

    //Exportar excel de inventario
    @GetMapping("/exportarExcelInv")
    public void exportarExcelInv(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=inventario.xlsx");

        List<Inventario> lista = inventarioServicio.listaInventarios();

        Workbook libro = new XSSFWorkbook();
        Sheet hoja = libro.createSheet("Inventarios");

        Row header = hoja.createRow(0);
        header.createCell(0).setCellValue("Nombre Del Gestor");
        header.createCell(1).setCellValue("Nombre De La Obra");
        header.createCell(2).setCellValue("Tipo De Registro");
        header.createCell(3).setCellValue("Fecha");
        header.createCell(4).setCellValue("Unidad De Medida");
        header.createCell(5).setCellValue("Cantidad");
        header.createCell(6).setCellValue("Material");
        header.createCell(7).setCellValue("Precio De Unidad");

        int fila = 1;
        for (Inventario inv : lista){
            Row row = hoja.createRow(fila++);
            row.createCell(0).setCellValue(inv.getNombreGestor());
            row.createCell(1).setCellValue(inv.getNombreobra());
            row.createCell(2).setCellValue(inv.getTipoRegistro());
            row.createCell(3).setCellValue(inv.getFecha().toString());
            row.createCell(4).setCellValue(inv.getUnidadMedida());
            row.createCell(5).setCellValue(inv.getCantidad());
            row.createCell(6).setCellValue(inv.getMaterial());
            row.createCell(7).setCellValue(inv.getPrecioUnidad());
        }
        libro.write(response.getOutputStream());
        libro.close();
    }
}