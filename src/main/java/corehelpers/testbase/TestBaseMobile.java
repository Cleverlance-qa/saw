package corehelpers.testbase;

import confighelpers.DriversConfig;
import corehelpers.DataReader;
import corehelpers.ElementsFinder;
import corehelpers.datapools.ReportBase;
import corehelpers.datapools.ReportGrafana;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;

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
 * Inicialization and basic work with Mobile test instances
 */
public class TestBaseMobile implements TestBase {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected AppiumDriver appDriver;
    protected WebDriverWait wait;
    protected DataReader dataReader;
    protected boolean success = false;
    private String browser = null;
    private String platform = null;
    private String testName = "";
    protected String testBodyName = "";
    private String xmlUrl = ElementsFinder.TESTINPUTS + "properties.xml";

    /**
     * Init mobile webdriver (appiumdriver) for chosen device.
     * @param device - name of mobile device which should be initialized. Devices are configured in DriversSetUp class
     */
    public void initDriver(String device) {
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
        this.browser = "mobile";
        this.platform = device;
        final DriversConfig driversConfig = new DriversConfig();
        driversConfig.setXmlUrl(xmlUrl);
        appDriver = driversConfig.initAppium(device);
        dataReader = new DataReader(appDriver, device, xmlUrl);
        wait = new WebDriverWait(appDriver, 60);
        testName = this.getClass().getSimpleName();
    }

    /**
     * Close current test instance, make final screenshot and write test report
     * @param success - true is test passed
     */
    public void quitDriver(boolean success) {
        takeScreenshot();
        appDriver.quit();
        new ReportBase().flushBasicReports(testName, null, success, browser, platform, dataReader.isExcelReporting(), dataReader);
    }

    /**
     * Close current test instance, make final screenshot and write test report
     * @param success - true is test passed
     * @param testBodyName - name of test body, will be used in grafana reports and screenshots
     */
    public void quitDriver(boolean success, String testBodyName) {
        takeScreenshot(testBodyName);
        appDriver.quit();
        new ReportBase().flushBasicReports(testName, testBodyName, success, browser, platform, dataReader.isExcelReporting(), dataReader);
    }

    /**
     * Take a final screenshot
     */
    public void takeScreenshot() {
        try {
            FileUtils.copyFile(appDriver.getScreenshotAs(OutputType.FILE), new File( "TestOutputs/Screenshots/" + testName + ".png"));
        } catch (IOException | WebDriverException e) {
            LOGGER.log(Level.SEVERE,"Error when taking screenshot", e);
        }
    }

    /**
     * Take a final screenshot
     * @param testBodyName - name of test body
     */
    public void takeScreenshot(String testBodyName) {
        try{
            FileUtils.copyFile(appDriver.getScreenshotAs(OutputType.FILE), new File( "TestOutputs/Screenshots/" + testName + "_" + testBodyName + ".png"));
        } catch (IOException | WebDriverException e) {
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
