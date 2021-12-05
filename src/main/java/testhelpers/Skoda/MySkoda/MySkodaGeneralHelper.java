package testhelpers.Skoda.MySkoda;

import corehelpers.DataReader;
import corehelpers.helpers.MethodHelperMobile;

public class MySkodaGeneralHelper extends MethodHelperMobile {

    public MySkodaGeneralHelper(DataReader dataReader) {     //Konstruktor tridy. Pomaha definovat co se stane pri vytvoreni tridy. V tomto pripade dojde k predani informace ulozene v datareader promene.
        super(dataReader);
    }

    public void login(String username, String password, String carModel) {  //Vlastni helper metoda
        waitForElementExist("loginEmail").sendKeys("loginEmail", username);
        clickOnElement("nextButton");
        waitForElementExist("loginPassword").sendKeys("loginPassword", password);
        clickOnElement("nextButton");
        waitForElementExist("welcomeFirstPageLogo");
        swipeLeft();
        swipeLeft();
        waitForClickable("welcomeCloseButton");
        waitForClickable("garageButton");
        waitForElementExist("garageScreenLabel");
        setElementParameter(carModel);
        clickOnElement("garageCarSelectByLabel");
    }

    public void loginNoReset(String carModel) {
        waitForClickable("garageButton");
        waitForElementExist("garageScreenLabel", 60);
        setElementParameter(carModel);
        clickOnElement("garageCarSelectByLabel");
    }

    public void selectFromCarMenu(String menu) {
        setElementParameter(menu);
        waitForClickable("carMenuByLabel",60);
    }

    public void nejakaMetoda() {

        waitForClickable("carMenuByLabel",60);
    }

}
