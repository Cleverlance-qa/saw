package testbody.skoda;

import corehelpers.DataReader;
import corehelpers.helpers.MethodHelperMobile;
import testhelpers.Skoda.MySkoda.MySkodaGeneralHelper;

public class MySkodaChargeLimit extends MethodHelperMobile {

    public MySkodaChargeLimit(DataReader dataReader) {     //Konstruktor tridy. Pomaha definovat co se stane pri vytvoreni tridy. V tomto pripade dojde k predani informace ulozene v datareader promene.
        super(dataReader);
    }

    public boolean testRun(String carModel) {
        /*MySkodaGeneralHelper generalHelper = new MySkodaGeneralHelper(dataReader);
        generalHelper.loginNoReset(carModel);
        generalHelper.selectFromCarMenu("Nabíjení baterie");

        waitForClickable("chargingLimitMenuLabel");
        waitForElementExist("chargingLimitPageTitle");
        setChargingLimitToFull();
        clickOnElement("chargingLimitMinusButton");
        clickOnElement("chargingLimitMinusButton");
        waitForElementExist("chargingLimit80").clickOnElement("chargingLimitSaveButton");

        dialogPause();
        waitForClickable("chargingLimitMenuLabel");
        waitForElementExist("chargingLimit80");*/

        return true;
    }

    public void setChargingLimitToFull() {
        while (!verifyElementExist("chargingLimit100")) {
            clickOnElement("chargingLimitPlusButton");
            waitSomeTime(500);
        }
    }
}
