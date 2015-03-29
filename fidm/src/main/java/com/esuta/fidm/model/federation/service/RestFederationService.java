package com.esuta.fidm.model.federation.service;

import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.model.IModelService;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.util.JsonUtil;
import com.esuta.fidm.repository.schema.core.*;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
@Path(RestFederationServiceUtil.REST_SERVICE_PATH)
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

    private String getLocalFederationMemberIdentifier() throws DatabaseCommunicationException {
        SystemConfigurationType systemConfiguration = modelService.readObject(SystemConfigurationType.class, PageBase.SYSTEM_CONFIG_UID);
        return systemConfiguration.getIdentityProviderIdentifier();
    }

    private String getUniqueAttributeValue(ObjectType object, String uniqueAttributeName) throws NoSuchFieldException, IllegalAccessException {
        String attributeValue;

        Field attribute = object.getClass().getDeclaredField(uniqueAttributeName);
        attribute.setAccessible(true);
        attributeValue = (String)attribute.get(object);
        return attributeValue;
    }

    private OrgType getOrgUnitByUniqueAttributeValue(FederationMemberType member, String uniqueAttributeValue)
            throws NoSuchFieldException, DatabaseCommunicationException, IllegalAccessException {

        String uniqueAttributeName = member.getUniqueOrgIdentifier();

        List<OrgType> allOrgUnits = modelService.getAllObjectsOfType(OrgType.class);

        for(OrgType org: allOrgUnits){
            Field uniqueAttribute = org.getClass().getDeclaredField(uniqueAttributeName);
            uniqueAttribute.setAccessible(true);
            String attributeValue = (String)uniqueAttribute.get(org);

            if(uniqueAttributeValue.equals(attributeValue)){
                return org;
            }
        }

        return null;
    }

    private ObjectType getObjectFromUniqueValue(FederationMemberType member, String uniqueAttributeValue, String className)
            throws ClassNotFoundException, DatabaseCommunicationException, NoSuchFieldException, IllegalAccessException {

        String uniqueAttributeName;
        Class<?> clazz = Class.forName(className);
        List<? extends ObjectType> allObjectList;

        if(OrgType.class.equals(clazz)){
            uniqueAttributeName = member.getUniqueOrgIdentifier();
            allObjectList = modelService.getAllObjectsOfType(OrgType.class);
        } else if (UserType.class.equals(clazz)){
            uniqueAttributeName = member.getUniqueUserIdentifier();
            allObjectList = modelService.getAllObjectsOfType(UserType.class);
        } else if (RoleType.class.equals(clazz)){
            uniqueAttributeName = member.getUniqueRoleIdentifier();
            allObjectList = modelService.getAllObjectsOfType(RoleType.class);
        } else if (ResourceType.class.equals(clazz)){
            uniqueAttributeName = member.getUniqueResourceIdentifier();
            allObjectList = modelService.getAllObjectsOfType(ResourceType.class);
        } else {
            return null;
        }

        for(ObjectType obj: allObjectList){
            Field uniqueAttribute = obj.getClass().getDeclaredField(uniqueAttributeName);
            uniqueAttribute.setAccessible(true);
            String attributeValue = (String)uniqueAttribute.get(obj);

            if(uniqueAttributeValue.equals(attributeValue)){
                return obj;
            }
        }

        return null;
    }

    private void resolveOrgReferenceSharing(OrgType org){
        //Resolve parent org. ref sharing
        List<ObjectReferenceType<OrgType>> newParentOrgRefs = new ArrayList<>();
        for(ObjectReferenceType<OrgType> parentRef: org.getParentOrgUnits()){
            if(parentRef.isSharedInFederation()){
                newParentOrgRefs.add(parentRef);
            }
        }
        org.getParentOrgUnits().clear();
        org.getParentOrgUnits().addAll(newParentOrgRefs);

        //Resolve governors sharing
        List<ObjectReferenceType<UserType>> newGovernorRefs = new ArrayList<>();
        for(ObjectReferenceType<UserType> governorRef: org.getGovernors()){
            if(governorRef.isSharedInFederation()){
                newGovernorRefs.add(governorRef);
            }
        }
        org.getGovernors().clear();
        org.getGovernors().addAll(newGovernorRefs);

        //Resolve resource inducements sharing
        List<InducementType<ResourceType>> newResourceInducements = new ArrayList<>();
        for(InducementType<ResourceType> inducementRef: org.getResourceInducements()){
            if(inducementRef.isSharedInFederation()){
                newResourceInducements.add(inducementRef);
            }
        }
        org.getResourceInducements().clear();
        org.getResourceInducements().addAll(newResourceInducements);

        //Resolve role inducements sharing
        List<InducementType<RoleType>> newRoleInducements = new ArrayList<>();
        for(InducementType<RoleType> inducementRef: org.getRoleInducements()){
            if(inducementRef.isSharedInFederation()){
                newRoleInducements.add(inducementRef);
            }
        }
        org.getRoleInducements().clear();
        org.getRoleInducements().addAll(newRoleInducements);
    }

    private void prepareOrgReferences(OrgType org, FederationMemberType member)
            throws DatabaseCommunicationException, NoSuchFieldException, IllegalAccessException {

        for(ObjectReferenceType ref: org.getParentOrgUnits()){
            FederationIdentifierType identifier = new FederationIdentifierType();
            identifier.setObjectType(OrgType.class.getCanonicalName());

            OrgType parent = modelService.readObject(OrgType.class, ref.getUid());
            if(parent == null){
                continue;
            }

            identifier.setUniqueAttributeValue(getUniqueAttributeValue(parent, member.getUniqueOrgIdentifier()));
            identifier.setFederationMemberId(getLocalFederationMemberIdentifier());
            ref.setFederationIdentifier(identifier);
            ref.setUid(null);
        }

        for(ObjectReferenceType ref: org.getGovernors()){
            FederationIdentifierType identifier = new FederationIdentifierType();
            identifier.setObjectType(UserType.class.getCanonicalName());

            UserType user = modelService.readObject(UserType.class, ref.getUid());
            if(user == null){
                continue;
            }

            identifier.setUniqueAttributeValue(getUniqueAttributeValue(user, member.getUniqueUserIdentifier()));
            identifier.setFederationMemberId(getLocalFederationMemberIdentifier());
            ref.setFederationIdentifier(identifier);
            ref.setUid(null);
        }

        for(InducementType inducement: org.getResourceInducements()){
            FederationIdentifierType identifier = new FederationIdentifierType();
            identifier.setObjectType(ResourceType.class.getCanonicalName());

            ResourceType resource = modelService.readObject(ResourceType.class, inducement.getUid());
            if(resource == null){
                continue;
            }

            identifier.setUniqueAttributeValue(getUniqueAttributeValue(resource, member.getUniqueResourceIdentifier()));
            identifier.setFederationMemberId(getLocalFederationMemberIdentifier());
            inducement.setFederationIdentifier(identifier);
            inducement.setUid(null);
        }

        for(InducementType inducement: org.getRoleInducements()){
            FederationIdentifierType identifier = new FederationIdentifierType();
            identifier.setObjectType(RoleType.class.getCanonicalName());

            RoleType role = modelService.readObject(RoleType.class, inducement.getUid());
            if(role == null){
                continue;
            }

            identifier.setUniqueAttributeValue(getUniqueAttributeValue(role, member.getUniqueRoleIdentifier()));
            identifier.setFederationMemberId(getLocalFederationMemberIdentifier());
            inducement.setFederationIdentifier(identifier);
            inducement.setUid(null);
        }

        //Take care of sharing policy reference
        org.getSharingPolicy().setUid(null);
        org.getSharingPolicy().setType(null);

        //Take care of provisioning policy - this is never shared in federation,
        //so we always set the reference to null
        org.setProvisioningPolicy(null);
    }

    @GET
    @Path(RestFederationServiceUtil.GET_FEDERATION_MEMBER_IDENTIFIER)
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
    @Path(RestFederationServiceUtil.POST_FEDERATION_REQUEST)
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
    @Path(RestFederationServiceUtil.POST_FEDERATION_REQUEST_RESPONSE)
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
            } else if(response.getResponse().equals(FederationMembershipRequest.Response.DENY)){
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
    @Path(RestFederationServiceUtil.POST_FEDERATION_DELETION_REQUEST)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleFederationDeleteRequest(FederationMembershipRequest deletionRequest) {
        if(deletionRequest == null){
            return Response.status(HttpStatus.BAD_REQUEST_400)
                    .entity("Request body does not contain required identity provider identifier name.").build();
        }

        String identifier = deletionRequest.getIdentityProviderIdentifier();

        if(identifier == null || identifier.isEmpty()){
            return Response.status(HttpStatus.BAD_REQUEST_400)
                    .entity("Request body does not contain required identity provider identifier name.").build();
        }

        LOGGER.debug("Federation deletion request received. Request host address: " + "Identity provider ID: '" + identifier + "'.");

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
    @Path(RestFederationServiceUtil.POST_FEDERATION_DELETION_RESPONSE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleFederationDeleteResponse(FederationMembershipRequest deletionResponse) {
        if(deletionResponse == null){
            return Response.status(HttpStatus.BAD_REQUEST_400)
                    .entity("Request body does not contain required identity provider identifier name and/or reaction.").build();
        }

        String identifier = deletionResponse.getIdentityProviderIdentifier();

        if(identifier == null || identifier.isEmpty() || deletionResponse.getResponse() == null){
            return Response.status(HttpStatus.BAD_REQUEST_400)
                    .entity("Request body does not contain required identity provider identifier name and/or reaction.").build();
        }

        LOGGER.debug("Federation deletion request received. " + "Identity provider ID: '" + identifier + "'.");

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

    @GET
    @Path(RestFederationServiceUtil.GET_SHARED_ORG_UNIT_COUNT_PARAM)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSharedOrgUnitCount(@PathParam("memberIdentifier") String memberIdentifier) {
        if(memberIdentifier == null || memberIdentifier.isEmpty()){
            return Response.status(HttpStatus.BAD_REQUEST_400).entity("Bad or missing parameter.").build();
        }

        try {
            //TODO - as this is an often operation, consider moving it to separate method (not request, just method) + refactor
            //First, we need to find out, if there is an existing federation bond between requesting
            //and requested federation members
            List<FederationMemberType> federationMembers = modelService.getAllObjectsOfType(FederationMemberType.class);
            FederationMemberType currentMember = null;

            for(FederationMemberType member: federationMembers){
                if(memberIdentifier.equals(member.getFederationMemberName())){
                    currentMember = member;
                }
            }

            if(currentMember == null){
                LOGGER.error("No federation membership exists with requesting federation member: '" + memberIdentifier + "'.");
                return Response.status(HttpStatus.BAD_REQUEST_400)
                        .entity("No federation membership exists with requesting federation member: '" + memberIdentifier + "'.").build();
            }

            List<OrgType> orgUnits = modelService.getAllObjectsOfType(OrgType.class);
            int count = 0;

            for(OrgType org: orgUnits){
                if(org.isSharedInFederation()){
                    count++;
                }
            }

            return Response.status(HttpStatus.OK_200).entity(JsonUtil.objectToJson(count)).build();

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not load org. units from the repository.", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't read from the repository. Internal problem: " + e).build();
        }
    }

    @GET
    @Path(RestFederationServiceUtil.GET_SHARED_ORG_UNIT_PARAM)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSharedOrgUnits(@PathParam("memberIdentifier") String memberIdentifier) {
        if(memberIdentifier == null || memberIdentifier.isEmpty()){
            return Response.status(HttpStatus.BAD_REQUEST_400).entity("Bad or missing parameter.").build();
        }

        try {
            //TODO - as this is an often operation, consider moving it to separate method (not request, just method) + refactor
            List<FederationMemberType> federationMembers = modelService.getAllObjectsOfType(FederationMemberType.class);
            FederationMemberType currentMember = null;

            for(FederationMemberType member: federationMembers){
                if(memberIdentifier.equals(member.getFederationMemberName())){
                    currentMember = member;
                }
            }

            if(currentMember == null){
                LOGGER.error("No federation membership exists with requesting federation member: '" + memberIdentifier + "'.");
                return Response.status(HttpStatus.BAD_REQUEST_400)
                        .entity("No federation membership exists with requesting federation member: '" + memberIdentifier + "'.").build();
            }

            List<OrgType> orgUnits = modelService.getAllObjectsOfType(OrgType.class);
            List<OrgType> sharedOrgUnits = new ArrayList<>();

            for(OrgType org: orgUnits){
                if(org.isSharedInFederation()){
                    FederationIdentifierType federationIdentifier = new FederationIdentifierType();
                    federationIdentifier.setFederationMemberId(getLocalFederationMemberIdentifier());
                    federationIdentifier.setUniqueAttributeValue(getUniqueAttributeValue(org, currentMember.getUniqueOrgIdentifier()));
                    federationIdentifier.setObjectType(OrgType.class.getCanonicalName());
                    org.setFederationIdentifier(federationIdentifier);
                    org.setUid(null);
                    resolveOrgReferenceSharing(org);
                    prepareOrgReferences(org, currentMember);
                    sharedOrgUnits.add(org);
                }
            }

            return Response.status(HttpStatus.OK_200).entity(JsonUtil.objectToJson(sharedOrgUnits)).build();

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not load org. units from the repository.", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't read from the repository. Internal problem: " + e).build();
        } catch (IllegalAccessException  | NoSuchFieldException e) {
            LOGGER.error("Incorrect unique attribute for org. unit is set. Can't create unique identifier. ", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Incorrect unique attribute for org. unit is set. Can't create unique identifier. Reason: " + e).build();
        }
    }

    @GET
    @Path(RestFederationServiceUtil.GET_ORG_UNIT_PARAM)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrgUnit(@PathParam("memberIdentifier") String memberIdentifier, @PathParam("uniqueAttributeValue") String uniqueAttributeValue) {
        if(memberIdentifier == null || memberIdentifier.isEmpty() ||
                uniqueAttributeValue == null || uniqueAttributeValue.isEmpty()){
            return Response.status(HttpStatus.BAD_REQUEST_400).entity("Bad or missing parameter.").build();
        }

        try {
            //TODO - as this is an often operation, consider moving it to separate method (not request, just method) + refactor
            List<FederationMemberType> federationMembers = modelService.getAllObjectsOfType(FederationMemberType.class);
            FederationMemberType currentMember = null;

            for(FederationMemberType member: federationMembers){
                if(memberIdentifier.equals(member.getFederationMemberName())){
                    currentMember = member;
                }
            }

            if(currentMember == null){
                LOGGER.error("No federation membership exists with requesting federation member: '" + memberIdentifier + "'.");
                return Response.status(HttpStatus.BAD_REQUEST_400)
                        .entity("No federation membership exists with requesting federation member: '" + memberIdentifier + "'.").build();
            }

            OrgType org = getOrgUnitByUniqueAttributeValue(currentMember, uniqueAttributeValue);

            if(org == null){
                LOGGER.error("No org. unit exists with defined unique attribute value: " + uniqueAttributeValue);
                return Response.status(HttpStatus.BAD_REQUEST_400)
                        .entity("No org. unit exists with defined unique attribute value: " + uniqueAttributeValue).build();
            }

            resolveOrgReferenceSharing(org);
            prepareOrgReferences(org, currentMember);

            FederationIdentifierType federationIdentifier = new FederationIdentifierType();
            federationIdentifier.setFederationMemberId(getLocalFederationMemberIdentifier());
            federationIdentifier.setUniqueAttributeValue(getUniqueAttributeValue(org, currentMember.getUniqueOrgIdentifier()));
            federationIdentifier.setObjectType(OrgType.class.getCanonicalName());
            org.setFederationIdentifier(federationIdentifier);

            return Response.status(HttpStatus.OK_200).entity(JsonUtil.objectToJson(org)).build();

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not load org. unit from the repository.", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't read from the repository. Internal problem: " + e).build();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LOGGER.error("Incorrect unique attribute for org. unit is set. Can't find org. unique identifier. Reason: ", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Incorrect unique attribute for org. unit is set. Can't find org. unique identifier. Reason: " + e).build();
        }
    }

    @GET
    @Path(RestFederationServiceUtil.GET_OBJECT_INFORMATION_PARAM)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getObjectInformation(@PathParam("memberIdentifier")String memberIdentifier, @PathParam("uniqueAttributeValue")String uniqueAttributeValue,
                                         @PathParam("objectType")String objectType) {

        if(memberIdentifier == null || memberIdentifier.isEmpty() ||
                uniqueAttributeValue == null || uniqueAttributeValue.isEmpty() ||
                objectType == null || objectType.isEmpty()){
            return Response.status(HttpStatus.BAD_REQUEST_400).entity("Bad or missing parameter.").build();
        }

        try {
            //TODO - as this is an often operation, consider moving it to separate method (not request, just method) + refactor
            List<FederationMemberType> federationMembers = modelService.getAllObjectsOfType(FederationMemberType.class);
            FederationMemberType currentMember = null;

            for(FederationMemberType member: federationMembers){
                if(memberIdentifier.equals(member.getFederationMemberName())){
                    currentMember = member;
                }
            }

            if(currentMember == null){
                LOGGER.error("No federation membership exists with requesting federation member: '" + memberIdentifier + "'.");
                return Response.status(HttpStatus.BAD_REQUEST_400)
                        .entity("No federation membership exists with requesting federation member: '" + memberIdentifier + "'.").build();
            }

            ObjectType object = getObjectFromUniqueValue(currentMember, uniqueAttributeValue, objectType);

            if(object == null){
                LOGGER.error("No object exists with defined unique attribute value: " + uniqueAttributeValue);
                return Response.status(HttpStatus.BAD_REQUEST_400)
                        .entity("No object exists with defined unique attribute value: " + uniqueAttributeValue).build();
            }

            ObjectInformation informationObject = new ObjectInformation();
            informationObject.setObjectName(object.getName());
            informationObject.setObjectDescription(object.getDescription());

            return Response.status(HttpStatus.OK_200).entity(JsonUtil.objectToJson(informationObject)).build();

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not load requested object from the repository.", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't read from the repository. Internal problem: " + e).build();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            LOGGER.error("Incorrect unique attribute for object is set. Can't find the requested object identifier. Reason: ", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Incorrect unique attribute for object is set. Can't find the requested object identifier.. Reason: " + e).build();
        }
    }

    @GET
    @Path(RestFederationServiceUtil.GET_ORG_SHARING_POLICY_PARAM)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrgSharingPolicy(@PathParam("memberIdentifier")String memberIdentifier,
                                        @PathParam("uniqueAttributeValue")String uniqueAttributeValue) {

        if(memberIdentifier == null || memberIdentifier.isEmpty() ||
                uniqueAttributeValue == null || uniqueAttributeValue.isEmpty()){
            return Response.status(HttpStatus.BAD_REQUEST_400).entity("Bad or missing parameter.").build();
        }

        try {
            //TODO - as this is an often operation, consider moving it to separate method (not request, just method) + refactor
            List<FederationMemberType> federationMembers = modelService.getAllObjectsOfType(FederationMemberType.class);
            FederationMemberType currentMember = null;

            for(FederationMemberType member: federationMembers){
                if(memberIdentifier.equals(member.getFederationMemberName())){
                    currentMember = member;
                }
            }

            if(currentMember == null){
                LOGGER.error("No federation membership exists with requesting federation member: '" + memberIdentifier + "'.");
                return Response.status(HttpStatus.BAD_REQUEST_400)
                        .entity("No federation membership exists with requesting federation member: '" + memberIdentifier + "'.").build();
            }

            OrgType org = getOrgUnitByUniqueAttributeValue(currentMember, uniqueAttributeValue);

            if(org == null){
                LOGGER.error("No org. unit exists with defined unique attribute value: " + uniqueAttributeValue);
                return Response.status(HttpStatus.BAD_REQUEST_400)
                        .entity("No org. unit exists with defined unique attribute value: " + uniqueAttributeValue).build();
            }


            ObjectReferenceType sharingPolicyRef = org.getSharingPolicy();
            String sharingPolicyUid = sharingPolicyRef.getUid();

            FederationSharingPolicyType policy = modelService.readObject(FederationSharingPolicyType.class, sharingPolicyUid);

            if(policy == null){
                LOGGER.error("No sharing policy defined for requested org. unit: " + uniqueAttributeValue);
                return Response.status(HttpStatus.BAD_REQUEST_400)
                        .entity("No sharing policy defined for requested org. unit: " + uniqueAttributeValue).build();
            }

            return Response.status(HttpStatus.OK_200).entity(JsonUtil.objectToJson(policy)).build();

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not load org. unit or sharing policy from the repository.", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Can't read from the repository. Internal problem: " + e).build();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LOGGER.error("Incorrect unique attribute for org. unit is set. Can't find org. unique identifier. Reason: ", e);
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .entity("Incorrect unique attribute for org. unit is set. Can't find org. unique identifier. Reason: " + e).build();
        }
    }
}


