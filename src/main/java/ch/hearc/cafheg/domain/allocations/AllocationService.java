package ch.hearc.cafheg.domain.allocations;

import ch.hearc.cafheg.infrastructure.persistence.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistence.AllocationMapper;

import java.util.List;


public class AllocationService {

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
        System.out.println("Rechercher tous les allocataires");
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

    public String getParentDroitAllocation(ParentDroitAllocationRequest request) {
        System.out.println("Déterminer quel parent a le droit aux allocations");

        if (request.isParent1ActiviteLucrative() && !request.isParent2ActiviteLucrative()) {
            return PARENT_1;
        }
        if (request.isParent2ActiviteLucrative() && !request.isParent1ActiviteLucrative()) {
            return PARENT_2;
        }
        return request.getParent1Salaire().compareTo(request.getParent2Salaire()) > 0 ? PARENT_1 : PARENT_2;
    }
}
