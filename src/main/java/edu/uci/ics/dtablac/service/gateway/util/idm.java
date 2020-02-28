package edu.uci.ics.dtablac.service.gateway.util;

import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.dtablac.service.gateway.threadpool.HTTPMethod;
import edu.uci.ics.dtablac.service.gateway.transaction.TransactionGenerator;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

public class idm {

    // 204 is returned after the request has been put into the queue and the idm is called?? session_id too.
    public static Response callIdm(String endpointPath, HttpHeaders headers, byte[] jsonBytes) {

        // Config path
        IdmConfigs idmConfigs = GatewayService.getIdmConfigs();
        String servicePath = idmConfigs.getScheme() + idmConfigs.getHostName() + ":" +
                             idmConfigs.getPort() + idmConfigs.getPath();

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

            GatewayService.getThreadPool().putRequest(request); // TODO: This is not being added to the queue.

            ServiceLogger.LOGGER.info("Request added to queue.");

            response = Response.status(Response.Status.NO_CONTENT);
            response.header("message", "POST REQUEST TO IDM");
            response.header("transaction_id", TRANSACTION_ID);
            response.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());

            return response.build();
        }

        return utility.POSTRequest(servicePath, endpointPath, EMAIL, SESSION_ID, TRANSACTION_ID, jsonBytes);
    }
}
