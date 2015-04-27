package com.esuta.fidm.model;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.model.util.JsonUtil;
import com.esuta.fidm.repository.schema.core.*;
import com.esuta.fidm.repository.schema.support.AttributeModificationType;
import com.esuta.fidm.repository.schema.support.ObjectModificationType;
import org.apache.log4j.Logger;

import java.util.*;

/**
 *  This service serves as a simple provisioning engine that takes care about change
 *  processing. It works with a list of modifications that are provided by other services
 *  (this list should already be filtered by sharing policies since this will not be
 *  done here) that are processed and applied based on provisioning rules defined
 *  for an org. unit.
 *
 *  @author shood
 * */
public class ProvisioningService implements IProvisioningService{

    private static final Logger LOGGER = Logger.getLogger(ProvisioningService.class);

    /**
     *  Single ObjectChangeProcessor instance
     * */
    private static ProvisioningService instance = null;

    /**
     *  Change processor instance - applies changes on org. units after provisioning
     *  policy is applied
     * */
    private ObjectChangeProcessor changeProcessor;

    /**
     *  Model service instance for all kinds of support operations
     * */
    private IModelService modelService;

    /**
     *  A special map of changes for individual org. units (represented by uid of org. unit).
     *  This list is populated with modifications (multi-value attribute modifications in
     *  most cases) that are evaluated when certain actions are triggered - these are the actions,
     *  which processing may be affected by the change in this list - e.g. user trying to access
     *  a relying party.
     * */
    private Map<String, List<AttributeModificationType>> jitModificationList = new HashMap<>();

    private ProvisioningService(){
        changeProcessor = ObjectChangeProcessor.getInstance();
        modelService = ModelService.getInstance();
    }

    public static ProvisioningService getInstance(){
        if(instance == null){
            instance = new ProvisioningService();
        }

        return instance;
    }

    /**
     *  Applies provisioning policy on a set of rules on a certain org. unit. These rules are processed
     *  based on provisioning policy of org. unit and specific provisioning rules defined for attributes
     *  that are changes. There are basically 3 scenarios for changes:
     *      * A change is processed immediately, if PRO-ACTIVE provisioning rule is specified
     *      * A change is added to jitModificationList and checked every-time when actions requiring
     *        change processing are triggered if JUST-IN-TIME provisioning rule is defined
     *      * A special task is created for STATIC provisioning changes - this task will be
     *        performed when the time specified in provisioning rule is met
     * */
    public void applyProvisioningPolicy(OrgType org, List<AttributeModificationType> modifications)
            throws DatabaseCommunicationException, ObjectNotFoundException, NoSuchFieldException, IllegalAccessException {

        if(org == null || modifications == null || modifications.isEmpty()){
            return;
        }

        ProvisioningPolicyType policy = modelService.readObject(ProvisioningPolicyType.class,
                org.getProvisioningPolicy().getUid());

        if(policy == null){
            return;
        }

        for(AttributeModificationType modification: modifications){
            ProvisioningRuleType rule = findRuleForModification(modification, policy);
            List<AttributeModificationType> constantUpdateModifications = new ArrayList<>();

            if(rule == null){
                //Apply default provisioning behavior
                switch (policy.getDefaultBehavior()){
                    case PRO_ACTIVE:
                        changeProcessor.applyModificationsOnOrg(org, wrapModification(modification));
                        recomputeInducementsIfNeeded(org, modification);
                        break;
                    case JUST_IN_TIME:
                        changeProcessor.applyModificationsOnOrg(org, wrapModification(modification));
                        insertChangeIntoJitModificationList(org.getUid(), modification);
                        break;
                    case STATIC:
                        constantUpdateModifications.add(modification);
                        break;
                    default:
                        LOGGER.error("Invalid provisioning behavior type encountered. Processing not completed correctly.");
                }

            } else {
                //Find out, if there is a specific rule for this modification and act accordingly
                switch (rule.getProvisioningType()){
                    case PRO_ACTIVE:
                        changeProcessor.applyModificationsOnOrg(org, wrapModification(modification));
                        recomputeInducementsIfNeeded(org, modification);
                        break;
                    case JUST_IN_TIME:
                        changeProcessor.applyModificationsOnOrg(org, wrapModification(modification));
                        insertChangeIntoJitModificationList(org.getUid(), modification);
                        break;
                    case STATIC:
                        constantUpdateModifications.add(modification);
                        break;
                    default:
                        LOGGER.error("Invalid provisioning behavior type encountered. Processing not completed correctly.");
                }
            }

            if(!constantUpdateModifications.isEmpty()){
                createConstantProvisioningUpdateTask(org, constantUpdateModifications, policy);
            }

            OrgType oldOrg = modelService.readObject(OrgType.class, org.getUid());

            //And finally, save the changes, if there is anything to save at the moment
            if(!oldOrg.equals(org)){
                modelService.updateObject(org);
            }
        }
    }

    /**
     *  This method will check the existing list with just-in-time provisioning changes and will apply
     *  (if there are any) modifications for the user that triggered the action.
     * */
    public void checkJitProvisioningList(UserType user, ResourceType resource){
        if(user == null){
            LOGGER.debug("Can't apply any changes on non-existing user");
            return;
        }

        for(AssignmentType orgRef: user.getOrgUnitAssignments()){
            if(jitModificationList.containsKey(orgRef.getUid())){
                List<AttributeModificationType> modifications = jitModificationList.get(orgRef.getUid());

                for(AttributeModificationType modification: modifications){
                    if(modification.getAttribute().equals("resourceInducements")){
                        ObjectReferenceType newResourceRef = (ObjectReferenceType)JsonUtil
                                .jsonToObject(modification.getNewValue(), ObjectReferenceType.class);

                        ObjectReferenceType oldResourceRef = (ObjectReferenceType)JsonUtil
                                .jsonToObject(modification.getOldValue(), ObjectReferenceType.class);

                        switch (modification.getModificationType()){
                            case ADD:
                                addAccountToUser(user, newResourceRef);
                                break;
                            case MODIFY:
                                removeAccountFromUser(user, oldResourceRef);
                                addAccountToUser(user, newResourceRef);
                                break;
                            case DELETE:
                                removeAccountFromUser(user, oldResourceRef);
                                break;
                            default:
                                LOGGER.error("Invalid modification type. Could not process Just-In-Time provisioning modification.");
                                break;
                        }

                    } else if (modification.getAttribute().equals("roleInducements")){
                        ObjectReferenceType newRoleRef = (ObjectReferenceType)JsonUtil
                                .jsonToObject(modification.getNewValue(), ObjectReferenceType.class);

                        ObjectReferenceType oldRoleRef = (ObjectReferenceType)JsonUtil
                                .jsonToObject(modification.getOldValue(), ObjectReferenceType.class);

                        switch (modification.getModificationType()) {
                            case ADD:
                                addRoleToUser(user, newRoleRef);
                                break;
                            case MODIFY:
                                removeRoleFromUser(user, oldRoleRef);
                                addRoleToUser(user, newRoleRef);
                                break;
                            case DELETE:
                                removeRoleFromUser(user, oldRoleRef);
                                break;
                            default:
                                LOGGER.error("Invalid modification type. Could not process Just-In-Time provisioning modification.");
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     *  This method performs an evaluation of all modifications in just-in-time provisioning list
     *  and throws away the modifications that have been applied for all possible situations -
     *  there is no reason to store these changes anymore since it causes unwanted processing.
     *  This should be performed once upon a time (periodicity depends on the rate of modification
     *  generation) and should be preferably performed at night - or any other time when system is
     *  not highly used.
     * */
    public void cleanJitProvisioningList(){
        //TODO
    }

    /**
     *  Creates a special task according to configured times options for changes contained
     *  in provided list. It also groups changes according to attributes and may create
     *  multiple tasks according to time options set in specific provisioning rules.
     * */
    public void createConstantProvisioningUpdateTask(final OrgType org, List<AttributeModificationType> modifications,
                                                     ProvisioningPolicyType policy){

        List<String> attributeNames = WebMiscUtil.createOrgAttributeList();

        for(String attributeName: attributeNames){
            final List<AttributeModificationType> attributeModifications = new ArrayList<>();

            //First, select changes for current attribute
            for(AttributeModificationType mod: modifications){
                if(attributeName.equals(mod.getAttribute())){
                    attributeModifications.add(mod);
                }
            }

            if(attributeModifications.isEmpty()){
                continue;
            }

            ProvisioningRuleType rule = findRuleForModification(attributeModifications.get(0), policy);
            Date executionTime = rule == null ? policy.getDefaultExecutionTime() : rule.getExecutionTime();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    try {
                        for(AttributeModificationType modification: attributeModifications){
                            changeProcessor.applyModificationsOnOrg(org, wrapModification(modification));
                            recomputeInducementsIfNeeded(org, modification);
                        }
                        LOGGER.info("Timer task applied all modifications for org: " + org.getName());
                    } catch (ObjectNotFoundException | DatabaseCommunicationException | IllegalAccessException | NoSuchFieldException e) {
                        LOGGER.error("Could not apply object modification on org. unit.");
                    }
                }

            }, executionTime);
            LOGGER.info("New timer task created for changes in org. " + org.getName());
        }
    }

    /**
     *  Simply adds a new role reference to the user, if it already does not exist
     * */
    private void addRoleToUser(UserType user, ObjectReferenceType roleReference){
        if(user == null || roleReference == null){
            return;
        }

        //Check, if role assignment already exists for this user
        for(AssignmentType roleAssignment: user.getRoleAssignments()){
            if(roleAssignment.getUid().equals(roleReference.getUid())){
                LOGGER.debug("Could not add role assignment with uid: " + roleReference.getUid()
                        + ". Such assignment already exists");
                return;
            }
        }

        //Create new role assignment
        try {
            AssignmentType roleAssignment = new AssignmentType(roleReference.getUid());
            roleAssignment.setAssignedByInducement(true);
            user.getRoleAssignments().add(roleAssignment);
            modelService.updateObject(user);
            LOGGER.debug("New role assignment added to role: " + roleReference.getUid());
        } catch (ObjectNotFoundException | DatabaseCommunicationException e) {
            LOGGER.error("Could not create new role assignment in user. Reason: ", e);
        }
    }

    /**
     *  Simply removes a role assignment, if such assignment exists.
     * */
    private void removeRoleFromUser(UserType user, ObjectReferenceType roleReference){
        if(user == null || roleReference == null){
            return;
        }

        AssignmentType assignmentToRemove = null;
        for(AssignmentType roleAssignment: user.getRoleAssignments()){
            if(roleAssignment.getUid().equals(roleReference.getUid())){
                assignmentToRemove = roleAssignment;
            }
        }

        try {
            if(assignmentToRemove != null && assignmentToRemove.isAssignedByInducement()){
                user.getRoleAssignments().remove(assignmentToRemove);
                modelService.updateObject(user);
                LOGGER.debug("Removing role assignment to role: " + assignmentToRemove.getUid());
                return;
            }
        } catch (ObjectNotFoundException | DatabaseCommunicationException e) {
            LOGGER.error("Could not remove role assignment from the user. Reason: ", e);
        }

        LOGGER.debug("Could not remove role assignment. Assignment to role with uid: "
                + roleReference.getUid() + " does not exists.");
    }

    /**
     *  Adds an account to the user, if account on such resource does not exist
     * */
    private void addAccountToUser(UserType user, ObjectReferenceType resourceReference){
        if(user == null || resourceReference == null){
            return;
        }

        //Check, if user has an account on provided resource and add it, if not
        try {
            for(AssignmentType accountAssignment: user.getAccounts()){
                AccountType account = modelService.readObject(AccountType.class, accountAssignment.getUid());

                if(account != null && resourceReference.getUid().equals(account.getResource().getUid())){
                    //Account exists, log this information and return to provisioning processing without
                    //new account addition
                    LOGGER.debug("Account not added. Account on provided resource already exists.");
                    return;
                }
            }

            AccountType account = new AccountType();
            account.setOwner(new ObjectReferenceType(user.getUid()));
            account.setResource(new ObjectReferenceType(resourceReference.getUid()));
            account.setPassword(user.getPassword());
            account = modelService.createObject(account);

            AssignmentType assignment = new AssignmentType(account.getUid());
            assignment.setAssignedByInducement(true);
            user.getAccounts().add(assignment);
            modelService.updateObject(user);
            LOGGER.debug("Creating new user account: " + assignment.getUid());

        } catch (DatabaseCommunicationException | ObjectAlreadyExistsException e) {
            LOGGER.error("Could not create new account. Reason: ", e);
        } catch (ObjectNotFoundException e) {
            LOGGER.error("Could not update the user. Reason: ", e);
        }
    }

    /**
     *  Removes an account from user, if it exists
     * */
    private void removeAccountFromUser(UserType user, ObjectReferenceType resourceReference){
        if(user == null || resourceReference == null){
            return;
        }

        //Check, if user has an account that is to be removed and remove it, if it exists
        AssignmentType accountToRemove = null;
        try {
            for(AssignmentType accountAssignment: user.getAccounts()){
                AccountType account = modelService.readObject(AccountType.class, accountAssignment.getUid());

                if(account != null && resourceReference.getUid().equals(account.getResource().getUid())){
                    accountToRemove = accountAssignment;
                    modelService.deleteObject(account);
                    break;
                }
            }

            if (accountToRemove != null && accountToRemove.isAssignedByInducement()){
                user.getAccounts().remove(accountToRemove);
                modelService.updateObject(user);
                LOGGER.debug("Removing user account with uid: " + accountToRemove.getUid());
            }
        } catch (DatabaseCommunicationException | ObjectNotFoundException e) {
            LOGGER.error("Could not remove an account from the user. Reason: ", e);
        }
    }

    /**
     *  Recomputes inducements of users of selected org. unit using inducement processor instance.
     *  Adds accounts, if needed
     * */
    private void recomputeInducementsIfNeeded(OrgType org, AttributeModificationType modification)
            throws DatabaseCommunicationException, ObjectNotFoundException {

        if(modification.getAttribute().equals("resourceInducements") || modification.getAttribute().equals("roleInducements")) {
            modelService.updateObject(org);
            modelService.recomputeOrganizationalUnit(org);
        }
    }

    private ProvisioningRuleType findRuleForModification(AttributeModificationType modification, ProvisioningPolicyType policy){
        String attributeName = modification.getAttribute();
        ModificationType modificationType = modification.getModificationType();

        for(ProvisioningRuleType rule: policy.getRules()){
            if(attributeName.equals(rule.getAttributeName()) &&
                    (modificationType.equals(rule.getModificationType()) || ModificationType.ALL.equals(rule.getModificationType()))){
                return rule;
            }
        }

        return null;
    }

    private ObjectModificationType wrapModification(AttributeModificationType modification){
        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);
        return modificationObject;
    }

    private void insertChangeIntoJitModificationList(String uid, AttributeModificationType modification){
        if(jitModificationList.containsKey(uid)){
            jitModificationList.get(uid).add(modification);
        } else {
            List<AttributeModificationType> modificationList = new ArrayList<>();
            modificationList.add(modification);
            jitModificationList.put(uid, modificationList);
        }
    }
}
