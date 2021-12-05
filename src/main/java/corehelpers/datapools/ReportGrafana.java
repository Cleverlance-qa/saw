package corehelpers.datapools;

import corehelpers.DataReader;
import corehelpers.constants.SAWConstants;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.SSLHandshakeException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for test reporting to grafana
 */
public class ReportGrafana {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static String grafanaXml = SAWConstants.TESTINPUTS + "grafana_settings.xml";
    private Properties properties;
    private final String testName;
    private final String testResult;
    private final String testBrowser;
    private final String testOS;
    private final boolean enableGrafanaReport;
    private final String grafanaURL;
    private final DataReader dataReader;

    /**
     * Init and load class for Grafana integration.
     * @param testName - test name
     * @param testResult - test result
     * @param testBrowser - tested browser
     * @param testOS - OS where test run
     * @param dataReader - DataReader instance
     */
    public ReportGrafana(String testName, boolean testResult, String testBrowser, String testOS, DataReader dataReader){
        this.testName = testName;
        this.testResult = getStatus(testResult);
        this.testBrowser = testBrowser;
        this.testOS = testOS;
        this.dataReader = dataReader;
        loadProperties();
        grafanaURL = properties.getProperty("telegrafURL");
        enableGrafanaReport = Boolean.valueOf(properties.getProperty("enableGrafanaReport"));
    }

    /**
     * Set the grafana properties file
     * @param grafanaXml - path to grafana properties file
     */
    public static void setGrafanaXml(String grafanaXml) {
        ReportGrafana.grafanaXml = SAWConstants.TESTINPUTS + grafanaXml;
    }

    /**
     * Send test results and information to Telegraf for propagation to Influx and Grafana.
     */
    public void postToTelegraf() {
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        if(enableGrafanaReport) {
            try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
                final HttpPost httppost = new HttpPost(grafanaURL);
                final StringEntity stringEntity = new StringEntity(makeTelegrafString(), "utf-8");
                httppost.setEntity(stringEntity);
                httpclient.execute(httppost);
            } catch (UnsupportedEncodingException e) {
                LOGGER.log(Level.SEVERE, "Error when sending request to Telegraf. UnsupportedEncodingException.", e);
            } catch (ClientProtocolException e) {
                LOGGER.log(Level.SEVERE, "Error when sending request to Telegraf. ClientProtocolException.", e);
            } catch (SSLHandshakeException e) {
                LOGGER.log(Level.SEVERE, "Error with grafana report. There is possible problem with certificate. Can't write results!", e);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error when sending request to Telegraf. IOException.", e);
            }
            LOGGER.log(Level.INFO, "Report was sent to Grafana with ID: " + System.getProperty("testRunId"));
        } else {
            LOGGER.log(Level.INFO, "REPORT TO GRAFANA IS DISABLED");
        }
    }

    /**
     * Send test results and information to Telegraf for propagation to Influx and Grafana on demand.
     */
    public void postToTelegrafManually() {
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpPost httppost = new HttpPost(grafanaURL);
            final StringEntity stringEntity = new StringEntity(makeTelegrafString(), "utf-8");
            httppost.setEntity(stringEntity);
            httpclient.execute(httppost);
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "Error when sending request to Telegraf. UnsupportedEncodingException.", e);
        } catch (ClientProtocolException e) {
            LOGGER.log(Level.WARNING, "Error when sending request to Telegraf. ClientProtocolException.", e);
        } catch (SSLHandshakeException e) {
            LOGGER.log(Level.WARNING, "Error with grafana report. There is possible problem with certificate. Can't write results!", e);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error when sending request to Telegraf. IOException.", e);
        }
        LOGGER.log(Level.INFO, "Report was sent to Grafana with ID: " + System.getProperty("SELEF_TIMESTAMP"));
    }

    private String makeTelegrafString() {
        String customProperties = "";
        String customTagProperties = "";
        final List<String> propertiesNames = new ArrayList<>();
        propertiesNames.addAll(properties.stringPropertyNames());
        propertiesNames.remove("enableGrafanaReport");
        propertiesNames.remove("telegrafURL");
        propertiesNames.remove("measurement");
        propertiesNames.remove("app");
        propertiesNames.remove("environment");

        for(final String property: propertiesNames) {
            if(!"-1".equals(checkCustomProperties(property)) && !"-1".equals(checkCustomTagProperties(property))) {
                customProperties = customProperties + "," + property + "=\"" + checkCustomProperties(property) + "\"";
                customTagProperties = customTagProperties + "," + property + "=" + checkCustomTagProperties(property) + "";
            }
            else if(!"-1".equals(checkCustomProperties(property))) {
                customProperties = customProperties + "," + property + "=\"" + checkCustomProperties(property) + "\"";
            }
            else if(!"-1".equals(checkCustomTagProperties(property))) {
                customTagProperties = customTagProperties + "," + property + "=" + checkCustomTagProperties(property) + "";
            } else {
                customProperties = customProperties + "," + property + "=\"" + properties.getProperty(property) + "\"";
            }
        }

        final String grafanaString = properties.getProperty("measurement") + "," +
                "app=" + properties.getProperty("app") + "," +
                "environment=" + properties.getProperty("environment") + "," +
                "test_name=" + testName + "," +
                "OS=" + testOS + "," +
                "browser=" + testBrowser +
                customTagProperties +
                " " +
                "result=\"" + testResult +"\"" + "," +
                "testRunId=\"" + System.getProperty("testRunId") +"\"" +
                customProperties;
        LOGGER.log(Level.INFO, "Grafana string: " + grafanaString);
        return grafanaString;
    }

    private void loadProperties() {
        properties = new Properties();
        try(InputStream input = new FileInputStream(grafanaXml)) {
            properties.loadFromXML(input);
        } catch (InvalidPropertiesFormatException e) {
            LOGGER.log(Level.SEVERE, "Error with data reading", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error with data reading. grafana_settings.xml is maybe missing.", e);
        }
    }

    private static String getStatus(boolean testResult) {
        if(testResult) {
            return "1";
        } else {
            return "-1";
        }
    }

    private String checkCustomProperties(String propertyName) {
        dataReader.getGrafanaCustomFieldMap();
        final Iterator it = dataReader.getGrafanaCustomFieldMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            if(me.getKey().equals(propertyName)) {
                return me.getValue().toString();
            }
        }
        return "-1";
    }

    private String checkCustomTagProperties(String propertyName) {
        dataReader.getGrafanaCustomTagMap();
        final Iterator it = dataReader.getGrafanaCustomTagMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            if(me.getKey().equals(propertyName)) {
                return me.getValue().toString();
            }
        }
        return "-1";
    }

}