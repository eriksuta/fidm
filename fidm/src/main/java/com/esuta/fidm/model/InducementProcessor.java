package com.esuta.fidm.model;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.model.federation.client.ObjectTypeRestResponse;
import com.esuta.fidm.model.federation.client.RestFederationServiceClient;
import com.esuta.fidm.model.federation.client.SimpleRestResponse;
import com.esuta.fidm.repository.schema.core.*;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 *  This class is responsible for handling inducement-related behavior.
 *
 *  @author shood
 * */
public class InducementProcessor {

    private static final Logger LOGGER = Logger.getLogger(InducementProcessor.class);

    /**
     *  Single InducementProcessor instance
     * */
    private static InducementProcessor processor = null;

    private ModelService modelService;

    public static InducementProcessor getInstance(){
        if(processor == null){
            processor = new InducementProcessor();
        }

        return processor;
    }

    public void pushModelService(ModelService modelService){
        this.modelService = modelService;
    }

    public void handleUserInducements(UserType user) throws DatabaseCommunicationException {
        if(user.getUid() == null){
            handleNewUserInducements(user);
        } else {
            handleExistingUserInducements(user);
        }
    }

    private void handleNewUserInducements(UserType user) throws DatabaseCommunicationException {
        List<AssignmentType> userOrgAssignments = user.getOrgUnitAssignments();

        if(userOrgAssignments.isEmpty()){
            LOGGER.trace("Handled user with uid: '" + user.getName() + "' has no org. unit assignments. Inducement handling done.");
            return;
        }

        List<OrgType> userOrgUnits = retrieveOrgUnits(userOrgAssignments);

        for(OrgType org: userOrgUnits){
            handleNewUserResourceInducements(user, org.getResourceInducements());
            handleNewUserRoleInducements(user, org.getRoleInducements());
        }
    }

    private void handleExistingUserInducements(UserType user){
        List<AssignmentType> userOrgAssignments = user.getOrgUnitAssignments();

        if(userOrgAssignments.isEmpty()){
            LOGGER.trace("Handled user with uid: '" + user.getUid() + "'(" + user.getName() +
                    ") has no org. unit assignments. Inducement handling done.");
            return;
        }

        List<OrgType> userOrgUnits = retrieveOrgUnits(userOrgAssignments);

        for(OrgType org: userOrgUnits){
            handleExistingUserResourceInducements(user, org.getResourceInducements());
            handleExistingUserRoleInducements(user, org.getRoleInducements());
        }
    }

    private List<OrgType> retrieveOrgUnits(List<AssignmentType> orgUnitAssignments){
        List<OrgType> orgUnitList = new ArrayList<>();

        for(AssignmentType assignment: orgUnitAssignments){
            try {
                orgUnitList.add(modelService.readObject(OrgType.class, assignment.getUid()));
            } catch (DatabaseCommunicationException e) {
                LOGGER.error("Could not retrieve org. unit with UID: '" + assignment.getUid() + "'.", e);
            }
        }

        return orgUnitList;
    }

    private void handleExistingUserResourceInducements(UserType user, List<InducementType> resourceInducementIdentifiers){
        List<InducementType> localInducements = new ArrayList<>();
        List<InducementType> remoteInducements = new ArrayList<>();

        for(InducementType inducement: resourceInducementIdentifiers){
            if(inducement.getUid() == null && inducement.getFederationIdentifier() != null){
                remoteInducements.add(inducement);
            } else if(inducement.getUid() != null && inducement.getFederationIdentifier() == null){
                localInducements.add(inducement);
            } else {
                LOGGER.error("Invalid inducement state: " + inducement);
                //This should not happen
            }
        }

        handleExistingUserLocalResourceInducements(user, localInducements);
        handleExistingUserRemoteResourceInducements(user, remoteInducements);
    }

    private void handleExistingUserRemoteResourceInducements(UserType user, List<InducementType> resourceInducementIdentifiers){
        List<AccountType> remoteAccounts = new ArrayList<>();
        List<FederationIdentifierType> resourcesWithAccounts = new ArrayList<>();
        List<FederationIdentifierType> resourcesWithoutAccounts = new ArrayList<>();

        // Retrieve existing accounts of handled user
        for(AssignmentType assignment: user.getAccounts()){
            FederationIdentifierType identifier = assignment.getFederationIdentifier();

            FederationMemberType member = WebMiscUtil.getFederationMemberByName(identifier.getFederationMemberId());

            try {
                ObjectTypeRestResponse response = RestFederationServiceClient.getInstance()
                        .createGetAccountRequest(member, identifier.getUniqueAttributeValue());

                if(response.getStatus() == HttpStatus.OK_200){
                    remoteAccounts.add((AccountType)response.getValue());
                }

            } catch (DatabaseCommunicationException | NoSuchFieldException | IllegalAccessException e) {
                LOGGER.error("Could not find out, if user '" + user.getName() + "' has account on remote resource: '" +
                        identifier.getUniqueAttributeValue() + "'. Reason: ", e);
            }
        }

        for(AccountType account: remoteAccounts){
            resourcesWithAccounts.add(account.getResource().getFederationIdentifier());
        }

        // Find out, if we need to create any new account enforced by inducement
        for(InducementType resourceInducement: resourceInducementIdentifiers){
            FederationIdentifierType uniqueResourceIdentifier = resourceInducement.getFederationIdentifier();

            if(!resourcesWithAccounts.contains(uniqueResourceIdentifier)){
                resourcesWithoutAccounts.add(uniqueResourceIdentifier);
            }
        }

        // Create missing accounts
        for(FederationIdentifierType resourceReference: resourcesWithoutAccounts){

            try {
                SimpleRestResponse response = RestFederationServiceClient.getInstance().createAccountRequest(WebMiscUtil.getFederationMemberByName(resourceReference.getFederationMemberId()),
                        resourceReference.getUniqueAttributeValue(), user);

                if(HttpStatus.OK_200 != response.getStatus()){
                    LOGGER.error("Account not created on remote resource. Error: " + response.getMessage());
                    continue;
                }

                AssignmentType assignment = new AssignmentType();
                assignment.setAssignedByInducement(true);
                assignment.setShareInFederation(true);

                FederationIdentifierType federationIdentifier = new FederationIdentifierType();
                federationIdentifier.setObjectType(AccountType.class.getCanonicalName());
                federationIdentifier.setFederationMemberId(resourceReference.getFederationMemberId());
                federationIdentifier.setUniqueAttributeValue(response.getMessage());
                assignment.setFederationIdentifier(federationIdentifier);
                user.getAccounts().add(assignment);

            } catch (NoSuchFieldException | IllegalAccessException | DatabaseCommunicationException e) {
                LOGGER.error("Could not create a request to create a remote account for user.");
            }
        }

        //Remove accounts, if necessary
        List<FederationIdentifierType> remoteResourceInducementIdentifiers = new ArrayList<>();
        for(InducementType inducement: resourceInducementIdentifiers){
            remoteResourceInducementIdentifiers.add(inducement.getFederationIdentifier());
        }

        try {

            List<AssignmentType> assignmentsToRemove = new ArrayList<>();
            for(AssignmentType assignment: user.getAccounts()){
                FederationIdentifierType identifier = assignment.getFederationIdentifier();

                FederationMemberType member = WebMiscUtil.getFederationMemberByName(identifier.getFederationMemberId());

                ObjectTypeRestResponse response = RestFederationServiceClient.getInstance()
                        .createGetAccountRequest(member, identifier.getUniqueAttributeValue());

                if(HttpStatus.OK_200 == response.getStatus()){
                    AccountType account = (AccountType)response.getValue();

                    FederationIdentifierType remoteResourceIdentifier = account.getResource().getFederationIdentifier();

                    if(!remoteResourceInducementIdentifiers.contains(remoteResourceIdentifier) && assignment.isAssignedByInducement()){
                        assignmentsToRemove.add(assignment);
                    }
                }
            }

            for(AssignmentType assignmentToRemove: assignmentsToRemove){
                FederationIdentifierType identifier = assignmentToRemove.getFederationIdentifier();
                FederationMemberType member = WebMiscUtil.getFederationMemberByName(identifier.getFederationMemberId());

                SimpleRestResponse response = RestFederationServiceClient.getInstance()
                        .createRemoveAccountRequest(member, identifier.getUniqueAttributeValue());

                LOGGER.info("Remove account removal operation progress: Status[" + response.getStatus() + "], Message[" + response.getMessage() + "]");

                user.getAccounts().remove(assignmentToRemove);
                LOGGER.debug("Removing assignment with uid: '" + assignmentToRemove.getUid() + "' from user with uid: '" + user.getName() + "'.");
            }
        } catch (DatabaseCommunicationException | NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Could not remove remote account for some reason. Whatever.");
        }
    }

    private void handleExistingUserLocalResourceInducements(UserType user, List<InducementType> resourceInducementIdentifiers){
        List<AccountType> userAccounts = new ArrayList<>();
        List<ObjectReferenceType> resourcesWithAccounts = new ArrayList<>();
        List<ObjectReferenceType> resourcesWithoutAccounts = new ArrayList<>();

        // Retrieve existing accounts of handled user
        for(AssignmentType assignment: user.getAccounts()){
            try {
                if(assignment.getUid() != null) {
                    userAccounts.add(modelService.readObject(AccountType.class, assignment.getUid()));
                }
            } catch (DatabaseCommunicationException e) {
                LOGGER.error("Could not retrieve account with uid: '" + assignment.getUid() + "' belonging to user: '" + user.getName() + "'.", e);
            }
        }

        for(AccountType account: userAccounts){
            resourcesWithAccounts.add(account.getResource());
        }

        // Find out, if we need to create any new account enforced by inducement
        for(InducementType resourceInducement: resourceInducementIdentifiers){
            ObjectReferenceType resourceReference = new ObjectReferenceType(resourceInducement.getUid());

            if(!resourcesWithAccounts.contains(resourceReference)){
                resourcesWithoutAccounts.add(resourceReference);
            }
        }

        // Create missing accounts
        for(ObjectReferenceType resourceReference: resourcesWithoutAccounts){
            String resourceUid = resourceReference.getUid();

            AccountType newAccount = new AccountType();
            newAccount.setResource(resourceReference);
            newAccount.setPassword(user.getPassword());

            ObjectReferenceType ownerReference = new ObjectReferenceType(user.getUid());
            newAccount.setOwner(ownerReference);

            try {
                newAccount = modelService.createObject(newAccount);

                AssignmentType accountAssignment = new AssignmentType(newAccount.getUid());
                accountAssignment.setAssignedByInducement(true);
                user.getAccounts().add(accountAssignment);
                LOGGER.debug("New account with uid: '" + newAccount.getUid() + "' created on resource: '" + resourceUid + "' for user: '" + user.getUid() + "'.");
            } catch (ObjectAlreadyExistsException | DatabaseCommunicationException e) {
                LOGGER.error("Could not create new account for user: '" + user.getUid() + "' on resource: '" + resourceUid + "'. ", e);
            }
        }

        //Remove accounts, if necessary
        List<String> resourceInducementUids = new ArrayList<>();
        for(InducementType inducement: resourceInducementIdentifiers){
            resourceInducementUids.add(inducement.getUid());
        }

        try {
            List<AssignmentType> assignmentsToRemove = new ArrayList<>();
            for(AssignmentType assignment: user.getAccounts()){

                if(assignment.getUid() != null) {
                    AccountType account = modelService.readObject(AccountType.class, assignment.getUid());
                    String resourceUid = account.getResource().getUid();

                    if (!resourceInducementUids.contains(resourceUid) && assignment.isAssignedByInducement()) {
                        assignmentsToRemove.add(assignment);
                    }
                }
            }

            for(AssignmentType assignmentToRemove: assignmentsToRemove){
                AccountType account = modelService.readObject(AccountType.class, assignmentToRemove.getUid());
                modelService.deleteObject(account);

                user.getAccounts().remove(assignmentToRemove);
                LOGGER.debug("Removing rp;e assignment with uid: '" + assignmentToRemove.getUid() + "' from user with uid: '" + user.getName() + "'.");
            }
        } catch (DatabaseCommunicationException | ObjectNotFoundException e) {
            LOGGER.error("Could not remove an account for user: '" + user.getUid() + "Reason: ", e);
        }
    }

    private void handleNewUserResourceInducements(UserType user, List<InducementType> resourceIdentifiers)
            throws DatabaseCommunicationException {

        for(InducementType inducement: resourceIdentifiers) {
            if (inducement.getFederationIdentifier() == null) {

                String inducementUid = inducement.getUid();
                AccountType newAccount = new AccountType();

                ObjectReferenceType resourceReference = new ObjectReferenceType(inducementUid);
                newAccount.setResource(resourceReference);
                newAccount.setPassword(user.getPassword());

                ObjectReferenceType ownerReference = new ObjectReferenceType(user.getUid());
                newAccount.setOwner(ownerReference);
                newAccount.setName(user.getName());

                try {
                    newAccount = modelService.createObject(newAccount);

                    AssignmentType accountAssignment = new AssignmentType(newAccount.getUid());
                    accountAssignment.setAssignedByInducement(true);
                    user.getAccounts().add(accountAssignment);
                    LOGGER.debug("New account with uid: '" + newAccount.getUid() + "' created on resource: '" + inducementUid + "' for user: '" + user.getName() + "'.");
                } catch (ObjectAlreadyExistsException | DatabaseCommunicationException e) {
                    LOGGER.error("Could not create new account for user: '" + user.getName() + "' on resource: '" + inducementUid + "'. ", e);
                }
            } else {
                FederationIdentifierType resourceIdentifier = inducement.getFederationIdentifier();

                try {
                    SimpleRestResponse response = RestFederationServiceClient.getInstance().createAccountRequest(
                            WebMiscUtil.getFederationMemberByName(resourceIdentifier.getFederationMemberId()),
                            resourceIdentifier.getUniqueAttributeValue(), user);

                    if(HttpStatus.OK_200 != response.getStatus()){
                        LOGGER.error("Account not created on remote resource. Error: " + response.getMessage());
                        continue;
                    }

                    AssignmentType assignment = new AssignmentType();
                    assignment.setAssignedByInducement(true);
                    assignment.setShareInFederation(true);

                    FederationIdentifierType federationIdentifier = new FederationIdentifierType();
                    federationIdentifier.setObjectType(AccountType.class.getCanonicalName());
                    federationIdentifier.setFederationMemberId(resourceIdentifier.getFederationMemberId());
                    federationIdentifier.setUniqueAttributeValue(response.getMessage());
                    assignment.setFederationIdentifier(federationIdentifier);
                    user.getAccounts().add(assignment);

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    LOGGER.error("Could not create a request to create a remote account for user.");
                }
            }
        }
    }

    private void handleExistingUserRoleInducements(UserType user, List<InducementType> roleIdentifiers){
        List<String> userRoleAssignmentUids = new ArrayList<>();
        for(AssignmentType assignment: user.getRoleAssignments()){
            userRoleAssignmentUids.add(assignment.getUid());
        }

        //Create role assignments, if needed
        for(InducementType roleInducement: roleIdentifiers){

            if(!userRoleAssignmentUids.contains(roleInducement.getUid())){
                AssignmentType newAssignment = new AssignmentType(roleInducement.getUid());
                newAssignment.setAssignedByInducement(true);
                user.getRoleAssignments().add(newAssignment);
                LOGGER.debug("New assignment of role with uid: '" + roleInducement.getUid() + "' added to user with uid: '" + user.getName() + "'.");
            }
        }

        //Remove inducement enforced role assignments, if there are any
        List<String> roleInducementUids = new ArrayList<>();
        for(InducementType inducement: roleIdentifiers){
            roleInducementUids.add(inducement.getUid());
        }

        List<AssignmentType> assignmentsToRemove = new ArrayList<>();
        for(AssignmentType roleAssignment: user.getRoleAssignments()){
            if(!roleInducementUids.contains(roleAssignment.getUid()) && roleAssignment.isAssignedByInducement()){
                assignmentsToRemove.add(roleAssignment);
            }
        }

        for(AssignmentType assignmentToRemove: assignmentsToRemove){
            user.getRoleAssignments().remove(assignmentToRemove);
            LOGGER.debug("Removing rp;e assignment with uid: '" + assignmentToRemove.getUid() + "' from user with uid: '" + user.getName() + "'.");
        }
    }

    private void handleNewUserRoleInducements(UserType user, List<InducementType> roleInducements){
        for(InducementType inducement: roleInducements){
            AssignmentType roleAssignment = new AssignmentType(inducement.getUid());
            roleAssignment.setAssignedByInducement(true);
            user.getRoleAssignments().add(roleAssignment);
            LOGGER.debug("New assignment of role with uid: '" + inducement.getUid() + "' added to user with uid: '" + user.getUid() + "'.");
        }
    }
}
