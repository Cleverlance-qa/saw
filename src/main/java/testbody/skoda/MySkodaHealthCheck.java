package testbody.skoda;

import corehelpers.DataReader;
import corehelpers.helpers.MethodHelperDesktop;
import corehelpers.helpers.MethodHelperMobile;
import testhelpers.Skoda.MySkoda.MySkodaGeneralHelper;

public class MySkodaHealthCheck extends MethodHelperMobile {

    public MySkodaHealthCheck(DataReader dataReader) {     //Konstruktor tridy. Pomaha definovat co se stane pri vytvoreni tridy. V tomto pripade dojde k predani informace ulozene v datareader promene.
        super(dataReader);
    }

    public boolean testRun(String carModel) {
        MySkodaGeneralHelper generalHelper = new MySkodaGeneralHelper(dataReader);
        generalHelper.loginNoReset(carModel);
        waitForClickable("prohlidkaMenuButton");
        generalHelper.selectFromCarMenu("Zdravotní karta vozu");
        waitForElementExist("prohlidkaNoProblemLabel");

        String miles = getText("prohlidkaMilesCounter");
        miles = miles.replaceAll("\\D+","");  //odstrani z hodnoty vsechny mezery
        assertImageExistOnScreen("svetla");
        assertImageExistOnScreen("motor");
        assertImageExistOnScreen("pneumatiky");
        assertImageExistOnScreen("brzdy");
        swipeUp();
        dialogPause();
        assertImageExistOnScreen("komfort");
        assertImageExistOnScreen("asistence");
        assertImageExistOnScreen("ostatni");
        assertImageExistOnScreen("nezarazeno");
        clickOnElement("prohlidkaDownloadButton");
        setElementParameter(miles);
        waitForElementExist("prohlidkaReportTest");

        return true;
    }
}
