package com.esuta.fidm.repository.schema.core;

import com.esuta.fidm.repository.schema.support.FederationIdentifierType;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
@Entity
public class OrgType extends ObjectType{

    /**
     *  A system unique name of the organizational unit.
     * */
    @Index(unique = "true")
    private String name;

    /**
     *  The display name of the organizational unit. Should be a
     *  human readable form, such as 'Chemistry Department' etc.
     *  This attribute is mostly used in user interface.
     * */
    @Index
    private String displayName;

    /**
     *  A list of types of organizational unit. A type of org. unit
     *  defines its purpose, or other form of specification. An example
     *  can be a project, department, class, etc.
     * */
    @Index
    @OneToMany(fetch= FetchType.EAGER)
    private List<String> orgType;

    /**
     *  A physical locality of organizational unit. In most cases, this
     *  should be a place, e.g. a country, state, city or a street.
     * */
    @Index
    private String locality;

    /**
     *  A list of organizational units that are a level higher in the org.
     *  unit hierarchy. Thanks to this attribute, we are able to create
     *  org. unit hierarchies. One org. unit may have multiple parents
     *  and one org. units may have multiple children. The children org. units
     *  are not stored in org. unit in current implementation. There are
     *  also several limitations in org. unit hierarchies.
     *      - org. unit can't be parent to itself
     *      - org. unit can't have a children that is somewhere
     *        in the chain of it's parents (to prevent cycles)
     * */
    @OneToMany(fetch = FetchType.EAGER)
     private List<ObjectReferenceType<OrgType>> parentOrgUnits;

    /**
     *  A list of governors, a references to the users that
     *  possess some level of control over org. units and
     *  decisions made with org. units
     * */
    @OneToMany(fetch = FetchType.EAGER)
     private List<ObjectReferenceType<UserType>> governors;

    /**
     *  A list of references to relying parties (service providers,
     *  connected resources). These are the resources, that members
     *  of org. unit are forced to have by belonging to org. unit.
     *  Org. units, or more specifically, the inducement mechanisms
     *  takes care that all members of org. unit have these inducements
     *  at ALL times. If they do not, such state is considered
     *  as inconsistent.
     * */
    @OneToMany(fetch = FetchType.EAGER)
    private List<InducementType<ResourceType>> resourceInducements;

    /**
     *  The same concept as with the attribute 'resourceInducements', but
     *  with roles.
     * */
    @OneToMany(fetch = FetchType.EAGER)
    private List<InducementType<RoleType>> roleInducements;

    /**
     *  A unique sharing policy - or a set of sharing rules that
     *  are applied during interaction in identity federation and
     *  determines, what can be changed by the copies of this
     *  org. unit.
     * */
    private ObjectReferenceType<FederationSharingPolicyType> sharingPolicy;

    /**
     *  A federation identifier used to uniquely identify the org. unit
     *  across federation. More specifically, it is a link to the origin
     *  of this org. unit. If this attribute is empty, the org. unit is
     *  local, thus the origin of this org. unit is current identity
     *  provider. A federation identifier contains an identifier
     *  of federation member (FederationMemberType) and a single
     *  'uniqueAttributeValue' - an attribute containing a value that
     *  is guaranteed to be unique in origin identity provider. The
     *  source of this value is not known to this provider, it is handled
     *  by origin identity provider, so we believe that this mechanism
     *  is privacy-respecting.
     * */
    private FederationIdentifierType federationIdentifier;

    /**
     *  An attribute declaring, if this org. unit can be shared in
     *  federation environment. This decision is solemnly made by
     *  the 'owner' of org. unit - current identity provider.
     * */
    private boolean sharedInFederation;

    /**
     *  An attribute responsible for sharing a subtree of org. unit.
     *  This attribute can be set only in case, when this org. unit
     *  is already shared in identity federation, thus 'sharedInFederation'
     *  attribute is set to true. If set to false, only this org. unit
     *  is shared among federation members, not it's subtree.
     * */
    private boolean sharedSubtree;

    /**
     *  An attribute used to override the decision of parent org. unit.
     *  This attribute can be used only in case, when this org. unit
     *  is already shared in federation, thus 'sharedInFederation' attribute
     *  is set to true. If the org. unit is shared in federation and this
     *  attribute is set to true (default value is false), then this
     *  specific org. unit and it's subtree won't be shared in federation.
     *  This simple attribute enables identity providers to share only
     *  specific parts of org. unit hierarchies in identity federation.
     * */
    private boolean overrideParentSharing;

    public OrgType(){}

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

    public List<String> getOrgType() {
        if(orgType == null){
            orgType = new ArrayList<>();
        }

        return orgType;
    }

    public void setOrgType(List<String> orgType) {
        this.orgType = orgType;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public List<ObjectReferenceType<OrgType>> getParentOrgUnits() {
        if(parentOrgUnits == null){
            parentOrgUnits = new ArrayList<>();
        }

        return parentOrgUnits;
    }

    public void setParentOrgUnits(List<ObjectReferenceType<OrgType>> parentOrgUnits) {
        this.parentOrgUnits = parentOrgUnits;
    }

    public List<ObjectReferenceType<UserType>> getGovernors() {
        if(governors == null){
            governors = new ArrayList<>();
        }

        return governors;
    }

    public void setGovernors(List<ObjectReferenceType<UserType>> governors) {
        this.governors = governors;
    }

    public List<InducementType<ResourceType>> getResourceInducements() {
        if(resourceInducements == null){
            resourceInducements = new ArrayList<>();
        }

        return resourceInducements;
    }

    public void setResourceInducements(List<InducementType<ResourceType>> resourceInducements) {
        this.resourceInducements = resourceInducements;
    }

    public List<InducementType<RoleType>> getRoleInducements() {
        if(roleInducements == null){
            roleInducements = new ArrayList<>();
        }

        return roleInducements;
    }

    public void setRoleInducements(List<InducementType<RoleType>> roleInducements) {
        this.roleInducements = roleInducements;
    }

    public FederationIdentifierType getFederationIdentifier() {
        return federationIdentifier;
    }

    public void setFederationIdentifier(FederationIdentifierType federationIdentifier) {
        this.federationIdentifier = federationIdentifier;
    }

    public boolean isSharedInFederation() {
        return sharedInFederation;
    }

    public void setSharedInFederation(boolean sharedInFederation) {
        this.sharedInFederation = sharedInFederation;
    }

    public boolean isSharedSubtree() {
        return sharedSubtree;
    }

    public void setSharedSubtree(boolean sharedSubtree) {
        this.sharedSubtree = sharedSubtree;
    }

    public boolean isOverrideParentSharing() {
        return overrideParentSharing;
    }

    public void setOverrideParentSharing(boolean overrideParentSharing) {
        this.overrideParentSharing = overrideParentSharing;
    }

    public ObjectReferenceType<FederationSharingPolicyType> getSharingPolicy() {
        return sharingPolicy;
    }

    public void setSharingPolicy(ObjectReferenceType<FederationSharingPolicyType> sharingPolicy) {
        this.sharingPolicy = sharingPolicy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrgType)) return false;
        if (!super.equals(o)) return false;

        OrgType orgType1 = (OrgType) o;

        if (overrideParentSharing != orgType1.overrideParentSharing) return false;
        if (sharedInFederation != orgType1.sharedInFederation) return false;
        if (sharedSubtree != orgType1.sharedSubtree) return false;
        if (displayName != null ? !displayName.equals(orgType1.displayName) : orgType1.displayName != null)
            return false;
        if (federationIdentifier != null ? !federationIdentifier.equals(orgType1.federationIdentifier) : orgType1.federationIdentifier != null)
            return false;
        if (governors != null ? !governors.equals(orgType1.governors) : orgType1.governors != null) return false;
        if (locality != null ? !locality.equals(orgType1.locality) : orgType1.locality != null) return false;
        if (name != null ? !name.equals(orgType1.name) : orgType1.name != null) return false;
        if (orgType != null ? !orgType.equals(orgType1.orgType) : orgType1.orgType != null) return false;
        if (parentOrgUnits != null ? !parentOrgUnits.equals(orgType1.parentOrgUnits) : orgType1.parentOrgUnits != null)
            return false;
        if (resourceInducements != null ? !resourceInducements.equals(orgType1.resourceInducements) : orgType1.resourceInducements != null)
            return false;
        if (roleInducements != null ? !roleInducements.equals(orgType1.roleInducements) : orgType1.roleInducements != null)
            return false;
        if (sharingPolicy != null ? !sharingPolicy.equals(orgType1.sharingPolicy) : orgType1.sharingPolicy != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (orgType != null ? orgType.hashCode() : 0);
        result = 31 * result + (locality != null ? locality.hashCode() : 0);
        result = 31 * result + (parentOrgUnits != null ? parentOrgUnits.hashCode() : 0);
        result = 31 * result + (governors != null ? governors.hashCode() : 0);
        result = 31 * result + (resourceInducements != null ? resourceInducements.hashCode() : 0);
        result = 31 * result + (roleInducements != null ? roleInducements.hashCode() : 0);
        result = 31 * result + (sharingPolicy != null ? sharingPolicy.hashCode() : 0);
        result = 31 * result + (federationIdentifier != null ? federationIdentifier.hashCode() : 0);
        result = 31 * result + (sharedInFederation ? 1 : 0);
        result = 31 * result + (sharedSubtree ? 1 : 0);
        result = 31 * result + (overrideParentSharing ? 1 : 0);
        return result;
    }
}
