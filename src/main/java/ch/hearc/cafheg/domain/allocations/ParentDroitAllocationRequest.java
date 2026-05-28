package ch.hearc.cafheg.domain.allocations;

import java.math.BigDecimal;

public class ParentDroitAllocationRequest {
    private String enfantResidence;
    private boolean parent1ActiviteLucrative;
    private String parent1Residence;
    private boolean parent2ActiviteLucrative;
    private String parent2Residence;
    private boolean parentsEnsemble;
    private BigDecimal parent1Salaire;
    private BigDecimal parent2Salaire;
    private boolean parent1AutoriteParentale;
    private boolean parent2AutoriteParentale;
    private boolean parent1Independant;
    private boolean parent2Independant;
    private String parent1CantonTravail;
    private String parent2CantonTravail;

    public String getEnfantResidence() {
        return enfantResidence;
    }

    public void setEnfantResidence(String enfantResidence) {
        this.enfantResidence = enfantResidence;
    }

    public boolean isParent1ActiviteLucrative() {
        return parent1ActiviteLucrative;
    }

    public void setParent1ActiviteLucrative(boolean parent1ActiviteLucrative) {
        this.parent1ActiviteLucrative = parent1ActiviteLucrative;
    }

    public String getParent1Residence() {
        return parent1Residence;
    }

    public void setParent1Residence(String parent1Residence) {
        this.parent1Residence = parent1Residence;
    }

    public boolean isParent2ActiviteLucrative() {
        return parent2ActiviteLucrative;
    }

    public void setParent2ActiviteLucrative(boolean parent2ActiviteLucrative) {
        this.parent2ActiviteLucrative = parent2ActiviteLucrative;
    }

    public String getParent2Residence() {
        return parent2Residence;
    }

    public void setParent2Residence(String parent2Residence) {
        this.parent2Residence = parent2Residence;
    }

    public boolean isParentsEnsemble() {
        return parentsEnsemble;
    }

    public void setParentsEnsemble(boolean parentsEnsemble) {
        this.parentsEnsemble = parentsEnsemble;
    }

    public BigDecimal getParent1Salaire() {
        return parent1Salaire;
    }

    public void setParent1Salaire(BigDecimal parent1Salaire) {
        this.parent1Salaire = parent1Salaire;
    }

    public BigDecimal getParent2Salaire() {
        return parent2Salaire;
    }

    public void setParent2Salaire(BigDecimal parent2Salaire) {
        this.parent2Salaire = parent2Salaire;
    }

    public boolean isParent1AutoriteParentale() {
        return parent1AutoriteParentale;
    }

    public void setParent1AutoriteParentale(boolean parent1AutoriteParentale) {
        this.parent1AutoriteParentale = parent1AutoriteParentale;
    }

    public boolean isParent2AutoriteParentale() {
        return parent2AutoriteParentale;
    }

    public void setParent2AutoriteParentale(boolean parent2AutoriteParentale) {
        this.parent2AutoriteParentale = parent2AutoriteParentale;
    }

    public boolean isParent1Independant() {
        return parent1Independant;
    }

    public void setParent1Independant(boolean parent1Independant) {
        this.parent1Independant = parent1Independant;
    }

    public boolean isParent2Independant() {
        return parent2Independant;
    }

    public void setParent2Independant(boolean parent2Independant) {
        this.parent2Independant = parent2Independant;
    }

    public String getParent1CantonTravail() {
        return parent1CantonTravail;
    }

    public void setParent1CantonTravail(String parent1CantonTravail) {
        this.parent1CantonTravail = parent1CantonTravail;
    }

    public String getParent2CantonTravail() {
        return parent2CantonTravail;
    }

    public void setParent2CantonTravail(String parent2CantonTravail) {
        this.parent2CantonTravail = parent2CantonTravail;
    }
}
