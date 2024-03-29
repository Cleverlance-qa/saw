package corehelpers.dbreaders;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for all database reader classes
 */
public class DBReader {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected String server;
    protected int port;
    protected String databaseName;
    protected String user;
    protected String password;
    protected String instanceName;
    protected String sid;
    protected Connection con;

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getSid() {
        return sid;
    }

    /**
     * Run database query.
     * Method expected SELECT as a query and just a one row as a result for better filtering.
     * @param query - SQL query
     * @param columnLabel - return value will be taken from this column
     * @return - return single value from first row from query and from given column
     */
    public String runQuery(String query, String columnLabel) {
        String result = null;
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)){
            while(rs.next()) {
                result = rs.getString(columnLabel);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Error when procesing SQL query", e);
        }

        return result;
    }

    /**
     * Run database query.
     * Method expected SELECT as a query and just a one row as a result for better filtering.
     * @param query - SQL query
     * @return - return result as a HashMap where key is column name and value is column value
     */
    public HashMap<String, String> runQuery(String query) {
        final HashMap<String, String> result = new HashMap<>();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)){
            while(rs.next()) {
                final ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i < rsmd.getColumnCount()+1; i++) {
                    if(rs.getString(i) == null) {
                        result.put(rsmd.getColumnName(i), "null");
                    } else {
                        result.put(rsmd.getColumnName(i), rs.getString(i));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Error when procesing SQL query", e);
        } catch (NoClassDefFoundError e) {
            LOGGER.log(Level.WARNING, "NoClassDefFoundError", e);
        }
        return result;
    }

    /**
     * Run database query.
     * Method expected SELECT as a query and return whole resultset as List.
     * @param query - SQL query
     * @return - return result as List of HashMap where key is column name and value is column value
     */
    public List<HashMap<String,String>> runQueryFull(String query) {
        List<HashMap<String,String>> resultList = new ArrayList<>();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)){
            while(rs.next()) {
                final HashMap<String, String> result = new HashMap<>();
                final ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i < rsmd.getColumnCount()+1; i++) {
                    if(rs.getString(i) == null) {
                        result.put(rsmd.getColumnName(i), "null");
                    } else {
                        result.put(rsmd.getColumnName(i), rs.getString(i));
                    }
                }
                resultList.add(result);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Error when procesing SQL query", e);
        } catch (NoClassDefFoundError e) {
            LOGGER.log(Level.WARNING, "NoClassDefFoundError", e);
        }
        return resultList;
    }

    /**
     * Run database query.
     * Method expected UPDATE as a query and return number of updated lines.
     * @param query - SQL query
     */
    public void runUpdateQuery(String query) {
        try (Statement stmt = con.createStatement()) {
            con.setAutoCommit(false);
            int rs = stmt.executeUpdate(query);
            con.commit();
            LOGGER.log(Level.INFO, "Number of updated rows: " + rs);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error when procesing SQL query", e);
        } catch (NoClassDefFoundError e) {
            LOGGER.log(Level.WARNING, "NoClassDefFoundError", e);
        }
    }

    /**
     * Close the connection
     */
    public void closeConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error when closing connection!", e);
        }
    }
}