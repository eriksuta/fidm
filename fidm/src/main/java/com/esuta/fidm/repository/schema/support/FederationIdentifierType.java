package com.esuta.fidm.repository.schema.support;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 *  @author shood
 * */
@Embeddable
public class FederationIdentifierType implements Serializable {

    /**
     *  An identifier of an origin identity provider in
     *  identity federation that holds the origin version
     *  of an object, that this identifier is referencing.
     * */
    private String federationMemberId;

    /**
     *  A String representation of a value of unique
     *  attribute, that identifies the object in origin
     *  identity provider. The holder of this identifier
     *  does not know the type or meaning of such attribute,
     *  only the value, thus privacy is respected
     * */
    private String uniqueAttributeValue;

    /**
     *  A String representation of a type, or class, of an
     *  origin object in origin identity federation.
     * */
    private String objectType;

    public String getFederationMemberId() {
        return federationMemberId;
    }

    public void setFederationMemberId(String federationMemberId) {
        this.federationMemberId = federationMemberId;
    }

    public String getUniqueAttributeValue() {
        return uniqueAttributeValue;
    }

    public void setUniqueAttributeValue(String uniqueAttributeValue) {
        this.uniqueAttributeValue = uniqueAttributeValue;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FederationIdentifierType)) return false;

        FederationIdentifierType that = (FederationIdentifierType) o;

        if (federationMemberId != null ? !federationMemberId.equals(that.federationMemberId) : that.federationMemberId != null)
            return false;
        if (objectType != null ? !objectType.equals(that.objectType) : that.objectType != null) return false;
        if (uniqueAttributeValue != null ? !uniqueAttributeValue.equals(that.uniqueAttributeValue) : that.uniqueAttributeValue != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = federationMemberId != null ? federationMemberId.hashCode() : 0;
        result = 31 * result + (uniqueAttributeValue != null ? uniqueAttributeValue.hashCode() : 0);
        result = 31 * result + (objectType != null ? objectType.hashCode() : 0);
        return result;
    }
}
