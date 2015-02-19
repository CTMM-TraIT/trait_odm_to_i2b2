package com.recomdata.i2b2;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the MetaDataXML class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class MetaDataXMLTest {
    /**
     * The default line separator.
     */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * The OID for the test item.
     */
    private static final String ITEM_OID = "item OID";

    /**
     * The item name.
     */
    private static final String ITEM_NAME = "item name";

    /**
     * The metadata xml object to test.
     */
    private MetaDataXML metaDataXML;

    /**
     * Initialization that is done before each test.
     */
    @Before
    public void setUp() {
        metaDataXML = new MetaDataXML();
    }

    /**
     * Test the getEnumMetadataXML method.
     */
    @Test
    public void testGetEnumMetadataXML() {
        final String enumValue1 = "enum value 1";
        final String enumValue2 = "enum value 2";
        final String[] enumValues = {enumValue1, enumValue2};

        checkMetadataXML(metaDataXML.getEnumMetadataXML(ITEM_OID, ITEM_NAME, enumValues), "Enum", Arrays.asList(enumValues));
    }

    /**
     * Test the getIntegerMetadataXML method.
     */
    @Test
    public void testGetIntegerMetadataXML() {
        checkMetadataXML(metaDataXML.getIntegerMetadataXML(ITEM_OID, ITEM_NAME), "Integer", null);
    }

    /**
     * Test the getFloatMetadataXML method.
     */
    @Test
    public void testGetFloatMetadataXML() {
        checkMetadataXML(metaDataXML.getFloatMetadataXML(ITEM_OID, ITEM_NAME), "Float", null);
    }

    /**
     * Test the getFloatMetadataXML method.
     */
    @Test
    public void testGetStringMetadataXML() {
        checkMetadataXML(metaDataXML.getStringMetadataXML(ITEM_OID, ITEM_NAME), "String", null);
    }

    /**
     * Check the metadata xml by matching it to the regular expression.
     *
     * @param metadataXML the metadata xml.
     * @param dataType    the expected data type.
     * @param enumValues  the expected enumeration values or null/an empty list if not needed.
     */
    private void checkMetadataXML(final String metadataXML, final String dataType, final List<String> enumValues) {
        final String metadataXMLRegularExpression = getMetadataXmlRegularExpression(dataType, enumValues);

        assertTrue("Metadata xml " + metadataXML + " should match the pattern " + metadataXMLRegularExpression + ".",
                   Pattern.compile(metadataXMLRegularExpression).matcher(metadataXML).matches());
    }

    /**
     * Create a regular expression to match the metadata xml.
     *
     * @param dataType   the expected data type.
     * @param enumValues the expected enumeration values or null/an empty list if not needed.
     * @return the regular expression to match the metadata xml.
     */
    @SuppressWarnings("SpellCheckingInspection")
    private String getMetadataXmlRegularExpression(final String dataType, final List<String> enumValues) {
        String enumValuesString = "";
        if (enumValues != null)
            for (final String enumValue : enumValues)
                enumValuesString += "<Val>" + enumValue + "</Val>";

        return Pattern.quote("<?xml version=\"1.0\" encoding=\"UTF-8\"?>") +
               LINE_SEPARATOR +
               Pattern.quote("<ValueMetadata><Version>3.02</Version><CreationDateTime>") +
               "[a-zA-Z0-9: ]*" +
               Pattern.quote("</CreationDateTime>" + "<TestID>" + ITEM_OID + "</TestID>" +
                             "<TestName>" + ITEM_NAME + "</TestName><DataType>" +
                             dataType +
                             "</DataType><CodeType>GRP</CodeType>" +
                             "<Loinc>1</Loinc><Flagstouse /><Oktousevalues>N</Oktousevalues><MaxStringLength />" +
                             "<LowofLowValue /><HighofLowValue /><LowofHighValue /><HighofHighValue />" +
                             "<LowofToxicValue /><HighofToxicValue /><EnumValues>" + enumValuesString +
                             "</EnumValues><CommentsDeterminingExclusion><Com />" +
                             "</CommentsDeterminingExclusion><UnitValues><NormalUnits>N/A</NormalUnits>" +
                             "<EqualUnits>N/A</EqualUnits><ExcludingUnits /><ConvertingUnits><Units />" +
                             "<MultiplyingFactor /></ConvertingUnits></UnitValues><Analysis><Enums /><Counts />" +
                             "<New /></Analysis></ValueMetadata>") +
               LINE_SEPARATOR;
    }
}
