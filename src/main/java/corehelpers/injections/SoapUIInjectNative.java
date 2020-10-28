package corehelpers.injections;


import com.eviware.soapui.tools.SoapUITestCaseRunner;

import java.net.ProxySelector;
import java.util.logging.Level;
import java.util.logging.Logger;

import static corehelpers.constants.SAWConstants.TESTINPUTS;

/**
 * SoapUI integration handling
 */
public class SoapUIInjectNative {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final String projectXMLFile;
    private String testSuite;
    private SoapUITestCaseRunner runner;

    /**
     * Load SoapUI xml project file and set TestSuite
     * @param projectXMLFile - path to xml file
     * @param testSuite - TestSuite name
     */
    public SoapUIInjectNative(String projectXMLFile, String testSuite) {
        this.projectXMLFile = TESTINPUTS + projectXMLFile;
        setTestSuite(testSuite);
        initRunner();
    }

    private void initRunner() {
        runner = new SoapUITestCaseRunner();
        runner.setProjectFile(projectXMLFile);
        runner.setTestSuite(testSuite);
        runner.setPrintReport(true);
        runner.setJUnitReport(true);
        runner.setExportAll(true);
        runner.setOutputFolder("TestOutputs/");
    }

    /**
     * Run SoapUI TestCase
     * @param testCase - name of TestCase
     */
    public void runTestCase(String testCase) {
        ProxySelector proxy = ProxySelector.getDefault();
        runner.setTestCase(testCase);
        try {
            runner.run();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,"Exception when running Test Case", e);
        }
        ProxySelector.setDefault(proxy);
    }

    private void setTestSuite(String testSuite) {
        this.testSuite = testSuite;
    }
}