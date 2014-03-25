package com.recomdata.redcap.odm;

import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdisk.odm.jaxb.CLDataType;
import org.cdisk.odm.jaxb.DataType;
import org.cdisk.odm.jaxb.EventType;
import org.cdisk.odm.jaxb.FileType;
import org.cdisk.odm.jaxb.ODM;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionBasicDefinitions;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionClinicalData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCodeList;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCodeListItem;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCodeListRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionDecode;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionDescription;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionGlobalVariables;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemGroupData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemGroupDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemGroupRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionMetaDataVersion;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionProtocol;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionProtocolName;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudy;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyDescription;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyEventData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyEventDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyEventRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyName;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionSubjectData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionTranslatedText;
import org.cdisk.odm.jaxb.YesOrNo;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import com.recomdata.i2b2.util.ODMUtil;
import com.recomdata.redcap.ws.GetRedcapService;

public class Redcap2ODM {
    /**
     * The log for this class.
     */
	private Log log = LogFactory.getLog(Redcap2ODM.class);

	public ODM buildODM(String projectID, String projectName, String projectDescription,
			String baseUrl, String metadataPath, String clinicalDataPath, String token)
	throws JsonParseException, IOException, JAXBException, DatatypeConfigurationException {
		ODM odm = new ODM();
		odm.setCreationDateTime(RedcapODMUtils.getXMLGregorianCalendar(Calendar.getInstance().getTime()));
		odm.setFileOID("000-000-000");
		odm.setODMVersion("1.3.1");
		odm.setFileType(FileType.TRANSACTIONAL);
		odm.setAsOfDateTime(odm.getCreationDateTime());
		odm.setSourceSystem(new URL(baseUrl).getHost());

		JsonFactory factory = new JsonFactory();
		JsonParser metadataParser = factory.createJsonParser(readServiceData(projectID, baseUrl + metadataPath, token));

		ODMcomplexTypeDefinitionStudy odmStudy = null;

		try {
			odmStudy = buildStudyMetadata(projectID, projectName, projectDescription, metadataParser);
			odm.getStudy().add(odmStudy);
			metadataParser.close();
		} catch (IOException e) {
			if (metadataParser != null) {
				try { metadataParser.close(); } catch (IOException f) {}
			}

			throw e;
		}

		JsonParser clinicalDataParser = factory.createJsonParser(readServiceData(projectID, baseUrl + clinicalDataPath, token));

		try {
			ODMcomplexTypeDefinitionClinicalData clinicalData = buildClinicalData(odmStudy, clinicalDataParser);
			odm.getClinicalData().add(clinicalData);
		} catch (IOException e) {
			if (metadataParser != null) {
				try { metadataParser.close(); } catch (IOException f) {}
			}

			throw e;
		}

		return odm;
	}

	private ODMcomplexTypeDefinitionStudy buildStudyMetadata(
			String studyOID, String studyName, String studyDesc, JsonParser jp)
	throws JsonParseException, IOException, JAXBException {
		ODMcomplexTypeDefinitionStudy odmStudy = new ODMcomplexTypeDefinitionStudy();
		odmStudy.setOID(studyOID);

		ODMcomplexTypeDefinitionBasicDefinitions odmBasicDefs = new ODMcomplexTypeDefinitionBasicDefinitions();
		odmStudy.setBasicDefinitions(odmBasicDefs);

		ODMcomplexTypeDefinitionGlobalVariables odmGlobalVars = new ODMcomplexTypeDefinitionGlobalVariables();
		odmStudy.setGlobalVariables(odmGlobalVars);

		ODMcomplexTypeDefinitionStudyName odmStudyName = new ODMcomplexTypeDefinitionStudyName();
		odmStudyName.setValue(studyName);
		odmGlobalVars.setStudyName(odmStudyName);

		ODMcomplexTypeDefinitionStudyDescription odmStudyDesc = new ODMcomplexTypeDefinitionStudyDescription();
		odmStudyDesc.setValue(studyDesc);
		odmGlobalVars.setStudyDescription(odmStudyDesc);

		ODMcomplexTypeDefinitionProtocolName odmProtocolName = new ODMcomplexTypeDefinitionProtocolName();
		odmProtocolName.setValue("-");
		odmGlobalVars.setProtocolName(odmProtocolName);

		ODMcomplexTypeDefinitionMetaDataVersion odmMetadataVersion = new ODMcomplexTypeDefinitionMetaDataVersion();
		odmStudy.getMetaDataVersion().add(odmMetadataVersion);
		odmMetadataVersion.setName("Version 1.3.1");
		odmMetadataVersion.setOID("v1.3.1");
		odmMetadataVersion.setProtocol(new ODMcomplexTypeDefinitionProtocol());

		ODMcomplexTypeDefinitionStudyEventDef currentStudyEvent = null;
		ODMcomplexTypeDefinitionFormDef currentForm = null;
		ODMcomplexTypeDefinitionItemGroupDef currentItemGroup = null;
		HashMap<String,String> codeListMap = new HashMap<String,String>();

		MetadataField field = new MetadataField();
		jp.nextValue(); // Start top level object
		jp.nextValue(); // Object fields
		field.read(jp);

		while (jp.getCurrentToken() != JsonToken.END_ARRAY) { // iterate over records
			if (field.processed){
				if (!field.read(jp)) {
					/*
					 * No more fields to process so exit loop
					 */
					break;
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("Read " + field);
			}

			field.processed = true;
			if (field.type.equals("descriptive"))
			{
				// A descriptive field does not contain clinical data.  It's only
				// there to help the user fill out the form.  Just skip it.
				continue;
			}

			/*
			 * No place in ODM to store Arm information, so encoding into name and OID.
			 * Using CDISC SDM extension to ODM could resolve this issue.
			 *
			 * See: http://www.cdisc.org/study-trial-design
			 */
			String studyEventName = field.armName + ": " + field.eventName;

			if (currentStudyEvent == null || !currentStudyEvent.getName().equals(studyEventName)) {
				/*
				 * New study event, so also need to start processing a new form
				 */
				currentForm = null;

				currentStudyEvent = new ODMcomplexTypeDefinitionStudyEventDef();
				currentStudyEvent.setName(studyEventName);
				currentStudyEvent.setOID("SE." + studyEventName);
				currentStudyEvent.setRepeating(YesOrNo.NO); // True value is unknown
				currentStudyEvent.setType(EventType.COMMON); // True value is unknown
				odmMetadataVersion.getStudyEventDef().add(currentStudyEvent);

				List<ODMcomplexTypeDefinitionStudyEventRef> eventRefs = odmMetadataVersion.getProtocol().getStudyEventRef();
				ODMcomplexTypeDefinitionStudyEventRef eventRef = new ODMcomplexTypeDefinitionStudyEventRef();
				eventRef.setStudyEventOID(currentStudyEvent.getOID());
				eventRef.setMandatory(YesOrNo.NO); // True value is unknown
				eventRef.setOrderNumber(BigInteger.valueOf(eventRefs.size() + 1));
				eventRefs.add(eventRef);
			}

			if (currentForm == null || !currentForm.getName().equals(field.formName)) {
				/*
				 * Add a reference to the form even though we haven't created the definition yet.
				 * The form either already exists or will be created below.
				 */
				String formOID = "FM." + field.formName;
				ODMcomplexTypeDefinitionFormRef formRef = new ODMcomplexTypeDefinitionFormRef();
				formRef.setFormOID(formOID);
				formRef.setMandatory(YesOrNo.NO); //True value is unknown
				formRef.setOrderNumber(BigInteger.valueOf(currentStudyEvent.getFormRef().size() + 1));
				currentStudyEvent.getFormRef().add(formRef);

				currentForm = ODMUtil.getForm(odmStudy, formOID);
				if (currentForm != null) {
					/*
					 * Already constructed this form definition, so skip all fields until next form
					 * and continue from next event/form.
					 */
					skipToNextForm(jp, field);
					continue;
				}

				currentForm = new ODMcomplexTypeDefinitionFormDef();
				currentForm.setOID(formOID);
				currentForm.setName(field.formName);
				currentForm.setRepeating(YesOrNo.NO); // True value is unknown
				odmMetadataVersion.getFormDef().add(currentForm);

				/*
				 * Define a default item group and reference for each form
				 */
				currentItemGroup = new ODMcomplexTypeDefinitionItemGroupDef();
				currentItemGroup.setOID("IG." + field.formName);
				currentItemGroup.setName(field.formName);
				currentItemGroup.setRepeating(YesOrNo.NO); // True value is unknown
				odmMetadataVersion.getItemGroupDef().add(currentItemGroup);

				ODMcomplexTypeDefinitionItemGroupRef groupRef = new ODMcomplexTypeDefinitionItemGroupRef();
				groupRef.setItemGroupOID(currentItemGroup.getOID());
				groupRef.setMandatory(YesOrNo.NO); // True value is unknown
				groupRef.setOrderNumber(BigInteger.valueOf(1));
				currentForm.getItemGroupRef().add(groupRef);
			}

			/*
			 * Fields are unique across all forms in REDCap, so always construct
			 * new item definitions.
			 */
			ODMcomplexTypeDefinitionItemDef item = new ODMcomplexTypeDefinitionItemDef();
			item.setOID("IT." + field.name);
			item.setName(field.name);
			item.setDataType(DataType.fromValue(getOdmDataType(field)));

			ODMcomplexTypeDefinitionDescription itemDescription = new ODMcomplexTypeDefinitionDescription();
			ODMcomplexTypeDefinitionTranslatedText translatedText = new ODMcomplexTypeDefinitionTranslatedText();
			translatedText.setLang("en");
			translatedText.setValue(field.label);
			itemDescription.getTranslatedText().add(translatedText);
			item.setDescription(itemDescription);

			odmMetadataVersion.getItemDef().add(item);

			ODMcomplexTypeDefinitionItemRef itemRef = new ODMcomplexTypeDefinitionItemRef();
			itemRef.setItemOID(item.getOID());
			itemRef.setMandatory(YesOrNo.NO); // True value is unknown
			itemRef.setOrderNumber(BigInteger.valueOf(currentItemGroup.getItemRef().size() + 1));
			currentItemGroup.getItemRef().add(itemRef);

			if (field.enumeration != null && !field.type.equals("calc") && !field.type.equals("slider")) {
				String codeListOID = codeListMap.get(field.enumeration);

				if (codeListOID == null) {
					codeListOID = "CL." + (codeListMap.size() + 1);
					codeListMap.put(field.enumeration, codeListOID);

					ODMcomplexTypeDefinitionCodeList codeList = new ODMcomplexTypeDefinitionCodeList();
					codeList.setOID(codeListOID);
					codeList.setName(String.valueOf(codeListMap.size()));
					codeList.setDataType(CLDataType.fromValue(getOdmDataType(field)));

					String[] tokens = field.enumeration.split("\\\\n");
					for (int i = 0; i < tokens.length; i++) {
						int valueIndex = tokens[i].indexOf(',');
						String codedValue = tokens[i].substring(0, valueIndex).trim();
						String value = tokens[i].substring(valueIndex + 1).trim();
						ODMcomplexTypeDefinitionCodeListItem codeListItem = new ODMcomplexTypeDefinitionCodeListItem();
						codeListItem.setCodedValue(codedValue);
						codeListItem.setDecode(new ODMcomplexTypeDefinitionDecode());

						ODMcomplexTypeDefinitionTranslatedText decodeText = new ODMcomplexTypeDefinitionTranslatedText();
						decodeText.setLang("en");
						decodeText.setValue(value);
						codeListItem.getDecode().getTranslatedText().add(decodeText);

						codeList.getCodeListItem().add(codeListItem);
					}

					odmMetadataVersion.getCodeList().add(codeList);
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Code list " + codeListOID + " already defined for enum " + field.enumeration);
					}
				}

				ODMcomplexTypeDefinitionCodeListRef codeListRef = new ODMcomplexTypeDefinitionCodeListRef();
				codeListRef.setCodeListOID(codeListOID);
				item.setCodeListRef(codeListRef);
			}
		}

		return odmStudy;
	}

	private ODMcomplexTypeDefinitionClinicalData buildClinicalData(
			ODMcomplexTypeDefinitionStudy odmStudy, JsonParser jp)
	throws JsonParseException, IOException, JAXBException {
		ODMcomplexTypeDefinitionClinicalData clinicalData = new ODMcomplexTypeDefinitionClinicalData();
		clinicalData.setStudyOID(odmStudy.getOID());
		clinicalData.setMetaDataVersionOID(odmStudy.getMetaDataVersion().get(0).getOID());

		ODMcomplexTypeDefinitionSubjectData currentSubjectData = null;
		ODMcomplexTypeDefinitionStudyEventData currentStudyEventData = null;
		ODMcomplexTypeDefinitionFormData currentFormData = null;
		ODMcomplexTypeDefinitionItemGroupData currentItemGroupData = null;

		ClinicalDataField field = new ClinicalDataField();
		jp.nextValue(); // Start top level object
		jp.nextValue(); // projectdata fields

		while (jp.getCurrentToken() != JsonToken.END_ARRAY) { // iterate over records
			if (!field.read(jp)) {
				break;
			}

			if (log.isDebugEnabled()) {
				log.debug("Read  " + field);
			}

			if (currentSubjectData == null || !currentSubjectData.getSubjectKey().equals(field.subjectID)) {
				currentSubjectData = new ODMcomplexTypeDefinitionSubjectData();
				currentSubjectData.setSubjectKey(field.subjectID);
				clinicalData.getSubjectData().add(currentSubjectData);

				/*
				 * New subject, so make sure we create a new StudyEventData element for it
				 */
				currentStudyEventData = null;
			}

			String studyEventOID = "SE." + field.armName + ": " + field.eventName;
			if (currentStudyEventData == null || !currentStudyEventData.getStudyEventOID().equals(studyEventOID)) {
				currentStudyEventData = new ODMcomplexTypeDefinitionStudyEventData();
				currentStudyEventData.setStudyEventOID(studyEventOID);
				currentSubjectData.getStudyEventData().add(currentStudyEventData);

				/*
				 * New study event, so make sure we create a new FormData element for it
				 */
				currentFormData = null;
			}

			String formOID = "FM." + field.formName;
			if (currentFormData == null || !currentFormData.getFormOID().equals(formOID)) {
				currentFormData = new ODMcomplexTypeDefinitionFormData();
				currentFormData.setFormOID(formOID);

				currentStudyEventData.getFormData().add(currentFormData);

				String itemGroupOID = "IG." + field.formName;
				currentItemGroupData = new ODMcomplexTypeDefinitionItemGroupData();
				currentItemGroupData.setItemGroupOID(itemGroupOID);

				currentFormData.getItemGroupData().add(currentItemGroupData);
			}

			String itemOID = "IT." + field.name;

			//TODO: generate type specific items, so that metadata is not required to convert to i2b2 observations
//			switch (ODMUtil.getItem(odmStudy, itemOID).getDataType()) {
//			case FLOAT:
//				ODMcomplexTypeDefinitionItemDataFloat itemData = new ODMcomplexTypeDefinitionItemDataFloat();
//				currentItemGroupData.getItemDataStarGroup().add(itemData);
//			}

			ODMcomplexTypeDefinitionItemData itemData = new ODMcomplexTypeDefinitionItemData();
			itemData.setItemOID(itemOID);
			itemData.setValue(field.value);

			currentItemGroupData.getItemDataGroup().add(itemData);
		}

		return clinicalData;
	}

	private String getOdmDataType(MetadataField field) {
		String odmDataType = null;
		String rcDataType = field.type;
		String rcValidationType = field.validationType;

		if(rcDataType.equalsIgnoreCase("select")
				|| rcDataType.equalsIgnoreCase("radio")
				|| rcDataType.equalsIgnoreCase("textarea")
				|| rcDataType.equalsIgnoreCase("yesno")
				|| rcDataType.equalsIgnoreCase("slider")
				|| rcDataType.equalsIgnoreCase("file")
				|| rcDataType.equalsIgnoreCase("checkbox")){
			if (rcValidationType != null
					&& (rcValidationType.equals("float")
							|| rcValidationType.equals("integer"))) {
				odmDataType = rcValidationType;
			} else {
				odmDataType = "text";
			}
		} else if(rcDataType.equalsIgnoreCase("calc")){
			odmDataType = "float";
		} else{
			odmDataType = rcDataType;
		}

		return odmDataType;
	}

	private Reader readServiceData(String projectID, String serviceUrl, String token)
	throws IOException {
		String endpoint = serviceUrl + "?projectID=" + projectID;
		GetRedcapService service = new GetRedcapService();

		Map<String, String> params = null;
		if (token != null) {
			params = new HashMap<String, String>();
			params.put("token", token);
		}

		return service.readRedcapWebServiceData(endpoint, params, null, 0);

//		File testFile = new File("examples/example_database_" + servicePath + ".json");
//		return new BufferedReader(new FileReader(testFile));
	}

	private void skipToNextForm(JsonParser jp, MetadataField field) throws JsonParseException, IOException {
		String eventName = field.eventName;
		String formName = field.formName;

		if (log.isDebugEnabled()) {
			log.debug("Skipping form " + formName + " in event " + eventName);
		}

		do {
			if (!field.read(jp)) {
				/*
				 * No more fields
				 */
				break;
			}
		} while (eventName.equals(field.eventName) && formName.equals(field.formName));
	}

	public static String nextValue(JsonParser jp) throws JsonParseException, IOException {
		jp.nextToken();
		jp.nextToken();

		String value = jp.getText();
		return "null".equals(value) ? null : value;
	}

	public static class MetadataField {
		boolean processed = true;

		int armNumber;
		String armName;
		String eventName;
		String formName;
		int index;
		String name;
		String label;
		String type;
		String enumeration;
		String minValue;
		String maxValue;
		String validationType;

		public boolean read(JsonParser jp) throws JsonParseException, IOException {
			if (jp.nextToken() == JsonToken.END_ARRAY) {
				return false;
			}

			processed = false;

			armNumber = Integer.valueOf(nextValue(jp));
			armName = nextValue(jp);
			eventName = nextValue(jp);
			formName = nextValue(jp);
			index = Integer.valueOf(nextValue(jp));
			name = nextValue(jp);
			label = nextValue(jp);
			type = nextValue(jp);
			enumeration = nextValue(jp);
			minValue = nextValue(jp);
			maxValue = nextValue(jp);
			validationType = nextValue(jp);

			jp.nextToken(); // end object

			return true;
		}

		@Override
		public String toString() {
			return "MetadataField [armName=" + armName + ", armNumber=" + armNumber
					+ ", enumeration=" + enumeration + ", eventName="
					+ eventName + ", formName=" + formName + ", index=" + index
					+ ", label=" + label + ", maxValue=" + maxValue
					+ ", minValue=" + minValue + ", name=" + name + ", type="
					+ type + ", validationType=" + validationType + "]";
		}
	}

	public static class ClinicalDataField {
		String subjectID;
		String armName;
		String eventName;
		String formName;
		String name;
		String value;

		private boolean read(JsonParser jp) throws JsonParseException, IOException {
			if (jp.nextToken() == JsonToken.END_ARRAY) {
				return false;
			}

			subjectID = nextValue(jp);
			armName   = nextValue(jp);
			eventName = nextValue(jp);
			formName  = nextValue(jp);
			name      = nextValue(jp);
			value     = nextValue(jp);

			jp.nextToken(); // end object

			return true;
		}

		@Override
		public String toString() {
			return "ClinicalDataField [armName=" + armName + ", eventName="
					+ eventName + ", formName=" + formName + ", name=" + name
					+ ", subjectID=" + subjectID + ", value=" + value + "]";
		}
	}
}