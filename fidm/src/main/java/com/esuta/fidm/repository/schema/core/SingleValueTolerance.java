package com.esuta.fidm.repository.schema.core;

import java.io.Serializable;

/**
 *  An enumeration type containing a list of levels of change
 *  toleration for a single-value attribute.
 *
 *  @author shood
 * */
public enum SingleValueTolerance implements Serializable{

    /**
     *  An origin identity provider enforces the value of
     *  attribute - the copies of such unit are not able
     *  to change it in any way.
     * */
    ENFORCE,

    /**
     *  An origin identity provider lets the copies of
     *  org. unit decide about the value of target attribute.
     *  It can be changed locally - this means that the
     *  change will only be applied on the copy of org.
     *  unit, not on the origin org. unit.
     * */
    ALLOW_OWN,

    /**
     *  An origin identity provider allows the specified copy
     *  of it to change the value of target attribute - this
     *  value will be then distributed and applied to origin
     *  org. unit and to all other copies of org. unit.
     * */
    ALLOW_MODIFY
}