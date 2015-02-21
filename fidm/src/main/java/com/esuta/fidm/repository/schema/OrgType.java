package com.esuta.fidm.repository.schema;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
@Entity
public class OrgType extends ObjectType{

    @Index
    private String displayName;
    private List<String> orgType;

    @Index
    private String locality;

    private List<String> parentOrgUnits;
    private List<String> governors;

    private List<String> resourceInducements;
    private List<String> roleInducements;

    private boolean federationAvailable;
    private boolean federationIdentifier;

    public OrgType(){}

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getOrgType() {
        if(orgType == null){
            orgType = new ArrayList<>();
        }

        return orgType;
    }

    public void setOrgType(List<String> orgType) {
        this.orgType = orgType;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public List<String> getParentOrgUnits() {
        if(parentOrgUnits == null){
            parentOrgUnits = new ArrayList<>();
        }

        return parentOrgUnits;
    }

    public void setParentOrgUnits(List<String> parentOrgUnits) {
        this.parentOrgUnits = parentOrgUnits;
    }

    public List<String> getGovernors() {
        if(governors == null){
            governors = new ArrayList<>();
        }

        return governors;
    }

    public void setGovernors(List<String> governors) {
        this.governors = governors;
    }

    public List<String> getResourceInducements() {
        if(resourceInducements == null){
            resourceInducements = new ArrayList<>();
        }

        return resourceInducements;
    }

    public void setResourceInducements(List<String> resourceInducements) {
        this.resourceInducements = resourceInducements;
    }

    public List<String> getRoleInducements() {
        if(roleInducements == null){
            roleInducements = new ArrayList<>();
        }

        return roleInducements;
    }

    public void setRoleInducements(List<String> roleInducements) {
        this.roleInducements = roleInducements;
    }

    public boolean isFederationAvailable() {
        return federationAvailable;
    }

    public void setFederationAvailable(boolean federationAvailable) {
        this.federationAvailable = federationAvailable;
    }

    public boolean isFederationIdentifier() {
        return federationIdentifier;
    }

    public void setFederationIdentifier(boolean federationIdentifier) {
        this.federationIdentifier = federationIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OrgType orgType1 = (OrgType) o;

        if (federationAvailable != orgType1.federationAvailable) return false;
        if (federationIdentifier != orgType1.federationIdentifier) return false;
        if (displayName != null ? !displayName.equals(orgType1.displayName) : orgType1.displayName != null)
            return false;
        if (governors != null ? !governors.equals(orgType1.governors) : orgType1.governors != null) return false;
        if (locality != null ? !locality.equals(orgType1.locality) : orgType1.locality != null) return false;
        if (orgType != null ? !orgType.equals(orgType1.orgType) : orgType1.orgType != null) return false;
        if (parentOrgUnits != null ? !parentOrgUnits.equals(orgType1.parentOrgUnits) : orgType1.parentOrgUnits != null)
            return false;
        if (resourceInducements != null ? !resourceInducements.equals(orgType1.resourceInducements) : orgType1.resourceInducements != null)
            return false;
        if (roleInducements != null ? !roleInducements.equals(orgType1.roleInducements) : orgType1.roleInducements != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (orgType != null ? orgType.hashCode() : 0);
        result = 31 * result + (locality != null ? locality.hashCode() : 0);
        result = 31 * result + (parentOrgUnits != null ? parentOrgUnits.hashCode() : 0);
        result = 31 * result + (governors != null ? governors.hashCode() : 0);
        result = 31 * result + (resourceInducements != null ? resourceInducements.hashCode() : 0);
        result = 31 * result + (roleInducements != null ? roleInducements.hashCode() : 0);
        result = 31 * result + (federationAvailable ? 1 : 0);
        result = 31 * result + (federationIdentifier ? 1 : 0);
        return result;
    }
}
