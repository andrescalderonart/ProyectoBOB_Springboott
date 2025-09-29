package com.example.controller.web;

import com.example.servicio.ReporteAPUService;
import com.example.servicio.ReporteProveedorService;

import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    // Controlador de reportes
    @GetMapping(value = "/apus.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void apusPdf(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            HttpServletResponse response
    ) throws IOException, DocumentException {

        if (ids == null || ids.isEmpty()) {
            reporteApuService.exportarListadoApusPdf(response);      // todos
        } else {
            reporteApuService.exportarApusSeleccionadosPdf(ids, response); // seleccionados
        }
    }

}

