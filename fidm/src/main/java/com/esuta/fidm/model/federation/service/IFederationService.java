package com.esuta.fidm.model.federation.service;

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
     *      A basic method that will return an identification of asked federation member
     *      to asking federation member. This method (as the entire API) assumes the
     *      existence of trust relationship in federation, so it will identify to
     *      any and all incoming requests
     *  </p>
     *
     *  @return javax.ws.rs.core.Response
     *      a response object containing one of the following return codes with response message
     *      containing more information about operation status and processing. The message
     *      is expected to be in JSON format
     *
     *      <b>200</b> - response with HTTP code 200 should be returned, when request
     *                   has been correctly handled. The body of response contains an
     *                   identifier of federation member
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when internal error occurs
     *                   in internal identity providers operations, such as problems with reading the
     *                   identifier of requested federation member etc.
     *
     * */
    public Response getFederationIdentifier();

    /**
     *  <p>
     *      A method with simple purpose, to handle a federation membership request. An identity
     *      provider sends this request if there is a need to create federation relationship
     *      with another identity provider in trusted federated environment.
     *  </p>
     *
     *  @param membershipRequest (FederationMembershipRequest)
     *      An instance containing information about requesting identity provider, specifically
     *      an identifier in federation, address and port for backward communication
     *
     *  @return javax.ws.rs.core.Response
     *      a response object containing one of the following return codes with response message
     *      containing more information about operation status and processing. The response
     *      message is expected to be in JSON format
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
    public Response handleFederationRequest(FederationMembershipRequest membershipRequest);

    /**
     *  <p>
     *      A method with simple purpose, to handle a response to federation membership request. An identity
     *      provider sends this response for existing local request if there is such request pending in
     *      identity provider.
     *  </p>
     *
     *  @param membershipResponse (FederationRequestResponseType)
     *      A simple response to federation membership request. This object contains the identifier of
     *      federation member and response to request (DENY or ACCEPT)
     *
     *  @return javax.ws.rs.core.Response
     *      a response object containing one of the following return codes with response message
     *      containing more information about operation status and processing. The response message
     *      is expected to be in JSON format.
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
    public Response handleFederationResponse(FederationMembershipRequest membershipResponse);
}
