package com.esuta.fidm.repository.schema.core;

import javax.persistence.Entity;

/**
 *  @author shood
 * */
@Entity
public class FederationMemberType extends ObjectType{

    public static enum FederationMemberStatusType{
        AVAILABLE,
        REQUESTED
    }

    /**
     *  This attribute is a unique identifier used in identity federations
     * */
    private String federationMemberName;

    private FederationMemberStatusType status;
    private int port;
    private String webAddress;
    private String displayName;
    private String locality;

    public FederationMemberType(){}

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public FederationMemberStatusType getStatus() {
        return status;
    }

    public void setStatus(FederationMemberStatusType status) {
        this.status = status;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFederationMemberName() {
        return federationMemberName;
    }

    public void setFederationMemberName(String federationMemberName) {
        this.federationMemberName = federationMemberName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FederationMemberType)) return false;
        if (!super.equals(o)) return false;

        FederationMemberType that = (FederationMemberType) o;

        if (port != that.port) return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (federationMemberName != null ? !federationMemberName.equals(that.federationMemberName) : that.federationMemberName != null)
            return false;
        if (locality != null ? !locality.equals(that.locality) : that.locality != null) return false;
        if (status != that.status) return false;
        if (webAddress != null ? !webAddress.equals(that.webAddress) : that.webAddress != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (federationMemberName != null ? federationMemberName.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (webAddress != null ? webAddress.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (locality != null ? locality.hashCode() : 0);
        return result;
    }
}
