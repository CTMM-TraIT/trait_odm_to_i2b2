/**
 * Copyright(c) 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.odmtoi2b2.export;

import com.recomdata.i2b2.util.ODMUtil;
import com.recomdata.odm.MetaDataWithIncludes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdisk.odm.jaxb.*;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In this class, the data object odm is crawled systematically through a series of loops.
 * First, the metadata is crawled, after the clinical data. The data that is retrieved is passed
 * to a file exporter (one for each study), which writes the data to a set of four files.
 */
public class OdmToFilesConvertor {
    private static final Log log = LogFactory.getLog(OdmToFilesConvertor.class);
    private ODM odm;
    private String exportFilePath;
    MetaDataWithIncludes metaDataWithIncludes;
    private Map<String, FileExporter> fileExporters;
    private Map<String, MetaDataWithIncludes> metaDataMap;
    private String patientNum;

    public OdmToFilesConvertor() {
        this.fileExporters = new HashMap<>();
        this.metaDataMap = new HashMap<>();
    }

    public void processODM(ODM odm, String exportFilePath) throws IOException, JAXBException {
        this.odm = odm;
        this.exportFilePath = exportFilePath;

        processODMStudy();
        processODMClinicalData();
    }

    public void closeExportWriters() {
        for (ODMcomplexTypeDefinitionStudy study : odm.getStudy()) {
            final String studyName = study.getGlobalVariables().getStudyName().getValue();
            fileExporters.get(studyName).close();
        }
    }


    private void processODMStudy() throws IOException, JAXBException {
        // Need to traverse through the study metadata to:
        // 1) Lookup all metadata definition values and paths for each tree leaf.
        // 2) Pass the metadata to the corresponding file exporter.

        for (ODMcomplexTypeDefinitionStudy study : odm.getStudy()) {

            saveStudy(study);
        }

    }

    private void saveStudy(ODMcomplexTypeDefinitionStudy study) throws IOException, JAXBException {
        final String studyName = study.getGlobalVariables().getStudyName().getValue();
        String studyOID = study.getOID();
        ODMcomplexTypeDefinitionMetaDataVersion metaData = study.getMetaDataVersion().get(0);
        ODMcomplexTypeDefinitionInclude includedMetaData = metaData.getInclude();
        List<MetaDataWithIncludes> metaDataWithIncludesList = new ArrayList<>();
        if (includedMetaData != null) {
            String includedMetaDataKey = includedMetaData.getStudyOID() + "/" + includedMetaData.getMetaDataVersionOID();
            if (metaDataMap.containsKey(includedMetaDataKey)) {
                // todo: recursive includes.
//                List<MetaDataWithIncludes> tbd = null;
//                MetaDataWithIncludes backup = new MetaDataWithIncludes(metaDataMap.get(includedMetaDataKey), tbd);
                metaDataWithIncludesList.add(metaDataMap.get(includedMetaDataKey));
            }
        }
        metaDataWithIncludes = new MetaDataWithIncludes(metaData, studyOID, metaDataWithIncludesList);
        metaDataMap.put(getMetaDataKey(study), metaDataWithIncludes);

        FileExporter fileExporter = new FileExporter(exportFilePath + new File(File.separator), studyName);
        fileExporters.put(studyName, fileExporter);

        if (metaData.getProtocol().getStudyEventRef() != null) {
            for (ODMcomplexTypeDefinitionStudyEventRef studyEventRef : metaData.getProtocol().getStudyEventRef()) {
                ODMcomplexTypeDefinitionStudyEventDef studyEventDef =
                        metaDataWithIncludes.getStudyEventDef(studyEventRef.getStudyEventOID());

                saveEvent(study, studyEventDef);
            }
        }
    }

    private String getMetaDataKey(ODMcomplexTypeDefinitionStudy study) {
        return study.getOID() + "/" + study.getMetaDataVersion().get(0).getOID();
    }

    private void saveEvent(ODMcomplexTypeDefinitionStudy study,
                           ODMcomplexTypeDefinitionStudyEventDef studyEventDef)
            throws JAXBException {

        if (studyEventDef.getFormRef() != null) {
            for (ODMcomplexTypeDefinitionFormRef formRef : studyEventDef.getFormRef()) {
//              ODMcomplexTypeDefinitionFormDef formDef = ODMUtil.getFormDef(study, formRef.getFormOID());
                ODMcomplexTypeDefinitionFormDef formDef = metaDataWithIncludes.getFormDef(formRef.getFormOID());

                saveForm(study, studyEventDef, formDef);
            }
        }
    }

    private void saveForm(ODMcomplexTypeDefinitionStudy study,
                          ODMcomplexTypeDefinitionStudyEventDef studyEventDef,
                          ODMcomplexTypeDefinitionFormDef formDef)
            throws JAXBException {

        if (formDef.getItemGroupRef() != null) {
            for (ODMcomplexTypeDefinitionItemGroupRef itemGroupRef : formDef.getItemGroupRef()) {
                ODMcomplexTypeDefinitionItemGroupDef itemGroupDef =
                        metaDataWithIncludes.getItemGroupDef(itemGroupRef.getItemGroupOID());

                saveItemGroup(study, studyEventDef, formDef, itemGroupDef);
            }
        }
    }

    private void saveItemGroup(ODMcomplexTypeDefinitionStudy study,
                               ODMcomplexTypeDefinitionStudyEventDef studyEventDef,
                               ODMcomplexTypeDefinitionFormDef formDef,
                               ODMcomplexTypeDefinitionItemGroupDef itemGroupDef)
            throws JAXBException {

        if (itemGroupDef.getItemRef() != null) {
            for (ODMcomplexTypeDefinitionItemRef itemRef : itemGroupDef.getItemRef()) {
                ODMcomplexTypeDefinitionItemDef itemDef = metaDataWithIncludes.getItemDef(itemRef.getItemOID());

                saveItem(study, studyEventDef, formDef, itemGroupDef, itemDef);
            }
        }
    }

    private void saveItem(ODMcomplexTypeDefinitionStudy study,
                          ODMcomplexTypeDefinitionStudyEventDef studyEventDef,
                          ODMcomplexTypeDefinitionFormDef formDef,
                          ODMcomplexTypeDefinitionItemGroupDef itemGroupDef,
                          ODMcomplexTypeDefinitionItemDef itemDef)
            throws JAXBException {
        String studyName      = study.getGlobalVariables().getStudyName().getValue();
        String studyEventName = getTranslatedDescription(studyEventDef.getDescription(), "en", studyEventDef.getName());
        String formName       = getTranslatedDescription(formDef.getDescription(),       "en", formDef.getName());
        String itemGroupName  = getTranslatedDescription(itemGroupDef.getDescription(),  "en", itemGroupDef.getName());
        String namePath       = studyEventName + "+" + formName + "+" + itemGroupName;
        String itemName       = getTranslatedDescription(itemDef.getDescription(),       "en", itemDef.getName());
        String questionValue  = getQuestionValue(itemDef);
        String preferredItemName = questionValue != null ? questionValue : itemName;

        String oidPath = study.getOID() + "\\"
                + studyEventDef.getOID() + "\\"
                + formDef.getOID() + "\\"
                + itemDef.getOID() + "\\";

        fileExporters.get(studyName).writeExportConceptMap(namePath, preferredItemName);
        fileExporters.get(studyName).writeExportColumns(namePath, preferredItemName, oidPath);

        if (itemDef.getCodeListRef() != null) {
            ODMcomplexTypeDefinitionCodeList codeList = ODMUtil.getCodeList(study, itemDef.getCodeListRef().getCodeListOID());

            if (codeList != null) {
                for (ODMcomplexTypeDefinitionCodeListItem codeListItem : codeList.getCodeListItem()) {

                    saveCodeListItem(study, codeListItem);
                }
            }
        }
    }

    private String getQuestionValue(ODMcomplexTypeDefinitionItemDef itemDef) {
        return itemDef.getQuestion() != null
                && itemDef.getQuestion().getTranslatedText() != null
                && itemDef.getQuestion().getTranslatedText().size() >= 1
                && itemDef.getQuestion().getTranslatedText().get(0) != null
                && itemDef.getQuestion().getTranslatedText().get(0).getValue() != null
                && !itemDef.getQuestion().getTranslatedText().get(0).getValue().trim().equals("")
                ? itemDef.getQuestion().getTranslatedText().get(0).getValue().trim()
                : null;
    }

    private void saveCodeListItem(ODMcomplexTypeDefinitionStudy study,
                                  ODMcomplexTypeDefinitionCodeListItem codeListItem) {
        String studyName = study.getGlobalVariables().getStudyName().getValue();
        String DataValue = ODMUtil.getTranslatedValue(codeListItem, "en");
        fileExporters.get(studyName).writeExportWordMap(DataValue);

    }

    private String getTranslatedDescription(ODMcomplexTypeDefinitionDescription description, String lang,
                                            String defaultValue) {
        if (description != null) {
            for (ODMcomplexTypeDefinitionTranslatedText translatedText : description.getTranslatedText()) {
                if (translatedText.getLang().equals(lang)) {
                    return translatedText.getValue();
                }
            }
        }
        return defaultValue;
    }


    /*****************************************************************************************************
     * Below this point are the methods for processing the clinical data itself.
     * Above are the methods for processing the study metadata.
     */
    private void processODMClinicalData() {
        for (ODMcomplexTypeDefinitionClinicalData clinicalData : odm.getClinicalData()) {
            if (clinicalData.getSubjectData() != null) {
                String studyOID = clinicalData.getStudyOID();
                ODMcomplexTypeDefinitionStudy study = ODMUtil.getStudy(odm, studyOID);
                if (study != null) {

                    saveClinicalData(study, clinicalData);
                } else {
                    log.error("ODM does not contain study metadata for study OID " + studyOID);
                }
            }
        }
    }

    private void saveClinicalData(ODMcomplexTypeDefinitionStudy study,
                                  ODMcomplexTypeDefinitionClinicalData clinicalData) {
        String studyOID = clinicalData.getStudyOID();
        log.info("Write Clinical data for study OID " + studyOID + " to clinical data file...");

        for (ODMcomplexTypeDefinitionSubjectData subjectData : clinicalData.getSubjectData()) {
            if (subjectData.getStudyEventData() != null) {

                saveSubjectData(study, clinicalData, subjectData);
            }
        }


    }

    private void saveSubjectData(ODMcomplexTypeDefinitionStudy study,
                                 ODMcomplexTypeDefinitionClinicalData clinicalData,
                                 ODMcomplexTypeDefinitionSubjectData subjectData) {
        for (ODMcomplexTypeDefinitionStudyEventData eventData : subjectData.getStudyEventData()) {
            if (eventData.getFormData() != null) {

                saveEventData(study, clinicalData, subjectData, eventData);
            }
        }
    }

    private void saveEventData(ODMcomplexTypeDefinitionStudy study,
                               ODMcomplexTypeDefinitionClinicalData clinicalData,
                               ODMcomplexTypeDefinitionSubjectData subjectData,
                               ODMcomplexTypeDefinitionStudyEventData eventData) {
        for (ODMcomplexTypeDefinitionFormData formData : eventData.getFormData()) {
            if (formData.getItemGroupData() != null) {

                saveFormData(study, clinicalData, subjectData, eventData, formData);
            }
        }
    }

    private void saveFormData(ODMcomplexTypeDefinitionStudy study,
                              ODMcomplexTypeDefinitionClinicalData clinicalData,
                              ODMcomplexTypeDefinitionSubjectData subjectData,
                              ODMcomplexTypeDefinitionStudyEventData eventData,
                              ODMcomplexTypeDefinitionFormData formData) {
        for (ODMcomplexTypeDefinitionItemGroupData itemGroupData : formData.getItemGroupData()) {
            if (itemGroupData.getItemDataGroup() != null) {

                saveItemGroupData(study, clinicalData, subjectData, eventData, formData, itemGroupData);
            }
        }
    }

    private void saveItemGroupData(ODMcomplexTypeDefinitionStudy study,
                                   ODMcomplexTypeDefinitionClinicalData clinicalData,
                                   ODMcomplexTypeDefinitionSubjectData subjectData,
                                   ODMcomplexTypeDefinitionStudyEventData eventData,
                                   ODMcomplexTypeDefinitionFormData formData,
                                   ODMcomplexTypeDefinitionItemGroupData itemGroupData) {
        for (ODMcomplexTypeDefinitionItemData itemData : itemGroupData.getItemDataGroup()) {
            if (itemData.getValue() != null) {
                saveItemData(study, clinicalData, subjectData, eventData, formData, itemGroupData, itemData);
            }
        }
    }

    private void saveItemData(ODMcomplexTypeDefinitionStudy study,
                              ODMcomplexTypeDefinitionClinicalData clinicalData,
                              ODMcomplexTypeDefinitionSubjectData subjectData,
                              ODMcomplexTypeDefinitionStudyEventData eventData,
                              ODMcomplexTypeDefinitionFormData formData,
                              ODMcomplexTypeDefinitionItemGroupData itemGroupData,
                              ODMcomplexTypeDefinitionItemData itemData) {
        String studyName = study.getGlobalVariables().getStudyName().getValue();
        String oidPath = clinicalData.getStudyOID() + "\\"
                + eventData.getStudyEventOID() + "\\"
                + formData.getFormOID() + "\\"
                + itemData.getItemOID() + "\\";
        String itemValue = itemData.getValue();
//      ODMcomplexTypeDefinitionItemDef item = ODMUtil.getItem(study, itemData.getItemOID());
        ODMcomplexTypeDefinitionItemDef itemDef = getMetaData(study).getItemDef(itemData.getItemOID());
        String tvalChar;
        BigDecimal nvalNum;
        String patientNum = subjectData.getSubjectKey();

        if (itemDef.getCodeListRef() != null) {
            nvalNum = null;

            ODMcomplexTypeDefinitionCodeList codeList = getMetaData(study).getCodeList(itemDef.getCodeListRef().getCodeListOID());
            ODMcomplexTypeDefinitionCodeListItem codeListItem = ODMUtil.getCodeListItem(codeList, itemValue);

            if (codeListItem == null) {
                log.error("Code list item for coded value: " + itemValue + " not found in code list: " + codeList.getOID());
                return;
            } else {
                tvalChar = ODMUtil.getTranslatedValue(codeListItem, "en");
            }
        } else if (ODMUtil.isNumericDataType(itemDef.getDataType())) {
            tvalChar = "";
            nvalNum = itemValue == null || itemValue.trim().equals("") || itemValue.length() == 0 ? null : new BigDecimal(itemValue);
        } else {
            tvalChar = itemValue;
            nvalNum = null;
        }

        fileExporters.get(studyName).writeExportClinicalDataInfo(oidPath, tvalChar, nvalNum, patientNum); //TODO: uncomment


    }

    private MetaDataWithIncludes getMetaData(ODMcomplexTypeDefinitionStudy study) {
        return metaDataMap.get(getMetaDataKey(study));
    }



}
