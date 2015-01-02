/**
 * Copyright(c) 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.odmtoi2b2.export;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * File exporter for the I2B2 "light" data model. All the data for a single patient will be written into a single line
 * of the file. Extra columns are generated for repeating data. The order of the columns is of importance to know for
 * repeating data, to which repeat and/or event the data in some column belongs. Consider the following 22 columns:
 *
 * 1  2  3  4  5     6  7  8    9  10 11    12 13 14    15  16  17   18  19  20    21  22
 * P1 c1 c2 c3 IG1-1 c4 c5 E1-1 c6 c7 IG2-1 c8 c9 IG2-2 c10 c11 E2-1 c12 c13 IG3-1 c14 c15
 *
 * Pn    = patient n
 * cn    = column n that contains data
 * IGn-m = repetition m of the item group of type n
 * En-m  = repetition m of the event      of type n
 *
 * Columns c1, c2 and c3 contain data that are not repeating data. They are associated to patient 1, but not to any event
 * or item group. Columns c4 and c5 contain repeating data, belonging to the first repeat of the item group of type 1.
 * It is important to notice that no events are mentioned to the left of IG1, which means that item group of type 1 does
 * not belong to any event. Columns c6 and c7 are data that belong to the first repetition of the event of type1, but do
 * not belong to any item group. Columns c8, c9, c10 anc c11 belong to item group of type 2 and to event of type 1. They
 * all belong to the first repetition of event type 1, but c10 and c11 belong to the second repetition of the item group
 * of type 2. Columns c12 and c13 belong to event type 2, but not to any item group. Columns c14 and c15 also belong to
 * event type 2, but in addition, they also belong to item group of type 3.
 *
 * As an example, event type 2 can be a doctor's visit, item group of type 3 can be a temperature measurement, c14 can
 * be the value of the measurement (e.g. 37 or 101) and c15 can be the unit of the measurement (e.g. Celcius or
 * Fahrenheit)
 *
 * In conclusion, for each column you have to walk to the left until you find an event type or a patient. If one or more
 * item group types were encountered during that walk, the first encountered item group (so the most to the right)
 * counts.
 *
 * In order to fit this format in a map-of-maps-structure, an event repetition of type zero is created, which contains the
 * patient data and item group data that have no associated event. This event type zero has maximally one repetition.
 * Then the patient data contains a series of event repetitions, each of which has a type and a repetition number.
 * Again, in each event repetition map we create a - possibly empty - item group repetition of type zero, which
 * contains the event data that have no associated item group. Then the event data contains a series of item group
 * type maps.
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blondé</a>
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class FileExporterLight implements FileExporter {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(FileExporterFull.class);

    /**
     * A column header in the columns file and the word map file.
     */
    private static final String COLUMN_NUMBER = "Column Number";

    /**
     * A column header in the columns file and the word map file.
     */
    private static final String FILENAME = "Filename";

    /**
     * The separator that tranSMART expects to separate the concept names in the column name path.
     */
    private static final String SEPARATOR = "+";

    /**
     * The separator that tranSMART expects to separate the concept names in the column name path,
     * like it is called in a regular expression.  Remove the "\\"
     * in case a separator is chosen that does not require to be preceded with \ in a regular expression.
     */
    private static final String SEPARATOR_IN_REGEX = "\\" + SEPARATOR;

    /**
     * The string by which the separator has to be replaced in case it occurs in the middle of a concept.
     */
    private static final String SEPARATOR_REPLACEMENT = " and ";

    /**
     * A space for usage between the words in the log statements.
     */
//    private static final String SPACE = " ";

    /**
     * The character encoding used for all the files.
     */
    private static final String UTF8 = "UTF-8";

    /**
     * The regex that specifies all the symbols that should not appear in the output file.
     */
    private String forbiddenSymbolRegex;

    /**
     * The column identifier of the very first column, which contains the patient identifiers.
     */
    private static final String FIRST_COLUMN_ID_WITH_PATIENT_IDS = "firstColumnIdWithPatientIds";

    /**
     * The boolean that is true when precautions have to be taken to avoid bugs caused by
     * special symbols in tranSMART.
     */
    private boolean avoidTransmartSymbolBugs;

    /**
     * The directory where the export files will be written to.
     */
    private final String exportFilePath;

    /**
     * The writer for writing the columns file.
     */
    private Writer columnsWriter;

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
    private Writer wordMapWriter;

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
     * The patient IDs.
     */
    private List<String> patientIds;

    /**
     * The column headers for the clinical data.
     */
    private List<String> columnHeaders;

    /**
     * The column IDs (paths) for the clinical data.
     */
    private List<String> columnIds;

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
     * The cut-off length of the clinical data entry strings in the clinical data file.
     */
    private int maxClinicalDataEntry;


    /**
     * Construct a file exporter.
     *
     * @param exportFilePath the directory for the export files.
     * @param studyName      the name of the study.
     * @param configuration  a configuration object that is derived from the configuration file.
     * @throws IOException when creating the file fails.
     */
    public FileExporterLight(final String exportFilePath, final String studyName, final Configuration configuration)
            throws IOException {
        final String studyNameWithUnderscores = studyName.replace(' ', '_');
        final String columnsFileName = studyNameWithUnderscores + "_columns.txt";
        final String wordMapFileName = studyNameWithUnderscores + "_word_map.txt";
        this.clinicalDataFileName = studyNameWithUnderscores + "_clinical_data.txt";
        this.exportFilePath = exportFilePath;
        this.maxClinicalDataEntry = configuration.getMaxClinicalDataEntry();
        this.forbiddenSymbolRegex = configuration.getForbiddenSymbolRegex();
        this.avoidTransmartSymbolBugs = configuration.getAvoidTransmartSymbolBugs();
        this.writeWordMapHeaders = true;
        this.valueCounter = 1;
        this.increasedColumnNumber = false;
        this.currentColumnNumber = 0;
        this.currentColumnId = null;
        this.columnHeaders = new ArrayList<>(Arrays.asList("Patient ID"));
        this.columnIds = new ArrayList<>(Arrays.asList("firstColumnIdWithPatientIds"));
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
            final FileOutputStream fileOutputStream = new FileOutputStream(exportFilePath + columnsFileName);
            columnsWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, UTF8));
            logger.info("Writing columns to file " + exportFilePath + columnsFileName);
        } catch (final IOException e) {
            logger.error("Error while setting the columns filename.", e);
        }
    }

    /**
     * For testing purposes.
     *
     * @param columnsWriter the columns writer.
     */
//    protected void setColumnsWriter(final Writer columnsWriter) {
//        this.columnsWriter = columnsWriter;
//    }

    /**
     * For testing purposes.
     *
     * @param wordMapWriter the wordmap writer.
     */
//    protected void setWordMapWriter(final Writer wordMapWriter) {
//        this.wordMapWriter = wordMapWriter;
//    }

    /**
     * Set the output filename for the word map metadata file.
     *
     * @param wordMapFileName the output filename.
     */
    private void setWordMapName(final String wordMapFileName) {
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(exportFilePath + wordMapFileName);
            wordMapWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, UTF8));
            logger.info("Writing word mappings to file " + exportFilePath + wordMapFileName);
        } catch (final IOException e) {
            logger.error("Error while setting the word map filename.", e);
        }
    }

    /**
     * Set the output filename for the clinical data file.
     *
     * @param clinicalDataFileName The output filename.
     */
    private void setClinicalDataName(final String clinicalDataFileName) {
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(exportFilePath + clinicalDataFileName);
            clinicalDataWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, UTF8));
            logger.info("Writing clinical data to file " + exportFilePath + clinicalDataFileName);
        } catch (final IOException e) {
            logger.error("Error while setting the clinical data filename.", e);
        }
    }


    @Override
    public void storeColumn(final String eventName,
                            final String formName,
                            final String itemGroupName,
                            final String preferredItemName,
                            final String oidPath) throws IOException {
        if (currentColumnNumber == 0) {
            handleColumnMetadata(FILENAME, "Category Code", COLUMN_NUMBER, "Data Label", "Data Label Source", "Control Vocab Cd");
        }

        String cleanEventName     = eventName;
        String cleanFormName      = formName;
        String cleanItemGroupName = itemGroupName;
        if (avoidTransmartSymbolBugs) {
            cleanEventName      =      eventName.replaceAll(SEPARATOR_IN_REGEX, SEPARATOR_REPLACEMENT);
            cleanFormName       =       formName.replaceAll(SEPARATOR_IN_REGEX, SEPARATOR_REPLACEMENT);
            cleanItemGroupName  =  itemGroupName.replaceAll(SEPARATOR_IN_REGEX, SEPARATOR_REPLACEMENT);
        }

        String namePath = cleanEventName + SEPARATOR + cleanFormName + SEPARATOR + cleanItemGroupName;

        /**
         * Avoid that blank nodes are created by removing overabundant SEPARATOR symbols.
         */
        while (namePath.contains(SEPARATOR + SEPARATOR)) {
            namePath = namePath.replaceAll(SEPARATOR_IN_REGEX + SEPARATOR_IN_REGEX, SEPARATOR);
        }
        if (namePath.startsWith(SEPARATOR)) {
            namePath = namePath.substring(1);
        }
        if (namePath.endsWith(SEPARATOR)) {
            namePath = namePath.substring(0, namePath.length() - 1);
        }

        handleColumnAttribute(namePath, preferredItemName);

        currentColumnId = oidPath;
        columnHeaders.add(preferredItemName);
        columnIds.add(oidPath);

    }


    /**
     * This method fills a rowAsList list, which represents a line in the columns file, and
     * passes it to the file writer. The actual writing to the hard disk happens when the
     * file writer is closed.
     *
     * @param filename          The filename.
     * @param categoryCode      The category code (readable concept path).
     * @param columnNumber      The column number.
     * @param dataLabel         The data label (item name).
     * @param dataLabelSource   The data label source (not used yet in tranSMART on 2014/09).
     * @param controlVocabCode  The controlled vocabulary code (not used in tM   on 2014/09).
     * @throws IOException      An input-output exception.
     */
    private void handleColumnMetadata(final String filename,
                                      final String categoryCode,
                                      final String columnNumber,
                                      final String dataLabel,
                                      final String dataLabelSource,
                                      final String controlVocabCode) throws IOException {
        final List<String> rowAsList = new ArrayList<>();
        rowAsList.add(filename);
        rowAsList.add(categoryCode);
        rowAsList.add(columnNumber);
        rowAsList.add(dataLabel);
        rowAsList.add(dataLabelSource);
        rowAsList.add(controlVocabCode);
        writeCSVData(columnsWriter, rowAsList);
        currentColumnNumber++;
        increasedColumnNumber = true;
    }


    /**
     * This method fills a rowAsList list, which represents a line in the columns file, and
     * passes it to the file writer. The actual writing to the hard disk happens when the
     * file writer is closed.
     *
     * @param categoryCode      The category code (readable concept path).
     * @param dataLabel         The data label (item name).
     * @throws IOException      An input-output exception.
     */
    private void handleColumnAttribute(final String categoryCode, final String dataLabel) throws IOException {
        handleColumnMetadata(clinicalDataFileName, categoryCode, String.valueOf(currentColumnNumber), dataLabel, "", "");
    }


    @Override
    public void storeWord(final String wordValue) throws IOException {
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
        rowAsList.add(String.valueOf(currentColumnNumber - 1));
        rowAsList.add(value);
        rowAsList.add(wordValue);
        writeCSVData(wordMapWriter, rowAsList);
    }

    @Override
    public void storeClinicalDataInfo(final String columnId,
                                      final String dataValue,
                                      final String patientId,
                                      final String eventId,
                                      final String eventRepeatKey,
                                      final String itemGroupId,
                                      final String itemGroupRepeatKey) {

        if (eventRepeatKey == null && itemGroupRepeatKey == null) {
            addPatientData(columnId, dataValue, patientId);
        }  else if (eventRepeatKey == null) {
//            addItemGroupData(columnId, dataValue, patientId, eventId, itemGroupId, itemGroupRepeatKey);
        } //else if (eventRepeatKey != null && itemGroupRepeatKey == null) {
//            addEventData(columnId, dataValue, patientId, eventId, eventRepeatKey);
//        } else {
//            addEventAndItemGroupData(columnId, dataValue, patientId, eventId,
//                    eventRepeatKey, itemGroupId, itemGroupRepeatKey);
//        }

    }


    /**
     * Write the clinical data to a clinical data map, for the case of a patient.
     *
     * @param columnId The full path of OIDs, which identifies a column.
     * @param dataValue The value, which might not yet be converted to a number.
     * @param patientId The identifier of the patient.
     */
    private void addPatientData(final String columnId, final String dataValue, final String patientId) {
        /**
         * Mapping of column ID to values for the current entity.
         */
        Map<String, String> patientData = new HashMap<>();

        if (clinicalDataMap.containsKey(patientId)) {
            patientData = clinicalDataMap.get(patientId);
        } else {
            patientIds.add(patientId);
            patientData.put(FIRST_COLUMN_ID_WITH_PATIENT_IDS, patientId);
            clinicalDataMap.put(patientId, patientData);
        }

        patientData = addWordOrNumber(columnId, dataValue, patientData);

        logger.debug("Adding patient data for " + patientId);
        clinicalDataMap.put(patientId, patientData);
    }


    /**
     * Adds a word or a number to a field in the clinical data file. Replaces a word by a number in case the
     * word map has assigned such a replacement.
     *
     * @param columnId The column of the field in the clinical data file.
     * @param dataValue The non-replaced data value.
     * @param entityData The data of the row of the field in the clinical data file.
     * @return entityData The data of the row of the field in the clinical data file.
     */
    private Map<String, String> addWordOrNumber(final String columnId,
                                                final String dataValue,
                                                final Map<String, String> entityData) {
        if (wordMap.get(columnId + dataValue) != null) {
            //fills clinical data with words from word map
            entityData.put(columnId, wordMap.get(columnId + dataValue));
        } else {
            entityData.put(columnId, dataValue);
        }
        return entityData;
    }


    /**
     * Write the clinical data, which was kept in the memory, to the tab-delimited clinical data file.
     * @throws java.io.IOException An input-output exception.
     */
    private void writeEntityData() throws IOException {
        writeCSVData(clinicalDataWriter, columnHeaders);
        for (final String patientId : patientIds) {
            final List<String> rowAsList = new ArrayList<>();
            final Map<String, String> patientData = clinicalDataMap.get(patientId);
            for (final String columnId : columnIds) {
                final String rawDataEntry = patientData.get(columnId);
                String dataEntry;
                if (rawDataEntry == null) {
                    dataEntry = "";
                } else if (rawDataEntry.length() > maxClinicalDataEntry) {
                    final String tooLongIndicator = "...";
                    final int logSegmentLength = 15;
                    dataEntry = rawDataEntry.substring(0, maxClinicalDataEntry - tooLongIndicator.length())
                            + tooLongIndicator;
                    logger.warn("Data entry " + dataEntry.substring(0, logSegmentLength) + " of " + patientId
                            + " and column " + columnId + " was cut off at "
                            + rawDataEntry.substring(maxClinicalDataEntry - logSegmentLength,
                            maxClinicalDataEntry - tooLongIndicator.length()));
                } else {
                    dataEntry = rawDataEntry;
                }
                rowAsList.add(dataEntry);
            }
            writeCSVData(clinicalDataWriter, rowAsList);
        }
    }


    /**
     * Write one line of tab separated data to the correct file. Replaces double quotes by single quotes first.
     * @param writer The correct file.
     * @param rowAsList The line as a list of items that will be separated by tabs.
     * @throws IOException An input-output exception.
     */
    private void writeCSVData(final Writer writer, final List<String> rowAsList) throws IOException {
        if (forbiddenSymbolRegex != null && !"".equals(forbiddenSymbolRegex.trim())) {
            for (int i = 0; i < rowAsList.size(); i++) {
                rowAsList.set(i, rowAsList.get(i).replaceAll(forbiddenSymbolRegex, ""));
            }
        }
        if (avoidTransmartSymbolBugs) {
            for (int i = 0; i < rowAsList.size(); i++) {
                rowAsList.set(i, StringUtilities.convertString(rowAsList.get(i)));
            }
        }
        final CSVWriter csvWriter = new CSVWriter(writer, '\t', CSVWriter.NO_QUOTE_CHARACTER);
        final String[] rowAsArray = rowAsList.toArray(new String[rowAsList.size()]);
        csvWriter.writeNext(rowAsArray);
    }


    @Override
    public void close() {
        try {
            writeEntityData();
            columnsWriter.close();
            wordMapWriter.close();
            clinicalDataWriter.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
