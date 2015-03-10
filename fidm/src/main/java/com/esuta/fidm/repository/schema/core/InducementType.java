package com.esuta.fidm.repository.schema.core;

import com.esuta.fidm.repository.schema.support.FederationIdentifierType;

import java.io.Serializable;

/**
 *  This class represents a reference to some object in identity provider. It
 *  may be an object in local or remote identity provider. The relation
 *  modelled by this type is considered as special. It models the enforced possession
 *  of some other object in identity provider. For example, a membership
 *  in an organizational unit may induce the possession of some roles or
 *  the possession of accounts on selected resources. These enforced
 *  possessions should be modelled by inducement relations. The reference must be to type
 *  extending ObjectType.
 *
 *  @author shood
 * */
public class InducementType<T extends ObjectType> implements Serializable{

    /**
     *  The type of the object.
     * */
    private Class<T> type;

    /**
     *  A unique identifier of object that is the target of the inducement
     *  relation. This value makes sense only when the relation is made
     *  with object in local identity provider.
     * */
    private String uid;

    /**
     *  An identifier of the target object in remote identity provider.
     * */
    private FederationIdentifierType federationIdentifier;

    /**
     *  An attribute that determines, if this inducement relationship
     *  should be shared in the copies of object in identity federation.
     *  If this attribute is set to true, it does not mean that the
     *  relationship is not valid in local identity provider. However,
     *  it is not seen in any copy of object possessing this reference.
     * */
    private boolean sharedInFederation;

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
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

    public boolean isSharedInFederation() {
        return sharedInFederation;
    }

    public void setSharedInFederation(boolean sharedInFederation) {
        this.sharedInFederation = sharedInFederation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InducementType)) return false;

        InducementType that = (InducementType) o;

        if (sharedInFederation != that.sharedInFederation) return false;
        if (federationIdentifier != null ? !federationIdentifier.equals(that.federationIdentifier) : that.federationIdentifier != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (federationIdentifier != null ? federationIdentifier.hashCode() : 0);
        result = 31 * result + (sharedInFederation ? 1 : 0);
        return result;
    }
}
