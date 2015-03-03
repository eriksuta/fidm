package com.esuta.fidm.model.federation.client;

import com.esuta.fidm.model.federation.service.FederationMembershipRequest;
import com.esuta.fidm.model.federation.service.FederationServiceUtil;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 *  @author shood
 *
 *  TODO - description
 * */
public class RestFederationServiceClient {

    Logger LOGGER = Logger.getLogger(RestFederationServiceClient.class);

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

    /**
     *  Here, if status is 200, the message contains the requested identifier,
     *  else is will contain the error message
     * */
    public SimpleRestResponseStatus createGetFederationIdentifierRequest(FederationMemberType federationMember){
        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = FederationServiceUtil.createGetFederationMemberIdentifier(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponseStatus(responseStatus, responseMessage);
    }

    public SimpleRestResponseStatus createFederationRequest(FederationMemberType federationMember, String localAddress, int localPort) throws IOException {
        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = FederationServiceUtil.createFederationRequestUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        FederationMembershipRequest request = new FederationMembershipRequest();
        request.setIdentityProviderIdentifier(federationMember.getRequesterIdentifier());
        request.setPort(localPort);
        request.setAddress(localAddress);

        String jsonRequest = objectToJson(request);

        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonRequest);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponseStatus(responseStatus, responseMessage);
    }

    public SimpleRestResponseStatus createFederationResponse(FederationMemberType federationMember,
                                                             FederationMembershipRequest.Response responseType) throws IOException {
        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = FederationServiceUtil.createFederationRequestResponseUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        FederationMembershipRequest responseObject = new FederationMembershipRequest();
        responseObject.setIdentityProviderIdentifier(federationMember.getRequesterIdentifier());
        responseObject.setResponse(responseType);
        String responseObjectJson = objectToJson(responseObject);

        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, responseObjectJson);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponseStatus(responseStatus, responseMessage);
    }

    public SimpleRestResponseStatus createFederationDeletionRequest(FederationMemberType federationMember) throws IOException {
        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = FederationServiceUtil.createFederationDeleteRequestUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        FederationMembershipRequest request = new FederationMembershipRequest();
        request.setIdentityProviderIdentifier(federationMember.getRequesterIdentifier());

        String jsonRequest = objectToJson(request);

        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonRequest);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponseStatus(responseStatus, responseMessage);
    }

    public SimpleRestResponseStatus createFederationDeletionRequestResponse(FederationMemberType federationMember,
                                                                            FederationMembershipRequest.Response responseType) throws IOException {
        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = FederationServiceUtil.createFederationDeleteResponseUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        FederationMembershipRequest responseObject = new FederationMembershipRequest();
        responseObject.setIdentityProviderIdentifier(federationMember.getFederationMemberName());
        responseObject.setResponse(responseType);
        String responseObjectJson = objectToJson(responseObject);

        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, responseObjectJson);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponseStatus(responseStatus, responseMessage);
    }

    private String objectToJson(Object object){
        Gson gson = new Gson();
        return gson.toJson(object);
    }
}
