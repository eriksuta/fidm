package com.esuta.fidm.repository.schema.support;

import com.esuta.fidm.repository.schema.core.ModificationType;

import java.io.Serializable;

/**
 *  This object is a representation of change event and is responsible
 *  for handling the data for change event, such as change type, the
 *  attribute name of modified attribute and a JSON representation of
 *  an old and new attribute values.
 *
 *  @author shood
 * */
public class AttributeModificationType implements Serializable{

    /**
     *  An attribute that this specific change is applied to
     * */
    private String attribute;

    /**
     *  A type of modification (ADDITION, DELETION, MODIFICATION)
     * */
    private ModificationType modificationType;

    /**
     *  A JSON representation of old value of attribute, just before
     *  the change
     * */
    private String oldValue;

    /**
     *  A JSON representation of a new value for attribute.
     * */
    private String newValue;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(ModificationType modificationType) {
        this.modificationType = modificationType;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeModificationType)) return false;

        AttributeModificationType that = (AttributeModificationType) o;

        if (attribute != null ? !attribute.equals(that.attribute) : that.attribute != null) return false;
        if (modificationType != that.modificationType) return false;
        if (newValue != null ? !newValue.equals(that.newValue) : that.newValue != null) return false;
        if (oldValue != null ? !oldValue.equals(that.oldValue) : that.oldValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attribute != null ? attribute.hashCode() : 0;
        result = 31 * result + (modificationType != null ? modificationType.hashCode() : 0);
        result = 31 * result + (oldValue != null ? oldValue.hashCode() : 0);
        result = 31 * result + (newValue != null ? newValue.hashCode() : 0);
        return result;
    }
}
