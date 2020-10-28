package corehelpers.datapools;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for test reporting to excel
 */
public class ReportExcel {

    private static final String REPORT_PATH = "TestOutputs/TestReport.xls";

    /**
     * Write test results to excel in TestOutputs
     * @param testName - name of test
     * @param testOK - test result
     * @throws IOException - when problem with writing
     */
    public void writeTest(String testName, boolean testOK) throws IOException {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date date = new Date();
        final FileInputStream fsIP = new FileInputStream(new File(REPORT_PATH));
        try(HSSFWorkbook wb = new HSSFWorkbook(fsIP)) {
            final HSSFSheet worksheet = wb.getSheetAt(0);
            Cell cellResult = null;
            Cell cellTimestamp = null;
            Cell cellHyperlink = null;
            // Write value to cells
            cellResult = worksheet.getRow(findRow(worksheet, testName)).getCell(2);
            cellTimestamp = worksheet.getRow(findRow(worksheet, testName)).getCell(3);
            cellHyperlink = worksheet.getRow(findRow(worksheet, testName)).getCell(4);
            cellHyperlink.setCellValue("Screenshot");
            final HSSFHyperlink link = wb.getCreationHelper().createHyperlink(HyperlinkType.URL);
            link.setAddress("TestOutputs\\Screenshots\\" + testName + ".png");
            cellHyperlink.setHyperlink((HSSFHyperlink) link);

            // Get current cell value and overwrite the value
            cellResult.setCellValue(getStatus(testOK));
            cellTimestamp.setCellValue(dateFormat.format(date));
            fsIP.close();
            final FileOutputStream outputFile = new FileOutputStream(new File(REPORT_PATH));
            wb.write(outputFile);
            outputFile.close();
        }
    }

    private static int findRow(HSSFSheet sheet, String cellContent) {
        for (final Row row : sheet) {
            for (final Cell cell : row) {
                if (cell.getCellTypeEnum() == CellType.STRING && cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
                    return row.getRowNum();
                }
            }
        }
        return 0;
    }

    private static String getStatus(boolean failed) {
        if(failed) {
            return "OK";
        } else {
            return "NOK";
        }
    }
}
