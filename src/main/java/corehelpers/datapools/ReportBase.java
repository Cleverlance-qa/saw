package corehelpers.datapools;

import corehelpers.DataReader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for report handling
 */
public class ReportBase {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Flush test reports to Excel and Grafana
     * @param testName - test name
     * @param testBodyName - test body name
     * @param success - test status
     * @param browser - tested browser
     * @param platform - tested platform
     * @param excelReporting - true is excel reporting enabled
     * @param dataReader - DataReader instance
     */
    public void flushBasicReports(String testName, String testBodyName, boolean success, String browser, String platform, boolean excelReporting, DataReader dataReader) {
        if(testBodyName==null) {
            new ReportGrafana(testName, success, browser, platform, dataReader).postToTelegraf();
        } else {
            new ReportGrafana(testBodyName, success, browser, platform, dataReader).postToTelegraf();
        }
        if(excelReporting) {
            try {
                new ReportExcel().writeTest(testName, success);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error with excel report. Can't write results!", e);
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARNING, "Nenalezen radek pro zapis v reportu!", e);
            }
        }
    }
}