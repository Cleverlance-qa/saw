package corehelpers.testbase;

/**
 * Common class for all test bases
 */
public interface TestBase {
     /**
      * Final test method. Close drivers, make reports etc.
      * @param success - true is test passed
      */
     void quitDriver(boolean success);

     /**
      * Final test method. Close drivers, make reports etc.
      * @param success - true is test passed
      * @param testBodyName - name of test body, will be used in grafana reports
      */
     void quitDriver(boolean success, String testBodyName);

     /**
      * Will take a screenshot at the end of the test
      */
     void takeScreenshot();

     /**
      * Take a final screenshot
      * @param testBodyName - name of test body
      */
     void takeScreenshot(String testBodyName);

     /**
      * Set which properties xml file should be load
      * @param propertiesPath - path to properties file
      */
     void setProperties(String propertiesPath);

     /**
      * Set which grafana properties xml file should be load
      * @param propertiesPath - path to grafana properties file
      */
     void setGrafanaProperties(String propertiesPath);

     /**
      * Return last child of package name
      * @param c
      * @return name
      */
     String getSimplePackageName(Class c);
}

