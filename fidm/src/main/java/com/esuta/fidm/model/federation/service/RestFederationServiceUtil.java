package com.esuta.fidm.model.federation.service;

import java.io.Serializable;

/**
 *  @author shood
 * */
public class RestFederationServiceUtil implements Serializable{

    public static final String REST_SERVICE_PATH = "/rest";

    public static final String GET_FEDERATION_MEMBER_IDENTIFIER = "/getIdentifier";
    public static final String POST_FEDERATION_REQUEST = "/federationRequest";
    public static final String POST_FEDERATION_REQUEST_RESPONSE = "/federationResponse";
    public static final String POST_FEDERATION_DELETION_REQUEST = "/federationDeleteRequest";
    public static final String POST_FEDERATION_DELETION_RESPONSE = "/federationDeleteResponse";

    public static final String GET_SHARED_ORG_UNIT_COUNT = "/getSharedOrgUnitCount/";
    public static final String GET_SHARED_ORG_UNIT = "/getSharedOrgUnits/";
    public static final String GET_SHARED_ORG_UNIT_COUNT_PARAM = "/getSharedOrgUnitCount/{memberIdentifier}";
    public static final String GET_SHARED_ORG_UNIT_PARAM = "/getSharedOrgUnits/{memberIdentifier}";
    public static final String GET_ORG_UNIT = "/getOrgUnit/";
    public static final String GET_ORG_UNIT_PARAM = "/getOrgUnit/{memberIdentifier}/{uniqueAttributeValue}";
    public static final String GET_ORG_SHARING_POLICY = "/getSharingPolicy/";
    public static final String GET_ORG_SHARING_POLICY_PARAM = "/getSharingPolicy/{memberIdentifier}/{uniqueAttributeValue}";
    public static final String GET_OBJECT_INFORMATION = "/getObjectInformation/";
    public static final String GET_OBJECT_INFORMATION_PARAM = "/getObjectInformation/{memberIdentifier}/{uniqueAttributeValue}/{objectType}";

    public static final String POST_PROCESS_ORG_CHANGES = "/processOrgChanges";
    public static final String GET_REMOVE_ORG_LINK_PARAM = "/removeOrgLink/{memberIdentifier}/{uniqueAttributeValue}";
    public static final String GET_REMOVE_ORG_LINK = "/removeOrgLink/";

    public static final String POST_REQUEST_ACCOUNT = "/requestAccount";
    public static final String GET_ACCOUNT_PARAM = "/getAccount/{memberIdentifier}/{uniqueAccountIdentifier}";
    public static final String GET_ACCOUNT = "/getAccount/";
    public static final String GET_REMOVE_ACCOUNT_PARAM = "/removeAccount/{memberIdentifier}/{uniqueAccountIdentifier}";
    public static final String GET_REMOVE_ACCOUNT = "/removeAccount/";

    public static final String GET_ORG_MEMBERS_PARAM = "/getOrgMembers/{memberIdentifier}/{uniqueOrgIdentifier}";
    public static final String GET_ORG_MEMBERS = "/getOrgMembers/";

    public static String createGetFederationMemberIdentifier(String address, int port){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(GET_FEDERATION_MEMBER_IDENTIFIER);
        return sb.toString();
    }

    public static String createFederationRequestUrl(String address, int port){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(POST_FEDERATION_REQUEST);
        return sb.toString();
    }

    public static String createFederationRequestResponseUrl(String address, int port){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(POST_FEDERATION_REQUEST_RESPONSE);
        return sb.toString();
    }

    public static String createFederationDeleteRequestUrl(String address, int port){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(POST_FEDERATION_DELETION_REQUEST);
        return sb.toString();
    }

    public static String createFederationDeleteResponseUrl(String address, int port){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(POST_FEDERATION_DELETION_RESPONSE);
        return sb.toString();
    }

    public static String createGetSharedOrgUnitCountUrl(String address, int port, String memberIdentifier){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(GET_SHARED_ORG_UNIT_COUNT);
        sb.append(memberIdentifier);
        return sb.toString();
    }

    public static String createGetSharedOrgUnitUrl(String address, int port, String memberIdentifier){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(GET_SHARED_ORG_UNIT);
        sb.append(memberIdentifier);
        return sb.toString();
    }

    public static String createGetOrgUnitUrl(String address, int port, String memberIdentifier, String uniqueAttributeValue){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(GET_ORG_UNIT);
        sb.append(memberIdentifier);
        sb.append("/");
        sb.append(uniqueAttributeValue);
        return sb.toString();
    }

    public static String createGetOrgSharingPolicyUrl(String address, int port, String memberIdentifier, String uniqueAttributeValue){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(GET_ORG_SHARING_POLICY);
        sb.append(memberIdentifier);
        sb.append("/");
        sb.append(uniqueAttributeValue);
        return sb.toString();
    }

    public static String createGetObjectInformationUrl(String address, int port, String memberIdentifier, String uniqueAttributeValue, String objectType){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(GET_OBJECT_INFORMATION);
        sb.append(memberIdentifier);
        sb.append("/");
        sb.append(uniqueAttributeValue);
        sb.append("/");
        sb.append(objectType);
        return sb.toString();
    }

    public static String createPostProcessOrgChangesRequestUrl(String address, int port){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(POST_PROCESS_ORG_CHANGES);
        return sb.toString();
    }

    public static String createGetRemoveOrgLinkUrl(String address, int port, String memberIdentifier, String uniqueAttributeValue){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(GET_REMOVE_ORG_LINK);
        sb.append(memberIdentifier);
        sb.append("/");
        sb.append(uniqueAttributeValue);
        return sb.toString();
    }

    public static String createPostRequestAccountUrl(String address, int port){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(POST_REQUEST_ACCOUNT);
        return sb.toString();
    }

    public static String createGetAccountUrl(String address, int port, String memberIdentifier,
                                             String accountIdentifier){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(GET_ACCOUNT);
        sb.append(memberIdentifier);
        sb.append("/");
        sb.append(accountIdentifier);

        return sb.toString();
    }

    public static String createGetRemoveAccountUrl(String address, int port, String memberIdentifier,
                                                   String accountIdentifier){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(GET_REMOVE_ACCOUNT);
        sb.append(memberIdentifier);
        sb.append("/");
        sb.append(accountIdentifier);
        return sb.toString();
    }

    public static String createGetOrgMembersUrl(String address, int port, String memberIdentifier,
                                                String orgIdentifier){
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(address);
        sb.append(":");
        sb.append(port);
        sb.append(REST_SERVICE_PATH);
        sb.append(GET_ORG_MEMBERS);
        sb.append(memberIdentifier);
        sb.append("/");
        sb.append(orgIdentifier);
        return sb.toString();
    }
}
