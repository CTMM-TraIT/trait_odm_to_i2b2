/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 * This class is craeting a metadataxml for i2b2.
 * @author: Alex Wu
 * @date: November 3, 2011
 */
package com.recomdata.i2b2;

import java.util.Calendar;
import java.util.Date;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * This class generate metadataxml using Jdom
 * 
 * @author awu Modified on 11/09/2011
 */
public class MetaDataXML {

	/**
	 * This method creates Enum type metadataxml on fly for such as Sex, Race
	 * etc.
	 * 
	 * @throws Exception
	 */
	public String getEnumMetadataXML(String itemOID, String itemName, String[] enumValues) {
		Element root = createBaseMetadata(itemOID, itemName, "Enum");
		
		Element enumValuesElement = root.getChild("EnumValues");

		for (int i = 0; i < enumValues.length; i++) {
			Element val = new Element("Val");
			
			//val.setAttribute("description", enumValues[i]);			
			enumValuesElement.addContent(val.setText(enumValues[i]));
		}		

		return toString(root);		
	}
	
	public String getIntegerMetadataXML(String itemOID, String itemName) {
		Element root = createBaseMetadata(itemOID, itemName, "Integer");
		
		return toString(root);
	}
	
	public String getFloatMetadataXML(String itemOID, String itemName) {
		Element root = createBaseMetadata(itemOID, itemName, "Float");
		
		return toString(root);
	}
	
	public String getStringMetadataXML(String itemOID, String itemName) {
		Element root = createBaseMetadata(itemOID, itemName, "String");
		
		return toString(root);
	}
	
	private Element createBaseMetadata(String testId, String testName, String dataType) {
		Element root = new Element("ValueMetadata");
		
		// Creating children for the root element.
		// set the text of an xml element.
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();

		root.addContent(new Element("Version").setText("3.02"));
		root.addContent(new Element("CreationDateTime").setText(currentDate.toString()));
		root.addContent(new Element("TestID").setText(testId));
		root.addContent(new Element("TestName").setText(testName));
		root.addContent(new Element("DataType").setText(dataType));
		root.addContent(new Element("CodeType").setText("GRP"));
		root.addContent(new Element("Loinc").setText("1"));
		root.addContent(new Element("Flagstouse"));
		root.addContent(new Element("Oktousevalues").setText("N"));
		root.addContent(new Element("MaxStringLength"));
		root.addContent(new Element("LowofLowValue"));
		root.addContent(new Element("HighofLowValue"));
		root.addContent(new Element("LowofHighValue"));
		root.addContent(new Element("HighofHighValue"));
		root.addContent(new Element("LowofToxicValue"));
		root.addContent(new Element("HighofToxicValue"));
		root.addContent(new Element("EnumValues"));

		Element commentDetEx = new Element("CommentsDeterminingExclusion");
		commentDetEx.addContent(new Element("Com"));
		root.addContent(commentDetEx);

		Element unitValue = new Element("UnitValues");
		unitValue.addContent(new Element("NormalUnits").setText("N/A"));
		unitValue.addContent(new Element("EqualUnits").setText("N/A"));
		unitValue.addContent(new Element("ExcludingUnits"));
		Element convertUnit = new Element("ConvertingUnits");
		convertUnit.addContent(new Element("Units"));
		convertUnit.addContent(new Element("MultiplyingFactor"));
		unitValue.addContent(convertUnit);
		root.addContent(unitValue);

		Element analysis = new Element("Analysis");
		analysis.addContent(new Element("Enums"));
		analysis.addContent(new Element("Counts"));
		analysis.addContent(new Element("New"));
		root.addContent(analysis);

		return root; 
	}
	
	private String toString(Element rootElement) {
		String xml = null;
		
		Document document = new Document();
		document.setRootElement(rootElement);
		
		try {
			XMLOutputter outputter = new XMLOutputter();

			// Set the XLMOutputter to pretty formatter. This formatter
			// use the TextMode.TRIM, which mean it will remove the
			// trailing white-spaces of both side (left and right)
			outputter.setFormat(Format.getCompactFormat());			
			
			// output as a string
			xml = outputter.outputString(document);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return xml;
	}	
}
