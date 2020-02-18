package corehelpers;

import corehelpers.crypting.Decryptor;
import corehelpers.crypting.DecryptorImpl;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.winium.WiniumDriver;
import org.sikuli.script.App;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Base class of whole framework. DataReader stores instances, read elements from repositories and xml properties
 */
public class DataReader extends ElementsFinder {
    private static final String SUFFIX_PNG = ".PNG";
    private static final String SUFFIX_CSV = ".csv";
    private static final String SIKULI = "sikuli";
    private static final String WINIUM = "winium";
    private static final String DEFAULT_ELEMENT_REPO = "defaultElementRepository";
    private String xmlUrl = TESTINPUTS + "properties.xml";
    private String platform;
    private boolean excelReporting;
    private final HashMap<String, String> grafanaCustomFieldMap = new HashMap<>();
    private final HashMap<String, String> grafanaCustomTagMap = new HashMap<>();
    private App sikuliApp;

    /**
     * Inicialized DataReader for WebDriver instance for given platform
     * @param driver - WebDriver instance
     * @param platform - given platform
     * @param xmlUrl - path to properties file
     */
    public DataReader(WebDriver driver, String platform, String xmlUrl) {
        super();
        setXmlUrl(xmlUrl);
        this.driver = driver;
        this.platform = platform;
        setDefaultElementRepository();
        setExcelReporting(Boolean.parseBoolean(getXMLProperties().getProperty("excelReporting")));
    }

    /**
     * Inicialized DataReader for WebDriver instance for given platform
     * @param driver - WebDriver instance
     * @param platform - given platform
     */
    public DataReader(WebDriver driver, String platform) {
        super();
        this.driver = driver;
        this.platform = platform;
        setDefaultElementRepository();
        setExcelReporting(Boolean.parseBoolean(getXMLProperties().getProperty("excelReporting")));
    }

    /**
     * Inicialized DataReader for Winium instance
     * @param winiumDriver - Winium instance
     */
    public DataReader(WiniumDriver winiumDriver) {
        super();
        this.winiumDriver = winiumDriver;
        setDefaultElementRepository();
        isWinium = true;
        platform = WINIUM;
        setExcelReporting(Boolean.parseBoolean(getXMLProperties().getProperty("excelReporting")));
    }

    /**
     * Inicialized DataReader for WebDriver instance
     * @param driver - WebDriver instance
     */
    public DataReader(WebDriver driver) {
        super();
        this.driver = driver;
        setDefaultElementRepository();
        setExcelReporting(Boolean.parseBoolean(getXMLProperties().getProperty("excelReporting")));
    }

    /**
     * Inicialized DataReader for Appium instance for given device
     * @param appDriver - Appium instance
     * @param device - given device
     * @param xmlUrl - path to properties file
     */
    public DataReader(AppiumDriver appDriver, String device, String xmlUrl) {
        super();
        setXmlUrl(xmlUrl);
        this.appDriver = appDriver;
        this.platform = device;
        setDefaultElementRepository();
        isMobile = true;
        setExcelReporting(Boolean.parseBoolean(getXMLProperties().getProperty("excelReporting")));
    }

    /**
     * Inicialized DataReader for Winium instance
     * @param winiumDriver - Winium instance
     * @param xmlUrl - path to properties file
     */
    public DataReader(WiniumDriver winiumDriver, String xmlUrl) {
        super();
        setXmlUrl(xmlUrl);
        this.winiumDriver = winiumDriver;
        setDefaultElementRepository();
        isWinium = true;
        platform = WINIUM;
        setExcelReporting(Boolean.parseBoolean(getXMLProperties().getProperty("excelReporting")));
    }

    /**
     * Inicialized DataReader for Sikulix instance
     * @param screen - Sikulix instance
     * @param xmlUrl - path to properties file
     */
    public DataReader(Screen screen, String xmlUrl) {
        super();
        setXmlUrl(xmlUrl);
        this.screen = screen;
        platform = SIKULI;
        setExcelReporting(Boolean.parseBoolean(getXMLProperties().getProperty("excelReporting")));
    }

    /**
     * Inicialized DataReader for Sikulix instance
     * @param screen - Sikulix instance
     */
    public DataReader(Screen screen) {
        super();
        this.screen = screen;
        platform = SIKULI;
        setExcelReporting(Boolean.parseBoolean(getXMLProperties().getProperty("excelReporting")));
    }

    /**
     * Inicialized DataReader without any driver. This should be used only for properties reason.
     */
    public DataReader() {
        super();
        setDefaultElementRepository();
        setExcelReporting(Boolean.parseBoolean(getXMLProperties().getProperty("excelReporting")));
    }

    /**
     * Return map of custom values for grafana reporting
     * @return - map of values
     */
    public HashMap<String, String> getGrafanaCustomFieldMap() {
        return grafanaCustomFieldMap;
    }

    /**
     * Add new value to grafanaCustomFieldMap
     * @param name - name value
     * @param value - main value
     */
    public void addValueToGrafanaCustomFieldMap(String name, String value) {
        grafanaCustomFieldMap.put(name, value);
    }

    /**
     * Return map of custom tag values for grafana reporting
     * @return - map of values
     */
    public HashMap<String, String> getGrafanaCustomTagMap() {
        return grafanaCustomTagMap;
    }

    /**
     * Add new value to grafanaCustomTagMap
     * @param name - name value
     * @param value - main value
     */
    public void addValueToGrafanaCustomTagMap(String name, String value) {
        grafanaCustomTagMap.put(name, value);
    }

    /**
     * Return if the excel reporting is enabled
     * @return - true if enabled
     */
    public boolean isExcelReporting() {
        return excelReporting;
    }

    /**
     * Switch excel reporting to enabled/disabled
     * @param excelReporting - true for enabled
     */
    public void setExcelReporting(boolean excelReporting) {
        this.excelReporting = excelReporting;
    }

    /**
     * Set current test platform
     * @param platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * Set which properties xml file shoud be load
     * @param xmlUrl - path to xml file
     */
    public void setXmlUrl(String xmlUrl) {
        this.xmlUrl = xmlUrl;
    }

    /**
     * Return curren appium instance
     * @return - appium instance
     */
    public AppiumDriver getAppDriver() {
        return appDriver;
    }

    /**
     * Return current webdriver instance
     * @return - webdriver instance
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Return current Winium instance
     * @return - winium instance
     */
    public WiniumDriver getWiniumDriver() {
        return winiumDriver;
    }

    /**
     * Return current platform
     * @return - current platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Return current Sikulix Screen instance
     * @return - current Screen instance
     */
    public Screen getScreen() {
        return screen;
    }

    /**
     * Return true if mobile instance running
     * @return - true if mobile instance
     */
    public boolean isMobile() {
        return isMobile;
    }

    /**
     * Set csv file with element repository to given one
     * @param csvFile - csv file with new element repository
     */
    public void setCsvFile(String csvFile) {
        this.csvFile = TESTINPUTS + csvFile + SUFFIX_CSV;
    }

    /**
     * Switch element repository to default element repository
     */
    public void setDefaultElementRepository() {
        setCsvFile(getXMLProperties().getProperty(DEFAULT_ELEMENT_REPO));
    }

    /**
     * Method for loading properties.xml from testInputs
     * @return - loaded Properties
     */
    public Properties getXMLProperties() {
        Properties prop = new Properties();
        try(InputStream input = new FileInputStream(xmlUrl)) {
            prop.loadFromXML(input);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error with data reading", e);
        }

        final Decryptor decryptor = new DecryptorImpl();
        prop = decryptor.decryptProperties(prop);

        return (Properties) prop.clone();
    }

    /**
     * Get WebElement element from elementRepository
     * @param elementName - given element
     * @return - WebElement from elementRepository
     */
    public WebElement getElement(String elementName) {
        String line;
        boolean parametrized = false;
        WebElement webElement = null;

        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"))) {
            while ((line = br.readLine()) != null) {
                final String[] element = line.split(cvsSplitBy);
                if (element[0].equals(elementName)) {
                    if(element.length == 5 && "true".equals(element[4])) {
                        parametrized = true;
                    }
                    webElement = doElementSearch(element, parametrized);
                    return webElement;
                }
            }
            return webElement;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in getElement when reads csv file",e);
        }
        LOGGER.log(Level.SEVERE, String.format("Element %s not found in element repository", elementName));
        throw new NoSuchElementException(String.format("Element %s not found in element repository", elementName));
    }

    /**
     * Get WebElement elements from elementRepository
     * @param elementName - given element
     * @return - WebElements from elementRepository
     */
    public List<WebElement> getElements(String elementName) {
        String line;
        boolean parametrized = false;
        List<WebElement> webElementList = null;

        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"))) {
            while ((line = br.readLine()) != null) {
                final String[] element = line.split(cvsSplitBy);
                if (element[0].equals(elementName)) {
                    if(element.length == 5 && "true".equals(element[4])) {
                        parametrized = true;
                    }
                    webElementList = doElementsSearch(element, parametrized);
                    return webElementList;
                }
            }
            return webElementList;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in getElement when reads csv file",e);
        }
        LOGGER.log(Level.SEVERE, String.format("Element %s not found in element repository", elementName));
        throw new NoSuchElementException(String.format("Element %s not found in element repository", elementName));
    }

    /**
     * Get Sikulix element from Sikulix elementRepository
     * @param elementName - given element
     * @return - element from Sikulix elementRepository
     */
    public Region getSikuliElement(String elementName) {
        Region region = null;
        elementName = SIKULI_REPO_PATH + elementName + SUFFIX_PNG;
        try {
            region = screen.find(elementName);
        } catch (FindFailed findFailed) {
            LOGGER.log(Level.SEVERE, String.format("Element %s not found on screen!", elementName), findFailed);
        }
        return region;
    }

    /**
     * Return path of Sikulix element
     * @param elementName - given element
     * @return - path of given element
     */
    public String getSikuliElementPath(String elementName) {
        return SIKULI_REPO_PATH + elementName + SUFFIX_PNG;
    }

    /**
     * Return xpath from elementRepository for given element
     * @param elementName - given element
     * @return - xpath of given element
     */
    public String getElementXpath(String elementName) {
        return getElementByIndex(elementName, 3);
    }

    /**
     * Return id from elementRepository for given element
     * @param elementName - given element
     * @return - id of given element
     */
    public String getElementID(String elementName) {
        return getElementByIndex(elementName, 1);
    }

    /**
     * Return name from elementRepository for given element
     * @param elementName - given element
     * @return - name of given element
     */
    public String getElementName(String elementName) {
        return getElementByIndex(elementName, 2);
    }

    private String getElementByIndex(String elementName, int index) {
        String line;
        try(BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                final String[] element = line.split(cvsSplitBy);
                if (element[0].equals(elementName)) {
                    return fixNbsp(element[index]);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Element %s not found", elementName), e);
        }
        return null;
    }

    /**
     * Get parametr set for element repository parametrization
     * @return - current parameter
     */
    public String getElementParameter() {
        return elementParameter;
    }

    /**
     * Set parametr for element repository parametrization
     * @param elementParameter - given parameter
     */
    public void setElementParameter(String elementParameter) {
        this.elementParameter = elementParameter;
    }

    /**
     * Set Sikuli application
     * @param app - app
     */
    public void setSikuliApp (App app) {
        sikuliApp = app;
    }

    /**
     * Get Sikuli application
     * @return - sikuliApp
     */
    public App getSikuliApp() {
        return sikuliApp;
    }
}