/**
 * Copyright(c) 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package com.recomdata.odm;

import java.util.List;

import org.cdisk.odm.jaxb.ODM;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCodeList;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemGroupDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionMetaDataVersion;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudy;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyEventDef;

/**
 * A MetaDataWithIncludes object contains a block of metadata that belongs to a study, even if that
 * metadata is spread over different metadata blocks and over different studies. It contains all the
 * information to retrieve the correct metadata.
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blondé</a>
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class MetaDataWithIncludes {

    /**
     * The classical metadata block, which is incomplete if it contains an includes tag.
     */
    private ODMcomplexTypeDefinitionMetaDataVersion metaDataVersion;

    /**
     * The study that is described by the metadata.
     */
    private String studyOID;

    /**
     * The list of all the metadata blocks (= metadata versions) that are referred to by include tags,
     * or includes in includes in includes, etc.
     */
    private List<MetaDataWithIncludes> metaDataIncludes;

    /**
     * Constructs a metadata object for a given study and a given metadata version in the study.
     *
     * @param metaDataVersion The given metadata version.
     * @param studyOID The given study.
     * @param metaDataIncludes The list with all the (recursively) included metadata.
     */
    public MetaDataWithIncludes(final ODMcomplexTypeDefinitionMetaDataVersion metaDataVersion,
                                final String studyOID,
                                final List<MetaDataWithIncludes> metaDataIncludes) {
        this.metaDataVersion = metaDataVersion;
        this.studyOID = studyOID;
        this.metaDataIncludes = metaDataIncludes;
    }

    /**
     * This method assumes that the last study in the list of recursively included metadata is the
     * study that defines the metadata. This study is therefore called the defining study. The method
     * returns this study.
     *
     * @param odm The whole odm object, which contains the link between study and studyOID.
     * @return The defining study.
     */
    public ODMcomplexTypeDefinitionStudy getDefiningStudy(final ODM odm) {
        String definingStudyOID = studyOID;
        if (metaDataIncludes.size() > 0) {
            definingStudyOID = metaDataIncludes.get(metaDataIncludes.size() - 1).studyOID;
        }

        for (ODMcomplexTypeDefinitionStudy definingStudy : odm.getStudy()) {
            if (definingStudy.getOID().equals(definingStudyOID)) {
                return definingStudy;
            }
        }
        return null;
    }

    /**
     * Returns the study event object for a given study event OID.
     *
     * @param studyEventOID The given study event OID.
     * @return The study event object.
     */
    public ODMcomplexTypeDefinitionStudyEventDef getStudyEventDef(final String studyEventOID) {
        ODMcomplexTypeDefinitionStudyEventDef studyEventDef = searchStudyEventDef(studyEventOID);

        int includeIndex = 0;
        while (studyEventDef == null && includeIndex < metaDataIncludes.size()) {
            studyEventDef = metaDataIncludes.get(includeIndex).getStudyEventDef(studyEventOID);
            includeIndex++;
        }

        return studyEventDef;
    }

    /**
     * A helper method that searches the study event object in the list of recursively included
     * metadata objects, for a given study event OID.
     *
     * @param studyEventOID The given study event OID.
     * @return The study event object.
     */
    private ODMcomplexTypeDefinitionStudyEventDef searchStudyEventDef(final String studyEventOID) {
        ODMcomplexTypeDefinitionStudyEventDef result = null;
        for (ODMcomplexTypeDefinitionStudyEventDef studyEventDef : metaDataVersion.getStudyEventDef()) {
            if (studyEventDef.getOID().equals(studyEventOID)) {
                result = studyEventDef;
            }
        }
        return result;
    }

    /**
     * Returns the form object for a given form OID.
     *
     * @param formOID The given form OID.
     * @return The form object.
     */
    public ODMcomplexTypeDefinitionFormDef getFormDef(final String formOID) {
        ODMcomplexTypeDefinitionFormDef formDef = searchFormDef(formOID);

        int includeIndex = 0;
        while (formDef == null && includeIndex < metaDataIncludes.size()) {
            formDef = metaDataIncludes.get(includeIndex).getFormDef(formOID);
            includeIndex++;
        }

        return formDef;
    }

    /**
     * A helper method that searches the form object in the list of recursively included
     * metadata objects, for a given form OID.
     *
     * @param formOID The given form OID.
     * @return The form object.
     */
    private ODMcomplexTypeDefinitionFormDef searchFormDef(final String formOID) {
        ODMcomplexTypeDefinitionFormDef result = null;
        for (ODMcomplexTypeDefinitionFormDef formDef : metaDataVersion.getFormDef()) {
            if (formDef.getOID().equals(formOID)) {
                result = formDef;
            }
        }
        return result;
    }

    /**
     * Returns the item group object for a given item group OID.
     *
     * @param itemGroupOID The given item group OID.
     * @return The item group object.
     */
    public ODMcomplexTypeDefinitionItemGroupDef getItemGroupDef(final String itemGroupOID) {
        ODMcomplexTypeDefinitionItemGroupDef itemGroupDef = searchItemGroupDef(itemGroupOID);

        int includeIndex = 0;
        while (itemGroupDef == null && includeIndex < metaDataIncludes.size()) {
            itemGroupDef = metaDataIncludes.get(includeIndex).getItemGroupDef(itemGroupOID);
            includeIndex++;
        }

        return itemGroupDef;
    }

    /**
     * A helper method that searches the item group object in the list of recursively included
     * metadata objects, for a given item group OID.
     *
     * @param itemGroupOID The given item group OID.
     * @return The item group object.
     */
    private ODMcomplexTypeDefinitionItemGroupDef searchItemGroupDef(final String itemGroupOID) {
        ODMcomplexTypeDefinitionItemGroupDef result = null;
        for (ODMcomplexTypeDefinitionItemGroupDef itemGroupDef : metaDataVersion.getItemGroupDef()) {
            if (itemGroupDef.getOID().equals(itemGroupOID)) {
                result = itemGroupDef;
            }
        }
        return result;
    }

    /**
     * Returns the item object for a given item OID.
     *
     * @param itemOID The given item OID.
     * @return The item object.
     */
    public ODMcomplexTypeDefinitionItemDef getItemDef(final String itemOID) {
        ODMcomplexTypeDefinitionItemDef itemDef = searchItemDef(itemOID);

        int includeIndex = 0;
        while (itemDef == null && includeIndex < metaDataIncludes.size()) {
            itemDef = metaDataIncludes.get(includeIndex).getItemDef(itemOID);
            includeIndex++;
        }

        return itemDef;
    }

    /**
     * A helper method that searches the item object in the list of recursively included
     * metadata objects, for a given item OID.
     *
     * @param itemOID The given item OID.
     * @return The item object.
     */
    private ODMcomplexTypeDefinitionItemDef searchItemDef(final String itemOID) {
        ODMcomplexTypeDefinitionItemDef result = null;
        for (ODMcomplexTypeDefinitionItemDef itemDef : metaDataVersion.getItemDef()) {
            if (itemDef.getOID().equals(itemOID)) {
                result = itemDef;
            }
        }
        return result;
    }


    /**
     * Returns the code list object for a given code list OID.
     *
     * @param codeListOID The given code list OID.
     * @return The code list object.
     */
    public ODMcomplexTypeDefinitionCodeList getCodeList(final String codeListOID) {
        ODMcomplexTypeDefinitionCodeList codeList = searchCodeList(codeListOID);

        int includeIndex = 0;
        while (codeList == null && includeIndex < metaDataIncludes.size()) {
            codeList = metaDataIncludes.get(includeIndex).getCodeList(codeListOID);
            includeIndex++;
        }

        return codeList;
    }

    /**
     * A helper method that searches the code list object in the list of recursively included
     * metadata objects, for a given code list OID.
     *
     * @param codeListOID The given code list OID.
     * @return The code list object.
     */
    private ODMcomplexTypeDefinitionCodeList searchCodeList(final String codeListOID) {
        ODMcomplexTypeDefinitionCodeList result = null;
        for (ODMcomplexTypeDefinitionCodeList codeList : metaDataVersion.getCodeList()) {
            if (codeList.getOID().equals(codeListOID)) {
                result = codeList;
            }
        }
        return result;
    }
}
