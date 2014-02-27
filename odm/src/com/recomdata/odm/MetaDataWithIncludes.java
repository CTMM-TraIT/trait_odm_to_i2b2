package com.recomdata.odm;

import org.cdisk.odm.jaxb.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Freek
 * Date: 11-2-14
 * Time: 12:00
 */
public class MetaDataWithIncludes {
    private ODMcomplexTypeDefinitionMetaDataVersion metaDataVersion;
    private String studyOID;
    private List<MetaDataWithIncludes> metaDataIncludes;

    public MetaDataWithIncludes(final ODMcomplexTypeDefinitionMetaDataVersion metaDataVersion,
                                String studyOID,
                                final List<MetaDataWithIncludes> metaDataIncludes) {
        this.metaDataVersion = metaDataVersion;
        this.studyOID = studyOID;
        this.metaDataIncludes = metaDataIncludes;
    }

    public String getDefiningStudyOID () {
        String definingStudyOID = studyOID;
        if (metaDataIncludes.size() > 0) {
            definingStudyOID = metaDataIncludes.get(metaDataIncludes.size()-1).studyOID;
        }
        return definingStudyOID;
    }

    public ODMcomplexTypeDefinitionStudyEventDef getStudyEventDef(String studyEventOID) {
        ODMcomplexTypeDefinitionStudyEventDef studyEventDef = searchStudyEventDef(studyEventOID);

        int includeIndex = 0;
        while (studyEventDef == null && includeIndex < metaDataIncludes.size()) {
            studyEventDef = metaDataIncludes.get(includeIndex).getStudyEventDef(studyEventOID);
            includeIndex++;
        }

        return studyEventDef;
    }

    private ODMcomplexTypeDefinitionStudyEventDef searchStudyEventDef(final String studyEventOID) {
        ODMcomplexTypeDefinitionStudyEventDef result = null;
        for (ODMcomplexTypeDefinitionStudyEventDef studyEventDef : metaDataVersion.getStudyEventDef()) {
            if (studyEventDef.getOID().equals(studyEventOID)) {
                result = studyEventDef;
            }
        }
        return result;
    }

    public ODMcomplexTypeDefinitionFormDef getFormDef(String formOID) {
        ODMcomplexTypeDefinitionFormDef formDef = searchFormDef(formOID);

        int includeIndex = 0;
        while (formDef == null && includeIndex < metaDataIncludes.size()) {
            formDef = metaDataIncludes.get(includeIndex).getFormDef(formOID);
            includeIndex++;
        }

        return formDef;
    }

    private ODMcomplexTypeDefinitionFormDef searchFormDef(final String formOID) {
        ODMcomplexTypeDefinitionFormDef result = null;
        for (ODMcomplexTypeDefinitionFormDef formDef : metaDataVersion.getFormDef()) {
            if (formDef.getOID().equals(formOID)) {
                result = formDef;
            }
        }
        return result;
    }

    public ODMcomplexTypeDefinitionItemGroupDef getItemGroupDef(String itemGroupOID) {
        ODMcomplexTypeDefinitionItemGroupDef itemGroupDef = searchItemGroupDef(itemGroupOID);

        int includeIndex = 0;
        while (itemGroupDef == null && includeIndex < metaDataIncludes.size()) {
            itemGroupDef = metaDataIncludes.get(includeIndex).getItemGroupDef(itemGroupOID);
            includeIndex++;
        }

        return itemGroupDef;
    }

    private ODMcomplexTypeDefinitionItemGroupDef searchItemGroupDef(final String itemGroupOID) {
        ODMcomplexTypeDefinitionItemGroupDef result = null;
        for (ODMcomplexTypeDefinitionItemGroupDef itemGroupDef : metaDataVersion.getItemGroupDef()) {
            if (itemGroupDef.getOID().equals(itemGroupOID)) {
                result = itemGroupDef;
            }
        }
        return result;
    }

    public ODMcomplexTypeDefinitionItemDef getItemDef(String itemOID) {
        ODMcomplexTypeDefinitionItemDef itemDef = searchItemDef(itemOID);

        int includeIndex = 0;
        while (itemDef == null && includeIndex < metaDataIncludes.size()) {
            itemDef = metaDataIncludes.get(includeIndex).getItemDef(itemOID);
            includeIndex++;
        }

        return itemDef;
    }

    private ODMcomplexTypeDefinitionItemDef searchItemDef(final String itemOID) {
        ODMcomplexTypeDefinitionItemDef result = null;
        for (ODMcomplexTypeDefinitionItemDef itemDef : metaDataVersion.getItemDef()) {
            if (itemDef.getOID().equals(itemOID)) {
                result = itemDef;
            }
        }
        return result;
    }

    public ODMcomplexTypeDefinitionCodeList getCodeList(String codeListOID) {
        ODMcomplexTypeDefinitionCodeList codeList = searchCodeList(codeListOID);

        int includeIndex = 0;
        while (codeList == null && includeIndex < metaDataIncludes.size()) {
            codeList = metaDataIncludes.get(includeIndex).getCodeList(codeListOID);
            includeIndex++;
        }

        return codeList;
    }

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
