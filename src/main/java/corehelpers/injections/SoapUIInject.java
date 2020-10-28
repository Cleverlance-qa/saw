package corehelpers.injections;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestRunner;
import com.eviware.soapui.model.testsuite.TestSuite;
import com.eviware.soapui.support.SoapUIException;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.net.ProxySelector;
import java.util.logging.Level;
import java.util.logging.Logger;

import static corehelpers.constants.SAWConstants.TESTINPUTS;
import static org.junit.Assert.assertEquals;

/**
 * SoapUI integration handling
 */
public class SoapUIInject {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final String projectXMLFile;
    private String testSuite;
    private TestRunner tcRunner;
    private TestSuite ts;

    /**
     * Load SoapUI xml project file and set TestSuite
     * @param projectXMLFile - path to xml file
     * @param testSuite - TestSuite name
     */
    public SoapUIInject(String projectXMLFile, String testSuite) {
        this.projectXMLFile = TESTINPUTS + projectXMLFile;
        setTestSuite(testSuite);
        initRunner();
    }

    private void initRunner() {
        try {
            ProxySelector proxy = ProxySelector.getDefault();
            final WsdlProject project = new WsdlProject(projectXMLFile);
            ProxySelector.setDefault(proxy);
            ts = project.getTestSuiteByName(testSuite);
        } catch (XmlException e) {
            LOGGER.log(Level.SEVERE, "Error when reading XML file", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error when loading XML file", e);
        } catch (SoapUIException e) {
            LOGGER.log(Level.SEVERE, "Exception when running Test Case", e);
        }
    }

     /**
     * Run SoapUI TestCase
     * @param testCase - name of TestCase
     */
    public void runTestCase(String testCase) {
            final TestCase tc = ts.getTestCaseByName( testCase );
            tcRunner = tc.run(new PropertiesMap(), false );
            assertEquals( TestRunner.Status.FINISHED, tcRunner.getStatus() );
    }

    /**
     * Get XML response from given step
     * @param testStep - test step name
     * @return - XML response
     */
    public String getTestStepResponse(String testStep) {
        return tcRunner.getRunContext().expand("${" + testStep + "#Response}");
    }

    private void setTestSuite(String testSuite) {
        this.testSuite = testSuite;
    }
}