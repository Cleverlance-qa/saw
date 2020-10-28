package corehelpers.helpers;

import org.sikuli.script.Finder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static corehelpers.ElementsFinder.SIKULI_REPO_PATH;
import static corehelpers.helpers.MethodHelper.SUFFIX_PNG;

/**
 * Helper class for handling graphics in Desktop and Mobile tests
 */
public class GraphicHelper {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private BufferedImage screen = null;
    private BufferedImage targetImage = null;

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

}
