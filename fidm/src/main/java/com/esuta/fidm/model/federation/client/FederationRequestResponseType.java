package com.esuta.fidm.model.federation.client;

import java.io.Serializable;

/**
 *  @author shood
 * */

public class FederationRequestResponseType implements Serializable{

    public enum Response{
        ACCEPT("Accept"),
        DENY("Deny");

        private String value;

        public String getValue() {
            return value;
        }

            Response(String value){
            this.value = value;
        }
    }

    private Response response;
    private String identityProviderIdentifier;

    public FederationRequestResponseType() {}

    public FederationRequestResponseType(Response response, String identityProviderIdentifier) {
        this.response = response;
        this.identityProviderIdentifier = identityProviderIdentifier;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
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
        if (!(o instanceof FederationRequestResponseType)) return false;

        FederationRequestResponseType that = (FederationRequestResponseType) o;

        if (identityProviderIdentifier != null ? !identityProviderIdentifier.equals(that.identityProviderIdentifier) : that.identityProviderIdentifier != null)
            return false;
        if (response != that.response) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = response != null ? response.hashCode() : 0;
        result = 31 * result + (identityProviderIdentifier != null ? identityProviderIdentifier.hashCode() : 0);
        return result;
    }
}
