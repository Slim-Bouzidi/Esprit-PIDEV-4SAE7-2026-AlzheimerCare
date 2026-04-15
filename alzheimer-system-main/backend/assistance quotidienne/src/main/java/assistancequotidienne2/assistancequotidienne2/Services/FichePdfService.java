package assistancequotidienne2.assistancequotidienne2.Services;

import assistancequotidienne2.assistancequotidienne2.Entities.FicheTransmission;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FichePdfService {

    private static final Color PRIMARY = new Color(26, 58, 92);
    private static final Color HEADER_BG = new Color(102, 126, 234);
    private static final Color LIGHT_BG = new Color(245, 247, 250);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final DateTimeFormatter FR_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FR_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] genererPdf(FicheTransmission fiche) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new FooterEvent(fiche));
            document.open();

            // Fonts
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, Color.WHITE);
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD, PRIMARY);
            Font labelFont = new Font(Font.HELVETICA, 9, Font.BOLD, new Color(100, 100, 100));
            Font valueFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
            Font smallFont = new Font(Font.HELVETICA, 8, Font.NORMAL, new Color(150, 150, 150));

            // === HEADER BAR ===
            PdfPTable headerBar = new PdfPTable(1);
            headerBar.setWidthPercentage(100);
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(HEADER_BG);
            headerCell.setPadding(15);
            headerCell.setBorder(Rectangle.NO_BORDER);

            Paragraph headerTitle = new Paragraph("FICHE DE TRANSMISSION", titleFont);
            headerTitle.setAlignment(Element.ALIGN_CENTER);
            headerCell.addElement(headerTitle);

            Font subTitleFont = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(220, 220, 255));
            Paragraph subTitle = new Paragraph("Document horodaté — AXE Alzheimer e-Health", subTitleFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            headerCell.addElement(subTitle);

            headerBar.addCell(headerCell);
            document.add(headerBar);
            document.add(new Paragraph(" "));

            // === REFERENCE & DATE ===
            String ref = "FT-" + String.format("%05d", fiche.getId() != null ? fiche.getId() : 0);
            String dateGen = fiche.getDateCreation() != null
                    ? fiche.getDateCreation().format(FR_DATETIME)
                    : LocalDateTime.now().format(FR_DATETIME);
            String dateFiche = fiche.getDateFiche() != null
                    ? fiche.getDateFiche().format(FR_DATE)
                    : LocalDate.now().format(FR_DATE);

            PdfPTable refTable = new PdfPTable(2);
            refTable.setWidthPercentage(100);
            refTable.setWidths(new float[]{1, 1});
            addInfoCell(refTable, "Référence : " + ref, labelFont, Element.ALIGN_LEFT);
            addInfoCell(refTable, "Généré le : " + dateGen, labelFont, Element.ALIGN_RIGHT);
            document.add(refTable);
            document.add(new Paragraph(" "));

            // === SECTION A: Patient & Soignant ===
            document.add(createSectionTitle("A", "INFORMATIONS GÉNÉRALES", sectionFont));

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 1});

            // Parse JSON fields safely
            String patientNom = extractJsonField(fiche.getPatientInfoJson(), "prenom") + " " + extractJsonField(fiche.getPatientInfoJson(), "nom");
            String patientAge = extractJsonField(fiche.getPatientInfoJson(), "age");
            String soignantNom = extractJsonField(fiche.getSoignantInfoJson(), "prenom") + " " + extractJsonField(fiche.getSoignantInfoJson(), "nom");
            String soignantRole = extractJsonField(fiche.getSoignantInfoJson(), "role");

            addLabelValueRow(infoTable, "Patient", patientNom.trim(), labelFont, valueFont);
            addLabelValueRow(infoTable, "Âge", patientAge + " ans", labelFont, valueFont);
            addLabelValueRow(infoTable, "Soignant", soignantNom.trim(), labelFont, valueFont);
            addLabelValueRow(infoTable, "Rôle", soignantRole, labelFont, valueFont);
            addLabelValueRow(infoTable, "Date de la fiche", dateFiche, labelFont, valueFont);
            addLabelValueRow(infoTable, "Statut", fiche.getStatut() != null ? fiche.getStatut().toUpperCase() : "—", labelFont, valueFont);

            document.add(infoTable);
            document.add(new Paragraph(" "));

            // === SECTION B: Observance Médicamenteuse ===
            document.add(createSectionTitle("B", "OBSERVANCE MÉDICAMENTEUSE", sectionFont));
            String medsJson = fiche.getObservanceMedicamentsJson();
            if (medsJson != null && !medsJson.isEmpty()) {
                String listeMeds = extractJsonField(medsJson, "listeMedicaments");
                String totalPris = extractJsonField(medsJson, "totalPris");
                String totalPrevus = extractJsonField(medsJson, "totalPrevus");

                // Parse medication array
                PdfPTable medTable = new PdfPTable(4);
                medTable.setWidthPercentage(100);
                medTable.setWidths(new float[]{3, 2, 2, 1.5f});

                // Header row
                Font headerTableFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
                addTableHeader(medTable, "Médicament", headerTableFont);
                addTableHeader(medTable, "Dosage", headerTableFont);
                addTableHeader(medTable, "Moment", headerTableFont);
                addTableHeader(medTable, "Pris ?", headerTableFont);

                // Parse medications from JSON array
                parseMedicationsToTable(medTable, medsJson, valueFont);

                document.add(medTable);

                Paragraph summary = new Paragraph("Total pris : " + totalPris + " / " + totalPrevus, labelFont);
                summary.setSpacingBefore(5);
                document.add(summary);
            } else {
                document.add(new Paragraph("Aucun médicament renseigné.", smallFont));
            }
            document.add(new Paragraph(" "));

            // === SECTION C: Alimentation ===
            document.add(createSectionTitle("C", "ALIMENTATION ET HYDRATATION", sectionFont));
            String alimJson = fiche.getAlimentationJson();
            if (alimJson != null && !alimJson.isEmpty()) {
                PdfPTable alimTable = new PdfPTable(2);
                alimTable.setWidthPercentage(100);
                addLabelValueRow(alimTable, "Appétit", translateValue(extractJsonField(alimJson, "appetit")), labelFont, valueFont);
                addLabelValueRow(alimTable, "Hydratation", translateValue(extractJsonField(alimJson, "hydratation")), labelFont, valueFont);
                addLabelValueRow(alimTable, "Repas pris", extractJsonField(alimJson, "repasPris") + " / " + extractJsonField(alimJson, "repasPrevus"), labelFont, valueFont);
                String details = extractJsonField(alimJson, "details");
                if (!details.isEmpty()) {
                    addLabelValueRow(alimTable, "Observations", details, labelFont, valueFont);
                }
                document.add(alimTable);
            }
            document.add(new Paragraph(" "));

            // === SECTION D: Vie Sociale ===
            document.add(createSectionTitle("D", "VIE SOCIALE ET HYGIÈNE", sectionFont));
            String vieJson = fiche.getVieSocialeJson();
            if (vieJson != null && !vieJson.isEmpty()) {
                PdfPTable vieTable = new PdfPTable(2);
                vieTable.setWidthPercentage(100);
                addLabelValueRow(vieTable, "Interaction", translateValue(extractJsonField(vieJson, "interaction")), labelFont, valueFont);
                addLabelValueRow(vieTable, "Hygiène", translateValue(extractJsonField(vieJson, "hygiene")), labelFont, valueFont);
                addLabelValueRow(vieTable, "Sommeil", translateValue(extractJsonField(vieJson, "sommeil")), labelFont, valueFont);
                document.add(vieTable);
            }
            document.add(new Paragraph(" "));

            // === SECTION E: Directives ===
            String dirsJson = fiche.getSuiviDirectivesJson();
            if (dirsJson != null && !dirsJson.isEmpty() && !dirsJson.equals("[]")) {
                document.add(createSectionTitle("E", "SUIVI DES DIRECTIVES MÉDICALES", sectionFont));
                PdfPTable dirTable = new PdfPTable(3);
                dirTable.setWidthPercentage(100);
                dirTable.setWidths(new float[]{2, 1, 3});
                addTableHeader(dirTable, "Directive", new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE));
                addTableHeader(dirTable, "Statut", new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE));
                addTableHeader(dirTable, "Commentaire", new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE));
                parseDirectivesToTable(dirTable, dirsJson, valueFont);
                document.add(dirTable);
                document.add(new Paragraph(" "));
            }

            // === SECTION F: Commentaire libre ===
            if (fiche.getCommentaireLibre() != null && !fiche.getCommentaireLibre().isEmpty()) {
                document.add(createSectionTitle("F", "COMMENTAIRES GÉNÉRAUX", sectionFont));
                PdfPCell commentCell = new PdfPCell(new Phrase(fiche.getCommentaireLibre(), valueFont));
                commentCell.setPadding(10);
                commentCell.setBackgroundColor(LIGHT_BG);
                commentCell.setBorderColor(BORDER_COLOR);
                PdfPTable commentTable = new PdfPTable(1);
                commentTable.setWidthPercentage(100);
                commentTable.addCell(commentCell);
                document.add(commentTable);
                document.add(new Paragraph(" "));
            }

            // === SIGNATURE ===
            Paragraph sigParagraph = new Paragraph();
            sigParagraph.setSpacingBefore(15);
            Font sigFont = new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY);
            String sigStatus = Boolean.TRUE.equals(fiche.getSignatureSoignant()) ? "✓ Signé électroniquement" : "✗ Non signé";
            sigParagraph.add(new Chunk("Signature soignant : ", labelFont));
            sigParagraph.add(new Chunk(sigStatus, sigFont));
            document.add(sigParagraph);

            // Confidentiality notice
            Paragraph confidential = new Paragraph(
                    "Document confidentiel — Données médicales protégées — Loi n° 2002-303",
                    new Font(Font.HELVETICA, 7, Font.ITALIC, new Color(150, 150, 150))
            );
            confidential.setAlignment(Element.ALIGN_CENTER);
            confidential.setSpacingBefore(20);
            document.add(confidential);

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF: " + e.getMessage(), e);
        } finally {
            document.close();
        }

        return baos.toByteArray();
    }

    // === Helper Methods ===

    private Paragraph createSectionTitle(String letter, String title, Font font) {
        Paragraph p = new Paragraph();
        p.setSpacingBefore(8);
        p.setSpacingAfter(6);

        Font letterFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
        Chunk letterChunk = new Chunk(" " + letter + " ", letterFont);
        letterChunk.setBackground(PRIMARY);
        p.add(letterChunk);
        p.add(new Chunk("  " + title, font));

        return p;
    }

    private void addInfoCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }

    private void addLabelValueRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(LIGHT_BG);
        labelCell.setPadding(6);
        labelCell.setBorderColor(BORDER_COLOR);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "—", valueFont));
        valueCell.setPadding(6);
        valueCell.setBorderColor(BORDER_COLOR);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(PRIMARY);
        cell.setPadding(6);
        cell.setBorderColor(PRIMARY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void parseMedicationsToTable(PdfPTable table, String medsJson, Font valueFont) {
        // Simple JSON parsing without external library
        String listPart = extractJsonField(medsJson, "listeMedicaments");
        if (listPart.isEmpty() || listPart.equals("[]")) return;

        // Manual parse: split by },{
        String content = medsJson;
        int listStart = content.indexOf("\"listeMedicaments\"");
        if (listStart < 0) return;
        int arrStart = content.indexOf("[", listStart);
        int arrEnd = content.indexOf("]", arrStart);
        if (arrStart < 0 || arrEnd < 0) return;

        String arrContent = content.substring(arrStart + 1, arrEnd);
        String[] items = arrContent.split("\\},\\s*\\{");

        boolean alt = false;
        for (String item : items) {
            item = item.replace("{", "").replace("}", "");
            String nom = extractSimpleField(item, "nom");
            String dosage = extractSimpleField(item, "dosage");
            String moment = extractSimpleField(item, "moment");
            String pris = extractSimpleField(item, "pris");

            Color bg = alt ? LIGHT_BG : Color.WHITE;
            addMedRow(table, nom, dosage, translateValue(moment),
                    "true".equals(pris) ? "✓ Oui" : "✗ Non", valueFont, bg);
            alt = !alt;
        }
    }

    private void addMedRow(PdfPTable table, String nom, String dosage, String moment, String pris, Font font, Color bg) {
        for (String val : new String[]{nom, dosage, moment, pris}) {
            PdfPCell cell = new PdfPCell(new Phrase(val != null ? val : "—", font));
            cell.setPadding(5);
            cell.setBackgroundColor(bg);
            cell.setBorderColor(BORDER_COLOR);
            table.addCell(cell);
        }
    }

    private void parseDirectivesToTable(PdfPTable table, String dirsJson, Font valueFont) {
        if (dirsJson == null || dirsJson.equals("[]")) return;
        String content = dirsJson.substring(1, dirsJson.length() - 1); // remove [ ]
        String[] items = content.split("\\},\\s*\\{");
        boolean alt = false;
        for (String item : items) {
            item = item.replace("{", "").replace("}", "");
            String directiveId = extractSimpleField(item, "directiveId");
            String statut = extractSimpleField(item, "statut");
            String reponse = extractSimpleField(item, "reponse");

            Color bg = alt ? LIGHT_BG : Color.WHITE;
            addDirRow(table, directiveId, translateStatut(statut), reponse != null ? reponse : "", valueFont, bg);
            alt = !alt;
        }
    }

    private void addDirRow(PdfPTable table, String directive, String statut, String comment, Font font, Color bg) {
        for (String val : new String[]{directive, statut, comment}) {
            PdfPCell cell = new PdfPCell(new Phrase(val != null ? val : "—", font));
            cell.setPadding(5);
            cell.setBackgroundColor(bg);
            cell.setBorderColor(BORDER_COLOR);
            table.addCell(cell);
        }
    }

    private String extractJsonField(String json, String field) {
        if (json == null || json.isEmpty()) return "";
        String key = "\"" + field + "\"";
        int idx = json.indexOf(key);
        if (idx < 0) return "";
        int colonIdx = json.indexOf(":", idx + key.length());
        if (colonIdx < 0) return "";
        // Skip whitespace
        int valStart = colonIdx + 1;
        while (valStart < json.length() && json.charAt(valStart) == ' ') valStart++;
        if (valStart >= json.length()) return "";

        char c = json.charAt(valStart);
        if (c == '"') {
            int valEnd = json.indexOf("\"", valStart + 1);
            return valEnd > valStart ? json.substring(valStart + 1, valEnd) : "";
        } else if (c == '[') {
            // Return array as-is
            int depth = 1;
            int i = valStart + 1;
            while (i < json.length() && depth > 0) {
                if (json.charAt(i) == '[') depth++;
                if (json.charAt(i) == ']') depth--;
                i++;
            }
            return json.substring(valStart, i);
        } else {
            // Number or boolean
            int valEnd = valStart;
            while (valEnd < json.length() && json.charAt(valEnd) != ',' && json.charAt(valEnd) != '}' && json.charAt(valEnd) != ']') {
                valEnd++;
            }
            return json.substring(valStart, valEnd).trim();
        }
    }

    private String extractSimpleField(String item, String field) {
        String key = "\"" + field + "\"";
        int idx = item.indexOf(key);
        if (idx < 0) return "";
        int colonIdx = item.indexOf(":", idx + key.length());
        if (colonIdx < 0) return "";
        int valStart = colonIdx + 1;
        while (valStart < item.length() && item.charAt(valStart) == ' ') valStart++;
        if (valStart >= item.length()) return "";

        char c = item.charAt(valStart);
        if (c == '"') {
            int valEnd = item.indexOf("\"", valStart + 1);
            return valEnd > valStart ? item.substring(valStart + 1, valEnd) : "";
        } else {
            int valEnd = valStart;
            while (valEnd < item.length() && item.charAt(valEnd) != ',' && item.charAt(valEnd) != '}') {
                valEnd++;
            }
            return item.substring(valStart, valEnd).trim();
        }
    }

    private String translateValue(String value) {
        if (value == null) return "—";
        switch (value) {
            case "bon": return "Bon";
            case "moyen": return "Moyen";
            case "faible": return "Faible";
            case "refus": return "Refus de manger";
            case "suffisante": return "Suffisante";
            case "insuffisante": return "Insuffisante";
            case "normale": return "Normale";
            case "retrait": return "En retrait";
            case "conflit": return "Conflictuelle";
            case "autonome": return "Autonome";
            case "aide_partielle": return "Aide partielle";
            case "aide_totale": return "Aide totale";
            case "calme": return "Calme";
            case "agité": return "Agité";
            case "insomnie": return "Insomnie";
            case "matin": return "Matin";
            case "midi": return "Midi";
            case "soir": return "Soir";
            case "coucher": return "Coucher";
            default: return value;
        }
    }

    private String translateStatut(String statut) {
        if (statut == null) return "—";
        switch (statut) {
            case "fait": return "✓ Fait";
            case "en_cours": return "⏳ En cours";
            case "non_fait": return "✗ Non fait";
            default: return statut;
        }
    }

    /** Footer handler for page numbers */
    private static class FooterEvent extends PdfPageEventHelper {
        private final FicheTransmission fiche;

        FooterEvent(FicheTransmission fiche) {
            this.fiche = fiche;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Font footerFont = new Font(Font.HELVETICA, 7, Font.NORMAL, new Color(150, 150, 150));
            String ref = "FT-" + String.format("%05d", fiche.getId() != null ? fiche.getId() : 0);
            Phrase left = new Phrase("AXE Alzheimer e-Health — " + ref, footerFont);
            Phrase right = new Phrase("Page " + writer.getPageNumber(), footerFont);

            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, left,
                    document.leftMargin(), document.bottomMargin() - 10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, right,
                    document.right(), document.bottomMargin() - 10, 0);
        }
    }
}
