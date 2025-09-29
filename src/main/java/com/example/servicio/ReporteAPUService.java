package com.example.servicio;

import com.example.dao.APUDao;
import com.example.domain.Apu;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Font;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
public class ReporteAPUService {

    private final APUDao apuDao;

    public ReporteAPUService(APUDao apuDao) {
        this.apuDao = apuDao;
    }

    // ====== PUBLIC: TODOS LOS APUs ======
    @Transactional(readOnly = true)
    public void exportarListadoApusPdf(HttpServletResponse response) throws IOException, DocumentException {
        List<Apu> apus = apuDao.findAll();
        renderApusPdf(apus, "apus.pdf", "Listado de APU", response);
    }

    // ====== PUBLIC: APUs SELECCIONADOS POR ID ======
    @Transactional(readOnly = true)
    public void exportarApusSeleccionadosPdf(List<Long> ids, HttpServletResponse response)
            throws IOException, DocumentException {

        if (ids == null || ids.isEmpty()) {
            // Si no llegan IDs, exporta todos
            exportarListadoApusPdf(response);
            return;
        }

        List<Apu> apus = apuDao.findAllById(ids);
        renderApusPdf(apus, "apus_seleccionados.pdf",
                "APU seleccionados (" + apus.size() + ")", response);
    }

    // ====== PRIVATE: RENDER PDF REUTILIZABLE ======
    private void renderApusPdf(List<Apu> apus, String fileName, String tituloDoc, HttpServletResponse response)
            throws IOException, DocumentException {

        // Respuesta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + fileName);

        // Documento
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 40, 36);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        document.addAuthor("TuApp");
        document.addTitle(tituloDoc);
        document.open();

        // Fuentes
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        // Título
        Paragraph titulo = new Paragraph(tituloDoc, titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        document.add(titulo);

        // Tabla
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{10f, 30f, 42f, 10f, 14f, 14f, 14f, 20f});
        table.setHeaderRows(1);

        String[] headers = {
                "ID", "Nombre", "Descripción", "Unidad",
                "Materiales", "Mano de Obra", "Transporte", "Misceláneo / Total"
        };

        for (String h : headers) {
            PdfPCell hc = new PdfPCell(new Phrase(h, headerFont));
            hc.setBackgroundColor(new Color(33, 150, 243));
            hc.setHorizontalAlignment(Element.ALIGN_CENTER);
            hc.setPadding(6f);
            table.addCell(hc);
        }

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        BigDecimal sumMat = BigDecimal.ZERO;
        BigDecimal sumMano = BigDecimal.ZERO;
        BigDecimal sumTrans = BigDecimal.ZERO;
        BigDecimal sumMisc = BigDecimal.ZERO;
        BigDecimal sumTotal = BigDecimal.ZERO;

        for (Apu a : apus) {
            BigDecimal mat  = nz(a.getVMaterialesAPU());
            BigDecimal mano = nz(a.getVManoDeObraAPU());
            BigDecimal tran = nz(a.getVTransporteAPU());
            BigDecimal misc = nz(a.getVMiscAPU());
            BigDecimal total = mat.add(mano).add(tran).add(misc);

            sumMat   = sumMat.add(mat);
            sumMano  = sumMano.add(mano);
            sumTrans = sumTrans.add(tran);
            sumMisc  = sumMisc.add(misc);
            sumTotal = sumTotal.add(total);

            table.addCell(cell(String.valueOf(a.getIdAPU()), cellFont));
            table.addCell(cell(nullToEmpty(a.getNombreAPU()), cellFont));
            table.addCell(cell(nullToEmpty(a.getDescAPU()), cellFont));
            table.addCell(cell(nullToEmpty(a.getUnidadesAPU()), cellFont));
            table.addCell(numCell(nf.format(mat), cellFont));
            table.addCell(numCell(nf.format(mano), cellFont));
            table.addCell(numCell(nf.format(tran), cellFont));

            PdfPCell miscTotal = new PdfPCell();
            miscTotal.setPadding(5f);
            miscTotal.addElement(new Phrase(
                    nf.format(misc) + "   |   Total: " + nf.format(total), cellFont));
            miscTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(miscTotal);
        }

        // Totales
        PdfPCell totLabel = new PdfPCell(new Phrase("TOTALES", new Font(Font.HELVETICA, 10, Font.BOLD)));
        totLabel.setColspan(4);
        totLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totLabel.setPadding(6f);
        table.addCell(totLabel);
        table.addCell(numBoldCell(nf.format(sumMat)));
        table.addCell(numBoldCell(nf.format(sumMano)));
        table.addCell(numBoldCell(nf.format(sumTrans)));
        table.addCell(numBoldCell(nf.format(sumTotal)));

        document.add(table);

        Paragraph foot = new Paragraph("Generado automáticamente – " + new java.util.Date(),
                new Font(Font.HELVETICA, 8));
        foot.setSpacingBefore(10f);
        document.add(foot);

        document.close();
        writer.close();
    }

    // ====== HELPERS ======
    private static PdfPCell cell(String text, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(bytesafe(text), f));
        c.setPadding(5f);
        return c;
    }

    private static PdfPCell numCell(String text, Font f) {
        PdfPCell c = cell(text, f);
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return c;
    }

    private static PdfPCell numBoldCell(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, new Font(Font.HELVETICA, 10, Font.BOLD)));
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c.setPadding(6f);
        return c;
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    // Evita problemas con caracteres
    private static String bytesafe(String s) {
        if (s == null) return "";
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        return new String(b, StandardCharsets.UTF_8);
    }
}
