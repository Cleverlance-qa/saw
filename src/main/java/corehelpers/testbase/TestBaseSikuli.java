package corehelpers.testbase;

import corehelpers.DataReader;
import corehelpers.ElementsFinder;
import corehelpers.datapools.ReportBase;
import corehelpers.datapools.ReportGrafana;
import org.apache.commons.io.FileUtils;
import org.sikuli.script.App;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.sikuli.basics.Settings.*;

/**
 * Inicialization and basic work with Sikuli test instances
 */
public class TestBaseSikuli implements TestBase {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected DataReader dataReader;
    protected boolean success = false;
    private String browser = null;
    private String platform = null;
    private String testName = "";
    protected String testBodyName = "";
    private String xmlUrl = ElementsFinder.TESTINPUTS + "properties.xml";

    /**
     * Inicialite Sikulix Screen instance and set it's values
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
        this.browser = "sikuli";
        this.platform = "SIKULI";
        setMinSimilarity(0.8);
        setTesseractOcr();
        ImagePath.setBundlePath("user.dir");
        final Screen screen = new Screen();
        screen.setAutoWaitTimeout(1);
        dataReader = new <Screen>DataReader(screen, xmlUrl);
        if ("false".equals(dataReader.getXMLProperties().getProperty("sikuliConnectToRunningApp"))
                && dataReader.getXMLProperties().getProperty("sikuliTestProgramExecutable") != null ) {
            dataReader.setSikuliApp(new App(dataReader.getXMLProperties().getProperty("sikuliTestProgramExecutable")));
            dataReader.getSikuliApp().open();
        }
        testName = this.getClass().getSimpleName();
    }

    private static void setMinSimilarity(double minSimilarity) {
        MinSimilarity = minSimilarity;
    }

    /**
     * Close current test instance, make final screenshot and write test report
     * @param success - true is test passed
     */
    @Override
    public void quitDriver(boolean success) {
        takeScreenshot();
        new ReportBase().flushBasicReports(testName, null, success, browser, platform, dataReader.isExcelReporting(), dataReader);
        if (dataReader.getSikuliApp() != null) {
            dataReader.getSikuliApp().close();
        }
    }

    /**
     * Close current test instance, make final screenshot and write test report
     * @param success - true is test passed
     * @param testBodyName - name of test body, will be used in grafana reports and screenshots
     */
    public void quitDriver(boolean success, String testBodyName) {
        takeScreenshot(testBodyName);
        new ReportBase().flushBasicReports(testName, testBodyName, success, browser, platform, dataReader.isExcelReporting(), dataReader);
        if (dataReader.getSikuliApp() != null) {
            dataReader.getSikuliApp().close();
        }
    }

    /**
     * Take a final screenshot
     */
    @Override
    public void takeScreenshot() {
        try {
            FileUtils.copyFile(new File(dataReader.getScreen().capture(dataReader.getScreen().getBounds()).getFile()), new File( "TestOutputs/Screenshots/" + testName + ".png"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when taking screenshot", e);
        }
    }

    /**
     * Take a final screenshot
     * @param testBodyName - name of test body
     */
    public void takeScreenshot(String testBodyName) {
        try{
            FileUtils.copyFile(new File(dataReader.getScreen().capture(dataReader.getScreen().getBounds()).getFile()), new File( "TestOutputs/Screenshots/" + testName + "_" + testBodyName + ".png"));
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

    private static void setTesseractOcr() {
        final Path tessdataPath = Paths.get(System.getProperty("user.dir") + "\\src\\main\\resources\\tessdata\\");
        if(tessdataPath.toFile().exists()) {
            OcrDataPath = System.getProperty("user.dir") + "\\src\\main\\resources\\";
            LOGGER.log(Level.INFO,"Tesseract datapath set to: " + OcrDataPath);
            OcrLanguage = "ces";
        }
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
