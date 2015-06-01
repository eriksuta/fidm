package com.esuta.fidm.model.federation.service;

import com.esuta.fidm.repository.schema.support.ObjectModificationType;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *  A wrapper object for the purposes of federation REST API. This object
 *  is send using POST HTTP method and it contains an identification of
 *  requesting federation member, a unique identification of org. unit
 *  on the target federation member and, of course, a set of org. unit
 *  changes that should be processed by target federation member.
 *
 *  @author shood
 * */
public class OrgModificationWrapper implements Serializable{

    /**
     *  An identification of federation member in identity
     *  federation, in this case, this is an identification of
     *  federation member that is SENDING the changes to be
     *  processed.
     * */
    private String federationMember;

    /**
     *  A value of unique attribute of org. unit on target system.
     * */
    private String uniqueAttributeValue;

    /**
     *  A set of changes to be processed
     * */
    private ObjectModificationType modificationObject;

    public OrgModificationWrapper() {}

    public String getFederationMember() {
        return federationMember;
    }

    public void setFederationMember(String federationMember) {
        this.federationMember = federationMember;
    }

    public String getUniqueAttributeValue() {
        return uniqueAttributeValue;
    }

    public void setUniqueAttributeValue(String uniqueAttributeValue) {
        this.uniqueAttributeValue = uniqueAttributeValue;
    }

    public ObjectModificationType getModificationObject() {
        return modificationObject;
    }

    public void setModificationObject(ObjectModificationType modificationObject) {
        this.modificationObject = modificationObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrgModificationWrapper)) return false;

        OrgModificationWrapper that = (OrgModificationWrapper) o;

        if (federationMember != null ? !federationMember.equals(that.federationMember) : that.federationMember != null)
            return false;
        if (modificationObject != null ? !modificationObject.equals(that.modificationObject) : that.modificationObject != null)
            return false;
        if (uniqueAttributeValue != null ? !uniqueAttributeValue.equals(that.uniqueAttributeValue) : that.uniqueAttributeValue != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = federationMember != null ? federationMember.hashCode() : 0;
        result = 31 * result + (uniqueAttributeValue != null ? uniqueAttributeValue.hashCode() : 0);
        result = 31 * result + (modificationObject != null ? modificationObject.hashCode() : 0);
        return result;
    }
}
