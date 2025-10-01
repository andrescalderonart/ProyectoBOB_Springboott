package com.example.servicio;

import com.example.dao.APUDao;
import com.example.domain.Apu;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
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

    @Transactional(readOnly = true)
    public void exportarListadoApusPdf(HttpServletResponse response) throws IOException, DocumentException {

        // 1) Datos
        // Usa el que mejor te funcione en tu contexto:
        // List<Apu> apus = apuDao.findAllWithRels();
        List<Apu> apus = apuDao.findAll();

        // 2) Config respuesta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=apus.pdf");

        // 3) Documento
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 40, 36); // apaisado
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        document.addAuthor("TuApp");
        document.addTitle("Listado de APU");
        document.open();

        // 4) Fuentes (asegura tildes/ñ)
        // BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
        // Para UTF-8 robusto, podrías incrustar una TTF de tu proyecto (resources/fonts/FreeSans.ttf)
        // BaseFont bf = BaseFont.createFont("fonts/FreeSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        // 5) Título
        Paragraph titulo = new Paragraph("Listado de APU", titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        document.add(titulo);

        // 6) Tabla
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{10f, 30f, 42f, 10f, 14f, 14f, 14f, 14f});
        table.setHeaderRows(1);

        // Encabezados
        String[] headers = {
                "ID",
                "Nombre",
                "Descripción",
                "Unidad",
                "Materiales",
                "Mano de Obra",
                "Transporte",
                "Misceláneo / Total"
        };

        for (String h : headers) {
            PdfPCell hc = new PdfPCell(new Phrase(h, headerFont));
            hc.setBackgroundColor(new Color(33, 150, 243));
            hc.setHorizontalAlignment(Element.ALIGN_CENTER);
            hc.setPadding(6f);
            table.addCell(hc);
        }

        // 7) Formateo moneda
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        // 8) Filas
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
            miscTotal.addElement(new Phrase(nf.format(misc) + "   |   Total: " + nf.format(total), cellFont));
            miscTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(miscTotal);
        }

        // 9) Fila totales
        PdfPCell totLabel = new PdfPCell(new Phrase("TOTALES", new Font(Font.HELVETICA, 10, Font.BOLD)));
        totLabel.setColspan(4);
        totLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totLabel.setPadding(6f);
        table.addCell(totLabel);
        table.addCell(numBoldCell(nf.format(sumMat)));
        table.addCell(numBoldCell(nf.format(sumMano)));
        table.addCell(numBoldCell(nf.format(sumTrans)));
        table.addCell(numBoldCell(nf.format(sumTotal))); // aquí ponemos el total global

        document.add(table);

        // 10) Pie
        Paragraph foot = new Paragraph("Generado automáticamente – " + new java.util.Date(), new Font(Font.HELVETICA, 8));
        foot.setSpacingBefore(10f);
        document.add(foot);

        document.close();
        writer.close();
    }

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

    // Evita problemas con caracteres fuera de CP1252 cuando NO incrustas TTF
    private static String bytesafe(String s) {
        if (s == null) return "";
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        return new String(b, StandardCharsets.UTF_8);
    }
}
