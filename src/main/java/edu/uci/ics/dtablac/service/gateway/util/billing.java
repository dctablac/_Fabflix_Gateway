package edu.uci.ics.dtablac.service.gateway.util;

import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.configs.BillingConfigs;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.dtablac.service.gateway.threadpool.HTTPMethod;
import edu.uci.ics.dtablac.service.gateway.transaction.TransactionGenerator;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class billing {

    public static Response callBilling(String endpointPath, HttpHeaders headers, byte[] jsonBytes) {
        // Validate session
        Response session_response = utility.sendSessionVerification(headers);
        if (session_response != null) {
            return session_response;
        }

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
                    servicePath, endpointPath, HTTPMethod.POST, null , jsonBytes);

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
        Response session_response = utility.sendSessionVerification(headers);
        if (session_response != null) {
            return session_response;
        }

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
