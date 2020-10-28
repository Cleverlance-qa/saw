package corehelpers.helpers;

import corehelpers.DataReader;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.ui.WebDriverWait;
import winium.elements.desktop.ComboBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helpers/keywords for Winium tests
 */
public class MethodHelperWinium extends MethodHelper {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected Robot robotKeyboard;

    /**
     * Inicialized class for given DataReader instance.
     * @param dataReader - DataReader instance
     */
    public MethodHelperWinium(DataReader dataReader) {
        super(dataReader);
        initRobotKeyboard();
    }

    /**
     * Set Wait instance
     */
    @Override
    public void setWait() {
        wait = new WebDriverWait(dataReader.getWiniumDriver(), 60);
    }

    /**
     * Set tested platform
     */
    @Override
    public void setPlatform() {
        platform = dataReader.getPlatform();
    }

    /**
     * Init robot keyboard instance
     */
    private void initRobotKeyboard() {
        try {
            robotKeyboard = new Robot();
        } catch (AWTException e) {
            LOGGER.log(Level.SEVERE, "Error, can't init robot keyboard", e);
        }
    }

    /**
     * Take a screenshot and save it to the Screenshots folder in format "testRunnerName_nameFromParam"
     * @param name - name for screenshot
     */
    @Override
    public void takeScreenshot(String name) {
        try {
            FileUtils.copyFile(getWiniumDriver().getScreenshotAs(OutputType.FILE), new File( SCREENSHOTS_FOLDER + super.getClass().getSimpleName() + UNDERSCORE + name + SUFFIX_PNG));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when taking screenshot", e);
        }
    }

    /**
     * Get current robot keyboard instance
     * @return - robot keyboard instance
     */
    public Robot getRobotKeyboard() {
        return robotKeyboard;
    }

    /**
     * Select name value from combobox
     * @param comboboxXpath - XPath co combobox
     * @param valueName - name value to chose
     */
    public void selectFromComboboxByXpathAndName(String comboboxXpath, String valueName) {
        new ComboBox(getWiniumDriver().findElement(By.xpath(dataReader.getElementXpath(comboboxXpath)))).expand();
        getWiniumDriver().findElement(By.xpath(dataReader.getElementXpath(comboboxXpath) + "//*[@Name='"+ valueName + "']")).click();
    }
}