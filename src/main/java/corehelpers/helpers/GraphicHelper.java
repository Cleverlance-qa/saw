package corehelpers.helpers;

import corehelpers.DataReader;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Word;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.sikuli.script.Finder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static corehelpers.ElementsFinder.SIKULI_REPO_PATH;
import static corehelpers.constants.SAWConstants.PNG;
import static corehelpers.helpers.MethodHelper.SUFFIX_PNG;
import static nu.pattern.OpenCV.loadLocally;
import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * Helper class for handling graphics in Desktop and Mobile tests
 */
public class GraphicHelper {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private BufferedImage screen = null;
    private BufferedImage targetImage = null;
    String imgName;
    DataReader dataReader;
    String tempPath;

    public GraphicHelper() {

    }

    /**
     * Default constructor.
     * Load opencv library
     */
    public GraphicHelper(DataReader dataReader) {
        this.dataReader = dataReader;
        loadLocally();
        if (System.getProperty("java.io.tmpdir").endsWith("/") || System.getProperty("java.io.tmpdir").endsWith("\\")){
            tempPath = System.getProperty("java.io.tmpdir");
        } else {
            tempPath = System.getProperty("java.io.tmpdir") + "/";
        }
    }

    /**
     * Prepare Try to find given element on screenFile and return results as Finder
     * @param screenFile - file with graphic
     * @param sikuliElementName - element from sikuli repository
     * @return - search results
     */
    public Finder prepareFinder(File screenFile, String sikuliElementName) {
        Finder finder = null;
        try {
            screen = ImageIO.read(screenFile);
            final File targetFile = new File(SIKULI_REPO_PATH + sikuliElementName + SUFFIX_PNG);
            targetImage = ImageIO.read(targetFile);
            finder = new Finder(screen);
            finder.find(targetImage);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading file in assertGraphicsElementExist", e);
        }
        return finder;
    }

    /**
     * Check that image contains second image
     * @param searchedImage - searched image
     * @param baseImage - base image
     * @return - true if contains
     */
    public boolean checkMobileImageContains(String searchedImage, File baseImage) {
        LOGGER.log(Level.INFO, "Will find out if there is given image: " + searchedImage + " on screen.");
        try {
            FileUtils.copyFile(baseImage, new File(tempPath + "baseImage.png"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can't find screenshot temp file!", e);
        }

        final Mat imgA = Imgcodecs.imread(tempPath + "baseImage.png", Imgcodecs.CV_LOAD_IMAGE_COLOR);
        final Mat imgB = Imgcodecs.imread(System.getProperty("user.dir") + "/TestInputs/visual/" + searchedImage + SUFFIX_PNG, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        return doVisualContainsCheck(imgA, imgB);
    }

    /**
     * Check that image contains second image
     * @param searchedImage - searched image
     * @param baseImage - base image
     * @return - true if contains
     */
    public boolean checkMobileImageContains(String searchedImage, byte[] baseImage) {
        LOGGER.log(Level.INFO, "Will find out if there is given image: " + searchedImage + " on screen.");

        final Mat imgA = bufferedImage2Mat(takeMobileScreenshotNoSP(baseImage));
        final Mat imgB = imread(System.getProperty("user.dir") + "/TestInputs/visual/" + searchedImage + SUFFIX_PNG, Imgcodecs.CV_LOAD_IMAGE_COLOR);

        return doVisualContainsCheck(imgA, imgB);
    }

    /**
     * Cut off status bar from mobile screenshot
     * @param screen - mobile screen
     * @return - screenshot without status bar
     */
    public BufferedImage takeMobileScreenshotNoSP(byte[] screen) {
        final By locator = By.xpath("(//android.widget.FrameLayout)[2]");
        final BufferedImage screenshot;
        try {
            screenshot = ImageIO.read(new ByteArrayInputStream(screen));
            final int panelHeight = getMobilePanelHeight(screenshot);
            final WebElement element = dataReader.getAppDriver().findElement(locator);
            final org.openqa.selenium.Point elementLocation = element.getLocation();
            final Dimension elementSize = element.getSize();
            return screenshot.getSubimage(elementLocation.x, panelHeight,
                    elementSize.width, elementSize.height-panelHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean doVisualContainsCheck(Mat imgA, Mat imgB) {
        final double threshold = 0.1; // increase to be more tolerant
        final Mat resultMatrix = new Mat();
        final int result_cols = imgA.cols() - imgB.cols() + 1;
        final int result_rows = imgA.rows() - imgB.rows() + 1;
        resultMatrix.create(result_rows, result_cols, CvType.CV_32FC1);
        Imgproc.matchTemplate(imgA, imgB, resultMatrix, Imgproc.TM_SQDIFF_NORMED);

        Core.MinMaxLocResult mmr = Core.minMaxLoc(resultMatrix);
        if (mmr.minVal < threshold) {
            LOGGER.log(Level.INFO, "Image match found with difference: " + mmr.minVal);
            return true;
        }
        LOGGER.log(Level.SEVERE, "Image match not found! Diff is: " + mmr.minVal);
        return false;
    }

    private Mat bufferedImage2Mat(BufferedImage image) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, PNG, byteArrayOutputStream);
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_COLOR);
    }

    private int getMobilePanelHeight(BufferedImage image) {
        final ITesseract instance = new Tesseract();
        final File tessDataFolder = LoadLibs.extractTessResources("tessdata"); // Maven build bundles English data
        instance.setDatapath(tessDataFolder.getAbsolutePath());
        instance.setTessVariable("tessedit_char_whitelist", "0123456789:");

        for(int i = 1; i<=2; i++) {
            BufferedImage imageToCheck = image;
            final String mobileTime = dataReader.getAppDriver().getDeviceTime("mm");
            if(i==2) {
                imageToCheck = ImageHelper.invertImageColor(image);
            }
            imageToCheck = imageToCheck.getSubimage(imageToCheck.getMinX(), 1, imageToCheck.getWidth() - 1, 120);
            for (final Word word : instance.getWords(imageToCheck, ITessAPI.TessPageIteratorLevel.RIL_PARA)) {
                String readText = word.getText();
                if (readText.contains(mobileTime)) {
                    final Rectangle boundingBox = word.getBoundingBox();
                    LOGGER.log(Level.INFO, "System panel height = " + (boundingBox.getX() + boundingBox.getHeight()));
                    LOGGER.log(Level.INFO, "Screenshot without panel successfully taken");
                    return (int) (boundingBox.getX() + boundingBox.getHeight() + 5);
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error when thread.sleep", e);
            }
        }
        LOGGER.log(Level.WARNING, "Can't find system date panel!");
        return 0;
    }


}
