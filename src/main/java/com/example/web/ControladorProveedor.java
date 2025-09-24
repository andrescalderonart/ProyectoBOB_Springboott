package com.example.web;

import com.example.dao.IndividuoDao;
import com.example.domain.InformacionComercial;
import com.example.domain.Proveedor;
import com.example.servicio.ProveedorServicio;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/proveedores")
public class ControladorProveedor {

    private final ProveedorServicio proveedorServicio;
    private final IndividuoDao individuoDao;

    public ControladorProveedor(ProveedorServicio proveedorServicio,
                                IndividuoDao individuoDao) {
        this.proveedorServicio = proveedorServicio;
        this.individuoDao = individuoDao;
    }

    @ModelAttribute("individuos")
    public List<?> cargarIndividuos() {
        return StreamSupport.stream(individuoDao.findAll().spliterator(), false)
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
            return "proveedores/formulario";
        }
        try {
            proveedorServicio.guardar(proveedor);
            flash.addFlashAttribute("ok", proveedor.getIdProveedor() == null
                    ? "Proveedor creado correctamente"
                    : "Proveedor actualizado correctamente");
            return "redirect:/proveedores/inicioProveedor";
        } catch (IllegalArgumentException ex) {
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
}
