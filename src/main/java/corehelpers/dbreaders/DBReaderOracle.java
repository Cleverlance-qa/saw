package corehelpers.dbreaders;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for connection to ORACLE database
 */
public class DBReaderOracle extends DBReader {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Prepare connection to Oracle database.
     * @param server - database server URL
     * @param port - database port
     * @param sid - database sid
     * @param user - user
     * @param password - password
     */
    public DBReaderOracle(String server, int port, String sid, String user, String password) {
        super();
        super.server = server;
        super.port = port;
        super.user = user;
        super.password = password;
        super.sid = sid;
        super.con = getConnetion();
    }

    private Connection getConnetion() {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE,"Class for db not found!", e);
        }
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@" + server + ":" + port + ":" + sid,user,password);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Error when procesing SQL query", e);
        }
        return con;
    }
}