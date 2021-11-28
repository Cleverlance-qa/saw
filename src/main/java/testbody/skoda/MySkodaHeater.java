package testbody.skoda;

import corehelpers.DataReader;
import corehelpers.helpers.MethodHelperMobile;
import corehelpers.injections.TimeWatch;
import testhelpers.Skoda.MySkoda.MySkodaGeneralHelper;

import java.util.concurrent.TimeUnit;

public class MySkodaHeater extends MethodHelperMobile {

    public MySkodaHeater(DataReader dataReader) {     //Konstruktor tridy. Pomaha definovat co se stane pri vytvoreni tridy. V tomto pripade dojde k predani informace ulozene v datareader promene.
        super(dataReader);
    }

    public boolean testRun(String carModel) {
        MySkodaGeneralHelper generalHelper = new MySkodaGeneralHelper(dataReader);
       // generalHelper.login("Gqs2821@skoq.cz", "Connectedcar1");
        generalHelper.loginNoReset(carModel);
        generalHelper.selectFromCarMenu("Ovládání teploty (BETA)");

        waitForElementExist("climateControllStartButton");
        assertGraphicElementExist("klima_off");
        TimeWatch watch = new TimeWatch();
        watch.start();
        waitForClickable("climateControllStartButton");
        waitForElementExist("climateControllStartProgressLabel").waitForElementNotExist("climateControllStartProgressLabel",180);
        long passedTimeInSeconds = watch.time(TimeUnit.SECONDS);
        logMessageToConsole("Climate control starts in " + passedTimeInSeconds + " seconds");
        assertGraphicElementExist("klima_on");

        watch.start();
        waitForClickable("climateControllStopButton");
        waitForElementExist("climateControllStartButton");
        passedTimeInSeconds = watch.time(TimeUnit.SECONDS);
        logMessageToConsole("Climate control stops in " + passedTimeInSeconds + " seconds");
        assertGraphicElementExist("klima_off");

        return true;
    }

}
