package com.esuta.fidm.repository.schema.core;

import javax.jdo.annotations.Index;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  A policy that in simple words specifies the behavior, or a reaction of a
 *  system to change events that occurs in org. units or org. unit hierarchies.
 *  Every federation member may define a specific provisioning policy for
 *  org. unit and it does not matter, if org. unit is local or remote. This also means,
 *  that handling of changes in org. unit definition may be different in different
 *  federation members.
 *
 *
 *  @author shood
 * */
@Entity
public class FederationProvisioningPolicyType extends ObjectType{

    /**
     *  A system unique name of the provisioning policy.
     * */
    @Index(unique = "true")
    private String name;

    /**
     *  The display name of the provisioning policy. Should be a human readable form,
     *  such as 'Chemistry Department Provisioning Policy' etc. This attribute is mostly
     *  used in user interface.
     * */
    @Index
    private String displayName;

    /**
     *  A default provisioning behavior for this provisioning policy. A default behavior
     *  is applied for all attributes and change types, but it is overridden by
     *  rules defined for specific attributes.
     * */
    @Enumerated(EnumType.STRING)
     private ProvisioningBehaviorType defaultRule;

    /**
     *  A list of provisioning rules that together compose a policy of
     *  this object.
     * */
    @OneToMany(fetch = FetchType.EAGER)
    private List<FederationProvisioningRuleType> rules;

    public FederationProvisioningPolicyType(){}

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

    public ProvisioningBehaviorType getDefaultRule() {
        return defaultRule;
    }

    public void setDefaultRule(ProvisioningBehaviorType defaultRule) {
        this.defaultRule = defaultRule;
    }

    public List<FederationProvisioningRuleType> getRules() {
        if(rules == null){
            rules = new ArrayList<>();
        }

        return rules;
    }

    public void setRules(List<FederationProvisioningRuleType> rules) {
        this.rules = rules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FederationProvisioningPolicyType)) return false;
        if (!super.equals(o)) return false;

        FederationProvisioningPolicyType that = (FederationProvisioningPolicyType) o;

        if (defaultRule != null ? !defaultRule.equals(that.defaultRule) : that.defaultRule != null) return false;
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
        result = 31 * result + (defaultRule != null ? defaultRule.hashCode() : 0);
        result = 31 * result + (rules != null ? rules.hashCode() : 0);
        return result;
    }
}
