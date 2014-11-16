package com.esuta.fidm.repository;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class OrgType extends ObjectType{

    private String displayName;
    private List<String> orgType;

    private String locality;

    private List<ObjectReferenceType> parentOrgUnits;

    public OrgType(){}

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getOrgType() {
        if(orgType == null){
            orgType = new ArrayList<String>();
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

    public List<ObjectReferenceType> getParentOrgUnits() {
        if(parentOrgUnits == null){
            parentOrgUnits = new ArrayList<ObjectReferenceType>();
        }

        return parentOrgUnits;
    }

    public void setParentOrgUnits(List<ObjectReferenceType> parentOrgUnits) {
        this.parentOrgUnits = parentOrgUnits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrgType)) return false;
        if (!super.equals(o)) return false;

        OrgType orgType1 = (OrgType) o;

        if (displayName != null ? !displayName.equals(orgType1.displayName) : orgType1.displayName != null)
            return false;
        if (locality != null ? !locality.equals(orgType1.locality) : orgType1.locality != null) return false;
        if (orgType != null ? !orgType.equals(orgType1.orgType) : orgType1.orgType != null) return false;
        if (parentOrgUnits != null ? !parentOrgUnits.equals(orgType1.parentOrgUnits) : orgType1.parentOrgUnits != null)
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
        return result;
    }
}
