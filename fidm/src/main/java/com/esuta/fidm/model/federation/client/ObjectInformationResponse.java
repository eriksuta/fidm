package com.esuta.fidm.model.federation.client;

import com.esuta.fidm.model.federation.service.ObjectInformation;

/**
 *  @author shood
 * */
public class ObjectInformationResponse extends SimpleRestResponse{

    private ObjectInformation informationObject;

    public ObjectInformationResponse(){}

    public ObjectInformationResponse(ObjectInformation informationObject) {
        this.informationObject = informationObject;
    }

    public ObjectInformationResponse(int status, String message, ObjectInformation informationObject) {
        super(status, message);
        this.informationObject = informationObject;
    }

    public ObjectInformation getInformationObject() {
        return informationObject;
    }

    public void setInformationObject(ObjectInformation informationObject) {
        this.informationObject = informationObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectInformationResponse)) return false;
        if (!super.equals(o)) return false;

        ObjectInformationResponse that = (ObjectInformationResponse) o;

        if (informationObject != null ? !informationObject.equals(that.informationObject) : that.informationObject != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (informationObject != null ? informationObject.hashCode() : 0);
        return result;
    }
}
