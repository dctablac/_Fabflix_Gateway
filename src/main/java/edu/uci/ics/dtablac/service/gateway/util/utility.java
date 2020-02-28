package edu.uci.ics.dtablac.service.gateway.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.configs.ConfigsModel;
import edu.uci.ics.dtablac.service.gateway.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.gateway.configs.ServiceConfigs;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.gateway.models.SessionRequestModel;
import edu.uci.ics.dtablac.service.gateway.models.SessionResponseModel;
import edu.uci.ics.dtablac.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.dtablac.service.gateway.threadpool.HTTPMethod;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.POST;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class utility {

    public static Response handleRequest(ClientRequest request) {

        // Get the fields of the request
        String email = request.getEmail();
        String session_id = request.getSession_id();
        String transaction_id = request.getTransaction_id();
        HTTPMethod method = request.getMethod();
        byte[] jsonBytes = request.getRequestBytes();
        String uri = request.getURI();
        UriInfo uri_info = request.getUri_info();
        String endpoint = request.getEndpoint();

        Response response = null;

        // Send the appropriate HTTP request
        if (method.equals(HTTPMethod.POST)) {
            response = POSTRequest(uri, endpoint, email, session_id, transaction_id, jsonBytes);
        }
        else if (method.equals(HTTPMethod.GET)) {
            response = GETRequest(uri, endpoint, email, session_id, transaction_id, uri_info);
        }

        return response;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Response sendSessionVerification(HttpHeaders headers) {
        SessionResponseModel responseModel = null;
        SessionRequestModel requestModel = new SessionRequestModel(headers.getHeaderString("email"),
                                                                   headers.getHeaderString("session_id"));

        // Headers
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        // if path starts with /idm
        IdmConfigs idmConfigs = GatewayService.getIdmConfigs();
        String servicePath = idmConfigs.getScheme()+idmConfigs.getHostName()+":"+idmConfigs.getPort()+"/api/idm";
        String endpointPath = idmConfigs.getSessionPath();

        // Create a new client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Create a WebTarget to send a request to
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(servicePath).path(endpointPath);

        // Create an InvocationBuilder to create the HTTP request (bundle request)
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Send the request
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");

        ServiceLogger.LOGGER.info("Received status " + response.getStatus());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, SessionResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped session validation response to POJO.");

            if (responseModel.getRESULTCODE() != 130) {
                Response.ResponseBuilder builder = Response.status(response.getStatus()).entity(responseModel);
                builder.header("email", EMAIL);
                builder.header("session_id", SESSION_ID);
                builder.header("transaction_id", TRANSACTION_ID);

                return builder.build();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to map session validation response to POJO.");
        }
        return null; // Session is good.
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Response POSTRequest(String servicePath, String endpointPath, String EMAIL,
                                       String SESSION_ID, String TRANSACTION_ID, byte[] jsonBytes) {

        // Create a new client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Create a WebTarget to send a request to
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(servicePath).path(endpointPath);

        // Create an InvocationBuilder to create the HTTP request (bundle request)
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Send the request
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(jsonBytes, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");

        ServiceLogger.LOGGER.info("Received status " + response.getStatus());

        Response.ResponseBuilder builder = Response.status(response.getStatus()).entity(response.readEntity(String.class));
        builder.header("email", EMAIL);
        builder.header("session_id", SESSION_ID);
        builder.header("transaction_id", TRANSACTION_ID);

        return builder.build();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Response GETRequest(String servicePath, String endpointPath, String EMAIL,
                                      String SESSION_ID, String TRANSACTION_ID, UriInfo uri_info) {

        // Get query params and path params
        MultivaluedMap<String, String> pathParams = uri_info.getPathParameters();
        Set<String> pathParamKeys = pathParams.keySet();

        MultivaluedMap<String, String> queryParams = uri_info.getQueryParameters();
        Set<String> queryParamKeys = queryParams.keySet();

        // Create a new client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Create a WebTarget to send a request to
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(servicePath).path(endpointPath);

        // Add query params
        queryParamKeys.forEach(x -> webTarget.queryParam(x, queryParams.get(x).get(0)));

        // Add path params                                                  // Might be incorrect to get path param
        pathParamKeys.forEach(x -> webTarget.path(pathParams.get(x).get(0))); // This might not be storing the new webTarget

        // test print webtarget
        System.out.println(webTarget.getUri().toString());

        // Create an InvocationBuilder to create the HTTP request (bundle request)
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Send the request
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.get();
        ServiceLogger.LOGGER.info("Request sent.");

        ServiceLogger.LOGGER.info("Received status " + response.getStatus());

        Response.ResponseBuilder builder = Response.status(response.getStatus()).entity(response.readEntity(String.class));
        builder.header("email", EMAIL);
        builder.header("session_id", SESSION_ID);
        builder.header("transaction_id", TRANSACTION_ID);

        return builder.build();
    }
}
