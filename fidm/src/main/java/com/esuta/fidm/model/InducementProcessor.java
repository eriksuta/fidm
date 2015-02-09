package com.esuta.fidm.model;

import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.repository.schema.AccountType;
import com.esuta.fidm.repository.schema.OrgType;
import com.esuta.fidm.repository.schema.UserType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 *
 *  This class is responsible for handling inducement-related behavior.
 * */
public class InducementProcessor {

    private static final transient Logger LOGGER = Logger.getLogger(InducementProcessor.class);

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
        List<String> userOrgAssignments = user.getOrgUnitAssignments();

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
        List<String> userOrgAssignments = user.getOrgUnitAssignments();

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

    private List<OrgType> retrieveOrgUnits(List<String> orgUnitIdentifiers){
        List<OrgType> orgUnitList = new ArrayList<>();

        for(String orgUid: orgUnitIdentifiers){
            try {
                orgUnitList.add(modelService.readObject(OrgType.class, orgUid));
            } catch (DatabaseCommunicationException e) {
                LOGGER.error("Could not retrieve org. unit with UID: '" + orgUid + "'.", e);
            }
        }

        return orgUnitList;
    }

    private void handleExistingUserResourceInducements(UserType user, List<String> resourceInducementIdentifiers){
        List<AccountType> userAccounts = new ArrayList<>();
        List<String> resourcesWithAccounts = new ArrayList<>();
        List<String> resourcesWithoutAccounts = new ArrayList<>();

        // Retrieve existing accounts of handled user
        for(String accountUid: user.getAccounts()){
            try {
                userAccounts.add(modelService.readObject(AccountType.class, accountUid));
            } catch (DatabaseCommunicationException e) {
                LOGGER.error("Could not retrieve account with uid: '" + accountUid + "' belonging to user: '" + user.getName() + "'.", e);
            }
        }

        for(AccountType account: userAccounts){
            resourcesWithAccounts.add(account.getResource());
        }

        // Find out, if we need to create any new account enforced by inducement
        for(String resourceUid: resourceInducementIdentifiers){
            if(!resourcesWithAccounts.contains(resourceUid)){
                resourcesWithoutAccounts.add(resourceUid);
            }
        }

        // Create missing accounts
        for(String resourceUid: resourcesWithoutAccounts){
            AccountType newAccount = new AccountType();
            newAccount.setResource(resourceUid);
            newAccount.setOwner(user.getUid());
            newAccount.setAccountName(user.getName());

            try {
                newAccount = modelService.createObject(newAccount);
                user.getAccounts().add(newAccount.getUid());
                LOGGER.debug("New account with uid: '" + newAccount.getUid() + "' created on resource: '" + resourceUid + "' for user: '" + user.getUid() + "'.");
            } catch (ObjectAlreadyExistsException | DatabaseCommunicationException e) {
                LOGGER.error("Could not create new account for user: '" + user.getUid() + "' on resource: '" + resourceUid + "'. ", e);
            }
        }
    }

    private void handleNewUserResourceInducements(UserType user, List<String> resourceIdentifiers){
        for(String resourceUid: resourceIdentifiers){
            AccountType newAccount = new AccountType();
            newAccount.setResource(resourceUid);
            newAccount.setOwner(user.getUid());
            newAccount.setAccountName(user.getName());

            try {
                newAccount = modelService.createObject(newAccount);
                user.getAccounts().add(newAccount.getUid());
                LOGGER.debug("New account with uid: '" + newAccount.getUid() + "' created on resource: '" + resourceUid + "' for user: '" + user.getName() + "'.");
            } catch (ObjectAlreadyExistsException | DatabaseCommunicationException e) {
                LOGGER.error("Could not create new account for user: '" + user.getName() + "' on resource: '" + resourceUid + "'. ", e);
            }
        }
    }

    private void handleExistingUserRoleInducements(UserType user, List<String> roleIdentifiers){
        List<String> userRoleAssignments = user.getRoleAssignments();

        for(String roleUid: roleIdentifiers){
            if(!userRoleAssignments.contains(roleUid)){
                user.getRoleAssignments().add(roleUid);
                LOGGER.debug("New assignment of role with uid: '" + roleUid + "' added to user with uid: '" + user.getName() + "'.");
            }
        }
    }

    private void handleNewUserRoleInducements(UserType user, List<String> roleIdentifiers){
        for(String roleUid: roleIdentifiers){
            user.getRoleAssignments().add(roleUid);
            LOGGER.debug("New assignment of role with uid: '" + roleUid + "' added to user with uid: '" + user.getUid() + "'.");
        }
    }
}
