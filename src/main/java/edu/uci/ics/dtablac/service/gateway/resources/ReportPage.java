package edu.uci.ics.dtablac.service.gateway.resources;

import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.gateway.models.ResponseModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("g")
public class ReportPage {
    @Path("report")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response report(@Context HttpHeaders headers) {
        // Header fields
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        System.out.println("Reporting");
        ServiceLogger.LOGGER.info("Report endpoint.");
        return Response.status(Response.Status.OK).build();
    }


}
