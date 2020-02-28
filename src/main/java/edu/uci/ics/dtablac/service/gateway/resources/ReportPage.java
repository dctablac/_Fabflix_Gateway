package edu.uci.ics.dtablac.service.gateway.resources;

import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.connectionpool.ConnectionPoolManager;
import edu.uci.ics.dtablac.service.gateway.core.GatewayDBQuery;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

// TODO

@Path("report")
public class ReportPage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response report(@Context HttpHeaders headers) {
        // Declare response
        Response response = null;

        // Header fields
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        String MESSAGE = "This is a message.";
        long REQUEST_DELAY = GatewayService.getThreadConfigs().getRequestDelay();

        // Get a connection
        ConnectionPoolManager pool = GatewayService.getConnectionPoolManager();
        Connection connection = pool.requestCon();

        ResultSet rs = GatewayDBQuery.sendGatewayDBQuery(GatewayDBQuery.buildGatewayDBQuery(connection, TRANSACTION_ID));
        try {
            if (rs.next()) { // If transaction exists in the database, (send response to user), then delete it.
                int http_status = rs.getInt("http_status");
                String responseStr = rs.getString("response");
                Response.ResponseBuilder responseBuilder = Response.status(http_status).entity(responseStr);
                GatewayDBQuery.deleteEntry(GatewayDBQuery.buildDeleteEntryUpdate(connection, TRANSACTION_ID));
                response = responseBuilder.build();
                ServiceLogger.LOGGER.info("Response was found.");
            }
            else { // If no transaction_id is mapped, send response 204 (no content).
                Response.ResponseBuilder responseBuilder = Response.status(Response.Status.NO_CONTENT);
                responseBuilder.header("message", MESSAGE);
                responseBuilder.header("request_delay", REQUEST_DELAY);
                responseBuilder.header("transaction_id", TRANSACTION_ID);

                response = responseBuilder.build();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Something went wrong querying from the gateway's database.");
        }

        // Release the connection
        pool.releaseCon(connection);

        return response;
    }
}
