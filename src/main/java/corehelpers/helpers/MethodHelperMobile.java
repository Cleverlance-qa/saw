package corehelpers.helpers;

import corehelpers.DataReader;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.io.FileUtils;
import org.apache.http.annotation.Obsolete;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Finder;
import org.sikuli.script.Match;
import testhelpers.Skoda.MySkoda.MySkodaGeneralHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
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

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;
    }

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
       // getMobileDriver().navigate().back();
        ((AndroidDriver)this.getMobileDriver()).pressKey(new KeyEvent(AndroidKey.BACK));
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
    @Obsolete
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
            } catch (WebDriverException |NullPointerException e) {
                LOGGER.log(Level.INFO,"Waiting for " + elementName + " being clickable for " + i + " seconds");
                dialogPause();
            }
        }
        throw new TimeoutException("Element is still not clickable!");
    }

    public void swipeLeft(){
        swipeScreen(MySkodaGeneralHelper.Direction.LEFT);
    }

    public void swipeRight(){
        swipeScreen(MySkodaGeneralHelper.Direction.RIGHT);
    }

    public void swipeDown(){
        swipeScreen(MySkodaGeneralHelper.Direction.DOWN);
    }

    public void swipeUp(){
        swipeScreen(MySkodaGeneralHelper.Direction.UP);
    }

    private void swipeScreen(MySkodaGeneralHelper.Direction dir) {
        System.out.println("swipeScreen(): dir: '" + dir + "'"); // always log your actions
        // Animation default time:
        //  - Android: 300 ms
        //  - iOS: 200 ms
        // final value depends on your app and could be greater
        final int ANIMATION_TIME = 200; // ms
        final int PRESS_TIME = 200; // ms
        int edgeBorder = 10; // better avoid edges
        PointOption pointOptionStart, pointOptionEnd;

        // init screen variables
        Dimension dims = getMobileDriver().manage().window().getSize();

        // init start point = center of screen
        pointOptionStart = PointOption.point(dims.width / 2, dims.height / 2);

        switch (dir) {
            case DOWN:
                pointOptionEnd = PointOption.point(dims.width / 2, dims.height - edgeBorder);
                break;
            case UP:
                pointOptionEnd = PointOption.point(dims.width / 2, edgeBorder);
                break;
            case LEFT:
                pointOptionEnd = PointOption.point(edgeBorder, dims.height / 2);
                break;
            case RIGHT:
                pointOptionEnd = PointOption.point(dims.width - edgeBorder, dims.height / 2);
                break;
            default:
                throw new IllegalArgumentException("swipeScreen(): dir: '" + dir + "' NOT supported");
        }

        // execute swipe using TouchAction
        try {
            new TouchAction(getMobileDriver())
                    .press(pointOptionStart)
                    // a bit more reliable when we add small wait
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(PRESS_TIME)))
                    .moveTo(pointOptionEnd)
                    .release().perform();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "swipeScreen(): TouchAction FAILED\n" + e.getMessage());
            return;
        }

        try {
            Thread.sleep(ANIMATION_TIME);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * Check if given image exists on current screen.
     * @param image - picture name from TestInputs/visual folder withnout .png
     * @return - true if image exists on current screen
     */
    public boolean checkImageExistOnScreen(String image) {
        final GraphicHelper visual = new GraphicHelper(dataReader);
        return visual.checkMobileImageContains(image, getMobileDriver().getScreenshotAs(OutputType.FILE));
    }

    /**
     * Check if given image exists on current screen.
     * Throws fail if match return false.
     * @param image - picture name from TestInputs/visual folder withnout .png
     */
    public void assertImageExistOnScreen(String image) {
        final GraphicHelper visual = new GraphicHelper(dataReader);
        if(! visual.checkMobileImageContains(image, getMobileDriver().getScreenshotAs(OutputType.FILE))) {
            Assert.fail();
        }
    }


}