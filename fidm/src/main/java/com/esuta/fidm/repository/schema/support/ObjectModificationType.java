package com.esuta.fidm.repository.schema.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *  A special object that represents a set of changes performed on some object.
 *  It is used in federation service - changes are transported via REST API
 *  and applied on other federation members. Sending a set of changes is
 *  more efficient than sending an entire object.
 *
 *  @author shood
 * */
public class ObjectModificationType implements Serializable{

    /**
     *  A list of modifications for an object
     * */
    private List<AttributeModificationType> modificationList;

    public List<AttributeModificationType> getModificationList() {
        if(modificationList == null){
            modificationList = new ArrayList<>();
        }

        return modificationList;
    }

    public void setModificationList(List<AttributeModificationType> modificationList) {
        this.modificationList = modificationList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectModificationType)) return false;

        ObjectModificationType that = (ObjectModificationType) o;

        if (modificationList != null ? !modificationList.equals(that.modificationList) : that.modificationList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return modificationList != null ? modificationList.hashCode() : 0;
    }
}
