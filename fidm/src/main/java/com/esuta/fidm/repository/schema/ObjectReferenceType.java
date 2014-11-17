package com.esuta.fidm.repository.schema;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *  @author shood
 * */
@Embeddable
public class ObjectReferenceType implements Serializable{

    private String uid;
    private Class<? extends ObjectType> type;

    public ObjectReferenceType(){}

    public ObjectReferenceType(String uid, Class<? extends ObjectType> type){
        this.uid = uid;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Class<? extends ObjectType> getType() {
        return type;
    }

    public void setType(Class<? extends ObjectType> type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectReferenceType)) return false;

        ObjectReferenceType that = (ObjectReferenceType) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
