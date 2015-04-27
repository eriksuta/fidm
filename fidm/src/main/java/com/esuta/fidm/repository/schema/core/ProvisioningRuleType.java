package com.esuta.fidm.repository.schema.core;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Date;

/**
 *  @author shood
 * */
@Embeddable
public class ProvisioningRuleType implements Serializable{

    /**
     *  The String representation of a name of the attribute
     *  of org. unit, for which this rule is specified for.
     * */
    private String attributeName;

    /**
     *  The type of modification for which this behavior will be applied.
     *  The default value is 'ALL' - this means that provisioning rule
     *  will be applied to all kind of changes on the specified attribute.
     * */
    @Enumerated(EnumType.STRING)
    private ModificationType modificationType = ModificationType.ALL;

    /**
     *  A definition of provisioning type for the change type for specific
     *  attribute. This attribute specified when and how should the change
     *  event be processed.
     * */
    @Enumerated(EnumType.STRING)
    private ProvisioningBehaviorType provisioningType;

    /**
     *  The time of execution of provisioning changes. This field has meaning
     *  only if STATIC provisioning behavior is configured and it sets a moment
     *  in future, where all current changes will be triggered. If no date is set,
     *  the changes will be applied next time the configured time is here.
     * */
    private Date executionTime;

    public ProvisioningRuleType(){}

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(ModificationType modificationType) {
        this.modificationType = modificationType;
    }

    public ProvisioningBehaviorType getProvisioningType() {
        return provisioningType;
    }

    public void setProvisioningType(ProvisioningBehaviorType provisioningType) {
        this.provisioningType = provisioningType;
    }

    public Date getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Date executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProvisioningRuleType)) return false;

        ProvisioningRuleType that = (ProvisioningRuleType) o;

        if (attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null)
            return false;
        if (executionTime != null ? !executionTime.equals(that.executionTime) : that.executionTime != null)
            return false;
        if (modificationType != that.modificationType) return false;
        if (provisioningType != that.provisioningType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attributeName != null ? attributeName.hashCode() : 0;
        result = 31 * result + (modificationType != null ? modificationType.hashCode() : 0);
        result = 31 * result + (provisioningType != null ? provisioningType.hashCode() : 0);
        result = 31 * result + (executionTime != null ? executionTime.hashCode() : 0);
        return result;
    }
}
