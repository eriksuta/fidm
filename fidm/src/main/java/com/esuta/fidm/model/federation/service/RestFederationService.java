package com.esuta.fidm.model.federation.service;

import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.model.IModelService;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.esuta.fidm.repository.schema.core.SystemConfigurationType;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *  @author shood
 * */
@Path(FederationServiceUtil.REST_SERVICE_PATH)
public class RestFederationService implements IFederationService{

    Logger LOGGER = Logger.getLogger(RestFederationService.class);

    /**
     *  Single ModelService instance
     * */
    private static RestFederationService instance = null;
    private IModelService modelService;

    public static RestFederationService getInstance(){
        if(instance == null){
            instance = new RestFederationService();
        }

        return instance;
    }

    public RestFederationService(){
        initRestFederationService();
    }

    public void initRestFederationService(){
        this.modelService = ModelService.getInstance();
    }

    @GET
    @Path(FederationServiceUtil.GET_FEDERATION_MEMBER_IDENTIFIER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFederationIdentifier() {
        SystemConfigurationType config;

        try {
            config = modelService.readObject(SystemConfigurationType.class, PageBase.SYSTEM_CONFIG_UID);
        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not read system configuration: ", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Could not read system configuration.").build();
        }

        if(config != null){
            return Response.status(HttpStatus.OK_200).entity(config.getIdentityProviderIdentifier()).build();
        } else {
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Could not read system configuration.").build();
        }
    }

    @POST
    @Path(FederationServiceUtil.POST_FEDERATION_REQUEST)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleFederationRequest(FederationMembershipRequest membershipRequest){
        String remoteAddress = membershipRequest.getAddress();
        int remotePort = membershipRequest.getPort();
        String identifier = membershipRequest.getIdentityProviderIdentifier();

        if(identifier == null || identifier.isEmpty()){
            return Response.status(HttpStatus.BAD_REQUEST_400)
                    .entity("Request body does not contain required identity provider identifier name.").build();
        }

        LOGGER.debug("Federation request received. Request host address: " + remoteAddress + "(" + remotePort + "). " +
                "Identity provider ID: '" + identifier + "'.");

        try {
            List<FederationMemberType> federationMembers = modelService.getAllObjectsOfType(FederationMemberType.class);

            for(FederationMemberType federationMember: federationMembers){
                if(identifier.equals(federationMember.getFederationMemberName()) || identifier.equals(federationMember.getName())){
                    return Response.status(HttpStatus.CONFLICT_409)
                            .entity("Federation member with provided ID already exists").build();
                }
            }

            FederationMemberType newMember = new FederationMemberType();
            newMember.setName(identifier);
            newMember.setRequesterIdentifier(identifier);
            newMember.setFederationMemberName(identifier);
            newMember.setPort(remotePort);
            newMember.setWebAddress(remoteAddress);
            newMember.setStatus(FederationMemberType.FederationMemberStatusType.REQUESTED);

            newMember = modelService.createObject(newMember);
            LOGGER.info("Federation member with name: '" + newMember.getFederationMemberName() + "'(" + newMember.getUid() + ")");
            return Response.status(HttpStatus.OK_200).entity("Federation membership request handled correctly.").build();

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not load federation members from the repository.", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't read from the repository. Internal error: " + e).build();
        } catch (ObjectAlreadyExistsException e) {
            LOGGER.error("Could not save federation member to the repository. Federation member already exists.", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't save new federation membership request. Internal error: " + e).build();
        }
    }

    @POST
    @Path(FederationServiceUtil.POST_FEDERATION_REQUEST_RESPONSE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleFederationResponse(FederationMembershipRequest response){
        if(response == null || response.getIdentityProviderIdentifier() == null || response.getIdentityProviderIdentifier().isEmpty()
                || response.getResponse() == null){
            return Response.status(HttpStatus.BAD_REQUEST_400)
                    .entity("Request body does not contain required identity provider identifier name or request response.").build();
        }

        try {
            List<FederationMemberType> federationMembers = modelService.getAllObjectsOfType(FederationMemberType.class);

            FederationMemberType responseFederationMember = null;
            for(FederationMemberType federationMember: federationMembers){
                if(response.getIdentityProviderIdentifier().equals(federationMember.getRequesterIdentifier())){
                    responseFederationMember = federationMember;
                    break;
                }
            }

            if(responseFederationMember == null){
                return Response.status(HttpStatus.BAD_REQUEST_400)
                        .entity("Provided identity provider identifier does not contain valid " +
                                "federation membership request in this identity provider.").build();
            }

            if(response.getResponse().equals(FederationMembershipRequest.Response.ACCEPT)){
                responseFederationMember.setStatus(FederationMemberType.FederationMemberStatusType.AVAILABLE);
                LOGGER.info("Federation membership request for federation member: '" + responseFederationMember.getFederationMemberName() +
                        "'(" + responseFederationMember.getUid() + ") was accepted.");
            } else if(response.equals(FederationMembershipRequest.Response.DENY)){
                LOGGER.info("Federation membership request for federation member: '" + responseFederationMember.getFederationMemberName() +
                        "'(" + responseFederationMember.getUid() + ") was denied. Deleting federation member.");
                modelService.deleteObject(responseFederationMember);
            }

            return Response.status(HttpStatus.OK_200).entity("Response handled correctly.").build();

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not load federation members from the repository.", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't read from the repository. Internal error: " + e).build();
        } catch (ObjectNotFoundException e) {
            LOGGER.error("Could not remove federation member from the repository.", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't delete federation membership request. Internal error: " + e).build();
        }
    }

    @POST
    @Path(FederationServiceUtil.POST_FEDERATION_DELETION_REQUEST)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleFederationDeleteRequest(FederationMembershipRequest deletionRequest) {
        if(deletionRequest == null){
            return Response.status(HttpStatus.BAD_REQUEST_400)
                    .entity("Request body does not contain required identity provider identifier name.").build();
        }

        String remoteAddress = deletionRequest.getAddress();
        int remotePort = deletionRequest.getPort();
        String identifier = deletionRequest.getIdentityProviderIdentifier();

        if(identifier == null || identifier.isEmpty()){
            return Response.status(HttpStatus.BAD_REQUEST_400)
                    .entity("Request body does not contain required identity provider identifier name.").build();
        }

        LOGGER.debug("Federation deletion request received. Request host address: " + remoteAddress + "(" + remotePort + "). " +
                "Identity provider ID: '" + identifier + "'.");

        try {
            List<FederationMemberType> federationMembers = modelService.getAllObjectsOfType(FederationMemberType.class);

            FederationMemberType memberToDelete = null;
            for(FederationMemberType member: federationMembers){
                if(member.getFederationMemberName().equals(identifier)){
                    memberToDelete = member;
                }
            }

            if(memberToDelete == null){
                return Response.status(HttpStatus.BAD_REQUEST_400)
                        .entity("There is no federation membership between deletion request members. Nothing to delete.").build();
            }

            memberToDelete.setStatus(FederationMemberType.FederationMemberStatusType.DELETE_REQUESTED);
            modelService.updateObject(memberToDelete);
            LOGGER.info("Updating federation member: '" + memberToDelete.getFederationMemberName()
                    + "'(" + memberToDelete.getUid() + "). Adding status 'DELETE_REQUESTED'.");
            return Response.status(HttpStatus.OK_200).entity("Federation member delete request processed correctly.").build();

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not load federation members from the repository.", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't read from the repository. Internal problem: " + e).build();
        } catch (ObjectNotFoundException e) {
            LOGGER.error("Can't update federation member to delete. Internal problem:", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't update federation member to delete. Internal problem: " + e).build();
        }
    }

    @POST
    @Path(FederationServiceUtil.POST_FEDERATION_DELETION_RESPONSE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleFederationDeleteResponse(FederationMembershipRequest deletionResponse) {
        if(deletionResponse == null){
            return Response.status(HttpStatus.BAD_REQUEST_400)
                    .entity("Request body does not contain required identity provider identifier name and/or reaction.").build();
        }

        String remoteAddress = deletionResponse.getAddress();
        int remotePort = deletionResponse.getPort();
        String identifier = deletionResponse.getIdentityProviderIdentifier();

        if(identifier == null || identifier.isEmpty() || deletionResponse.getResponse() == null){
            return Response.status(HttpStatus.BAD_REQUEST_400)
                    .entity("Request body does not contain required identity provider identifier name and/or reaction.").build();
        }

        LOGGER.debug("Federation deletion request received. Request host address: " + remoteAddress + "(" + remotePort + "). " +
                "Identity provider ID: '" + identifier + "'.");

        try {
            List<FederationMemberType> federationMembers = modelService.getAllObjectsOfType(FederationMemberType.class);

            FederationMemberType memberToDelete = null;
            for(FederationMemberType member: federationMembers){
                if(member.getFederationMemberName().equals(identifier)){
                    memberToDelete = member;
                }
            }

            if(memberToDelete == null){
                return Response.status(HttpStatus.BAD_REQUEST_400)
                        .entity("There is no federation membership between deletion request members. Nothing to delete.").build();
            }

            modelService.deleteObject(memberToDelete);
            LOGGER.info("Federation member: '" + memberToDelete.getFederationMemberName()
                    + "'(" + memberToDelete.getUid() + ") deleted correctly.");
            return Response.status(HttpStatus.OK_200)
                    .entity("Deletion response processed correctly. Federation member deleted.").build();

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not load federation members from the repository.", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't read from the repository. Internal problem: " + e).build();
        } catch (ObjectNotFoundException e) {
            LOGGER.error("Can't delete federation member to delete. Internal problem:", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't delete federation member to delete. Internal problem: " + e).build();
        }
    }
}

