package edu.uci.ics.dtablac.service.gateway.core;

import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.connectionpool.ConnectionPoolManager;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GatewayDBQuery {

    public static PreparedStatement buildGatewayDBQuery(Connection connection, String transaction_id) {
        String SELECT = "\nSELECT *\n";
        String FROM = "FROM responses\n";
        String WHERE = "WHERE transaction_id = ?;";

        String query = SELECT + FROM + WHERE;

        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, transaction_id);
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to build query to access the gateway's database.");
        }
        return ps;
    }

    public static PreparedStatement buildAddEntryUpdate(Connection connection, String transaction_id, String email,
                                                        String session_id, String response, int http_status) {
        String INSERT = "\nINSERT INTO responses(transaction_id, email, session_id, response, http_status)\n";
        String VALUES = "VALUES(?,?,?,?,?);";

        String query = INSERT + VALUES;

        PreparedStatement ps = null;
        try {
            ps = GatewayService.getConnectionPoolManager().requestCon().prepareStatement(query);
            ps.setString(1, transaction_id);
            ps.setString(2, email);
            ps.setString(3, session_id);
            ps.setString(4, response);
            ps.setInt(5, http_status);
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to build update to add to the gateway database.");
        }
        return ps;
    }

    public static PreparedStatement buildDeleteEntryUpdate(Connection connection, String transaction_id) {
        String DELETE = "\nDELETE\n";
        String FROM = "FROM responses\n";
        String WHERE = "WHERE transaction_id = ?";

        String query = DELETE + FROM + WHERE;

        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, transaction_id);
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to build update to delete from the gateway database.");
        }
        return ps;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static ResultSet sendGatewayDBQuery(PreparedStatement ps) {
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve if a response is in the gateway db.");
        }
        return rs;
    }

    public static int addEntry(PreparedStatement ps) {
        int added = 0;
        try {
            added = ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Update failed: Unable to add the entry to the gateway db.");
        }
        return added;
    }

    public static int deleteEntry(PreparedStatement ps) {
        int affected = 0;
        try {
            affected = ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Update failed: Unable to delete the entry from the response database.");
        }
        return affected;
    }
}
