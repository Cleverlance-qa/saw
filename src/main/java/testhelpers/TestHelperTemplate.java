package testhelpers;

import corehelpers.DataReader;
import corehelpers.helpers.MethodHelperDesktop;

public class TestHelperTemplate extends MethodHelperDesktop {

    public TestHelperTemplate(DataReader dataReader) {     //Konstruktor tridy. Pomaha definovat co se stane pri vytvoreni tridy. V tomto pripade dojde k predani informace ulozene v datareader promene.
        super(dataReader);
    }

    public void sampleHelperMethod() {  //Vlastni helper metoda
        //Sem piste kod testu
    }

}
