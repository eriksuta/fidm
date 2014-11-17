package com.esuta.fidm.repository.schema;

import javax.persistence.Entity;

/**
 *  @author shood
 * */
@Entity
public class UserType extends ObjectType{

    private String fullName;
    private String givenName;
    private String familyName;
    private String additionalName;
    private String nickName;

    private String honorificPrefix;
    private String honorificSuffix;
    private String title;

    private String emailAdress;
    private String telephoneNumber;

    private String locality;
    private String password;

    public UserType(){}

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

    public String getEmailAdress() {
        return emailAdress;
    }

    public void setEmailAdress(String emailAdress) {
        this.emailAdress = emailAdress;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserType)) return false;
        if (!super.equals(o)) return false;

        UserType userType = (UserType) o;

        if (additionalName != null ? !additionalName.equals(userType.additionalName) : userType.additionalName != null)
            return false;
        if (emailAdress != null ? !emailAdress.equals(userType.emailAdress) : userType.emailAdress != null)
            return false;
        if (familyName != null ? !familyName.equals(userType.familyName) : userType.familyName != null) return false;
        if (fullName != null ? !fullName.equals(userType.fullName) : userType.fullName != null) return false;
        if (givenName != null ? !givenName.equals(userType.givenName) : userType.givenName != null) return false;
        if (honorificPrefix != null ? !honorificPrefix.equals(userType.honorificPrefix) : userType.honorificPrefix != null)
            return false;
        if (honorificSuffix != null ? !honorificSuffix.equals(userType.honorificSuffix) : userType.honorificSuffix != null)
            return false;
        if (locality != null ? !locality.equals(userType.locality) : userType.locality != null) return false;
        if (nickName != null ? !nickName.equals(userType.nickName) : userType.nickName != null) return false;
        if (password != null ? !password.equals(userType.password) : userType.password != null) return false;
        if (telephoneNumber != null ? !telephoneNumber.equals(userType.telephoneNumber) : userType.telephoneNumber != null)
            return false;
        if (title != null ? !title.equals(userType.title) : userType.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (additionalName != null ? additionalName.hashCode() : 0);
        result = 31 * result + (nickName != null ? nickName.hashCode() : 0);
        result = 31 * result + (honorificPrefix != null ? honorificPrefix.hashCode() : 0);
        result = 31 * result + (honorificSuffix != null ? honorificSuffix.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (emailAdress != null ? emailAdress.hashCode() : 0);
        result = 31 * result + (telephoneNumber != null ? telephoneNumber.hashCode() : 0);
        result = 31 * result + (locality != null ? locality.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
