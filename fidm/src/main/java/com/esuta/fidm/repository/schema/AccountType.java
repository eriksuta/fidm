package com.esuta.fidm.repository.schema;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;

/**
 *  @author shood
 * */
@Entity
public class AccountType extends ObjectType{

    @Index
    private String resource;

    @Index
    private String owner;

    private String description;

    private String password;

    private boolean _protected;

    public AccountType(){}

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountType)) return false;
        if (!super.equals(o)) return false;

        AccountType that = (AccountType) o;

        if (_protected != that._protected) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (resource != null ? !resource.equals(that.resource) : that.resource != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (_protected ? 1 : 0);
        return result;
    }
}
