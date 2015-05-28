package com.esuta.fidm.model.federation.client;

import com.esuta.fidm.repository.schema.core.ObjectType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class GenericListRestResponse<T extends Serializable> extends SimpleRestResponse{

    private List<T> values;

    public GenericListRestResponse(){}

    public GenericListRestResponse(List<T> values) {
        this.values = values;
    }

    public GenericListRestResponse(int status, String message, List<T> values) {
        super(status, message);
        this.values = values;
    }

    public List<T> getValues() {
        if(values == null){
            values = new ArrayList<>();
        }

        return values;
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenericListRestResponse)) return false;
        if (!super.equals(o)) return false;

        GenericListRestResponse that = (GenericListRestResponse) o;

        if (values != null ? !values.equals(that.values) : that.values != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }
}
