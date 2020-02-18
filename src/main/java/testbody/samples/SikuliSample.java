package testbody.samples;

import corehelpers.DataReader;
import corehelpers.helpers.MethodHelperSikuli;
import org.sikuli.script.Key;

public class SikuliSample extends MethodHelperSikuli {

    public SikuliSample(DataReader dataReader) {     //Konstruktor tridy. Pomaha definovat co se stane pri vytvoreni tridy. V tomto pripade dojde k predani informace ulozene v datareader promene.
        super(dataReader);
    }

    public boolean testRun() {    //Konstrukce metody ve ktere je ulozen samotny kod/script testu.
        waitForElementExist("WinLogo");
        doRightClick("WinLogo");
        clickOnElement("WinLogo");

        waitForElementExist("WinSettings");
        getSikuliScreen().type(Key.ESC);
        waitForElementNotExist("WinSettings");
        return true;
    }

}