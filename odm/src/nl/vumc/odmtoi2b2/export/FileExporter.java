/**
 * Copyright(c) 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.odmtoi2b2.export;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * For each study, there is one FileExporter object. This class supports exporting ODM data to four files in i2b2
 * format:
 * 1) the concept map file,
 * 2) the columns file,
 * 3) the word map file, and
 * 4) the clinical data file.
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blondé</a>
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class FileExporter {
    /**
     * The logger for this class.
     */
    private static final Log logger = LogFactory.getLog(FileExporter.class);

    /**
     * A column header in the columns file and the wordmap file.
     */
    private static final String COLUMN_NUMBER = "Column Number";

    /**
     * A column header in the columns file and the wordmap file.
     */
    private static final String FILENAME = "Filename";

    /**
     * The column identifier of the very first column, which contains the subject identifiers.
     */
    private static final String FIRST_COLUMN_ID_WITH_SUBJECT_IDS = "firstColumnIdWithSubjectIds";

    /**
     * The directory where the export files will be written to.
     */
    private final String exportFilePath;

    /**
     * The name of the study.
     */
    private final String studyName;

    /**
     * The writer for writing the columns file.
     */
    private BufferedWriter columnsWriter;

    /**
     * Is set to true right after the column number was increased.
     */
    private boolean increasedColumnNumber;

    /**
     * Whether the line with the word map headers still has to be written to file.
     */
    private boolean writeWordMapHeaders;

    /**
     * The writer for writing the word map file.
     */
    private BufferedWriter wordMapWriter;

    /**
     * The value that is written to the clinical data file, instead of the words in the word map file,
     * which maps these values with words.
     */
    private int valueCounter;

    /**
     * The name of the clinical data file.
     */
    private String clinicalDataFileName;

    /**
     * The writer for exporting the clinical data file.
     */
    private BufferedWriter clinicalDataWriter;

    /**
     * The column headers for the clinical data.
     */
    private List<String> columnHeaders;

    /**
     * The column IDs (paths) for the clinical data.
     */
    private List<String> columnIds;

    /**
     * The patient IDs (SubjectKeys), which correspond to the rows in the clinical data.
     */
    private List<String> patientIds;

    /**
     * The current column number during the processing of the study info.
     */
    private int currentColumnNumber;

    /**
     * The current column id during the processing of the study info.
     */
    private String currentColumnId;

    /**
     * Mapping of (column ID + word) to values for the current patient.
     */
    private Map<String, String> wordMap;

    /**
     * A map of maps: Map<patientID, patientData>, with patientData a map of columnIds to data values.
     */
    private Map<String, Map<String, String>> clinicalDataMap;

    /**
     * Construct a file exporter.
     *
     * @param exportFilePath the directory for the export files.
     * @param studyName      the name of the study.
     * @throws IOException when creating the file fails.
     */
    public FileExporter(final String exportFilePath, final String studyName) throws IOException {
        final String studyNameWithUnderscores = studyName.replace(' ', '_');
        final String columnsFileName = studyNameWithUnderscores + "_columns.txt";
        final String wordMapFileName = studyNameWithUnderscores + "_word_map.txt";
        this.clinicalDataFileName = studyNameWithUnderscores + "_clinical_data.txt";
        this.exportFilePath = exportFilePath;
        this.studyName = studyNameWithUnderscores;
        this.writeWordMapHeaders = true;
        this.valueCounter = 1;
        this.increasedColumnNumber = false;
        this.currentColumnNumber = 1;
        this.currentColumnId = null;
        this.columnHeaders = new ArrayList<>();
        columnHeaders.add(studyName + "_SUBJ_ID");
        this.columnIds = new ArrayList<>();
        columnIds.add(FIRST_COLUMN_ID_WITH_SUBJECT_IDS);
        this.patientIds = new ArrayList<>();
        this.wordMap = new HashMap<>();
        this.clinicalDataMap = new HashMap<>();
        setColumnsName(columnsFileName);
        setWordMapName(wordMapFileName);
        setClinicalDataName(this.clinicalDataFileName);
    }

    /**
     * Set the output filename for the columns metadata file.
     *
     * @param columnsFileName the output filename.
     */
    private void setColumnsName(final String columnsFileName) {
        try {
            columnsWriter = new BufferedWriter(new FileWriter(exportFilePath + columnsFileName));
            logger.info("Writing columns to file " + exportFilePath + columnsFileName);
        } catch (final IOException e) {
            logger.error("Error while setting the columns filename.", e);
        }
    }

    /**
     * Set the output filename for the wordmap metadata file.
     *
     * @param wordMapFileName the output filename.
     */
    private void setWordMapName(final String wordMapFileName) {
        try {
            wordMapWriter = new BufferedWriter(new FileWriter(exportFilePath + wordMapFileName));
            logger.info("Writing word mappings to file " + exportFilePath + wordMapFileName);
        } catch (final IOException e) {
            logger.error("Error while setting the wordmap filename.", e);
        }
    }

    /**
     * Set the output filename for the clinical data file.
     *
     * @param clinicalDataFileName The output filename.
     */
    private void setClinicalDataName(final String clinicalDataFileName) {
        try {
            clinicalDataWriter = new BufferedWriter(new FileWriter(exportFilePath + clinicalDataFileName));
            logger.info("Writing clinical data to file " + exportFilePath + clinicalDataFileName);
        } catch (final IOException e) {
            logger.error("Error while setting the clinical data filename.", e);
        }
    }

    /**
     * Write the columns file: first the clinical data file name, then the path as specified in the second column of the
     * user's input concept map without the last node, then the column number and then the last node of the path.
     *
     * @param namePath The full path of names, without the last node.
     *                 This is used as column identifier in tranSMART.
     * @param preferredItemName The name of the last node in the concept tree.
     * @param oidPath The full path of OID's, which provides a unique identifier for the columns.
     * @throws IOException An input-output exception.
     */
    public void writeExportColumns(final String namePath, final String preferredItemName, final String oidPath)
            throws IOException {
        if (currentColumnNumber == 1) {
            final List<String> rowAsList = new ArrayList<>();
            rowAsList.add(FILENAME);
            rowAsList.add("Category Code");
            rowAsList.add(COLUMN_NUMBER);
            rowAsList.add("Data Label");
            rowAsList.add("Data Label Source");
            rowAsList.add("Control Vocab Cd");
            writeCSVData(columnsWriter, rowAsList);
            // This first data line is required by tranSMART.
            final List<String> rowAsList2 = new ArrayList<>();
            rowAsList2.add(clinicalDataFileName);
            rowAsList2.add("");
            rowAsList2.add("1");
            rowAsList2.add("SUBJ_ID");
            rowAsList2.add("");
            rowAsList2.add("");
            writeCSVData(columnsWriter, rowAsList2);
        }
        currentColumnNumber++;
        increasedColumnNumber = true;
        final List<String> rowAsList = new ArrayList<>();
        rowAsList.add(clinicalDataFileName);
        rowAsList.add(namePath);
        rowAsList.add(currentColumnNumber + "");
        rowAsList.add(preferredItemName);
        rowAsList.add("");
        rowAsList.add("");
        writeCSVData(columnsWriter, rowAsList);
        currentColumnId = oidPath;
        columnHeaders.add(studyName + "_" + preferredItemName);
        columnIds.add(oidPath);
    }

    /**
     * Write the word mapping file: first the clinical data file name, then the column number, then the data value,
     * and then the mapped word.
     *
     * @param wordValue The possible values for those columns for which the values are mapped to a number.
     * @throws IOException An input-output exception.
     */

    public void writeExportWordMap(final String wordValue) throws IOException {
        if (writeWordMapHeaders) {
            final List<String> rowAsList = new ArrayList<>();
            rowAsList.add(FILENAME);
            rowAsList.add(COLUMN_NUMBER);
            rowAsList.add("Original Data Value");
            rowAsList.add("New Data Values");
            writeCSVData(wordMapWriter, rowAsList);
            writeWordMapHeaders = false;
        }
        if (increasedColumnNumber) {
            valueCounter = 1;
            increasedColumnNumber = false;
        } else {
            valueCounter++;
        }
        final String value = String.valueOf(valueCounter);
        wordMap.put(currentColumnId + wordValue, value);
        final List<String> rowAsList = new ArrayList<>();
        rowAsList.add(clinicalDataFileName);
        rowAsList.add(currentColumnNumber + "");
        rowAsList.add(valueCounter + "");
        rowAsList.add(wordValue);
        writeCSVData(wordMapWriter, rowAsList);
    }

    /**
     * Write the clinical data to a tab-delimited text file.
     *
     * @param columnId The full path of OID's, which identifies a column.
     * @param dataValue The value, which might not yet be converted to a number.
     * @param patientNum The identifier of the patient (aka subject).
     */
    public void writeExportClinicalDataInfo(final String columnId,
                                            final String dataValue,
                                            final String patientNum) {

        /**
         * Mapping of column ID to values for the current patient.
         */
        Map<String, String> patientData = new HashMap<>();

        if (clinicalDataMap.containsKey(patientNum)) {
            patientData = clinicalDataMap.get(patientNum);
        } else {
            patientIds.add(patientNum);
            patientData.put(FIRST_COLUMN_ID_WITH_SUBJECT_IDS, patientNum);
            clinicalDataMap.put(patientNum, patientData);
        }

        if (wordMap.get(columnId + dataValue) != null) {
            //fills clinical data with words from wordmap
            patientData.put(columnId, wordMap.get(columnId + dataValue));
        } else {
            patientData.put(columnId, dataValue);
        }

        logger.debug("Adding patient data for " + patientNum);
        clinicalDataMap.put(patientNum, patientData);
    }

    /**
     * Write the clinical data, which was kept in the memory, to the clinical data file.
     * @throws IOException An input-output exception.
     */
    public void writePatientData() throws IOException {
        writeCSVData(clinicalDataWriter, columnHeaders);
        for (final String patientId : patientIds) {
            final List<String> rowAsList = new ArrayList<>();
            final Map<String, String> patientData = clinicalDataMap.get(patientId);
            for (final String columnId : columnIds) {
                rowAsList.add(patientData.get(columnId));
            }
            writeCSVData(clinicalDataWriter, rowAsList);
        }
    }

    /**
     * Write one line of tab separated data to the correct file.
     * @param writer The correct file.
     * @param rowAsList The line as a list of items that will be separated by tabs.
     * @throws IOException An input-output exception.
     */
    private static void writeCSVData(final BufferedWriter writer, final List<String> rowAsList) throws IOException {
        final CSVWriter csvWriter = new CSVWriter(writer, '\t', CSVWriter.NO_QUOTE_CHARACTER);
        final String[] rowAsArray = rowAsList.toArray(new String[rowAsList.size()]);
        csvWriter.writeNext(rowAsArray);
    }

    /**
     * Close the export files.
     */
    public void close() {
        try {
            writePatientData();
            columnsWriter.close();
            wordMapWriter.close();
            clinicalDataWriter.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * For debugging: write all the data fields that would be written to the i2b2 database in text-form.
     *
     * @param dataObject The data object that is prepared for loading to the database.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void writeExportDataObject(final Object dataObject) {
        final String className = dataObject.getClass().getName();
        logger.info("[I2B2ODMStudyHandler] " + className.substring(className.lastIndexOf('.') + 1) + ":");
        try {
            for (Field field : dataObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                logger.info("- " + field.getName() + ": " + field.get(dataObject));
            }
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
        logger.info("");
    }
}
