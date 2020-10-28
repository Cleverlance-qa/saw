package samples;

import corehelpers.datapools.DataSetIO;
import org.junit.Test;

public class DataSetExamples {

    @Test
    public void readTest() {
          //CTENI POUZE JEDNOHO KONKRETNIHO RADKU.
          DataSetIO dataSetIO = new DataSetIO("dataset", "Test2");
          System.out.println(dataSetIO.getColumnValue("Jmeno"));

          /*CTENI CELEHO DATASETU, POUZITI POSLEDNI HODNOTY.
          Radek je pouzit posledni v datasetu. Po pouziti se smaze.
           */
          DataSetIO dataSetIOSecond = new DataSetIO("dataset");
          System.out.println(dataSetIOSecond.getColumnValue("RC"));
          dataSetIOSecond.deleteLastRow();

          /*ITEROVANE CTENI Z DATASETU.
          Pocet iteraci je stejny jako pocet radku v datasetu.
          Name radek v datasetu musi byt ciselna rada.
           */
          for(int i = 1; i<=new DataSetIO("dataset").getRowCount(); i++){
              DataSetIO dataSet = new DataSetIO("dataset", Integer.toString(i));


              //Sem patri kod s testem ktery se ma iterovat
          }
    }
}
