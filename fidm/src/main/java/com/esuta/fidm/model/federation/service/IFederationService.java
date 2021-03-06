package com.esuta.fidm.model.federation.service;

import javax.ws.rs.core.Response;

/**
 *  @author shood
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
    Response getFederationIdentifier();

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
    Response handleFederationRequest(FederationMembershipRequest membershipRequest);

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
    Response handleFederationResponse(FederationMembershipRequest membershipResponse);

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
    Response handleFederationDeleteRequest(FederationMembershipRequest deletionRequest);

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
    Response handleFederationDeleteResponse(FederationMembershipRequest deletionResponse);

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
    Response getSharedOrgUnitCount(String memberIdentifier);

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
    Response getSharedOrgUnits(String memberIdentifier);

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
    Response getOrgUnit(String memberIdentifier, String uniqueAttributeValue);

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
    Response getObjectInformation(String memberIdentifier, String uniqueAttributeValue, String objectType);

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
    Response getOrgSharingPolicy(String memberIdentifier, String uniqueAttributeValue);

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
     *      <b>200</b> - response with HTTP code 200 should be returned when request to process
     *                   org. unit changes is handled correctly and the body of response will
     *                   contain a success message.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when the request is
     *                   malformed, e.g. the memberIdentifier or uniqueAttributeValue is not
     *                   set or there is no existing membership relation between
     *                   requesting and requested federation members. Another situation
     *                   handled as bad request is when there is no org. unit for provided
     *                   unique attribute value, so the changes cannot be processed.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     *
     * */
    Response processOrgChanges(OrgModificationWrapper orgChange);

    /**
     *  <p>
     *      A method with purpose of removal of link to copy of org. unit from origin org. unit.
     *      This request is sent by federation member when not-local org. unit was deleted from
     *      the repository. It is a way to inform the origin that one of org. unit copies was
     *      deleted. The origin org. unit should remove the link to a copy and inform the
     *      requester about this event.
     *  </p>
     *
     *  @param memberIdentifier (String)
     *      A unique identifier of federation member in identity federation performing the
     *      request.
     *
     *  @param uniqueAttributeValue (String)
     *      A unique value of attribute specified by targeted federation member. By this value,
     *      requested federation member is able to uniquely identify the requested org. unit.
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing either a sharing policy of org. unit, if the request was processed
     *      properly, or a message informing requester about the error that happened
     *      during request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when the link to the copy
     *                   of org. unit was removed without problems.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when the request is
     *                   malformed, e.g. the memberIdentifier or uniqueAttributeValue is not
     *                   set or there is no existing membership relation between
     *                   requesting and requested federation members. Another situation
     *                   handled as bad request is when there is no org. unit for provided
     *                   unique attribute value, so the link to removed org. unit copy
     *                   cannot be removed as well.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     *
     * */
    Response removeOrgLink(String memberIdentifier, String uniqueAttributeValue);

    /**
     *  <p>
     *      A method with the purpose of informing all copies of org. unit in identity
     *      federation that origin org. unit has been removed. The standard procedure in
     *      this case is to remove the copies in members of identity federation along with all
     *      implied account and role inducements. All children of removed org. unit should be
     *      made root org. units.
     *  </p>
     *
     *  @param memberIdentifier (String)
     *      A unique identifier of federation member in identity federation performing the
     *      request.
     *
     *  @param uniqueAttributeValue (String)
     *      A unique value of attribute specified by targeted federation member. By this value,
     *      requested federation member is able to uniquely identify the requested org. unit.
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing an information, if the request was processed
     *      properly, or a message informing requester about the error that happened
     *      during request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when the copy
     *                   of org. unit was removed without problems.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when the request is
     *                   malformed, e.g. the memberIdentifier or uniqueAttributeValue is not
     *                   set or there is no existing membership relation between
     *                   requesting and requested federation members. Another situation
     *                   handled as bad request is when there is no org. unit for provided
     *                   unique attribute value, so the org. unit copy cannot be removed.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     * */
    Response originOrgRemoved(String memberIdentifier, String uniqueAttributeValue);

    /**
     *  <p>
     *      A method that is responsible for remote account creation. Remote account needs to be
     *      created when a user of one member of identity federation needs an account on another
     *      member of identity federation.
     *  </p>
     *
     *  @param accountWrapper
     *      A wrapper object for information about the account. It contains following fields
     *      (All fields are requested and must not be empty):
     *          - memberIdentifier - an identifier of a requesting member of identity federation
     *          - accountName - a name of the account to create (may be altered by the service to be unique)
     *          - resourceUniqueAttributeValue - a name of the resource on which we want the new account
     *          - password - password for requested account
     *          - ownerIdentifier - a FederationIdentifierType - an identifier of remote
     *                      owner of created account
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing a message informing requester about the state of the
     *      request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when the new account
     *                   was created without any problems. In this case, the message contains
     *                   a unique identifier of created account.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when the request is
     *                   malformed, e.g. some parameters of accountWrapper are not
     *                   set or there is no existing membership relation between
     *                   requesting and requested federation members. Another situation
     *                   handled as bad request is when there is no resource for provided
     *                   requested account, so the identity provider is not able to create it.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     * */
    Response requestAccount(AccountRequestWrapper accountWrapper);

    /**
     *  <p>
     *      This method retrieves a remote account specified by a unique attribute
     *      defining this account in requested identity provider.
     *  </p>
     *
     *  @param memberIdentifier
     *      A unique identifier of federation member in identity federation performing the
     *      request.
     *
     *  @param uniqueAccountIdentifier
     *      An identifier of an account in local identity provider that we are looking for.
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing a message informing requester about the state of the
     *      request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when the request
     *                   was processed correctly. In this case, the message contains
     *                   an account.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when the request is
     *                   malformed, e.g. some parameters of method are not set or there is no
     *                   existing membership relation between  requesting and requested federation
     *                   members.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     *
     * */
    Response getAccount(String memberIdentifier, String uniqueAccountIdentifier);

    /**
     *  <p>
     *      This method finds out if a certain user from remote member of identity federation has
     *      a remote account in this identity federation on a specified resource. If this account is
     *      present, it also removes it. It returns an information about retrieval/removal process
     *      in form of a success/error message.
     *  </p>
     *
     *  @param memberIdentifier
     *      A unique identifier of federation member in identity federation performing the
     *      request.
     *
     *  @param uniqueAccountIdentifier
     *      An identifier of an account in local identity provider that we are trying to
     *      remove
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing a message informing requester about the state of the
     *      request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when the request
     *                   was processed correctly. In this case, The account specified
     *                   by method parameter was removed.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when the request is
     *                   malformed, e.g. some parameters of method are not set or there is no
     *                   existing membership relation between  requesting and requested federation
     *                   members. Another situation is when the account specified as parameter
     *                   of this method does not exist.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     *
     * */
    Response removeAccount(String memberIdentifier, String uniqueAccountIdentifier);

    /**
     *  <p>
     *      This method retrieves a list of subjects that are members of org. unit
     *      specified by the attribute of method (uniqueOrgIdentifier).
     *  </p>
     *
     *  @param memberIdentifier
     *      A unique identifier of federation member in identity federation performing the
     *      request.
     *
     *  @param uniqueOrgIdentifier
     *      An identifier of org. unit in requested member of identity federation.
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing a message informing requester about the state of the
     *      request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when the request
     *                   was processed correctly. In this case, the response contains
     *                   a list of members of org. unit specified by attribute 'uniqueAccountIdentifier'
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when the request is
     *                   malformed, e.g. some parameters of method are not set or there is no
     *                   existing membership relation between  requesting and requested federation
     *                   members. Another situation is when the org. unit specified as parameter
     *                   of this method does not exist.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     *
     * */
    Response getOrgMembers(String memberIdentifier, String uniqueOrgIdentifier);

    /**
     *  <p>
     *      This method returns all available resources for the federation member identified
     *      by specified parameter. It should return a list of identifiers to these resources.
     *  </p>
     *
     *  @param memberIdentifier
     *      A unique identifier of federation member in identity federation performing the
     *      request.
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing a message informing requester about the state of the
     *      request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when the request
     *                   was processed correctly. In this case, the response contains
     *                   a list of identifiers of resources that are available to requester.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when the request is
     *                   malformed, e.g. some parameters of method are not set or there is no
     *                   existing membership relation between requesting and requested federation
     *                   members.
     *                   *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     *
     * */
    Response getAvailableResources(String memberIdentifier);

    /**
     *  <p>
     *      This method handles a login to remote resource. It returns a response with an
     *      information about the result of login operation.
     *  </p>
     *
     *  @param memberIdentifier
     *      A unique identifier of federation member in identity federation performing the
     *      request.
     *
     *  @param resourceName
     *      The name of the resource (unique identifier) to which the subject is trying to
     *      get access to
     *
     *  @param accountName
     *      The name of the account of the subject on the remote resource.
     *
     *  @param password
     *      The password to account
     *
     *  @return javax.ws.rs.core.Response
     *      A HTTP response containing a message informing requester about the state of the
     *      request processing. Following HTTP codes may be thrown:
     *
     *      <b>200</b> - response with HTTP code 200 should be returned when the request
     *                   was processed correctly. In this case, the response contains
     *                   an AuthResult object that informs the user about the result of
     *                   authentication. Even when user is not granted access to requested
     *                   resource, HTTP code is thrown.
     *
     *      <b>400</b> - response with HTTP code 400 should be returned when the request is
     *                   malformed, e.g. some parameters of method are not set or there is no
     *                   existing membership relation between requesting and requested federation
     *                   members.
     *
     *      <b>500</b> - response with HTTP code 500 should be returned when there is an internal
     *                   error on the server side of federation member processing the request, such
     *                   as problems with reading objects in repository.
     * */
    Response remoteLogin(String memberIdentifier, String resourceName, String accountName, String password);
}
