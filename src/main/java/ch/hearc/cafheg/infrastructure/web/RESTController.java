package ch.hearc.cafheg.infrastructure.web;

import ch.hearc.cafheg.domain.allocations.Allocataire;
import ch.hearc.cafheg.domain.allocations.Allocation;
import ch.hearc.cafheg.domain.allocations.AllocationService;
import ch.hearc.cafheg.domain.allocations.ParentDroitAllocationRequest;
import ch.hearc.cafheg.domain.versements.VersementService;
import ch.hearc.cafheg.infrastructure.pdf.PDFExporter;
import ch.hearc.cafheg.infrastructure.persistence.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistence.AllocationMapper;
import ch.hearc.cafheg.infrastructure.persistence.EnfantMapper;
import ch.hearc.cafheg.infrastructure.persistence.VersementMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static ch.hearc.cafheg.infrastructure.persistence.Database.inTransaction;

@RestController
@Tag(name = "CAFHEG API")
public class RESTController {

    private final AllocationService allocationService;
    private final VersementService versementService;

    public RESTController() {
        this.allocationService = new AllocationService(new AllocataireMapper(), new AllocationMapper());
        this.versementService = new VersementService(new VersementMapper(), new AllocataireMapper(),
                new PDFExporter(new EnfantMapper())
        );
    }

    /*
    // Headers de la requête HTTP doit contenir "Content-Type: application/json"
    // BODY de la requête HTTP à transmettre afin de tester le endpoint
    {
        "enfantResidence" : "Neuchâtel",
        "parent1Residence" : "Neuchâtel",
        "parent2Residence" : "Bienne",
        "parent1ActiviteLucrative" : true,
        "parent2ActiviteLucrative" : true,
        "parent1Salaire" : 2500,
        "parent2Salaire" : 3000
    }
     */
    @PostMapping("/droits/quel-parent")
    public String getParentDroitAllocation(@RequestBody ParentDroitAllocationRequest request) {
        return inTransaction(() -> allocationService.getParentDroitAllocation(request));
    }

    @GetMapping("/allocataires")
    public List<Allocataire> allocataires(
            @RequestParam(value = "startsWith", required = false) String start
    ) {
        return inTransaction(() -> allocationService.findAllAllocataires(start));
    }

        @DeleteMapping("/allocataires/{noAVS}")
    public ResponseEntity<String> deleteAllocataire(@PathVariable("noAVS") String noAVS) {
        try {
            inTransaction(() -> { allocationService.deleteAllocataire(noAVS); return null; });
            return ResponseEntity.ok("Allocataire supprimé");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/allocataires/{noAVS}")
    public ResponseEntity<String> updateAllocataire(
            @PathVariable("noAVS") String noAVS,
            @RequestBody java.util.Map<String, String> body) {
        try {
            String nom = body.get("nom");
            String prenom = body.get("prenom");
            inTransaction(() -> { allocationService.updateAllocataire(noAVS, nom, prenom); return null; });
            return ResponseEntity.ok("Allocataire modifié");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/allocations")
    public List<Allocation> allocations() {
        return inTransaction(allocationService::findAllocationsActuelles);
    }

    @GetMapping("/allocations/{year}/somme")
    public BigDecimal sommeAs(@PathVariable("year") int year) {
        return inTransaction(() -> versementService.findSommeAllocationParAnnee(year).getValue());
    }

    @GetMapping("/allocations-naissances/{year}/somme")
    public BigDecimal sommeAns(@PathVariable("year") int year) {
        return inTransaction(
                () -> versementService.findSommeAllocationNaissanceParAnnee(year).getValue());
    }

    @GetMapping(value = "/allocataires/{allocataireId}/allocations", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdfAllocations(@PathVariable("allocataireId") int allocataireId) {
        byte[] pdf = inTransaction(() -> versementService.exportPDFAllocataire(allocataireId));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"allocations_" + allocataireId + ".pdf\"");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping(value = "/allocataires/{allocataireId}/versements", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdfVersements(@PathVariable("allocataireId") int allocataireId) {
        byte[] pdf = inTransaction(() -> versementService.exportPDFVersements(allocataireId));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"versements_" + allocataireId + ".pdf\"");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
