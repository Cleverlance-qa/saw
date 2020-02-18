package corehelpers.testbase;

import confighelpers.DriversConfig;
import corehelpers.DataReader;
import corehelpers.ElementsFinder;
import corehelpers.datapools.ReportBase;
import corehelpers.datapools.ReportGrafana;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.winium.WiniumDriver;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Inicialization and basic work with Winium test instances
 */
public class TestBaseWinium implements TestBase {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected WiniumDriver winiumDriver;
    protected WebDriverWait wait;
    protected DataReader dataReader;
    protected boolean success = false;
    private String browser = null;
    private String platform = null;
    private String testName = "";
    protected String testBodyName = "";
    private String xmlUrl = ElementsFinder.TESTINPUTS + "properties.xml";

    /**
     * Inicialite Winium driver and set it's values
     */
    public void initDriver() {
        if(!ManagementFactory.getRuntimeMXBean().getName().equals(System.getProperty("SELEF_RUN_ID"))) {
            System.setProperty("SELEF_RUN_ID", ManagementFactory.getRuntimeMXBean().getName());
            System.setProperty("SELEF_TIMESTAMP", getProcessTimestamp());
        }
        if(System.getProperty("propertiesFile") != null) {
            setProperties(System.getProperty("propertiesFile"));
        }
        if(System.getProperty("grafanaSettingsFile") != null) {
            setGrafanaProperties(System.getProperty("grafanaSettingsFile"));
        }
        this.browser = "winium";
        this.platform = "WINIUM";
        final DriversConfig driversConfig = new DriversConfig();
        driversConfig.setXmlUrl(xmlUrl);
        winiumDriver = driversConfig.initWiniumDriver();
        dataReader = new <WiniumDriver>DataReader(winiumDriver, xmlUrl);
        wait = new WebDriverWait(winiumDriver, 1);
        testName = this.getClass().getSimpleName();
    }

    /**
     * Close current test instance, make final screenshot and write test report
     * @param success - true is test passed
     */
    @Override
    public void quitDriver(boolean success) {
        takeScreenshot();
        winiumDriver.close();
        winiumDriver.quit();
        new ReportBase().flushBasicReports(testName, null, success, browser, platform, dataReader.isExcelReporting(), dataReader);
    }

    /**
     * Close current test instance, make final screenshot and write test report
     * @param success - true is test passed
     * @param testBodyName - name of test body, will be used in grafana reports and screenshots
     */
    public void quitDriver(boolean success, String testBodyName) {
        takeScreenshot(testBodyName);
        winiumDriver.close();
        winiumDriver.quit();
        new ReportBase().flushBasicReports(testName, testBodyName, success, browser, platform, dataReader.isExcelReporting(), dataReader);
    }

    /**
     * Take a final screenshot
     */
    @Override
    public void takeScreenshot() {
        try {
            FileUtils.copyFile(winiumDriver.getScreenshotAs(OutputType.FILE), new File( "TestOutputs/Screenshots/" + testName + ".png"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when taking screenshot",e);
        }
    }

    /**
     * Take a final screenshot
     * @param testBodyName - name of test body
     */
    public void takeScreenshot(String testBodyName) {
        try{
            FileUtils.copyFile(winiumDriver.getScreenshotAs(OutputType.FILE), new File( "TestOutputs/Screenshots/" + testName + "_" + testBodyName + ".png"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when taking screenshot", e);
        }
    }

    /**
     * Set which properties xml file should be load
     * @param propertiesPath - path to properties file
     */
    @Override
    public void setProperties(String propertiesPath) {
        this.xmlUrl = ElementsFinder.TESTINPUTS + propertiesPath;
    }

    /**
     * Set which grafana properties xml file should be load
     * @param propertiesPath - path to grafana properties file
     */
    @Override
    public void setGrafanaProperties(String propertiesPath) {
        ReportGrafana.setGrafanaXml(propertiesPath);
    }

    /**
     * Return last child of package name
     * @param c - testBody class
     * @return name
     */
    @Override
    public String getSimplePackageName(Class c){
        String packages[];
        packages = c.getPackage().getName().split("[.]");
        return packages[packages.length-1];
    }

    private String getProcessTimestamp(){
        final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.UK);
        final Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
}