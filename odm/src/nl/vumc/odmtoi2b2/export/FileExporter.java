/**
 * Copyright(c) 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.odmtoi2b2.export;

import com.recomdata.i2b2.entity.I2B2ClinicalDataInfo;
import com.recomdata.i2b2.entity.I2B2StudyInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Logger for this class.
     */
    private static final Log log = LogFactory.getLog(FileExporter.class);

    /**
     * The directory where the export files will be written to.
     */
    private final String exportFilePath;

    /**
     * The name of the study.
     */
    private final String studyName;

    /**
     * The name of the concept map file.
     */
    private String conceptMapFileName;

    /**
     * Whether the line with the concept map headers still has to be written to file.
     */
    private boolean writeConceptMapHeaders;

    /**
     * The writer for writing the concept map file.
     */
    private BufferedWriter conceptMapWriter;

    /**
     * The name of the columns file.
     */
    private String columnsFileName;

    /**
     * The writer for writing the columns file.
     */
    private BufferedWriter columnsWriter;

    /**
     * Is set to true right after the column number was increased.
     */
    private boolean increasedColumnNumber;

    /**
     * The name of the word map file.
     */
    private String wordMapFileName;

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
     * Whether the line with the clinical data headers still has to be written to file.
     */
    private boolean writeClinicalDataHeaders;

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
     * The patient number that clinical data info records are being processed for. All data for a patient is collected
     * and written on one line.
     */
    private String currentPatientNumber;

    /**
     * The current column number during the processing of the study info.
     */
    private int currentColumnNumber;

    /**
     * The current column id during the processing of the study info.
     */
    private String currentColumnId;

    /**
     * Mapping of column ID to values for the current patient.
     */
    private Map<String, String> patientData;

    /**
     * Mapping of (column ID + word) to values for the current patient.
     */
    private Map<String, String> wordMap;

    /**
     * Construct a file exporter.
     *
     * @param exportFilePath the directory for the export files.
     * @param studyName the name of the study.
     * @throws IOException when creating the file fails.
     */
    public FileExporter(String exportFilePath, String studyName) throws IOException {
        this.exportFilePath = exportFilePath;
        this.studyName = studyName;
        this.conceptMapFileName = studyName + "_concept_map.txt";
        this.columnsFileName = studyName + "_columns.txt";
        this.wordMapFileName = studyName + "_word_map.txt";
        this.clinicalDataFileName = studyName + "_clinical_data.txt";
        setConceptMapName(this.conceptMapFileName);
        setColumnsName(this.columnsFileName);
        setWordMapName(this.wordMapFileName);
        setClinicalDataName(this.clinicalDataFileName);
        this.writeConceptMapHeaders = true;
        this.writeWordMapHeaders = true;
        this.valueCounter = 1;
        this.writeClinicalDataHeaders = true;
        this.increasedColumnNumber = false;
        this.currentColumnNumber = 1;
        this.currentColumnId = null;
        this.columnHeaders = new ArrayList<>();
        this.columnIds = new ArrayList<>();
        this.patientData = new HashMap<>();
        this.wordMap = new HashMap<>();
    }

    private void setColumnsName(String columnsFileName) {
        try {
            columnsWriter = new BufferedWriter(new FileWriter(exportFilePath + columnsFileName));
            log.info("Writing columns to file " + exportFilePath + columnsFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setWordMapName(String wordMapFileName) {
        try {
            wordMapWriter = new BufferedWriter(new FileWriter(exportFilePath + wordMapFileName));
            log.info("Writing word mappings to file " + exportFilePath + wordMapFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setConceptMapName(String conceptMapFileName) {
        try {
            conceptMapWriter = new BufferedWriter(new FileWriter(exportFilePath + conceptMapFileName));
            log.info("Writing concept map to file " + exportFilePath + conceptMapFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setClinicalDataName(String clinicalDataFileName) {
        try {
            clinicalDataWriter = new BufferedWriter(new FileWriter(exportFilePath + clinicalDataFileName));
            log.info("Writing clinical data to file " + exportFilePath + clinicalDataFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the concept mapping in two columns. The first column represents the tree structure as it will
     * appear in i2b2, the second column is the original tree structure from the ODM. Concepts are separated
     * by + symbols.
     *
     * @param studyInfo the metadata study information
     */
    public void writeExportConceptMap(I2B2StudyInfo studyInfo) {
        if (writeConceptMapHeaders) {
            writeLine(conceptMapWriter, "tranSMART_path\\tEDC_path\tControl Vocab Cd");
            writeConceptMapHeaders = false;
        }

        writeLine(conceptMapWriter, studyInfo.getNamePath() + "+" + studyInfo.getPreferredName() + "\t"
                                  + studyInfo.getNamePath() + "+" + studyInfo.getPreferredName() + "\t");
        columnHeaders.add(studyName + "_" + studyInfo.getPreferredName());
        columnIds.add(studyInfo.getCfullname());
    }

    /**
     * Write the columns file: first the clinical data file name, then the path as specified in the second column of the
     * user's input concept map without the last node, then the column number and then the last node of the path.
     * todo: update comment above (user's input concept map: in manual mode; empty columns at the end)
     *
     * @param studyInfo the metadata study information
     */
    public void writeExportColumns(I2B2StudyInfo studyInfo) {
        if (currentColumnNumber == 1) {
            writeLine(columnsWriter, "Filename\tCategory Code\tColumn Number\tData Label\tData Label Source\t"
                                     + "Control Vocab Cd");
            // This first data line is required by tranSMART. The data in the first study info object is ignored.
            writeLine(columnsWriter, clinicalDataFileName + "\t\t1\tSUBJ_ID\t\t");
        } else {
            writeLine(columnsWriter, clinicalDataFileName + "\t" + studyInfo.getNamePath() + "\t" +
                                      currentColumnNumber + "\t" + studyInfo.getPreferredName() + "\t\t" );
        }
        currentColumnNumber++;
        increasedColumnNumber = true;
        currentColumnId = studyInfo.getCfullname();
    }

    /**
     * Write the word mapping file: first the clinical data file name, then the column number, then the data value,
     * and then the mapped word
     *
     * @param studyInfo the metadata study information
     */

    public void writeExportWordMap(I2B2StudyInfo studyInfo) {
        if (writeWordMapHeaders) {
            writeLine(wordMapWriter, "Filename\tColumn Number\tOriginal Data Value\tNew Data Values");
            writeWordMapHeaders = false;
        }
        if (increasedColumnNumber) {
            valueCounter = 1;
            increasedColumnNumber = false;
        } else {
            valueCounter++;
        }
        String value = String.valueOf(valueCounter);
        wordMap.put(currentColumnId + studyInfo.getCname(), value);
        writeLine(wordMapWriter, clinicalDataFileName + "\t" + (currentColumnNumber - 1) + "\t" + valueCounter + "\t" + studyInfo.getCname());
    }

    /**
     * Write the clinical data to a tab-delimited text file.
     *
     * @param clinicalDataInfo the clinical data to be written to the file
     */
    public void writeExportClinicalDataInfo(I2B2ClinicalDataInfo clinicalDataInfo) {
        String columnId = clinicalDataInfo.getConceptCd();
        String wordValue = clinicalDataInfo.getTvalChar();
        if (clinicalDataInfo.getNvalNum() != null) {
            wordValue = clinicalDataInfo.getNvalNum().toString();
        }
        if (!clinicalDataInfo.getPatientNum().equals(currentPatientNumber)) {
            writePatientData();
            currentPatientNumber = clinicalDataInfo.getPatientNum();
        }
        if (wordMap.get(columnId + wordValue) != null) {
            patientData.put(columnId, wordMap.get(columnId + wordValue));
        } else {
            patientData.put(columnId, wordValue); //fills clinical data with words from wordmap
        }

        String className = clinicalDataInfo.getClass().getName();
        log.trace("[I2B2ODMStudyHandler] " + className.substring(className.lastIndexOf('.') + 1) + ":");
        log.trace("+ PatientNum = " + clinicalDataInfo.getPatientNum());
        log.trace("+ ConceptCd  = " + clinicalDataInfo.getConceptCd());
        log.trace("+ TValChar   = " + clinicalDataInfo.getTvalChar());
        log.trace("");
    }

    private void writePatientData() {
        if (writeClinicalDataHeaders) {
            StringBuilder headers = new StringBuilder();
            for (String columnHeader : columnHeaders) {
                if (headers.length() > 0)
                    headers.append("\t");
                headers.append(columnHeader);
            }
            writeLine(clinicalDataWriter, headers.toString());
            writeClinicalDataHeaders = false;
        }

        if (!patientData.isEmpty()) {
            StringBuilder line = new StringBuilder();
            for (String columnId : columnIds) {
                if (line.length() > 0)
                    line.append("\t");
                if (patientData.containsKey(columnId))
                    line.append(patientData.get(columnId));
            }
            writeLine(clinicalDataWriter, line.toString());
            patientData.clear();
        }
    }

    /**
     * Write a line to the export file.
     *
     * @param writer the writer to write to.
     * @param line the line to be written.
     */
    private void writeLine(BufferedWriter writer, String line) {
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the export files.
     */
    public void close() {
        try {
            writePatientData();
            columnsWriter.close();
            wordMapWriter.close();
            conceptMapWriter.close();
            clinicalDataWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * For debugging: write all the data fields that would be written to the database in text-form.
     *
     * @param dataObject the data object that is prepared for loading to the database
     */
    @SuppressWarnings("UnusedDeclaration")
    public void writeExportDataObject(Object dataObject) {
        String className = dataObject.getClass().getName();
        log.info("[I2B2ODMStudyHandler] " + className.substring(className.lastIndexOf('.') + 1) + ":");
        try {
            for (Field field : dataObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                log.info("- " + field.getName() + ": " + field.get(dataObject));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        log.info("");
    }
}
