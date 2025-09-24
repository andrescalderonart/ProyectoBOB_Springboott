package com.example.web;

import com.example.servicio.ReporteProveedorService;
import com.example.servicio.ReportePresupuestoService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/reportes")
public class ControladorReportes {

    private final ReporteProveedorService reporteProveedorService;
    private final ReportePresupuestoService reportePresupuestoService;

    public ControladorReportes(ReporteProveedorService reporteProveedorService,
                               ReportePresupuestoService reportePresupuestoService) {
        this.reporteProveedorService = reporteProveedorService;
        this.reportePresupuestoService = reportePresupuestoService;
    }

    @GetMapping("/proveedores.pdf")
    public void reporteProveedores(HttpServletResponse response) throws IOException {
        byte[] pdf = reporteProveedorService.generarReporteProveedores();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=reporte_proveedores.pdf");
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
        response.flushBuffer();
    }

    @GetMapping("/presupuestos.pdf")
    public void reportePresupuestos(HttpServletResponse res) throws IOException {
        byte[] pdf = reportePresupuestoService.pdfPresupuestosConDetalle();
        res.setContentType("application/pdf");
        res.setHeader("Content-Disposition", "inline; filename=reporte_presupuestos.pdf");
        res.setContentLength(pdf.length);
        res.getOutputStream().write(pdf);
        res.flushBuffer();
    }
}
