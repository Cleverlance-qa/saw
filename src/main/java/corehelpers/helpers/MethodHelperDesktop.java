package corehelpers.helpers;

import corehelpers.DataReader;
import corehelpers.injections.WiniumInject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Finder;
import org.sikuli.script.Match;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Helpers/keywords for Desktop tests
 */
public class MethodHelperDesktop extends MethodHelper {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String FILE_INPUT = "//input[@type='file']";
    private BufferedImage screen = null;

    /**
     * Inicialized class for given DataReader instance.
     * @param dataReader - DataReader instance
     */
    public MethodHelperDesktop(DataReader dataReader) {
        super(dataReader);
    }

    /**
     * Set Wait instance
     */
    @Override
    public void setWait() {
        wait = new WebDriverWait(dataReader.getDriver(), 60);
    }

    /**
     * Set tested platform
     */
    @Override
    public void setPlatform() {
        platform = dataReader.getPlatform();
    }

    /**
     * Take a screenshot and save it to the Screenshots folder in format "testRunnerName_nameFromParam"
     * @param name - name for screenshot
     */
    @Override
    public void takeScreenshot(String name) {
        try {
            FileUtils.copyFile(((TakesScreenshot) getDesktopDriver()).getScreenshotAs(OutputType.FILE), new File( SCREENSHOTS_FOLDER + super.getClass().getSimpleName() + UNDERSCORE + name + SUFFIX_PNG));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when taking screenshot", e);
        }
    }

    /**
     * Open url in browser
     * @param url - url to open
     */
    public void openURL(String url) {
        dataReader.getDriver().get(url);
    }

    /**
     * Simulate hovering mouse over given element
     * @param element - given element
     */
    public MethodHelperDesktop hoverMouseOverElement(String element){
        logMessageToConsole("Hovering mouse over " + element);
        final Actions action = new Actions(getDesktopDriver());
        action.moveToElement(dataReader.getElement(element)).build().perform();
        return this;
    }

    /**
     * Login popup dialog window hangle. Fill given username and password with Winium help.
     * @param url - url with popup login dialog
     * @param username - username to fill
     * @param password - password to fill
     */
    public void loginDialogByWiniumInject(String url, String username, String password) {
        runJavascript("document.body.setAttribute(\"webdriverLocale\", window.navigator.language)");
        final String locale = getDesktopDriver().findElement(By.tagName("body")).getAttribute("webdriverLocale");
        final Capabilities cap = ((RemoteWebDriver) getDesktopDriver()).getCapabilities();
        final String browserName = cap.getBrowserName();
        logMessageToConsole("Will try to log by the dialog in " + browserName + " with locale: " + locale);
        WiniumInject winiumInject;
        switch (browserName) {
            case "chrome":
                logMessageToConsole("Opening " + url);
                runJavascript("window.open(\"" + url + "\");");
                logMessageToConsole("Making special chrome tab maneuver..");
                Set<String> tabHandles = getDesktopDriver().getWindowHandles();
                final int tab_count = tabHandles.size();
                getDesktopDriver().switchTo().window(tabHandles.toArray()[tab_count-2].toString());
                dialogPause();
                getDesktopDriver().close();
                dialogPause();
                tabHandles = getDesktopDriver().getWindowHandles();
                getDesktopDriver().switchTo().window(tabHandles.toArray()[tab_count-2].toString());

                logMessageToConsole("Starting winium injection");
                winiumInject = new WiniumInject(getDesktopDriver());
                logMessageToConsole("Trying to log in using username and password");
                winiumInject.loginChromeCS(username, password, locale);
                logMessageToConsole("Stopping winium injection");
                winiumInject.stopWiniumInjection();
                dialogPause();
                break;
            case "firefox":
                logMessageToConsole("Opening " + url);
                runJavascript("window.open(\"" + url + "\",\"_self\")");
                logMessageToConsole("Starting winium injection");
                winiumInject = new WiniumInject(getDesktopDriver());
                logMessageToConsole("Trying to log in using username and password");
                winiumInject.loginFirefoxCS(username, password, locale);
                logMessageToConsole("Stopping winium injection");
                winiumInject.stopWiniumInjection();
                dialogPause();
                break;
        }
    }

    /**
     * File upload
     * @param filePath - path to file
     */
    public void uploadFile(String filePath) {
        runJavascript("document.getElementsByTagName('input')[0].style.visibility = \"visible\"");
        logMessageToConsole("Trying to put file to fileIputField");
        getDesktopDriver().findElement(By.xpath(FILE_INPUT)).sendKeys(filePath);
    }

    /**
     * File upload for specific input field
     * @param filePath - XPath to input field
     * @param fileInputXPath - path to file
     */
    public void uploadFile(String filePath, String fileInputXPath) {
        runJavascript("document.getElementsByTagName('input')[0].style.visibility = \"visible\"");
        logMessageToConsole("Trying to put file to fileIputField");
        getDesktopDriver().findElement(By.xpath(fileInputXPath)).sendKeys(filePath);
    }

    /**
     * Switch to given iframe
     * @param name - name of iframe
     */
    public void switchToIFrame(String name) {
        getDesktopDriver().switchTo().frame(name);
    }

    /**
     * Switch to given iframe
     * @param i - iframe index
     */
    public void switchToIFrame(int i) {
        getDesktopDriver().switchTo().frame(i);
    }

    /**
     * Switch to default iframe
     */
    public void switchToDefaultIFrame() {
        getDesktopDriver().switchTo().defaultContent();
    }

    /**
     * Will wait for given iFrame
     * @param frameName - iFrame Name
     */
    public void waitForIFrame(String frameName) {
        tryIFrame(frameName, 30);
    }

    /**
     * Will wait for given iFrame
     * @param frameName - iFrame Name
     * @param secondsToWait - time to wait in seconds
     */
    public void waitForIFrame(String frameName, int secondsToWait) {
        tryIFrame(frameName, secondsToWait);
    }

    private void tryIFrame(String frameName, int seconds){
        for(int i = 0;i<seconds;i++) {
            try {
                logMessageToConsole(String.format("Waiting for iFrame %s for %s seconds", frameName, i));
                getDesktopDriver().switchTo().frame(frameName);
                return;
            } catch (NoSuchFrameException e) {
                LOGGER.log(Level.FINE, String.format("iFrame with name %s still not exist", frameName));
            }
            dialogPause();
        }
        throw new TimeoutException(String.format("iFrame with name %s not exist!", frameName));
    }

    /**
     * Will wait for given iFrame
     * @param index - iFrame index
     */
    public void waitForIFrame(int index) {
        tryIFrame(index, 30);
    }

    /**
     * Will wait for given iFrame
     * @param index - iFrame index
     * @param secondsToWait - time to wait in seconds
     */
    public void waitForIFrame(int index, int secondsToWait) {
        tryIFrame(index, secondsToWait);
    }

    private void tryIFrame(int index, int seconds){
        for(int i = 0;i<seconds;i++) {
            try {
                logMessageToConsole(String.format("Waiting for iFrame %s for %s seconds", index, i));
                getDesktopDriver().switchTo().frame(index);
                return;
            } catch (NoSuchFrameException e) {
                LOGGER.log(Level.FINE, String.format("iFrame with index %s still not exist", index));
            }
            dialogPause();
        }
        throw new TimeoutException(String.format("iFrame with index %s not exist!", index));
    }

    /**
     * Will wait for document.readyState = complete
     */
    public void waitUntilDocumentComplete() {
        logMessageToConsole("Waiting for document state 'complete'");
        wait.until(driver1 -> ((JavascriptExecutor)getDesktopDriver()).executeScript("return document.readyState").equals("complete"));
        logMessageToConsole("document state is 'complete'");
    }

    /**
     * Open new browser tab
     */
    public void openNewTab(){
        runJavascript("window.open('about:blank','_blank');");
        final Set<String> tabHandles = getDesktopDriver().getWindowHandles();
        getDesktopDriver().switchTo().window(tabHandles.toArray()[tabHandles.size()-1].toString());
        dialogPause();
        getDesktopDriver().switchTo().activeElement();
    }

    /**
     * Open new browser tab and load given url
     * @param url - url
     */
    public void openNewTab(String url){
        runJavascript("window.open('about:blank','_blank');");
        final Set<String> tabHandles = getDesktopDriver().getWindowHandles();
        getDesktopDriver().switchTo().window(tabHandles.toArray()[tabHandles.size()-1].toString());
        dialogPause();
        getDesktopDriver().switchTo().activeElement();
        openURL(url);
    }

    /**
     * Close current browser tab
     */
    public void closeCurrentTab(){
        getDesktopDriver().close();
        final Set<String> tabHandles = getDesktopDriver().getWindowHandles();
        getDesktopDriver().switchTo().window(tabHandles.toArray()[tabHandles.size()-1].toString());
        dialogPause();
        getDesktopDriver().switchTo().activeElement();
    }

    /**
     * Switch to last browser tab
     */
    public void switchToLastTab() {
        final Set<String> tabHandles = getDesktopDriver().getWindowHandles();
        getDesktopDriver().switchTo().window(tabHandles.toArray()[tabHandles.size()-1].toString());
        dialogPause();
        getDesktopDriver().switchTo().activeElement();
    }

    /**
     * Switch to browser tab by tab index
     * @param tabNumber - browser tab index
     */
    public void switchToTab(int tabNumber) {
        final Set<String> tabHandles = getDesktopDriver().getWindowHandles();
        getDesktopDriver().switchTo().window(tabHandles.toArray()[tabNumber-1].toString());
        dialogPause();
        getDesktopDriver().switchTo().activeElement();
    }

    /**
     * Will wait for element become clickable
     * @param elementName - element to wait for
     */
    public void waitForClickable(String elementName){
        tryClickable(elementName, 30);
    }

    /**
     * Will wait for element become clickable for given time
     * @param elementName - element to wait for
     * @param secondsToWait - time to wait in seconds
     */
    public void waitForClickable(String elementName, int secondsToWait){
        tryClickable(elementName, secondsToWait);
    }

    private void tryClickable(String elementName, int seconds) {
        for(int i = 1; i<seconds; i++){
            try {
                clickOnElement(elementName);
                return;
            } catch (WebDriverException|NullPointerException e) {
                LOGGER.log(Level.INFO,"Waiting for " + elementName + " being clickable for " + i + " seconds");
                dialogPause();
            }
        }
        throw new TimeoutException("Element is still not clickable!");
    }

    /**
     * Simulates mouse doubleclick on given element
     * @param elementName - element from repository
     */
    public void doDoubleClick(String elementName) {
        final Actions actions = new Actions(getDesktopDriver());
        actions.doubleClick(dataReader.getElement(elementName)).perform();
    }

    /**
     * Simulates mouse rightclick on given element
     * @param elementName - element from repository
     */
    public void doRightClick(String elementName) {
        final Actions actions = new Actions(getDesktopDriver());
        actions.contextClick(dataReader.getElement(elementName)).perform();
    }

    /**
     * Click on the 'OK' button of the alert and log the alert message
     */
    public void alertAccept() {
        final Alert alert = dataReader.getDriver().switchTo().alert();
        logMessageToConsole("Alert text: " + alert.getText());
        alert.accept();
    }

    /**
     * Click on the 'Cancel' button of the alert and log the alert message
     */
    public void alertDismiss() {
        final Alert alert = dataReader.getDriver().switchTo().alert();
        logMessageToConsole("Alert text: " + alert.getText());
        alert.dismiss();
    }

    /**
     * Send the string to alert box and click on the 'OK' button of the alert and log the alert message
     * @param sendKeys - keys to send
     */
    public void alertAccept(String sendKeys) {
        final Alert alert = dataReader.getDriver().switchTo().alert();
        logMessageToConsole("Alert text: " + alert.getText());
        alert.sendKeys(sendKeys);
        alert.accept();
    }

    /**
     * Get browser console log and write it to the console and file in TestOutputs
     */
    public void getBrowserConsoleLog() {
        final Capabilities cap = ((RemoteWebDriver) getDesktopDriver()).getCapabilities();
        final String browserName = cap.getBrowserName();
        if("chrome".equals(browserName)) {
            Path path = Paths.get(OUTPUT_FOLDER + CONSOLELOGS_FOLDER);
            if(!Files.exists(path)) {
                new File(OUTPUT_FOLDER + CONSOLELOGS_FOLDER).mkdir();
            }
            try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(OUTPUT_FOLDER + CONSOLELOGS_FOLDER
                    + super.getClass().getSimpleName()
                    + UNDERSCORE + getCurrentDateTime("dd-MM-yyyy_HHmms")
                    + SUFFIX_CSV));
                final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withDelimiter(";".charAt(0)))) {
                final List<String> rowToWrite = new ArrayList<>();
                final LogEntries logEntries = getDesktopDriver().manage().logs().get(LogType.BROWSER);
                for (final LogEntry entry : logEntries) {
                    logMessageToConsole(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                    rowToWrite.add(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                    csvPrinter.printRecord(rowToWrite);
                    rowToWrite.clear();
                }
                csvPrinter.flush();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE,"Error while writing csv", e);
            }
        } else {
            LOGGER.log(Level.WARNING, "getBrowserConsoleLog function is supported only by CHROME");
        }
    }

    /**
     * Assert that given graphic element exist on current screen
     * @param sikuliElementName - element name from Sikuli repository
     * @return - true if exists
     */
    public boolean assertGraphicElementExist(String sikuliElementName) {
        final WiniumInject inject = new WiniumInject(getDesktopDriver());
        final File screenFile = inject.getWiniumDriver().getScreenshotAs(OutputType.FILE);
        final Finder finder = new GraphicHelper().prepareFinder(screenFile, sikuliElementName);
        if(finder.hasNext()) {
            LOGGER.log(Level.INFO, sikuliElementName + "not found on screen");
            inject.stopWiniumInjection();
            return true;
        }
        LOGGER.log(Level.INFO, sikuliElementName + "not found on screen");
        inject.stopWiniumInjection();
        return false;
    }

    /**
     * Click on graphic element on current screen
     * @param sikuliElementName - element name from Sikuli repository
     */
    public void clickOnGraphicElement(String sikuliElementName) {
        final WiniumInject inject = new WiniumInject(getDesktopDriver());
        final File screenFile = inject.getWiniumDriver().getScreenshotAs(OutputType.FILE);
        final Finder finder = new GraphicHelper().prepareFinder(screenFile, sikuliElementName);
        if(finder.hasNext()) {
            final Match match = finder.next();
            new Actions(inject.getWiniumDriver()).moveByOffset(-screen.getWidth(), -screen.getHeight()).perform();
            LOGGER.log(Level.INFO, sikuliElementName + " found on screen, will click on it");
            new Actions(inject.getWiniumDriver()).moveByOffset(match.getTarget().x, match.getTarget().y).click().build().perform();
            inject.stopWiniumInjection();
            return;
        }
        throw new NoSuchElementException("Can't find " + sikuliElementName + " on screen");
    }

    /**
     * Right click on graphic element on current screen
     * @param sikuliElementName - element name from Sikuli repository
     */
    public void rightClickOnGraphicElement(String sikuliElementName) {
        final WiniumInject inject = new WiniumInject(getDesktopDriver());
        final File screenFile = inject.getWiniumDriver().getScreenshotAs(OutputType.FILE);
        final Finder finder = new GraphicHelper().prepareFinder(screenFile, sikuliElementName);
        if(finder.hasNext()) {
            final Match match = finder.next();
            new Actions(inject.getWiniumDriver()).moveByOffset(-screen.getWidth(), -screen.getHeight()).perform();
            LOGGER.log(Level.INFO, sikuliElementName + " found on screen, will right click on it");
            new Actions(inject.getWiniumDriver()).moveByOffset(match.getTarget().x, match.getTarget().y).contextClick().build().perform();
            inject.stopWiniumInjection();
            return;
        }
        throw new NoSuchElementException("Can't find " + sikuliElementName + " on screen");
    }

    /**
     * Find a text on current screen and click on it
     * @param text - text to find
     */
    public void getTextOnScreenAndClick(String text) {
        logMessageToConsole("Will try to find text on screen");
        final WiniumInject inject = new WiniumInject(getDesktopDriver());
        final Match match = prepareTextFinder(inject, text);
        LOGGER.log(Level.INFO, text + " found on screen, will click on it");
        new Actions(inject.getWiniumDriver()).moveByOffset(match.getTarget().x, match.getTarget().y).click().build().perform();
        inject.stopWiniumInjection();
    }

    /**
     * Find a text on current screen and click on it
     * @param text - text to find
     */
    public void getTextOnScreenAndRightClick(String text) {
        logMessageToConsole("Will try to find text on screen");
        final WiniumInject inject = new WiniumInject(getDesktopDriver());
        final Match match = prepareTextFinder(inject, text);
        LOGGER.log(Level.INFO, text + " found on screen, will right click on it");
        new Actions(inject.getWiniumDriver()).moveByOffset(match.getTarget().x, match.getTarget().y).contextClick().build().perform();
        inject.stopWiniumInjection();
    }

    /**
     * Verify that given text exists on current screen
     * @param text - text to find
     */
    public boolean verifyTextOnScreenExist(String text) {
        logMessageToConsole("Will try to find text on screen");
        final WiniumInject inject = new WiniumInject(getDesktopDriver());
        Match match = null;
        try {
             match = prepareTextFinder(inject, text);
        } catch (Exception e) {
            LOGGER.log(Level.INFO, text + " not found on screen");
            return false;
        }
        inject.stopWiniumInjection();
        if (match.getText()==null) {
            LOGGER.log(Level.INFO, text + " not found on screen");
            return false;
        }
        return true;
    }

    private Match prepareTextFinder(WiniumInject winiumInject, String text) {
        logMessageToConsole("Will try to find text on screen");
        Match match = null;
        final WiniumInject inject = new WiniumInject(getDesktopDriver());
        final File screenFile = inject.getWiniumDriver().getScreenshotAs(OutputType.FILE);
        try {
            screen = ImageIO.read(screenFile);
            final Finder finder = new Finder(screen);
            finder.find(screen);
            if(finder.hasNext()) {
                match = finder.next();
                match = match.findText(text);
                new Actions(inject.getWiniumDriver()).moveByOffset(-screen.getWidth(), -screen.getHeight()).perform();
            }
        } catch (IOException e) {
            inject.stopWiniumInjection();
            LOGGER.log(Level.SEVERE, "Error reading file in assertGraphicElementExist", e);
        } catch (FindFailed findFailed) {
            inject.stopWiniumInjection();
            LOGGER.log(Level.SEVERE, "Can't find " + text + " on screen");
        }
        return match;
    }
}