package com.esuta.fidm.repository.schema.support;

import java.io.Serializable;

/**
 *  @author shood
 * */
public enum FederationRequestResponseType implements Serializable{

    ACCEPT("Accept"),
    DENY("Deny");

    private String value;

    public String getValue() {
        return value;
    }

    FederationRequestResponseType(String value){
        this.value = value;
    }
}
