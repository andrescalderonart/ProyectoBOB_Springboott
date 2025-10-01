package com.example.controller.web;

import com.example.dao.PersonaDao;
import com.example.domain.InformacionComercial;
import com.example.domain.Material;
import com.example.domain.Persona;
import com.example.domain.Proveedor;
import com.example.servicio.ProveedorServicio;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/proveedores")
public class ControladorProveedores {

    private final ProveedorServicio proveedorServicio;
    private final PersonaDao personaDao;

    public ControladorProveedores(ProveedorServicio proveedorServicio,
                                  PersonaDao personaDao) {
        this.proveedorServicio = proveedorServicio;
        this.personaDao = personaDao;
    }

    @ModelAttribute("persona")
    public List<?> cargarPersonas() {
        return StreamSupport.stream(personaDao.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    private void setDashboard(Authentication auth, HttpSession session) {
        String destino = "/login?error=sin-permisos";
        if (auth != null) {
            Collection<?> auths = auth.getAuthorities();
            if (auths != null && !auths.isEmpty()) {
                String rol = auths.iterator().next().toString();
                destino = switch (rol) {
                    case "ROLE_ADMINISTRADOR" -> "/dashboardADMIN";
                    case "ROLE_OPERATIVO"     -> "/dashboardOPERA";
                    case "ROLE_SUPERVISOR"    -> "/dashboardSUPER";
                    default -> destino;
                };
            }
        }
        session.setAttribute("dashboardOrigen", destino);
    }

    @GetMapping({"", "/"})
    public String raiz(Model model, Authentication auth, HttpSession session) {
        return inicioProveedor(model, auth, session);
    }

    @GetMapping("/inicioProveedor")
    public String inicioProveedor(Model model, Authentication auth, HttpSession session) {
        setDashboard(auth, session);
        model.addAttribute("proveedores", proveedorServicio.listar()); // LISTA real
        return "proveedores/inicioProveedor";
    }

    // NUEVO
    @GetMapping("/formulario")
    public String nuevo(Model model) {
        Proveedor p = new Proveedor();
        if (p.getInformacionComercial() == null) {
            p.setInformacionComercial(new InformacionComercial());
        }
        if (p.getIdPersona()==null){
            p.setIdPersona(new Persona());
        }
        model.addAttribute("proveedor", p);
        model.addAttribute("Editando", false);
        return "proveedores/formulario"; // tu vista existente
    }

    // GUARDAR (crear/actualizar)
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("proveedor") Proveedor proveedor,
                         BindingResult result,
                         Model model,
                         RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("Editando", proveedor.getIdProveedor() != null);
            return "/proveedores/inicioProveedor";
        }
        try {
            proveedorServicio.guardar(proveedor);
            flash.addFlashAttribute("ok", proveedor.getIdProveedor() == null
                    ? "Proveedor creado correctamente"
                    : "Proveedor actualizado correctamente");
            return "redirect:/proveedores/inicioProveedor";
        } catch (IllegalArgumentException ex) {
            result.rejectValue("informacionComercial.nitRut", "duplicado", "El NIT ya está registrado.");
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("Editando", proveedor.getIdProveedor() != null);
            return "proveedores/formulario";
        }
    }

    // EDITAR (carga form con datos)
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Proveedor p = proveedorServicio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no existe"));
        if (p.getInformacionComercial() == null) {
            p.setInformacionComercial(new InformacionComercial());
        }
        model.addAttribute("proveedor", p);
        model.addAttribute("Editando", true);
        return "proveedores/formulario";
    }

    // ELIMINAR
    @PostMapping("/borrar/{id}")
    public String borrar(@PathVariable Long id, RedirectAttributes flash) {
        proveedorServicio.eliminar(id);
        flash.addFlashAttribute("ok", "Proveedor eliminado");
        return "redirect:/proveedores/inicioProveedor";
    }

    // Ping opcional
    @GetMapping("/ping")
    @ResponseBody public String ping() { return "ok"; }

    // Reporte de proveedores
    @GetMapping("/proveedores/excel")
    public void exportarProveedoresExcel(HttpServletResponse response) throws IOException {
        List<Proveedor> proveedores = proveedorServicio.listar();
        String nombreArchivo = "reporte_proveedores.xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo);

        Workbook libro = new XSSFWorkbook();
        Sheet hoja = libro.createSheet("Proveedores");

        // Crear encabezados
        Row header = hoja.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("NIT");
        header.createCell(3).setCellValue("Banco");
        header.createCell(4).setCellValue("Cuenta");
        header.createCell(5).setCellValue("Contacto");
        header.createCell(6).setCellValue("Productos/Servicios");

        // Llenar datos
        int fila = 1;
        for (Proveedor proveedor : proveedores) {
            Row row = hoja.createRow(fila++);
            row.createCell(0).setCellValue(proveedor.getIdProveedor());
            row.createCell(1).setCellValue(proveedor.getIdPersona().getNombre());
            row.createCell(2).setCellValue(proveedor.getInformacionComercial().getNitRut());
            row.createCell(3).setCellValue(proveedor.getInformacionComercial().getBanco());
            row.createCell(4).setCellValue(proveedor.getInformacionComercial().getNumCuenta());
            row.createCell(5).setCellValue(proveedor.getInformacionComercial().getCorreoElectronico());
            row.createCell(6).setCellValue(proveedor.getInformacionComercial().getProducto());
            for (Material material : proveedor.getMaterialList()){
                row = hoja.createRow(fila++);
                row.createCell(0).setCellValue(material.getNombreMaterial());
                row.createCell(1).setCellValue(material.getPrecioMaterial().longValue());
                row.createCell(3).setCellValue(material.getUnidadMaterial());

            }


        }

        libro.write(response.getOutputStream());
        libro.close();
    }

    // Reporte de proveedores PARA CORREOS
    @GetMapping("/proveedores/excelCorreo")
    // Métod0 para generar el reporte y devolverlo como byte array
    public byte[] generarReporteProveedoresExcel() throws IOException {
        List<Proveedor> proveedores = proveedorServicio.listar();

        Workbook libro = new XSSFWorkbook();
        Sheet hoja = libro.createSheet("Proveedores");

        // Crear encabezados
        Row header = hoja.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Dirección");
        header.createCell(3).setCellValue("Contacto");
        header.createCell(4).setCellValue("Teléfono");
        header.createCell(5).setCellValue("Email");

        // Llenar datos
        int fila = 1;
        for (Proveedor proveedor : proveedores) {
            Row row = hoja.createRow(fila++);
            row.createCell(0).setCellValue(proveedor.getIdProveedor());
            row.createCell(1).setCellValue(proveedor.getIdPersona().getNombre() + " " + proveedor.getIdPersona().getApellido());
            row.createCell(4).setCellValue(proveedor.getIdPersona().getTelefono());
            row.createCell(5).setCellValue(proveedor.getIdPersona().getCorreo());

            // Materiales
            for (Material material : proveedor.getMaterialList()) {
                Row materialRow = hoja.createRow(fila++);
                materialRow.createCell(0).setCellValue("Material: " + material.getNombreMaterial());
                materialRow.createCell(1).setCellValue("Precio: " + material.getPrecioMaterial());
                materialRow.createCell(2).setCellValue("Unidad: " + material.getUnidadMaterial());
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        libro.write(outputStream);
        libro.close();

        return outputStream.toByteArray();
    }
}
