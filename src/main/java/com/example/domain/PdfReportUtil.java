package com.example.domain;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
public class PdfReportUtil {


        public static byte[] buildStandardA4(String titulo, ElementBuilder bodyBuilder) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
                PdfWriter writer = PdfWriter.getInstance(doc, baos);

                writer.setPageEvent(new PdfPageEventHelper() {
                    @Override public void onEndPage(PdfWriter w, Document d) {
                        PdfContentByte cb = w.getDirectContent();
                        Phrase footer = new Phrase(titulo + "  •  página " + d.getPageNumber(),
                                FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY));
                        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                                footer, (d.right() + d.left())/2, d.bottom() - 10, 0);
                    }
                });

                doc.open();

                // Título
                Font fTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
                Paragraph header = new Paragraph(titulo, fTitle);
                header.setAlignment(Element.ALIGN_LEFT);
                doc.add(header);

                // Fecha
                var dt = LocalDateTime.now();
                String fecha = dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", new Locale("es","CO")));
                Paragraph meta = new Paragraph("Generado: " + fecha, FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY));
                meta.setSpacingAfter(10f);
                doc.add(meta);

                // Cuerpo delegado
                bodyBuilder.build(doc);

                doc.close();
                return baos.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException("No se pudo generar el PDF: " + e.getMessage(), e);
            }
        }

        public interface ElementBuilder { void build(Document doc) throws Exception; }

        // Helpers
        public static void addSectionTitle(Document doc, String text) throws Exception {
            doc.add(new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 4)));
        }

        public static PdfPTable newTable(float[] widths) {
            PdfPTable t = new PdfPTable(widths);
            t.setWidthPercentage(100);
            return t;
        }

        public static void addHeader(PdfPTable t, String txt) {
            PdfPCell h = new PdfPCell(new Phrase(txt, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            h.setHorizontalAlignment(Element.ALIGN_LEFT);
            h.setBackgroundColor(new Color(240,240,240));
            h.setPadding(6f);
            t.addCell(h);
        }

        public static void addCell(PdfPTable t, String txt) {
            PdfPCell c = new PdfPCell(new Phrase(nullToDash(txt), FontFactory.getFont(FontFactory.HELVETICA, 10)));
            c.setPadding(5f);
            t.addCell(c);
        }

        public static void addSeparator(Document doc) throws Exception {
            doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 6)));
            doc.add(new LineSeparator());
        }

        public static String nullToDash(String s) { return (s == null || s.isBlank()) ? "—" : s; }
    }


