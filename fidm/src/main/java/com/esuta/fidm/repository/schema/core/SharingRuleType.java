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
public class SharingRuleType implements Serializable{

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

    public SharingRuleType() {}

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
        if (!(o instanceof SharingRuleType)) return false;

        SharingRuleType that = (SharingRuleType) o;

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
