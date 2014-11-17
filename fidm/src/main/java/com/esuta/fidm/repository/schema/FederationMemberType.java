package com.esuta.fidm.repository.schema;

import javax.persistence.Entity;

/**
 *  @author shood
 * */
@Entity
public class FederationMemberType {

    private String displayName;
    private String locality;

    public FederationMemberType(){}

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FederationMemberType)) return false;

        FederationMemberType that = (FederationMemberType) o;

        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (locality != null ? !locality.equals(that.locality) : that.locality != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = displayName != null ? displayName.hashCode() : 0;
        result = 31 * result + (locality != null ? locality.hashCode() : 0);
        return result;
    }
}
