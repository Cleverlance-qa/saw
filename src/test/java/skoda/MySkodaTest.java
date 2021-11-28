package skoda;

import corehelpers.testbase.TestBaseMobile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testbody.skoda.MySkodaChargeLimit;
import testbody.skoda.MySkodaHealthCheck;
import testbody.skoda.MySkodaHeater;

public class MySkodaTest extends TestBaseMobile {

    @Before
    public void setUp() {
        initDriver("S20_MySkoda");
    }

    @Test
    public void heaterTest() {
        success = new MySkodaHeater(dataReader).testRun("ENYAQ");
        testBodyName = MySkodaHeater.class.getSimpleName();
    }

    @Test
    public void chargeLimitTest() {
        success = new MySkodaChargeLimit(dataReader).testRun("ENYAQ");
        testBodyName = MySkodaChargeLimit.class.getSimpleName();
    }

    @Test
    public void healthCheckTest() {
        success = new MySkodaHealthCheck(dataReader).testRun("KAROQ");
        testBodyName = MySkodaHealthCheck.class.getSimpleName();
    }

    @After
    public void cleanUp() {
        quitDriver(success, testBodyName);
    }
}
