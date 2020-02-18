package samples;

import corehelpers.testbase.TestBaseSikuli;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testbody.samples.SikuliSample;

public class SikuliSampleTest extends TestBaseSikuli {

    @Before
    public void setUp() {
        initDriver();
    } //Sekce before urcuje co se stane pred spustenim testu. V tomto pripade dojde k inicialici frameworku pro danou konfiguraci

    @Test
    public void testStarter() {
        testBodyName = SikuliSample.class.getSimpleName(); //Zde zmente test body na to, ktere chcete pouzit
        success = new SikuliSample(dataReader).testRun(); //Zde zmente test body na to, ktere chcete pouzit
    }

    @After
    public void cleanUp() {
        quitDriver(success, testBodyName); //Cistici sekce spoustena po skonceni testu. Zapisuje do exportu, ukoncuje session atd.
    }
}
