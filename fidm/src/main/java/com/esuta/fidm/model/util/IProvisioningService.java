package com.esuta.fidm.model.util;

import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.repository.schema.core.FederationProvisioningPolicyType;
import com.esuta.fidm.repository.schema.core.OrgType;
import com.esuta.fidm.repository.schema.core.ResourceType;
import com.esuta.fidm.repository.schema.core.UserType;
import com.esuta.fidm.repository.schema.support.AttributeModificationType;

import java.util.List;

/**
 *  @author shood
 * */
public interface IProvisioningService {

    /**
     *  <p>
     *      Applies provisioning policy on a set of rules on a certain org. unit. These rules are processed
     *      based on provisioning policy of org. unit and specific provisioning rules defined for attributes
     *      that are changes. There are basically 3 scenarios for changes:
     *          * A change is processed immediately, if PRO-ACTIVE provisioning rule is specified.
     *          * A change is added to jitModificationList and checked every-time when actions requiring
     *           change processing are triggered if JUST-IN-TIME provisioning rule is defined.
     *          * A special task is created for CONSTANT provisioning changes - this task will be
     *            performed when the time specified in provisioning rule is met.
     *  </p>
     *
     *  @param org
     *      An org unit on which the changes and provisioning policy should be applied.
     *
     *  @param modifications
     *      A list of attribute modifications.
     *
     *  @throws DatabaseCommunicationException
     *      Thrown when there is a problem with repository service (Database connection, etc.). This
     *      signalizes some serious internal server errors.
     *
     *  @throws ObjectNotFoundException
     *      Thrown when provided org. unit does not exist in the repository.
     *
     *  @throws NoSuchFieldException
     *      Thrown when one or more of the provided changes are targeted at attributes
     *      that do not exist in org. unit.
     *
     *  @throws IllegalAccessException
     *      Thrown when trying to access an attribute that is not accessible by provisioning
     *      service
     *
     * */
    void applyProvisioningPolicy(OrgType org, List<AttributeModificationType> modifications)
            throws DatabaseCommunicationException, ObjectNotFoundException, NoSuchFieldException, IllegalAccessException;

    /**
     *  <p>
     *      This method will check the existing list with just-in-time provisioning changes and will apply
     *      (if there are any) modifications for the user that triggered the action.
     *  </p>
     *
     *  @param user
     *      A user that triggers the check of provisioning changes.
     *
     *  @param resource
     *      A resource, or other relying party that is a target of user triggered action.
     *
     * */
    void checkJitProvisioningList(UserType user, ResourceType resource);

    /**
     *  <p>
     *      Creates a special task according to configured times options for changes contained
     *      in provided list. It also groups changes according to attributes and may create
     *      multiple tasks according to time options set in specific provisioning rules.
     *  </p>
     *
     *  @param  org
     *      An org unit that is the source for newly created CONSTANT provisioning task.
     *
     *  @param modifications
     *      A list of modifications to be sorted and processed in newly created CONSTANT
     *      provisioning task.
     *
     *  @param policy
     *      A provisioning policy that determines a set of rules, so the service is able
     *      to determine required reaction to specific types of changes regarding their
     *      attributes.
     *
     * */
    void createConstantProvisioningUpdateTask(OrgType org, List<AttributeModificationType> modifications, FederationProvisioningPolicyType policy);
}
