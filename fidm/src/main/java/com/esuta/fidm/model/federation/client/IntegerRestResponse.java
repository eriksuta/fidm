package com.esuta.fidm.model.federation.client;

/**
 *  @author shood
 * */
public class IntegerRestResponse extends SimpleRestResponse{

    private Integer value;

    public IntegerRestResponse(){}

    public IntegerRestResponse(Integer value) {
        this.value = value;
    }

    public IntegerRestResponse(int status, String message, Integer value) {
        super(status, message);
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerRestResponse)) return false;
        if (!super.equals(o)) return false;

        IntegerRestResponse that = (IntegerRestResponse) o;

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
