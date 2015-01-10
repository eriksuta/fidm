package com.esuta.fidm.repository.schema;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
@Entity
public class FederationType {

    private List<String> members;

    public FederationType(){}

    public List<String> getMembers() {
        if(members == null){
            members = new ArrayList<String>();
        }

        return members;
    }

    public void setMembers(List<String> members) {
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
