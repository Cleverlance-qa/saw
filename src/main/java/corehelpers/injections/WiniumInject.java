package corehelpers.injections;

import corehelpers.DataReader;
import corehelpers.DataReaderImpl;
import corehelpers.DriversSetUp;
import corehelpers.helpers.MethodHelperWinium;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.winium.WiniumDriver;

import static junit.framework.TestCase.fail;

/**
 * Winium Injection for non-winium tests
 */
public class WiniumInject extends MethodHelperWinium {
    private static final String UNSUPPORTED_BROWSER_LOCALE = "Unsuported browser locale used in loginDialogByWiniumInject";

    /**
     * Prepare Winium injection
     */
    public WiniumInject() {
        super(new <WiniumDriver>DataReaderImpl(new DriversSetUp().initWiniumInjection()));
    }

    /**
     * Prepare Winium injection with specific propertiesFile
     */
    public WiniumInject(String propertiesFile) {
        super(new <WiniumDriver>DataReaderImpl(new DriversSetUp().initWiniumInjection(propertiesFile)));
    }

    /**
     * Prepare Winium injection. Winium will be run on same machine as given WebDriver
     */
    public WiniumInject(WebDriver driver) {
        super(new <WiniumDriver>DataReaderImpl(new DriversSetUp().initWiniumInjection(driver)));
    }

    /**
     * Prepare Winium injection with specific propertiesFile. Winium will be run on same machine as given WebDriver
     */
    public WiniumInject(String propertiesFile, WebDriver driver) {
        super(new <WiniumDriver>DataReaderImpl(new DriversSetUp().initWiniumInjection(propertiesFile, driver)));
    }

    /**
     * Get current DataReader instance
     * @return - DataReader instance
     */
    public DataReader getDataReader() {
        return dataReader;
    }

    /**
     * Close and quit Winium instance
     */
    public void stopWiniumInjection() {
        getWiniumDriver().close();
        getWiniumDriver().quit();
    }

    /**
     * Chrome dialog popup window handle.
     * @param username - username
     * @param password - password
     * @param locale - browser locale
     */
    public void loginChromeCS(String username, String password, String locale) {
        WebElement window;
        switch (locale) {
            case "cs-CZ":
                window = getWiniumDriver().findElementByClassName("Chrome_WidgetWin_1");
                window.findElements(By.name("Uživatelské jméno")).get(1).sendKeys(username);
                window.findElements(By.name("Heslo")).get(1).sendKeys(password);
                window.findElements(By.name("Přihlaste se")).get(3).click();
                break;
            case "en-GB":
                window = getWiniumDriver().findElementByClassName("Chrome_WidgetWin_1");
                window.findElements(By.name("Username")).get(1).sendKeys(username);
                window.findElements(By.name("Password")).get(1).sendKeys(password);
                window.findElements(By.name("Sign in")).get(3).click();
                break;
            case "en-US":
                window = getWiniumDriver().findElementByClassName("Chrome_WidgetWin_1");
                window.findElements(By.name("Username")).get(1).sendKeys(username);
                window.findElements(By.name("Password")).get(1).sendKeys(password);
                window.findElements(By.name("Sign in")).get(3).click();
                break;
            default:
                fail(UNSUPPORTED_BROWSER_LOCALE);
        }
    }

    /**
     * Firefox dialog popup window handle.
     * @param username - username
     * @param password - password
     * @param locale - browser locale
     */
    public void loginFirefoxCS(String username, String password, String locale) {
        switch (locale) {
            case "cs":
                final WebElement window = getWiniumDriver().findElementByClassName("MozillaWindowClass");
                window.findElements(By.name("Uživatelské jméno:")).get(1).sendKeys(username);
                window.findElements(By.name("Heslo:")).get(1).sendKeys(password);
                window.findElement(By.name("OK")).click();
                break;
            default:
                fail(UNSUPPORTED_BROWSER_LOCALE);
        }
    }
}