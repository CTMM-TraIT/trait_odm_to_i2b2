/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 *
 * This class is creating a metadata xml for i2b2.
 *
 * @date November 3, 2011
 */
package com.recomdata.i2b2;

import java.util.Calendar;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * This class generates metadata xml using the JDOM library.
 *
 * @author <a href="mailto:alex.wu@unknown.org">Alex Wu</a>
 *
 * Modified on 11/09/2011
 */
public class MetaDataXML {
    /**
     * The element name used for the enumeration values.
     */
    private static final String ENUM_VALUES_ELEMENT_NAME = "EnumValues";

    /**
     * The value used for units that are not available.
     */
    private static final String NOT_AVAILABLE_VALUE = "N/A";

    /**
     * Create Enum type metadata xml for items such as Sex, Race, etc.
     *
     * @param itemOID    the OID of the item.
     * @param itemName   the name of the item.
     * @param enumValues the enumeration values to add.
     * @return the metadata xml (as a string).
     */
    public String getEnumMetadataXML(final String itemOID, final String itemName, final String[] enumValues) {
        final Element root = createBaseMetadata(itemOID, itemName, "Enum");

        final Element enumValuesElement = root.getChild(ENUM_VALUES_ELEMENT_NAME);
        for (String enumValue : enumValues) {
            enumValuesElement.addContent(new Element("Val").setText(enumValue));
        }

        return toString(root);
    }

    /**
     * Create Integer type metadata xml.
     *
     * @param itemOID  the OID of the item.
     * @param itemName the name of the item.
     * @return the metadata xml (as a string).
     */
    public String getIntegerMetadataXML(final String itemOID, final String itemName) {
        return toString(createBaseMetadata(itemOID, itemName, "Integer"));
    }

    /**
     * Create Float type metadata xml.
     *
     * @param itemOID  the OID of the item.
     * @param itemName the name of the item.
     * @return the metadata xml (as a string).
     */
    public String getFloatMetadataXML(final String itemOID, final String itemName) {
        return toString(createBaseMetadata(itemOID, itemName, "Float"));
    }

    /**
     * Create String type metadata xml.
     *
     * @param itemOID  the OID of the item.
     * @param itemName the name of the item.
     * @return the metadata xml (as a string).
     */
    public String getStringMetadataXML(final String itemOID, final String itemName) {
        return toString(createBaseMetadata(itemOID, itemName, "String"));
    }

    /**
     * Create metadata xml using the specified values (which can be altered if required).
     *
     * @param testId   the value for the TestID element.
     * @param testName the value for the TestName element.
     * @param dataType the value for the DataType element.
     * @return the metadata xml (as a string).
     */
    private Element createBaseMetadata(final String testId, final String testName, final String dataType) {
        final Element root = new Element("ValueMetadata");

        addSimpleElements(root, testId, testName, dataType);

        // Add CommentsDeterminingExclusion element with sub element.
        root.addContent(new Element("CommentsDeterminingExclusion").addContent(new Element("Com")));

        // Add UnitValues element with sub elements.
        final Element unitValue = new Element("UnitValues");
        unitValue.addContent(new Element("NormalUnits").setText(NOT_AVAILABLE_VALUE));
        unitValue.addContent(new Element("EqualUnits").setText(NOT_AVAILABLE_VALUE));
        unitValue.addContent(new Element("ExcludingUnits"));
        final Element convertUnit = new Element("ConvertingUnits");
        convertUnit.addContent(new Element("Units"));
        convertUnit.addContent(new Element("MultiplyingFactor"));
        unitValue.addContent(convertUnit);
        root.addContent(unitValue);

        // Add Analysis element with sub elements.
        final Element analysis = new Element("Analysis");
        analysis.addContent(new Element("Enums"));
        analysis.addContent(new Element("Counts"));
        analysis.addContent(new Element("New"));
        root.addContent(analysis);

        return root;
    }

    /**
     * Add the simple elements for the metadat xml to the root element.
     *
     * @param root     the root element for the xml document.
     * @param testId   the value for the TestID element.
     * @param testName the value for the TestName element.
     * @param dataType the value for the DataType element.
     */
    @SuppressWarnings("SpellCheckingInspection")
    private void addSimpleElements(final Element root, final String testId, final String testName, final String dataType) {
        final String currentDateString = Calendar.getInstance().getTime().toString();

        // Creating children for the root element.
        root.addContent(new Element("Version").setText("3.02"));
        root.addContent(new Element("CreationDateTime").setText(currentDateString));
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
        root.addContent(new Element(ENUM_VALUES_ELEMENT_NAME));
    }

    /**
     * Convert an xml root element into a compact formatted xml string.
     *
     * @param rootElement the root element to convert.
     * @return the compact formatted xml string.
     */
    private String toString(final Element rootElement) {
        final XMLOutputter outputter = new XMLOutputter();

        // Set the XLMOutputter to pretty formatter. This formatter
        // use the TextMode.TRIM, which means it will remove the
        // trailing white-spaces of both sides (left and right).
        outputter.setFormat(Format.getCompactFormat());

        final Document document = new Document();
        document.setRootElement(rootElement);

        // output as a string
        return outputter.outputString(document);
    }
}
