package edu.uci.ics.dtablac.service.gateway.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.configs.MoviesConfigs;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.gateway.models.SessionResponseModel;
import edu.uci.ics.dtablac.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.dtablac.service.gateway.threadpool.HTTPMethod;
import edu.uci.ics.dtablac.service.gateway.transaction.TransactionGenerator;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

public class movies {

    public static Response callMovies(String endpointPath, HttpHeaders headers, UriInfo uri_info) {
        // Validate session
        Response session_response = utility.sendSessionVerification(headers);
        String session_id = headers.getHeaderString("session_id");

        try {
            ObjectMapper mapper = new ObjectMapper();
            ServiceLogger.LOGGER.warning("Trying to make jsonText");
            String jsonText = session_response.readEntity(String.class);
            ServiceLogger.LOGGER.warning(jsonText);
            ServiceLogger.LOGGER.warning("OK");
            ServiceLogger.LOGGER.warning("Trying to make response model");
            SessionResponseModel responseModel = mapper.readValue(jsonText, SessionResponseModel.class);
            ServiceLogger.LOGGER.warning("Success");
            if (responseModel.getRESULTCODE() != 130) {
                ServiceLogger.LOGGER.warning("SOMETHING IS UP");
                ServiceLogger.LOGGER.warning(responseModel.getMESSAGE());

                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            session_id = responseModel.getSESSION_ID();
        }
        catch (IOException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Could not read session response");
        }

        // Config path
        MoviesConfigs moviesConfigs = GatewayService.getMoviesConfigs();
        String servicePath = moviesConfigs.getScheme() + moviesConfigs.getHostName() + ":" +
                moviesConfigs.getPort() + moviesConfigs.getPath();

        // Header fields
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = session_id; // new one to return to update front end
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        Response.ResponseBuilder response = null;


        if (TRANSACTION_ID == null) {
            TRANSACTION_ID = TransactionGenerator.generate();

            // Make a new request and add to the queue
            ClientRequest request = new ClientRequest(EMAIL, SESSION_ID, TRANSACTION_ID,
                    servicePath, endpointPath, HTTPMethod.GET, uri_info , null);

            GatewayService.getThreadPool().putRequest(request);
            ServiceLogger.LOGGER.info("Request added to queue.");

            response = Response.status(Response.Status.NO_CONTENT);
            response.header("message", "GET REQUEST TO MOVIES");
            response.header("transaction_id", TRANSACTION_ID);
            response.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());

            return response.build();
        }
        return utility.GETRequest(servicePath, endpointPath, EMAIL, SESSION_ID, TRANSACTION_ID, uri_info);
    }

    public static Response callThumbnail(String endpointPath, HttpHeaders headers, byte[] jsonBytes) {
        // Validate session
        ObjectMapper mapper = new ObjectMapper();
        Response session_response = utility.sendSessionVerification(headers);
        String jsonText = session_response.readEntity(String.class);
        String session_id = headers.getHeaderString("session_id");

        try {
            SessionResponseModel responseModel = mapper.readValue(jsonText, SessionResponseModel.class);
            if (responseModel.getRESULTCODE() != 130) {
                ServiceLogger.LOGGER.warning("SOMETHING IS UP");
                return session_response;
            }
            session_id = responseModel.getSESSION_ID();
        }
        catch (IOException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Could not read session response");
        }

        // Config path
        MoviesConfigs moviesConfigs = GatewayService.getMoviesConfigs();
        String servicePath = moviesConfigs.getScheme() + moviesConfigs.getHostName() + ":" +
                             moviesConfigs.getPort() + moviesConfigs.getPath();

        // Header fields
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = session_id;
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        Response.ResponseBuilder response = null;

        if (TRANSACTION_ID == null) {
            TRANSACTION_ID = TransactionGenerator.generate();

            // Make a new request and add to the queue
            ClientRequest request = new ClientRequest(EMAIL, SESSION_ID, TRANSACTION_ID,
                    servicePath, endpointPath, HTTPMethod.POST, null , jsonBytes);

            GatewayService.getThreadPool().putRequest(request);
            ServiceLogger.LOGGER.info("Request added to queue.");

            response = Response.status(Response.Status.NO_CONTENT);
            response.header("message", "POST REQUEST TO THUMBNAIL");
            response.header("transaction_id", TRANSACTION_ID);
            response.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());

            return response.build();
        }
        return utility.POSTRequest(servicePath, endpointPath, EMAIL, SESSION_ID, TRANSACTION_ID, jsonBytes);
    }
}
