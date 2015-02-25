package com.esuta.fidm.model.federation;

import java.io.Serializable;

/**
 *  @author shood
 * */
public class FederationServiceUtil implements Serializable{

    public static final String REST_SERVICE_PATH = "/rest";

    public static final String FEDERATION_REQUEST = "/federationRequest";
    public static final String FEDERATION_REQUEST_RESPONSE = "/federationResponse";

    public static String createFederationRequestUrl(String address, int port){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(FEDERATION_REQUEST);
        return sb.toString();
    }

    public static String createFederationRequestResponseUrl(String address, int port){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(FEDERATION_REQUEST_RESPONSE);
        return sb.toString();
    }

}
