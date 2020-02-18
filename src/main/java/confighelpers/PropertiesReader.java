package confighelpers;

import corehelpers.DataReader;

import java.util.Properties;

/**
 * Class for reading and setting items from properties config file
 */
public class PropertiesReader {
    protected DataReader dataReader;
    protected Properties properties;
    protected String baseURL;

    /**
     * Read values from properties.xml file and set them as a variables
     * @param dataReader - current datareader instance
     */
    public PropertiesReader(DataReader dataReader) {
        this.dataReader = dataReader;
        properties = dataReader.getXMLProperties();
        baseURL = properties.getProperty("baseURL");
    }
}
