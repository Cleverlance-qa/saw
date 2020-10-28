package corehelpers.testbase;

import confighelpers.DriversConfig;
import corehelpers.DataReader;
import corehelpers.DataReaderImpl;
import corehelpers.constants.SAWConstants;
import corehelpers.datapools.ReportBase;
import corehelpers.datapools.ReportGrafana;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Inicialization and basic work with Desktop test instances
 */
public class TestBaseDesktop implements TestBase {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected DataReader dataReader;
    protected boolean success = false;
    private String browser;
    private String platform;
    private String testName = "";
    protected String testBodyName = "";
    private String xmlUrl = SAWConstants.TESTINPUTS + "properties.xml";

    /**
     * Init desktop webdriver for chosen browser.
     * @param browser - name of browser which should be initialized - firefox, chrome, ie11, edge, safari, local, localMac, localLinux
     * @param platform - name of platform where test should be initialized - local or some predefined station
     */
    public void initDriver(String browser, String platform) {
        if(!ManagementFactory.getRuntimeMXBean().getName().equals(System.getProperty("SAW_RUN_ID"))) {
            System.setProperty("SAW_RUN_ID", ManagementFactory.getRuntimeMXBean().getName());
            System.setProperty("testRunId", getProcessTimestamp());
        }
        if(System.getProperty("propertiesFile") != null) {
            setProperties(System.getProperty("propertiesFile"));
        }
        if(System.getProperty("grafanaSettingsFile") != null) {
            setGrafanaProperties(System.getProperty("grafanaSettingsFile"));
        }
        this.browser = browser;
        this.platform = platform;
        final DriversConfig driversConfig = new DriversConfig();
        driversConfig.setXmlUrl(xmlUrl);
        driver = driversConfig.initDesktop(browser,platform);
        dataReader = new DataReaderImpl(driver, platform, xmlUrl);
        wait = new WebDriverWait(driver, 60);
        driver = new Augmenter().augment(driver); //This serves for screenshot feature
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        testName = this.getClass().getSimpleName();
    }

    /**
     * Close current test instance, make final screenshot and write test report
     * @param success - true is test passed
     */
    public void quitDriver(boolean success) {
        takeScreenshot();
        driver.quit();
        new ReportBase().flushBasicReports(testName, null, success, browser, platform, dataReader.isExcelReporting(), dataReader);
    }

    /**
     * Close current test instance, make final screenshot and write test report
     * @param success - true is test passed
     * @param testBodyName - name of test body, will be used in grafana reports and screenshots
     */
    public void quitDriver(boolean success, String testBodyName) {
        takeScreenshot(testBodyName);
        driver.quit();
        new ReportBase().flushBasicReports(testName, testBodyName, success, browser, platform, dataReader.isExcelReporting(), dataReader);
    }

    /**
     * Take a final screenshot
     */
    public void takeScreenshot() {
        try{
            FileUtils.copyFile(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE), new File( "TestOutputs/Screenshots/" + testName + ".png"));
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
            FileUtils.copyFile(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE), new File( "TestOutputs/Screenshots/" + testName + "_" + testBodyName + ".png"));
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
        this.xmlUrl = SAWConstants.TESTINPUTS + propertiesPath;
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
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.UK);
        final Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
}