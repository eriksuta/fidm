package com.esuta.fidm.repository.schema.core;

import com.esuta.fidm.repository.schema.support.FederationIdentifier;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;

/**
 *  @author shood
 * */
@Entity
public class ResourceType extends ObjectType{

    @Index
    private String resourceType;

    private FederationIdentifier federationIdentifier;

    public ResourceType(){}

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
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

        ResourceType that = (ResourceType) o;

        if (federationIdentifier != null ? !federationIdentifier.equals(that.federationIdentifier) : that.federationIdentifier != null)
            return false;
        if (resourceType != null ? !resourceType.equals(that.resourceType) : that.resourceType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (resourceType != null ? resourceType.hashCode() : 0);
        result = 31 * result + (federationIdentifier != null ? federationIdentifier.hashCode() : 0);
        return result;
    }
}
