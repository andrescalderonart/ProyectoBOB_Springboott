package com.example.controller.web;

import com.example.domain.Apu;
import com.example.domain.Usuario;
import com.example.servicio.APUServicio;
import com.example.servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/apu")
public class ControladorAPU {

    @Autowired
    private APUServicio apuServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/inicioAPU")
    public String inicioAPU(Model model) {
        model.addAttribute("apus", apuServicio.listarElementos());
        return "apus/inicioAPU";
    }

    @GetMapping("/crearAPU")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("apu", new Apu());
        return "apus/crearAPU";
    }

    // ✅ NUEVO: Método para mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Apu apu = apuServicio.obtenerPorId(id);
        if (apu == null) {
            return "redirect:/apu/inicioAPU";
        }
        model.addAttribute("apu", apu);
        return "apus/editarAPU";
    }

    @PostMapping("/salvar")
    public String salvarAPU(@ModelAttribute Apu apu, BindingResult result) {
        if (result.hasErrors()) {
            return "apus/crearAPU";
        }

        if (apu.getVMaterialesAPU() == null) {
            apu.setVMaterialesAPU(BigDecimal.ZERO);
        }
        if (apu.getVManoDeObraAPU() == null) {
            apu.setVManoDeObraAPU(BigDecimal.ZERO);
        }
        if (apu.getVTransporteAPU() == null) {
            apu.setVTransporteAPU(BigDecimal.ZERO);
        }

        apuServicio.guardar(apu);
        return "redirect:/apu/inicioAPU";
    }

    // ✅ NUEVO: Método para actualizar APU existente
    @PostMapping("/actualizar")
    public String actualizarAPU(@ModelAttribute Apu apu, BindingResult result) {
        if (result.hasErrors()) {
            return "apus/editarAPU";
        }

        if (apu.getVMaterialesAPU() == null) {
            apu.setVMaterialesAPU(BigDecimal.ZERO);
        }
        if (apu.getVManoDeObraAPU() == null) {
            apu.setVManoDeObraAPU(BigDecimal.ZERO);
        }
        if (apu.getVTransporteAPU() == null) {
            apu.setVTransporteAPU(BigDecimal.ZERO);
        }

        apuServicio.guardar(apu);
        return "redirect:/apu/inicioAPU";
    }

    @GetMapping("/detalle/{id}")
    public String verDetalleAPU(@PathVariable Long id, Model model) {
        Apu apu = apuServicio.obtenerPorId(id);
        model.addAttribute("apu", apu);
        return "apus/detalleAPU";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarAPU(@PathVariable Long id) {
        apuServicio.eliminar(apuServicio.obtenerPorId(id));
        return "redirect:/apu/inicioAPU";
    }

    @PostMapping("/importar")
    public String importarAPUsDesdeCSV(
            @RequestParam("archivoCSV") MultipartFile archivo,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Por favor seleccione un archivo CSV");
            return "redirect:/apu/inicioAPU";
        }

        if (!archivo.getContentType().equals("text/csv") &&
                !archivo.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            redirectAttributes.addFlashAttribute("error", "El archivo debe ser un CSV válido");
            return "redirect:/apu/inicioAPU";
        }

        try {
            String username = authentication.getName();
            Usuario usuario = usuarioServicio.encontrarPorNombreUsuario(username);

            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/apu/inicioAPU";
            }

            List<Apu> apusImportados = apuServicio.importarAPUsDesdeCSV(archivo, usuario);

            for (Apu apu : apusImportados) {
                apuServicio.guardar(apu);
            }

            redirectAttributes.addFlashAttribute("success",
                    "Se importaron " + apusImportados.size() + " APUs correctamente");

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al procesar el archivo CSV: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error inesperado: " + e.getMessage());
        }

        return "redirect:/apu/inicioAPU";
    }
}