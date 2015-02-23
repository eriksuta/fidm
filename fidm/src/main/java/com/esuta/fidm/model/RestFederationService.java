package com.esuta.fidm.model;

import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *  @author shood
 * */
@Path("/rest")
public class RestFederationService implements IFederationService{

//    Logger LOGGER = Logger.getLogger(RestFederationService.class);
//
//    @GET
//    @Path("/federationRequest")
//    public Response getFederationRequest(){
//        LOGGER.debug("Federation request received");
//
//        String output = "Federation request was received.";
//        return Response.status(200).entity(output).build();
//    }
}



