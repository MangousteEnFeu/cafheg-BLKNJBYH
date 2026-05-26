package ch.hearc.cafheg.domain.allocations;

import ch.hearc.cafheg.domain.common.Montant;
import ch.hearc.cafheg.infrastructure.persistence.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistence.AllocationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AllocationServiceTest {

    private AllocationService allocationService;

    private AllocataireMapper allocataireMapper;
    private AllocationMapper allocationMapper;

    @BeforeEach
    void setUp() {
        allocataireMapper = Mockito.mock(AllocataireMapper.class);
        allocationMapper = Mockito.mock(AllocationMapper.class);

        allocationService = new AllocationService(allocataireMapper, allocationMapper);
    }

    @Test
    void findAllAllocataires_GivenEmptyAllocataires_ShouldBeEmpty() {
        Mockito.when(allocataireMapper.findAll("Geiser")).thenReturn(Collections.emptyList());
        List<Allocataire> all = allocationService.findAllAllocataires("Geiser");
        assertThat(all).isEmpty();
    }

    @Test
    void findAllAllocataires_Given2Geiser_ShouldBe2() {
        Mockito.when(allocataireMapper.findAll("Geiser"))
                .thenReturn(Arrays.asList(new Allocataire(new NoAVS("1000-2000"), "Geiser", "Arnaud"),
                        new Allocataire(new NoAVS("1000-2001"), "Geiser", "Aurélie")));
        List<Allocataire> all = allocationService.findAllAllocataires("Geiser");
        assertAll(() -> assertThat(all.size()).isEqualTo(2),
                () -> assertThat(all.get(0).getNoAVS()).isEqualTo(new NoAVS("1000-2000")),
                () -> assertThat(all.get(0).getNom()).isEqualTo("Geiser"),
                () -> assertThat(all.get(0).getPrenom()).isEqualTo("Arnaud"),
                () -> assertThat(all.get(1).getNoAVS()).isEqualTo(new NoAVS("1000-2001")),
                () -> assertThat(all.get(1).getNom()).isEqualTo("Geiser"),
                () -> assertThat(all.get(1).getPrenom()).isEqualTo("Aurélie"));
    }

    @Test
    void findAllocationsActuelles() {
        Mockito.when(allocationMapper.findAll())
                .thenReturn(Arrays.asList(new Allocation(new Montant(new BigDecimal(1000)), Canton.NE,
                        LocalDate.now(), null), new Allocation(new Montant(new BigDecimal(2000)), Canton.FR,
                        LocalDate.now(), null)));
        List<Allocation> all = allocationService.findAllocationsActuelles();
        assertAll(() -> assertThat(all.size()).isEqualTo(2),
                () -> assertThat(all.get(0).getMontant()).isEqualTo(new Montant(new BigDecimal(1000))),
                () -> assertThat(all.get(0).getCanton()).isEqualTo(Canton.NE),
                () -> assertThat(all.get(0).getDebut()).isEqualTo(LocalDate.now()),
                () -> assertThat(all.get(0).getFin()).isNull(),
                () -> assertThat(all.get(1).getMontant()).isEqualTo(new Montant(new BigDecimal(2000))),
                () -> assertThat(all.get(1).getCanton()).isEqualTo(Canton.FR),
                () -> assertThat(all.get(1).getDebut()).isEqualTo(LocalDate.now()),
                () -> assertThat(all.get(1).getFin()).isNull());
    }

    // ================================================================
    // Tests Exercice 2 : Suppression
    // ================================================================

    @Test
    void deleteAllocataire_whenNoVersements_shouldDelete() {
        Mockito.when(allocataireMapper.findByNoAVS("1000-2000"))
                .thenReturn(new Allocataire(new NoAVS("1000-2000"), "Geiser", "Arnaud"));
        Mockito.when(allocataireMapper.hasVersements("1000-2000")).thenReturn(false);
        allocationService.deleteAllocataire("1000-2000");
        Mockito.verify(allocataireMapper).deleteByNoAVS("1000-2000");
    }

    @Test
    void deleteAllocataire_whenHasVersements_shouldThrow() {
        Mockito.when(allocataireMapper.findByNoAVS("1000-2000"))
                .thenReturn(new Allocataire(new NoAVS("1000-2000"), "Geiser", "Arnaud"));
        Mockito.when(allocataireMapper.hasVersements("1000-2000")).thenReturn(true);
        assertThat(assertThrows(
                IllegalStateException.class,
                () -> allocationService.deleteAllocataire("1000-2000")
        ).getMessage()).contains("versements");
    }

    @Test
    void deleteAllocataire_whenNotFound_shouldThrow() {
        Mockito.when(allocataireMapper.findByNoAVS("9999-9999")).thenReturn(null);
        assertThrows(
                IllegalArgumentException.class,
                () -> allocationService.deleteAllocataire("9999-9999")
        );
    }

    // ================================================================
    // Tests Exercice 2 : Modification
    // ================================================================

    @Test
    void updateAllocataire_whenNameChanged_shouldUpdate() {
        Mockito.when(allocataireMapper.findByNoAVS("1000-2000"))
                .thenReturn(new Allocataire(new NoAVS("1000-2000"), "Geiser", "Arnaud"));
        allocationService.updateAllocataire("1000-2000", "Müller", "Arnaud");
        Mockito.verify(allocataireMapper).updateAllocataire("1000-2000", "Müller", "Arnaud");
    }

    @Test
    void updateAllocataire_whenPrenomChanged_shouldUpdate() {
        Mockito.when(allocataireMapper.findByNoAVS("1000-2000"))
                .thenReturn(new Allocataire(new NoAVS("1000-2000"), "Geiser", "Arnaud"));
        allocationService.updateAllocataire("1000-2000", "Geiser", "Pierre");
        Mockito.verify(allocataireMapper).updateAllocataire("1000-2000", "Geiser", "Pierre");
    }

    @Test
    void updateAllocataire_whenNothingChanged_shouldThrow() {
        Mockito.when(allocataireMapper.findByNoAVS("1000-2000"))
                .thenReturn(new Allocataire(new NoAVS("1000-2000"), "Geiser", "Arnaud"));
        assertThat(assertThrows(
                IllegalArgumentException.class,
                () -> allocationService.updateAllocataire("1000-2000", "Geiser", "Arnaud")
        ).getMessage()).contains("modification");
    }

    @Test
    void updateAllocataire_whenNotFound_shouldThrow() {
        Mockito.when(allocataireMapper.findByNoAVS("9999-9999")).thenReturn(null);
        assertThrows(
                IllegalArgumentException.class,
                () -> allocationService.updateAllocataire("9999-9999", "Nom", "Prenom")
        );
    }

    // ================================================================
    // Tests Exercice 1 : getParentDroitAllocation (schéma LAFam)
    // ================================================================

    // --- Cas a : un seul parent avec activité lucrative ---

    @Test
    void getParentDroitAllocation_casA_seulParent1Actif_retourneParent1() {
        ParentDroitAllocationRequest req = buildRequest(true, false);
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent1");
    }

    @Test
    void getParentDroitAllocation_casA_seulParent2Actif_retourneParent2() {
        ParentDroitAllocationRequest req = buildRequest(false, true);
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent2");
    }

    // --- Aucun parent actif ---

    @Test
    void getParentDroitAllocation_aucunParentActif_retourneParent2ParDefaut() {
        ParentDroitAllocationRequest req = buildRequest(false, false);
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent2");
    }

    // --- Cas b : deux parents actifs, un seul avec autorité parentale ---

    @Test
    void getParentDroitAllocation_casB_seulParent1AvecAutorite_retourneParent1() {
        ParentDroitAllocationRequest req = buildRequest(true, true);
        req.setParent1AutoriteParentale(true);
        req.setParent2AutoriteParentale(false);
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent1");
    }

    @Test
    void getParentDroitAllocation_casB_seulParent2AvecAutorite_retourneParent2() {
        ParentDroitAllocationRequest req = buildRequest(true, true);
        req.setParent1AutoriteParentale(false);
        req.setParent2AutoriteParentale(true);
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent2");
    }

    // --- Cas c : deux parents actifs, deux avec autorité, séparés,
    //             parent qui vit avec l'enfant ---

    @Test
    void getParentDroitAllocation_casC_separes_parent1VitAvecEnfant_retourneParent1() {
        ParentDroitAllocationRequest req = buildRequestSepares("Neuchâtel", "Neuchâtel", "Bienne");
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent1");
    }

    @Test
    void getParentDroitAllocation_casC_separes_parent2VitAvecEnfant_retourneParent2() {
        ParentDroitAllocationRequest req = buildRequestSepares("Bienne", "Neuchâtel", "Bienne");
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent2");
    }

    // --- Cas d : deux parents actifs, séparés, aucun ne vit avec l'enfant,
    //             celui qui travaille dans le canton de l'enfant ---

    @Test
    void getParentDroitAllocation_casD_separes_aucunVitAvecEnfant_parent1DansCantonEnfant() {
        ParentDroitAllocationRequest req = buildRequestSepares("Lausanne", "Lausanne", "Genève");
        // Parent1 résidence = Lausanne = enfant -> parent1 travaille dans canton enfant
        // Mais ici parent1 vit aussi avec l'enfant... on a besoin d'un cas où aucun ne vit avec
        // Pour un vrai cas d : les deux vivent ailleurs que l'enfant
        req.setEnfantResidence("Fribourg");
        req.setParent1Residence("Fribourg"); // parent1 travaille dans le canton de l'enfant
        req.setParent2Residence("Genève");
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent1");
    }

    @Test
    void getParentDroitAllocation_casD_separes_parent2TravailleDansCantonEnfant() {
        ParentDroitAllocationRequest req = buildRequestSepares("Zurich", "Berne", "Zurich");
        // enfant vit à Zurich, parent1 à Berne, parent2 à Zurich
        // parent2 vit avec l'enfant -> cas c, pas d
        // Changeons : enfant à Fribourg, aucun n'y vit, mais parent2 y travaille
        req.setEnfantResidence("Fribourg");
        req.setParent1Residence("Berne");
        req.setParent2Residence("Fribourg");
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent2");
    }

    @Test
    void getParentDroitAllocation_separes_aucunDansCantonEnfant_salairePlusEleve() {
        ParentDroitAllocationRequest req = buildRequestSepares("Fribourg", "Berne", "Genève");
        // Aucun ne vit à Fribourg ni ne travaille à Fribourg
        req.setParent1Salaire(BigDecimal.valueOf(5000));
        req.setParent2Salaire(BigDecimal.valueOf(3000));
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent1");
    }

    // --- Cas e : parents ensemble, un salarié et un indépendant -> le salarié ---

    @Test
    void getParentDroitAllocation_casE_ensemble_parent1SalarieParent2Independant_retourneParent1() {
        ParentDroitAllocationRequest req = buildRequestEnsemble(false, true);
        req.setParent1Salaire(BigDecimal.valueOf(3000));
        req.setParent2Salaire(BigDecimal.valueOf(5000));
        // Même si parent2 gagne plus, parent1 est salarié donc il a droit
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent1");
    }

    @Test
    void getParentDroitAllocation_casE_ensemble_parent2SalarieParent1Independant_retourneParent2() {
        ParentDroitAllocationRequest req = buildRequestEnsemble(true, false);
        req.setParent1Salaire(BigDecimal.valueOf(5000));
        req.setParent2Salaire(BigDecimal.valueOf(3000));
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent2");
    }

    // --- Cas e (suite) : parents ensemble, deux salariés -> revenu le plus élevé ---

    @Test
    void getParentDroitAllocation_casE_ensemble_deuxSalaries_parent1GagnePlus_retourneParent1() {
        ParentDroitAllocationRequest req = buildRequestEnsemble(false, false);
        req.setParent1Salaire(BigDecimal.valueOf(5000));
        req.setParent2Salaire(BigDecimal.valueOf(3000));
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent1");
    }

    @Test
    void getParentDroitAllocation_casE_ensemble_deuxSalaries_parent2GagnePlus_retourneParent2() {
        ParentDroitAllocationRequest req = buildRequestEnsemble(false, false);
        req.setParent1Salaire(BigDecimal.valueOf(3000));
        req.setParent2Salaire(BigDecimal.valueOf(5000));
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent2");
    }

    // --- Cas f : parents ensemble, deux indépendants -> revenu le plus élevé ---

    @Test
    void getParentDroitAllocation_casF_ensemble_deuxIndependants_parent1GagnePlus_retourneParent1() {
        ParentDroitAllocationRequest req = buildRequestEnsemble(true, true);
        req.setParent1Salaire(BigDecimal.valueOf(6000));
        req.setParent2Salaire(BigDecimal.valueOf(4000));
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent1");
    }

    @Test
    void getParentDroitAllocation_casF_ensemble_deuxIndependants_parent2GagnePlus_retourneParent2() {
        ParentDroitAllocationRequest req = buildRequestEnsemble(true, true);
        req.setParent1Salaire(BigDecimal.valueOf(3000));
        req.setParent2Salaire(BigDecimal.valueOf(7000));
        assertThat(allocationService.getParentDroitAllocation(req)).isEqualTo("Parent2");
    }

    // ================================================================
    // Méthodes utilitaires pour construire les requêtes de test
    // ================================================================

    private ParentDroitAllocationRequest buildRequest(boolean p1Actif, boolean p2Actif) {
        ParentDroitAllocationRequest req = new ParentDroitAllocationRequest();
        req.setParent1ActiviteLucrative(p1Actif);
        req.setParent2ActiviteLucrative(p2Actif);
        req.setParent1AutoriteParentale(true);
        req.setParent2AutoriteParentale(true);
        req.setParentsEnsemble(true);
        req.setParent1Salaire(BigDecimal.ZERO);
        req.setParent2Salaire(BigDecimal.ZERO);
        return req;
    }

    private ParentDroitAllocationRequest buildRequestSepares(
            String enfantRes, String parent1Res, String parent2Res) {
        ParentDroitAllocationRequest req = new ParentDroitAllocationRequest();
        req.setParent1ActiviteLucrative(true);
        req.setParent2ActiviteLucrative(true);
        req.setParent1AutoriteParentale(true);
        req.setParent2AutoriteParentale(true);
        req.setParentsEnsemble(false);
        req.setEnfantResidence(enfantRes);
        req.setParent1Residence(parent1Res);
        req.setParent2Residence(parent2Res);
        req.setParent1Salaire(BigDecimal.valueOf(3000));
        req.setParent2Salaire(BigDecimal.valueOf(3000));
        return req;
    }

    private ParentDroitAllocationRequest buildRequestEnsemble(
            boolean parent1Independant, boolean parent2Independant) {
        ParentDroitAllocationRequest req = new ParentDroitAllocationRequest();
        req.setParent1ActiviteLucrative(true);
        req.setParent2ActiviteLucrative(true);
        req.setParent1AutoriteParentale(true);
        req.setParent2AutoriteParentale(true);
        req.setParentsEnsemble(true);
        req.setParent1Independant(parent1Independant);
        req.setParent2Independant(parent2Independant);
        req.setParent1Salaire(BigDecimal.valueOf(3000));
        req.setParent2Salaire(BigDecimal.valueOf(3000));
        return req;
    }
}
