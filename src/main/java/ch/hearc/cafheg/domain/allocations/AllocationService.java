package ch.hearc.cafheg.domain.allocations;

import ch.hearc.cafheg.infrastructure.persistence.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistence.AllocationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class AllocationService {

    private static final Logger log = LoggerFactory.getLogger(AllocationService.class);

    private static final String PARENT_1 = "Parent1";
    private static final String PARENT_2 = "Parent2";

    private final AllocataireMapper allocataireMapper;
    private final AllocationMapper allocationMapper;

    public AllocationService(
            AllocataireMapper allocataireMapper,
            AllocationMapper allocationMapper) {
        this.allocataireMapper = allocataireMapper;
        this.allocationMapper = allocationMapper;
    }

    public List<Allocataire> findAllAllocataires(String likeNom) {
        log.info("Rechercher tous les allocataires");
        return allocataireMapper.findAll(likeNom);
    }

    public List<Allocation> findAllocationsActuelles() {
        return allocationMapper.findAll();
    }

    public void deleteAllocataire(String noAVS) {
        if (allocataireMapper.findByNoAVS(noAVS) == null) {
            throw new IllegalArgumentException("Allocataire non trouvé");
        }
        if (allocataireMapper.hasVersements(noAVS)) {
            throw new IllegalStateException("Impossible de supprimer un allocataire qui possède des versements");
        }
        allocataireMapper.deleteByNoAVS(noAVS);
    }

    public void updateAllocataire(String noAVS, String nom, String prenom) {
        Allocataire existing = allocataireMapper.findByNoAVS(noAVS);
        if (existing == null) {
            throw new IllegalArgumentException("Allocataire non trouvé");
        }
        if (existing.getNom().equals(nom) && existing.getPrenom().equals(prenom)) {
            throw new IllegalArgumentException("Aucune modification détectée");
        }
        allocataireMapper.updateAllocataire(noAVS, nom, prenom);
    }

    /**
     * Détermine quel parent a le droit aux allocations familiales
     * selon le schéma de la LAFam (art. 7 OAFam).
     *
     * Arbre de décision :
     * 1. Un seul parent avec activité lucrative -> ce parent a droit (cas a)
     * 2. Deux parents avec activité lucrative :
     *    a. Un seul parent avec autorité parentale -> ce parent a droit (cas b)
     *    b. Deux parents avec autorité parentale :
     *       - Parents séparés -> parent qui vit avec l'enfant (cas c)
     *         Si aucun ne vit avec l'enfant -> parent qui travaille dans le
     *         canton de domicile de l'enfant (cas d)
     *       - Parents ensemble :
     *         * Un salarié + un indépendant (ou les deux salariés) ->
     *           le salarié, ou si les deux sont salariés celui avec le
     *           revenu le plus élevé (cas e)
     *         * Deux indépendants -> celui avec le revenu le plus élevé (cas f)
     */
    public String getParentDroitAllocation(ParentDroitAllocationRequest request) {
        log.info("Déterminer quel parent a le droit aux allocations");

        // Cas a : un seul parent exerce une activité lucrative
        if (request.isParent1ActiviteLucrative() && !request.isParent2ActiviteLucrative()) {
            return PARENT_1;
        }
        if (request.isParent2ActiviteLucrative() && !request.isParent1ActiviteLucrative()) {
            return PARENT_2;
        }

        // Aucun parent avec activité lucrative : pas de droit clair, on retourne Parent2 par défaut
        if (!request.isParent1ActiviteLucrative() && !request.isParent2ActiviteLucrative()) {
            return PARENT_2;
        }

        // Deux parents avec activité lucrative
        // Cas b : un seul parent a l'autorité parentale
        if (request.isParent1AutoriteParentale() && !request.isParent2AutoriteParentale()) {
            return PARENT_1;
        }
        if (request.isParent2AutoriteParentale() && !request.isParent1AutoriteParentale()) {
            return PARENT_2;
        }

        // Deux parents avec autorité parentale
        if (!request.isParentsEnsemble()) {
            // Parents séparés
            // Cas c : le parent qui vit avec l'enfant
            String enfantResidence = request.getEnfantResidence();

            boolean parent1VitAvecEnfant = enfantResidence != null
                    && enfantResidence.equals(request.getParent1Residence());
            boolean parent2VitAvecEnfant = enfantResidence != null
                    && enfantResidence.equals(request.getParent2Residence());

            if (parent1VitAvecEnfant && !parent2VitAvecEnfant) {
                return PARENT_1;
            }
            if (parent2VitAvecEnfant && !parent1VitAvecEnfant) {
                return PARENT_2;
            }

            // Cas d : aucun ne vit avec l'enfant (ou les deux y vivent)
            // -> celui qui travaille dans le canton de domicile de l'enfant
            boolean parent1TravailleDansCantonEnfant = enfantResidence != null
                    && enfantResidence.equals(request.getParent1Residence());
            boolean parent2TravailleDansCantonEnfant = enfantResidence != null
                    && enfantResidence.equals(request.getParent2Residence());

            if (parent1TravailleDansCantonEnfant && !parent2TravailleDansCantonEnfant) {
                return PARENT_1;
            }
            if (parent2TravailleDansCantonEnfant && !parent1TravailleDansCantonEnfant) {
                return PARENT_2;
            }

            // En dernier recours, celui avec le salaire le plus élevé
            return request.getParent1Salaire().compareTo(request.getParent2Salaire()) > 0
                    ? PARENT_1 : PARENT_2;
        }

        // Parents vivent ensemble
        // Cas e : un salarié et un indépendant -> le salarié a droit
        boolean parent1Salarie = !request.isParent1Independant();
        boolean parent2Salarie = !request.isParent2Independant();

        if (parent1Salarie && !parent2Salarie) {
            return PARENT_1;
        }
        if (parent2Salarie && !parent1Salarie) {
            return PARENT_2;
        }

        // Cas e (suite) : deux salariés -> celui avec le revenu le plus élevé
        // Cas f : deux indépendants -> celui avec le revenu le plus élevé
        return request.getParent1Salaire().compareTo(request.getParent2Salaire()) > 0
                ? PARENT_1 : PARENT_2;
    }
}
