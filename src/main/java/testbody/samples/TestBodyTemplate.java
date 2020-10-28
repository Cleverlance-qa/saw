package testbody.samples;

import corehelpers.DataReader;
import corehelpers.helpers.MethodHelperDesktop;

public class TestBodyTemplate extends MethodHelperDesktop {

    public TestBodyTemplate(DataReader dataReader) {     //Konstruktor tridy. Pomaha definovat co se stane pri vytvoreni tridy. V tomto pripade dojde k predani informace ulozene v datareader promene.
        super(dataReader);
    }

    public boolean testRun() {
        //Sem piste kod testu

        return true;
    }
}
