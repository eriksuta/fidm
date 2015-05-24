package com.esuta.fidm.model.federation.client;

import com.esuta.fidm.repository.schema.core.ObjectType;

/**
 *  @author shood
 * */
public class ObjectTypeRestResponse<T extends ObjectType> extends SimpleRestResponse{

    private T value;

    public ObjectTypeRestResponse(){}

    public ObjectTypeRestResponse(T value) {
        this.value = value;
    }

    public ObjectTypeRestResponse(int status, String message) {
        super(status, message);
    }

    public ObjectTypeRestResponse(int status, String message, T value) {
        super(status, message);
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectTypeRestResponse)) return false;
        if (!super.equals(o)) return false;

        ObjectTypeRestResponse that = (ObjectTypeRestResponse) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
