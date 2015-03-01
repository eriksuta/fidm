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
    private int port;
    private String localAddress;

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemConfigurationType)) return false;
        if (!super.equals(o)) return false;

        SystemConfigurationType that = (SystemConfigurationType) o;

        if (port != that.port) return false;
        if (dbConnectionFile != null ? !dbConnectionFile.equals(that.dbConnectionFile) : that.dbConnectionFile != null)
            return false;
        if (identityProviderIdentifier != null ? !identityProviderIdentifier.equals(that.identityProviderIdentifier) : that.identityProviderIdentifier != null)
            return false;
        if (localAddress != null ? !localAddress.equals(that.localAddress) : that.localAddress != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (identityProviderIdentifier != null ? identityProviderIdentifier.hashCode() : 0);
        result = 31 * result + (dbConnectionFile != null ? dbConnectionFile.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (localAddress != null ? localAddress.hashCode() : 0);
        return result;
    }
}
