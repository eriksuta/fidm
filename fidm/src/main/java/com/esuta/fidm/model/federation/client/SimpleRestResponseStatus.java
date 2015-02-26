package com.esuta.fidm.model.federation.client;

/**
 *  @author shood
 * */
public class SimpleRestResponseStatus {

    private int status;
    private String message;

    public SimpleRestResponseStatus() {}

    public SimpleRestResponseStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleRestResponseStatus)) return false;

        SimpleRestResponseStatus that = (SimpleRestResponseStatus) o;

        if (status != that.status) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = status;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
