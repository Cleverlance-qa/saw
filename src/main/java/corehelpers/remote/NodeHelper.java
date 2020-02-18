package corehelpers.remote;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for RemoteWebdriver node
 */
public class NodeHelper {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Get an ip address from current RemoteWebdriver instance
     * @param remoteDriver - current RemoteWebdriver instance
     * @return - ip address
     */
    public String getIPOfNode(RemoteWebDriver remoteDriver) {
        String hostFound = null;
        final HttpCommandExecutor ce = (HttpCommandExecutor) remoteDriver.getCommandExecutor();
        final String hostName = ce.getAddressOfRemoteServer().getHost();
        final int port = ce.getAddressOfRemoteServer().getPort();
        final HttpHost host = new HttpHost(hostName, port);
        final HttpClient client = HttpClients.createDefault();
        URL sessionURL = null;
        try {
            sessionURL = new URL("http://" + hostName + ":" + port + "/grid/api/testsession?session=" + remoteDriver.getSessionId());
            final BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest( "POST", sessionURL.toExternalForm());
            final HttpResponse response = client.execute(host, r);
            final JSONObject object = extractObject(response);
            final URL myURL = new URL(object.getString("proxyId"));
            if ((myURL.getHost() != null) && (myURL.getPort() != -1)) {
                hostFound = myURL.getHost();
            }
        } catch (IOException|JSONException e) {
            LOGGER.log(Level.WARNING, "Error when obtaining ip address from RemoteWebDriver node, will set it to localhost");
            hostFound = "localhost";
        }
        return hostFound;
    }

    private static JSONObject extractObject(HttpResponse resp) throws IOException, JSONException {
        final InputStream contents = resp.getEntity().getContent();
        final StringWriter writer = new StringWriter();
        IOUtils.copy(contents, writer, "UTF8");
        return new JSONObject(writer.toString());
    }
}