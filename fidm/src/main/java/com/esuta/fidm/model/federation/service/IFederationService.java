package com.esuta.fidm.model.federation.service;

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

    /**
     *  <p>
     *      An operation responsible for deletion of federation membership. This operation
     *      also requires both parts of membership to accept the deletion request. This
     *      operation simply creates a deletion request.
     *  </p>
     *
     *  @param deletionRequest (FederationMembershipRequest)
     *      A simple representation of deletion request. For the purposes of this operation,
     *      the deletion request only needs to contain an identifier of REQUESTING
     *      federation member, so the processing side is able to correctly identify it.
     *
     *  @return javax.ws.rs.core.Response
     *      a response object containing one of the following return codes with response message
     *      containing more information about operation status and processing. The response message
     *      is expected to be in JSON format.
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when request to federation
     *                   deletion was correctly handled.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when there is an issue
     *                   with request format, e. g. deletionRequest does not contain requested
     *                   parameters, or there is no federation bond between requesting and requested
     *                   federation members.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading or updating objects in repository.
     * */
    public Response handleFederationDeleteRequest(FederationMembershipRequest deletionRequest);

    /**
     *  <p>
     *      An operation responsible for handling a response for federation membership deletion request.
     *      A federation member may accept or deny a deletion request.
     *  </p>
     *
     *  @param deletionResponse (FederationMembershipRequest)
     *      A simple response to federation deletion request. This response should contain an
     *      identifier of REQUESTING federation member, so the processing side is able to
     *      correctly identify it. It should also contain a reaction to deletion request that
     *      has two values, ACCEPT or DENY. ACCEPT reaction should lead to deletion of federation
     *      on both sides of the request.
     *
     *  @return javax.ws.rs.core.Response
     *      a response object containing one of the following return codes with response message
     *      containing more information about operation status and processing. The response message
     *      is expected to be in JSON format.
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when response to federation
     *                   deletion request was processed correctly, thus target federation member
     *                   was deleted and federation membership does no longer exist.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when there is an issue
     *                   with request format, e. g. deletionRequest does not contain requested
     *                   parameters, or there is no federation bond between requesting and requested
     *                   federation members.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading or deleting objects in repository.
     * */
    public Response handleFederationDeleteResponse(FederationMembershipRequest deletionResponse);

    /**
     *  <p>
     *      A simple operation that returns a number of org. units that are shared by federation
     *      member that the request is directed at. The request should fail, if there is no
     *      accepted federation relationship between requester and requested identity provider.
     *      If there is an org. unit hierarchy shared, each org. unit in this hierarchy
     *      is counted as one.
     *  </p>
     *
     *  @param memberIdentifier (String)
     *      A unique identifier of federation member in identity federation performing the
     *      request
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing either a number of shared org. units, if the request was
     *      processed properly, or a message informing requester about the error that happened
     *      during request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when request to retrieve
     *                   shared org. unit count was handled correctly. In this case, the message of Response
     *                   object contains the number of shared org. units.
     *
     *      <b>400</b> - response with HTTP cod 400 should be returned when the request is
     *                   malformed, e.g. the memberIdentifier value is not set or there is no
     *                   existing membership relation between requesting and requested
     *                   federation members.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     * */
    public Response getSharedOrgUnitCount(String memberIdentifier);

    /**
     *  <p>
     *      A simple operation that returns a list of org. units that are shared by federation
     *      member that the request is directed at. The request should fail, if there is no
     *      accepted federation relationship between requester and requested identity provider.
     *      If there is an org. unit hierarchy shared, the list containing all shared org.
     *      units is returned
     *  </p>
     *
     *  @param memberIdentifier (String)
     *      A unique identifier of federation member in identity federation performing the
     *      request
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing either a list of shared org. units, if the request was
     *      processed properly, or a message informing requester about the error that happened
     *      during request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when request to retrieve
     *                   shared org. unit count was handled correctly. In this case, the message of Response
     *                   object contains the list of shared org. units.
     *
     *      <b>400</b> - response with HTTP cod 400 should be returned when the request is
     *                   malformed, e.g. the memberIdentifier value is not set or there is no
     *                   existing membership relation between requesting and requested
     *                   federation members.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     * */
    public Response getSharedOrgUnits(String memberIdentifier);

    /**
     *  <p>
     *      An operation that retrieve an organizational unit from specified member of a
     *      federation.
     *  </p>
     *
     *  @param memberIdentifier (String)
     *      A unique identifier of federation member in identity federation performing the
     *      request
     *
     *  @param uniqueAttributeValue (String)
     *      A unique value of attribute specified by targeted federation member. By this value,
     *      requested federation member is able to uniquely identify the requested org. unit.
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing either an org. units, if the request was processed
     *      properly, or a message informing requester about the error that happened
     *      during request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when request to
     *                   retrieve an org. unit is handled correctly and in this case,
     *                   HTTP response contains requested org. unit in response
     *                   body.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when the request is
     *                   malformed, e.g. the memberIdentifier or uniqueAttributeValue is not
     *                   set or there is no existing membership relation between
     *                   requesting and requested federation members. Another situation
     *                   handled as bad request is when there is no org. unit for provided
     *                   unique attribute value.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     *
     * */
    public Response getOrgUnit(String memberIdentifier, String uniqueAttributeValue);

    /**
     *  <p>
     *      A method that retrieves information about the requested object. The information about
     *      the object are composed of object name and description. The description of the object
     *      depends purely on the specific implementation.
     *  </p>
     *
     *  @param memberIdentifier
     *      unique identifier of federation member in identity federation performing the
     *      request
     *
     *  @param uniqueAttributeValue
     *      A unique value of attribute specified by targeted federation member. By this value
     *      and the value of objectType, requested federation member is able to uniquely
     *      identify the requested object.
     *
     *  @param objectType
     *      A String representation of type of the object that this request needs information
     *      about. This also depends on specific implementation, for example, in Java, this would
     *      be a canonical name of the class.
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing either an object with information about requested
     *      object (ObjectInformation), if the request was processed
     *      properly, or a message informing requester about the error that happened
     *      during request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when request to
     *                   retrieve an org. unit is handled correctly and in this case,
     *                   HTTP response contains requested an org. unit hierarchy in response
     *                   body.
     *
     *      <b>400</b> - response with HTTP cod 400 should be returned when the request is
     *                   malformed, e.g. the memberIdentifier or uniqueAttributeValue is not
     *                   set or there is no existing membership relation between
     *                   requesting and requested federation members. Another situation
     *                   handled as bad request is when there is no org. unit for provided
     *                   unique attribute value.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     * */
    public Response getObjectInformation(String memberIdentifier, String uniqueAttributeValue, String objectType);

    /**
     *  <p>
     *      An operation that retrieves a sharing policy object for org. unit
     *      identified by uniqueAttributeValue.
     *  </p>
     *
     *  @param memberIdentifier (String)
     *      A unique identifier of federation member in identity federation performing the
     *      request.
     *
     *  @param uniqueAttributeValue (String)
     *      A unique value of attribute specified by targeted federation member. By this value,
     *      requested federation member is able to uniquely identify the requested org. unit.
     *      and since there can be only one sharing policy for each org. unit, it will
     *      retrieve it in response object.
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing either a sharing policy of org. unit, if the request was processed
     *      properly, or a message informing requester about the error that happened
     *      during request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when request to
     *                   retrieve a sharing policy for org. unit is handled correctly and in this case,
     *                   HTTP response contains requested sharing policy of org. unit in response
     *                   body.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when the request is
     *                   malformed, e.g. the memberIdentifier or uniqueAttributeValue is not
     *                   set or there is no existing membership relation between
     *                   requesting and requested federation members. Another situation
     *                   handled as bad request is when there is no org. unit for provided
     *                   unique attribute value, so the sharing policy cannot be retrieved
     *                   either.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     *
     * */
    public Response getOrgSharingPolicy(String memberIdentifier, String uniqueAttributeValue);

    /**
     *  <p>
     *      An operation that server as a way to distribute changes in org. units
     *      to other members of identity federation. The requesting member prepares the changes
     *      and sends them for processing to other identity federation members. Federation
     *      member receiving org. object changes are bound to inform the requesting
     *      member about the state of change processing.
     *  </p>
     *
     *  @param orgChange (OrgChangeWrapper)
     *      An object that has several meanings. It contains the identification of federation
     *      member requesting the processing, it contains an identification of org. unit that
     *      is the source of changes and a set of changes.
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing either a sharing policy of org. unit, if the request was processed
     *      properly, or a message informing requester about the error that happened
     *      during request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> -
     *
     *      <b>400</b> -
     *
     *      <b>500</b> -
     *
     * */
    public Response processOrgChanges(OrgChangeWrapper orgChange);
}
