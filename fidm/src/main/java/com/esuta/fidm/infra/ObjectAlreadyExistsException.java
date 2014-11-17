package com.esuta.fidm.infra;

/**
 *  @author shood
 * */
public class ObjectAlreadyExistsException extends Exception{

    private String uid;

    public ObjectAlreadyExistsException(){
        super();
    }

    public ObjectAlreadyExistsException(String message){
        super(message);
    }

    public ObjectAlreadyExistsException(Throwable cause){
        super(cause);
    }

    public ObjectAlreadyExistsException(String message, Throwable cause){
        super(message, cause);
    }

    public ObjectAlreadyExistsException(String message, String uid){
        super(message);
        this.uid = uid;
    }

    public ObjectAlreadyExistsException(String message, Throwable cause, String uid){
        super(message, cause);
        this.uid = uid;
    }
}
