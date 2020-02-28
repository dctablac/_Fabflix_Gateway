package edu.uci.ics.dtablac.service.gateway.resources;

import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.gateway.util.idm;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

@Path("idm")
public class IdmPage {

    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@Context HttpHeaders headers, byte[] jsonBytes) {
        return idm.callIdm(GatewayService.getIdmConfigs().getRegisterPath(), headers, jsonBytes);
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpHeaders headers, byte[] jsonBytes) {
        return idm.callIdm(GatewayService.getIdmConfigs().getLoginPath(), headers, jsonBytes);
    }

    @Path("session")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response session(@Context HttpHeaders headers, byte[] jsonBytes) {
        return idm.callIdm(GatewayService.getIdmConfigs().getSessionPath(), headers, jsonBytes);
    }

    @Path("privilege")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response privilege(@Context HttpHeaders headers, byte[] jsonBytes) {
        return idm.callIdm(GatewayService.getIdmConfigs().getPrivilegePath(), headers, jsonBytes);
    }
}
