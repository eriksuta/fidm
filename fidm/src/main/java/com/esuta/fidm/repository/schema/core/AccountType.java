package com.esuta.fidm.repository.schema.core;

import com.esuta.fidm.repository.schema.support.FederationIdentifierType;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;

/**
 *  @author shood
 * */
@Entity
public class AccountType extends ObjectType{

    /**
     *  A system unique name of the account.
     * */
    @Index(unique = "true")
    private String name;

    /**
     *  A reference to the resource, on which the account
     *  is located
     * */
    @Index
    private ObjectReferenceType<ResourceType> resource;

    /**
     *  A reference to the UserType instance, a user that
     *  is an owner of account entity in identity provider
     *  as well as on the target system
     * */
    private ObjectReferenceType<UserType> owner;

    /**
     *  A password used to login, or verify the identity
     *  of the user on target system.
     * */
    private String password;

    /**
     *  An attribute representing the 'protection' state of an account.
     *  Protected accounts can't be deleted from the target system, but
     *  they may be deleted from the identity provider system
     * */
    private boolean _protected;

    /**
     *  A federation identifier used to uniquely identify the account object
     *  across federation. More specifically, it is a link to the origin
     *  of this account. If this attribute is empty, the account is
     *  local, thus the origin of this account is current identity
     *  provider. A federation identifier contains an identifier
     *  of federation member (FederationMemberType) and a single
     *  'uniqueAttributeValue' - an attribute containing a value that
     *  is guaranteed to be unique in origin identity provider. The
     *  source of this value is not known to this provider, it is handled
     *  by origin identity provider, so we believe that this mechanism
     *  is privacy-respecting.
     * */
    private FederationIdentifierType federationIdentifier;

    public AccountType(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectReferenceType<ResourceType> getResource() {
        return resource;
    }

    public void setResource(ObjectReferenceType<ResourceType> resource) {
        this.resource = resource;
    }

    public ObjectReferenceType<UserType> getOwner() {
        return owner;
    }

    public void setOwner(ObjectReferenceType<UserType> owner) {
        this.owner = owner;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean is_protected() {
        return _protected;
    }

    public void set_protected(boolean _protected) {
        this._protected = _protected;
    }

    public FederationIdentifierType getFederationIdentifier() {
        return federationIdentifier;
    }

    public void setFederationIdentifier(FederationIdentifierType federationIdentifier) {
        this.federationIdentifier = federationIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountType)) return false;
        if (!super.equals(o)) return false;

        AccountType that = (AccountType) o;

        if (_protected != that._protected) return false;
        if (federationIdentifier != null ? !federationIdentifier.equals(that.federationIdentifier) : that.federationIdentifier != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (resource != null ? !resource.equals(that.resource) : that.resource != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (_protected ? 1 : 0);
        result = 31 * result + (federationIdentifier != null ? federationIdentifier.hashCode() : 0);
        return result;
    }
}
