package testbody.samples;

import corehelpers.DataReader;
import corehelpers.datapools.DataSetCsvIO;
import corehelpers.helpers.MethodHelperDesktop;
import org.openqa.selenium.WebElement;

import java.util.List;

public class Sample extends MethodHelperDesktop {

    public Sample(DataReader dataReader) {     //Konstruktor tridy. Pomaha definovat co se stane pri vytvoreni tridy. V tomto pripade dojde k predani informace ulozene v datareader promene.
        super(dataReader);
    }

    public boolean testRun() {
        //Otevreni google a vyhledani cleverlance
        openURL(baseURL);
        sendKeys("googleSearchField", "cleverlance");
        waitForClickable("googleSearchButton");

        //Cekani na zobrazeni vysledku a proklik na stranky cleverlance
        waitForClickable("googleCleverlance");

        //Cekani na nacteni cleverlance stranek a proklik do TaaS sekce pres vysunovaci menu
        waitForElementExist("cleverlanceProductMenu");
        hoverMouseOverElement("cleverlanceProductMenu").waitForClickable("cleverlanceProductMenuTaaS");

        //Nacteni testing sekce a proklik na kontakty
        waitForClickable("cleverlanceTaasContact");

        //Nacteni vsech odkazu na socialni media a ulozeni do csv
        DataSetCsvIO dataset = new DataSetCsvIO("cleverlanceDemo");
        dataset.setOutputFolder("TestOutputs/").setDataSetName("vyhledanaMedia").clearAllData();
        List<WebElement> media = getMultipleElements("cleverlanceSocialMedias");
        for(WebElement element : media){
            dataset.addNewLine(element.getAttribute("class"), element.getAttribute("href"));
        }

        //Vypise a ulozi browser javascript konzoli
        getBrowserConsoleLog();

        return true;
    }
}