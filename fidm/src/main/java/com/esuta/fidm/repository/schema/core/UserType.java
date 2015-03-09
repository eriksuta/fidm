package com.esuta.fidm.repository.schema.core;

import com.esuta.fidm.repository.schema.support.FederationIdentifier;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
@Entity
public class UserType extends ObjectType{

    /**
     *  A system unique name of the user.
     * */
    @Index(unique = "true")
    private String name;

    @Index
    private String fullName;

    @Index
    private String givenName;

    @Index
    private String familyName;

    @Index
    private String emailAddress;

    @Index
    private String locality;

    private String additionalName;
    private String nickName;

    private String honorificPrefix;
    private String honorificSuffix;
    private String title;

    private String telephoneNumber;

    private String password;

    private List<String> roleAssignments;
    private List<String> orgUnitAssignments;
    private List<String> accounts;

    /**
     *  A federation identifier used to uniquely identify the user
     *  across federation. More specifically, it is a link to the origin
     *  of this user. If this attribute is empty, the user is
     *  local, thus the origin of this org. unit is current identity
     *  provider. A federation identifier contains an identifier
     *  of federation member (FederationMemberType) and a single
     *  'uniqueAttributeValue' - an attribute containing a value that
     *  is guaranteed to be unique in origin identity provider. The
     *  source of this value is not known to this provider, it is handled
     *  by origin identity provider, so we believe that this mechanism
     *  is privacy-respecting.
     * */
    private FederationIdentifier federationIdentifier;

    public UserType(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getAdditionalName() {
        return additionalName;
    }

    public void setAdditionalName(String additionalName) {
        this.additionalName = additionalName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHonorificPrefix() {
        return honorificPrefix;
    }

    public void setHonorificPrefix(String honorificPrefix) {
        this.honorificPrefix = honorificPrefix;
    }

    public String getHonorificSuffix() {
        return honorificSuffix;
    }

    public void setHonorificSuffix(String honorificSuffix) {
        this.honorificSuffix = honorificSuffix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoleAssignments() {
        if(roleAssignments == null){
            roleAssignments = new ArrayList<>();
        }

        return roleAssignments;
    }

    public void setRoleAssignments(List<String> roleAssignments) {
        this.roleAssignments = roleAssignments;
    }

    public List<String> getOrgUnitAssignments() {
        if(orgUnitAssignments == null){
            orgUnitAssignments = new ArrayList<>();
        }

        return orgUnitAssignments;
    }

    public void setOrgUnitAssignments(List<String> orgUnitAssignments) {
        this.orgUnitAssignments = orgUnitAssignments;
    }

    public List<String> getAccounts() {
        if(accounts == null){
            accounts = new ArrayList<>();
        }

        return accounts;
    }

    public void setAccounts(List<String> accounts) {
        this.accounts = accounts;
    }

    public FederationIdentifier getFederationIdentifier() {
        return federationIdentifier;
    }

    public void setFederationIdentifier(FederationIdentifier federationIdentifier) {
        this.federationIdentifier = federationIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserType)) return false;
        if (!super.equals(o)) return false;

        UserType userType = (UserType) o;

        if (accounts != null ? !accounts.equals(userType.accounts) : userType.accounts != null) return false;
        if (additionalName != null ? !additionalName.equals(userType.additionalName) : userType.additionalName != null)
            return false;
        if (emailAddress != null ? !emailAddress.equals(userType.emailAddress) : userType.emailAddress != null)
            return false;
        if (familyName != null ? !familyName.equals(userType.familyName) : userType.familyName != null) return false;
        if (federationIdentifier != null ? !federationIdentifier.equals(userType.federationIdentifier) : userType.federationIdentifier != null)
            return false;
        if (fullName != null ? !fullName.equals(userType.fullName) : userType.fullName != null) return false;
        if (givenName != null ? !givenName.equals(userType.givenName) : userType.givenName != null) return false;
        if (honorificPrefix != null ? !honorificPrefix.equals(userType.honorificPrefix) : userType.honorificPrefix != null)
            return false;
        if (honorificSuffix != null ? !honorificSuffix.equals(userType.honorificSuffix) : userType.honorificSuffix != null)
            return false;
        if (locality != null ? !locality.equals(userType.locality) : userType.locality != null) return false;
        if (name != null ? !name.equals(userType.name) : userType.name != null) return false;
        if (nickName != null ? !nickName.equals(userType.nickName) : userType.nickName != null) return false;
        if (orgUnitAssignments != null ? !orgUnitAssignments.equals(userType.orgUnitAssignments) : userType.orgUnitAssignments != null)
            return false;
        if (password != null ? !password.equals(userType.password) : userType.password != null) return false;
        if (roleAssignments != null ? !roleAssignments.equals(userType.roleAssignments) : userType.roleAssignments != null)
            return false;
        if (telephoneNumber != null ? !telephoneNumber.equals(userType.telephoneNumber) : userType.telephoneNumber != null)
            return false;
        if (title != null ? !title.equals(userType.title) : userType.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
        result = 31 * result + (locality != null ? locality.hashCode() : 0);
        result = 31 * result + (additionalName != null ? additionalName.hashCode() : 0);
        result = 31 * result + (nickName != null ? nickName.hashCode() : 0);
        result = 31 * result + (honorificPrefix != null ? honorificPrefix.hashCode() : 0);
        result = 31 * result + (honorificSuffix != null ? honorificSuffix.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (telephoneNumber != null ? telephoneNumber.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (roleAssignments != null ? roleAssignments.hashCode() : 0);
        result = 31 * result + (orgUnitAssignments != null ? orgUnitAssignments.hashCode() : 0);
        result = 31 * result + (accounts != null ? accounts.hashCode() : 0);
        result = 31 * result + (federationIdentifier != null ? federationIdentifier.hashCode() : 0);
        return result;
    }
}
