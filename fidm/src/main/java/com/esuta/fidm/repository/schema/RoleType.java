package com.esuta.fidm.repository.schema;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
@Entity
public class RoleType extends ObjectType{

    @Index
    private String displayName;

    @Index
    private String roleType;

    private List<ObjectReferenceType> organizations;

    public RoleType(){}

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<ObjectReferenceType> getOrganizaitons() {
        if(organizations == null){
            organizations = new ArrayList<ObjectReferenceType>();
        }

        return organizations;
    }

    public void setOrganizaitons(List<ObjectReferenceType> organizaitons) {
        this.organizations = organizaitons;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleType)) return false;
        if (!super.equals(o)) return false;

        RoleType roleType1 = (RoleType) o;

        if (displayName != null ? !displayName.equals(roleType1.displayName) : roleType1.displayName != null)
            return false;
        if (organizations != null ? !organizations.equals(roleType1.organizations) : roleType1.organizations != null)
            return false;
        if (roleType != null ? !roleType.equals(roleType1.roleType) : roleType1.roleType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (roleType != null ? roleType.hashCode() : 0);
        result = 31 * result + (organizations != null ? organizations.hashCode() : 0);
        return result;
    }
}
