package com.esuta.fidm.repository.schema.core;

import com.esuta.fidm.repository.schema.support.FederationIdentifier;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;

/**
 *  @author shood
 * */
@Entity
public class RoleType extends ObjectType{

    /**
     *  A system unique name of the role.
     * */
    @Index(unique = "true")
    private String name;

    /**
     *  The display name of role. Should be a human readable form,
     *  such as 'Chemistry Department' etc. This attribute is mostly
     *  used in user interface.
     * */
    @Index
    private String displayName;

    /**
     *  A type of role, it defines it purpose. The role may be
     *  organizational, security etc.
     * */
    @Index
    private String roleType;

    /**
     *  A federation identifier used to uniquely identify the role
     *  across federation. More specifically, it is a link to the origin
     *  of this role. If this attribute is empty, the role is
     *  local, thus the origin of this org. unit is current identity
     *  provider. A federation identifier contains an identifier
     *  of federation member (FederationMemberType) and a single
     *  'uniqueAttributeValue' - an attribute containing a value that
     *  is guaranteed to be unique in origin identity provider. The
     *  source of this value is not known to this provider, it is handled
     *  by origin identity provider, so we believe that this mechanism
     *  is privacy-respecting.
     * */
    private FederationIdentifier federationIdentifier;

    public RoleType(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
        if (!(o instanceof RoleType)) return false;
        if (!super.equals(o)) return false;

        RoleType roleType1 = (RoleType) o;

        if (displayName != null ? !displayName.equals(roleType1.displayName) : roleType1.displayName != null)
            return false;
        if (federationIdentifier != null ? !federationIdentifier.equals(roleType1.federationIdentifier) : roleType1.federationIdentifier != null)
            return false;
        if (name != null ? !name.equals(roleType1.name) : roleType1.name != null) return false;
        if (roleType != null ? !roleType.equals(roleType1.roleType) : roleType1.roleType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (roleType != null ? roleType.hashCode() : 0);
        result = 31 * result + (federationIdentifier != null ? federationIdentifier.hashCode() : 0);
        return result;
    }
}
