package com.esuta.fidm.repository.schema.support;

import java.io.Serializable;

/**
 *  @author shood
 *
 *  TODO - add correct description
 * */
public class FederationIdentifierType implements Serializable {

    private String federationMemberId;
    private String uniqueAttributeValue;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FederationIdentifierType that = (FederationIdentifierType) o;

        if (federationMemberId != null ? !federationMemberId.equals(that.federationMemberId) : that.federationMemberId != null)
            return false;
        if (uniqueAttributeValue != null ? !uniqueAttributeValue.equals(that.uniqueAttributeValue) : that.uniqueAttributeValue != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = federationMemberId != null ? federationMemberId.hashCode() : 0;
        result = 31 * result + (uniqueAttributeValue != null ? uniqueAttributeValue.hashCode() : 0);
        return result;
    }
}
