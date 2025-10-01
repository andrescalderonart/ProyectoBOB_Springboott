package com.example.servicio;

import com.example.dao.ProveedorDao;
import com.example.domain.Proveedor;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteProveedorService {

    private final ProveedorDao proveedorDao;

    public ReporteProveedorService(ProveedorDao proveedorDao) {
        this.proveedorDao = proveedorDao;
    }

    public byte[] generarReporteProveedores() {
        List<Proveedor> proveedores = proveedorDao.findAll();

        // Pequeñas estadísticas (ejemplos)
        Map<String, Long> porBanco = proveedores.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getInformacionComercial() != null ? nullToDash(p.getInformacionComercial().getBanco()) : "—",
                        Collectors.counting()));

        Map<String, Long> porFormaPago = proveedores.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getInformacionComercial() != null ? nullToDash(p.getInformacionComercial().getFormaPago()) : "—",
                        Collectors.counting()));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
            PdfWriter writer = PdfWriter.getInstance(doc, baos);

            // Encabezado/Pie de página simple
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override public void onEndPage(PdfWriter w, Document d) {
                    PdfContentByte cb = w.getDirectContent();
                    Phrase footer = new Phrase("Reporte de Proveedores  •  página " + d.getPageNumber(),
                            FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY));
                    ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                            footer, (d.right() + d.left())/2, d.bottom() - 10, 0);
                }
            });

            doc.open();

            // Título
            Font fTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
            Paragraph title = new Paragraph("Reporte de Proveedores", fTitle);
            title.setAlignment(Element.ALIGN_LEFT);
            doc.add(title);

            // Fecha/hora
            var dt = LocalDateTime.now();
            String fecha = dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", new Locale("es", "CO")));
            Paragraph meta = new Paragraph("Generado: " + fecha + "   •   Total: " + proveedores.size(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY));
            meta.setSpacingAfter(12f);
            doc.add(meta);

            // Sección: Estadísticas (resumen)
            doc.add(new Paragraph("Resumen estadístico", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 4)));

            PdfPTable stats = new PdfPTable(2);
            stats.setWidths(new float[]{1.2f, 3f});
            stats.setWidthPercentage(100);

            PdfPCell c1 = new PdfPCell(new Phrase("Proveedores por Banco", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            c1.setBackgroundColor(new Color(240,240,240));
            c1.setColspan(2);
            c1.setPadding(6f);
            stats.addCell(c1);
            if (porBanco.isEmpty()) {
                addStatRow(stats, "—", "0");
            } else {
                porBanco.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey(String::compareToIgnoreCase))
                        .forEach(e -> addStatRow(stats, e.getKey(), String.valueOf(e.getValue())));
            }

            PdfPCell sep = new PdfPCell(new Phrase(" "));
            sep.setColspan(2);
            sep.setBorder(Rectangle.NO_BORDER);
            sep.setFixedHeight(6f);
            stats.addCell(sep);

            PdfPCell c2 = new PdfPCell(new Phrase("Proveedores por Forma de Pago", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            c2.setBackgroundColor(new Color(240,240,240));
            c2.setColspan(2);
            c2.setPadding(6f);
            stats.addCell(c2);
            if (porFormaPago.isEmpty()) {
                addStatRow(stats, "—", "0");
            } else {
                porFormaPago.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey(String::compareToIgnoreCase))
                        .forEach(e -> addStatRow(stats, e.getKey(), String.valueOf(e.getValue())));
            }

            doc.add(stats);

            doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 12)));
            doc.add(new LineSeparator());

            // Sección: Tabla de proveedores
            doc.add(new Paragraph("Detalle de Proveedores", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 6)));

            PdfPTable table = new PdfPTable(7);
            table.setWidths(new float[]{1.4f, 1.2f, 1.0f, 1.2f, 1.2f, 1.2f, 1.2f});
            table.setWidthPercentage(100);

            addHeader(table, "Nombre/Razón Social");
            addHeader(table, "NIT/RUT");
            addHeader(table, "Forma Pago");
            addHeader(table, "Banco");
            addHeader(table, "No. Cuenta");
            addHeader(table,"Contacto");
            addHeader(table, "Direccion");

            for (Proveedor p : proveedores) {
                String nom = (p.getIdPersona() != null)
                        ? (nullToDash(p.getIdPersona().getNombre()) + " " +
                        nullToDash(p.getIdPersona().getApellido()))
                        : "—";

                String nit = (p.getInformacionComercial() != null)
                        ? nullToDash(p.getInformacionComercial().getNitRut())
                        : "—";

                String fp  = (p.getInformacionComercial() != null)
                        ? nullToDash(p.getInformacionComercial().getFormaPago())
                        : "—";

                String ban = (p.getInformacionComercial() != null)
                        ? nullToDash(p.getInformacionComercial().getBanco())
                        : "—";

                String cta = (p.getInformacionComercial() != null)
                        ? nullToDash(p.getInformacionComercial().getNumCuenta())
                        : "—";
                String con = (p.getInformacionComercial() != null)
                        ? nullToDash(p.getInformacionComercial().getCorreoElectronico())
                        :"—";
                String dir = (p.getInformacionComercial() != null)
                        ? nullToDash(p.getInformacionComercial().getDireccion())
                        :"—";

                addCell(table, nom);
                addCell(table, nit);
                addCell(table, fp);
                addCell(table, ban);
                addCell(table, cta);
                addCell(table, con);
                addCell(table, dir);
            }


            doc.add(table);
            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el PDF: " + e.getMessage(), e);
        }
    }

    private static void addHeader(PdfPTable t, String txt) {
        PdfPCell h = new PdfPCell(new Phrase(txt, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        h.setHorizontalAlignment(Element.ALIGN_LEFT);
        h.setBackgroundColor(new Color(240,240,240));
        h.setPadding(6f);
        t.addCell(h);
    }

    private static void addCell(PdfPTable t, String txt) {
        PdfPCell c = new PdfPCell(new Phrase(txt, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        c.setPadding(5f);
        t.addCell(c);
    }

    private static void addStatRow(PdfPTable t, String k, String v) {
        PdfPCell ck = new PdfPCell(new Phrase(k, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        ck.setPadding(5f);
        PdfPCell cv = new PdfPCell(new Phrase(v, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cv.setPadding(5f);
        t.addCell(ck);
        t.addCell(cv);
    }

    private static String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

}
