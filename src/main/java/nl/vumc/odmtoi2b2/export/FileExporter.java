/**
 * Copyright(c) 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.odmtoi2b2.export;

import au.com.bytecode.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;

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
    private static final Logger logger = LoggerFactory.getLogger(FileExporter.class);

    /**
     * A column header in the columns file and the word map file.
     */
    private static final String COLUMN_NUMBER = "Column Number";

    /**
     * A column header in the columns file and the word map file.
     */
    private static final String FILENAME = "Filename";

    /**
     * The column identifier of the very first column, which contains the subject identifiers.
     */
    private static final String FIRST_COLUMN_ID_WITH_SUBJECT_IDS = "firstColumnIdWithSubjectIds";

    /**
     * The column identifier of the second column, which contains the type (patient, event, or item group).
     */
    private static final String SECOND_COLUMN_ID_WITH_TYPE = "secondColumnIdWithType";

    /**
     * The column identifier of the third column, which contains the associated-patient IDs.
     */
    private static final String THIRD_COLUMN_ID_WITH_ASSOC_PATIENT_IDS = "thirdColumnIdWithAssocPatientIds";

    /**
     * The column identifier of the fourth column, which contains the associated-event IDs.
     */
    private static final String FOURTH_COLUMN_ID_WITH_ASSOC_EVENT_IDS = "fourthColumnIdWithAssocEventIds";

    /**
     * The column identifier of the fifth column, which contains the event repeat key (nr in the
     * series of repeated events).
     */
    private static final String FIFTH_COLUMN_ID_WITH_EVENT_NR = "fifthColumnIdWithEventNr";

    /**
     * The column identifier of the sixth column, which contains the item group repeat key (nr in the
     * series of repeated item groups).
     */
    private static final String SIXTH_COLUMN_ID_WITH_IG_NR = "sixthColumnIdWithIgNr";

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
     * The regex that specifies all the symbols that should not appear in the output file.
     */
    private String forbiddenSymbolRegex;

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
     * The column headers for the clinical data.
     */
    private List<String> columnHeaders;

    /**
     * The column IDs (paths) for the clinical data.
     */
    private List<String> columnIds;

    /**
     * The subject IDs (either patient id, event id, or IG id), which correspond to the rows in the clinical data.
     */
    private List<String> subjectIds;

    /**
     * The IDs of a repeating event, which are turned into an integer (the place in the list)
     * and then made part of the ID of a repeated event of this repeating event (the eventSubjectId).
     */
    private List<String> repeatingEventIds;

    /**
     * The IDs of a repeating item group, which are turned into an integer (the place in the list)
     * and then made part of the ID of a repeated item group of this repeating item group (the itemGroupSubjectId).
     */
    private List<String> repeatingItemGroupIds;

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
     * @param configuration  todo
     * @throws IOException when creating the file fails.
     */
    public FileExporter(final String exportFilePath, final String studyName, final Configuration configuration)
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
        this.columnHeaders = new ArrayList<>();
        columnHeaders.add("SUBJ_ID");
        columnHeaders.add("TYPE");
        columnHeaders.add("ASSOC_PATIENT_ID");
        columnHeaders.add("ASSOC_EVENT_ID");
        columnHeaders.add("event number");
        columnHeaders.add("repeat number");
        this.columnIds = new ArrayList<>();
        columnIds.add(FIRST_COLUMN_ID_WITH_SUBJECT_IDS);
        columnIds.add(SECOND_COLUMN_ID_WITH_TYPE);
        columnIds.add(THIRD_COLUMN_ID_WITH_ASSOC_PATIENT_IDS);
        columnIds.add(FOURTH_COLUMN_ID_WITH_ASSOC_EVENT_IDS);
        columnIds.add(FIFTH_COLUMN_ID_WITH_EVENT_NR);
        columnIds.add(SIXTH_COLUMN_ID_WITH_IG_NR);
        this.subjectIds = new ArrayList<>();
        this.repeatingEventIds = new ArrayList<>();
        this.repeatingItemGroupIds = new ArrayList<>();
        this.wordMap = new HashMap<>();
        this.clinicalDataMap = new HashMap<>();
        setColumnsName(columnsFileName);
        setWordMapName(wordMapFileName);
        setClinicalDataName(this.clinicalDataFileName);
    }

	/**
	 * Get a copy of the clinical data map. This method is meant for testing purposes.
	 *
	 * @return a copy of the clinical data map.
	 */
	protected Map<String, Map<String, String>> getClinicalDataMap() {
		return new HashMap<>(clinicalDataMap);
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
     * For testing purposes.
     *
     * @param columnsWriter the columns writer.
     */
    protected void setColumnsWriter(final Writer columnsWriter) {
        this.columnsWriter = columnsWriter;
    }

    /**
     * For testing purposes.
     *
     * @param wordMapWriter the wordmap writer.
     */
    protected void setWordMapWriter(final Writer wordMapWriter) {
        this.wordMapWriter = wordMapWriter;
    }

    /**
     * Set the output filename for the word map metadata file.
     *
     * @param wordMapFileName the output filename.
     */
    private void setWordMapName(final String wordMapFileName) {
        try {
            wordMapWriter = new BufferedWriter(new FileWriter(exportFilePath + wordMapFileName));
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
     * @param studyEventName
     * @param formName
     * @param preferredItemName The name of the last node in the concept tree.
     * @param oidPath The full path of OIDs, which provides a unique identifier for the columns.
     * @throws IOException An input-output exception.
     */
    public void writeExportColumns(String studyEventName,
                                   String formName,
                                   String itemGroupName,
                                   final String preferredItemName,
                                   final String oidPath)
            throws IOException {
        if (currentColumnNumber == 0) {
            handleColumnMetadata(FILENAME, "Category Code", COLUMN_NUMBER, "Data Label", "Data Label Source", "Control Vocab Cd");
            handleColumnMetadata(clinicalDataFileName, ""                     , String.valueOf(currentColumnNumber), "SUBJ_ID", "", "");
            handleColumnMetadata(clinicalDataFileName, "Subset selection type", String.valueOf(currentColumnNumber), "type (patient, event or repeat)", "", "");
            handleColumnMetadata(clinicalDataFileName, "Subset selection type", String.valueOf(currentColumnNumber), "associated patient id", "", "");
            handleColumnMetadata(clinicalDataFileName, "Subset selection type", String.valueOf(currentColumnNumber), "associated event id", "", "");
            handleColumnMetadata(clinicalDataFileName, "Subset selection type", String.valueOf(currentColumnNumber), "event number", "", "");
            handleColumnMetadata(clinicalDataFileName, "Subset selection type", String.valueOf(currentColumnNumber), "repeat number", "", "");
        }


        if (avoidTransmartSymbolBugs) {
            studyEventName = studyEventName.replaceAll(SEPARATOR_IN_REGEX, SEPARATOR_REPLACEMENT);
            formName       =       formName.replaceAll(SEPARATOR_IN_REGEX, SEPARATOR_REPLACEMENT);
            itemGroupName  =  itemGroupName.replaceAll(SEPARATOR_IN_REGEX, SEPARATOR_REPLACEMENT);
        }

        String namePath = studyEventName + SEPARATOR + formName + SEPARATOR + itemGroupName;

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
            namePath = namePath.substring(0,namePath.length()-1);
        }

        handleColumnMetadata(clinicalDataFileName, namePath, currentColumnNumber + "", preferredItemName, "", "");

        currentColumnId = oidPath;
        columnHeaders.add(preferredItemName);
        columnIds.add(oidPath);
    }

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
        rowAsList.add(String.valueOf(currentColumnNumber - 1));
        rowAsList.add(value);
        rowAsList.add(wordValue);
        writeCSVData(wordMapWriter, rowAsList);
    }

    /**
     * Write the clinical data to a clinical data map, which is kept in the memory until the moment
     * that everything can be written out to a file in once.
     *
     * @param columnId The full path of OIDs, which identifies a column.
     * @param dataValue The value, which might not yet be converted to a number.
     * @param patientId The identifier of the patient.
     * @param eventId
     * @param eventRepeatKey
     * @param itemGroupId
     * @param itemGroupRepeatKey
     */
    public void writeExportClinicalDataInfo(final String columnId,
                                            final String dataValue,
                                            final String patientId,
                                            final String eventId,
                                            final String eventRepeatKey,
                                            final String itemGroupId,
                                            final String itemGroupRepeatKey) {

        if (eventRepeatKey == null && itemGroupRepeatKey == null) {
            addPatientData(columnId, dataValue, patientId);
        } else if (eventRepeatKey != null && itemGroupRepeatKey == null) {
            addEventData(columnId, dataValue, patientId, eventId, eventRepeatKey);
        } else if (eventRepeatKey == null) {
            addItemGroupData(columnId, dataValue, patientId, eventId, itemGroupId, itemGroupRepeatKey);
        } else {
            addEventAndItemGroupData(columnId, dataValue, patientId, eventId,
                                     eventRepeatKey, itemGroupId, itemGroupRepeatKey);
        }
    }

    private void addPatientData(String columnId, String dataValue, String patientId) {
        /**
         * Mapping of column ID to values for the current subject.
         */
        Map<String, String> subjectData = new HashMap<>();

        if (clinicalDataMap.containsKey(patientId)) {
            subjectData = clinicalDataMap.get(patientId);
        } else {
            subjectIds.add(patientId);
            subjectData.put(FIRST_COLUMN_ID_WITH_SUBJECT_IDS, patientId);
            subjectData.put(SECOND_COLUMN_ID_WITH_TYPE, "patient");
            clinicalDataMap.put(patientId, subjectData);
        }

        subjectData = addWordOrNumber(columnId, dataValue, subjectData);

        logger.debug("Adding subject data for " + patientId);
        clinicalDataMap.put(patientId, subjectData);
    }

    private void addEventData(String columnId,
                              String dataValue,
                              String patientId,
                              String eventId,
                              String eventRepeatKey) {
        /**
         * Mapping of column ID to values for the current subject.
         */
        Map<String, String> subjectData = new HashMap<>();

        if (!repeatingEventIds.contains(eventId)) {
            repeatingEventIds.add(eventId);
        }

        int repeatingEventIndex = repeatingEventIds.indexOf(eventId) + 1;
        String eventSubjectId = patientId + "_E" + repeatingEventIndex + "_R" + eventRepeatKey;

        if (clinicalDataMap.containsKey(eventSubjectId)) {
            subjectData = clinicalDataMap.get(eventSubjectId);
        } else {
            subjectIds.add(eventSubjectId);
            subjectData.put(FIRST_COLUMN_ID_WITH_SUBJECT_IDS, eventSubjectId);
            subjectData.put(SECOND_COLUMN_ID_WITH_TYPE, "event");
            subjectData.put(THIRD_COLUMN_ID_WITH_ASSOC_PATIENT_IDS, patientId);
            subjectData.put(FIFTH_COLUMN_ID_WITH_EVENT_NR, eventRepeatKey);
            clinicalDataMap.put(eventSubjectId, subjectData);
        }

        subjectData = addWordOrNumber(columnId, dataValue, subjectData);

        logger.debug("Adding subject data for " + eventSubjectId);
        clinicalDataMap.put(eventSubjectId, subjectData);
    }

    private void addItemGroupData(String columnId,
                                  String dataValue,
                                  String patientId,
                                  String eventId,
                                  String itemGroupId,
                                  String itemGroupRepeatKey) {
        /**
         * Mapping of column ID to values for the current subject.
         */
        Map<String, String> subjectData = new HashMap<>();

        if (!repeatingEventIds.contains(eventId)) {
            repeatingEventIds.add(eventId);
        }

        if (!repeatingItemGroupIds.contains(itemGroupId)) {
            repeatingItemGroupIds.add(itemGroupId);
        }

        int repeatingEventIndex = repeatingEventIds.indexOf(eventId) + 1;
        int repeatingItemGroupIndex = repeatingItemGroupIds.indexOf(itemGroupId) + 1;
        String itemGroupSubjectId = patientId +
                "_E"  + repeatingEventIndex +
                "_IG" + repeatingItemGroupIndex +
                "_R"  + itemGroupRepeatKey;

        if (clinicalDataMap.containsKey(itemGroupSubjectId)) {
            subjectData = clinicalDataMap.get(itemGroupSubjectId);
        } else {
            subjectIds.add(itemGroupSubjectId);
            subjectData.put(FIRST_COLUMN_ID_WITH_SUBJECT_IDS, itemGroupSubjectId);
            subjectData.put(SECOND_COLUMN_ID_WITH_TYPE, "repeat");
            subjectData.put(THIRD_COLUMN_ID_WITH_ASSOC_PATIENT_IDS, patientId);
            subjectData.put(SIXTH_COLUMN_ID_WITH_IG_NR, itemGroupRepeatKey);
            clinicalDataMap.put(itemGroupSubjectId, subjectData);
        }

        subjectData = addWordOrNumber(columnId, dataValue, subjectData);

        logger.debug("Adding subject data for " + itemGroupSubjectId);
        clinicalDataMap.put(itemGroupSubjectId, subjectData);
    }

    private void addEventAndItemGroupData(String columnId,
                                          String dataValue,
                                          String patientId,
                                          String eventId,
                                          String eventRepeatKey,
                                          String itemGroupId,
                                          String itemGroupRepeatKey) {
        /**
         * Mapping of column ID to values for the current subject.
         */
        Map<String, String> subjectData = new HashMap<>();

        if (!repeatingEventIds.contains(eventId)) {
            repeatingEventIds.add(eventId);
        }

        if (!repeatingItemGroupIds.contains(itemGroupId)) {
            repeatingItemGroupIds.add(itemGroupId);
        }

        int repeatingEventIndex = repeatingEventIds.indexOf(eventId) + 1;
        int repeatingItemGroupIndex = repeatingItemGroupIds.indexOf(itemGroupId) + 1;
        String eventSubjectId = patientId + "_E" + repeatingEventIndex + "_R" + eventRepeatKey;
        String itemGroupSubjectId = patientId +
                                    "_E"  + repeatingEventIndex + "_R" + eventRepeatKey +
                                    "_IG" + repeatingItemGroupIndex + "_R" + itemGroupRepeatKey;

        if (clinicalDataMap.containsKey(itemGroupSubjectId)) {
            subjectData = clinicalDataMap.get(itemGroupSubjectId);
        } else {
            subjectIds.add(itemGroupSubjectId);
            subjectData.put(FIRST_COLUMN_ID_WITH_SUBJECT_IDS, itemGroupSubjectId);
            subjectData.put(SECOND_COLUMN_ID_WITH_TYPE, "repeat");
            subjectData.put(THIRD_COLUMN_ID_WITH_ASSOC_PATIENT_IDS, patientId);
            subjectData.put(FOURTH_COLUMN_ID_WITH_ASSOC_EVENT_IDS, eventSubjectId);
            subjectData.put(FIFTH_COLUMN_ID_WITH_EVENT_NR, eventRepeatKey);
            subjectData.put(SIXTH_COLUMN_ID_WITH_IG_NR, itemGroupRepeatKey);
            clinicalDataMap.put(itemGroupSubjectId, subjectData);
        }

        subjectData = addWordOrNumber(columnId, dataValue, subjectData);

        logger.debug("Adding subject data for " + itemGroupSubjectId);
        clinicalDataMap.put(itemGroupSubjectId, subjectData);
    }

    private Map<String,String> addWordOrNumber(String columnId, String dataValue, Map<String, String> subjectData) {
        if (wordMap.get(columnId + dataValue) != null) {
            //fills clinical data with words from word map
            subjectData.put(columnId, wordMap.get(columnId + dataValue));
        } else {
            subjectData.put(columnId, dataValue);
        }
        return subjectData;
    }

    private void completeClinicalData() {
        List<String> newlyFoundSubjectIds = new ArrayList<>();
        for (final String subject1Id : subjectIds) {
            Map<String, String> subject1Data = clinicalDataMap.get(subject1Id);
            String subject1Type = subject1Data.get(SECOND_COLUMN_ID_WITH_TYPE);
            if (subject1Type.equals("patient")) {
                copyClinicalData(subject1Id, subject1Data, THIRD_COLUMN_ID_WITH_ASSOC_PATIENT_IDS);
            } else if (subject1Type.equals("event")) {
                copyClinicalData(subject1Id, subject1Data, FOURTH_COLUMN_ID_WITH_ASSOC_EVENT_IDS);
                newlyFoundSubjectIds = findNewSubjects(subject1Data,
                                                       newlyFoundSubjectIds,
                                                       THIRD_COLUMN_ID_WITH_ASSOC_PATIENT_IDS,
                                                       "patient");
            } else if (subject1Type.equals("repeat")) {
                newlyFoundSubjectIds = findNewSubjects(subject1Data,
                                                       newlyFoundSubjectIds,
                                                       THIRD_COLUMN_ID_WITH_ASSOC_PATIENT_IDS,
                                                       "patient");
                if (subject1Data.get(FOURTH_COLUMN_ID_WITH_ASSOC_EVENT_IDS) != null) {
                    newlyFoundSubjectIds = findNewSubjects(subject1Data,
                                                           newlyFoundSubjectIds,
                                                           FOURTH_COLUMN_ID_WITH_ASSOC_EVENT_IDS,
                                                           "event");
                }
            } else {
                logger.error("Unexpected: the type of subject " + subject1Id + " is " + subject1Type);
            }
        }
        subjectIds.addAll(newlyFoundSubjectIds);
    }

    private List<String> findNewSubjects(Map<String, String> subject1Data,
                                         List<String> newlyFoundSubjectIds,
                                         String associationColumnId,
                                         String associationType) {
        String associatedSubjectId = subject1Data.get(associationColumnId);
        if (!subjectIds.contains(associatedSubjectId) &&
            !newlyFoundSubjectIds.contains(associatedSubjectId)) {
            Map<String, String> newlyFoundSubjectData = new HashMap<>();
            newlyFoundSubjectIds.add(associatedSubjectId);
            newlyFoundSubjectData.put(FIRST_COLUMN_ID_WITH_SUBJECT_IDS, associatedSubjectId);
            newlyFoundSubjectData.put(SECOND_COLUMN_ID_WITH_TYPE, associationType);
            clinicalDataMap.put(associatedSubjectId, newlyFoundSubjectData);
            logger.debug("Found " + associationType + " " + associatedSubjectId + " from " +
                    subject1Data.get(SECOND_COLUMN_ID_WITH_TYPE) + " " +
                    subject1Data.get(FIRST_COLUMN_ID_WITH_SUBJECT_IDS));
        }
        return newlyFoundSubjectIds;
    }

    private void copyClinicalData(String subject1Id,
                                  Map<String, String> subject1Data,
                                  String associationType) {
        for (final String subject2Id : subjectIds) {
            Map<String, String> subject2Data = clinicalDataMap.get(subject2Id);
            if (subject2Data.get(associationType) != null && subject2Data.get(associationType).equals(subject1Id)) {
                for (final String columnId : columnIds) {
                    if (!columnId.equals(FIRST_COLUMN_ID_WITH_SUBJECT_IDS) &&
                        !columnId.equals(SECOND_COLUMN_ID_WITH_TYPE )) {
                        if (subject1Data.get(columnId) != null) {
                            subject2Data.put(columnId, subject1Data.get(columnId));
                        }
                    }
                }
                clinicalDataMap.put(subject2Id, subject2Data);
            }
        }
    }

    /**
     * Write the clinical data, which was kept in the memory, to the tab-delimited clinical data file.
     * @throws IOException An input-output exception.
     */
    public void writeSubjectData() throws IOException {
        writeCSVData(clinicalDataWriter, columnHeaders);
        for (final String subjectId : subjectIds) {
            final List<String> rowAsList = new ArrayList<>();
            final Map<String, String> patientData = clinicalDataMap.get(subjectId);
            for (final String columnId : columnIds) {
                String rawDataEntry = patientData.get(columnId);
                String dataEntry;
                if (rawDataEntry == null) {
                    dataEntry = "";
                } else if (rawDataEntry.length() > maxClinicalDataEntry) {
                    dataEntry = rawDataEntry.substring(0, maxClinicalDataEntry - 4) + "...";
                    logger.warn("Data entry " + dataEntry.substring(0, 15) + " of " + subjectId
                            + " and " + columnId + " was cut off at "
                            + rawDataEntry.substring(maxClinicalDataEntry - 20, maxClinicalDataEntry - 4));
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
        if (forbiddenSymbolRegex != null && !forbiddenSymbolRegex.trim().equals("")) {
            for (int i=0; i < rowAsList.size(); i++) {
                rowAsList.set(i, rowAsList.get(i).replaceAll(forbiddenSymbolRegex,""));
            }
        }
        if (avoidTransmartSymbolBugs) {
            for (int i=0; i < rowAsList.size(); i++) {
                rowAsList.set(i, StringUtilities.convertString(rowAsList.get(i)));
            }
        }
        final CSVWriter csvWriter = new CSVWriter(writer, '\t', CSVWriter.NO_QUOTE_CHARACTER);
        final String[] rowAsArray = rowAsList.toArray(new String[rowAsList.size()]);
        csvWriter.writeNext(rowAsArray);
    }

    /**
     * Close the export files.
     */
    public void close() {
        try {
            completeClinicalData();
            writeSubjectData();
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
