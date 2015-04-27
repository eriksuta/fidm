package com.esuta.fidm.repository.schema.core;

import java.io.Serializable;

/**
 *  An enumeration type containing a list of levels of change
 *  toleration for a multi-value attributes.
 *
 *  @author shood
 * */
public enum MultiValueTolerance implements Serializable{

    /**
     *  An origin identity provider enforces the value(s) of
     *  attribute - the copies of such unit are not able
     *  to change it in any way.
     * */
    ENFORCE,

    /**
     *  An origin org. unit specifies a set of values for target
     *  attribute and these values must remain unchanged. However,
     *  the copy of org. unit is able to define additional values
     *  of such attribute. These added values are only applied
     *  on a copy of such unit.
     * */
    ALLOW_ADD_OWN,

    /**
     *  An origin org. unit allows not only addition of own values
     *  for target attribute, but also allows the change of existing
     *  values of multi-value attribute, however the changes are still
     *  only applied on local values of such org. unit
     * */
    ALLOW_MODIFY_OWN,

    /**
     *  An origin. org unit allows the copy of org. unit in federation
     *  to add values and these values are distributed and applied in
     *  origin org. unit as well as on every copy of such org. unit
     *  in identity federation.
     * */
    ALLOW_ADD,

    /**
     *  An origin. org. unit allows maximal level of control over
     *  multi-value attribute. All change operations, such as addition,
     *  updated or delete operations are applied on origin org. unit
     *  as well as on every other copy of org. unit in identity
     *  federation.
     * */
    ALLOW_MODIFY
}