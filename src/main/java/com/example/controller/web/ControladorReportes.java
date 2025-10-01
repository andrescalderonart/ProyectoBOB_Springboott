package com.example.controller.web;

import com.example.servicio.ReporteAPUService;
import com.example.servicio.ReporteProveedorService;

import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/reportes")
public class ControladorReportes {

    private final ReporteProveedorService reporteProveedorService;
    private final ReporteAPUService reporteApuService;

    public ControladorReportes(ReporteProveedorService reporteProveedorService,
    ReporteAPUService reportesApuService){
        this.reporteApuService = reportesApuService;
        this.reporteProveedorService = reporteProveedorService;
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

    @GetMapping("/apus.pdf")
    public void apusPdf(HttpServletResponse response) throws IOException, DocumentException {
        reporteApuService.exportarListadoApusPdf(response);
    }
}

