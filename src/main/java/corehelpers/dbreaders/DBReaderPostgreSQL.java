package corehelpers.dbreaders;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for connection to POSTGRE SQL database
 */
public class DBReaderPostgreSQL extends DBReader {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Prepare connection to Postgre database.
     * @param server - database server URL
     * @param port - database port
     * @param databaseName - database name
     * @param user - user
     * @param password - password
     */
    public DBReaderPostgreSQL(String server, int port, String databaseName, String user, String password) {
        super();
        super.server = server;
        super.port = port;
        super.databaseName = databaseName;
        super.user = user;
        super.password = password;
        super.con = getConnetion();
    }

    private Connection getConnetion() {
        Connection con = null;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE,"Class for db not found!", e);
        }
        try {
            con = DriverManager.getConnection("jdbc:postgresql://" + server + ":" + port + "/" + databaseName, user, password);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Error when procesing SQL query", e);
        }
        return con;
    }
}