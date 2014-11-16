package com.esuta.fidm.repository;

/**
 *  @author shood
 * */
public class ResourceType extends ObjectType{

    private String resourceType;

    public ResourceType(){}

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceType)) return false;
        if (!super.equals(o)) return false;

        ResourceType that = (ResourceType) o;

        if (resourceType != null ? !resourceType.equals(that.resourceType) : that.resourceType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (resourceType != null ? resourceType.hashCode() : 0);
        return result;
    }
}
