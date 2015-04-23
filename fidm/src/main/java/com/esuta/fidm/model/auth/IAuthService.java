package com.esuta.fidm.model.auth;

import com.esuta.fidm.infra.exception.DatabaseCommunicationException;

/**
 *  @author shood
 * */
public interface IAuthService {

    /**
     *  <p>
     *      A method used for authentication to an identity provider
     *  </p>
     *
     *  @param name
     *      The name of the account a subject is trying to log to
     *      (The account is present on identity provider)
     *
     *  @param password
     *      The password for the account specified previously. The
     *      password is to account present on identity provider
     *
     *  @return AuthResult
     *      The result of authentication process
     *
     *  @throws DatabaseCommunicationException
     *      Thrown when there is a problem with reading the user from
     *      the repository. This means that there is some internal issue
     *      with the identity provider
     *
     * */
    public AuthResult login(String name, String password) throws DatabaseCommunicationException;

    /**
     *  <p>
     *      A method used for authentication to a specific relying party
     *      selected by a subject during login process.
     *  </p>
     *
     *  @param name
     *      The name of the account a subject is trying to log to
     *      (The account is present on one of the relying parties
     *      connected to the identity provider)
     *
     *  @param password
     *      The password for the account specified previously. The
     *      password is to account present on one of the resources
     *      connected to the identity provider
     *
     *  @param resourceName
     *      The name of the resource on the identity provider that
     *      the subject is trying to log to.
     *
     *  @return AuthResult
     *      The result of authentication process
     *
     *  @throws DatabaseCommunicationException
     *      Thrown when there is a problem with reading the user from
     *      the repository. This means that there is some internal issue
     *      with the identity provider
     *
     * */
    public AuthResult loginToResource(String name, String password, String resourceName) throws DatabaseCommunicationException;
}
