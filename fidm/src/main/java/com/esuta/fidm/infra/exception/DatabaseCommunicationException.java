package com.esuta.fidm.infra.exception;

/**
 *  @author shood
 * */
public class DatabaseCommunicationException extends GeneralException{

    public DatabaseCommunicationException(){
        super();
    }

    public DatabaseCommunicationException(String message){
        super(message);
    }

    public DatabaseCommunicationException(Throwable cause){
        super(cause);
    }

    public DatabaseCommunicationException(String message, Throwable cause){
        super(message, cause);
    }

    @Override
    public String getExceptionMessage() {
        return "Connection to database could not be established. Message: " + getMessage();
    }
}
