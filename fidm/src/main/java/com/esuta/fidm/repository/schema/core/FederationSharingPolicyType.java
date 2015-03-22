package com.esuta.fidm.repository.schema.core;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;
import java.util.List;

/**
 *  A policy that, in simple words, tells the copies of specific
 *  org. unit in federation, what can it change. This object is
 *  referenced from an org. unit and can be edited only by origin
 *  org. unit, the copies of such org. unit may only read it
 *  and are bound to act by it. This means, that they cannot perform
 *  any changes that are not specified in this sharing policy.
 *
 *  @author shood
 * */
@Entity
public class FederationSharingPolicyType extends ObjectType{

    /**
     *  A system unique name of the role.
     * */
    @Index(unique = "true")
    private String name;

    /**
     *  The display name of sharing policy. Should be a human readable form,
     *  such as 'Chemistry Department Sharing Policy' etc. This attribute is mostly
     *  used in user interface.
     * */
    @Index
    private String displayName;

    /**
     *  A list of sharing rules that together compose a policy of
     *  this object.
     * */
    List<FederationSharingRuleType> rules;

    public FederationSharingPolicyType() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<FederationSharingRuleType> getRules() {
        return rules;
    }

    public void setRules(List<FederationSharingRuleType> rules) {
        this.rules = rules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FederationSharingPolicyType)) return false;
        if (!super.equals(o)) return false;

        FederationSharingPolicyType that = (FederationSharingPolicyType) o;

        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (rules != null ? !rules.equals(that.rules) : that.rules != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (rules != null ? rules.hashCode() : 0);
        return result;
    }
}
