package nl.vumc.odmtoi2b2.export;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * File exporter for the I2B2 "light" data model.
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
    private static final String SPACE = " ";

    /**
     * The character encoding used for all the files.
     */
    private static final String UTF8 = "UTF-8";

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
     * The patient IDs.
     */
    private List<String> entityIds;

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
        this.columnHeaders = new ArrayList<>();
        this.columnIds = new ArrayList<>();
        this.entityIds = new ArrayList<>();
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
        System.out.println("wordValue = " + wordValue);
    }

    @Override
    public void storeClinicalDataInfo(final String columnId,
                                      final String dataValue,
                                      final String patientId,
                                      final String eventId,
                                      final String eventRepeatKey,
                                      final String itemGroupId,
                                      final String itemGroupRepeatKey) {
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
        System.out.println("FileExporterLight.close");
    }
}
