package com.esuta.fidm.repository.schema.core;

import com.esuta.fidm.repository.schema.support.FederationIdentifierType;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;

/**
 *  @author shood
 * */
@Entity
public class ResourceType extends ObjectType{

    /**
     *  A system unique name of the resource (the relying party).
     * */
    @Index(unique = "true")
    private String name;

    /**
     *  A display name for resource object
     * */
    @Index
    private String displayName;

    /**
     *  A type of resource, represents it's intent or purpose,
     *  for example HR resource etc.
     * */
    @Index
    private String resourceType;

    /**
     *  A federation identifier used to uniquely identify the resource
     *  across federation. More specifically, it is a link to the origin
     *  of this resource. If this attribute is empty, the resource is
     *  local, thus the origin of this resource is current identity
     *  provider. A federation identifier contains an identifier
     *  of federation member (FederationMemberType) and a single
     *  'uniqueAttributeValue' - an attribute containing a value that
     *  is guaranteed to be unique in origin identity provider. The
     *  source of this value is not known to this provider, it is handled
     *  by origin identity provider, so we believe that this mechanism
     *  is privacy-respecting.
     * */
    private FederationIdentifierType federationIdentifier;

    public ResourceType(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public FederationIdentifierType getFederationIdentifier() {
        return federationIdentifier;
    }

    public void setFederationIdentifier(FederationIdentifierType federationIdentifier) {
        this.federationIdentifier = federationIdentifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceType)) return false;
        if (!super.equals(o)) return false;

        ResourceType that = (ResourceType) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (resourceType != null ? !resourceType.equals(that.resourceType) : that.resourceType != null) return false;
        return !(federationIdentifier != null ? !federationIdentifier.equals(that.federationIdentifier) : that.federationIdentifier != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (resourceType != null ? resourceType.hashCode() : 0);
        result = 31 * result + (federationIdentifier != null ? federationIdentifier.hashCode() : 0);
        return result;
    }
}
