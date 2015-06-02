package com.esuta.fidm.model.federation.client;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.model.IModelService;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.auth.AuthResult;
import com.esuta.fidm.model.federation.service.*;
import com.esuta.fidm.model.util.JsonUtil;
import com.esuta.fidm.repository.schema.core.*;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import com.esuta.fidm.repository.schema.support.ObjectModificationType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 *  @author shood
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

        String url = RestFederationServiceUtil.createGetFederationMemberIdentifier(address, port);
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

        String url = RestFederationServiceUtil.createFederationRequestUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        FederationMembershipRequest request = new FederationMembershipRequest();
        request.setIdentityProviderIdentifier(federationMember.getRequesterIdentifier());
        request.setPort(localPort);
        request.setAddress(localAddress);

        String jsonRequest = JsonUtil.objectToJson(request);

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

        String url = RestFederationServiceUtil.createFederationRequestResponseUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        FederationMembershipRequest responseObject = new FederationMembershipRequest();
        responseObject.setIdentityProviderIdentifier(federationMember.getRequesterIdentifier());
        responseObject.setResponse(responseType);
        String responseObjectJson = JsonUtil.objectToJson(responseObject);

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

        String url = RestFederationServiceUtil.createFederationDeleteRequestUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        FederationMembershipRequest request = new FederationMembershipRequest();
        request.setIdentityProviderIdentifier(getLocalFederationMemberIdentifier());

        String jsonRequest = JsonUtil.objectToJson(request);

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

        String url = RestFederationServiceUtil.createFederationDeleteResponseUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        FederationMembershipRequest responseObject = new FederationMembershipRequest();
        responseObject.setIdentityProviderIdentifier(getLocalFederationMemberIdentifier());
        responseObject.setResponse(responseType);
        String responseObjectJson = JsonUtil.objectToJson(responseObject);

        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, responseObjectJson);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponse(responseStatus, responseMessage);
    }

    public IntegerRestResponse createGetSharedOrgUnitCountRequest(FederationMemberType federationMember) throws DatabaseCommunicationException {
        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = RestFederationServiceUtil.createGetSharedOrgUnitCountUrl(address, port, getLocalFederationMemberIdentifier());
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        IntegerRestResponse responseObject = new IntegerRestResponse();
        responseObject.setStatus(responseStatus);
        if(responseStatus == HttpStatus.OK_200){
            responseObject.setValue((Integer)JsonUtil.jsonToObject(responseMessage, Integer.class));
        } else {
            responseObject.setMessage(responseMessage);
        }

        return responseObject;
    }

    public GenericListRestResponse<OrgType> createGetSharedOrgUnitRequest(FederationMemberType federationMember) throws DatabaseCommunicationException {
        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = RestFederationServiceUtil.createGetSharedOrgUnitUrl(address, port, getLocalFederationMemberIdentifier());
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        GenericListRestResponse<OrgType> responseObject = new GenericListRestResponse<>();
        responseObject.setStatus(responseStatus);
        if(responseStatus == HttpStatus.OK_200){
              responseObject.setValues(JsonUtil.jsonListToObject(responseMessage, OrgType[].class));
        } else {
            responseObject.setMessage(responseMessage);
        }

        return responseObject;
    }

    public ObjectTypeRestResponse<OrgType> createGetOrgUnitRequest(FederationMemberType federationMember, FederationIdentifierType federationIdentifier)
            throws DatabaseCommunicationException {

        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = RestFederationServiceUtil.createGetOrgUnitUrl(address, port,
                getLocalFederationMemberIdentifier(), federationIdentifier.getUniqueAttributeValue());
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        ObjectTypeRestResponse<OrgType> responseObject = new ObjectTypeRestResponse<>();
        responseObject.setStatus(responseStatus);
        if(responseStatus == HttpStatus.OK_200){
            responseObject.setValue((OrgType) JsonUtil.jsonToObject(responseMessage, OrgType.class));
        } else {
            responseObject.setMessage(responseMessage);
        }

        return responseObject;
    }

    public ObjectInformationResponse createGetObjectInformationRequest(FederationMemberType federationMember, FederationIdentifierType federationIdentifier)
            throws DatabaseCommunicationException {

        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = RestFederationServiceUtil.createGetObjectInformationUrl(address, port,
                getLocalFederationMemberIdentifier(), federationIdentifier.getUniqueAttributeValue(), federationIdentifier.getObjectType());
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        ObjectInformationResponse responseObject = new ObjectInformationResponse();
        responseObject.setStatus(responseStatus);
        if(responseStatus == HttpStatus.OK_200){
            responseObject.setInformationObject((ObjectInformation) JsonUtil.jsonToObject(responseMessage, ObjectInformation.class));
        } else {
            responseObject.setMessage(responseMessage);
        }

        return responseObject;
    }

    public ObjectTypeRestResponse<SharingPolicyType> createGetOrgSharingPolicyRequest(FederationMemberType federationMember, FederationIdentifierType federationIdentifier)
            throws DatabaseCommunicationException {

        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = RestFederationServiceUtil.createGetOrgSharingPolicyUrl(address, port,
                getLocalFederationMemberIdentifier(), federationIdentifier.getUniqueAttributeValue());
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        ObjectTypeRestResponse<SharingPolicyType> responseObject = new ObjectTypeRestResponse<>();
        responseObject.setStatus(responseStatus);
        if(responseStatus == HttpStatus.OK_200){
            responseObject.setValue((SharingPolicyType) JsonUtil.jsonToObject(responseMessage, SharingPolicyType.class));
        } else {
            responseObject.setMessage(responseMessage);
        }

        return responseObject;
    }

//    public SimpleRestResponse createPostOrgChangesRequest(FederationMemberType federationMember,
//                                                          FederationIdentifierType federationIdentifier,
//                                                          ObjectModificationType modificationObject) throws DatabaseCommunicationException {
//
//        String address = federationMember.getWebAddress();
//        int port = federationMember.getPort();
//
//        String url = RestFederationServiceUtil.createPostProcessOrgChangesRequestUrl(address, port);
//        Client client = Client.create();
//        WebResource webResource = client.resource(url);
//
//        OrgModificationWrapper requestObject = new OrgModificationWrapper();
//        requestObject.setUniqueAttributeValue(federationIdentifier.getUniqueAttributeValue());
//        requestObject.setFederationMember(getLocalFederationMemberIdentifier());
//        requestObject.setModificationObject(modificationObject);
//
//        String jsonRequest = JsonUtil.objectToJson(requestObject);
//
//        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonRequest);
//
//        int responseStatus = response.getStatus();
//        String responseMessage = response.getEntity(String.class);
//        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);
//
//        return new SimpleRestResponse(responseStatus, responseMessage);
//    }

    public SimpleRestResponse createPostOrgChangesRequest(FederationMemberType federationMember,
                                                          String orgUniqueAttributeValue,
                                                          ObjectModificationType modificationObject) throws DatabaseCommunicationException {

        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = RestFederationServiceUtil.createPostProcessOrgChangesRequestUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        OrgModificationWrapper requestObject = new OrgModificationWrapper();
        requestObject.setUniqueAttributeValue(orgUniqueAttributeValue);
        requestObject.setFederationMember(getLocalFederationMemberIdentifier());
        requestObject.setModificationObject(modificationObject);

        String jsonRequest = JsonUtil.objectToJson(requestObject);

        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonRequest);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponse(responseStatus, responseMessage);
    }

    public SimpleRestResponse createRemoveOrgLinkRequest(FederationMemberType federationMember,
                                                         FederationIdentifierType federationIdentifier) throws DatabaseCommunicationException {

        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = RestFederationServiceUtil.createGetRemoveOrgLinkUrl(address, port,
                getLocalFederationMemberIdentifier(), federationIdentifier.getUniqueAttributeValue());
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponse(responseStatus, responseMessage);
    }

    public SimpleRestResponse createRemoveOriginOrgRequest(FederationMemberType federationMember,
                                                           FederationIdentifierType federationIdentifier) throws DatabaseCommunicationException {

        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = RestFederationServiceUtil.createGetRemoveOriginOrgkUrl(address, port,
                getLocalFederationMemberIdentifier(), federationIdentifier.getUniqueAttributeValue());
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponse(responseStatus, responseMessage);
    }

    public SimpleRestResponse createAccountRequest(FederationMemberType member, String uniqueAttributeValue, UserType user)
            throws DatabaseCommunicationException, NoSuchFieldException, IllegalAccessException {

        String address = member.getWebAddress();
        int port = member.getPort();

        AccountRequestWrapper accountWrapper = new AccountRequestWrapper();
        accountWrapper.setMemberIdentifier(WebMiscUtil.getLocalFederationMemberIdentifier());
        accountWrapper.setAccountName(user.getName());
        accountWrapper.setPassword(user.getPassword());
        accountWrapper.setResourceUniqueAttributeValue(uniqueAttributeValue);

        FederationIdentifierType ownerIdentifier = new FederationIdentifierType();
        ownerIdentifier.setFederationMemberId(WebMiscUtil.getLocalFederationMemberIdentifier());
        ownerIdentifier.setObjectType(UserType.class.getCanonicalName());
        ownerIdentifier.setUniqueAttributeValue(WebMiscUtil.getUniqueAttributeValue(user, member.getUniqueUserIdentifier()));
        accountWrapper.setOwnerIdentifier(ownerIdentifier);

        String url = RestFederationServiceUtil.createPostRequestAccountUrl(address, port);
        Client client = Client.create();
        WebResource webResource = client.resource(url);
        String jsonObject = JsonUtil.objectToJson(accountWrapper);

        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonObject);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        return new SimpleRestResponse(responseStatus, responseMessage);
    }

    public ObjectTypeRestResponse createGetAccountRequest(FederationMemberType member, String accountIdentifier)
            throws DatabaseCommunicationException, NoSuchFieldException, IllegalAccessException {

        String address = member.getWebAddress();
        int port = member.getPort();

        String url = RestFederationServiceUtil.createGetAccountUrl(address, port,
                WebMiscUtil.getLocalFederationMemberIdentifier(), accountIdentifier);

        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + responseStatus + ", message: " + responseMessage);

        if(HttpStatus.OK_200 == responseStatus){
            ObjectTypeRestResponse accountResponse = new ObjectTypeRestResponse<>((AccountType)JsonUtil.jsonToObject(responseMessage, AccountType.class));
            accountResponse.setStatus(responseStatus);
            return accountResponse;
        } else {
            return new ObjectTypeRestResponse<>(responseStatus, responseMessage);
        }
    }

    public SimpleRestResponse createRemoveAccountRequest(FederationMemberType member, String accountIdentifier)
            throws DatabaseCommunicationException, NoSuchFieldException, IllegalAccessException {

        String address = member.getWebAddress();
        int port = member.getPort();

        String url = RestFederationServiceUtil.createGetRemoveAccountUrl(address, port,
                WebMiscUtil.getLocalFederationMemberIdentifier(), accountIdentifier);

        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + responseStatus + ", message: " + responseMessage);

        return new SimpleRestResponse(responseStatus, responseMessage);
    }

    public GenericListRestResponse<UserType> createGetOrgUnitMembersRequest(FederationMemberType federationMember, String orgIdentifier)
            throws DatabaseCommunicationException {

        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = RestFederationServiceUtil.createGetOrgMembersUrl(address, port,
                getLocalFederationMemberIdentifier(), orgIdentifier);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        GenericListRestResponse<UserType> responseObject = new GenericListRestResponse<>();
        responseObject.setStatus(responseStatus);
        if(responseStatus == HttpStatus.OK_200){
            responseObject.setValues(JsonUtil.jsonListToObject(responseMessage, UserType[].class));
        } else {
            responseObject.setMessage(responseMessage);
        }

        return responseObject;
    }

    public GenericListRestResponse<String> createGetAvailableResourcesRequest(FederationMemberType federationMember)
            throws DatabaseCommunicationException {

        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = RestFederationServiceUtil.createGetAvailableResourcesUrl(address, port,
                getLocalFederationMemberIdentifier());
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        GenericListRestResponse<String> responseObject = new GenericListRestResponse<>();
        responseObject.setStatus(responseStatus);
        if(responseStatus == HttpStatus.OK_200){
            responseObject.setValues(JsonUtil.jsonListToObject(responseMessage, String[].class));
        } else {
            responseObject.setMessage(responseMessage);
        }

        return responseObject;
    }

    public AuthRestResponse createGetRemoteLoginRequest(FederationMemberType federationMember,
                                                        String resourceName, String accountName, String password)
            throws DatabaseCommunicationException {

        String address = federationMember.getWebAddress();
        int port = federationMember.getPort();

        String url = RestFederationServiceUtil.createGetRemoteLoginUrl(address, port,
                getLocalFederationMemberIdentifier(), resourceName, accountName, password);
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        int responseStatus = response.getStatus();
        String responseMessage = response.getEntity(String.class);
        LOGGER.info("Response status: " + response.getStatus() + ", message: " + responseMessage);

        if(responseStatus == HttpStatus.OK_200){
            return new AuthRestResponse(responseStatus, (AuthResult)JsonUtil.jsonToObject(responseMessage, AuthResult.class));
        } else {
            return new AuthRestResponse(responseStatus, responseMessage);
        }
    }
}
