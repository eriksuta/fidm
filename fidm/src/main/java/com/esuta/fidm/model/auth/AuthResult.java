package com.esuta.fidm.model.auth;

/**
 *  An enumeration class representing a result of authentication operation.
 *
 *  @author shood
 * */
public enum AuthResult {

    /**
     *  Authentication process was successful
     * */
    SUCCESS,

    /**
     *  There is no such account created in identity provider
     * */
    NO_ACCOUNT,

    /**
     *  The user has provided a bad password for specified account name
     * */
    BAD_PASSWORD,

    /**
     *  There is no such account on the resource selected by user
     * */
    NO_ACCOUNT_ON_RESOURCE
}
