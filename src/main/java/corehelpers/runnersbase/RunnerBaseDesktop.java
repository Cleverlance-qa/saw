package corehelpers.runnersbase;

import corehelpers.testbase.TestBaseDesktop;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RunWith(Parameterized.class)
public abstract class RunnerBaseDesktop extends TestBaseDesktop {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String ERROR_FILE_NOT_FOUND = "File not found!";
    private static List<CSVRecord> recordsList = new ArrayList<>();
    private static final String MATRIX_FILE_NAME = "TestInputs/BrowserMatrixDesktop.csv";
    private static final String CVS_SPLIT_BY = ";";
    public final String browser;
    public final String platform;
    public final String propertiesFile;
    public final String grafanaFile;

    public RunnerBaseDesktop(String browser, String platform, String propertiesFile, String grafanaFile) {
        this.browser = browser;
        this.platform = platform;
        this.propertiesFile = propertiesFile;
        this.grafanaFile = grafanaFile;
    }

    @Before
    public void setUp() {
        setProperties(propertiesFile);
        setGrafanaProperties(grafanaFile);
        initDriver(browser, platform);
    }

    @Parameterized.Parameters(name = "Run {index}: browser={0}, platform={1}")
    public static Iterable<Object[]> data()
    {
        final List<Object[]> settingsList = new ArrayList();
        try(Reader in = new FileReader(MATRIX_FILE_NAME)) {
            final Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().withDelimiter(CVS_SPLIT_BY.charAt(0)).parse(in);
            recordsList.clear();
            for (final CSVRecord record : records) {
                recordsList.add(record);
            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE,ERROR_FILE_NOT_FOUND, e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error while reading BrowserMatrixDesktop.csv", e);
        }
        for (final CSVRecord record : recordsList ) {
            settingsList.add(new Object[] {
                     record.get(0), record.get(1), record.get(2), record.get(3)
            });
        }
        return settingsList;
    }
}