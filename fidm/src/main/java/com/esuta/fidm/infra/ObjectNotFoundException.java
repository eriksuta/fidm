package com.esuta.fidm.infra;

/**
 *  @author shood
 * */
public class ObjectNotFoundException extends Exception{

    private String uid;

    public ObjectNotFoundException(){
        super();
    }

    public ObjectNotFoundException(String message){
        super(message);
    }

    public ObjectNotFoundException(Throwable cause){
        super(cause);
    }

    public ObjectNotFoundException(String message, Throwable cause){
        super(message, cause);
    }

    public ObjectNotFoundException(String message, String uid){
        super(message);
        this.uid = uid;
    }

    public ObjectNotFoundException(String message, Throwable cause, String uid){
        super(message, cause);
        this.uid = uid;
    }
}
