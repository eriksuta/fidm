package com.esuta.fidm.repository.schema.core;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;

/**
 *  @author shood
 * */
@Entity
public class FederationMemberType extends ObjectType{

    public static enum FederationMemberStatusType{
        AVAILABLE,
        REQUESTED,
        DENIED,
        DELETE_REQUESTED
    }

    /**
     *  A system unique name of the federation member.
     * */
    @Index(unique = "true")
    private String name;

    /**
     *  Display name can be used as a readable form for identity
     *  provider, member of identity federation
     * */
    @Index
    private String displayName;

    /**
     *  This attribute is a unique identifier used in identity federations
     * */
    private String federationMemberName;

    /**
     *  Another identifier attribute, this one uniquely identifies the
     *  requester of federation membership relationship
     * */
    private String requesterIdentifier;

    /**
     *  A status of federation membership, can have several values:
     *      AVAILABLE
     *      REQUESTED
     *      DENIED
     *      DELETE_REQUESTED
     * */
    private FederationMemberStatusType status;

    /**
     *  A communication attribute - this one specified a port on which
     *  federation member can be contacted.
     * */
    private int port;

    /**
     *  A communication attribute - this one specifies a web address on
     *  which federation member can be contacted
     * */
    private String webAddress;

    /**
     *  A physical locality, can be a city, country, state, address, etc.
     * */
    private String locality;

    /**
     *  A name of attribute of OrgType that is unique in federation
     *  member system. A system can identify the org. unit by using the value
     *  of this attribute.
     * */
    private String uniqueOrgIdentifier;

    /**
     *  A name of attribute of UserType that is unique in federation
     *  member system. A system can identify the user (subject) by using the value
     *  of this attribute.
     * */
    private String uniqueUserIdentifier;

    /**
     *  A name of attribute of ResourceType that is unique in federation
     *  member system. A system can identify the resource (relying party)
     *  by using the value of this attribute.
     * */
    private String uniqueResourceIdentifier;

    /**
     *  A name of attribute of RoleType that is unique in federation
     *  member system. A system can identify the role by using the value
     *  of this attribute.
     * */
    private String uniqueRoleIdentifier;

    public FederationMemberType(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getRequesterIdentifier() {
        return requesterIdentifier;
    }

    public void setRequesterIdentifier(String requesterIdentifier) {
        this.requesterIdentifier = requesterIdentifier;
    }

    public String getUniqueOrgIdentifier() {
        return uniqueOrgIdentifier;
    }

    public void setUniqueOrgIdentifier(String uniqueOrgIdentifier) {
        this.uniqueOrgIdentifier = uniqueOrgIdentifier;
    }

    public String getUniqueUserIdentifier() {
        return uniqueUserIdentifier;
    }

    public void setUniqueUserIdentifier(String uniqueUserIdentifier) {
        this.uniqueUserIdentifier = uniqueUserIdentifier;
    }

    public String getUniqueResourceIdentifier() {
        return uniqueResourceIdentifier;
    }

    public void setUniqueResourceIdentifier(String uniqueResourceIdentifier) {
        this.uniqueResourceIdentifier = uniqueResourceIdentifier;
    }

    public String getUniqueRoleIdentifier() {
        return uniqueRoleIdentifier;
    }

    public void setUniqueRoleIdentifier(String uniqueRoleIdentifier) {
        this.uniqueRoleIdentifier = uniqueRoleIdentifier;
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
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (requesterIdentifier != null ? !requesterIdentifier.equals(that.requesterIdentifier) : that.requesterIdentifier != null)
            return false;
        if (status != that.status) return false;
        if (uniqueOrgIdentifier != null ? !uniqueOrgIdentifier.equals(that.uniqueOrgIdentifier) : that.uniqueOrgIdentifier != null)
            return false;
        if (uniqueResourceIdentifier != null ? !uniqueResourceIdentifier.equals(that.uniqueResourceIdentifier) : that.uniqueResourceIdentifier != null)
            return false;
        if (uniqueRoleIdentifier != null ? !uniqueRoleIdentifier.equals(that.uniqueRoleIdentifier) : that.uniqueRoleIdentifier != null)
            return false;
        if (uniqueUserIdentifier != null ? !uniqueUserIdentifier.equals(that.uniqueUserIdentifier) : that.uniqueUserIdentifier != null)
            return false;
        if (webAddress != null ? !webAddress.equals(that.webAddress) : that.webAddress != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (federationMemberName != null ? federationMemberName.hashCode() : 0);
        result = 31 * result + (requesterIdentifier != null ? requesterIdentifier.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (webAddress != null ? webAddress.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (locality != null ? locality.hashCode() : 0);
        result = 31 * result + (uniqueOrgIdentifier != null ? uniqueOrgIdentifier.hashCode() : 0);
        result = 31 * result + (uniqueUserIdentifier != null ? uniqueUserIdentifier.hashCode() : 0);
        result = 31 * result + (uniqueResourceIdentifier != null ? uniqueResourceIdentifier.hashCode() : 0);
        result = 31 * result + (uniqueRoleIdentifier != null ? uniqueRoleIdentifier.hashCode() : 0);
        return result;
    }
}
