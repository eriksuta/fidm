package com.esuta.fidm.repository.schema.core;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 *  @author shood
 * */
@Entity
public class SystemConfigurationType extends ObjectType{

    /**
     *  A system unique name of the system configuration.
     * */
    @Index(unique = "true")
    private String name;

    /**
     *  An identifier for the member of the identity federation.
     *  This attribute uniquely identifier the federation member
     *  and it's format should be defined by internal policy of the
     *  federation.
     * */
    private String identityProviderIdentifier;

    /**
     *  A system path to the database
     * */
    private String dbConnectionFile;

    /**
     *  A port on which this local identity provider runs
     * */
    private int port;

    /**
     *  A web address representation of server on which this
     *  identity provider runs.
     * */
    private String localAddress;

    public SystemConfigurationType(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (identityProviderIdentifier != null ? identityProviderIdentifier.hashCode() : 0);
        result = 31 * result + (dbConnectionFile != null ? dbConnectionFile.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (localAddress != null ? localAddress.hashCode() : 0);
        return result;
    }
}
