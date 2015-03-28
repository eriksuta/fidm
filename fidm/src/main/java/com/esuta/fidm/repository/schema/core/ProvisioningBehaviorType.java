package com.esuta.fidm.repository.schema.core;

import java.io.Serializable;

/**
 *  An enumeration type defining three types of provisioning behaviors.
 *  These behaviors serves as a prescription for what should happen, when
 *  certain actions are triggered (like change on an attribute of org. type).
 *  These behaviors basically specifies, when an attribute change event
 *  should be processed.
 *
 *  @author shood
 * */
public enum ProvisioningBehaviorType implements Serializable{

    /**
     *  Most strict provisioning behavior type. Choosing this as a provisioning
     *  behavior in provisioning rule will tell the system to compute and apply
     *  the reaction to change immediately as this change is detected.
     * */
    PRO_ACTIVE,

    /**
     *  This provisioning behavior tells the system to compute and apply the
     *  reaction to the attribute change event when it is needed. For example,
     *  if another resource inducement was added to org. unit and JUST_IN_TIME
     *  provisioning behavior is specified as a reaction to such change, the
     *  accounts for all members of org. unit will not be created immediately,
     *  but gradually, over time, as needed for each member specifically - the
     *  account for member will be created when the member requests an access
     *  to such resource, no sooner, no later, precisely when it is needed.
     * */
    JUST_IN_TIME,

    /**
     *  A special type of provisioning behavior. It defines a specific moment
     *  in the future, when a change processing should be made. This behavior
     *  is especially useful, when the change processing may be a resource
     *  intensive process (such as addition of resource inducement to org.
     *  unit - the system may need to create accounts for thousands of members).
     * */
    CONSTANT
}
