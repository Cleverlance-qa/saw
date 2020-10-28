package corehelpers;

import corehelpers.constants.SAWConstants;
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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Base class of whole framework. DataReader stores instances, read elements from repositories and xml properties
 */
public class DataReaderImpl extends ElementsFinder implements DataReader {
    private static final String SIKULI = "sikuli";
    private static final String WINIUM = "winium";
    private String xmlUrl = SAWConstants.TESTINPUTS + "properties.xml";
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
    public DataReaderImpl(WebDriver driver, String platform, String xmlUrl) {
        super();
        setXmlUrl(xmlUrl);
        this.driver = driver;
        this.platform = platform;
        setDefaultElementRepository();
        setExcelReporting();
    }

    /**
     * Inicialized DataReader for WebDriver instance for given platform
     * @param driver - WebDriver instance
     * @param platform - given platform
     */
    public DataReaderImpl(WebDriver driver, String platform) {
        this(driver, platform, SAWConstants.TESTINPUTS + "properties.xml");
    }

    /**
     * Inicialized DataReader for WebDriver instance
     * @param driver - WebDriver instance
     */
    public DataReaderImpl(WebDriver driver) {
        this(driver, null, SAWConstants.TESTINPUTS + "properties.xml");
    }

    /**
     * Inicialized DataReader for Winium instance
     * @param winiumDriver - Winium instance
     * @param xmlUrl - path to properties file
     */
    public DataReaderImpl(WiniumDriver winiumDriver, String xmlUrl) {
        super();
        setXmlUrl(xmlUrl);
        this.winiumDriver = winiumDriver;
        setDefaultElementRepository();
        isWinium = true;
        platform = WINIUM;
        setExcelReporting();
    }

    /**
     * Inicialized DataReader for Winium instance
     * @param winiumDriver - Winium instance
     */
    public DataReaderImpl(WiniumDriver winiumDriver) {
        this(winiumDriver, SAWConstants.TESTINPUTS + "properties.xml");
    }

    /**
     * Inicialized DataReader for Appium instance for given device
     * @param appDriver - Appium instance
     * @param device - given device
     * @param xmlUrl - path to properties file
     */
    public DataReaderImpl(AppiumDriver appDriver, String device, String xmlUrl) {
        super();
        setXmlUrl(xmlUrl);
        this.appDriver = appDriver;
        this.platform = device;
        setDefaultElementRepository();
        isMobile = true;
        setExcelReporting();
    }

    /**
     * Inicialized DataReader for Sikulix instance
     * @param screen - Sikulix instance
     * @param xmlUrl - path to properties file
     */
    public DataReaderImpl(Screen screen, String xmlUrl) {
        super();
        setXmlUrl(xmlUrl);
        this.screen = screen;
        platform = SIKULI;
        setExcelReporting();
    }

    /**
     * Inicialized DataReader for Sikulix instance
     * @param screen - Sikulix instance
     */
    public DataReaderImpl(Screen screen) {
        this(screen, SAWConstants.TESTINPUTS + "properties.xml");
    }

    /**
     * Inicialized DataReader without any driver. This should be used only for properties reason.
     */
    public DataReaderImpl() {
        super();
        setDefaultElementRepository();
        setExcelReporting();
    }

    public HashMap<String, String> getGrafanaCustomFieldMap() {
        return grafanaCustomFieldMap;
    }

    public void addValueToGrafanaCustomFieldMap(String name, String value) {
        grafanaCustomFieldMap.put(name, value);
    }

    public HashMap<String, String> getGrafanaCustomTagMap() {
        return grafanaCustomTagMap;
    }

    public void addValueToGrafanaCustomTagMap(String name, String value) {
        grafanaCustomTagMap.put(name, value);
    }

    public boolean isExcelReporting() {
        return excelReporting;
    }

    public void setExcelReporting() {
        this.excelReporting = Boolean.parseBoolean(getXMLProperties().getProperty("excelReporting"));
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setXmlUrl(String xmlUrl) {
        this.xmlUrl = xmlUrl;
    }

    public AppiumDriver getAppDriver() {
        return appDriver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public WiniumDriver getWiniumDriver() {
        return winiumDriver;
    }

    public String getPlatform() {
        return platform;
    }

    public Screen getScreen() {
        return screen;
    }

    public boolean isMobile() {
        return isMobile;
    }

    public void setCsvFile(String csvFile) {
        this.csvFile = SAWConstants.TESTINPUTS + csvFile + SAWConstants.SUFFIX_CSV;
    }

    public void setDefaultElementRepository() {
        setCsvFile(getXMLProperties().getProperty(SAWConstants.DEFAULT_ELEMENT_REPO));
    }

    public Properties getXMLProperties() {
        Properties prop = new Properties();;
        try(InputStream input = new FileInputStream(xmlUrl)) {
            prop.loadFromXML(input);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error with data reading", e);
        }
        return (Properties) prop.clone();
    }

    public WebElement getElement(String elementName) throws NoSuchElementException {
        String line;
        boolean parametrized = false;

        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                final String[] element = line.split(cvsSplitBy);
                if (element[0].equals(elementName)) {
                    if(element.length == 5 && "true".equals(element[4])) {
                        parametrized = true;
                    }
                    return doElementSearch(element, parametrized);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in getElement when reads csv file",e);
        }
        LOGGER.log(Level.SEVERE, String.format("Element %s not found in element repository", elementName));
        throw new NoSuchElementException(String.format("Element %s not found in element repository", elementName));
    }

    public List<WebElement> getElements(String elementName) throws NoSuchElementException {
        String line;
        boolean parametrized = false;

        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                final String[] element = line.split(cvsSplitBy);
                if (element[0].equals(elementName)) {
                    if(element.length == 5 && "true".equals(element[4])) {
                        parametrized = true;
                    }
                    return doElementsSearch(element, parametrized);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in getElement when reads csv file",e);
        }
        LOGGER.log(Level.SEVERE, String.format("Element %s not found in element repository", elementName));
        throw new NoSuchElementException(String.format("Element %s not found in element repository", elementName));
    }

    public Region getSikuliElement(String elementName) throws NoSuchElementException {
        elementName = SIKULI_REPO_PATH + elementName + SAWConstants.SUFFIX_PNG;
        if(new File(elementName).exists()) {
            try {
                return screen.find(elementName);
            } catch (FindFailed findFailed) {
                LOGGER.log(Level.SEVERE, String.format("Element %s not found on screen!", elementName));
                throw new NoSuchElementException(String.format("Element %s not found on screen!", elementName));
            }
        }
        LOGGER.log(Level.SEVERE, String.format("Can't find file %s", elementName));
        throw new NoSuchElementException(String.format("Can't find file %s", elementName));
    }

    public String getSikuliElementPath(String elementName) {
        return SIKULI_REPO_PATH + elementName + SAWConstants.SUFFIX_PNG;
    }

    public String getElementXpath(String elementName) {
        return getElementByIndex(elementName, 3);
    }

    public String getElementID(String elementName) {
        return getElementByIndex(elementName, 1);
    }

    public String getElementName(String elementName) {
        return getElementByIndex(elementName, 2);
    }

    private String getElementByIndex(String elementName, int index) throws NoSuchElementException {
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
        throw new NoSuchElementException(String.format("Element %s not found", elementName));
    }

    public String[] getElementParameter() {
        return elementParameter;
    }

    public void setElementParameter(String... elementParameters) {
        this.elementParameter = elementParameters;
    }

    public void setSikuliApp (App app) {
        sikuliApp = app;
    }

    public App getSikuliApp() {
        return sikuliApp;
    }

    public String getSikuliRepoPath() {
        return SIKULI_REPO_PATH;
    }
}