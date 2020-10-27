package edu.uci.ics.dtablac.service.gateway.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.configs.BillingConfigs;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.gateway.models.SessionResponseModel;
import edu.uci.ics.dtablac.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.dtablac.service.gateway.threadpool.HTTPMethod;
import edu.uci.ics.dtablac.service.gateway.transaction.TransactionGenerator;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

public class billing {

    public static Response callBilling(String endpointPath, HttpHeaders headers, byte[] jsonBytes) {
        // Validate session
        ObjectMapper mapper = new ObjectMapper();
        Response session_response = utility.sendSessionVerification(headers);
        String jsonText = session_response.readEntity(String.class);
        String session_id = headers.getHeaderString("session_id");

        try {
            SessionResponseModel responseModel = mapper.readValue(jsonText, SessionResponseModel.class);
            if (responseModel.getRESULTCODE() == 134) {
                ServiceLogger.LOGGER.warning("SOMETHING IS UP");
                ServiceLogger.LOGGER.warning(String.format("%d",(responseModel.getRESULTCODE())));
                return session_response;
            }
            session_id = responseModel.getSESSION_ID();
        }
        catch (IOException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Could not read session response");
        }

        // Config path
        BillingConfigs billingConfigs = GatewayService.getBillingConfigs();
        String servicePath = billingConfigs.getScheme() + billingConfigs.getHostName() + ":" +
                             billingConfigs.getPort() + billingConfigs.getPath();

        // Header fields
        String EMAIL = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("Billing has header email: "+EMAIL);
        String SESSION_ID = session_id;
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        Response.ResponseBuilder response = null;

        if (TRANSACTION_ID == null) {
            TRANSACTION_ID = TransactionGenerator.generate();

            // Make a new request and add to the queue
            ClientRequest request = new ClientRequest(EMAIL, SESSION_ID, TRANSACTION_ID,
                    servicePath, endpointPath, HTTPMethod.POST, null, jsonBytes);


            GatewayService.getThreadPool().putRequest(request);
            ServiceLogger.LOGGER.info("Request added to queue.");

            response = Response.status(Response.Status.NO_CONTENT);
            response.header("message", "POST REQUEST TO BILLING");
            response.header("transaction_id", TRANSACTION_ID);
            response.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());


            return response.build();
        }
        return utility.POSTRequest(servicePath, endpointPath, EMAIL, SESSION_ID, TRANSACTION_ID, jsonBytes);
    }

    public static Response callComplete(String endpointPath, HttpHeaders headers, UriInfo uri_info) {
        // Validate session
        //ObjectMapper mapper = new ObjectMapper();
        //Response session_response = utility.sendSessionVerification(headers);
        //String jsonText = session_response.readEntity(String.class);
        //String session_id = headers.getHeaderString("session_id");

        /*try {
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
        }*/

        // Config path
        BillingConfigs billingConfigs = GatewayService.getBillingConfigs();
        String servicePath = billingConfigs.getScheme() + billingConfigs.getHostName() + ":" +
                billingConfigs.getPort() + billingConfigs.getPath();

        // Header fields
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        Response.ResponseBuilder response = null;

        if (TRANSACTION_ID == null) {
            TRANSACTION_ID = TransactionGenerator.generate();

            // Make a new request and add to the queue
            ClientRequest request = new ClientRequest(EMAIL, SESSION_ID, TRANSACTION_ID,
                    servicePath, endpointPath, HTTPMethod.GET, uri_info, null);

            GatewayService.getThreadPool().putRequest(request);
            ServiceLogger.LOGGER.info("Request added to queue.");

            response = Response.status(Response.Status.NO_CONTENT);
            response.header("message", "GET REQUEST TO COMPLETE ORDER");
            response.header("transaction_id", TRANSACTION_ID);
            response.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());

            return response.build();
        }
        return utility.GETRequest(servicePath, endpointPath, EMAIL, SESSION_ID, TRANSACTION_ID, uri_info);
    }
}
