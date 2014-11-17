package com.esuta.fidm.repository.schema;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class FederationType {

    private List<ObjectReferenceType> members;

    public FederationType(){}

    public List<ObjectReferenceType> getMembers() {
        if(members == null){
            members = new ArrayList<ObjectReferenceType>();
        }

        return members;
    }

    public void setMembers(List<ObjectReferenceType> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FederationType)) return false;

        FederationType that = (FederationType) o;

        if (members != null ? !members.equals(that.members) : that.members != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return members != null ? members.hashCode() : 0;
    }
}
