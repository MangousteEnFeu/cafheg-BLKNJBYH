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

    public String getEnfantResidence() {
        return enfantResidence;
    }

    public void setEnfantResidence(String v) {
        this.enfantResidence = v;
    }

    public boolean isParent1ActiviteLucrative() {
        return parent1ActiviteLucrative;
    }

    public void setParent1ActiviteLucrative(boolean v) {
        this.parent1ActiviteLucrative = v;
    }

    public String getParent1Residence() {
        return parent1Residence;
    }

    public void setParent1Residence(String v) {
        this.parent1Residence = v;
    }

    public boolean isParent2ActiviteLucrative() {
        return parent2ActiviteLucrative;
    }

    public void setParent2ActiviteLucrative(boolean v) {
        this.parent2ActiviteLucrative = v;
    }

    public String getParent2Residence() {
        return parent2Residence;
    }

    public void setParent2Residence(String v) {
        this.parent2Residence = v;
    }

    public boolean isParentsEnsemble() {
        return parentsEnsemble;
    }

    public void setParentsEnsemble(boolean v) {
        this.parentsEnsemble = v;
    }

    public BigDecimal getParent1Salaire() {
        return parent1Salaire;
    }

    public void setParent1Salaire(BigDecimal v) {
        this.parent1Salaire = v;
    }

    public BigDecimal getParent2Salaire() {
        return parent2Salaire;
    }

    public void setParent2Salaire(BigDecimal v) {
        this.parent2Salaire = v;
    }
}