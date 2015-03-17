package com.esuta.fidm.model.federation.service;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *  This is a simple object used in REST Federation Service API. It is used
 *  as a holder for information about different type of objects.
 *
 *  @author shood
 * */
@XmlRootElement
public class ObjectInformation implements Serializable{

    /**
     *  The name of the object
     * */
    private String objectName;

    /**
     *  The description of the object
     * */
    private String objectDescription;

    public ObjectInformation() {}

    public ObjectInformation(String objectName, String objectDescription) {
        this.objectName = objectName;
        this.objectDescription = objectDescription;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectDescription() {
        return objectDescription;
    }

    public void setObjectDescription(String objectDescription) {
        this.objectDescription = objectDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectInformation)) return false;

        ObjectInformation that = (ObjectInformation) o;

        if (objectDescription != null ? !objectDescription.equals(that.objectDescription) : that.objectDescription != null)
            return false;
        if (objectName != null ? !objectName.equals(that.objectName) : that.objectName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = objectName != null ? objectName.hashCode() : 0;
        result = 31 * result + (objectDescription != null ? objectDescription.hashCode() : 0);
        return result;
    }
}
