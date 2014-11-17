package com.esuta.fidm.repository.schema;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 *  @author shood
 * */
@Entity
public class SystemConfigurationType implements Serializable{

    private String dbConnectionFile;

    public SystemConfigurationType(){}

    public String getDbConnectionFile() {
        return dbConnectionFile;
    }

    public void setDbConnectionFile(String dbConnectionFile) {
        this.dbConnectionFile = dbConnectionFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemConfigurationType)) return false;

        SystemConfigurationType that = (SystemConfigurationType) o;

        if (dbConnectionFile != null ? !dbConnectionFile.equals(that.dbConnectionFile) : that.dbConnectionFile != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return dbConnectionFile != null ? dbConnectionFile.hashCode() : 0;
    }
}
