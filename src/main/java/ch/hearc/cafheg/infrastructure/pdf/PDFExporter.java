package ch.hearc.cafheg.infrastructure.pdf;

import ch.hearc.cafheg.domain.allocations.Allocataire;
import ch.hearc.cafheg.domain.versements.Enfant;
import ch.hearc.cafheg.domain.common.Montant;
import ch.hearc.cafheg.infrastructure.persistence.EnfantMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFExporter {

    private static final Logger log = LoggerFactory.getLogger(PDFExporter.class);

    private final EnfantMapper enfantMapper;
    private static final PDType1Font DEFAULT_FONT;

    static {
        DEFAULT_FONT = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
    }

    public PDFExporter(EnfantMapper enfantMapper) {
        this.enfantMapper = enfantMapper;
    }

    public byte[] generatePDFVersement(Allocataire allocataire,
            Map<LocalDate, Montant> montantParMois) {
        log.info("Génération du PDF des versements");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(25, 500);
                    contentStream.setFont(DEFAULT_FONT, 12);
                    contentStream.showText(
                            "Les versement suivants ont été fait à l'allocataire : " + allocataire.getNom() + " "
                                    + allocataire.getPrenom() + " ("
                                    + allocataire.getNoAVS().getValue() + ")");
                    contentStream.endText();

                    int i = 0;
                    for (Map.Entry<LocalDate, Montant> entry : montantParMois.entrySet()) {
                        LocalDate dv = entry.getKey();
                        contentStream.beginText();
                        contentStream.newLineAtOffset(25, 450 - (i * 24));
                        contentStream.setFont(DEFAULT_FONT, 12);
                        contentStream.showText(dv.toString());
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.newLineAtOffset(300, 450 - (i * 24));
                        contentStream.setFont(DEFAULT_FONT, 12);
                        contentStream.showText(entry.getValue().getValue() + " CHF");
                        contentStream.endText();
                        i++;
                    }
                }

                document.save(baos);
            }
            log.info("PDF des versements généré");
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Erreur lors de la génération du PDF des versements", e);
            throw new RuntimeException(e);
        }
    }

    public byte[] generatePDFAllocataire(Allocataire allocataire,
            Map<Long, Montant> montantsParEnfant) {
        log.info("Génération du PDF pour un allocataire");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(25, 500);
                    contentStream.setFont(DEFAULT_FONT, 12);
                    contentStream.showText(
                            "L'allocataire " + allocataire.getNom() + " " + allocataire.getPrenom() + " ("
                                    + allocataire.getNoAVS().getValue()
                                    + ") possèdent des droits d'allocations pour " + montantsParEnfant.size()
                                    + " enfant(s) : ");
                    contentStream.endText();

                    int i = 0;
                    for (Map.Entry<Long, Montant> entry : montantsParEnfant.entrySet()) {
                        long eId = entry.getKey();
                        Enfant enfant = enfantMapper.findById(eId);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(25, 450 - (i * 24));
                        contentStream.setFont(DEFAULT_FONT, 12);
                        contentStream.showText(
                                enfant.getNom() + " " + enfant.getPrenom() + " (" + enfant.getNoAVS().getValue() + ")");
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.newLineAtOffset(300, 450 - (i * 24));
                        contentStream.setFont(DEFAULT_FONT, 12);
                        contentStream.showText(entry.getValue().getValue().toString() + " CHF");
                        contentStream.endText();
                        i++;
                    }
                }

                document.save(baos);
            }
            log.info("PDF allocataire généré");
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Erreur lors de la génération du PDF allocataire", e);
            throw new RuntimeException(e);
        }
    }
}
