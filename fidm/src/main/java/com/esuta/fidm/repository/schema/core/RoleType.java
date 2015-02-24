package com.esuta.fidm.repository.schema.core;

import com.esuta.fidm.repository.schema.support.FederationIdentifier;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;

/**
 *  @author shood
 * */
@Entity
public class RoleType extends ObjectType{

    @Index
    private String displayName;

    @Index
    private String roleType;

    private FederationIdentifier federationIdentifier;

    public RoleType(){}

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public FederationIdentifier getFederationIdentifier() {
        return federationIdentifier;
    }

    public void setFederationIdentifier(FederationIdentifier federationIdentifier) {
        this.federationIdentifier = federationIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RoleType roleType1 = (RoleType) o;

        if (displayName != null ? !displayName.equals(roleType1.displayName) : roleType1.displayName != null)
            return false;
        if (federationIdentifier != null ? !federationIdentifier.equals(roleType1.federationIdentifier) : roleType1.federationIdentifier != null)
            return false;
        if (roleType != null ? !roleType.equals(roleType1.roleType) : roleType1.roleType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (roleType != null ? roleType.hashCode() : 0);
        result = 31 * result + (federationIdentifier != null ? federationIdentifier.hashCode() : 0);
        return result;
    }
}
