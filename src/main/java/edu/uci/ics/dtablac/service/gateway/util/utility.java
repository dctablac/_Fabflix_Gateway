package edu.uci.ics.dtablac.service.gateway.util;

import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.gateway.models.SessionRequestModel;
import edu.uci.ics.dtablac.service.gateway.models.SessionResponseModel;
import edu.uci.ics.dtablac.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.dtablac.service.gateway.threadpool.HTTPMethod;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
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

        ServiceLogger.LOGGER.info("Worker is handling email: "+email);
        ServiceLogger.LOGGER.info("Worker is handling sesh_id: "+session_id);

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
        String servicePath = idmConfigs.getScheme()+idmConfigs.getHostName()+":"+idmConfigs.getPort()+idmConfigs.getPath();
        String endpointPath = idmConfigs.getSessionPath();
        ServiceLogger.LOGGER.warning(servicePath+endpointPath);

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

        ServiceLogger.LOGGER.warning(requestModel.getEMAIL()+"  "+requestModel.getSESSION_ID());

        // Send the request
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");

        return response; // session good. get sesh_id
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Response POSTRequest(String servicePath, String endpointPath, String EMAIL,
                                       String SESSION_ID, String TRANSACTION_ID, byte[] jsonBytes) {

        ServiceLogger.LOGGER.warning("THIS GUY HAS EMAIL" +EMAIL);

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
        invocationBuilder.header("email", EMAIL);
        invocationBuilder.header("session_id", SESSION_ID);
        invocationBuilder.header("transaction_id", TRANSACTION_ID);

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
        Object[] finalPathParamKeys = pathParamKeys.toArray();

        MultivaluedMap<String, String> queryParams = uri_info.getQueryParameters();
        Set<String> queryParamKeys = queryParams.keySet();
        Object[] finalQueryParamKeys = queryParamKeys.toArray();

        // Create a new client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Create a WebTarget to send a request to
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(servicePath).path(endpointPath);

        // Add path params
        int pathCount = pathParamKeys.size();
        for (int i = 0; i < pathCount; i++) {
            String key = (String)finalPathParamKeys[i];
            String value = pathParams.get(key).get(0);
            webTarget = webTarget.path(value);
        }

        // Add query params
        int queryCount = queryParamKeys.size();
        for (int i = 0; i < queryCount; i++) {
            String key = (String)finalQueryParamKeys[i];
            String value = queryParams.get(key).get(0);
            webTarget = webTarget.queryParam(key, value);
        }

        // Create an InvocationBuilder to create the HTTP request (bundle request)
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        invocationBuilder.header("email", EMAIL);
        invocationBuilder.header("session_id", SESSION_ID);
        invocationBuilder.header("transaction_id", TRANSACTION_ID);

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
