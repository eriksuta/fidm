package com.esuta.fidm.model.federation.client;

import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.model.IModelService;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.federation.service.FederationMembershipRequest;
import com.esuta.fidm.model.federation.service.FederationServiceUtil;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.esuta.fidm.repository.schema.core.OrgType;
import com.esuta.fidm.repository.schema.core.SystemConfigurationType;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 *  @author shood
 *
 *  TODO - description
 *  TODO - add commentary to all methods
 * */
public class RestFederationServiceClient {

    Logger LOGGER = Logger.getLogger(RestFederationServiceClient.class);

    private static RestFederationServiceClient instance = null;
    private IModelService modelService;

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
        modelService = ModelService.getInstance();
    }

    private String getLocalFederationMemberIdentifier() throws DatabaseCommunicationException {
        SystemConfigurationType systemConfiguration = modelService.readObject(SystemConfigurationType.class, PageBase.SYSTEM_CONFIG_UID);
        return systemConfiguration.getIdentityProviderIdentifier();
    }

    /**
     *  Here, if status is 200, the message contains the requested identifier,
     *  else is will contain the error message
     * */
    public SimpleRestResponse createGetFederationIdentifierRequest(FederationMemberType federationMember){
        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = FederationServiceUtil.createGetFederationMemberIdentifier(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponse(responseStatus, responseMessage);
    }

    public SimpleRestResponse createFederationRequest(FederationMemberType federationMember, String localAddress, int localPort) throws IOException {
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

        return new SimpleRestResponse(responseStatus, responseMessage);
    }

    public SimpleRestResponse createFederationResponse(FederationMemberType federationMember,
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

        return new SimpleRestResponse(responseStatus, responseMessage);
    }

    public SimpleRestResponse createFederationDeletionRequest(FederationMemberType federationMember)
            throws IOException, DatabaseCommunicationException {

        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = FederationServiceUtil.createFederationDeleteRequestUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        FederationMembershipRequest request = new FederationMembershipRequest();
        request.setIdentityProviderIdentifier(getLocalFederationMemberIdentifier());

        String jsonRequest = objectToJson(request);

        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonRequest);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponse(responseStatus, responseMessage);
    }

    public SimpleRestResponse createFederationDeletionRequestResponse(FederationMemberType federationMember,
                                                                            FederationMembershipRequest.Response responseType) throws IOException, DatabaseCommunicationException {
        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = FederationServiceUtil.createFederationDeleteResponseUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        FederationMembershipRequest responseObject = new FederationMembershipRequest();
        responseObject.setIdentityProviderIdentifier(getLocalFederationMemberIdentifier());
        responseObject.setResponse(responseType);
        String responseObjectJson = objectToJson(responseObject);

        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, responseObjectJson);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponse(responseStatus, responseMessage);
    }

    public IntegerRestResponse createGetSharedOrgUnitCountRequest(FederationMemberType federationMember) throws DatabaseCommunicationException {
        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = FederationServiceUtil.createGetSharedOrgUnitCountUrl(address, port, getLocalFederationMemberIdentifier());
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        IntegerRestResponse responseObject = new IntegerRestResponse();
        responseObject.setStatus(responseStatus);
        if(responseStatus == HttpStatus.OK_200){
            responseObject.setValue((Integer)jsonToObject(responseMessage, Integer.class));
        } else {
            responseObject.setMessage(responseMessage);
        }

        return responseObject;
    }

    public GenericListRestResponse<OrgType> createGetSharedOrgUnitRequest(FederationMemberType federationMember) throws DatabaseCommunicationException {
        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = FederationServiceUtil.createGetSharedOrgUnitUrl(address, port, getLocalFederationMemberIdentifier());
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        GenericListRestResponse<OrgType> responseObject = new GenericListRestResponse<>();
        responseObject.setStatus(responseStatus);
        if(responseStatus == HttpStatus.OK_200){
              responseObject.setValues(jsonListToObject(responseMessage, OrgType[].class));
        } else {
            responseObject.setMessage(responseMessage);
        }

        return responseObject;
    }

    private String objectToJson(Object object){
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    private Object jsonToObject(String jsonObject, Class type){
        Gson gson = new Gson();
        return gson.fromJson(jsonObject, type);
    }

    /**
     *  Use this when you need to deserialize parametrized List from json String
     * */
    private <T extends Serializable> List<T> jsonListToObject(String jsonList, Class<T[]> type){
        T[] arr = new Gson().fromJson(jsonList, type);
        return Arrays.asList(arr);
    }
}
