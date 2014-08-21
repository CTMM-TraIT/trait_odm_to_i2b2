package com.recomdata.i2b2.util;

/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 * ODMUtil class resolves between the definition and the reference for
 * ODM classes.
 * This is a singleton class.
 * @author Alex Wu
 * @date August 31, 2011
 */
import java.util.List;

import javax.xml.bind.JAXBException;
import org.cdisk.odm.jaxb.*;

public class ODMUtil {
	public static ODMcomplexTypeDefinitionStudy getStudy(ODM odm, String studyOID) {
		for (ODMcomplexTypeDefinitionStudy study : odm.getStudy()) {
			if (study.getOID().equals(studyOID)) {
				return study;
			}
		}
		
		return null;
	}

	/**
	 * Resolve StudyEventDef from StudyEventRef
	 */
	public static ODMcomplexTypeDefinitionStudyEventDef getStudyEvent(ODMcomplexTypeDefinitionStudy study, String studyEventOID)
	 	throws JAXBException
	{		
		ODMcomplexTypeDefinitionMetaDataVersion version = study.getMetaDataVersion().get(0);

		if ((version.getStudyEventDef() != null) && (version.getStudyEventDef().size() > 0)) {
			for (ODMcomplexTypeDefinitionStudyEventDef studyEventDef : version.getStudyEventDef()) {
				if(studyEventDef.getOID().equals(studyEventOID)){
					return studyEventDef;
				}
			}
		}
		
		return null;
	}

	/**Resolve FormDef from FormRef
	*/
	public static ODMcomplexTypeDefinitionFormDef getForm(ODMcomplexTypeDefinitionStudy study, String formOID)
		 	throws JAXBException
	{
		ODMcomplexTypeDefinitionMetaDataVersion version = study.getMetaDataVersion().get(0);

		if ((version.getFormDef() != null) && (version.getFormDef().size() > 0)) {
			for (ODMcomplexTypeDefinitionFormDef formDef : version.getFormDef()) {
				if(formDef.getOID().equals(formOID)){
					return formDef;
				}
			}
		}
		
		return null;
	}

	/**Resolve ItemGroupDef from ItemGroupRef
	*/
	public static ODMcomplexTypeDefinitionItemGroupDef getItemGroup(ODMcomplexTypeDefinitionStudy study, String itemGroupOID)
			throws JAXBException
	{
		ODMcomplexTypeDefinitionMetaDataVersion version = study.getMetaDataVersion().get(0);

		if ((version.getItemGroupDef() != null) && (version.getItemGroupDef().size() > 0)) {
			for (ODMcomplexTypeDefinitionItemGroupDef itemGroupDef : version.getItemGroupDef()) {
				if(itemGroupDef.getOID().equals(itemGroupOID)){
					return itemGroupDef;
				}
			}
		}

		return null;
	}

	/**Resolve ItemDef from ItemRef
	*/
	public static ODMcomplexTypeDefinitionItemDef getItem(ODMcomplexTypeDefinitionStudy study, String itemOID)
			throws JAXBException
	{
		ODMcomplexTypeDefinitionMetaDataVersion version = study.getMetaDataVersion().get(0);

		if ((version.getItemDef() != null) && (version.getItemDef().size() > 0)) {
			for (ODMcomplexTypeDefinitionItemDef itemDef : version.getItemDef()) {
				if(itemDef.getOID().equals(itemOID)){
					return itemDef;
				}
			}
		}

		return null;
	}

	/**Resolve CodListDef from CodeListRef
	*/
	public static ODMcomplexTypeDefinitionCodeList getCodeList(ODMcomplexTypeDefinitionStudy study, String codeListOID)
			throws JAXBException
	{
		ODMcomplexTypeDefinitionMetaDataVersion version = study.getMetaDataVersion().get(0);

		if ((version.getCodeList() != null) && (version.getCodeList().size() > 0)) {
			for (ODMcomplexTypeDefinitionCodeList codeListDef : version.getCodeList()) {
				if(codeListDef.getOID().equals(codeListOID)){
					return codeListDef;
				}
			}
		}

		return null;
	}

	public static String[] getCodeListValues(ODMcomplexTypeDefinitionCodeList codeList, String lang) {
		List<ODMcomplexTypeDefinitionCodeListItem> codeListItems = codeList.getCodeListItem();
		String[] codeListValues = new String[codeListItems.size()];
		
		for (int i = 0; i < codeListValues.length; i++) {
			ODMcomplexTypeDefinitionCodeListItem codeListItem = codeListItems.get(i);
		
			codeListValues[i] = getTranslatedValue(codeListItem, lang);
		}
		
		return codeListValues;
	}

	/**
	 * Look for a translated value for the given item. Returns the language specific value, or the
	 * first value if the translated value could not be found.
	 */
	public static String getTranslatedValue(
			ODMcomplexTypeDefinitionCodeListItem codeListItem, String lang) {
		String translatedValue = null;
		
		for (ODMcomplexTypeDefinitionTranslatedText translatedText :
			codeListItem.getDecode().getTranslatedText()) {
			// TODO: the language attribute is not always available for OpenClinica data.
			if (translatedText.getLang() != null && translatedText.getLang().equals("en")) {
				translatedValue = translatedText.getValue();
				break;
			}
		}
	
		if (translatedValue == null) {
			// take first value if we can't find an english translation
			translatedValue = codeListItem.getDecode().getTranslatedText().get(0).getValue();
		}
		
		return translatedValue;
	}
	
	public static boolean isNumericDataType(DataType dataType) {
		switch (dataType) {
		case INTEGER:
		case FLOAT:
		case DOUBLE:
			return true;
		}
		
		return false;
	}

	public static ODMcomplexTypeDefinitionCodeListItem getCodeListItem(
			ODMcomplexTypeDefinitionCodeList codeList, String codedValue) {
		for (ODMcomplexTypeDefinitionCodeListItem codeListItem : codeList.getCodeListItem()) {
			if (codeListItem.getCodedValue().equals(codedValue)) {
				return codeListItem;
			}
		}
		
		return null;
	}
}