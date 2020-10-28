package corehelpers.datapools;

import corehelpers.crypting.Decryptor;
import corehelpers.crypting.DecryptorImpl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static corehelpers.constants.SAWConstants.SUFFIX_CSV;
import static corehelpers.constants.SAWConstants.TESTINPUTS;

/**
 * Class for reading and writing from/to csv files
 */
public class DataSetCsvIO{
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String ERROR_FILE_NOT_FOUND = "File not found!";
    private static final String DEFAULT_DELIMITER = ";";
    private String outputFolder = TESTINPUTS;
    private String dataSetName;
    private String rowName;
    private String cvsSplitBy;
    private final List<CSVRecord> recordsList = new ArrayList<>();
    private final List<String> columnNamesList = new ArrayList<>();
    private String[] columnNames;
    private Decryptor decryptor;

    /**
     * Inicialized csv for specific row and specific separator
     * @param dataSetName - path to csv file
     * @param rowName - specific row name
     * @param cvsSplitBy - csv separator
     */
    public DataSetCsvIO(String dataSetName, String rowName, String cvsSplitBy){
        this.dataSetName = dataSetName;
        this.rowName = rowName;
        this.cvsSplitBy = cvsSplitBy;
        decryptor = new DecryptorImpl();
        initCsv();
    }

    /**
     * Inicialized csv for specific row
     * @param dataSetName - path to csv file
     * @param rowName - specific row name
     */
    public DataSetCsvIO(String dataSetName, String rowName){
        this(dataSetName, rowName, DEFAULT_DELIMITER);
    }

    /**
     * Inicialized csv without specific row. Last row in file will be used.
     * @param dataSetName - path to csv file
     */
    public DataSetCsvIO(String dataSetName) {
        this(dataSetName, null, DEFAULT_DELIMITER);
    }

    /**
     * Get value from chosen columnName
     * @param columnName - name of column
     * @return - value from chosen columnName
     */
    public String getColumnValue(String columnName) {
        if (rowName != null) {
            for (final CSVRecord record : recordsList) {
                if (record.get(0).equals(rowName)) {
                    return record.get(columnName);
                }
            }
        } else {
            return decryptor.decryptString(recordsList.get(recordsList.size()).get(columnName));
        }
        return null;
    }

    /**
     * Return number of csv rows
     * @return number of rows
     */
    public int getRowCount() {
        return recordsList.size();
    }

    /**
     * Write value to cell based on column
     * @param columnName - given column
     * @param value - string value
     */
    public void writeColumnValue(String columnName, String value) {
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFolder + dataSetName + SUFFIX_CSV));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withDelimiter(cvsSplitBy.charAt(0)).withHeader(columnNames))) {
            for (final CSVRecord record : recordsList) {
                if (record.get(0).equals(rowName)) {
                    final List<String> rowToWrite = new ArrayList<>();
                    int i = 0;
                    for (final Iterator<String> iter = record.iterator(); iter.hasNext(); ) {
                        final String element = iter.next();
                        if(columnNames[i].equals(columnName)) {
                            rowToWrite.add(value);
                        } else {
                            rowToWrite.add(element);
                        }
                        i++;
                    }
                    csvPrinter.printRecord(rowToWrite);
                } else {
                    csvPrinter.printRecord(record);
                }
            }
            csvPrinter.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error while writing csv", e);
        }
        initCsv();
    }

    /**
     * Add new line to csv file with data from inputs
     * @param line - data to write
     */
    public void addNewLine(String... line) {
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFolder + dataSetName + SUFFIX_CSV));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withDelimiter(cvsSplitBy.charAt(0)).withHeader(columnNames))) {
            for (final CSVRecord record : recordsList) {
                csvPrinter.printRecord(record);
            }
            csvPrinter.printRecord(line);
            csvPrinter.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error while writing csv", e);
        }
        initCsv();
    }

    private void initCsv() {
        try(Reader in = new FileReader(outputFolder + dataSetName + SUFFIX_CSV);
            Reader inHeaders = new FileReader(outputFolder + dataSetName + SUFFIX_CSV)) {
            final Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().withDelimiter(cvsSplitBy.charAt(0)).parse(in);
            recordsList.clear();
            for (final CSVRecord record : records) {
                recordsList.add(record);
            }
            if(rowName == null) {
                rowName = recordsList.get(getRowCount()-1).get(0);
            }
            final CSVParser csvFileParser = new CSVParser(inHeaders, CSVFormat.EXCEL.withDelimiter(cvsSplitBy.charAt(0)));
            final List<CSVRecord> csvRecords = csvFileParser.getRecords();
            final CSVRecord record = csvRecords.get(0);
            columnNamesList.clear();
            for (final Iterator<String> iter = record.iterator(); iter.hasNext(); ) {
                final String element = iter.next();
                columnNamesList.add(element);
            }
            columnNames = columnNamesList.toArray(new String[columnNamesList.size()]);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE,ERROR_FILE_NOT_FOUND, e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error while reading csv", e);
        }
    }

    /**
     * Clear all data in output csv except first name with column names
     */
    public DataSetCsvIO clearAllData() {
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFolder + dataSetName + SUFFIX_CSV));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withDelimiter(cvsSplitBy.charAt(0)).withHeader(columnNames))) {
            csvPrinter.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error while writing csv", e);
        }
        initCsv();
        return this;
    }

    /**
     * Set output folder for csv saving
     * @param outputFolder - path to output folder
     */
    public DataSetCsvIO setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
        return this;
    }

    /**
     * Set dataset name for csv saving
     * @param dataSetName - new dataset name
     */
    public DataSetCsvIO setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
        return this;
    }
}
