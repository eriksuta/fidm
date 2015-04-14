package com.esuta.fidm.model;

import com.esuta.fidm.repository.schema.core.OrgType;
import com.esuta.fidm.repository.schema.core.UserType;
import com.esuta.fidm.repository.schema.support.AttributeModificationType;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  This service serves as a simple provisioning engine that takes care about change
 *  processing. It works with a list of modifications that are provided by other services
 *  (this list should already be filtered by sharing policies since this will not be
 *  done here) that are processed and applied based on provisioning rules defined
 *  for an org. unit.
 *
 *  @author shood
 * */
public class ProvisioningService {

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
     *  A special map of changes for individual org. units (represented by uid of org. unit).
     *  This list is populated with modifications (multi-value attribute modifications in
     *  most cases) that are evaluated when certain actions are triggered - these are the actions,
     *  which processing may be affected by the change in this list - e.g. user trying to access
     *  a relying party.
     * */
    private Map<String, List<AttributeModificationType>> jitModificationList = new HashMap<>();

    private ProvisioningService(){
        changeProcessor = ObjectChangeProcessor.getInstance();
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
     *      * A special task is created for CONSTANT provisioning changes - this task will be
     *        performed when the time specified in provisioning rule is met
     * */
    public void applyProvisioningPolicy(OrgType org, List<AttributeModificationType> modifications){
        //TODO
    }

    /**
     *  This method will check the existing list with just-in-time provisioning changes and will apply
     *  (if there are any) modifications for the user that triggered the action.
     * */
    public void checkJitProvisioningList(UserType user){
        //TODO
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
}
