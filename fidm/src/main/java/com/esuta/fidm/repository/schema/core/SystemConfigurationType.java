package com.esuta.fidm.repository.schema.core;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 *  @author shood
 * */
@Entity
public class SystemConfigurationType extends ObjectType{

    private String identityProviderIdentifier;
    private String dbConnectionFile;

    public SystemConfigurationType(){}

    public String getDbConnectionFile() {
        return dbConnectionFile;
    }

    public void setDbConnectionFile(String dbConnectionFile) {
        this.dbConnectionFile = dbConnectionFile;
    }

    public String getIdentityProviderIdentifier() {
        return identityProviderIdentifier;
    }

    public void setIdentityProviderIdentifier(String identityProviderIdentifier) {
        this.identityProviderIdentifier = identityProviderIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemConfigurationType)) return false;

        SystemConfigurationType that = (SystemConfigurationType) o;

        if (dbConnectionFile != null ? !dbConnectionFile.equals(that.dbConnectionFile) : that.dbConnectionFile != null)
            return false;
        if (identityProviderIdentifier != null ? !identityProviderIdentifier.equals(that.identityProviderIdentifier) : that.identityProviderIdentifier != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = identityProviderIdentifier != null ? identityProviderIdentifier.hashCode() : 0;
        result = 31 * result + (dbConnectionFile != null ? dbConnectionFile.hashCode() : 0);
        return result;
    }
}
