package com.esuta.fidm.repository.schema.core;

import javax.jdo.annotations.Index;
import javax.persistence.*;
import java.util.ArrayList;
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
     *  A system unique name of the sharing policy.
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
     *  A default tolerance level for single value attributes. This attribute
     *  must always be set. If there is no specific rule for some single
     *  value attribute, the value in this variable will be used to decide
     *  sharing policy.
     * */
    @Enumerated(EnumType.STRING)
    private SingleValueTolerance defaultSingleValueTolerance;

    /**
     *  A default tolerance level for multi value attributes. This attribute
     *  must always be set. If there is no specific rule for some multi
     *  value attribute, the value in this variable will be used to decide
     *  sharing policy.
     * */
    @Enumerated(EnumType.STRING)
    private MultiValueTolerance defaultMultiValueTolerance;

    /**
     *  A list of sharing rules that together compose a policy of
     *  this object.
     * */
    @OneToMany(fetch = FetchType.EAGER)
    private List<FederationSharingRuleType> rules;

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
        if(rules == null){
            rules = new ArrayList<>();
        }

        return rules;
    }

    public void setRules(List<FederationSharingRuleType> rules) {
        this.rules = rules;
    }

    public SingleValueTolerance getDefaultSingleValueTolerance() {
        return defaultSingleValueTolerance;
    }

    public void setDefaultSingleValueTolerance(SingleValueTolerance defaultSingleValueTolerance) {
        this.defaultSingleValueTolerance = defaultSingleValueTolerance;
    }

    public MultiValueTolerance getDefaultMultiValueTolerance() {
        return defaultMultiValueTolerance;
    }

    public void setDefaultMultiValueTolerance(MultiValueTolerance defaultMultiValueTolerance) {
        this.defaultMultiValueTolerance = defaultMultiValueTolerance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FederationSharingPolicyType)) return false;
        if (!super.equals(o)) return false;

        FederationSharingPolicyType that = (FederationSharingPolicyType) o;

        if (defaultMultiValueTolerance != that.defaultMultiValueTolerance) return false;
        if (defaultSingleValueTolerance != that.defaultSingleValueTolerance) return false;
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
        result = 31 * result + (defaultSingleValueTolerance != null ? defaultSingleValueTolerance.hashCode() : 0);
        result = 31 * result + (defaultMultiValueTolerance != null ? defaultMultiValueTolerance.hashCode() : 0);
        result = 31 * result + (rules != null ? rules.hashCode() : 0);
        return result;
    }
}
