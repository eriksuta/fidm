package com.esuta.fidm.model.federation.service;

import com.esuta.fidm.repository.schema.support.FederationIdentifierType;

import java.io.Serializable;

/**
 *  A simple wrapper object for operations regarding
 *  remote account creation and deletion
 *
 *  @author shood
 * */
public class AccountRequestWrapper implements Serializable{

    /**
     *  The identifier of the federation member requesting an operation
     *  with the account
     * */
    String memberIdentifier;

    /**
     *  A name of the account (to delete, or to create)
     * */
    String accountName;

    /**
     *  unique attribute value of the resource for which this account should be created
     * */
    String resourceUniqueAttributeValue;

    /**
     *  A password for created account
     * */
    String password;

    /**
     *  An identifier of remote owner of the account
     * */
    FederationIdentifierType ownerIdentifier;

    public AccountRequestWrapper() {}

    public String getMemberIdentifier() {
        return memberIdentifier;
    }

    public void setMemberIdentifier(String memberIdentifier) {
        this.memberIdentifier = memberIdentifier;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getResourceUniqueAttributeValue() {
        return resourceUniqueAttributeValue;
    }

    public void setResourceUniqueAttributeValue(String resourceUniqueAttributeValue) {
        this.resourceUniqueAttributeValue = resourceUniqueAttributeValue;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FederationIdentifierType getOwnerIdentifier() {
        return ownerIdentifier;
    }

    public void setOwnerIdentifier(FederationIdentifierType ownerIdentifier) {
        this.ownerIdentifier = ownerIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountRequestWrapper)) return false;

        AccountRequestWrapper that = (AccountRequestWrapper) o;

        if (memberIdentifier != null ? !memberIdentifier.equals(that.memberIdentifier) : that.memberIdentifier != null)
            return false;
        if (accountName != null ? !accountName.equals(that.accountName) : that.accountName != null) return false;
        if (resourceUniqueAttributeValue != null ? !resourceUniqueAttributeValue.equals(that.resourceUniqueAttributeValue) : that.resourceUniqueAttributeValue != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        return !(ownerIdentifier != null ? !ownerIdentifier.equals(that.ownerIdentifier) : that.ownerIdentifier != null);

    }

    @Override
    public int hashCode() {
        int result = memberIdentifier != null ? memberIdentifier.hashCode() : 0;
        result = 31 * result + (accountName != null ? accountName.hashCode() : 0);
        result = 31 * result + (resourceUniqueAttributeValue != null ? resourceUniqueAttributeValue.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (ownerIdentifier != null ? ownerIdentifier.hashCode() : 0);
        return result;
    }
}
