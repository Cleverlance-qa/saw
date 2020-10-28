package corehelpers;

import corehelpers.constants.SAWConstants;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.*;
import org.openqa.selenium.winium.WiniumDriver;
import org.sikuli.script.Screen;

import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Inner class for finding element taken from element repository
 */
public class ElementsFinder {
    protected static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static String SIKULI_REPO_PATH = SAWConstants.TESTINPUTS + "sikuliRepository/";
    protected static final String ESCAPE_NBSP = "%%";
    protected WebDriver driver;
    protected AppiumDriver appDriver;
    protected WiniumDriver winiumDriver;
    protected String csvFile = SAWConstants.TESTINPUTS + "testElementsRepository.csv";
    protected boolean isMobile = false;
    protected boolean isWinium = false;
    protected Screen screen;
    protected String cvsSplitBy = ";";
    protected String[] elementParameter;

    /**
     * Basic constructor. Set global logger.
     */
    public ElementsFinder() {
        for (final Handler handler : LOGGER.getHandlers()) {
            LOGGER.removeHandler(handler);
        }
        final Handler handlerObj = new ConsoleHandler();
        handlerObj.setLevel(Level.ALL);
        LOGGER.addHandler(handlerObj);
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
    }

    protected WebElement doElementSearch(String[] element, boolean parametrized) {
        for (int x = 1; x < 4; x++) {
            switch (x) {
                case 1:
                    if (!element[1].equals(SAWConstants.EMPTY_TEXT)) {
                        return getElementFromCSVId(element[1], parametrized);
                    }
                    break;
                case 2:
                    if (!element[2].equals(SAWConstants.EMPTY_TEXT)) {
                        return getElementFromCSVName(element[2], parametrized);
                    }
                    break;
                case 3:
                    if (!element[3].equals(SAWConstants.EMPTY_TEXT)) {
                        return getElementFromCSVXpath(element[3], parametrized);
                    }
            }
        }
        throw new NoSuchElementException(String.format("Element %s not found in element repository", element[0]));
    }

    protected WebElement getElementFromCSVId(String element, boolean parametrized) {
        WebElement webElement = null;
        element = replaceElementParameters(element, parametrized);
        if (!element.equals(SAWConstants.EMPTY_TEXT)) {
            if (isMobile) {
                try {
                    webElement = appDriver.findElement(By.id(element));
                } catch (WebDriverException e) {
                    LOGGER.log(Level.FINE, "ID not found, will use AccesibilityID");
                    webElement = appDriver.findElement(MobileBy.AccessibilityId(element));
                }
            } else if (isWinium) {
                webElement = winiumDriver.findElement(By.id(element));
            } else {
                webElement = driver.findElement(By.id(element));
            }
        }
        return webElement;
    }

    protected WebElement getElementFromCSVName(String element, boolean parametrized) {
        WebElement webElement = null;
        element = replaceElementParameters(element, parametrized);
        if (!element.equals(SAWConstants.EMPTY_TEXT)) {
            if (isMobile) {
                webElement = appDriver.findElement(By.name(element));
            } else if (isWinium) {
                webElement = winiumDriver.findElement(By.name(element));
            } else {
                webElement = driver.findElement(By.name(element));
            }
        }
        return webElement;
    }

    protected WebElement getElementFromCSVXpath(String element, boolean parametrized) {
        WebElement webElement = null;
        element = replaceElementParameters(element, parametrized);
        if (!element.equals(SAWConstants.EMPTY_TEXT)) {
            if (isMobile) {
                webElement = appDriver.findElement(By.xpath(fixNbsp(element)));
            } else if (isWinium) {
                webElement = winiumDriver.findElement(By.xpath(fixNbsp(element)));
            } else {
                webElement = driver.findElement(By.xpath(fixNbsp(element)));
            }
        }
        return webElement;
    }

    protected List<WebElement> doElementsSearch(String[] element, boolean parametrized) {
        List<WebElement> webElementList = null;
        elementSearch: for (int x = 1; x < 4; x++) {
            switch (x) {
                case 1:
                    if (!element[1].equals(SAWConstants.EMPTY_TEXT)) {
                        webElementList = getElementsFromCSVId(element[1], parametrized);
                        break elementSearch;
                    }
                    break;
                case 2:
                    if (!element[2].equals(SAWConstants.EMPTY_TEXT)) {
                        webElementList = getElementsFromCSVName(element[2], parametrized);
                        break elementSearch;
                    }
                    break;
                case 3:
                    if (!element[3].equals(SAWConstants.EMPTY_TEXT)) {
                        webElementList = getElementsFromCSVXpath(element[3], parametrized);
                        break elementSearch;
                    }
            }
        }
        return webElementList;
    }

    private List<WebElement> getElementsFromCSVId(String element, boolean parametrized) {
        List<WebElement> webElements = null;
        element = replaceElementParameters(element, parametrized);
        if (!element.equals(SAWConstants.EMPTY_TEXT)) {
            if (isMobile) {
                try {
                    webElements = appDriver.findElements(By.id(element));
                } catch (WebDriverException e) {
                    LOGGER.log(Level.FINE, "ID not found, will use AccesibilityID");
                    webElements = appDriver.findElements(MobileBy.AccessibilityId(element));
                }
            } else if (isWinium) {
                webElements = winiumDriver.findElements(By.id(element));
            } else {
                webElements = driver.findElements(By.id(element));
            }
        }
        return webElements;
    }

    private List<WebElement> getElementsFromCSVName(String element, boolean parametrized) {
        List<WebElement> webElements = null;
        element = replaceElementParameters(element, parametrized);
        if (!element.equals(SAWConstants.EMPTY_TEXT)) {
            if (isMobile) {
                webElements = appDriver.findElements(By.name(element));
            } else if (isWinium) {
                webElements = winiumDriver.findElements(By.name(element));
            } else {
                webElements = driver.findElements(By.name(element));
            }
        }
        return webElements;
    }

    private List<WebElement> getElementsFromCSVXpath(String element, boolean parametrized) {
        List<WebElement> webElements = null;
        element = replaceElementParameters(element, parametrized);
        if (!element.equals(SAWConstants.EMPTY_TEXT)) {
            if (isMobile) {
                webElements = appDriver.findElements(By.xpath(fixNbsp(element)));
            } else if (isWinium) {
                webElements = winiumDriver.findElements(By.xpath(fixNbsp(element)));
            } else {
                webElements = driver.findElements(By.xpath(fixNbsp(element)));
            }
        }
        return webElements;
    }

    private String replaceElementParameters(String element, boolean parametrized) {
        if(parametrized) {
            if(elementParameter == null) {
                LOGGER.log(Level.SEVERE,"ELEMENT PARAMETERS ARE NOT SET!");
            }
            for (int i = 0; i < elementParameter.length; i++) {
                element = element.replaceFirst("%s", elementParameter[i]);
            }
            LOGGER.log(Level.INFO, "Element with parameters: " + element);
        }
        return element;
    }

    //Method for hadling white spaces in xpath
    protected String fixNbsp(String element) {
        return element.replaceAll(ESCAPE_NBSP, "\u00a0");
    }

    /**
     * Set repo path for Sikuli
     * @param sikuliRepoPath - new path
     */
    public void setSikuliRepoPath(String sikuliRepoPath) {
        this.SIKULI_REPO_PATH = sikuliRepoPath;
    }
    
}