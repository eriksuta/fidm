package com.esuta.fidm.model.federation.service;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *  @author shood
 * */

@XmlRootElement
public class FederationMembershipRequest implements Serializable{

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
    private String address;
    private int port;
    private String identityProviderIdentifier;

    public FederationMembershipRequest() {}

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FederationMembershipRequest)) return false;

        FederationMembershipRequest that = (FederationMembershipRequest) o;

        if (port != that.port) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (identityProviderIdentifier != null ? !identityProviderIdentifier.equals(that.identityProviderIdentifier) : that.identityProviderIdentifier != null)
            return false;
        if (response != that.response) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = response != null ? response.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (identityProviderIdentifier != null ? identityProviderIdentifier.hashCode() : 0);
        return result;
    }
}
