package edu.uci.ics.dtablac.service.gateway.threadpool;

import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.connectionpool.ConnectionPoolManager;
import edu.uci.ics.dtablac.service.gateway.core.GatewayDBQuery;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.gateway.util.utility;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Worker extends Thread {
    int id;
    ThreadPool threadPool;

    ClientRequest request;
    Connection connection;

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
            ConnectionPoolManager pool = GatewayService.getConnectionPoolManager();
            this.connection = pool.requestCon();

            // Send the request to the proper microservice and update the gateway database
            Response response = utility.handleRequest(this.request);
            ServiceLogger.LOGGER.info("Handling the next request.");

            // Update the gateway database
            PreparedStatement ps = GatewayDBQuery.buildAddEntryUpdate(
                    this.connection, response.getHeaderString("transaction_id"),
                    response.getHeaderString("email"), response.getHeaderString("session_id"),
                    response.getEntity().toString(), response.getStatus());
            GatewayDBQuery.addEntry(ps);

            // Release the connection
            pool.releaseCon(this.connection);
        }
    }

    @Override
    public void run() {
        while (true) {
            process();
        }
    }
}
