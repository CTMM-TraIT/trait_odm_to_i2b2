/**
 * Copyright(c) 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.odmtoi2b2.export;

import com.recomdata.i2b2.util.ODMUtil;
import com.recomdata.odm.MetaDataWithIncludes;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.cdisk.odm.jaxb.ODM;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionClinicalData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCodeList;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCodeListItem;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionDescription;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionInclude;
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
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class goes through the tree-structure of the odm object that was constructed by the
 * unmarshaller. Going through the tree-structure of the odm object is done in a series of
 * nested for-loops, in which each nested level is assigned to a separate method.
 * First, the metadata is crawled; after that, the clinical data. The data that is retrieved is passed
 * to a file exporter (one for each study), which writes the data to a set of four files that can be
 * imported by tranSMART.
 * For the metadata, the order is: study - event - form (= CRF) - itemgroup - item - codeListItem
 *
 * The situation becomes more complicated when a study is spread over different sites (e.g. hospitals).
 * In that case a separate "study" is present in the ODM file for each site, even though it is in fact
 * the same study. In that case, the metadata is defined just once and is referred to in the so-called
 * studies (in fact sites) by a metadata include tag. These study-sites contain the clinical data that
 * was assembled in that site. An extra column is created to indicate which patient was treated in which
 * site.
 *
 * study 1      study 2
 * metadata     <-(study 1)
 * -            clinical data
 *
 * Map<String, ODMcomplexTypeDefinitionMetaDataVersion> metadataMap;
 * metadataMap.put("study 1", metadata);
 *
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blondé</a>
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class OdmToFilesConverter {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(OdmToFilesConverter.class);

    /**
     * The language variant of the label that is retrieved. en = english.
     */
    private static final String LANGUAGE = "en";

    /**
     * A separator to construct the namepath.
     */
    private static final String PLUS = "+";

    /**
     * A separator to separate OIDs in the OIDPath.
     */
    private static final String SEP = "\\";

    /**
     * The name for the node in tranSMART under which different studies will be arranged as sites.
     */
    private static final String STUDYSITE = "Study-site";

    /**
     * Is set to true when different studies that are written to the same files,
     * need to be modelled as a separate Study-site column.
     */
    private boolean modelStudiesAsColumn;


    /**
     * Is set to true when the export should be made to i2b2-light, in which each patient
     * corresponds to exactly one row in the clinical data file. Repeated events and repeated
     * observations are modeled as concepts, hence as columns in the clinical data file.
     */
    private boolean exportToI2b2Light;

    /**
     * The odm object with all the content from the ODM xml file.
     */
    private ODM odm;

    /**
     * The path to the directory where the export files will be written to (has no slash yet).
     */
    private String exportFilePath;

    /**
     * The metadata of a study, including the metadata fragments that are included with an include tag.
     */
    private MetaDataWithIncludes metaDataWithIncludes;

    /**
     * A list with the full human-readable names of the columns, which are used as identifiers within tranSMART.
     * For this reason, they should be kept unique.
     */
    private List<String> columnFullNameList;

    /**
     * Map<studyName, fileExporter> to keep track of all the FileExporter objects that were created.
     */
    private Map<String, FileExporter> fileExporters;

    /**
     * Map<studyOID+metadata_ID, metaDataWithIncludes> to keep track of all the metadata objects that were created.
     */
    private Map<String, MetaDataWithIncludes> metaDataMap;

    /**
     * Key: ODM-study, value: defining study with the metadata. File exporters are only made for the latter.
     */
    private Map<String, String> studies;


    /**
     * This class is instantiated once for each ODM file.
     */
    public OdmToFilesConverter() {
        this.fileExporters = new HashMap<>();
        this.metaDataMap = new HashMap<>();
        this.modelStudiesAsColumn = false;
        this.columnFullNameList = new ArrayList<>();
        this.studies = new HashMap<>();
    }

    /**
     * This method is called once for each ODM file. It separates the processing of the ODM file
     * in two phases: first the metadata and then the clinical data.
     *
     * @param odm The odm object with a tree-structure that is constructed from the XML file by
     *            the unmarshaller. The unmarshaller uses automatically generated Java sources,
     *            generated from xsd files.
     * @param exportFilePath The path to the directory in which the export files will be written.
     * @param exportToI2b2Light Is true when the export should be made to i2b2-light instead of -full.
     *@param propertiesFilePath the file path to the properties.  @throws IOException An input-output exception.
     * @throws JAXBException A Java Architecture for XML Binding exception.
     */
    public void processODM(final ODM odm, final String exportFilePath, Boolean exportToI2b2Light, final String propertiesFilePath)
        throws IOException, JAXBException {
        this.odm = odm;
        this.exportFilePath = exportFilePath + File.separator;
        this.exportToI2b2Light = exportToI2b2Light;

        processODMStudy(propertiesFilePath);
        processODMClinicalData();
    }

    /**
     * Closes the files after the data has been written.
     */
    public void closeExportWriters() {
        for (ODMcomplexTypeDefinitionStudy study : odm.getStudy()) {
            final String studyName = study.getGlobalVariables().getStudyName().getValue();
            if (fileExporters.containsKey(studyName)) {
                logger.debug("Closing file exporter for study " + studyName);
                fileExporters.get(studyName).close();
            }
        }
    }

    /**
     * Process the metadata by traversing all the studies in the ODM file.
     *
     * @param propertiesFilePath the file path to the properties.
     * @throws IOException An input-output exception.
     * @throws JAXBException A Java Architecture for XML Binding exception.
     */
    private void processODMStudy(final String propertiesFilePath) throws IOException, JAXBException {
        // Need to traverse through the study metadata to:
        // 1) Lookup all metadata definition values and paths for each tree leaf.
        // 2) Pass the metadata to the corresponding file exporter.

        for (ODMcomplexTypeDefinitionStudy study : odm.getStudy()) {
            saveStudy(study, propertiesFilePath);
        }
        writeStudySites();
    }

    /**
     * The metadata of studies are sometimes defined by other studies. This correspondence is saved
     * in the studies map, where the study is the key and the defining study the value. Take for instance
     * the following studies map:
     * study 1 - study 1
     * study A - study 1
     * study B - study 1
     * study 2 - study 2
     * We want an extra line in the columns file of study 1, and three extra lines in its wordmap file. We
     * do not want anything extra for study 2.
     *
     * @throws IOException An input-output exception.
     */
    private void writeStudySites() throws IOException {
        final Map<String, Boolean> handledStudies = new HashMap<>();
        for (String evaluatedStudyName : studies.keySet()) {
            handledStudies.put(studies.get(evaluatedStudyName), false);
        }
        for (String evaluatedStudyName : studies.keySet()) {
            final String definingStudyName = studies.get(evaluatedStudyName);
            if (!evaluatedStudyName.equals(definingStudyName) && !handledStudies.get(definingStudyName)) {
                final String oidPath = definingStudyName + SEP + STUDYSITE;
                fileExporters.get(definingStudyName).storeColumn("", "", "", STUDYSITE, oidPath);
                for (String studyName : studies.keySet()) {
                    if (studies.get(studyName).equals(definingStudyName)) {
                        fileExporters.get(definingStudyName).storeWord(studyName);
                    }
                }
                handledStudies.put(definingStudyName, true);
            }
        }
    }

    /**
     * Handles a study in three steps:
     * 1. Handle included metadata issue.
     * 2. Create a fileExporter object for the study, but not if it is a study-site. If the study is not
     *    a study-site, it contains its own metadata. It is then called a defining study.
     * 3. Loop through all the events.
     *
     * @param study the current ODM study
     *              definingStudy: the furthest study from which metadata is included with an include tag
     * @param propertiesFilePath the file path to the properties.
     * @throws IOException An input-output exception.
     * @throws JAXBException A Java Architecture for XML Binding exception.
     */
    private void saveStudy(final ODMcomplexTypeDefinitionStudy study, final String propertiesFilePath)
        throws IOException, JAXBException {
        final String studyName = study.getGlobalVariables().getStudyName().getValue();
        final String studyOID = study.getOID();

        // 1. Handle included metadata issue.
        final ODMcomplexTypeDefinitionMetaDataVersion metaData = study.getMetaDataVersion().get(0);
        final ODMcomplexTypeDefinitionInclude includedMetaData = metaData.getInclude();
        final List<MetaDataWithIncludes> metaDataWithIncludesList = new ArrayList<>();
        if (includedMetaData != null) {
            final String includedMetaDataKey = includedMetaData.getStudyOID() + SEP + includedMetaData.getMetaDataVersionOID();
            if (metaDataMap.containsKey(includedMetaDataKey)) {
//                The following two lines may be of use in the case of recursive includes. We assume that
//                the situation will not become so complicated for now.
//                List<MetaDataWithIncludes> tbd = null;
//                MetaDataWithIncludes backup = new MetaDataWithIncludes(metaDataMap.get(includedMetaDataKey), tbd);
                metaDataWithIncludesList.add(metaDataMap.get(includedMetaDataKey));
            }
            modelStudiesAsColumn = true;
        }
        metaDataWithIncludes = new MetaDataWithIncludes(metaData, studyOID, metaDataWithIncludesList);
        metaDataMap.put(getMetaDataKey(study), metaDataWithIncludes);

        // 2. Create a fileExporter for the defining studies.
        final ODMcomplexTypeDefinitionStudy definingStudy = metaDataWithIncludes.getDefiningStudy(odm);
        final String definingStudyName = definingStudy.getGlobalVariables().getStudyName().getValue();
        studies.put(studyName, definingStudyName);

        if (!fileExporters.containsKey(definingStudyName)) {
            logger.debug("Creating file exporter for study " + definingStudyName);
            final Configuration configuration = new Configuration(propertiesFilePath);
            final FileExporter fileExporter;
            if (exportToI2b2Light) {
                fileExporter = new FileExporterTransmart(exportFilePath, definingStudyName, configuration);
            } else {
                fileExporter = new FileExporterFull(exportFilePath, definingStudyName, configuration);
            }
            fileExporters.put(definingStudyName, fileExporter);
        }

        // 3. Loop through the events.
        if (metaData.getProtocol().getStudyEventRef() != null && includedMetaData == null) {
            for (ODMcomplexTypeDefinitionStudyEventRef studyEventRef : metaData.getProtocol().getStudyEventRef()) {
                final ODMcomplexTypeDefinitionStudyEventDef studyEventDef =
                        metaDataWithIncludes.getStudyEventDef(studyEventRef.getStudyEventOID());
                saveEvent(definingStudy, studyEventDef);
            }
        }
    }

    /**
     * Get the metadata for a given study, even if the metadata is stored in another study and
     * referenced by an include tag.
     *
     * @param study The given study.
     * @return The metadata with includes object.
     */
    private MetaDataWithIncludes getMetaData(final ODMcomplexTypeDefinitionStudy study) {
        return metaDataMap.get(getMetaDataKey(study));
    }

    /**
     * Returns a unique ID for the first pack of metadata (we assume there is only one pack of metadata)
     * for a given study.
     *
     * @param study The given study.
     * @return The unique ID.
     */
    private String getMetaDataKey(final ODMcomplexTypeDefinitionStudy study) {
        return study.getOID() + SEP + study.getMetaDataVersion().get(0).getOID();
    }

    /**
     * Handles an event by looping through all its forms (= Case Report Forms).
     *
     * @param definingStudy The study in which the metadata is defined.
     * @param studyEventDef The event object, part of the odm object, that contains the data.
     * @throws IOException An input-output exception.
     * @throws JAXBException A Java Architecture for XML Binding exception.
     */
    private void saveEvent(final ODMcomplexTypeDefinitionStudy definingStudy,
                           final ODMcomplexTypeDefinitionStudyEventDef studyEventDef)
            throws JAXBException, IOException {

        if (studyEventDef.getFormRef() != null) {
            for (ODMcomplexTypeDefinitionFormRef formRef : studyEventDef.getFormRef()) {
//              final ODMcomplexTypeDefinitionFormDef formDef = ODMUtil.getFormDef(study, formRef.getFormOID());
                final ODMcomplexTypeDefinitionFormDef formDef = metaDataWithIncludes.getFormDef(formRef.getFormOID());

                saveForm(definingStudy, studyEventDef, formDef);
            }
        }
    }

    /**
     * Handles a form by looping through all its itemgroups.
     *
     * @param definingStudy The study in which the metadata is defined.
     * @param studyEventDef The event object, part of the study object, that contains the data.
     * @param formDef  The form object, part of the event object, that contains the data.
     * @throws IOException An input-output exception.
     * @throws JAXBException A Java Architecture for XML Binding exception.
     */
    private void saveForm(final ODMcomplexTypeDefinitionStudy definingStudy,
                          final ODMcomplexTypeDefinitionStudyEventDef studyEventDef,
                          final ODMcomplexTypeDefinitionFormDef formDef)
            throws JAXBException, IOException {

        if (formDef.getItemGroupRef() != null) {
            for (ODMcomplexTypeDefinitionItemGroupRef itemGroupRef : formDef.getItemGroupRef()) {
                final ODMcomplexTypeDefinitionItemGroupDef itemGroupDef =
                        metaDataWithIncludes.getItemGroupDef(itemGroupRef.getItemGroupOID());

                saveItemGroup(definingStudy, studyEventDef, formDef, itemGroupDef);
            }
        }
    }

    /**
     * Handles an itemGroup by looping through all its items.
     *
     * @param definingStudy The study in which the metadata is defined.
     * @param studyEventDef The event object, part of the study object, that contains the data.
     * @param formDef  The form object, part of the event object, that contains the data.
     * @param itemGroupDef The itemGroup object, part of the form object, that contains the data.
     * @throws IOException An input-output exception.
     * @throws JAXBException A Java Architecture for XML Binding exception.
     */
    private void saveItemGroup(final ODMcomplexTypeDefinitionStudy definingStudy,
                               final ODMcomplexTypeDefinitionStudyEventDef studyEventDef,
                               final ODMcomplexTypeDefinitionFormDef formDef,
                               final ODMcomplexTypeDefinitionItemGroupDef itemGroupDef)
            throws JAXBException, IOException {

        if (itemGroupDef.getItemRef() != null) {
            for (ODMcomplexTypeDefinitionItemRef itemRef : itemGroupDef.getItemRef()) {
                final ODMcomplexTypeDefinitionItemDef itemDef =
                        metaDataWithIncludes.getItemDef(itemRef.getItemOID());

                saveItem(definingStudy, studyEventDef, formDef, itemGroupDef, itemDef);
            }
        }
    }

    /**
     * Handles an item. In this method the data assembled in all the loops are written to the
     * columns file. After that it loops through the codes list for each item.
     *
     * @param definingStudy The study in which the metadata is defined.
     * @param studyEventDef The event object, part of the study object, that contains the data.
     * @param formDef  The form object, part of the event object, that contains the data.
     * @param itemGroupDef The itemGroup object, part of the form object, that contains the data.
     * @param itemDef The item object, part of the itemGroup object, that contains the data.
     * @throws IOException An input-output exception.
     * @throws JAXBException A Java Architecture for XML Binding exception.
     */
    private void saveItem(final ODMcomplexTypeDefinitionStudy definingStudy,
                          final ODMcomplexTypeDefinitionStudyEventDef studyEventDef,
                          final ODMcomplexTypeDefinitionFormDef formDef,
                          final ODMcomplexTypeDefinitionItemGroupDef itemGroupDef,
                          final ODMcomplexTypeDefinitionItemDef itemDef)
            throws JAXBException, IOException {
        final String studyName      = definingStudy.getGlobalVariables().getStudyName().getValue();
        final String studyEventName = getTranslatedDescription(studyEventDef.getDescription(), LANGUAGE, studyEventDef.getName());
        final String formName       = getTranslatedDescription(formDef.getDescription(),       LANGUAGE, formDef.getName());
        final String itemGroupName  = getTranslatedDescription(itemGroupDef.getDescription(),  LANGUAGE, itemGroupDef.getName());
        final String namePath       = studyEventName + PLUS + formName + PLUS + itemGroupName;
        final String preferredItemName = getPreferredItemName(itemDef, namePath);

        final String oidPath = definingStudy.getOID() + SEP
                + studyEventDef.getOID() + SEP
                + formDef.getOID() + SEP
                + itemDef.getOID() + SEP;

        logger.trace("Write columns; study event name: " + studyEventName
                + "; form name: " + formName
                + "; item group name: " + itemGroupName
                + "; preferred item name: " + preferredItemName
                + "; OID path: " + oidPath);
        fileExporters.get(studyName).storeColumn(studyEventName, formName, itemGroupName, preferredItemName, oidPath);

        if (itemDef.getCodeListRef() != null) {
            final ODMcomplexTypeDefinitionCodeList codeList = ODMUtil.getCodeList(definingStudy,
                    itemDef.getCodeListRef().getCodeListOID());

            if (codeList != null) {
                for (ODMcomplexTypeDefinitionCodeListItem codeListItem : codeList.getCodeListItem()) {
                    saveCodeListItem(definingStudy, codeListItem);
                }
            }
        }
    }

    /**
     * This method strikes a balance in choosing for the human readable name in questionValue, or a
     * unique identifier in itemName. At least the namePath + preferredItemName must be unique for
     * loading in tranSMART. HTML tags are removed from the human readable strings.
     *
     * @param itemDef The item object in which both questionValue as itemName can be found.
     * @param namePath The namePath that identifies a column, for checking uniqueness.
     * @return The preferred name for an item to be written to the columns file.
     */
    private String getPreferredItemName(final ODMcomplexTypeDefinitionItemDef itemDef, final String namePath) {
        final String itemName       = getTranslatedDescription(itemDef.getDescription(),       LANGUAGE, itemDef.getName());
        final String questionValue  = getQuestionValue(itemDef);
        final String preferredItemNameWithHtml = questionValue != null ? questionValue : itemName;
        String preferredItemName =  Jsoup.parse(preferredItemNameWithHtml).text();
        for (String fullNamePath : columnFullNameList) {
            if (fullNamePath.equals(namePath + PLUS + preferredItemName)) {
                logger.warn("\"" + fullNamePath + "\" was found more than once. "
                        + itemName + " is now taken as preferred name.");
                preferredItemName = itemName;
            }
        }
        columnFullNameList.add(namePath + PLUS + preferredItemName);
        return preferredItemName;
    }

    /**
     * This method checks whether a question value is present in the item object and returns it.
     * It returns null otherwise.
     *
     * @param itemDef The item object.
     * @return The question value, or null.
     */
    private String getQuestionValue(final ODMcomplexTypeDefinitionItemDef itemDef) {
        return itemDef.getQuestion() != null
                && itemDef.getQuestion().getTranslatedText() != null
                && itemDef.getQuestion().getTranslatedText().size() >= 1
                && itemDef.getQuestion().getTranslatedText().get(0) != null
                && itemDef.getQuestion().getTranslatedText().get(0).getValue() != null
                && !"".equals(itemDef.getQuestion().getTranslatedText().get(0).getValue().trim())
                ? itemDef.getQuestion().getTranslatedText().get(0).getValue().trim()
                : null;
    }

    /**
     * This method passes the data of value codes to the wordmap writer.
     *
     * @param definingStudy The study in which the metadata is defined.
     * @param codeListItem Some items have a fixed list of codes as values, which can be replaced by numbers.
     *                     A codeListItem is the object that contains one such value.
     * @throws IOException An input-output exception.
     */
    private void saveCodeListItem(final ODMcomplexTypeDefinitionStudy definingStudy,
                                  final ODMcomplexTypeDefinitionCodeListItem codeListItem) throws IOException {
        final String studyName = definingStudy.getGlobalVariables().getStudyName().getValue();
        final String dataValue = ODMUtil.getTranslatedValue(codeListItem, LANGUAGE);
        fileExporters.get(studyName).storeWord(dataValue);

    }

    /**
     * This method returns a translated value according to a given language.
     *
     * @param description The data object in which a translated value may be present.
     * @param lang The language.
     * @param defaultValue The value that is returned if there is no translated value present.
     * @return The translated value if it is present. Otherwise keeps the default value.
     */
    private String getTranslatedDescription(final ODMcomplexTypeDefinitionDescription description,
                                            final String lang,
                                            final String defaultValue) {
        if (description != null) {
            for (ODMcomplexTypeDefinitionTranslatedText translatedText : description.getTranslatedText()) {
                if (translatedText.getLang().equals(lang)) {
                    return translatedText.getValue();
                }
            }
        }
        return defaultValue;
    }

    /********************************************************************************************
     * Above this point are the methods for processing the study metadata.
     * Below are the methods for processing the clinical data itself.
     ********************************************************************************************/

    /**
     * This method loops through the clinical data objects, which are part of the odm tree, in order
     * to save its contents.
     */
    private void processODMClinicalData() {
        for (ODMcomplexTypeDefinitionClinicalData clinicalData : odm.getClinicalData()) {
            if (clinicalData.getSubjectData() != null) {
                final String studyOID = clinicalData.getStudyOID();
                final ODMcomplexTypeDefinitionStudy study = ODMUtil.getStudy(odm, studyOID);
                if (study != null) {
                    saveClinicalData(study, clinicalData);
                } else {
                    logger.error("ODM does not contain study metadata for study OID " + studyOID);
                }
            }
        }
    }

    /**
     * This method loops through the data of one patient (aka subject) in order to save its contents.
     *
     * @param study The study in which the patient is stored.
     * @param clinicalData The clinical data object in which the patient is stored.
     */
    private void saveClinicalData(final ODMcomplexTypeDefinitionStudy study,
                                  final ODMcomplexTypeDefinitionClinicalData clinicalData) {
        final String studyOID = clinicalData.getStudyOID();
        logger.info("Write Clinical data for study OID " + studyOID + " to clinical data file...");

        for (ODMcomplexTypeDefinitionSubjectData subjectData : clinicalData.getSubjectData()) {
            if (subjectData.getStudyEventData() != null) {
                saveSubjectData(study, subjectData);
            }
        }
    }

    /**
     * This method loops through the events, and, if necessary, writes in which study-site a patient
     * was treated.
     *
     * @param study The study or study-site in which the patient is treated.
     * @param subjectData The data of a particular patient.
     */
    private void saveSubjectData(final ODMcomplexTypeDefinitionStudy study,
                                 final ODMcomplexTypeDefinitionSubjectData subjectData) {
        final ODMcomplexTypeDefinitionStudy definingStudy = metaDataMap.get(getMetaDataKey(study)).getDefiningStudy(odm);
        final String definingStudyName = definingStudy.getGlobalVariables().getStudyName().getValue();
        final String oidPath = definingStudyName + SEP + STUDYSITE;
        final String studyName = study.getGlobalVariables().getStudyName().getValue();
        final String patientId = subjectData.getSubjectKey();

        for (ODMcomplexTypeDefinitionStudyEventData eventData : subjectData.getStudyEventData()) {
            if (eventData.getFormData() != null) {
                saveEventData(study, subjectData, eventData);
            }
        }
        if (modelStudiesAsColumn) {
            fileExporters.get(definingStudyName).storeClinicalDataInfo(oidPath, studyName, patientId,
                    null, null, null, null);
        }
    }

    /**
     * This method loops through the forms of a particular event of a particular patient.
     *
     * @param study The study or study-site in which the patient is treated.
     * @param subjectData The data of a particular patient.
     * @param eventData The data of a particular event.
     */
    private void saveEventData(final ODMcomplexTypeDefinitionStudy study,
                               final ODMcomplexTypeDefinitionSubjectData subjectData,
                               final ODMcomplexTypeDefinitionStudyEventData eventData) {
        for (ODMcomplexTypeDefinitionFormData formData : eventData.getFormData()) {
            if (formData.getItemGroupData() != null) {
                saveFormData(study, subjectData, eventData, formData);
            }
        }
    }

    /**
     * This method loops through the item groups in a particular form.
     *
     * @param study The study or study-site in which the patient is treated.
     * @param subjectData The data of a particular patient.
     * @param eventData The data of a particular event.
     * @param formData The data of a particular form.
     */
    private void saveFormData(final ODMcomplexTypeDefinitionStudy study,
                              final ODMcomplexTypeDefinitionSubjectData subjectData,
                              final ODMcomplexTypeDefinitionStudyEventData eventData,
                              final ODMcomplexTypeDefinitionFormData formData) {
        for (ODMcomplexTypeDefinitionItemGroupData itemGroupData : formData.getItemGroupData()) {
            if (itemGroupData.getItemDataGroup() != null) {
                saveItemGroupData(study, subjectData, eventData, formData, itemGroupData);
            }
        }
    }

    /**
     * This method loops through the items of a particular item group.
     *
     * @param study The study or study-site in which the patient is treated.
     * @param subjectData The data of a particular patient.
     * @param eventData The data of a particular event.
     * @param formData The data of a particular form.
     * @param itemGroupData The data of a particular item group.
     */
    private void saveItemGroupData(final ODMcomplexTypeDefinitionStudy study,
                                   final ODMcomplexTypeDefinitionSubjectData subjectData,
                                   final ODMcomplexTypeDefinitionStudyEventData eventData,
                                   final ODMcomplexTypeDefinitionFormData formData,
                                   final ODMcomplexTypeDefinitionItemGroupData itemGroupData) {
        for (ODMcomplexTypeDefinitionItemData itemData : itemGroupData.getItemDataGroup()) {
            if (itemData.getValue() != null) {
                saveItemData(study, subjectData, eventData, formData, itemGroupData, itemData);
            }
        }
    }

    /**
     * This method sticks the data together that was assembled in the series of nested loops and passes
     * it to the clinical data file. Three things are constructed: an OIDPath, a final value and a patient
     * identifier.
     *
     * @param study The study or study-site in which the patient is treated.
     * @param subjectData The data of a particular patient.
     * @param eventData The data of a particular event.
     * @param formData The data of a particular form.
     * @param itemGroupData The data of a particular item group.
     * @param itemData The data of a particular item.
     */
    private void saveItemData(final ODMcomplexTypeDefinitionStudy study,
                              final ODMcomplexTypeDefinitionSubjectData subjectData,
                              final ODMcomplexTypeDefinitionStudyEventData eventData,
                              final ODMcomplexTypeDefinitionFormData formData,
                              final ODMcomplexTypeDefinitionItemGroupData itemGroupData,
                              final ODMcomplexTypeDefinitionItemData itemData) {
        final ODMcomplexTypeDefinitionStudy definingStudy = metaDataMap.get(getMetaDataKey(study)).getDefiningStudy(odm);
        final String definingStudyName = definingStudy.getGlobalVariables().getStudyName().getValue();
        final String oidPath = definingStudy.getOID() + SEP
                             + eventData.getStudyEventOID() + SEP
                             + formData.getFormOID() + SEP
                             + itemData.getItemOID() + SEP;
        final String itemValue = itemData.getValue();
//      final ODMcomplexTypeDefinitionItemDef item = ODMUtil.getItem(study, itemData.getItemOID());
        final ODMcomplexTypeDefinitionItemDef itemDef = getMetaData(study).getItemDef(itemData.getItemOID());
        final String wordValue;
        final BigDecimal bigDecimal;
        final String patientId = subjectData.getSubjectKey();
        final String eventId = eventData.getStudyEventOID();
        final String eventRepeatKey = eventData.getStudyEventRepeatKey();
        final String itemGroupId = itemGroupData.getItemGroupOID();
        final String itemGroupRepeatKey = itemGroupData.getItemGroupRepeatKey();

        if (itemDef.getCodeListRef() != null) {
            bigDecimal = null;

            final ODMcomplexTypeDefinitionCodeList codeList = getMetaData(study).getCodeList(itemDef.getCodeListRef().getCodeListOID());
            final ODMcomplexTypeDefinitionCodeListItem codeListItem = ODMUtil.getCodeListItem(codeList, itemValue);

            if (codeListItem == null) {
                logger.error("Code list item for coded value: " + itemValue + " not found in code list: " + codeList.getOID());
                return;
            } else {
                wordValue = ODMUtil.getTranslatedValue(codeListItem, LANGUAGE);
            }
        } else if (ODMUtil.isNumericDataType(itemDef.getDataType())) {
            wordValue = "";
            bigDecimal = itemValue == null || "".equals(itemValue.trim()) ? null : new BigDecimal(itemValue);
        } else {
            wordValue = itemValue;
            bigDecimal = null;
        }

        final String finalValue = (bigDecimal != null) ? bigDecimal.toString() : wordValue;
        fileExporters.get(definingStudyName).storeClinicalDataInfo(oidPath, finalValue, patientId,
                eventId, eventRepeatKey, itemGroupId, itemGroupRepeatKey);
    }
}
