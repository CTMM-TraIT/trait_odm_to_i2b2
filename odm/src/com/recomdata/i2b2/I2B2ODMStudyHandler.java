package com.recomdata.i2b2;

/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 * This class parses data from ODM xml file by jaxb and save into I2B2 database.
 * @author: Alex Wu
 * @date: September 2, 2011
 */
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdisk.odm.jaxb.ODM;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionClinicalData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCodeList;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCodeListItem;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionDescription;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemGroupData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemGroupDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemGroupRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionMetaDataVersion;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudy;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyEventData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyEventDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyEventRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionSubjectData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionTranslatedText;

import com.recomdata.i2b2.dao.ClinicalDataDao;
import com.recomdata.i2b2.dao.IClinicalDataDao;
import com.recomdata.i2b2.dao.IStudyDao;
import com.recomdata.i2b2.dao.StudyDao;
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

    // initialize ODM object
    private ODM odm = null;

    private I2B2StudyInfo studyInfo = new I2B2StudyInfo();
    private I2B2ClinicalDataInfo clinicalDataInfo = new I2B2ClinicalDataInfo();

    private IStudyDao studyDao = null;
    private IClinicalDataDao clinicalDataDao = null;

    private Date currentDate = null;
    private MessageDigest messageDigest = null;
    private StringBuffer conceptBuffer = new StringBuffer("STUDY|");
    private MetaDataXML mdx = new MetaDataXML();

    /**
     * Constructor to set ODM object
     *
     * @param odm the entire ODM tree.
     * @throws SQLException
     * @throws NoSuchAlgorithmException
     */
    public I2B2ODMStudyHandler(ODM odm) throws SQLException,
            NoSuchAlgorithmException {
        this.odm = odm;

        studyDao = new StudyDao();
        clinicalDataDao = new ClinicalDataDao();

        studyInfo.setSourceSystemCd(odm.getSourceSystem());
        clinicalDataInfo.setSourcesystemCd(odm.getSourceSystem());

        currentDate = Calendar.getInstance().getTime();
        messageDigest = MessageDigest.getInstance("MD5");
    }

    /**
     * set up i2b2 metadata level 1 (Study) info into STUDY
     *
     * @throws JAXBException
     */
    private void saveStudy(ODMcomplexTypeDefinitionStudy study)
            throws SQLException, JAXBException {
        // Need to include source system in path to avoid conflicts between servers
        String studyKey = odm.getSourceSystem() + ":" + study.getOID();

        String studyPath = "\\" + "STUDY" + "\\" + studyKey + "\\";
        String studyToolTip = "STUDY" + "\\" + studyKey;

        // set c_hlevel 1 data (Study)
        studyInfo.setChlevel(IConstants.C_HLEVEL_1);
        studyInfo.setCfullname(studyPath);
        studyInfo.setCname(study.getGlobalVariables().getStudyName().getValue());
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
        studyDao.insertMetadata(studyInfo);

        // save child events
        ODMcomplexTypeDefinitionMetaDataVersion version = study.getMetaDataVersion().get(0);

        if (version.getProtocol().getStudyEventRef() != null) {
            for (ODMcomplexTypeDefinitionStudyEventRef studyEventRef : version.getProtocol().getStudyEventRef()) {
                ODMcomplexTypeDefinitionStudyEventDef studyEventDef =
                        ODMUtil.getStudyEvent(study, studyEventRef.getStudyEventOID());

                saveEvent(study, studyEventDef, studyPath, studyToolTip);
            }
        }
    }

    /**
     * set up i2b2 metadata level 2 (Event) info into STUDY
     *
     * @throws JAXBException
     */
    private void saveEvent(ODMcomplexTypeDefinitionStudy study,
                           ODMcomplexTypeDefinitionStudyEventDef studyEventDef,
                           String studyPath, String studyToolTip) throws SQLException,
            JAXBException {
        String eventPath = studyPath + studyEventDef.getOID() + "\\";
        String eventToolTip = studyToolTip + "\\" + studyEventDef.getOID();

        // set c_hlevel 2 data (StudyEvent)
        studyInfo.setChlevel(IConstants.C_HLEVEL_2);
        studyInfo.setCfullname(eventPath);
        studyInfo.setCname(studyEventDef.getName());
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
        studyDao.insertMetadata(studyInfo);

        if (studyEventDef.getFormRef() != null) {
            for (ODMcomplexTypeDefinitionFormRef formRef : studyEventDef.getFormRef()) {
                ODMcomplexTypeDefinitionFormDef formDef = ODMUtil.getForm(study, formRef.getFormOID());

                saveForm(study, studyEventDef, formDef, eventPath, eventToolTip);
            }
        }
    }

    /**
     * set up i2b2 metadata level 3 (Form) info into STUDY
     *
     * @throws JAXBException
     */
    private void saveForm(ODMcomplexTypeDefinitionStudy study,
                          ODMcomplexTypeDefinitionStudyEventDef studyEventDef,
                          ODMcomplexTypeDefinitionFormDef formDef, String eventPath,
                          String eventToolTip) throws SQLException, JAXBException {
        String formPath = eventPath + formDef.getOID() + "\\";
        String formToolTip = eventToolTip + "\\" + formDef.getOID();

        // set c_hlevel 3 data (Form)
        studyInfo.setChlevel(IConstants.C_HLEVEL_3);
        studyInfo.setCfullname(formPath);
        studyInfo.setCname(getTranslatedDescription(formDef.getDescription(), "en", formDef.getName()));
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
        studyDao.insertMetadata(studyInfo);


        if (formDef.getItemGroupRef() != null) {
            for (ODMcomplexTypeDefinitionItemGroupRef itemGroupRef : formDef.getItemGroupRef()) {
                ODMcomplexTypeDefinitionItemGroupDef itemGroupDef =
                        ODMUtil.getItemGroup(study, itemGroupRef.getItemGroupOID());

                if (itemGroupDef.getItemRef() != null) {
                    for (ODMcomplexTypeDefinitionItemRef itemRef : itemGroupDef.getItemRef()) {
                        ODMcomplexTypeDefinitionItemDef itemDef = ODMUtil.getItem(study, itemRef.getItemOID());

                        saveItem(study, studyEventDef, formDef, itemDef, formPath, formToolTip);
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
                          ODMcomplexTypeDefinitionItemDef itemDef, String formPath,
                          String formToolTip) throws SQLException, JAXBException {
        String itemPath = formPath + itemDef.getOID() + "\\";
        String itemToolTip = formToolTip + "\\" + itemDef.getOID();

        // set c_hlevel 4 data (Items)
        studyInfo.setChlevel(IConstants.C_HLEVEL_4);
        studyInfo.setCfullname(itemPath);
        studyInfo.setCname(getTranslatedDescription(itemDef.getDescription(), "en", itemDef.getName()));
        studyInfo.setCbasecode(generateConceptCode(study.getOID(), studyEventDef.getOID(), formDef.getOID(), itemDef.getOID(), null));
        studyInfo.setCdimcode(itemPath);
        studyInfo.setCtooltip(itemToolTip);
        studyInfo.setCmetadataxml(createMetadataXml(study, itemDef));

        // It is a leaf node
        if (itemDef.getCodeListRef() == null) {
            studyInfo.setCvisualAttributes(IConstants.C_VISUALATTRIBUTES_LEAF);
        } else {
            studyInfo.setCvisualAttributes(IConstants.C_VISUALATTRIBUTES_FOLDER);
        }

        logStudyInfo();

        // insert level 4 data
        studyDao.insertMetadata(studyInfo);

        if (itemDef.getCodeListRef() != null) {
            ODMcomplexTypeDefinitionCodeList codeList = ODMUtil.getCodeList(study, itemDef.getCodeListRef().getCodeListOID());

            if (codeList != null) {
                for (ODMcomplexTypeDefinitionCodeListItem codeListItem : codeList.getCodeListItem()) {
                    // save
                    // level 5
                    saveCodeListItem(study, studyEventDef, formDef, itemDef, codeListItem, itemPath, itemToolTip);
                }
            }
        }
    }

    private String getTranslatedDescription(
            ODMcomplexTypeDefinitionDescription description, String lang, String defaultValue) {
        if (description != null) {
            for (ODMcomplexTypeDefinitionTranslatedText translatedText : description.getTranslatedText()) {
                if (translatedText.getLang().equals(lang)) {
                    return translatedText.getValue();
                }
            }
        }

        return defaultValue;
    }

    private String createMetadataXml(ODMcomplexTypeDefinitionStudy study,
                                     ODMcomplexTypeDefinitionItemDef itemDef) throws JAXBException {
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

            case BOOLEAN:

                break;

            case DATE:
            case TIME:
            case DATETIME:
                metadataXml = mdx.getStringMetadataXML(itemDef.getOID(), itemDef.getName());
                break;

            default:
        }

        return metadataXml;
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
                                  String itemToolTip) throws SQLException {
        String value = ODMUtil.getTranslatedValue(codeListItem, "en");
        String codedValue = codeListItem.getCodedValue();
        String codeListItemPath = itemPath + codedValue + "\\";
        String codeListItemToolTip = itemToolTip + "\\"	+ value;



        // set c_hlevel 5 data (TranslatedText)
        studyInfo.setChlevel(IConstants.C_HLEVEL_5);
        studyInfo.setCfullname(codeListItemPath);
        studyInfo.setCname(getTranslatedDescription(itemDef.getDescription(), "en", itemDef.getName()) + ": " + value);
        studyInfo.setCbasecode(generateConceptCode(study.getOID(), studyEventDef.getOID(), formDef.getOID(), itemDef.getOID(), codedValue));
        studyInfo.setCdimcode(codeListItemPath);
        studyInfo.setCtooltip(codeListItemToolTip);
        studyInfo.setCmetadataxml(null);
        studyInfo.setCvisualAttributes(IConstants.C_VISUALATTRIBUTES_LEAF);

        logStudyInfo();

        studyDao.insertMetadata(studyInfo);
    }

    /**
     * method to parse ODM and save data into i2b2
     *
     * @throws JAXBException
     * @throws ParseException
     */
    public void processODM() throws SQLException, JAXBException, ParseException {
        log.info("Start to parse ODM xml and save to i2b2");

        // build the call
        processODMStudy();
        processODMClinicalData();
    }

    /*
     * This method takes ODM XML io.File obj as input and parsed by JAXB API and
     * then traverses through ODM tree object and save data into i2b2 metadata
     * database in i2b2 data format.
     */
    public void processODMStudy() throws SQLException, JAXBException {
		/*
		 * Need to traverse through the study definition to: 1) Lookup all
		 * definition values in tree nodes. 2) Set node values into i2b2 bean
		 * info and ready for populating into i2b2 database.
		 */
        for (ODMcomplexTypeDefinitionStudy study : odm.getStudy()) {
            log.info("Processing study metadata for study " + study.getGlobalVariables().getStudyName().getValue() + "(OID " + study.getOID() + ")");
            log.info("Deleting old study metadata and data");

            studyDao.preSetupI2B2Study(study.getOID(), odm.getSourceSystem());

            log.info("Inserting study metadata into i2b2");
            long startTime = System.currentTimeMillis();

            saveStudy(study);

            long endTime = System.currentTimeMillis();
            log.info("Completed loading study metadata into i2b2 in " + (endTime - startTime) + " ms");
        }

		/*
		 * Flush any remaining batched up records.
		 */
        studyDao.executeBatch();
    }

    /*
     * This method takes ODM XML io.File obj as input and parsed by JAXB API and
     * then traversal through ODM tree object and save clinical data into i2b2
     * demo database ini2b2 data format. Keep method public in case of only want
     * to parse demodata.
     */
    public void processODMClinicalData() throws JAXBException, ParseException, SQLException {
        log.info("Parse and save ODM clinical data into i2b2...");

        // traverse through the clinical data to:
        // 1) Lookup the concept path from odm study metadata.
        // 2) Set patient and clinical information into observation fact.
        if (odm.getClinicalData() == null || odm.getClinicalData().size() == 0) {
            log.info("ODM does not contain clinical data");
            return;
        }

        for (ODMcomplexTypeDefinitionStudy study : odm.getStudy()) {
            clinicalDataDao.cleanupClinicalData(study.getOID(), odm.getSourceSystem());
        }

        for (ODMcomplexTypeDefinitionClinicalData clinicalData : odm.getClinicalData()) {
            if (clinicalData.getSubjectData() == null) {
                continue;
            }

            log.info("Save Clinical data for study OID " + clinicalData.getStudyOID() + " into i2b2...");
            long startTime = System.currentTimeMillis();

            ODMcomplexTypeDefinitionStudy study = ODMUtil.getStudy(odm, clinicalData.getStudyOID());
            if (study == null) {
                log.error("ODM does not contain study metadata for study OID " + clinicalData.getStudyOID());

                continue;
            }

			/*
			 * Generate a unique encounter number per subject per study to ensure that
			 * observation fact primary key is not violated.
			 */
            int encounterNum = 0;

            for (ODMcomplexTypeDefinitionSubjectData subjectData : clinicalData.getSubjectData()) {
                if (subjectData.getStudyEventData() == null) {
                    continue;
                }

                encounterNum++;

                for (ODMcomplexTypeDefinitionStudyEventData studyEventData : subjectData.getStudyEventData()) {
                    if (studyEventData.getFormData() == null) {
                        continue;
                    }

                    for (ODMcomplexTypeDefinitionFormData formData : studyEventData.getFormData()) {
                        if (formData.getItemGroupData() == null) {
                            continue;
                        }

                        for (ODMcomplexTypeDefinitionItemGroupData itemGroupData : formData.getItemGroupData()) {
                            if (itemGroupData.getItemDataGroup() == null) {
                                continue;
                            }

                            for (ODMcomplexTypeDefinitionItemData itemData : itemGroupData.getItemDataGroup()) {
                                if (itemData.getValue() != null) {
                                    saveItemData(study, clinicalData, subjectData, studyEventData, formData, itemData, encounterNum);
                                }
                            }
                        }
                    }
                }
            }

			/*
			 * Flush any remaining batched up observations;
			 */
            clinicalDataDao.executeBatch();

            long endTime = System.currentTimeMillis();
            log.info("Completed Clinical data to i2b2 for study OID " + clinicalData.getStudyOID() + " in " + (endTime - startTime) + " ms");

        }
    }

    private void logStudyInfo() {
        if (log.isDebugEnabled()) {
            log.debug("Inserting study metadata record: " + studyInfo);
        }
    }

    private void saveItemData(
            ODMcomplexTypeDefinitionStudy study,
            @SuppressWarnings("UnusedParameters") ODMcomplexTypeDefinitionClinicalData clinicalData,
            ODMcomplexTypeDefinitionSubjectData subjectData,
            ODMcomplexTypeDefinitionStudyEventData studyEventData,
            ODMcomplexTypeDefinitionFormData formData,
            ODMcomplexTypeDefinitionItemData itemData,
            int encounterNum) throws JAXBException, ParseException, SQLException {

        String itemValue = itemData.getValue();
        ODMcomplexTypeDefinitionItemDef item = ODMUtil.getItem(study, itemData.getItemOID());

        String conceptCd;

        if (item.getCodeListRef() != null) {
            clinicalDataInfo.setValTypeCd("T");
            clinicalDataInfo.setNvalNum(null);

            ODMcomplexTypeDefinitionCodeList codeList = ODMUtil.getCodeList(study, item.getCodeListRef().getCodeListOID());
            ODMcomplexTypeDefinitionCodeListItem codeListItem = ODMUtil.getCodeListItem(codeList, itemValue);

            if (codeListItem == null) {
                log.error("Code list item for coded value: " + itemValue + " not found in code list: " + codeList.getOID());
                return;
            } else {
				/*
				 * Need to include the item value in the concept code, since there is a different code for each code list item.
				 */
                conceptCd = generateConceptCode(
                        study.getOID(),
                        studyEventData.getStudyEventOID(),
                        formData.getFormOID(),
                        itemData.getItemOID(),
                        itemValue);
                clinicalDataInfo.setTvalChar(ODMUtil.getTranslatedValue(codeListItem, "en"));
            }
        } else if (ODMUtil.isNumericDataType(item.getDataType())) {
            conceptCd = generateConceptCode(
                    study.getOID(),
                    studyEventData.getStudyEventOID(),
                    formData.getFormOID(),
                    itemData.getItemOID(),
                    null);

            clinicalDataInfo.setValTypeCd("N");
            clinicalDataInfo.setTvalChar("E");
            clinicalDataInfo.setNvalNum(itemValue == null || itemValue.trim().equals("") || itemValue.length() == 0 ? null : new BigDecimal(itemValue));
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

        // save observation
        // into i2b2

        try {
            log.info("clinicalDataInfo: " + clinicalDataInfo);
            clinicalDataDao.insertObservation(clinicalDataInfo);
        } catch (SQLException e) {
            String sError = "Error inserting observation_fact record.";
            sError += " study: " + study.getOID();
            sError += " item: " + itemData.getItemOID();
            log.error(sError, e);
        }
    }

    /**
     * Create concept code with all OIDs and make the total length less than 50
     * and unique
     *
     * @return the unique concept code.
     */
    private String generateConceptCode(String studyOID, String studyEventOID,
                                       String formOID, String itemOID, String value) {
        conceptBuffer.setLength(6);
        conceptBuffer.append(studyOID).append("|");

        messageDigest.update(odm.getSourceSystem().getBytes());
        messageDigest.update((byte) '|');
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
        for (byte digestByte : digest) {
            conceptBuffer.append(Integer.toHexString(0xFF & digestByte));
        }

        String conceptCode = conceptBuffer.toString();
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
}
