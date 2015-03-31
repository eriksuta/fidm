package com.esuta.fidm.model;

import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.repository.schema.core.*;
import org.apache.log4j.Logger;

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

    private InducementProcessor(){}

    public static InducementProcessor getInstance(){
        if(processor == null){
            processor = new InducementProcessor();
        }

        return processor;
    }

    public void pushModelService(ModelService modelService){
        this.modelService = modelService;
    }

    public void handleUserInducements(UserType user){
        if(user.getUid() == null){
            handleNewUserInducements(user);
        } else {
            handleExistingUserInducements(user);
        }
    }

    private void handleNewUserInducements(UserType user){
        List<AssignmentType<OrgType>> userOrgAssignments = user.getOrgUnitAssignments();

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
        List<AssignmentType<OrgType>> userOrgAssignments = user.getOrgUnitAssignments();

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

    private List<OrgType> retrieveOrgUnits(List<AssignmentType<OrgType>> orgUnitAssignments){
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

    private void handleExistingUserResourceInducements(UserType user, List<InducementType<ResourceType>> resourceInducementIdentifiers){
        List<AccountType> userAccounts = new ArrayList<>();
        List<ObjectReferenceType<ResourceType>> resourcesWithAccounts = new ArrayList<>();
        List<ObjectReferenceType<ResourceType>> resourcesWithoutAccounts = new ArrayList<>();

        // Retrieve existing accounts of handled user
        for(AssignmentType accountReferences: user.getAccounts()){
            try {
                userAccounts.add(modelService.readObject(AccountType.class, accountReferences.getUid()));
            } catch (DatabaseCommunicationException e) {
                LOGGER.error("Could not retrieve account with uid: '" + accountReferences.getUid() + "' belonging to user: '" + user.getName() + "'.", e);
            }
        }

        for(AccountType account: userAccounts){
            resourcesWithAccounts.add(account.getResource());
        }

        // Find out, if we need to create any new account enforced by inducement
        for(InducementType<ResourceType> resourceInducement: resourceInducementIdentifiers){
            ObjectReferenceType<ResourceType> resourceReference = new ObjectReferenceType<>(resourceInducement.getUid(), ResourceType.class);

            if(!resourcesWithAccounts.contains(resourceReference)){
                resourcesWithoutAccounts.add(resourceReference);
            }
        }

        // Create missing accounts
        for(ObjectReferenceType<ResourceType> resourceReference: resourcesWithoutAccounts){
            String resourceUid = resourceReference.getUid();

            AccountType newAccount = new AccountType();
            newAccount.setResource(resourceReference);

            ObjectReferenceType<UserType> ownerReference = new ObjectReferenceType<>(user.getUid(), UserType.class);
            newAccount.setOwner(ownerReference);
            newAccount.setName(user.getName());

            try {
                newAccount = modelService.createObject(newAccount);

                AssignmentType<AccountType> accountAssignment = new AssignmentType<>(newAccount.getUid(), AccountType.class);
                user.getAccounts().add(accountAssignment);
                LOGGER.debug("New account with uid: '" + newAccount.getUid() + "' created on resource: '" + resourceUid + "' for user: '" + user.getUid() + "'.");
            } catch (ObjectAlreadyExistsException | DatabaseCommunicationException e) {
                LOGGER.error("Could not create new account for user: '" + user.getUid() + "' on resource: '" + resourceUid + "'. ", e);
            }
        }
    }

    private void handleNewUserResourceInducements(UserType user, List<InducementType<ResourceType>> resourceIdentifiers){
        for(InducementType inducement: resourceIdentifiers){
            String inducementUid = inducement.getUid();
            AccountType newAccount = new AccountType();

            ObjectReferenceType<ResourceType> resourceReference = new ObjectReferenceType<>(inducementUid, ResourceType.class);
            newAccount.setResource(resourceReference);

            ObjectReferenceType<UserType> ownerReference = new ObjectReferenceType<>(user.getUid(), UserType.class);
            newAccount.setOwner(ownerReference);
            newAccount.setName(user.getName());

            try {
                newAccount = modelService.createObject(newAccount);

                AssignmentType<AccountType> accountAssignment = new AssignmentType<>(newAccount.getUid(), AccountType.class);
                user.getAccounts().add(accountAssignment);
                LOGGER.debug("New account with uid: '" + newAccount.getUid() + "' created on resource: '" + inducementUid + "' for user: '" + user.getName() + "'.");
            } catch (ObjectAlreadyExistsException | DatabaseCommunicationException e) {
                LOGGER.error("Could not create new account for user: '" + user.getName() + "' on resource: '" + inducementUid + "'. ", e);
            }
        }
    }

    private void handleExistingUserRoleInducements(UserType user, List<InducementType<RoleType>> roleIdentifiers){
        List<AssignmentType<RoleType>> userRoleAssignments = user.getRoleAssignments();

        for(InducementType roleInducement: roleIdentifiers){
            AssignmentType<RoleType> roleAssignment = new AssignmentType<>(roleInducement.getUid(), RoleType.class);

            if(!userRoleAssignments.contains(roleAssignment)){

                user.getRoleAssignments().add(roleAssignment);
                LOGGER.debug("New assignment of role with uid: '" + roleInducement.getUid() + "' added to user with uid: '" + user.getName() + "'.");
            }
        }
    }

    private void handleNewUserRoleInducements(UserType user, List<InducementType<RoleType>> roleInducements){
        for(InducementType inducement: roleInducements){
            AssignmentType<RoleType> roleAssignment = new AssignmentType<>(inducement.getUid(), RoleType.class);
            user.getRoleAssignments().add(roleAssignment);
            LOGGER.debug("New assignment of role with uid: '" + inducement.getUid() + "' added to user with uid: '" + user.getUid() + "'.");
        }
    }
}
