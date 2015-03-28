package com.esuta.fidm.repository.schema.core;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 *  A rule defining the tolerance level for a specified
 *  attribute of org. unit.
 *
 *  @author shood
 * */
@Embeddable
public class FederationSharingRuleType implements Serializable{

    /**
     *  An enumeration type containing a list of levels of change
     *  toleration for a single-value attribute.
     * */
    public static enum SingleValueTolerance{

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
        ALLOW_CHANGE
    }

    /**
     *  An enumeration type containing a list of levels of change
     *  toleration for a multi-value attributes.
     * */
    public static enum MultiValueTolerance{

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
        ALLOW_CHANGE_OWN,

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
        ALLOW_CHANGE
    }

    /**
     *  The String representation of a name of the attribute
     *  of org. unit, for which this rule is specified for.
     * */
    private String attributeName;

    /**
     *  If the attribute of this rule is single-value, this field
     *  should be filled and it will represent the change tolerance
     *  level for target attribute org org. unit
     * */
    @Enumerated(EnumType.STRING)
    private SingleValueTolerance singleValueTolerance;

    /**
     *  If the attribute of this rule is multi-value, this field
     *  should be filled and it will represent the change tolerance
     *  level for target attribute org org. unit
     * */
    @Enumerated(EnumType.STRING)
    private MultiValueTolerance multiValueTolerance;

    public FederationSharingRuleType() {}

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public SingleValueTolerance getSingleValueTolerance() {
        return singleValueTolerance;
    }

    public void setSingleValueTolerance(SingleValueTolerance singleValueTolerance) {
        this.singleValueTolerance = singleValueTolerance;
    }

    public MultiValueTolerance getMultiValueTolerance() {
        return multiValueTolerance;
    }

    public void setMultiValueTolerance(MultiValueTolerance multiValueTolerance) {
        this.multiValueTolerance = multiValueTolerance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FederationSharingRuleType)) return false;

        FederationSharingRuleType that = (FederationSharingRuleType) o;

        if (attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null)
            return false;
        if (multiValueTolerance != that.multiValueTolerance) return false;
        if (singleValueTolerance != that.singleValueTolerance) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attributeName != null ? attributeName.hashCode() : 0;
        result = 31 * result + (singleValueTolerance != null ? singleValueTolerance.hashCode() : 0);
        result = 31 * result + (multiValueTolerance != null ? multiValueTolerance.hashCode() : 0);
        return result;
    }
}
