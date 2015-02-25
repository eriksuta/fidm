package com.esuta.fidm.model.federation;

import com.esuta.fidm.repository.schema.support.FederationRequestResponseType;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

/**
 *  @author shood
 *
 *  TODO - interface description
 * */
public interface IFederationService {

    /**
     *  <p>
     *      A method with simple purpose, to handle a federation membership request. An identity
     *      provider sends this request if there is a need to create federation relationship
     *      with another identity provider in trusted federated environment.
     *  </p>
     *
     *  @param requestContext (HttpServletRequest)
     *      a request context, usually gained from underlying implementation technology rather than
     *      sent in request itself. Needed for information about sender (address, port, etc.)
     *
     *  @param identityProviderIdentifier (String)
     *      A unique identified of federation membership requesting identity provider (Should
     *      be based on specified policy in identity federation)
     *
     *  @return Object
     *      a response object, specific type depends on concrete implementation. However it should
     *      be an incarnation of HTTP response object. Possible HTTP return codes:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned, when request
     *                   has been correctly handled, thus new federation membership request
     *                   was created in target identity provider.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when request body is
     *                   malformed, specifically, in this case, the identifier parameter
     *                   is not defined.
     *
     *      <b>409</b> - response with HTTP code 409 should be returned when there already exists
     *                   a federation membership request or accepted federation member in target
     *                   identity provider - thus relationship between identity providers is
     *                   well defined and this request is obsolete.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when internal error occurs
     *                   in internal identity providers operations, such as handling the uniqueness
     *                   of requested federation member etc.
     * */
    public Response handleFederationRequest(HttpServletRequest requestContext, String identityProviderIdentifier);

    /**
     *  <p>
     *      A method with simple purpose, to handle a response to federation membership request. An identity
     *      provider sends this response for existing local request if there is such request pending in
     *      identity provider.
     *  </p>
     *
     *  @param requestContext (HttpServletRequest)
     *      a request context, usually gained from underlying implementation technology rather than
     *      sent in request itself. Needed for information about sender (address, port, etc.)
     *
     *  @param identityProviderIdentifier (String)
     *      A unique identified of identity provider sending a response to requesting identity provider (Should
     *      be based on specified policy in identity federation)
     *
     *  @param response (FederationRequestResponseType)
     *      A simple response to federation membership request.
     *
     *  @return Object
     *      a response object, specific type depends on concrete implementation. However it should
     *      be an incarnation of HTTP response object. Possible HTTP return codes:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when response to federation
     *                   membership request was correctly processed.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when there is an issue with
     *                   provided parameters, such as no values in identityProviderIdentifier or
     *                   response. It should also be thrown in situation, when response is targeted
     *                   at a not existing federation member.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when internal error occurs
     *                   in internal identity providers operations, such as handling the uniqueness
     *                   of requested federation member etc.
     * */
    public Response handleFederationResponse(HttpServletRequest requestContext, String identityProviderIdentifier, FederationRequestResponseType response);
}
