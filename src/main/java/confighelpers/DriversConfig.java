package confighelpers;

import corehelpers.DriversSetUp;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.winium.DesktopOptions;
import org.openqa.selenium.winium.WiniumDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.openqa.selenium.remote.CapabilityType.ACCEPT_SSL_CERTS;

/**
 * Settings of drivers configurazions
 */
public class DriversConfig extends DriversSetUp {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String ERROR_GRID_SERVER = "Can't connect to the grid server";
    private static final String ERROR_APPIUM_SERVER = "Can't connect to the appium server";
    private static final String ERROR_WINIUM_SERVER = "Can't connect to the winium server";
    private static final String WIN = "win";
    private static final String FIREFOX = "firefox";
    private static final String CHROME = "chrome";
    private static final String SAFARI = "safari";
    private static final String IE11 = "ie11";
    private static final String EDGE = "edge";

    /**
     * Inicialization of Desktop webdriver
     * @param browser Browser to iniciate
     * @param platform Platform where to iniciate driver
     * @return Inicialized driver
     */
    public RemoteWebDriver initDesktop(String browser, String platform) {
        this.platform = platform;
        switch (browser) {
            case FIREFOX:
                initFirefoxDesktop();
                break;
            case CHROME:
                initChromeDesktop();
                break;
            case SAFARI:
                initSafariDesktop();
                break;
            case IE11:
                initIE11Desktop();
                break;
            case EDGE:
                initEdgeDesktop();
                break;
            default:
                LOGGER.log(Level.SEVERE,"Wrong browser type!");
        }
        return driver;
    }

    private RemoteWebDriver initFirefoxDesktop() {
        caps = DesiredCapabilities.firefox();
        if (platform.contains(WIN)) {
            caps.setCapability(CapabilityType.PLATFORM_NAME, WINDOWS_PLATFORM_NAME);
        }
        caps.setPlatform(getPlatform(platform));
        try {
            switch (platform) {
                case PLATFORM_LOCAL:
                    System.setProperty(WEBDRIVER_GECKO_DRIVER, GeckoDriverPathWin);
                    driver = new FirefoxDriver();
                    break;
                case PLATFORM_LOCALMAC:
                    System.setProperty(WEBDRIVER_GECKO_DRIVER, GeckoDriverPathMac);
                    driver = new FirefoxDriver();
                    break;
                case PLATFORM_LOCALLINUX:
                    System.setProperty(WEBDRIVER_GECKO_DRIVER, GeckoDriverPathLinux);
                    driver = new FirefoxDriver();
                    break;
                default:
                    driver = new RemoteWebDriver(new URL(gridURL), caps);
                    driver.setFileDetector(new LocalFileDetector());
                    break;
            }
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE,ERROR_GRID_SERVER, e);
        }
        return driver;
    }

    private RemoteWebDriver initChromeDesktop() {
        final ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        caps = DesiredCapabilities.chrome();
        caps.setCapability(ChromeOptions.CAPABILITY, options);
        if (platform.contains(WIN)) {
            caps.setCapability(CapabilityType.PLATFORM_NAME, WINDOWS_PLATFORM_NAME);
        }
        caps.setPlatform(getPlatform(platform));
        try {
            switch (platform) {
                case PLATFORM_LOCAL:
                    System.setProperty(WEBDRIVER_CHROME_DRIVER, ChromeDriverPathWin);
                    driver = new ChromeDriver();
                    break;
                case PLATFORM_LOCALMAC:
                    System.setProperty(WEBDRIVER_CHROME_DRIVER, ChromeDriverPathMac);
                    driver = new ChromeDriver();
                    break;
                case PLATFORM_LOCALLINUX:
                    System.setProperty(WEBDRIVER_CHROME_DRIVER, ChromeDriverPathLinux);
                    driver = new ChromeDriver();
                    break;
                default:
                    driver = new RemoteWebDriver(new URL(gridURL), caps);
                    driver.setFileDetector(new LocalFileDetector());
                    break;
            }
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE,ERROR_GRID_SERVER, e);
        }
        return driver;
    }

    private RemoteWebDriver initSafariDesktop() {
        caps = DesiredCapabilities.safari();
        caps.setPlatform(getPlatform(platform));
        caps.setCapability(ACCEPT_SSL_CERTS, true);
        final SafariOptions safariOptions = new SafariOptions();

        try {
            switch (platform) {
                case PLATFORM_LOCAL:
                    LOGGER.log(Level.SEVERE,"Safari is only supported for MAC");
                    break;
                case PLATFORM_LOCALMAC:
                    safariOptions.setCapability(ACCEPT_SSL_CERTS, true);
                    safariOptions.setUseTechnologyPreview(false);
                    driver = new SafariDriver(safariOptions);
                    break;
                default:
                    driver = new RemoteWebDriver(new URL(gridURL), caps);
                    driver.setFileDetector(new LocalFileDetector());
                    break;
            }
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE,ERROR_GRID_SERVER, e);
        }
        return driver;
    }

    private RemoteWebDriver initIE11Desktop() {
        caps = DesiredCapabilities.internetExplorer();
        caps.setCapability(CapabilityType.PLATFORM_NAME, WINDOWS_PLATFORM_NAME);
        caps.setPlatform(getPlatform(platform));
        caps.setCapability(ACCEPT_SSL_CERTS, true);
        final InternetExplorerOptions options = new InternetExplorerOptions(caps);
        System.setProperty("webdriver.ie.driver", IE_DRIVER_PATH);
        try {
            switch (platform) {
                case "local":
                    driver = new InternetExplorerDriver(options);
                    break;
                default:
                    driver = new RemoteWebDriver(new URL(gridURL), caps);
                    driver.setFileDetector(new LocalFileDetector());
                    break;
            }
        } catch (MalformedURLException e) {
            throw new WebDriverException();
        }
        return driver;
    }

    private RemoteWebDriver initEdgeDesktop() {
        caps = DesiredCapabilities.edge();
        caps.setPlatform(getPlatform(platform));
        caps.setBrowserName("MicrosoftEdge");
        caps.setCapability(ACCEPT_SSL_CERTS, true);
        try {
            driver = new RemoteWebDriver(new URL(gridURL), caps);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE,ERROR_GRID_SERVER, e);
        }
        driver.setFileDetector(new LocalFileDetector());
        return driver;
    }

    /**
     * Set and init Appium driver for given device
     * @param device - device for inicialization
     * @return - inicialized Appium driver
     */
    public AppiumDriver initAppium(String device) {
        caps = new DesiredCapabilities();
        final ChromeOptions options = new ChromeOptions();
        switch (device) {
            case "MySkodaBS":
                final String USERNAME = "";
                final String AUTOMATE_KEY = "";
                appiumURL = "https://hub-cloud.browserstack.com/wd/hub";

                caps.setCapability("platformName", "android");
                caps.setCapability("platformVersion", "9.0");
                caps.setCapability("deviceName", "Google Pixel 3");
                caps.setCapability("app", "bs://550d3d041abbf7cbb80370c13b60e97715bc8cc4");
                caps.setCapability("appPackage","cz.skodaauto.connect");
                caps.setCapability("browserstack.user", USERNAME);
                caps.setCapability("browserstack.key", AUTOMATE_KEY);
                options.merge(caps);
                break;

            case "S20_MySkoda":
                caps.setCapability(MobileCapabilityType.PLATFORM_NAME, "ANDROID");
                caps.setCapability(MobileCapabilityType.DEVICE_NAME, "ZY224GM9FK");
                caps.setCapability(MobileCapabilityType.VERSION, "11");
                caps.setCapability(MobileCapabilityType.AUTOMATION_NAME,"uiautomator2");
                caps.setCapability("appPackage","cz.skodaauto.connect");
                caps.setCapability("appActivity", "cz.eman.oneconnect.wrapper.system.MainActivity");
                caps.setCapability("locale", "CZ");
                caps.setCapability("language", "cs");
                caps.setCapability("noReset", true);
                options.addArguments("no-first-run");
                options.addArguments("ignore-certificate-errors");
                options.addArguments("disable-translate");
                caps.setPlatform(Platform.ANDROID);
                options.merge(caps);
                break;

            default:
                LOGGER.log(Level.SEVERE,"Used device is not defined in DriversConfig!");
        }
        try {
            appDriver = new AndroidDriver(new URL(appiumURL), caps);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE,ERROR_APPIUM_SERVER, e);
        }

        // appDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        return appDriver;
    }

    /**
     * Set and init Winium driver
     * @return - inicialized Winium driver
     */
    //Inicialization of Winium
    public WiniumDriver initWiniumDriver() {
        final DesktopOptions options = new DesktopOptions();
        options.setApplicationPath(winiumTestProgramExecutable);
        options.setLaunchDelay(10);
        options.setDebugConnectToRunningApp(Boolean.valueOf(winiumConnectToRunningApp));
        try {
            winiumDriver = new WiniumDriver(new URL(winiumURL),options);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE,ERROR_WINIUM_SERVER, e);
        }
        return winiumDriver;
    }
}