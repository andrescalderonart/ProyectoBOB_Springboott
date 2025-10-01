package com.example.controller.web;

import com.example.servicio.ReporteAPUService;
import com.example.servicio.ReporteProveedorService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    // ===== Proveedores (PDF general)
    @GetMapping("/proveedores.pdf")
    public void reporteProveedores(HttpServletResponse response) throws IOException {
        byte[] pdf = reporteProveedorService.generarReporteProveedores();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=reporte_proveedores.pdf");
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
        response.flushBuffer();
    }

    // ===== APU: PDF general o por ids vía GET (ids en query ?ids=1&ids=2...)
    @GetMapping(value = "/apus/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void apusPdfGET(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            HttpServletResponse response
    ) throws IOException, DocumentException {
        if (ids == null || ids.isEmpty()) {
            reporteApuService.exportarListadoApusPdf(response);      // todos
        } else {
            reporteApuService.exportarApusSeleccionadosPdf(ids, response); // seleccionados
        }
    }

    // (Alias opcional si aún usas /reportes/apus.pdf en vistas antiguas)
    @GetMapping(value = "/apus.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void apusPdfAlias(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            HttpServletResponse response
    ) throws IOException, DocumentException {
        apusPdfGET(ids, response);
    }

    // ===== APU: Exportar seleccionados vía POST (coincide con tu form con CSRF)
    @PostMapping(value = "/apus/seleccionados/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void apusPdfPOST(
            @RequestParam("ids") List<Long> ids,
            HttpServletResponse response
    ) throws Exception {
        // ids nunca será null aquí porque el form siempre envía al menos uno (validado en JS)
        reporteApuService.exportarApusSeleccionadosPdf(ids, response);
    }

    // ===== APU: Resumen estadístico (topN por query param)
    @GetMapping("/apus/estadisticas")
    public void resumenApus(
            @RequestParam(name = "topN", defaultValue = "10") int topN,
            HttpServletResponse response
    ) throws Exception {
        reporteApuService.exportarResumenEstadisticoApusPdf(response, topN);
    }
}
