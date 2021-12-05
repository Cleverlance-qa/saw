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
        testBodyName = MySkodaHeater.class.getSimpleName();
        success = new MySkodaHeater(dataReader).testRun("ENYAQ");
    }

    @Test
    public void chargeLimitTest() {
        testBodyName = MySkodaChargeLimit.class.getSimpleName();
        success = new MySkodaChargeLimit(dataReader).testRun("ENYAQ");
    }

    @Test
    public void healthCheckTest() {
        testBodyName = MySkodaHealthCheck.class.getSimpleName();
        success = new MySkodaHealthCheck(dataReader).testRun("KAROQ");
    }

    @After
    public void cleanUp() {
        quitDriver(success, testBodyName);
    }
}
