/**
 * Copyright(c) 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.odmtoi2b2.export;

import com.recomdata.odm.MetaDataWithIncludes;
import org.cdisk.odm.jaxb.*;

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

    private ODM odm;
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

    public void processODM(ODM odm, String exportFilePath) throws IOException {
        this.odm = odm;
        this.exportFilePath = exportFilePath;

        processODMStudy();
        processODMClinicalData();
    }

    public void closeExportWriters() {
        for (ODMcomplexTypeDefinitionStudy study : odm.getStudy()) {
            fileExporters.get(study.getGlobalVariables().getStudyName().getValue()).close();
        }
    }


    private void processODMStudy() throws IOException {
        // Need to traverse through the study metadata to:
        // 1) Lookup all metadata definition values and paths for each tree leaf.
        // 2) Pass the metadata to the corresponding file exporter.

        for (ODMcomplexTypeDefinitionStudy study : odm.getStudy()) {
            saveStudy(study);
        }

    }

    private String getMetaDataKey(ODMcomplexTypeDefinitionStudy study) {
        return study.getOID() + "/" + study.getMetaDataVersion().get(0).getOID();
    }

    private void saveStudy(ODMcomplexTypeDefinitionStudy study) throws IOException {
        studyName = study.getGlobalVariables().getStudyName().getValue();
        studyOID = study.getOID();

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

    private void saveEvent(ODMcomplexTypeDefinitionStudyEventDef studyEventDef) {
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

    private void saveForm(ODMcomplexTypeDefinitionFormDef formDef) {
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

    private void saveItemGroup(ODMcomplexTypeDefinitionItemGroupDef itemGroupDef) {
        oidPath += "\\" + itemGroupDef.getOID();
        namePath += "+" + getTranslatedDescription(itemGroupDef.getDescription(), "en", itemGroupDef.getName());

        if (itemGroupDef.getItemRef() != null) {
            for (ODMcomplexTypeDefinitionItemRef itemRef : itemGroupDef.getItemRef()) {
                ODMcomplexTypeDefinitionItemDef itemDef = metaDataWithIncludes.getItemDef(itemRef.getItemOID());

                saveItem(itemDef);
            }
        }
    }

    private void saveItem(ODMcomplexTypeDefinitionItemDef itemDef) {
        String itemName = getTranslatedDescription(itemDef.getDescription(), "en", itemDef.getName());
        String questionValue = getQuestionValue(itemDef);
        String preferredItemName = questionValue != null ? questionValue : itemName;
        oidPath += "\\" + itemDef.getOID();

//      fileExporters.get(studyName).writeExportConceptMap(namePath, preferredItemName);
//      fileExporters.get(studyName).writeExportColumns(namePath, preferredItemName, oidPath);

        namePath += "+" + preferredItemName;


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



    private void processODMClinicalData() {
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


}
