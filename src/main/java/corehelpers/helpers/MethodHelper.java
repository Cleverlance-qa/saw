package corehelpers.helpers;

import com.testautomationguru.utility.PDFUtil;
import confighelpers.PropertiesReader;
import corehelpers.DataReader;
import corehelpers.crypting.DecryptorImpl;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.io.FileUtils;
import org.apache.http.annotation.Obsolete;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.winium.WiniumDriver;
import org.sikuli.script.Screen;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

/**
 * Base class for all helpers/keywords.
 */
public abstract class MethodHelper extends PropertiesReader {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected static final String SUFFIX_PNG = ".png";
    protected static final String SUFFIX_CSV = ".csv";
    protected static final String OUTPUT_FOLDER = "TestOutputs/";
    protected static final String CONSOLELOGS_FOLDER = "ConsoleLogs/";
    protected static final String UNDERSCORE = "_";
    protected static final String SCREENSHOTS_FOLDER = "TestOutputs/Screenshots/";
    protected WebDriverWait wait;
    protected String platform;
    protected Random random;

    /**
     * Inicialized class for given DataReader instance.
     * @param dataReader - DataReader instance
     */
    public MethodHelper(DataReader dataReader) {
        super(dataReader);
        setPlatform();
        if(dataReader.getPlatform()!="sikuli") {
            setWait();
        }
    }

    /**
     * Set Wait instance
     */
    public abstract void setWait();

    /**
     * Set tested platform
     */
    public abstract void setPlatform();

    /**
     * Take a screenshot and save it to the Screenshots folder in format "testRunnerName_nameFromParam"
     * @param name - name for screenshot
     */
    public abstract void takeScreenshot(String name);

    /**
     * Set which DataReader instance should be used
     * @param dataReader - DataReader instance
     */
    public void setDataReader(DataReader dataReader) {
        this.dataReader = dataReader;
    }

    /**
     * Get curently inicialized webdriver
     * @return - curently inicialized webdriver
     */
    public WebDriver getDesktopDriver() {
        return dataReader.getDriver();
    }

    /**
     * Get curently inicialized appiumdriver
     * @return - curently inicialized appiumdriver
     */
    public AppiumDriver getMobileDriver() {
        return dataReader.getAppDriver();
    }

    /**
     * Get curently inicialized winiumdriver
     * @return - curently inicialized winiumdriver
     */
    public WiniumDriver getWiniumDriver() {
        return dataReader.getWiniumDriver();
    }

    /**
     * Get curently inicialized Sikuli screen
     * @return - curently inicialized winiumdriver
     */
    public Screen getSikuliScreen() {
        return dataReader.getScreen();
    }

    /**
     * Set BaseURL from properties file
     * @param baseURLFromProperties - key value from properties file
     */
    public void setBaseURL(String baseURLFromProperties) {
        this.baseURL = properties.getProperty(baseURLFromProperties);
        logMessageToConsole("baseURL set to: " + properties.getProperty(baseURLFromProperties));
    }

    /**
     * Get text of given element
     * @param elementName - Name of element from repository
     * @return - text of given webelement
     */
    public String getText(String elementName) {
        return dataReader.getElement(elementName).getText();
    }

    /**
     * Get text of given element
     * @param elementName - Name of element from repository
     * @return - text of given webelement
     */
    public String getValue(String elementName) {
        return dataReader.getElement(elementName).getAttribute("value");
    }

    /**
     * Assert value of given element and given string. Value should be @value from textfield or similar element.
     * @param elementName - Name of element from repository
     * @param value - value to assert
     */
    public void assertValue(String elementName, String value) {
        Assert.assertEquals(getValue(elementName), value);
        logMessageToConsole("Assertion of " + elementName + " and " + value + " OK");
    }

    /**
     * Assert text value of given element and given string.
     * @param elementName - Name of element from repository
     * @param text - text to assert
     */
    public void assertText(String elementName, String text) {
        Assert.assertEquals(getText(elementName), text);
        logMessageToConsole("Text assertion of " + elementName + " and " + text + " OK");
    }

    /**
     * Assert two text(String) values
     * @param textOne - first string
     * @param textTwo - second string
     */
    public void assertPureText(String textOne, String textTwo) {
        Assert.assertEquals(textOne, textTwo);
        logMessageToConsole("Text assertion of " + textOne + " and " + textTwo + " OK");
    }

    /**
     * Assert two text(String) values for not equals
     * @param textOne - first string
     * @param textTwo - second string
     */
    public void assertPureTextNotEquals(String textOne, String textTwo) {
        Assert.assertNotEquals(textOne, textTwo);
        logMessageToConsole("Negative text assertion of " + textOne + " and " + textTwo + " OK");
    }

    /**
     * Assert that String contains given String
     * @param textOne - main String
     * @param contains - contains String
     */
    public void assertPureTextContains(String textOne, String contains) {
        Assert.assertTrue(textOne.contains(contains));
    }

    /**
     * Assert that String not contains given String
     * @param textOne - main String
     * @param contains - contains String
     */
    public void assertPureTextNotContains(String textOne, String contains) {
        Assert.assertFalse(textOne.contains(contains));
    }

    /**
     * Click on given element
     * @param elementName - Name of element from repository
     */
    public void clickOnElement(String elementName) {
        dataReader.getElement(elementName).click();
    }

    /**
     * Method check if the given element is enabled and return boolean value.
     * @param elementName - Name of element from repository
     * @return - true if given element is enabled
     */
    public boolean verifyElementEnabled(String elementName) {
        return dataReader.getElement(elementName).isEnabled();
    }

    /**
     * Method check if the given element exist and return boolean value.
     * @param elementName - Name of element from repository
     * @return - true if given element exist
     */
    public boolean verifyElementExist(String elementName) {
        boolean exist = false;
        try {
            dataReader.getElement(elementName);
            exist = true;
            logMessageToConsole(String.format("%s exist", elementName));
        } catch (NoSuchElementException e) {
            LOGGER.log(Level.INFO, "Element " + elementName + " not exists");
        }
        return exist;
    }

    /**
     * Method check if the given element exist and return boolean value. If false, it will throw exception.
     * Obsolete - use assertElementExistWithException
     * @param elementName - Name of element from repository
     * @return - true if given element exist, NoSuchElementException if false
     */
    @Obsolete
    public boolean verifyElementExistWithException(String elementName) {
        dataReader.getElement(elementName);
        logMessageToConsole(String.format("%s exist", elementName));
        return true;
    }

    /**
     * Method check if the given element exist and throw exception when not.
     * @param elementName - Name of element from repository
     */
    public void assertElementExist(String elementName) {
        dataReader.getElement(elementName);
        logMessageToConsole(String.format("%s exist", elementName));
    }

    /**
     * Method check if the given element not exist and return boolean value. If false, it will throw exception.
     * Obsolete - use assertElementExistWithException
     * @param elementName - Name of element from repository
     * @return - true if given element not exist, NoSuchElementException if false
     */
    @Obsolete
    public boolean verifyElementNotExistWithException(String elementName) {
        if(verifyElementExist(elementName)) {
            logMessageToConsole(String.format("%s exist", elementName));
            fail(String.format("Element %s exist", elementName));
        }
        logMessageToConsole(String.format("%s not exist", elementName));
        return true;
    }

    /**
     * Method check if the given element not exist and throw exception when exists.
     * @param elementName - Name of element from repository
     */
    public void assertElementNotExist(String elementName) {
        if(verifyElementExist(elementName)) {
            logMessageToConsole(String.format("%s exist", elementName));
            fail(String.format("Element %s exist", elementName));
        }
        logMessageToConsole(String.format("%s not exist", elementName));
    }

    /**
     * Wait for element until they exist on page
     * @param elementName - Name of element from repository
     */
    public MethodHelper waitForElementExist(String elementName) {
        tryElementExist(elementName, 30);
        return this;
    }

    /**
     * Wait for element until they exist on page
     * @param elementName - Name of element from repository
     * @param secondsToWait - time to wait in seconds
     */
    public MethodHelper waitForElementExist(String elementName, int secondsToWait) {
        tryElementExist(elementName, secondsToWait);
        return this;
    }

    private void tryElementExist(String elementName, int seconds) {
        for(int i = 0;i<seconds;i++) {
            try {
                logMessageToConsole(String.format("Waiting for %s for %s seconds", elementName, i));
                if (verifyElementExist(elementName)) {
                    return;
                }
            } catch (WebDriverException e) {
                LOGGER.log(Level.FINE, String.format("%s still not exist", elementName));
            }
            dialogPause();
        }
        throw new TimeoutException(String.format("Element %s not exist!", elementName));
    }

    /**
     * Wait for element until they not exist on page
     * @param elementName - Name of element from repository
     */
    public MethodHelper waitForElementNotExist(String elementName) {
        tryElementNotExist(elementName, 30);
        return this;
    }

    /**
     * Wait for element until they not exist on page
     * @param elementName - Name of element from repository
     * @param secondsToWait - time to wait in seconds
     */
    public MethodHelper waitForElementNotExist(String elementName, int secondsToWait) {
        tryElementNotExist(elementName, secondsToWait);
        return this;
    }

    private void tryElementNotExist(String elementName, int seconds) {
        for(int i = 0;i<seconds;i++) {
            try {
                logMessageToConsole("Waiting for not existing " + elementName + "for " + i + " seconds");
                if (verifyElementExist(elementName)) {
                    dialogPause();
                } else {
                    logMessageToConsole(elementName + " not exist anymore");
                    return;
                }
            } catch (WebDriverException e) {
                LOGGER.log(Level.FINE, elementName + " not exist anymore");
                return;
            }
        }
        throw new TimeoutException("Element " + elementName + " still exist!");
    }

    /**
     * Method write text into given element. Element should be a input field.
     * @param elementName - Name of element from repository
     * @param keys - text which should be written
     */
    public void sendKeys(String elementName, String keys) {
        dataReader.getElement(elementName).sendKeys(keys);
    }

    /**
     * Method simulates keyboard click. Should be targeted to some element.
     * @param elementName - targeted element
     * @param charSequence - Char to click
     */
    public void sendKeys(String elementName, CharSequence charSequence) {
        dataReader.getElement(elementName).sendKeys(charSequence);
    }

    /**
     * Method clear given element's value in case that the element is a input field.
     * @param elementName - Name of element from repository
     */
    public void clearField(String elementName) {
        dataReader.getElement(elementName).clear();
    }

    /**
     * Method will wait until element is present in browser. Implicit wait time before throwing exception should be 10 seconds.
     * @param elementName - Name of element from repository
     */
    @Deprecated
    public void waitForPresenceOfElementByXpath(String elementName) {
        logMessageToConsole("Waiting for " + elementName);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(dataReader.getElementXpath(elementName))));
    }

    /**
     * Method will wait until element is present in browser. Implicit wait time before throwing exception should be 10 seconds.
     * @param elementName - Name of element from repository
     */
    @Deprecated
    public void waitForPresenceOfElementByName(String elementName) {
        logMessageToConsole("Waiting for " + elementName);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name(dataReader.getElementName(elementName))));
    }

    /**
     * Method will wait until element is present in browser. Implicit wait time before throwing exception should be 10 seconds.
     * @param elementName - Name of element from repository
     */
    @Deprecated
    public void waitForPresenceOfElementByID(String elementName) {
        logMessageToConsole("Waiting for " + elementName);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(dataReader.getElementID(elementName))));
    }

    /**
     * Select value from dropdown by value
     * @param elementName - Name of element from repository
     * @param valueText - Chosen value
     */
    public void selectFromDropdown(String elementName, String valueText) {
        final Select dropdown = new Select(dataReader.getElement(elementName));
        try {
            logMessageToConsole("Trying to set dropdown value by text");
            dropdown.selectByVisibleText(valueText);
        } catch (WebDriverException e) {
            LOGGER.log(Level.INFO, "Text value for dropdown not found");
            logMessageToConsole("Trying to set dropdown value by value");
            dropdown.selectByValue(valueText);
        }
    }

    /**
     * Select value from dropdown by index
     * @param elementName - Name of element from repository
     * @param index - Chozen index
     */
    public void selectFromDropdown(String elementName, int index) {
        final Select dropdown = new Select(dataReader.getElement(elementName));
        dropdown.selectByIndex(index);
    }

    /**
     * Generate random number starts with 732
     * @return - random phone number in format 732xxxxxx
     */
    public int generatePhoneNumber (){
        random = new Random();
        final int value = 732000000 + random.nextInt(900000);
        logMessageToConsole("Phone number: " + value);
        return value;
    }

    /**
     * Generate random delay betwen 1-10 seconds
     */
    public void randomDelay(){
        random = new Random();
        final int randomDelay = 1000 + random.nextInt(9000);
        try {
            Thread.sleep(randomDelay);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error during Thread.sleep in randomDelay", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Generate random character from a to z
     * @return - random character
     */
    public String generateRandomChar() {
        Random r = new Random();
        char c = (char)(r.nextInt(26) + 'a');
        return String.valueOf(c);
    }

    /**
     * Wait for one second
     */
    public void dialogPause(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error during Thread.sleep in dialogPause", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Run javascript
     * @param script - javascript to run
     */
    public void runJavascript(String script){
        if(dataReader.isMobile()) {
            getMobileDriver().executeScript(script);
        } else {
            ((JavascriptExecutor) getDesktopDriver()).executeScript(script);
        }
    }

    /**
     * Check if checkbox is in right state
     * @param elementName - Name of element from repository, must be an input element
     * @param expectedState - true or false
     */
    public void assertCheckboxState(String elementName, boolean expectedState){
        Assert.assertEquals(expectedState, dataReader.getElement(elementName).isSelected());
        logMessageToConsole("Checkbox assertion ok. " + elementName + " is " + expectedState);
    }

    /**
     * Log message to java console
     * @param message - message to log
     */
    public void logMessageToConsole(String message){
        LOGGER.log(Level.INFO, message);
    }

    /**
     * Set element repository by name
     * @param repositoryName - name of new repository
     */
    public void setElementRepository(String repositoryName) {
        dataReader.setCsvFile(repositoryName);
        logMessageToConsole("Element repository set to " + repositoryName);
    }

    /**
     * Set default element repository from properties
     */
    public void setDefaultElementRepository() {
        dataReader.setDefaultElementRepository();
    }

    /**
     * Will wait for given amount of time in milliseconds
     * @param timeInMillis - time to wait
     */
    public void waitSomeTime(int timeInMillis) {
        logMessageToConsole("Will wait for " + timeInMillis + " milliseconds");
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error during Thread.sleep in waitSomeTime",e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Return current Date in Time in given format
     * @param dateTimeFormat - DateTime format
     * @return Date and Time in given format
     */
    public String getCurrentDateTime(String dateTimeFormat){
        final LocalDateTime date = LocalDateTime.now();
        return DateTimeFormatter.ofPattern(dateTimeFormat, Locale.UK).format(date);
    }

    /**
     * Return current Date in Time in given format
     * @param dateTimeFormat - DateTime format
     * @param addDays - Days to add
     * @return Date and Time in given format
     */
    public String getCurrentDateTime(String dateTimeFormat, int addDays){
        final LocalDateTime date = LocalDateTime.now().plusDays(addDays);
        return DateTimeFormatter.ofPattern(dateTimeFormat, Locale.UK).format(date);
    }

    /**
     * Set the parameters for element from element repository
     * @param parameters - Values to set
     */
    public void setElementParameter(String... parameters) {
        dataReader.setElementParameter(parameters);
    }

    /**
     * Get current value for parameter for elements from element repository
     * @return - current value
     */
    public String[] getElementParameter() {
        return dataReader.getElementParameter();
    }

    /**
     * Will compare content of two files
     * @param urlToFileOne - path to first file
     * @param urlToFileTwo - path to second file
     * @return - true if the same content
     */
    public Boolean compareFiles(String urlToFileOne, String urlToFileTwo) {
        boolean areEquals = false;
        try {
            checkFileExists(urlToFileOne);
            checkFileExists(urlToFileTwo);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File not found!", e);
        }
        final File file1 = new File(urlToFileOne);
        final File file2 = new File(urlToFileTwo);
        try {
            areEquals = FileUtils.contentEquals(file1, file2);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when comparing file", e);
        }
        return areEquals;
    }

    /**
     * Return all elements found by element search
     * @param elementName - Name of element from repository
     * @return - all found elements
     */
    public List<WebElement> getMultipleElements(String elementName) {
        return dataReader.getElements(elementName);
    }

    /**
     * Get number of all elements found by element search
     * @param elementName - Name of element from repository
     * @return - number of elements
     */
    public int getElementsCount(String elementName) {
        return dataReader.getElements(elementName).size();
    }

    /**
     * Will compare text content of two pdf files
     * @param urlToFileOne - path to first file
     * @param urlToFileTwo - path to second file
     * @return - true if the same text content
     */
    public boolean comparePDFTextContent(String urlToFileOne, String urlToFileTwo) {
        boolean areEquals = false;
        try {
            checkFileExists(urlToFileOne);
            checkFileExists(urlToFileTwo);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File not found!", e);
        }
        try {
            areEquals = new PDFUtil().compare(urlToFileOne, urlToFileTwo);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when comparing file", e);
        }
        return areEquals;
    }

    /**
     * Will compare text content of two pdf files
     * @param urlToFileOne - path to first file
     * @param urlToFileTwo - path to second file
     * @param startPage - start page to compare
     * @param endPage - end page to compare
     * @return - true if the same text content
     */
    public boolean comparePDFTextContent(String urlToFileOne, String urlToFileTwo, int startPage, int endPage) {
        boolean areEquals = false;
        try {
            checkFileExists(urlToFileOne);
            checkFileExists(urlToFileTwo);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File not found!", e);
        }
        try {
            areEquals = new PDFUtil().compare(urlToFileOne, urlToFileTwo, startPage, endPage);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when comparing file", e);
        }
        return areEquals;
    }

    /**
     * Will compare text content of two pdf files
     * @param urlToFileOne - path to first file
     * @param urlToFileTwo - path to second file
     * @param regexsToExclude - regexes which should be excluded from comparing
     * @return - true if the same text content
     */
    public boolean comparePDFTextContent(String urlToFileOne, String urlToFileTwo, String... regexsToExclude) {
        boolean areEquals = false;
        try {
            checkFileExists(urlToFileOne);
            checkFileExists(urlToFileTwo);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File not found!", e);
        }
        try {
            final PDFUtil pdfUtil = new PDFUtil();
            pdfUtil.excludeText(regexsToExclude);
            areEquals = pdfUtil.compare(urlToFileOne, urlToFileTwo);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when comparing file", e);
        }
        return areEquals;
    }

    /**
     * Will compare text content of two pdf files
     * @param urlToFileOne - path to first file
     * @param urlToFileTwo - path to second file
     * @param startPage - start page to compare
     * @param endPage - end page to compare
     * @param regexsToExclude - regexes which should be excluded from comparing
     * @return - true if the same text content
     */
    public boolean comparePDFTextContent(String urlToFileOne, String urlToFileTwo, int startPage, int endPage, String... regexsToExclude) {
        boolean areEquals = false;
        try {
            checkFileExists(urlToFileOne);
            checkFileExists(urlToFileTwo);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File not found!", e);
        }
        try {
            final PDFUtil pdfUtil = new PDFUtil();
            pdfUtil.excludeText(regexsToExclude);
            areEquals = pdfUtil.compare(urlToFileOne, urlToFileTwo, startPage, endPage);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when comparing file", e);
        }
        return areEquals;
    }

    /**
     * Get text content from PDF file
     * @param urlToFile - path to pdf file
     * @return - text from PDF file as a String
     */
    public String getPDFText(String urlToFile) {
        String minedText = "";
        try {
            checkFileExists(urlToFile);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File not found!", e);
        }
        try {
            minedText = new PDFUtil().getText(urlToFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when reading file", e);
        }
        return minedText;
    }

    /**
     * Get text content from PDF file
     * @param urlToFile - path to pdf file
     * @param startPage - start page
     * @param endPage - end page
     * @return - text from PDF file as a String
     */
    public String getPDFText(String urlToFile, int startPage, int endPage) {
        String minedText = "";
        try {
            checkFileExists(urlToFile);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File not found!", e);
        }
        try {
            minedText = new PDFUtil().getText(urlToFile, startPage, endPage);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when reading file", e);
        }
        return minedText;
    }

    private void checkFileExists(String urlToFile) throws FileNotFoundException {
        final File tempFile = new File(urlToFile);
        if(!tempFile.exists()) {
            throw new FileNotFoundException();
        }
    }

    /**
     * Get attribute value from given element
     * @param elementName - Name of element from repository
     * @param attributeName - Attribute name
     * @return - Value from given attribute
     */
    public String getAttributValue(String elementName, String attributeName) {
        return dataReader.getElement(elementName).getAttribute(attributeName);
    }

    /**
     * Set custom value for specific field in grafana report. Field must be predefined in grafana_setting file.
     * @param fieldName - field name
     * @param value - new value
     */
    public void setGrafanaExportValue(String fieldName, String value) {
        dataReader.addValueToGrafanaCustomFieldMap(fieldName, value);
        LOGGER.log(Level.INFO, "Value for field " + fieldName + " in grafana export is set to " + value);
    }

    /**
     * Set custom value for specific tag in grafana report. Tag must be predefined in grafana_setting file.
     * @param fieldName - field name
     * @param value - new value
     */
    public void setGrafanaTagValue(String fieldName, String value) {
        dataReader.addValueToGrafanaCustomTagMap(fieldName, value);
        LOGGER.log(Level.INFO, "Value for tag " + fieldName + " in grafana export is set to " + value);
    }

    /**
     * Wait until element's value equals expected value
     * @param elementName - Name of element from repository
     * @param value - expected value
     * @return - this instance
     */
    public MethodHelper waitForElementValue(String elementName, String value) {
        tryElementValue(elementName, value, 30);
        return this;
    }

    /**
     * Wait until element's value equals expected value
     * @param elementName - Name of element from repository
     * @param value - expected value
     * @param secondsToWait - time to wait in seconds
     * @return
     */
    public MethodHelper waitForElementValue(String elementName, String value, int secondsToWait) {
        tryElementValue(elementName, value, secondsToWait);
        return this;
    }

    private void tryElementValue(String elementName, String value, int seconds) {
        for(int i = 0;i<seconds;i++) {
            try {
                logMessageToConsole(String.format("Waiting for value %s for %s seconds", value, i));
                if (value.equals(getValue(elementName))) {
                    LOGGER.log(Level.FINE, String.format("%s have %s value", elementName,value));
                    return;
                }
            } catch (WebDriverException e) {
                LOGGER.log(Level.FINE, String.format("%s still not have %s value", elementName,value));
            }
            dialogPause();
        }
        throw new TimeoutException(String.format("Element %s have not %s value!", elementName, value));
    }

    /**
     * Wait until element's value equals expected value
     * @param elementName - Name of element from repository
     * @param text - expected value
     * @return - this instance
     */
    public MethodHelper waitForElementText(String elementName, String text) {
        tryElementText(elementName, text, 30);
        return this;
    }

    /**
     * Wait until element's text equals expected text
     * @param elementName - Name of element from repository
     * @param text - expected text
     * @param secondsToWait - time to wait in seconds
     * @return
     */
    public MethodHelper waitForElementText(String elementName, String text, int secondsToWait) {
        tryElementText(elementName, text, secondsToWait);
        return this;
    }

    private void tryElementText(String elementName, String text, int seconds) {
        for(int i = 0;i<seconds;i++) {
            try {
                logMessageToConsole(String.format("Waiting for text %s for %s seconds", text, i));
                if (text.equals(getText(elementName))) {
                    LOGGER.log(Level.FINE, String.format("%s have %s text", elementName,text));
                    return;
                }
            } catch (WebDriverException e) {
                LOGGER.log(Level.FINE, String.format("%s still not have %s text", elementName,text));
            }
            dialogPause();
        }
        throw new TimeoutException(String.format("Element %s have not %s text!", elementName, text));
    }

    /**
     * Wait until element's attribute value equals expected value
     * @param elementName - Name of element from repository
     * @param attribute - element's attribute
     * @param value - expected value
     * @return - this instance
     */
    public MethodHelper waitForElementAttributeValue(String elementName, String attribute, String value) {
        tryElementAttributeValue(elementName, attribute,  value, 30);
        return this;
    }

    /**
     * Wait until element's attribute value equals expected value
     * @param elementName - Name of element from repository
     * @param attribute - element's attribute
     * @param value - expected value
     * @param secondsToWait - time to wait in seconds
     * @return
     */
    public MethodHelper waitForElementAttributeValue(String elementName, String attribute, String value, int secondsToWait) {
        tryElementAttributeValue(elementName, attribute, value, secondsToWait);
        return this;
    }

    private void tryElementAttributeValue(String elementName, String attribute, String value, int seconds) {
        for(int i = 0;i<seconds;i++) {
            try {
                logMessageToConsole(String.format("Waiting for attribute value %s for %s seconds", value, i));
                if (value.equals(getAttributValue(elementName, attribute))) {
                    LOGGER.log(Level.FINE, String.format("%s have %s attribute value", elementName,value));
                    return;
                }
            } catch (WebDriverException e) {
                LOGGER.log(Level.FINE, String.format("%s still not have %s attribute value", elementName,value));
            }
            dialogPause();
        }
        throw new TimeoutException(String.format("Element %s have not %s attribute value!", elementName, value));
    }

    /**
     * Get a property value from loaded properties
     * @param property - property name
     * @return - property value
     */
    public String getProperty(String property) {
        return dataReader.getXMLProperties().getProperty(property);
    }

    /**
     * Generate MODULO11 number in given interval
     * @param min - minimum number
     * @param max - maximum number
     * @return - generated MODULO11. In case of wront input, return 0.
     */
    public String generateMod11(long min, long max) {
        if (min<11 || min>max) {
            LOGGER.log(Level.WARNING, "Min number must be at least 11 and must be smaller than Max number");
            return "0";
        }
        long generatedMod11;
        for(int i = 1; i<200; i++) {
            generatedMod11 = (min + (long) (Math.random() * (max - min)));
            if(generatedMod11 % 11 == 0) {
                logMessageToConsole("Generated mod11 number: " + generatedMod11);
                return Long.toString(generatedMod11);
            }
        }
        LOGGER.log(Level.WARNING, "Unable to make MODULO11 from given numbers");
        return "0";
    }

    /**
     * Decrytpt encrypted text with Jasypt
     * @param text - given encrypted text
     * @return - decrypted text
     */
    public String decryptText(String text) {
        return new DecryptorImpl().decryptString(text);
    }

    /**
     * Parse regex from String
     * @param inputText - input String
     * @param regex - input regex
     * @return - parsed regex
     */
    public String parseRegex(String inputText, String regex) {
        String output = "";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(inputText);
        if(matcher.find()) {
            output = matcher.group();
            LOGGER.log(Level.INFO, "Regex output: " + output);
        } else {
            LOGGER.log(Level.WARNING, "No regex match found!");
        }
        return output;
    }

    /**
     * Parse XPath from String. String must be a valid XML
     * @param inputXML - String with valid XML structure
     * @param xpath - valid XPath
     * @return - parsed text
     */
    public String parseXPath(String inputXML, String xpath) {
        String output = "";
        try {
            LOGGER.log(Level.INFO, "Will parse XPath: " + xpath);
            final InputSource inputSource = new InputSource( new StringReader( inputXML ) );
            final XPath xPath = XPathFactory.newInstance().newXPath();
            output = xPath.evaluate(xpath, inputSource);
        } catch (XPathExpressionException e) {
            LOGGER.log(Level.SEVERE, "Error when parsing XPath. Wrong input XML or XPath expression", e);
        }
        return output;
    }

    /**
     * Get text from given file and return it as String
     * @param pathToFile - path to input file
     * @return - text from input file as String
     */
    public String getTextFromFile(String pathToFile) {
        String line;
        final StringBuilder stringBuilder = new StringBuilder();
        final String ls = System.getProperty("line.separator");
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pathToFile), "UTF-8"))) {
            while((line = br.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can't read input file!", e);
        }
        return stringBuilder.toString();
    }

    /**
     * Run curl command and return results
     * @param command - curl command
     * @return - results
     */
    public String runCurlCommand(String[] command) {
        logMessageToConsole("Will run curl command");
        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);

        String curlResult = "";
        String line = "";

        try {
            final Process process = builder.start();
            final BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                logMessageToConsole(line);
                curlResult = curlResult + line;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when running curl command", e);
        }
        return curlResult;
    }

    /**
     * Will download file from given URL to TestOutputs as given fileName
     * @param url - file adress
     * @param fileName - downloaded file name
     */
    public void downloadFile(String url, String fileName) {
        logMessageToConsole("Will download file: " + url + "\ninto TestOutputs as: " + fileName);
        String[] command = {"curl", url, "--output", System.getProperty("user.dir") + "/TestOutputs/" + fileName};
        runCurlCommand(command);
    }

    /**
     * Will download file using proxy from given URL to TestOutputs as given fileName.
     * @param url - file adress
     * @param fileName - downloaded file name
     * @param proxyAddress - proxy address
     * @param proxyPort - proxy port
     * @param proxyLogin - proxy login name
     * @param proxyPassword - proxy login password
     */
    public void downloadFile(String url, String fileName, String proxyAddress, String proxyPort, String proxyLogin, String proxyPassword) {
        logMessageToConsole("Will download file: " + url + "\ninto TestOutputs as: " + fileName);
        String[] command = {"curl", "--proxy-ntlm", "--proxy", proxyAddress + ":" + proxyPort,
                "--proxy-user", proxyLogin + ":" + proxyPassword,
                url, "--output", System.getProperty("user.dir") + "/TestOutputs/" + fileName};
        runCurlCommand(command);
    }

    /**
     * Check if checkbox is checked and return boolean value
     * @param elementName - Name of element from repository, must be an input element
     * @return - checkbox state. True for checked.
     */
    public boolean checkCheckboxState(String elementName) {
        return dataReader.getElement(elementName).isSelected();
    }
}