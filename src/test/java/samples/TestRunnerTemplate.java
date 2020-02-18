package samples;

import corehelpers.testbase.TestBaseDesktop;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testbody.samples.TestBodyTemplate;

public class TestRunnerTemplate extends TestBaseDesktop {

    @Before
    public void setUp() {
        initDriver("chrome", "local");
    }

    @Test
    public void testStarter() {
        success = new TestBodyTemplate(dataReader).testRun();  //Zde zmente test body na to, ktere chcete pouzit
        testBodyName = TestBodyTemplate.class.getSimpleName();
    }

    @After
    public void cleanUp() {
        quitDriver(success, testBodyName);
    }
}
