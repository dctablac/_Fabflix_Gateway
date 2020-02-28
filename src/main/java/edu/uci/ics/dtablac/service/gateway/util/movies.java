package edu.uci.ics.dtablac.service.gateway.util;

import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.gateway.configs.MoviesConfigs;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.dtablac.service.gateway.threadpool.HTTPMethod;
import edu.uci.ics.dtablac.service.gateway.transaction.TransactionGenerator;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class movies {

    public static Response callMovies(String endpointPath, HttpHeaders headers, UriInfo uri_info) {

        // Config path
        MoviesConfigs moviesConfigs = GatewayService.getMoviesConfigs();
        String servicePath = moviesConfigs.getScheme() + moviesConfigs.getHostName() + ":" +
                moviesConfigs.getPort() + moviesConfigs.getPath();

        // Header fields
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        Response.ResponseBuilder response = null;

        if (TRANSACTION_ID == null) {
            TRANSACTION_ID = TransactionGenerator.generate();

            // Make a new request and add to the queue
            ClientRequest request = new ClientRequest(EMAIL, SESSION_ID, TRANSACTION_ID,
                    servicePath, endpointPath, HTTPMethod.GET, uri_info , null);

            GatewayService.getThreadPool().putRequest(request); // TODO: This is not being added to the queue.

            ServiceLogger.LOGGER.info("Request added to queue.");

            response = Response.status(Response.Status.NO_CONTENT);
            response.header("message", "POST REQUEST TO IDM");
            response.header("transaction_id", TRANSACTION_ID);
            response.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());

            return response.build();
        }
        return utility.GETRequest(servicePath, endpointPath, EMAIL, SESSION_ID, TRANSACTION_ID, uri_info);
    }
}
