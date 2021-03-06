package com.esuta.fidm.repository.schema.core;

import com.esuta.fidm.repository.schema.support.FederationIdentifierType;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *  This class represents a reference to some object in identity provider. It
 *  may be an object in local or remote identity provider. The relation
 *  modelled by this type is considered as generic and it can be used when
 *  AssignmentType/InducementType are not suitable. Common situations are
 *  parent org. relation etc. However, the reference must be to type
 *  extending ObjectType.
 *
 *  @author shood
 * */
@Embeddable
public class ObjectReferenceType implements Serializable{

    /**
     *  A unique identifier of object that is the target of the reference
     *  relation. This value makes sense only when the relation is made
     *  with object in local identity provider.
     * */
    private String uid;

    /**
     *  An identifier of the target object in remote identity provider.
     * */
    private FederationIdentifierType federationIdentifier;

    /**
     *  An attribute that determines, if this reference relationship
     *  should be shared in the copies of object in identity federation.
     *  If this attribute is set to true, it does not mean that the
     *  relationship is not valid in local identity provider. However,
     *  it is not seen in any copy of object possessing this reference.
     * */
    private boolean shareInFederation;

    public ObjectReferenceType(){}

    public ObjectReferenceType(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
        if (!(o instanceof ObjectReferenceType)) return false;

        ObjectReferenceType that = (ObjectReferenceType) o;

        if (shareInFederation != that.shareInFederation) return false;
        if (federationIdentifier != null ? !federationIdentifier.equals(that.federationIdentifier) : that.federationIdentifier != null)
            return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (federationIdentifier != null ? federationIdentifier.hashCode() : 0);
        result = 31 * result + (shareInFederation ? 1 : 0);
        return result;
    }
}
