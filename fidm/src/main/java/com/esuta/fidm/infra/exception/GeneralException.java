package com.esuta.fidm.infra.exception;

/**
 *  A general purpose exception, serve as a super-class for
 *  more specific exceptions
 *
 *  @author shood
 * */
public abstract class GeneralException extends Exception{

    public GeneralException(){
        super();
    }

    public GeneralException(String message){
        super(message);
    }

    public GeneralException(Throwable cause){
        super(cause);
    }

    public GeneralException(String message, Throwable cause){
        super(message, cause);
    }

    /**
     *  Provides a specific message for exception cause, or
     *  to provide more context for an exception.
     * */
    public abstract String getExceptionMessage();

}
