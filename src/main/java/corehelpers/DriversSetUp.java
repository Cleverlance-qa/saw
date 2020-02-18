package corehelpers;

import confighelpers.DriversConfig;
import corehelpers.remote.NodeHelper;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.winium.DesktopOptions;
import org.openqa.selenium.winium.WiniumDriver;
import org.sikuli.script.Screen;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static corehelpers.DataReader.TESTINPUTS;

/**
 * Base class for drivers settings.
 */
public class DriversSetUp{
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected DataReader dataReader = new DataReader();
    protected DesiredCapabilities caps;
    protected String platform;
    protected RemoteWebDriver driver;
    protected AppiumDriver appDriver;
    protected WiniumDriver winiumDriver;
    protected static final String GeckoDriverPathWin = "Drivers/geckodriver.exe";
    protected static final String GeckoDriverPathMac = "Drivers/geckodriver_mac";
    protected static final String GeckoDriverPathLinux = "Drivers/geckodriver_linux";
    protected static final String ChromeDriverPathWin = "Drivers/chromedriver.exe";
    protected static final String ChromeDriverPathMac = "Drivers/chromedriver_mac";
    protected static final String ChromeDriverPathLinux = "Drivers/chromedriver_linux";
    protected static final String IE_DRIVER_PATH = "Drivers/IEDriverServer.exe";
    protected String appiumURL = dataReader.getXMLProperties().getProperty("appiumURL");
    protected String gridURL = dataReader.getXMLProperties().getProperty("gridURL");
    protected String winiumTestProgramExecutable = dataReader.getXMLProperties().getProperty("winiumTestProgramExecutable");
    protected String winiumConnectToRunningApp = dataReader.getXMLProperties().getProperty("winiumConnectToRunningApp");
    protected String winiumURL = "http://localhost:9999";
    protected static final String WINDOWS_PLATFORM_NAME = "WINDOWS";
    protected static final String PLATFORM_LOCAL = "local";
    protected static final String PLATFORM_LOCALMAC = "localMac";
    protected static final String PLATFORM_LOCALLINUX = "localLinux";
    protected static final String WEBDRIVER_GECKO_DRIVER = "webdriver.gecko.driver";
    protected static final String WEBDRIVER_CHROME_DRIVER = "webdriver.chrome.driver";
    protected static final String WIN7 = "win7";
    protected static final String WIN8 = "win8";
    protected static final String WIN10 = "win10";
    protected static final String MAC = "mac";
    protected static final String LINUX = "linux";

    /**
     * Inicialized Winium driver. This driver will not be used as main driver, only for small helps.
     * @return - WiniumDriver instance
     */
    public WiniumDriver initWiniumInjection() {
        prepareWiniumInjection(winiumURL);
        return winiumDriver;
    }

    /**
     * Inicialized Winium driver on same machine as given WebDriver. This driver will not be used as main driver, only for small helps.
     * @param driver - webdriver instance
     * @return - WiniumDriver instance
     */
    public WiniumDriver initWiniumInjection(WebDriver driver) {
        prepareWiniumInjection("http://" + new NodeHelper().getIPOfNode((RemoteWebDriver) driver) + ":9999");
        return winiumDriver;
    }

    /**
     * Inicialized Winium driver with specific propertiesFile. This driver will not be used as main driver, only for small helps.
     * @param propertiesFile - path to properties.xml file
     * @return - WiniumDriver instance
     */
    public WiniumDriver initWiniumInjection(String propertiesFile) {
        setXmlUrl(TESTINPUTS + propertiesFile);
        prepareWiniumInjection(winiumURL);
        return winiumDriver;
    }

    /**
     * Inicialized Winium driver on same machine as given WebDriver with specific propertiesFile. This driver will not be used as main driver, only for small helps.
     * @param propertiesFile - path to properties.xml file
     * @return - WiniumDriver instance
     */
    public WiniumDriver initWiniumInjection(String propertiesFile, WebDriver driver) {
        setXmlUrl(TESTINPUTS + propertiesFile);
        prepareWiniumInjection("http://" + new NodeHelper().getIPOfNode((RemoteWebDriver) driver) + ":9999");
        return winiumDriver;
    }

    private void prepareWiniumInjection(String url) {
        final DesktopOptions options = new DesktopOptions();
        options.setApplicationPath(winiumTestProgramExecutable);
        options.setLaunchDelay(1);
        options.setDebugConnectToRunningApp(Boolean.valueOf(winiumConnectToRunningApp));
        try {
            winiumDriver = new WiniumDriver(new URL(url),options);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "Can't connect to Winium server",e);
        }
    }

    /**
     * Inicialized SikuliX. This instance will not be used as main driver, only for small helps.
     * @return - SikuliX instance
     */
    public Screen initSikuliInjection() {
        final Screen screen = new Screen();
        screen.setAutoWaitTimeout(1);
        return screen;
    }

    /**
     * Inicialized Webdriver. This instance will not be used as main driver, only for small helps.
     * @param browser - browser
     * @param platform - platform
     * @return - Webdriver instance
     */
    public WebDriver initWebdriverinjection(String browser, String platform) {
        return new DriversConfig().initDesktop(browser,platform);
    }

    protected Platform getPlatform(String platform) {
        switch (platform) {
            case WIN7:
                return Platform.VISTA;
            case WIN8:
                return Platform.WIN8_1;
            case WIN10:
                return Platform.WIN10;
            case MAC:
                return Platform.MAC;
            case LINUX:
                return Platform.LINUX;
            default:
                return Platform.ANY;
        }
    }

    /**
     * Set which properties xml file shoud be load
     * @param xmlUrl - path to xml file
     */
    public void setXmlUrl(String xmlUrl) {
        dataReader.setXmlUrl(xmlUrl);
        this.gridURL = dataReader.getXMLProperties().getProperty("gridURL");
        this.appiumURL = dataReader.getXMLProperties().getProperty("appiumURL");
        this.winiumTestProgramExecutable = dataReader.getXMLProperties().getProperty("winiumTestProgramExecutable");
        this.winiumConnectToRunningApp = dataReader.getXMLProperties().getProperty("winiumConnectToRunningApp");
        if(dataReader.getXMLProperties().getProperty("winiumURL") != null) {
            this.winiumURL = dataReader.getXMLProperties().getProperty("winiumURL");
        }
    }
}