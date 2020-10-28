package corehelpers;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.winium.WiniumDriver;
import org.sikuli.script.App;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public interface DataReader {

    /**
     * Return map of custom values for grafana reporting
     * @return - map of values
     */
    public HashMap<String, String> getGrafanaCustomFieldMap();

    /**
     * Add new value to grafanaCustomFieldMap
     * @param name - name value
     * @param value - main value
     */
    public void addValueToGrafanaCustomFieldMap(String name, String value);

    /**
     * Return map of custom tag values for grafana reporting
     * @return - map of values
     */
    public HashMap<String, String> getGrafanaCustomTagMap();

    /**
     * Add new value to grafanaCustomTagMap
     * @param name - name value
     * @param value - main value
     */
    public void addValueToGrafanaCustomTagMap(String name, String value);

    /**
     * Return if the excel reporting is enabled
     * @return - true if enabled
     */
    public boolean isExcelReporting();

    /**
     * Switch excel reporting to enabled/disabled
     */
    public void setExcelReporting();

    /**
     * Set current test platform
     * @param platform
     */
    public void setPlatform(String platform);

    /**
     * Set which properties xml file shoud be load
     * @param xmlUrl - path to xml file
     */
    public void setXmlUrl(String xmlUrl);

    /**
     * Return curren appium instance
     * @return - appium instance
     */
    public AppiumDriver getAppDriver();

    /**
     * Return current webdriver instance
     * @return - webdriver instance
     */
    public WebDriver getDriver();

    /**
     * Return current Winium instance
     * @return - winium instance
     */
    public WiniumDriver getWiniumDriver();

    /**
     * Return current platform
     * @return - current platform
     */
    public String getPlatform();

    /**
     * Return current Sikulix Screen instance
     * @return - current Screen instance
     */
    public Screen getScreen();

    /**
     * Return true if mobile instance running
     * @return - true if mobile instance
     */
    public boolean isMobile();

    /**
     * Set csv file with element repository to given one
     * @param csvFile - csv file with new element repository
     */
    public void setCsvFile(String csvFile);

    /**
     * Switch element repository to default element repository
     */
    public void setDefaultElementRepository();

    /**
     * Method for loading properties.xml from testInputs
     * @return - loaded Properties
     */
    public Properties getXMLProperties();

    /**
     * Get WebElement element from elementRepository
     * @param elementName - given element
     * @return - WebElement from elementRepository
     * @throws NoSuchElementException - in case that element can't be find
     */
    public WebElement getElement(String elementName) throws NoSuchElementException;

    /**
     * Get WebElement elements from elementRepository
     * @param elementName - given element
     * @return - WebElements from elementRepository
     * @throws NoSuchElementException - in case that element can't be find
     */
    public List<WebElement> getElements(String elementName) throws NoSuchElementException;

    /**
     * Get Sikulix element from Sikulix elementRepository
     * @param elementName - given element
     * @return - element from Sikulix elementRepository
     * @throws NoSuchElementException - in case that element can't be find
     */
    public Region getSikuliElement(String elementName) throws NoSuchElementException;

    /**
     * Return path of Sikulix element
     * @param elementName - given element
     * @return - path of given element
     */
    public String getSikuliElementPath(String elementName);

    /**
     * Return xpath from elementRepository for given element
     * @param elementName - given element
     * @return - xpath of given element
     */
    public String getElementXpath(String elementName);

    /**
     * Return id from elementRepository for given element
     * @param elementName - given element
     * @return - id of given element
     */
    public String getElementID(String elementName);

    /**
     * Return name from elementRepository for given element
     * @param elementName - given element
     * @return - name of given element
     */
    public String getElementName(String elementName);

    /**
     * Get parametr set for element repository parametrization
     * @return - current parameter
     */
    public String[] getElementParameter();

    /**
     * Set parameter for element repository parametrization
     * @param elementParameters - given parameter
     */
    public void setElementParameter(String... elementParameters);

    /**
     * Set Sikuli application
     * @param app - app
     */
    public void setSikuliApp (App app);

    /**
     * Get Sikuli application
     * @return - sikuliApp
     */
    public App getSikuliApp();

    /**
     * Get sikuliRepoPath
     * @return - repo path
     */
    public String getSikuliRepoPath();

}
