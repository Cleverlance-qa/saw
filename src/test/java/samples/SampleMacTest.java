package samples;

import corehelpers.testbase.TestBaseDesktop;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testbody.samples.Sample;

public class SampleMacTest extends TestBaseDesktop {

    @Before
    public void setUp() {
        initDriver("chrome", "localMac");
    } //Sekce before urcuje co se stane pred spustenim testu. V tomto pripade dojde k inicialici frameworku pro danou konfiguraci

    @Test
    public void testStarter() {
        success = new Sample(dataReader).testRun();
        testBodyName = Sample.class.getSimpleName();
    } //Zde dojde ke spusteni testu pro urcene TestBody

    @After
    public void cleanUp() {
        quitDriver(success, testBodyName);
    } //Cistici sekce spoustena po skonceni testu. Zapisuje do exportu, ukoncuje session atd.
}
