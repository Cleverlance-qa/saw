package corehelpers.dbreaders;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for connection to MS SQL database
 */
public class DBReaderMSSQL extends DBReader{
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Prepare connection to MSSQL database.
     * @param server - database server URL
     * @param port - database port
     * @param instanceName - database instance
     * @param databaseName - database name
     * @param user - user
     * @param password - password
     */
    public DBReaderMSSQL(String server, int port, String instanceName, String databaseName, String user, String password) {
        super();
        super.server = server;
        super.port = port;
        super.databaseName = databaseName;
        super.user = user;
        super.password = password;
        super.instanceName = instanceName;
        super.con = getConnetion();
    }

    private Connection getConnetion() {
        Connection con = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE,"Class for db not found!", e);
        }
        final String connectionUrl = "jdbc:sqlserver://" + server + ":" + port + ";instanceName=" + instanceName +
                ";databaseName=" + databaseName + ";user=" + user + ";password=" + password + ";";
        try {
            con = DriverManager.getConnection(connectionUrl);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Error when procesing SQL query", e);
        }
        return con;
    }
}