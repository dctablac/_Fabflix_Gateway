package edu.uci.ics.dtablac.service.gateway.threadpool;

import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.connectionpool.ConnectionPoolManager;
import edu.uci.ics.dtablac.service.gateway.core.GatewayDBQuery;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.gateway.util.utility;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Worker extends Thread {
    int id;
    ThreadPool threadPool;

    ClientRequest request;

    private Worker(int id, ThreadPool threadPool) {
        this.id = id;
        this.threadPool = threadPool;
    }

    public static Worker CreateWorker(int id, ThreadPool threadPool) {
        return new Worker(id, threadPool);
    }

    public void process() { // TODO
        this.request = this.threadPool.takeRequest();
        if (this.request != null) {

            // Request a connection
            Connection connection = GatewayService.getConnectionPoolManager().requestCon();

            // Send the request to the proper microservice and update the gateway database
            Response response = utility.handleRequest(this.request);
            ServiceLogger.LOGGER.info("Handling the next request.");

            ServiceLogger.LOGGER.info("Email in the process: "+response.getHeaderString("email"));

            // Update the gateway database
            PreparedStatement ps = GatewayDBQuery.buildAddEntryUpdate(
                    connection, response.getHeaderString("transaction_id"),
                    response.getHeaderString("email"), response.getHeaderString("session_id"),
                    response.getEntity().toString(), response.getStatus());
            GatewayDBQuery.addEntry(ps);

            // Release the connection
            GatewayService.getConnectionPoolManager().releaseCon(connection);
            ServiceLogger.LOGGER.info("Connection released.");
        }
    }

    @Override
    public void run() {
        while (true) {
            process();
        }
    }
}
