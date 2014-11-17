package com.esuta.fidm.infra.exception;

/**
 *  @author shood
 * */
public class ObjectAlreadyExistsException extends GeneralException{

    private String uid;
    private String name;

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

    public ObjectAlreadyExistsException(String message, String uid, String name){
        super(message);
        this.uid = uid;
        this.name = name;
    }

    public ObjectAlreadyExistsException(String message, Throwable cause, String uid){
        super(message, cause);
        this.uid = uid;
    }

    public ObjectAlreadyExistsException(String message, Throwable cause, String uid, String name){
        super(message, cause);
        this.uid = uid;
        this.name = name;
    }

    @Override
    public String getExceptionMessage() {
        return "Object wit uid='" + uid + "' and(or) name='" + name + "' already exists in the repository. Message: " + getMessage();
    }
}
