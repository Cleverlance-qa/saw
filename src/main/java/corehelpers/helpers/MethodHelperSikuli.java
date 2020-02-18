package corehelpers.helpers;

import corehelpers.DataReader;
import corehelpers.injections.WiniumInject;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Word;
import net.sourceforge.tess4j.util.ImageHelper;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.TimeoutException;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Region;
import org.sikuli.script.TextRecognizer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Helpers/keywords for Sikuli tests
 */
public class MethodHelperSikuli extends MethodHelper {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String ERROR_TARGET_ELEMENT = "Nenalezen target element!";
    private static final String PNG = "PNG";
    private static final String JPG = "jpg";
    private static final String SUFFIX_JPG = ".jpg";
    private boolean savePictures = false;
    private boolean convertToGrayscale = true;
    private double multiplier = 2;
    protected WiniumInject winium;

    /**
     * Inicialized class for given DataReader instance.
     * @param dataReader - DataReader instance
     */
    public MethodHelperSikuli(DataReader dataReader) {
        super(dataReader);
    }

    /**
     * Set Wait instance - not working for Sikulix
     */
    @Override
    public void setWait() {
        logMessageToConsole("setWait function is not supported for Sikuli!");
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
            FileUtils.copyFile(new File(dataReader.getScreen().capture(dataReader.getScreen().getBounds()).getFile()), new File( SCREENSHOTS_FOLDER + super.getClass().getSimpleName() + UNDERSCORE + name + SUFFIX_PNG));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when taking screenshot", e);
        }
    }

    /**
     * Click on given element
     * @param elementName - Name of element from repository
     */
    @Override
    public void clickOnElement(String elementName) {
        dataReader.getSikuliElement(elementName).click();
    }

    /**
     * Click on target element right from first element
     * @param elementNameFrom - Name of element from repository
     * @param targetElementName - Name of second element from repository
     */
    public void clickOnElementRight(String elementNameFrom, String targetElementName) {
        final Region region = dataReader.getSikuliElement(elementNameFrom);
        try {
            region.right().find(dataReader.getSikuliElementPath(targetElementName)).click();
        } catch (FindFailed findFailed) {
            LOGGER.log(Level.SEVERE, ERROR_TARGET_ELEMENT, findFailed);
        }
    }

    /**
     * Click on target element left from first element
     * @param elementNameFrom - Name of element from repository
     * @param targetElementName - Name of second element from repository
     */
    public void clickOnElementLeft(String elementNameFrom, String targetElementName) {
        final Region region = dataReader.getSikuliElement(elementNameFrom);
        try {
            region.left().find(dataReader.getSikuliElementPath(targetElementName)).click();
        } catch (FindFailed findFailed) {
            LOGGER.log(Level.SEVERE, ERROR_TARGET_ELEMENT, findFailed);
        }
    }

    /**
     * Click left from element in given distance
     * @param elementNameFrom - Name of element from repository
     * @param distance - distance
     */
    public void clickOnElementLeft(String elementNameFrom, int distance) {
        dataReader.getSikuliElement(elementNameFrom).left(distance).click();
    }

    /**
     * Click on target element below from first element
     * @param elementNameFrom - Name of element from repository
     * @param targetElementName - Name of second element from repository
     */
    public void clickOnElementBelow(String elementNameFrom, String targetElementName) {
        final Region region = dataReader.getSikuliElement(elementNameFrom);
        try {
            region.below().find(dataReader.getSikuliElementPath(targetElementName)).click();
        } catch (FindFailed findFailed) {
            LOGGER.log(Level.SEVERE, ERROR_TARGET_ELEMENT, findFailed);
        }
    }

    /**
     * Click below element in given distance
     * @param elementNameFrom - Name of element from repository
     * @param distance - distance
     */
    public void clickOnElementBelow(String elementNameFrom, int distance) {
        dataReader.getSikuliElement(elementNameFrom).below(distance).click();
    }

    /**
     * Method check if the given element exist and return boolean value.
     * @param elementName - Name of element from repository
     * @return - true if given element exist
     */
    @Override
    public boolean verifyElementExist(String elementName) {
        boolean exist = false;
        try {
            getSikuliScreen().find(dataReader.getSikuliElementPath(elementName));
            exist = true;
            logMessageToConsole(elementName + " exist");
        } catch (FindFailed findFailed) {
            LOGGER.log(Level.INFO, elementName + " not exist");
        }
        return exist;
    }

    /**
     * Method check if the given element exist below first element and return boolean value.
     * @param elementNameFrom - Name of element from repository
     * @param targetElementName - Name of second element from repository
     * @return - true if given element exist
     */
    public boolean verifyElementExistBelow(String elementNameFrom, String targetElementName) {
        boolean exist = false;
        try {
            final Region region = dataReader.getSikuliElement(elementNameFrom);
            region.below().find(dataReader.getSikuliElementPath(targetElementName));
            exist = true;
            logMessageToConsole(targetElementName + " exist");
        } catch (FindFailed findFailed) {
            LOGGER.log(Level.WARNING, targetElementName + " not exist");
        }
        return exist;
    }

    /**
     * Method check if the given element exist right from first element and return boolean value.
     * @param elementNameFrom - Name of element from repository
     * @param targetElementName - Name of second element from repository
     * @return - true if given element exist
     */
    public boolean verifyElementExistRight(String elementNameFrom, String targetElementName) {
        boolean exist = false;
        try {
            final Region region = dataReader.getSikuliElement(elementNameFrom);
            region.right().find(dataReader.getSikuliElementPath(targetElementName));
            exist = true;
            logMessageToConsole(targetElementName + " exist");
        } catch (FindFailed findFailed) {
            LOGGER.log(Level.WARNING, targetElementName + " not exist");
        }
        return exist;
    }

    /**
     * Right click on given element
     * @param elementName - Name of element from repository
     */
    public void doRightClick(String elementName) {
        dataReader.getSikuliElement(elementName).rightClick();
    }

    /**
     * Wait for element until they exist on page
     * @param elementName - Name of element from repository
     */
    @Override
    public MethodHelper waitForElementExist(String elementName) {
        tryElementExists(elementName, 30);
        return this;
    }

    /**
     * Wait for element until they exist on page
     * @param elementName - Name of element from repository
     * @param secondsToWait - time to wait in seconds
     */
    @Override
    public MethodHelper waitForElementExist(String elementName, int secondsToWait) {
        tryElementExists(elementName, secondsToWait);
        return this;
    }

    private void tryElementExists(String elementName, int seconds) {
        elementName = dataReader.getSikuliElementPath(elementName);
        for(int i=0;i<seconds;i++) {
            try {
                logMessageToConsole("Waiting for " + elementName + " for " + i + " seconds");
                getSikuliScreen().find(elementName);
                logMessageToConsole(elementName + " exist");
                return;
            } catch (FindFailed findFailed) {
                dialogPause();
                LOGGER.log(Level.FINE, elementName + " still not exist");
            }
        }
        throw new TimeoutException("Element " + elementName + " not exist!");
    }

    /**
     * Wait for element until they not exist on page
     * @param elementName - Name of element from repository
     */
    @Override
    public MethodHelper waitForElementNotExist(String elementName) {
        tryElementNotExists(elementName, 30);
        return this;
    }

    /**
     * Wait for element until they not exist on page
     * @param elementName - Name of element from repository
     * @param secondsToWait - time to wait in seconds
     */
    @Override
    public MethodHelper waitForElementNotExist(String elementName, int secondsToWait) {
        tryElementNotExists(elementName, secondsToWait);
        return this;
    }

    private void tryElementNotExists(String elementName, int seconds) {
        elementName = dataReader.getSikuliElementPath(elementName);
        for(int i=0;i<seconds;i++) {
            try {
                logMessageToConsole("Waiting for not existing " + elementName + " for " + i + " seconds");
                getSikuliScreen().find(elementName);
                dialogPause();
            } catch (FindFailed findFailed) {
                LOGGER.log(Level.FINE, elementName + " not exist anymore");
                return;
            }
        }
        throw new TimeoutException("Element " + elementName + " still exist!");
    }

    /**
     * Method write text into given element.
     * @param elementName - Name of element from repository
     * @param keys - text which should be written
     */
    @Override
    public void sendKeys(String elementName, String keys) {
        logMessageToConsole("Typing " + keys + " to " + elementName);
        dataReader.getSikuliElement(elementName).click();
        getSikuliScreen().type(keys);
    }

    /**
     * Method write text below given element.
     * @param elementName - Name of element from repository
     * @param keys - text which should be written
     */
    public void sendKeysBelow(String elementName, String keys) {
        logMessageToConsole("Typing " + keys + " to " + elementName);
        dataReader.getSikuliElement(elementName).below(15).click();
        getSikuliScreen().type(keys);
    }

    /**
     * Method write text below given element.
     * @param elementName - Name of element from repository
     * @param keys - text which should be written
     * @param distance - distance
     */
    public void sendKeysBelow(String elementName, String keys, int distance) {
        logMessageToConsole("Typing " + keys + " to " + elementName);
        dataReader.getSikuliElement(elementName).below(distance).click();
        getSikuliScreen().type(keys);
    }

    /**
     * Method write text to element below first element.
     * @param elementNameFrom - Name of element from repository
     * @param targetElementName - Name of second element from repository
     * @param keys - text which should be written
     */
    public void sendKeysBelow(String elementNameFrom, String targetElementName, String keys) {
        logMessageToConsole("Typing " + keys + " to " + targetElementName);
        final Region region = dataReader.getSikuliElement(elementNameFrom);
        try {
            region.below().find(dataReader.getSikuliElementPath(targetElementName)).type(keys);
        } catch (FindFailed findFailed) {
            LOGGER.log(Level.SEVERE, ERROR_TARGET_ELEMENT, findFailed);
        }
    }

    /**
     * Method write text right from given element.
     * @param elementName - Name of element from repository
     * @param keys - text which should be written
     */
    public void sendKeysRight(String elementName, String keys) {
        logMessageToConsole("Typing " + keys + " to " + elementName);
        dataReader.getSikuliElement(elementName).right(50).click();
        getSikuliScreen().type(keys);
    }

    /**
     * Method write text right from given element.
     * @param elementName - Name of element from repository
     * @param keys - text which should be written
     * @param distance - distance
     */
    public void sendKeysRight(String elementName, String keys, int distance) {
        logMessageToConsole("Typing " + keys + " to " + elementName);
        dataReader.getSikuliElement(elementName).right(distance).click();
        getSikuliScreen().type(keys);
    }

    /**
     * Method write text to element right from first element.
     * @param elementNameFrom - Name of element from repository
     * @param targetElementName - Name of second element from repository
     * @param keys - text which should be written
     */
    public void sendKeysRight(String elementNameFrom, String targetElementName, String keys) {
        logMessageToConsole("Typing " + keys + " to " + targetElementName);
        final Region region = dataReader.getSikuliElement(elementNameFrom);
        try {
            region.right().find(dataReader.getSikuliElementPath(targetElementName)).type(keys);
        } catch (FindFailed findFailed) {
            LOGGER.log(Level.SEVERE, ERROR_TARGET_ELEMENT, findFailed);
        }
    }

    /**
     * Method clear given element's value in case that the element is a input field.
     * @param elementName - Name of element from repository
     */
    @Override
    public void clearField(String elementName) {
        logMessageToConsole("Clearing field " + elementName);
        dataReader.getSikuliElement(elementName).click();
        getSikuliScreen().type("a", Key.CTRL);
        getSikuliScreen().type(Key.BACKSPACE);
    }

    /**
     * Method clear area below element in case that the area is a input field.
     * @param elementName - Name of element from repository
     */
    public void clearFieldBelow(String elementName) {
        logMessageToConsole("Clearing field " + elementName);
        dataReader.getSikuliElement(elementName).below(15).click();
        getSikuliScreen().type("a", Key.CTRL);
        getSikuliScreen().type(Key.BACKSPACE);
    }

    /**
     * Method clear area below element in case that the area is a input field.
     * @param elementName - Name of element from repository
     * @param distance - distance
     */
    public void clearFieldBelow(String elementName, int distance) {
        logMessageToConsole("Clearing field " + elementName);
        dataReader.getSikuliElement(elementName).below(distance).click();
        getSikuliScreen().type("a", Key.CTRL);
        getSikuliScreen().type(Key.BACKSPACE);
    }

    /**
     * Method clear target element below first element in case that the element is a input field.
     * @param elementNameFrom - Name of element from repository
     * @param targetElementName - Name of second element from repository
     */
    public void clearFieldBelow(String elementNameFrom, String targetElementName) {
        logMessageToConsole("Clearing field " + targetElementName);
        final Region region = dataReader.getSikuliElement(elementNameFrom);
        try {
            final Region target = region.below().find(dataReader.getSikuliElementPath(targetElementName));
            target.type("a", Key.CTRL);
            target.type(Key.BACKSPACE);
        } catch (FindFailed findFailed) {
            LOGGER.log(Level.SEVERE, ERROR_TARGET_ELEMENT, findFailed);
        }
    }

    /**
     * Method clear area right from element in case that the area is a input field.
     * @param elementName - Name of element from repository
     */
    public void clearFieldRight(String elementName) {
        logMessageToConsole("Clearing field " + elementName);
        dataReader.getSikuliElement(elementName).right(50).click();
        getSikuliScreen().type("a", Key.CTRL);
        getSikuliScreen().type(Key.BACKSPACE);
    }

    /**
     * Method clear area right from element in case that the area is a input field.
     * @param elementName - Name of element from repository
     * @param distance - distance
     */
    public void clearFieldRight(String elementName, int distance) {
        logMessageToConsole("Clearing field " + elementName);
        dataReader.getSikuliElement(elementName).right(distance).click();
        getSikuliScreen().type("a", Key.CTRL);
        getSikuliScreen().type(Key.BACKSPACE);
    }

    /**
     * Method clear target element right from first element in case that the element is a input field.
     * @param elementNameFrom - Name of element from repository
     * @param targetElementName - Name of second element from repository
     */
    public void clearFieldRight(String elementNameFrom, String targetElementName) {
        logMessageToConsole("Clearing field " + targetElementName);
        final Region region = dataReader.getSikuliElement(elementNameFrom);
        try {
            final Region target = region.right().find(dataReader.getSikuliElementPath(targetElementName));
            target.type("a", Key.CTRL);
            target.type(Key.BACKSPACE);
        } catch (FindFailed findFailed) {
            LOGGER.log(Level.SEVERE, ERROR_TARGET_ELEMENT, findFailed);
        }
    }

    /**
     * Run javascript - not working for Sikulix
     * @param script - javascript to run
     */
    @Override
    public void runJavascript(String script){
        logMessageToConsole("runJavascript function is not supported for Sikuli!");
    }

    /**
     * Check if checkbox is in right state - not working for Sikulix
     * @param elementName - Name of element from repository, must be an input element
     * @param expectedState - true or false
     */
    @Override
    public void assertCheckboxState(String elementName, boolean expectedState){
        logMessageToConsole("assertCheckboxState function is not supported for Sikuli!");
    }

    /**
     * Read text from target element with OCR
     * @param elementName - Name of element from repository
     * @return - Read text
     */
    @Override
    public String getText(String elementName) {
        return TextRecognizer.doOCR(prepareTextToOCR(dataReader.getSikuliElement(elementName).getScreen().capture(dataReader.getSikuliElement(elementName)).getImage(), elementName));
    }

    /**
     * Read text from area below target element with OCR
     * @param elementName - Name of element from repository
     * @param distance - distance
     * @return - text read from given area
     */
    public String getTextBelow(String elementName, int distance) {
        return TextRecognizer.doOCR(prepareTextToOCR(dataReader.getSikuliElement(elementName).getScreen().capture(dataReader.getSikuliElement(elementName).below(distance)).getImage(), elementName));
    }

    /**
     * Read text from area right from target element with OCR
     * @param elementName - Name of element from repository
     * @param distance - distance
     * @return - text read from given area
     */
    public String getTextRight(String elementName, int distance) {
        return TextRecognizer.doOCR(prepareTextToOCR(dataReader.getSikuliElement(elementName).getScreen().capture(dataReader.getSikuliElement(elementName).right(distance)).getImage(), elementName));
    }

    /**
     * Get text of given element - not working with Sikulix
     * @param elementName - Name of element from repository
     * @return - text of given webelement
     */
    @Override
    public String getValue(String elementName) {
        logMessageToConsole("getValue function is not supported for Sikuli! Use getTextBelow or getTextRight instead.");
        return null;
    }

    /**
     * Not working with Sikulix
     * @param baseURLFromProperties - key value from properties file
     */
    @Override
    public void setBaseURL(String baseURLFromProperties) {
        logMessageToConsole("setBaseURL function is not supported for Sikuli!");
    }

    private BufferedImage prepareTextToOCR(BufferedImage image, String elementName) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            if (savePictures) {
                ImageIO.write(image, JPG, new File(dataReader.getSikuliRepoPath() + elementName + "_before.jpg"));
            }
            ImageIO.write(image, PNG, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            final Mat mat = Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), -1);
            Imgproc.resize(mat, mat, new Size(), multiplier, multiplier, Imgproc.INTER_CUBIC);
            final MatOfByte mob = new MatOfByte();
            Imgcodecs.imencode(SUFFIX_JPG, mat, mob);
            image = ImageIO.read(new ByteArrayInputStream(mob.toArray()));
            if (savePictures) {
                ImageIO.write(image, JPG, new File(dataReader.getSikuliRepoPath() + elementName + "_rescaled.jpg"));
            }
            if(convertToGrayscale) {
                image = ImageHelper.convertImageToGrayscale(image);
                image = ImageHelper.getScaledInstance(image, image.getWidth(), image.getHeight());
            }
            if (savePictures) {
                ImageIO.write(image, JPG, new File(dataReader.getSikuliRepoPath() + elementName + "_rescaled_grayscale.jpg"));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error when reading OCR text", e);
        }
        return image;
    }

    /**
     * Enable/disable saving of pictures used for OCR reading.
     * If true pictures will be saved to the Sikuli repository folder.
     * Saves 3 picture. Original, after multiply and after greyscale.
     * @param savePictures - enable/disable function
     */
    public void setSavePictures(boolean savePictures) {
        this.savePictures = savePictures;
    }

    /**
     * Check if save picture function is enabled
     * @return - true if enabled
     */
    public boolean isSavePictures() {
        return savePictures;
    }

    /**
     * Return current value for multiplying
     * @return - current value
     */
    public double getImageReaderMultiplier() {
        return multiplier;
    }

    /**
     * Set value for image resizing for better OCR reading.
     * 1 is for normal size, 2 is for double size
     * @param multiplier - multiplicator
     */
    public void setImageReaderMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * Eneble/disable picture conversion to grayscale colors for better OCR reading
     * @param convertToGrayscale - true for enabled
     */
    public void setConvertToGrayscale(boolean convertToGrayscale) {
        this.convertToGrayscale = convertToGrayscale;
    }

    /**
     * Return if greyscale conversion is enabled
     * @return - current value
     */
    public boolean isConvertToGrayscale() {
        return convertToGrayscale;
    }

    /**
     * Find text on current screen
     * @param text - text to find
     * @return - region with text
     */
    public Region findTextOnScreen(String text) {
        logMessageToConsole("Will try to find text on screen");
        try {
            final Region region = getSikuliScreen().findText(text);
            logMessageToConsole(text + "found");
            return region;
        } catch (FindFailed e) {
            LOGGER.log(Level.SEVERE, "Error when finding text on screen", e);
            return null;
        }
    }

    /**
     * Find text on current screen with inverted colors
     * @param text - text to find
     * @return - region with text
     */
    public Region findTextOnScreenInverted(String text) {
        final Rectangle positions = getInvertedTextPosition(dataReader.getScreen().capture().getImage(), text);
        if(positions == null) {
            LOGGER.log(Level.SEVERE, "Can't find " + text + " on screen");
        }
        Region region = new Region(positions.x,positions.y);
        region = region.right(positions.width/2);
        region = region.below(positions.height/2);
        return region;
    }

    /**
     * Find text on current screen with resized image
     * @param text - text to find
     * @param xResize - x multiplier
     * @param yResize - y multiplier
     * @return - region with text
     */
    public Region findTextOnScreenResized(String text, float xResize, float yResize) {
        final Rectangle positions = getResizedTextPosition(dataReader.getScreen().capture().getImage(), text, xResize, yResize);
        if(positions == null) {
            LOGGER.log(Level.SEVERE, "Can't find " + text + " on screen");
        }
        Region region = new Region(Math.round(positions.x/xResize),Math.round(positions.y/yResize));
        region = region.right(positions.width/2);
        region = region.below(positions.height/2);
        return region;
    }

    /**
     * Find text right from given element
     * @param elementName - given element
     * @param text - text to find
     * @return - region with text
     */
    public Region findTextRight(String elementName, String text) {
        logMessageToConsole("Will try to find text on screen");
        try {
            final Region region = dataReader.getSikuliElement(elementName).right().findText(text);
            logMessageToConsole(text + " found");
            return region;
        } catch (FindFailed e) {
            LOGGER.log(Level.SEVERE, "Error when finding text on screen", e);
            return null;
        }
    }

    /**
     * Find text right from given element with inverted colors
     * @param elementName - given element
     * @param text - text to find
     * @return - region with text
     */
    public Region findTextRightInverted(String elementName, String text) {
        final Rectangle positions = getInvertedTextPosition(dataReader.getSikuliElement(elementName).getScreen().capture(dataReader.getSikuliElement(elementName).right()).getImage(), text);
        if(positions == null) {
            LOGGER.log(Level.SEVERE, "Can't find " + text + " on screen");
        }
        final Region positionReg = dataReader.getSikuliElement(elementName).right();
        Region region = new Region(positionReg.x+positions.x,positionReg.y+positions.y);
        region = region.right(positions.width/2);
        region = region.below(positions.height/2);
        return region;
    }

    /**
     * Find text below given element
     * @param elementName - given element
     * @param text - text to find
     * @return - region with text
     */
    public Region findTextBelow(String elementName, String text) {
        logMessageToConsole("Will try to find text on screen");
        try {
            final Region region = dataReader.getSikuliElement(elementName).below().findText(text);
            logMessageToConsole(text + "found");
            return region;
        } catch (FindFailed e) {
            LOGGER.log(Level.SEVERE, "Error when finding text on screen", e);
            return null;
        }
    }

    /**
     * Find text below given element with inverted colors
     * @param elementName - given element
     * @param text - text to find
     * @return - region with text
     */
    public Region findTextBelowInverted(String elementName, String text) {
        final Rectangle positions = getInvertedTextPosition(dataReader.getSikuliElement(elementName).getScreen().capture(dataReader.getSikuliElement(elementName).below()).getImage(), text);
        if(positions == null) {
            LOGGER.log(Level.SEVERE, "Can't find " + text + " on screen");
        }
        final Region positionReg = dataReader.getSikuliElement(elementName).below();
        Region region = new Region(positionReg.x+positions.x,positionReg.y+positions.y);
        region = region.right(positions.width/2);
        region = region.below(positions.height/2);
        return region;
    }

    private Rectangle getInvertedTextPosition(BufferedImage image, String text) {
        Rectangle positions = null;
        image = ImageHelper.invertImageColor(image);
        for (final Word word : TextRecognizer.start().getAPI().getWords(image, ITessAPI.TessPageIteratorLevel.RIL_WORD)) {
            if (word.getText().contains(text)) {
                positions = word.getBoundingBox();
                break;
            }
        }
        return positions;
    }

    private Rectangle getResizedTextPosition(BufferedImage image, String text, float xResize, float yResize) {
        Rectangle positions = null;
        image = ImageHelper.getScaledInstance(image,Math.round(image.getWidth()*xResize),Math.round(image.getHeight()*yResize));
        for (final Word word : TextRecognizer.start().getAPI().getWords(image, ITessAPI.TessPageIteratorLevel.RIL_WORD)) {
            if (word.getText().contains(text)) {
                positions = word.getBoundingBox();
                break;
            }
        }
        return positions;
    }

    /**
     * Type string. Can be used to key press.
     * @param key - key to press
     */
    public void pressKey(String key) {
        getSikuliScreen().type(key);
    }

    @Override
    public MethodHelper waitForElementValue(String elementName, String value) {
        LOGGER.log(Level.WARNING, "waitForElementValue is not supported for Sikuli!");
        return this;
    }

    @Override
    public MethodHelper waitForElementValue(String elementName, String value, int secondsToWait) {
        LOGGER.log(Level.WARNING, "waitForElementValue is not supported for Sikuli!");
        return this;
    }

    @Override
    public MethodHelper waitForElementText(String elementName, String text) {
        LOGGER.log(Level.WARNING, "waitForElementtext is not supported for Sikuli!");
        return this;
    }


    @Override
    public MethodHelper waitForElementText(String elementName, String text, int secondsToWait) {
        LOGGER.log(Level.WARNING, "waitForElementtext is not supported for Sikuli!");
        return this;
    }

    @Override
    public MethodHelper waitForElementAttributeValue(String elementName, String attribute, String value) {
        LOGGER.log(Level.WARNING, "waitForElementAttributeValue is not supported for Sikuli!");
        return this;
    }

    @Override
    public MethodHelper waitForElementAttributeValue(String elementName, String attribute, String value, int secondsToWait) {
        LOGGER.log(Level.WARNING, "waitForElementAttributeValue is not supported for Sikuli!");
        return this;
    }

    /**
     * Focus app by appName. It will chose application by part of window title
     * @param appName
     */
    public void switchToApp(String appName) {
        dataReader.getSikuliApp().focus(appName);
    }
}