package com.esuta.fidm.repository.schema.core;

import com.esuta.fidm.repository.schema.support.FederationIdentifierType;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *  This class represents a reference to some object in identity provider. It
 *  may be an object in local or remote identity provider. The relation
 *  modelled by this type is considered as special. It models the possession
 *  of some other object in identity provider. For example, a user
 *  may have assigned accounts, roles or org. units. The reference must be to type
 *  extending ObjectType.
 *
 *  @author shood
 * */
@Embeddable
public class AssignmentType implements Serializable{

    /**
     *  A unique identifier of object that is the target of the assignment
     *  relation. This value makes sense only when the relation is made
     *  with object in local identity provider.
     * */
    private String uid;

    /**
     *  This attribute identifies the source of assignment. In general,
     *  assignment may be manual (done by administrator by GUI) or
     *  automatic - performed by system based on inducement configuration.
     *  This attribute stores the information about it.
     * */
    private boolean assignedByInducement = false;

    /**
     *  An identifier of the target object in remote identity provider.
     * */
    private FederationIdentifierType federationIdentifier;

    /**
     *  An attribute that determines, if this assignment relationship
     *  should be shared in the copies of object in identity federation.
     *  If this attribute is set to true, it does not mean that the
     *  relationship is not valid in local identity provider. However,
     *  it is not seen in any copy of object possessing this assignment.
     * */
    private boolean shareInFederation;

    public AssignmentType() {}

    public AssignmentType(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isAssignedByInducement() {
        return assignedByInducement;
    }

    public void setAssignedByInducement(boolean assignedByInducement) {
        this.assignedByInducement = assignedByInducement;
    }

    public FederationIdentifierType getFederationIdentifier() {
        return federationIdentifier;
    }

    public void setFederationIdentifier(FederationIdentifierType federationIdentifier) {
        this.federationIdentifier = federationIdentifier;
    }

    public boolean isShareInFederation() {
        return shareInFederation;
    }

    public void setShareInFederation(boolean shareInFederation) {
        this.shareInFederation = shareInFederation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssignmentType)) return false;

        AssignmentType that = (AssignmentType) o;

        if (assignedByInducement != that.assignedByInducement) return false;
        if (shareInFederation != that.shareInFederation) return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;
        return !(federationIdentifier != null ? !federationIdentifier.equals(that.federationIdentifier) : that.federationIdentifier != null);

    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (assignedByInducement ? 1 : 0);
        result = 31 * result + (federationIdentifier != null ? federationIdentifier.hashCode() : 0);
        result = 31 * result + (shareInFederation ? 1 : 0);
        return result;
    }
}
