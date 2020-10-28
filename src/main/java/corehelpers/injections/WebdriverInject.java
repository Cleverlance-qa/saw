package corehelpers.injections;

import corehelpers.DataReaderImpl;
import corehelpers.DriversSetUp;
import corehelpers.helpers.MethodHelperDesktop;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

/**
 * Webdriver Injection for non-Webdriver tests or for second instance
 */
public class WebdriverInject extends MethodHelperDesktop {

    /**
     * Prepare Webdriver injection. Will start new driver session.
     * @param browser - given browser
     * @param platform - given platform
     */
    public WebdriverInject(String browser, String platform) {
        super(new <WebDriver>DataReaderImpl(new DriversSetUp().initWebdriverinjection(browser,platform), platform));
        setDriver();
    }

    /**
     * Stop current Webdriver instance
     */
    public void stopWebdriverInject() {
        getDesktopDriver().quit();
    }

    private void setDriver() {
        getDesktopDriver().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        getDesktopDriver().manage().window().maximize();
    }
}
