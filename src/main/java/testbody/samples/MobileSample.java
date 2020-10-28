package testbody.samples;

import corehelpers.DataReader;
import corehelpers.helpers.MethodHelperMobile;

public class MobileSample extends MethodHelperMobile {

    public MobileSample(DataReader dataReader) {     //Konstruktor tridy. Pomaha definovat co se stane pri vytvoreni tridy. V tomto pripade dojde k predani informace ulozene v datareader promene.
        super(dataReader);
    }

    public boolean testRun() {    //Konstrukce metody ve ktere je ulozen samotny kod/script testu.
        waitForElementExist("googleMenu_");
        clickOnElement("googleMenu_");
        waitForElementExist("googleMenuTraffic");
        logMessageToConsole(getText("googleMenuTraffic"));
        swipeFromTo("googleMenuTraffic", "googleMenuMessages");
        goBack();

        return true;
    }
}

