package corehelpers.datapools;

import corehelpers.crypting.Decryptor;
import corehelpers.crypting.DecryptorImpl;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static corehelpers.constants.SAWConstants.TESTINPUTS;


/**
 * Class for reading and writing from/to xls files
 */
public class DataSetIO {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String ERROR_FILE_NOT_FOUND = "File not found!";
    private static final String SUFFIX_XLS = ".xls";
    private static final String TC_ROW_NAME = "Name";
    private FileInputStream dataSetStream;
    private HSSFWorkbook excelWorkbook;
    private HSSFSheet excelWorksheet;
    private HSSFRow labelsRow;
    private HSSFRow tcRow;
    private final List<String> rowLabels = new ArrayList<>();
    private final List<String> rowValues = new ArrayList<>();
    private final HashMap<String, String> rowData = new HashMap<>();
    private final HashMap<String, Integer> rowDataIndex = new HashMap<>();
    private final String dataSetName;
    private Decryptor decryptor;

    /**
     * Constructor for standart excel dataset. Load specific row.
     * @param dataSetName - name of dataset excel
     * @param rowName - name of row with data for current test
     */
    public DataSetIO(String dataSetName, String rowName) {
        this.dataSetName = dataSetName;
        decryptor = new DecryptorImpl();
        try {
            dataSetStream = new FileInputStream(TESTINPUTS + dataSetName + SUFFIX_XLS);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, ERROR_FILE_NOT_FOUND,e);
        }
        loadDataRow(rowName);
    }

    /**
     * Constructor for dataset read from last row
     * @param dataSetName - name of dataset excel
     */
    public DataSetIO(String dataSetName) {
        this(dataSetName, null);
    }

    /**
     * Load data from current row to hashmap
     * @param rowName - name of row with data for current test
     */
    private void loadDataRow(String rowName) {
        try {
            excelWorkbook = new HSSFWorkbook(dataSetStream);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_FILE_NOT_FOUND, e);
        }
        excelWorksheet = excelWorkbook.getSheetAt(0);

        saveColumnNames();
        saveRowValues(rowName);

        for(int i = 0; i < rowLabels.size(); i++) {
            rowData.put(rowLabels.get(i), decryptor.decryptString(rowValues.get(i)));
        }

        for(int i = 0; i < rowLabels.size(); i++) {
            rowDataIndex.put(rowLabels.get(i), i);
        }
    }

    private void saveColumnNames() {
        labelsRow = excelWorksheet.getRow(findRowByTCName(TC_ROW_NAME));
        final Iterator cellLabelIterator = labelsRow.cellIterator();
        while (cellLabelIterator.hasNext()) {
            final HSSFCell cell = (HSSFCell) cellLabelIterator.next();
            cell.setCellType(CellType.STRING);
            rowLabels.add(cell.getRichStringCellValue().toString());
        }
    }

    private void saveRowValues(String rowName) {
        if(rowName == null) {
            tcRow = excelWorksheet.getRow(excelWorksheet.getLastRowNum());
        } else {
            tcRow = excelWorksheet.getRow(findRowByTCName(rowName));
        }
        final Iterator cellIterator = tcRow.cellIterator();
        while (cellIterator.hasNext()) {
            final HSSFCell cell = (HSSFCell)cellIterator.next();
            cell.setCellType(CellType.STRING);
            rowValues.add(cell.getRichStringCellValue().toString());
        }
    }

    /**
     * Return index for row based on tcname
     * @param tcname - name of row. Should be name of test.
     * @return - row index
     */
    private int findRowByTCName(String tcname) {
        for (final Row row : excelWorksheet) {
            final Cell cell = row.getCell(0);
            cell.setCellType(CellType.STRING);
            if (cell.getCellTypeEnum() == CellType.STRING && cell.getRichStringCellValue().getString().trim().equals(tcname)) {
                return row.getRowNum();
            }
        }
        return 0;
    }

    /**
     * Return column value
     * @param columnName - name of column
     * @return - string column value
     */
    public String getColumnValue(String columnName) {
        return rowData.get(columnName);
    }

    /**
     * Delete last non-empty excel row
     */
    public void deleteLastRow() {
        final int lastRowNum = excelWorksheet.getLastRowNum();
        final HSSFRow removingRow = excelWorksheet.getRow(lastRowNum);
        excelWorksheet.removeRow(removingRow);
        try(FileOutputStream outputFile = new FileOutputStream(new File(TESTINPUTS + dataSetName + SUFFIX_XLS))) {
            excelWorkbook.write(outputFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error when deleting last row", e);
        }
    }

    /**
     * Return number of excel rows
     * @return number of rows
     */
    public int getRowCount() {
        return excelWorksheet.getLastRowNum();
    }

    /**
     * Write value to cell based on column
     * @param columnName - given column
     * @param value - string value
     */
    public void writeColumnValue(String columnName, String value) {
        final Cell valueCell = tcRow.getCell(rowDataIndex.get(columnName));
        valueCell.setCellValue(value);
        try(FileOutputStream outputFile = new FileOutputStream(new File(TESTINPUTS + dataSetName + SUFFIX_XLS))) {
            excelWorkbook.write(outputFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error when writing column value", e);
        }
    }
}
