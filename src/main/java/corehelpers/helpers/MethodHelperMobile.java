package corehelpers.helpers;

import corehelpers.DataReader;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Finder;
import org.sikuli.script.Match;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helpers/keywords for Mobile tests
 */
public class MethodHelperMobile extends MethodHelper {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private BufferedImage screen = null;
    private BufferedImage targetImage = null;

    /**
     * Inicialized class for given DataReader instance.
     * @param dataReader - DataReader instance
     */
    public MethodHelperMobile(DataReader dataReader) {
        super(dataReader);
    }

    /**
     * Set Wait instance
     */
    @Override
    public void setWait() {
        wait = new WebDriverWait(getMobileDriver(), 60);
    }

    /**
     * Set tested platform
     */
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
            FileUtils.copyFile(getMobileDriver().getScreenshotAs(OutputType.FILE), new File( SCREENSHOTS_FOLDER + super.getClass().getSimpleName() + UNDERSCORE + name + SUFFIX_PNG));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error when taking screenshot", e);
        }
    }

    /**
     * Open url in browser
     * @param url - url to open
     */
    public void openURL(String url) {
        getMobileDriver().get(url);
    }

    /**
     * Simulate mobile back action
     */
    public void goBack() {
        getMobileDriver().navigate().back();
    }

    /**
     * Simulate mobile enter action
     */
    public void pressEnter() {
        getMobileDriver().getKeyboard().pressKey(Keys.ENTER);
    }

    /**
     * Swipe on the screen from one element to second one
     * @param elementFrom - start element
     * @param elementTo - target element
     */
    public void swipeFromTo(String elementFrom, String elementTo){
        final TouchAction touchAction = new TouchAction(getMobileDriver());
        final Point pointFrom = dataReader.getElement(elementFrom).getLocation();
        final Point pointTo = dataReader.getElement(elementTo).getLocation();
        touchAction.longPress(PointOption.point(pointFrom)).moveTo(PointOption.point(pointTo)).release().perform();
    }

    /**
     * Swipe on the screen from one element to given coordinates
     * @param elementFrom - start element
     * @param toXCoordinate - target x coordinates
     * @param toYCoordinate - target y coordinates
     */
    public void swipeFromTo(String elementFrom, int toXCoordinate, int toYCoordinate){
        final TouchAction touchAction = new TouchAction(getMobileDriver());
        final Point pointFrom = dataReader.getElement(elementFrom).getLocation();
        touchAction.longPress(PointOption.point(pointFrom)).moveTo(PointOption.point(toXCoordinate,toYCoordinate)).release().perform();
    }

    /**
     * Swipe on screen from given coordinates to target element
     * @param fromXCoordinate - start x coordinates
     * @param fromYCoordinate - start y coordinates
     * @param elementTo - target element
     */
    public void swipeFromTo(int fromXCoordinate, int fromYCoordinate, String elementTo){
        final TouchAction touchAction = new TouchAction(getMobileDriver());
        final Point pointTo = dataReader.getElement(elementTo).getLocation();
        touchAction.longPress(PointOption.point(fromXCoordinate, fromYCoordinate)).moveTo(PointOption.point(pointTo)).release().perform();
    }

    /**
     * Swipe on the screen from first coordinates to second coordinates
     * @param fromXCoordinate - start x coordinates
     * @param fromYCoordinate - start y coordinates
     * @param toXCoordinate - target x coordinates
     * @param toYCoordinate - target y coordinates
     */
    public void swipeFromTo(int fromXCoordinate, int fromYCoordinate, int toXCoordinate, int toYCoordinate){
        final TouchAction touchAction = new TouchAction(getMobileDriver());
        touchAction.longPress(PointOption.point(fromXCoordinate, fromYCoordinate)).moveTo(PointOption.point(toXCoordinate,toYCoordinate)).release().perform();
    }

    /**
     * Switch to different app by appPackage and appActivity. For Android devices only.
     * @param appPackage - aplication appPackage
     * @param appActivity - aplication appActivity
     */
    public void switchToApp(String appPackage, String appActivity) {
        if("android".equals(getMobileDriver().getPlatformName())) {
            final Activity activity = new Activity(appPackage, appActivity);
            activity.setStopApp(false);
            ((AndroidDriver) getMobileDriver()).startActivity(activity);
            return;
        }
        LOGGER.log(Level.WARNING,"switchToApp(String appPackage, String appActivity) method is only for Android devices. For iOS use switchToApp(String bundleId)");
    }

    /**
     * Switch to different app by appPackage and appActivity. For iOS devices only.
     * @param bundleId - aplication bundleId
     */
    public void switchToApp(String bundleId) {
        if("ios".equals(getMobileDriver().getPlatformName())) {
            final HashMap<String, Object> args = new HashMap<>();
            args.put("bundleId", bundleId);
            getMobileDriver().executeScript("mobile: launchApp", args);
            dialogPause();
        }
        LOGGER.log(Level.WARNING,"switchToApp(String bundleId) method is only for iOS devices. For Android use switchToApp(String appPackage, String appActivity)");
    }

    /**
     * Return relative screen position by percentage
     * @param percent - screen positon in percentage
     * @return - relative position in pixels
     */
    public int getRelativePositionHeight(int percent) {
        try {
            checkPercentage(percent);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE,"Percentage must be between 0 and 100");
            return 0;
        }
        final double windowSize = getMobileDriver().manage().window().getSize().getHeight();
        return (int) ((windowSize / 100) * percent);
    }

    /**
     * Return relative screen position by percentage
     * @param percent - screen positon in percentage
     * @return - relative position in pixels
     */
    public int getRelativePositionWidth(int percent) {
        try {
            checkPercentage(percent);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE,"Percentage must be between 0 and 100");
            return 0;
        }
        final double windowSize = getMobileDriver().manage().window().getSize().getWidth();
        return (int) ((windowSize / 100) * percent);
    }

    private void checkPercentage(int percent) throws IllegalArgumentException {
        if(percent>=0 && percent<=100) {
            return;
        }
        throw new IllegalArgumentException("Percentage must be between 0 and 100");
    }

    /**
     * Click on graphic element on current screen
     * @param sikuliElementName - element name from Sikuli repository
     */
    public void clickOnGraphicElement(String sikuliElementName) {
        final File screenFile = getMobileDriver().getScreenshotAs(OutputType.FILE);
        Finder finder = new GraphicHelper().prepareFinder(screenFile, sikuliElementName);
        if(finder.hasNext()) {
            final Match match = finder.next();
            LOGGER.log(Level.INFO, sikuliElementName + " found on screen, will click on it");
            final TouchAction touchAction = new TouchAction(getMobileDriver());
            System.out.println(match.getTarget().x + " " + match.getTarget().y);
            touchAction.tap(PointOption.point(match.getTarget().x, match.getTarget().y)).perform();
            return;
        }
        throw new NoSuchElementException("Can't find " + sikuliElementName + " on screen");
    }

    /**
     * Assert that given graphic element exist on current screen
     * @param sikuliElementName - element name from Sikuli repository
     * @return - true if exists
     */
    public boolean assertGraphicElementExist(String sikuliElementName) {
        final File screenFile = getMobileDriver().getScreenshotAs(OutputType.FILE);
        Finder finder = new GraphicHelper().prepareFinder(screenFile, sikuliElementName);
        if(finder.hasNext()) {
            LOGGER.log(Level.INFO, sikuliElementName + "not found on screen");
            return true;
        }
        LOGGER.log(Level.INFO, sikuliElementName + "not found on screen");
        return false;
    }

    /**
     * Find a text on current screen and click on it
     * @param text - text to find
     */
    public void getTextOnScreenAndClick(String text) {
        logMessageToConsole("Will try to find text on screen");
        final Match match = prepareTextFinder(text);
        LOGGER.log(Level.INFO, text + " found on screen, will click on it");
        final TouchAction touchAction = new TouchAction(getMobileDriver());
        touchAction.tap(PointOption.point(match.getTarget().x, match.getTarget().y)).perform();
    }

    /**
     * Find a text on current screen and click on it
     * @param text - text to find
     */
    public void getTextOnScreenAndRightClick(String text) {
        logMessageToConsole("Will try to find text on screen");
        final Match match = prepareTextFinder(text);
        LOGGER.log(Level.INFO, text + " found on screen, will right click on it");
        final TouchAction touchAction = new TouchAction(getMobileDriver());
        touchAction.tap(PointOption.point(match.getTarget().x, match.getTarget().y)).perform();
    }

    /**
     * Verify that given text exists on current screen
     * @param text - text to find
     */
    public boolean verifyTextOnScreenExist(String text) {
        logMessageToConsole("Will try to find text on screen");
        Match match = null;
        try {
            match = prepareTextFinder(text);
        } catch (Exception e) {
            LOGGER.log(Level.INFO, text + " not found on screen");
            return false;
        }
        if (match.getText()==null) {
            LOGGER.log(Level.INFO, text + " not found on screen");
            return false;
        }
        return true;
    }

    private Match prepareTextFinder(String text) {
        logMessageToConsole("Will try to find text on screen");
        Match match = null;
        final File screenFile = getMobileDriver().getScreenshotAs(OutputType.FILE);
        try {
            screen = ImageIO.read(screenFile);
            final Finder finder = new Finder(screen);
            finder.find(screen);
            if(finder.hasNext()) {
                match = finder.next();
                match = match.findText(text);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading file in assertGraphicElementExist", e);
        } catch (FindFailed findFailed) {
            LOGGER.log(Level.SEVERE, "Can't find " + text + " on screen");
        }
        return match;
    }
}