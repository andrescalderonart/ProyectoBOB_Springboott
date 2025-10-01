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
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            exportarListadoApusPdf(response);
            return;
        }

        List<Apu> apus = apuDao.findAllById(ids);
        renderApusPdf(apus, "apus_seleccionados.pdf",
                "APU seleccionados (" + apus.size() + ")", response);
    }

    // ====== PUBLIC: REPORTE ESTADÍSTICO ======
    /**
     * Genera un reporte estadístico con KPIs, porcentajes, top N por total y totales por Unidad.
     * @param response HttpServletResponse
     * @param topN cantidad de registros a mostrar en los rankings (ej: 10)
     */
    @Transactional(readOnly = true)
    public void exportarResumenEstadisticoApusPdf(HttpServletResponse response, int topN)
            throws IOException, DocumentException {

        List<Apu> apus = apuDao.findAll();
        if (apus == null) apus = Collections.emptyList();

        // Config respuesta
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=apus_estadisticas.pdf");

        // Documento
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 40, 36);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        document.addAuthor("TuApp");
        document.addTitle("Estadísticas de APU");
        document.open();

        // Fuentes
        Font titleFont  = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        Font cellFont   = new Font(Font.HELVETICA, 9, Font.NORMAL);
        Font kpiFont    = new Font(Font.HELVETICA, 11, Font.BOLD);

        NumberFormat nfCur = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        NumberFormat nfPct = NumberFormat.getPercentInstance(new Locale("es", "CO"));
        nfPct.setMaximumFractionDigits(2);

        // ===== Título
        Paragraph titulo = new Paragraph("Resumen Estadístico de APU", titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(12f);
        document.add(titulo);

        // ===== Cálculos base
        BigDecimal sumMat   = BigDecimal.ZERO;
        BigDecimal sumMano  = BigDecimal.ZERO;
        BigDecimal sumTrans = BigDecimal.ZERO;
        BigDecimal sumMisc  = BigDecimal.ZERO;
        BigDecimal sumTotal = BigDecimal.ZERO;

        List<RegistroCalc> calcs = new ArrayList<>(apus.size());
        for (Apu a : apus) {
            BigDecimal m  = nz(a.getVMaterialesAPU());
            BigDecimal mo = nz(a.getVManoDeObraAPU());
            BigDecimal t  = nz(a.getVTransporteAPU());
            BigDecimal mi = nz(a.getVMiscAPU());
            BigDecimal tot = m.add(mo).add(t).add(mi);

            sumMat   = sumMat.add(m);
            sumMano  = sumMano.add(mo);
            sumTrans = sumTrans.add(t);
            sumMisc  = sumMisc.add(mi);
            sumTotal = sumTotal.add(tot);

            calcs.add(new RegistroCalc(a, m, mo, t, mi, tot));
        }

        int n = calcs.size();
        BigDecimal bdN = BigDecimal.valueOf(Math.max(n, 1));
        BigDecimal promMat   = safeDiv(sumMat, bdN);
        BigDecimal promMano  = safeDiv(sumMano, bdN);
        BigDecimal promTrans = safeDiv(sumTrans, bdN);
        BigDecimal promMisc  = safeDiv(sumMisc, bdN);
        BigDecimal promTotal = safeDiv(sumTotal, bdN);

        // ===== KPIs generales
        PdfPTable kpi = new PdfPTable(5);
        kpi.setWidthPercentage(100);
        kpi.setSpacingAfter(8f);
        kpi.setWidths(new float[]{18f, 16f, 16f, 16f, 16f});

        kpi.addCell(kpiCell("Cantidad de APU", String.valueOf(n), kpiFont));
        kpi.addCell(kpiCell("Total Materiales", nfCur.format(sumMat), kpiFont));
        kpi.addCell(kpiCell("Total Mano de Obra", nfCur.format(sumMano), kpiFont));
        kpi.addCell(kpiCell("Total Transporte", nfCur.format(sumTrans), kpiFont));
        kpi.addCell(kpiCell("Total Misceláneo", nfCur.format(sumMisc), kpiFont));
        document.add(kpi);

        PdfPTable kpi2 = new PdfPTable(5);
        kpi2.setWidthPercentage(100);
        kpi2.setSpacingAfter(12f);
        kpi2.setWidths(new float[]{20f, 20f, 20f, 20f, 20f});

        kpi2.addCell(kpiCell("TOTAL GENERAL", nfCur.format(sumTotal), kpiFont));
        kpi2.addCell(kpiCell("Promedio Total/APU", nfCur.format(promTotal), kpiFont));
        kpi2.addCell(kpiCell("% Materiales", pct(sumMat, sumTotal, nfPct), kpiFont));
        kpi2.addCell(kpiCell("% Mano de Obra", pct(sumMano, sumTotal, nfPct), kpiFont));
        kpi2.addCell(kpiCell("% Transporte+Miscel.", pct(sumTrans.add(sumMisc), sumTotal, nfPct), kpiFont));
        document.add(kpi2);

        // ===== Tabla de promedios por rubro
        PdfPTable proms = new PdfPTable(5);
        proms.setHeaderRows(1);
        proms.setWidthPercentage(100);
        proms.setSpacingAfter(12f);
        proms.setWidths(new float[]{20f, 20f, 20f, 20f, 20f});

        addHeader(proms, headerFont, "Prom. Materiales", "Prom. Mano de Obra", "Prom. Transporte", "Prom. Misceláneo", "Prom. Total/APU");
        proms.addCell(numCell(nfCur.format(promMat), cellFont));
        proms.addCell(numCell(nfCur.format(promMano), cellFont));
        proms.addCell(numCell(nfCur.format(promTrans), cellFont));
        proms.addCell(numCell(nfCur.format(promMisc), cellFont));
        proms.addCell(numCell(nfCur.format(promTotal), cellFont));
        document.add(proms);


        // ===== Top N APU por Total
        int limit = Math.min(Math.max(topN, 1), Math.max(n, 1));
        List<RegistroCalc> top = calcs.stream()
                .sorted(Comparator.comparing(RegistroCalc::total).reversed())
                .limit(limit)
                .toList();

        Paragraph subtTop = new Paragraph("Top " + limit + " APU por costo total", new Font(Font.HELVETICA, 12, Font.BOLD));
        subtTop.setSpacingAfter(6f);
        document.add(subtTop);

        PdfPTable tablaTop = new PdfPTable(7);
        tablaTop.setHeaderRows(1);
        tablaTop.setWidthPercentage(100);
        tablaTop.setSpacingAfter(12f);
        tablaTop.setWidths(new float[]{10f, 26f, 12f, 13f, 13f, 13f, 13f});
        addHeader(tablaTop, headerFont, "ID", "Nombre", "Materiales", "Mano de Obra", "Transporte", "Misceláneo", "Total");

        for (RegistroCalc r : top) {
            tablaTop.addCell(cell(String.valueOf(r.apu().getIdAPU()), cellFont));
            tablaTop.addCell(cell(nullToEmpty(r.apu().getNombreAPU()), cellFont));
            tablaTop.addCell(numCell(nfCur.format(r.mat()), cellFont));
            tablaTop.addCell(numCell(nfCur.format(r.mano()), cellFont));
            tablaTop.addCell(numCell(nfCur.format(r.trans()), cellFont));
            tablaTop.addCell(numCell(nfCur.format(r.misc()), cellFont));
            tablaTop.addCell(numBoldCell(nfCur.format(r.total())));
        }
        document.add(tablaTop);

        try {
            // --- Pie: distribución por rubros (usa los totales agregados)
            org.jfree.data.general.DefaultPieDataset<String> pie = new org.jfree.data.general.DefaultPieDataset<>();
            pie.setValue("Materiales",   sumMat.doubleValue());
            pie.setValue("Mano de Obra", sumMano.doubleValue());
            pie.setValue("Transporte",   sumTrans.doubleValue());
            pie.setValue("Misceláneo",   sumMisc.doubleValue());

            org.jfree.chart.JFreeChart pieChart = org.jfree.chart.ChartFactory.createPieChart(
                    "Distribución por rubros", pie, true, false, false);

            com.lowagie.text.Image pieImg = chartToItextImage(pieChart, 560, 320);
            pieImg.setSpacingBefore(6f);
            document.add(pieImg);

            // --- Barras: Top N por total (usa la lista 'top')
            org.jfree.data.category.DefaultCategoryDataset bar = new org.jfree.data.category.DefaultCategoryDataset();
            for (RegistroCalc r : top) {
                String label = (r.apu().getNombreAPU() == null || r.apu().getNombreAPU().isBlank())
                        ? ("APU " + r.apu().getIdAPU())
                        : r.apu().getNombreAPU();
                bar.addValue(r.total().doubleValue(), "Total", label);
            }

            org.jfree.chart.JFreeChart barChart = org.jfree.chart.ChartFactory.createBarChart(
                    "Top " + limit + " APU por costo total", "APU", "COP", bar);

            com.lowagie.text.Image barImg = chartToItextImage(barChart, 760, 360);
            barImg.setSpacingBefore(10f);
            document.add(barImg);

        } catch (Exception e) {
            // Si hay algún problema con las librerías de imagen, no rompas el PDF:
            Paragraph warn = new Paragraph("No fue posible renderizar las gráficas: " + e.getMessage(),
                    new Font(Font.HELVETICA, 9, Font.ITALIC, Color.RED));
            warn.setSpacingBefore(8f);
            document.add(warn);
        }

        // ===== Totales por Unidad (si aplica)
        Map<String, BigDecimal> totalPorUnidad = calcs.stream()
                .collect(Collectors.groupingBy(
                        rc -> nullToEmpty(rc.apu().getUnidadesAPU()).trim(),
                        Collectors.mapping(RegistroCalc::total,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        // Elimina clave vacía si no aporta
        if (totalPorUnidad.size() > 0) {
            Paragraph subtUnidad = new Paragraph("Totales por Unidad (ordenado desc.)", new Font(Font.HELVETICA, 12, Font.BOLD));
            subtUnidad.setSpacingAfter(6f);
            document.add(subtUnidad);

            List<Map.Entry<String, BigDecimal>> unidadesOrd = totalPorUnidad.entrySet().stream()
                    .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                    .limit(20) // evita tablas enormes
                    .toList();

            PdfPTable tablaUnidad = new PdfPTable(3);
            tablaUnidad.setHeaderRows(1);
            tablaUnidad.setWidthPercentage(70);
            tablaUnidad.setWidths(new float[]{40f, 30f, 30f});
            addHeader(tablaUnidad, headerFont, "Unidad", "Total", "% sobre Total General");

            for (Map.Entry<String, BigDecimal> e : unidadesOrd) {
                String unidad = e.getKey().isBlank() ? "(Sin unidad)" : e.getKey();
                BigDecimal totU = e.getValue();
                tablaUnidad.addCell(cell(unidad, cellFont));
                tablaUnidad.addCell(numCell(nfCur.format(totU), cellFont));
                tablaUnidad.addCell(numCell(pct(totU, sumTotal, nfPct), cellFont));
            }
            document.add(tablaUnidad);
        }

        // ===== Nota al pie
        Paragraph foot = new Paragraph("Generado automáticamente – " + new java.util.Date(),
                new Font(Font.HELVETICA, 8));
        foot.setSpacingBefore(10f);
        document.add(foot);

        document.close();
        writer.close();
    }

    // ====== PRIVATE: RENDER TABLA DETALLADA (ya existía) ======
    private void renderApusPdf(List<Apu> apus, String fileName, String tituloDoc, HttpServletResponse response)
            throws IOException, DocumentException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + fileName);

        Document document = new Document(PageSize.A4.rotate(), 36, 36, 40, 36);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        document.addAuthor("TuApp");
        document.addTitle(tituloDoc);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        Paragraph titulo = new Paragraph(tituloDoc, titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        document.add(titulo);

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

    // ===== Helpers UI
    private static void addHeader(PdfPTable table, Font headerFont, String... labels) {
        for (String h : labels) {
            PdfPCell hc = new PdfPCell(new Phrase(bytesafe(h), headerFont));
            hc.setBackgroundColor(new Color(33, 150, 243));
            hc.setHorizontalAlignment(Element.ALIGN_CENTER);
            hc.setPadding(6f);
            table.addCell(hc);
        }
    }

    private static PdfPCell kpiCell(String label, String value, Font font) {
        PdfPTable inner = new PdfPTable(1);
        inner.setWidthPercentage(100);

        PdfPCell c1 = new PdfPCell(new Phrase(bytesafe(label), new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY)));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setPadding(4f);
        c1.setBorderColor(new Color(230,230,230));

        PdfPCell c2 = new PdfPCell(new Phrase(bytesafe(value), font));
        c2.setHorizontalAlignment(Element.ALIGN_LEFT);
        c2.setPadding(6f);
        c2.setBorderColor(new Color(230,230,230));

        inner.addCell(c1);
        inner.addCell(c2);

        PdfPCell wrap = new PdfPCell(inner);
        wrap.setPadding(2f);
        wrap.setBorderColor(new Color(200,200,200));
        return wrap;
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
        PdfPCell c = new PdfPCell(new Phrase(bytesafe(text), new Font(Font.HELVETICA, 10, Font.BOLD)));
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

    private static String bytesafe(String s) {
        if (s == null) return "";
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        return new String(b, StandardCharsets.UTF_8);
    }

    private static String pct(BigDecimal part, BigDecimal whole, NumberFormat nfPct) {
        if (whole == null || whole.compareTo(BigDecimal.ZERO) == 0) return "0%";
        BigDecimal ratio = part.divide(whole, 6, RoundingMode.HALF_UP);
        return nfPct.format(ratio.doubleValue());
    }

    private static BigDecimal safeDiv(BigDecimal a, BigDecimal b) {
        if (b == null || b.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return a.divide(b, 2, RoundingMode.HALF_UP);
    }

    private static com.lowagie.text.Image chartToItextImage(org.jfree.chart.JFreeChart chart, int width, int height) throws Exception {
        java.awt.image.BufferedImage bi = chart.createBufferedImage(width, height);
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bi, "png", baos);
        com.lowagie.text.Image img = com.lowagie.text.Image.getInstance(baos.toByteArray());
        img.setAlignment(Element.ALIGN_CENTER);
        img.setCompressionLevel(9);
        return img;
    }

    // Wrapper para cálculos por APU
    private record RegistroCalc(Apu apu, BigDecimal mat, BigDecimal mano, BigDecimal trans, BigDecimal misc, BigDecimal total) {}
}
