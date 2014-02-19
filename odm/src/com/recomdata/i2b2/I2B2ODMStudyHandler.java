package com.recomdata.i2b2;

/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 * This class parses data from ODM xml file by jaxb and save into I2B2 database.
 * @author: Alex Wu
 * @date: September 2, 2011
 */
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

import javax.xml.bind.JAXBException;

import com.recomdata.i2b2.dao.*;
import com.recomdata.odm.MetaDataWithIncludes;
import nl.vumc.odmtoi2b2.export.FileExporter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdisk.odm.jaxb.*;

import com.recomdata.i2b2.entity.I2B2ClinicalDataInfo;
import com.recomdata.i2b2.entity.I2B2StudyInfo;
import com.recomdata.i2b2.util.ODMUtil;

/**
 * Class parse ODM and save meta data and clinical data into i2b2
 *
 * @author awu
 *
 */
public class I2B2ODMStudyHandler implements IConstants {
    private static final Log log = LogFactory.getLog(I2B2ODMStudyHandler.class);

    private ODM odm = null;

    private I2B2StudyInfo studyInfo = new I2B2StudyInfo();
    private I2B2ClinicalDataInfo clinicalDataInfo = new I2B2ClinicalDataInfo();

    private String exportFilePath = null;
    private boolean exportToDatabase;
    private Map<String, FileExporter> fileExporters;
    private Map<String, MetaDataWithIncludes> metaDataMap;
    private MetaDataWithIncludes currentMetaData;
    private IStudyDao studyDao = null;
    private IClinicalDataDao clinicalDataDao = null;

    private Date currentDate = null;
    private MessageDigest messageDigest = null;
	private StringBuffer conceptBuffer = new StringBuffer("STUDY|");
    private MetaDataXML mdx = new MetaDataXML();

    /**
     * Constructor to create an ODM study handler object.
     *
     * @param odm Operational Data Model object
     * @param exportToDatabase whether to export to a database or to files.
     * @param exportFilePath the path of the export file.
     * @throws SQLException
     * @throws NoSuchAlgorithmException
     */
    public I2B2ODMStudyHandler(ODM odm, boolean exportToDatabase, String exportFilePath) throws SQLException,
            NoSuchAlgorithmException, IOException {
        this.odm = odm;
        this.exportToDatabase = exportToDatabase;
        this.exportFilePath = exportFilePath;
        this.fileExporters = new HashMap<>();
        this.metaDataMap = new HashMap<>();

        if (exportToDatabase) {
            studyDao = new StudyDao();
            clinicalDataDao = new ClinicalDataDao();
        }

        studyInfo.setSourceSystemCd(odm.getSourceSystem());
        clinicalDataInfo.setSourcesystemCd(odm.getSourceSystem());

        currentDate = Calendar.getInstance().getTime();
		messageDigest = MessageDigest.getInstance("MD5");
    }

    /**
     * Parse ODM and save data into i2b2 format.
     *
     * @throws JAXBException
     * @throws ParseException
     */
    public void processODM() throws SQLException, JAXBException, ParseException, IOException {
        log.info("Start to parse ODM xml and save to i2b2");

        // build the call
        processODMStudy();
        processODMClinicalData();
    }

    /*
     * This method takes ODM XML io.File obj as input and parsed by JAXB API and then traverses through ODM tree object
     * and save data into i2b2 metadata database in i2b2 data format.
     */
    private void processODMStudy() throws SQLException, JAXBException, IOException {
        // Need to traverse through the study definition to:
        // 1) Lookup all definition values in tree nodes.
        // 2) Set node values into i2b2 bean info and ready for populating into i2b2 database.

        for (ODMcomplexTypeDefinitionStudy study : odm.getStudy()) {
            String studyName = study.getGlobalVariables().getStudyName().getValue();
            log.info("Processing study metadata for study " + studyName + "(OID " + study.getOID() + ")");
            log.info("Deleting old study metadata and data");

            if (exportToDatabase) {
                studyDao.preSetupI2B2Study(study.getOID(), odm.getSourceSystem());
            }

            log.info("Inserting study metadata into i2b2");
            long startTime = System.currentTimeMillis();

            FileExporter fileExporter = new FileExporter(exportFilePath + new File(File.separator), studyName);  // + "\\"
            fileExporters.put(studyName, fileExporter);

            saveStudy(study);

            long endTime = System.currentTimeMillis();
            log.info("Completed loading study metadata into i2b2 in " + (endTime - startTime) + " ms");
        }

        // Flush any remaining batched up records.
        if (exportToDatabase) {
            studyDao.executeBatch();
        }
    }

    /*
     * This method takes ODM XML io.File obj as input and parsed by JAXB API and the traversal through ODM tree object
     * and save clinical data into i2b2 demo database in i2b2 data format.
     *
     * TODO: Keep method public in case of only want to parse demodata?
     */
    private void processODMClinicalData() throws JAXBException, ParseException, SQLException {
        log.info("Parse and save ODM clinical data into i2b2...");

        // Traverse through the clinical data to:
        // 1) Lookup the concept path from odm study metadata.
        // 2) Set patient and clinical information into observation fact.
        if (odm.getClinicalData() == null || odm.getClinicalData().size() == 0) {
            log.info("ODM does not contain clinical data");
        } else {
            for (ODMcomplexTypeDefinitionStudy study : odm.getStudy()) {
                if (exportToDatabase) {
                    clinicalDataDao.cleanupClinicalData(study.getOID(), odm.getSourceSystem());
                }
            }

            for (ODMcomplexTypeDefinitionClinicalData clinicalData : odm.getClinicalData()) {
                if (clinicalData.getSubjectData() != null) {
                    String studyOID = clinicalData.getStudyOID();
                    ODMcomplexTypeDefinitionStudy study = ODMUtil.getStudy(odm, studyOID);
                    if (study != null) {
                        saveStudyClinicalData(clinicalData, study, studyOID);
                    } else {
                        log.error("ODM does not contain study metadata for study OID " + studyOID);
                    }
                }
            }
        }
    }

    /**
     * Save the clinical data for a study.
     *
     * @param clinicalData the clinical data.
     * @param study the study.
     * @param studyOID the study OID.
     * @throws JAXBException when retrieving one of the lower level ODM elements fails.
     * @throws SQLException when export to database fails.
     */
    private void saveStudyClinicalData(ODMcomplexTypeDefinitionClinicalData clinicalData,
                                       ODMcomplexTypeDefinitionStudy study, String studyOID)
            throws JAXBException, SQLException {
        log.info("Save Clinical data for study OID " + studyOID + " into i2b2...");
        long startTime = System.currentTimeMillis();

        // Generate a unique encounter number per subject per study to ensure that observation fact primary key is
        // not violated.
        int encounterNum = 0;

        for (ODMcomplexTypeDefinitionSubjectData subjectData : clinicalData.getSubjectData()) {
            if (subjectData.getStudyEventData() != null) {
                encounterNum++;
                saveStudyEventData(study, encounterNum, subjectData);
            }
        }

        // Flush any remaining batched up observations.
        if (exportToDatabase) {
            clinicalDataDao.executeBatch();
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Completed Clinical data to i2b2 for study OID " + studyOID + " in " + duration + " ms");
    }

    /**
     * Save the clinical data for a study event.
     *
     * @param study the study.
     * @param encounterNum the unique encounter number.
     * @param subjectData the subject data that contains the study events.
     * @throws JAXBException when retrieving one of the lower level ODM elements fails.
     */
    private void saveStudyEventData(ODMcomplexTypeDefinitionStudy study, int encounterNum,
                                    ODMcomplexTypeDefinitionSubjectData subjectData)
            throws JAXBException {
        for (ODMcomplexTypeDefinitionStudyEventData studyEventData : subjectData.getStudyEventData()) {
            if (studyEventData.getFormData() != null) {
                for (ODMcomplexTypeDefinitionFormData formData : studyEventData.getFormData()) {
                    if (formData.getItemGroupData() != null) {
                        for (ODMcomplexTypeDefinitionItemGroupData itemGroupData : formData.getItemGroupData()) {
                            if (itemGroupData.getItemDataGroup() != null) {
                                for (ODMcomplexTypeDefinitionItemData itemData : itemGroupData.getItemDataGroup()) {
                                    if (itemData.getValue() != null) {
                                        saveItemData(study, subjectData, studyEventData, formData, itemData,
                                                     encounterNum);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * set up i2b2 metadata level 1 (Study) info into STUDY
     *
     * @throws JAXBException
     */
    private void saveStudy(ODMcomplexTypeDefinitionStudy study) throws SQLException, JAXBException {
        // Need to include source system in path to avoid conflicts between servers
        String studyKey = odm.getSourceSystem() + ":" + study.getOID();

        String studyPath = "\\" + "STUDY" + "\\" + studyKey + "\\";
        String studyName = study.getGlobalVariables().getStudyName().getValue();
        String studyToolTip = "STUDY" + "\\" + studyKey;

        // set c_hlevel 1 data (Study)
        studyInfo.setChlevel(IConstants.C_HLEVEL_1);
        studyInfo.setCfullname(studyPath);
        studyInfo.setCname(studyName);
        studyInfo.setNamePath(studyName);
        studyInfo.setCsynonmCd(IConstants.C_SYNONYM_CD);
        studyInfo.setCvisualAttributes(IConstants.C_VISUALATTRIBUTES_FOLDER);
        studyInfo.setCfactTableColumn(IConstants.C_FACTTABLECOLUMN);
        studyInfo.setCtablename(IConstants.C_TABLENAME);
        studyInfo.setCcolumnname(IConstants.C_COLUMNNAME);
        studyInfo.setCcolumnDatatype(IConstants.C_COLUMNDATATYPE);
        studyInfo.setCoperator(IConstants.C_OPERATOR);
        studyInfo.setSourceSystemCd(odm.getSourceSystem());
        studyInfo.setUpdateDate(currentDate);
        studyInfo.setDownloadDate(currentDate);
        studyInfo.setImportDate(currentDate);
        studyInfo.setCdimcode(studyPath);
        studyInfo.setCtooltip(studyToolTip);

        logStudyInfo();

        // insert level 1 data
        if (exportToDatabase) {
            studyDao.insertMetadata(studyInfo);
        }

        // save child events
        ODMcomplexTypeDefinitionMetaDataVersion version = study.getMetaDataVersion().get(0);
        ODMcomplexTypeDefinitionInclude includedVersion = version.getInclude();
        List<MetaDataWithIncludes> metaDataWithIncludesList = new ArrayList<>();
        if (includedVersion != null) {
            String includedVersionId = includedVersion.getStudyOID() + "/" + includedVersion.getMetaDataVersionOID();
            if (metaDataMap.containsKey(includedVersionId)) {
                // todo: recursive includes.
//                List<MetaDataWithIncludes> tbd = null;
//                MetaDataWithIncludes backup = new MetaDataWithIncludes(metaDataMap.get(includedVersionId), tbd);
                metaDataWithIncludesList.add(metaDataMap.get(includedVersionId));
            }
        }
        currentMetaData = new MetaDataWithIncludes(version, metaDataWithIncludesList);
        metaDataMap.put(getMetaDataKey(study), currentMetaData);
        MetaDataWithIncludes metaDataWithIncludes = new MetaDataWithIncludes(version, metaDataWithIncludesList);

        if (version.getProtocol().getStudyEventRef() != null) {
            for (ODMcomplexTypeDefinitionStudyEventRef studyEventRef : version.getProtocol().getStudyEventRef()) {
                ODMcomplexTypeDefinitionStudyEventDef studyEventDef =
                        metaDataWithIncludes.getStudyEventDef(studyEventRef.getStudyEventOID());
                    saveEvent(study, metaDataWithIncludes, studyEventDef, studyPath, studyName, studyToolTip);
            }
        }
    }

    private String getMetaDataKey(ODMcomplexTypeDefinitionStudy study) {
        return study.getOID() + "/" + study.getMetaDataVersion().get(0).getOID();
    }

    /**
     * set up i2b2 metadata level 2 (Event) info into STUDY
     *
     * @throws JAXBException
     */
    private void saveEvent(ODMcomplexTypeDefinitionStudy study, MetaDataWithIncludes metaDataWithIncludes,
                           ODMcomplexTypeDefinitionStudyEventDef studyEventDef, String studyPath, String studyNamePath,
                           String studyToolTip) throws SQLException,
            JAXBException {
        String eventPath = studyPath + studyEventDef.getOID() + "\\";   //(studyEventDef != null ? studyEventDef.getOID() : "")
        String eventName = studyEventDef.getName();
        String eventToolTip = studyToolTip + "\\" + studyEventDef.getOID();

        // set c_hlevel 2 data (StudyEvent)
        studyInfo.setChlevel(IConstants.C_HLEVEL_2);
        studyInfo.setCfullname(eventPath);
        studyInfo.setCname(eventName);
        studyInfo.setNamePath(studyNamePath);
        studyInfo.setCdimcode(eventPath);
        studyInfo.setCtooltip(eventToolTip);

        // It is a leaf node
        if (studyEventDef.getFormRef() == null) {
            studyInfo.setCvisualAttributes(IConstants.C_VISUALATTRIBUTES_LEAF);
        } else {
            studyInfo.setCvisualAttributes(IConstants.C_VISUALATTRIBUTES_FOLDER);
        }

        logStudyInfo();

        // insert level 2 data
        if (exportToDatabase) {
            studyDao.insertMetadata(studyInfo);
        }

        if (studyEventDef.getFormRef() != null) {
            for (ODMcomplexTypeDefinitionFormRef formRef : studyEventDef.getFormRef()) {
//                ODMcomplexTypeDefinitionFormDef formDef = ODMUtil.getFormDef(study, formRef.getFormOID());
                ODMcomplexTypeDefinitionFormDef formDef = metaDataWithIncludes.getFormDef(formRef.getFormOID());

                saveForm(study, metaDataWithIncludes, studyEventDef, formDef, eventPath, eventName, eventToolTip);
            }
        }
    }

    /**
     * set up i2b2 metadata level 3 (Form) info into STUDY
     *
     * @throws JAXBException
     */
    private void saveForm(ODMcomplexTypeDefinitionStudy study,
                          MetaDataWithIncludes metaDataWithIncludes, ODMcomplexTypeDefinitionStudyEventDef studyEventDef,
                          ODMcomplexTypeDefinitionFormDef formDef, String eventPath, String eventNamePath,
                          String eventToolTip) throws SQLException, JAXBException {
        String formPath = eventPath + formDef.getOID() + "\\";
        String formName = getTranslatedDescription(formDef.getDescription(), "en", formDef.getName());
        String formNamePath = eventNamePath + "+" + formName;
        String formToolTip = eventToolTip + "\\" + formDef.getOID();

        // set c_hlevel 3 data (Form)
        studyInfo.setChlevel(IConstants.C_HLEVEL_3);
        studyInfo.setCfullname(formPath);
        studyInfo.setCname(formName);
        studyInfo.setNamePath(eventNamePath);
        studyInfo.setCdimcode(formPath);
        studyInfo.setCtooltip(formToolTip);

        // It is a leaf node
        if (formDef.getItemGroupRef() == null) {
            studyInfo.setCvisualAttributes(IConstants.C_VISUALATTRIBUTES_LEAF);
        } else {
            studyInfo.setCvisualAttributes(IConstants.C_VISUALATTRIBUTES_FOLDER);
        }

        logStudyInfo();

        // insert level 3 data
        if (exportToDatabase) {
            studyDao.insertMetadata(studyInfo);
        }

        if (formDef.getItemGroupRef() != null) {
            for (ODMcomplexTypeDefinitionItemGroupRef itemGroupRef : formDef.getItemGroupRef()) {
                ODMcomplexTypeDefinitionItemGroupDef itemGroupDef =
                        metaDataWithIncludes.getItemGroupDef(itemGroupRef.getItemGroupOID());

                if (itemGroupDef.getItemRef() != null) {
                    for (ODMcomplexTypeDefinitionItemRef itemRef : itemGroupDef.getItemRef()) {
                        ODMcomplexTypeDefinitionItemDef itemDef = metaDataWithIncludes.getItemDef(itemRef.getItemOID());

                        saveItem(study, studyEventDef, formDef, itemGroupDef, itemDef, formPath, formNamePath, formToolTip);
                    }
                }
            }
        }
    }

    /**
     * set up i2b2 metadata level 4 (Item) info into STUDY and CONCEPT_DIMENSION
     *
     * @throws SQLException
     * @throws JAXBException
     */
    private void saveItem(ODMcomplexTypeDefinitionStudy study,
                          ODMcomplexTypeDefinitionStudyEventDef studyEventDef,
                          ODMcomplexTypeDefinitionFormDef formDef,
                          ODMcomplexTypeDefinitionItemGroupDef itemGroupDef,
                          ODMcomplexTypeDefinitionItemDef itemDef,
                          String formPath,
                          String formNamePath,
                          String formToolTip) throws SQLException, JAXBException {
        String studyName = study.getGlobalVariables().getStudyName().getValue();
        String itemPath = formPath + itemDef.getOID() + "\\";
        String itemGroupName = itemGroupDef.getName();
        String itemNamePath = formNamePath + "+" + itemGroupName;
        String itemName = getTranslatedDescription(itemDef.getDescription(), "en", itemDef.getName());
        String itemConceptCode = generateConceptCode(study.getOID(), studyEventDef.getOID(), formDef.getOID(),
                                                     itemDef.getOID(), null);
        String itemToolTip = formToolTip + "\\" + itemDef.getOID();

        // set c_hlevel 4 data (Items)
        studyInfo.setChlevel(IConstants.C_HLEVEL_4);
        studyInfo.setCfullname(itemPath);
        studyInfo.setCname(itemName);
        studyInfo.setNamePath(itemNamePath);
        studyInfo.setCbasecode(itemConceptCode);
        studyInfo.setCdimcode(itemPath);
        studyInfo.setCtooltip(itemToolTip);
        studyInfo.setCmetadataxml(createMetadataXml(study, itemDef));
        String questionValue = getQuestionValue(itemDef);
        studyInfo.setPreferredName(questionValue != null ? questionValue : itemName);
//        if (itemDef.getComment() == null) {
//        if (questionValue == null) {
//            studyInfo.setPreferredName(itemName);
//        }

        // It is a leaf node
        if (itemDef.getCodeListRef() == null) {
            studyInfo.setCvisualAttributes(IConstants.C_VISUALATTRIBUTES_LEAF);
        } else {
            studyInfo.setCvisualAttributes(IConstants.C_VISUALATTRIBUTES_FOLDER);
        }

        logStudyInfo();

        // insert level 4 data
        if (exportToDatabase) {
            studyDao.insertMetadata(studyInfo);
        } else {
            fileExporters.get(studyName).writeExportConceptMap(studyInfo);
            fileExporters.get(studyName).writeExportColumns(studyInfo);
        }

        if (itemDef.getCodeListRef() != null) {
            ODMcomplexTypeDefinitionCodeList codeList = ODMUtil.getCodeList(study, itemDef.getCodeListRef().getCodeListOID());

            if (codeList != null) {
                for (ODMcomplexTypeDefinitionCodeListItem codeListItem : codeList.getCodeListItem()) {
                    // save level 5
                    saveCodeListItem(study, studyEventDef, formDef, itemDef, codeListItem, itemPath, itemNamePath,
                                     itemToolTip);
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
                ? itemDef.getQuestion().getTranslatedText().get(0).getValue().trim()
                : null;
    }

    /**
     * set up i2b2 metadata level 5 (TranslatedText) info into STUDY
     *
     * @throws SQLException
     */
    private void saveCodeListItem(ODMcomplexTypeDefinitionStudy study,
                                  ODMcomplexTypeDefinitionStudyEventDef studyEventDef,
                                  ODMcomplexTypeDefinitionFormDef formDef,
                                  ODMcomplexTypeDefinitionItemDef itemDef,
                                  ODMcomplexTypeDefinitionCodeListItem codeListItem, String itemPath,
                                  String itemNamePath,
                                  String itemToolTip) throws SQLException {
        String studyName = study.getGlobalVariables().getStudyName().getValue();
        String value = ODMUtil.getTranslatedValue(codeListItem, "en");
        String codedValue = codeListItem.getCodedValue();
        String codeListItemPath = itemPath + codedValue + "\\";
        String codeListName = getTranslatedDescription(itemDef.getDescription(), "en", itemDef.getName()) + ": " + value;
        String itemConceptCode = generateConceptCode(study.getOID(), studyEventDef.getOID(), formDef.getOID(),
                                                     itemDef.getOID(), codedValue);
        String codeListItemToolTip = itemToolTip + "\\"	+ value;

        // set c_hlevel 5 data (TranslatedText)
        studyInfo.setChlevel(IConstants.C_HLEVEL_5);
        studyInfo.setCfullname(codeListItemPath);
        studyInfo.setNamePath(itemNamePath);
        studyInfo.setCbasecode(itemConceptCode);
        studyInfo.setCdimcode(codeListItemPath);
        studyInfo.setCtooltip(codeListItemToolTip);
        studyInfo.setCmetadataxml(null);
        studyInfo.setCvisualAttributes(IConstants.C_VISUALATTRIBUTES_LEAF);
        if (exportToDatabase) {
            studyInfo.setCname(codeListName);
        } else {
            studyInfo.setCname(value);
        }

        logStudyInfo();

        if (exportToDatabase) {
            studyDao.insertMetadata(studyInfo);
        } else {
            fileExporters.get(studyName).writeExportWordMap(studyInfo);
        }
    }

    private void saveItemData(
            ODMcomplexTypeDefinitionStudy study,
            ODMcomplexTypeDefinitionSubjectData subjectData,
            ODMcomplexTypeDefinitionStudyEventData studyEventData,
            ODMcomplexTypeDefinitionFormData formData,
            ODMcomplexTypeDefinitionItemData itemData,
            int encounterNum) throws JAXBException {

        String studyName = study.getGlobalVariables().getStudyName().getValue();
        String itemValue = itemData.getValue();
//        ODMcomplexTypeDefinitionItemDef item = ODMUtil.getItem(study, itemData.getItemOID());
        ODMcomplexTypeDefinitionItemDef itemDef = getMetaData(study).getItemDef(itemData.getItemOID());

        String conceptCd;

        if (itemDef.getCodeListRef() != null) {
            clinicalDataInfo.setValTypeCd("T");
            clinicalDataInfo.setNvalNum(null);

            ODMcomplexTypeDefinitionCodeList codeList = getMetaData(study).getCodeList(itemDef.getCodeListRef().getCodeListOID());
            ODMcomplexTypeDefinitionCodeListItem codeListItem = ODMUtil.getCodeListItem(codeList, itemValue);

            if (codeListItem == null) {
                log.error("Code list item for coded value: " + itemValue + " not found in code list: " + codeList.getOID());
                return;
            } else {
                // Need to include the item value in the concept code, since there is a different code for each code
                // list item.
                conceptCd = generateConceptCode(
                        study.getOID(),
                        studyEventData.getStudyEventOID(),
                        formData.getFormOID(),
                        itemData.getItemOID(),
                        itemValue);
                clinicalDataInfo.setTvalChar(ODMUtil.getTranslatedValue(codeListItem, "en"));
            }
        } else if (ODMUtil.isNumericDataType(itemDef.getDataType())) {
            conceptCd = generateConceptCode(
                    study.getOID(),
                    studyEventData.getStudyEventOID(),
                    formData.getFormOID(),
                    itemData.getItemOID(),
                    null);

            clinicalDataInfo.setValTypeCd("N");
            clinicalDataInfo.setTvalChar("E");
            clinicalDataInfo.setNvalNum(itemValue == null || itemValue.equals(" ") || itemValue.length() == 0 ? null : new BigDecimal(itemValue));
        } else {
            conceptCd = generateConceptCode(
                    study.getOID(),
                    studyEventData.getStudyEventOID(),
                    formData.getFormOID(),
                    itemData.getItemOID(),
                    null);

            clinicalDataInfo.setValTypeCd("T");
            clinicalDataInfo.setTvalChar(itemValue);
            clinicalDataInfo.setNvalNum(null);
        }

        clinicalDataInfo.setConceptCd(conceptCd);
        clinicalDataInfo.setEncounterNum(encounterNum);
        clinicalDataInfo.setPatientNum(subjectData.getSubjectKey());
        clinicalDataInfo.setUpdateDate(currentDate);
        clinicalDataInfo.setDownloadDate(currentDate);
        clinicalDataInfo.setImportDate(currentDate);
        clinicalDataInfo.setStartDate(currentDate);
        clinicalDataInfo.setEndDate(currentDate);

        log.debug("Inserting clinical data: " + clinicalDataInfo);

        // save observation into i2b2
        try {
            log.info("clinicalDataInfo: " + clinicalDataInfo);
            if (exportToDatabase) {
                clinicalDataDao.insertObservation(clinicalDataInfo);
            } else {
                fileExporters.get(studyName).writeExportClinicalDataInfo(clinicalDataInfo);
            }
        } catch (SQLException e) {
            String sError = "Error inserting observation_fact record.";
            sError += " study: " + study.getOID();
            sError += " item: " + itemData.getItemOID();
            log.error(sError, e);
        }
    }

    private MetaDataWithIncludes getMetaData(ODMcomplexTypeDefinitionStudy study) {
        return metaDataMap.get(getMetaDataKey(study));
    }

    private void logStudyInfo() {
        if (log.isDebugEnabled()) {
            log.debug("Inserting study metadata record: " + studyInfo);
        }
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

    /**
     * Create concept code with all OIDs and make the total length less than 50 and unique.
     *
     * @param studyEventOID the study event identifier.
     * @param formOID the form identifier.
     * @param itemOID  the item identifier.
     * @return the concept code for this item.
     */
    private String generateConceptCode(String studyOID, String studyEventOID,
                                       String formOID, String itemOID, String value) {
        String conceptCode;
        String studyKey = odm.getSourceSystem() + ":" + studyOID;


        if (exportToDatabase) {
            conceptBuffer.setLength(6);
            conceptBuffer.append(studyOID).append("|");

            // TODO: the source system can be null?
            if (odm.getSourceSystem() != null) {
                messageDigest.update(odm.getSourceSystem().getBytes());
                messageDigest.update((byte) '|');
            }
            messageDigest.update(studyEventOID.getBytes());
            messageDigest.update((byte) '|');
            messageDigest.update(formOID.getBytes());
            messageDigest.update((byte) '|');
            messageDigest.update(itemOID.getBytes());

            if (value != null) {
                messageDigest.update((byte) '|');
                messageDigest.update(value.getBytes());
            }

            byte[] digest = messageDigest.digest();

            for (int i = 0; i < digest.length; i++) {
                conceptBuffer.append(Integer.toHexString(0xFF & digest[i]));
            }

            conceptCode = conceptBuffer.toString();
        } else {
            conceptCode = "\\STUDY\\" + studyKey + "\\" + studyEventOID + "\\" + formOID + "\\" + itemOID + "\\";
        }


        if (log.isDebugEnabled()) {
            log.debug(new StringBuffer("Concept code ").append(conceptCode)
                              .append(" generated for studyOID=").append(studyOID)
                              .append(", studyEventOID=").append(studyEventOID)
                              .append(", formOID=").append(formOID)
                              .append(", itemOID=").append(itemOID)
                              .append(", value=").append(value).toString());
        }

        return conceptCode;
    }

    private String createMetadataXml(ODMcomplexTypeDefinitionStudy study, ODMcomplexTypeDefinitionItemDef itemDef)
            throws JAXBException {
        String metadataXml = null;

        switch (itemDef.getDataType()) {
            case INTEGER:
                metadataXml = mdx.getIntegerMetadataXML(itemDef.getOID(), itemDef.getName());
                break;

            case FLOAT:
            case DOUBLE:
                metadataXml = mdx.getFloatMetadataXML(itemDef.getOID(), itemDef.getName());
                break;

            case TEXT:
            case STRING:
                if (itemDef.getCodeListRef() == null) {
                    metadataXml = mdx.getStringMetadataXML(itemDef.getOID(), itemDef.getName());
                } else {
                    ODMcomplexTypeDefinitionCodeList codeList =
                            ODMUtil.getCodeList(study, itemDef.getCodeListRef().getCodeListOID());
                    String[] codeListValues = ODMUtil.getCodeListValues(codeList, "en");

                    metadataXml = mdx.getEnumMetadataXML(itemDef.getOID(), itemDef.getName(), codeListValues);
                }
                break;

            case DATE:
            case TIME:
            case DATETIME:
                metadataXml = mdx.getStringMetadataXML(itemDef.getOID(), itemDef.getName());
                break;

            case BOOLEAN:
            default:
        }

        return metadataXml;
    }

    public boolean exportedToFile() {
        return !exportToDatabase;
    }

    public void closeExportWriters() {
        if (!exportToDatabase) {
            for (ODMcomplexTypeDefinitionStudy study : odm.getStudy()) {
                fileExporters.get(study.getGlobalVariables().getStudyName().getValue()).close();
            }
        }
    }
}
