package com.esuta.fidm.model.federation.client;

import com.esuta.fidm.model.federation.FederationServiceUtil;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  @author shood
 *
 *  TODO - description
 * */
public class RestFederationServiceClient {

    private static RestFederationServiceClient instance = null;

    private RestFederationServiceClient(){
        initRestFederationServiceClient();
    }

    public static RestFederationServiceClient getInstance(){
        if(instance == null){
            instance = new RestFederationServiceClient();
        }

        return instance;
    }

    private void initRestFederationServiceClient(){
        //Put any future configuration here
    }

    public void createFederationRequest(FederationMemberType federationMember) throws IOException {
        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();
        String identifier = federationMember.getFederationMemberName();

        String url = FederationServiceUtil.createFederationRequestUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        //TODO - continue here
        //http://www.mkyong.com/webservices/jax-rs/restful-java-client-with-jersey-client/






    }

    private String objectToJson(Object object){
        Gson gson = new Gson();
        return gson.toJson(object);
    }


}
