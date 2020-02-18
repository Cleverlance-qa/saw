package samples;

import corehelpers.testbase.TestBaseMobile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testbody.samples.MobileSample;

public class MobileSampleTest extends TestBaseMobile {

    @Before
    public void setUp() {
        initDriver("androidExample");
    }

    @Test
    public void testStarter() {
        success = new MobileSample(dataReader).testRun();  //Zde zmente test body na to, ktere chcete pouzit
        testBodyName = MobileSample.class.getSimpleName();
    }

    @After
    public void cleanUp() {
        quitDriver(success, testBodyName);
    }
}
