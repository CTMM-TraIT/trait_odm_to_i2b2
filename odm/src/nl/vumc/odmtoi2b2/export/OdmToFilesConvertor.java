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
    private ODMcomplexTypeDefinitionStudy study;
    private String exportFilePath;
    private String studyName;
    private String studyOID;
    private String oidPath;
    private String namePath;
    MetaDataWithIncludes metaDataWithIncludes;
    private Map<String, FileExporter> fileExporters;
    private Map<String, MetaDataWithIncludes> metaDataMap;

    public OdmToFilesConvertor() {
        this.fileExporters = new HashMap<>();

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
            this.study = study;
            this.studyName = study.getGlobalVariables().getStudyName().getValue();
            this.studyOID = study.getOID();

            saveStudy();
        }

    }

    private void saveStudy() throws IOException, JAXBException {

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

                saveEvent(studyEventDef);
            }
        }
    }

    private String getMetaDataKey(ODMcomplexTypeDefinitionStudy study) {
        return study.getOID() + "/" + study.getMetaDataVersion().get(0).getOID();
    }

    private void saveEvent(ODMcomplexTypeDefinitionStudyEventDef studyEventDef) throws JAXBException {
        oidPath = studyOID + "\\" + studyEventDef.getOID();   //(studyEventDef != null ? studyEventDef.getOID() : "")
        namePath = studyName + "+" + studyEventDef.getName();

        if (studyEventDef.getFormRef() != null) {
            for (ODMcomplexTypeDefinitionFormRef formRef : studyEventDef.getFormRef()) {
//              ODMcomplexTypeDefinitionFormDef formDef = ODMUtil.getFormDef(study, formRef.getFormOID());
                ODMcomplexTypeDefinitionFormDef formDef = metaDataWithIncludes.getFormDef(formRef.getFormOID());

                saveForm(formDef);
            }
        }
    }

    private void saveForm(ODMcomplexTypeDefinitionFormDef formDef) throws JAXBException {
        oidPath += "\\" + formDef.getOID();
        namePath += "+" + getTranslatedDescription(formDef.getDescription(), "en", formDef.getName());

        if (formDef.getItemGroupRef() != null) {
            for (ODMcomplexTypeDefinitionItemGroupRef itemGroupRef : formDef.getItemGroupRef()) {
                ODMcomplexTypeDefinitionItemGroupDef itemGroupDef =
                        metaDataWithIncludes.getItemGroupDef(itemGroupRef.getItemGroupOID());

                saveItemGroup(itemGroupDef);
            }
        }
    }

    private void saveItemGroup(ODMcomplexTypeDefinitionItemGroupDef itemGroupDef) throws JAXBException {
        oidPath += "\\" + itemGroupDef.getOID();
        namePath += "+" + getTranslatedDescription(itemGroupDef.getDescription(), "en", itemGroupDef.getName());

        if (itemGroupDef.getItemRef() != null) {
            for (ODMcomplexTypeDefinitionItemRef itemRef : itemGroupDef.getItemRef()) {
                ODMcomplexTypeDefinitionItemDef itemDef = metaDataWithIncludes.getItemDef(itemRef.getItemOID());

                saveItem(itemDef);
            }
        }
    }

    private void saveItem(ODMcomplexTypeDefinitionItemDef itemDef) throws JAXBException {
        String itemName = getTranslatedDescription(itemDef.getDescription(), "en", itemDef.getName());
        String questionValue = getQuestionValue(itemDef);
        String preferredItemName = questionValue != null ? questionValue : itemName;
        oidPath += "\\" + itemDef.getOID();

//      fileExporters.get(studyName).writeExportConceptMap(namePath, preferredItemName);  //TODO: uncomment
//      fileExporters.get(studyName).writeExportColumns(namePath, preferredItemName, oidPath); //TODO: uncomment

        namePath += "+" + preferredItemName;

        if (itemDef.getCodeListRef() != null) {
            ODMcomplexTypeDefinitionCodeList codeList = ODMUtil.getCodeList(study, itemDef.getCodeListRef().getCodeListOID());

            if (codeList != null) {
                for (ODMcomplexTypeDefinitionCodeListItem codeListItem : codeList.getCodeListItem()) {

                    saveCodeListItem(codeListItem);
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

    private void saveCodeListItem(ODMcomplexTypeDefinitionCodeListItem codeListItem) {
        String DataValue = ODMUtil.getTranslatedValue(codeListItem, "en");
//      fileExporters.get(studyName).writeExportWordMap(DataValue);  //TODO: uncomment

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

    private void processODMClinicalData() {
        for (ODMcomplexTypeDefinitionClinicalData clinicalData : odm.getClinicalData()) {
            if (clinicalData.getSubjectData() != null) {
                studyOID = clinicalData.getStudyOID();
                this.study = ODMUtil.getStudy(odm, studyOID);
                if (study != null) {

                    saveStudyClinicalData(clinicalData);
                } else {
                    log.error("ODM does not contain study metadata for study OID " + studyOID);
                }
            }
        }
    }

    private void saveStudyClinicalData(ODMcomplexTypeDefinitionClinicalData clinicalData) {
        log.info("Write Clinical data for study OID " + studyOID + " to clinical data file...");

        for (ODMcomplexTypeDefinitionSubjectData subjectData : clinicalData.getSubjectData()) {
            if (subjectData.getStudyEventData() != null) {

                saveEventData(subjectData);
            }
        }


    }

    private void saveEventData(ODMcomplexTypeDefinitionSubjectData subjectData) {
    }


}
