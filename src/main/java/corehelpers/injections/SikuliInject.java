package corehelpers.injections;

import corehelpers.DataReader;
import corehelpers.DriversSetUp;
import corehelpers.helpers.MethodHelperSikuli;
import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;

/**
 * Sikuli Injection for non-sikuli tests
 */
public class SikuliInject extends MethodHelperSikuli {

    /**
     * Prepace SikuliX injection.
     */
    public SikuliInject() {
        super(new <Screen>DataReader(new DriversSetUp().initSikuliInjection()));
        ImagePath.setBundlePath("user.dir");
        Settings.MinSimilarity = 0.8;
        Settings.OcrLanguage = "ces";
    }

    /**
     * Get current DataReader instance
     * @return - DataReader instance
     */
    public DataReader getDataReader() {
        return dataReader;
    }
}