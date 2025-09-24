package com.example.servicio;

import com.example.domain.Matriz;
import com.example.domain.Presupuesto;
import com.example.servicio.PresupuestoServicio;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ReportePresupuestoService {

    private final PresupuestoServicio presupuestoServicio;

    public ReportePresupuestoService(PresupuestoServicio presupuestoServicio) {
        this.presupuestoServicio = presupuestoServicio;
    }

    public byte[] pdfPresupuestosConDetalle() {
        List<Presupuesto> presupuestos = presupuestoServicio.listaPresupuesto();

        // Cache de materiales por id_m
        Map<Integer, Matriz> cacheMatriz = presupuestoServicio.listarMateriales()
                .stream()
                .collect(Collectors.toMap(Matriz::getId_m, m -> m));

        // Estadísticas globales
        int totalObras = presupuestos.size();
        int totalActividades = presupuestos.stream()
                .mapToInt(p -> p.getActiviValues() == null ? 0 : p.getActiviValues().size())
                .sum();
        double totalCantidades = presupuestos.stream()
                .mapToDouble(p -> sumCantidades(p.getActiviValues()))
                .sum();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
            PdfWriter.getInstance(doc, baos);

            doc.open();

            // Título
            Font fTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
            doc.add(new Paragraph("Reporte de Presupuestos", fTitle));
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", new Locale("es", "CO")));
            doc.add(new Paragraph("Generado: " + fecha, FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY)));
            doc.add(new Paragraph(" "));

            // Resumen global
            doc.add(new Paragraph("Resumen Global", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable resumen = new PdfPTable(new float[]{2f, 1f});
            resumen.setWidthPercentage(100);
            addHeader(resumen, "Métrica"); addHeader(resumen, "Valor");
            addCell(resumen, "Total obras"); addCell(resumen, String.valueOf(totalObras));
            addCell(resumen, "Total actividades"); addCell(resumen, String.valueOf(totalActividades));
            addCell(resumen, "Total cantidades"); addCell(resumen, format2(totalCantidades));
            doc.add(resumen);

            doc.add(new Paragraph(" "));
            doc.add(new LineSeparator());
            doc.add(new Paragraph(" "));

            // Detalle de cada obra
            for (Presupuesto p : presupuestos) {
                doc.add(new Paragraph("Obra #" + p.getId_obra() + " — " + safeStr(p.getObraName()),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));

                PdfPTable detalle = new PdfPTable(new float[]{0.8f, 2.5f, 1.0f, 1.0f});
                detalle.setWidthPercentage(100);
                addHeader(detalle, "ID Act.");
                addHeader(detalle, "Material/Actividad");
                addHeader(detalle, "Cantidad");
                addHeader(detalle, "Unidades");

                if (p.getActiviValues() == null || p.getActiviValues().isEmpty()) {
                    PdfPCell c = new PdfPCell(new Phrase("Sin actividades",
                            FontFactory.getFont(FontFactory.HELVETICA, 10)));
                    c.setColspan(4);
                    c.setHorizontalAlignment(Element.ALIGN_CENTER);
                    c.setPadding(6f);
                    detalle.addCell(c);
                } else {
                    p.getActiviValues().entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(entry -> {
                                Integer idMat = entry.getKey();
                                Double cantidad = entry.getValue();

                                Matriz m = cacheMatriz.get(idMat);
                                String nombre = (m != null) ? m.getNombre() : "ID " + idMat;
                                String unidades = (m != null) ? safeStr(m.getUnidades()) : "—";

                                addCell(detalle, safeStr(idMat));
                                addCell(detalle, nombre);
                                addCell(detalle, format2(cantidad));
                                addCell(detalle, unidades);
                            });
                }

                doc.add(detalle);

                doc.add(new Paragraph(" "));
                doc.add(new LineSeparator());
                doc.add(new Paragraph(" "));
            }

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el PDF: " + e.getMessage(), e);
        }
    }

    // ==== Helpers ====
    private static void addHeader(PdfPTable t, String txt) {
        PdfPCell h = new PdfPCell(new Phrase(txt, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        h.setHorizontalAlignment(Element.ALIGN_LEFT);
        h.setBackgroundColor(new Color(240, 240, 240));
        h.setPadding(6f);
        t.addCell(h);
    }

    private static void addCell(PdfPTable t, String txt) {
        PdfPCell c = new PdfPCell(new Phrase(safeStr(txt), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        c.setPadding(5f);
        t.addCell(c);
    }

    private static String safeStr(Object o) {
        return (o == null) ? "—" : o.toString();
    }

    private static String format2(Double d) {
        return String.format(Locale.US, "%.2f", (d == null ? 0.0 : d));
    }

    private static double sumCantidades(Map<Integer, Double> map) {
        if (map == null || map.isEmpty()) return 0.0;
        return map.values().stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
    }
}
