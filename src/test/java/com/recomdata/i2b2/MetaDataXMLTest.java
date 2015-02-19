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
     * The default line separator. It seems that JDOM always uses "\r\n" as the line separator.
     */
    private static final String LINE_SEPARATOR = "\r\n";

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
     * Readable form of the expected metadata xml (without the characters added by Pattern.quote, the line separators,
     * and the regular expression that matched the creation date/time) to allow easy comparison.
     */
    private String readableMetadataXML;

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
     * @param metadataXML the actual metadata xml.
     * @param dataType    the expected data type.
     * @param enumValues  the expected enumeration values or null/an empty list if not needed.
     */
    private void checkMetadataXML(final String metadataXML, final String dataType, final List<String> enumValues) {
        final String metadataXMLRegularExpression = getMetadataXmlRegularExpression(dataType, enumValues);
        final String actualMetadataXML = metadataXML.replaceAll(LINE_SEPARATOR, "");

        assertTrue("Metadata xml " + metadataXML + " should match the pattern " + metadataXMLRegularExpression + "." +
                   "\nexpected with spaces: " + getSpacedText(readableMetadataXML) +
                   "\nactual with spaces:   " + getSpacedText(actualMetadataXML) +
                   "\nexpected hexadecimal: " + getHexadecimalText(readableMetadataXML) +
                   "\nactual hexadecimal:   " + getHexadecimalText(actualMetadataXML),
                   Pattern.compile(metadataXMLRegularExpression).matcher(metadataXML).matches());
    }

    /**
     * Convert a text to a string with two spaces around each character (to compare to the hexadecimal representation).
     *
     * @param text the original text.
     * @return the spaced text.
     */
    private String getSpacedText(final String text) {
        String spacedText = "";
        for (final char character : text.toCharArray())
            spacedText += " " + character + " ";
        return spacedText;
    }

    /**
     * Convert a text to its hexadecimal representation.
     *
     * @param text the original text.
     * @return the hexadecimal representation.
     */
    private String getHexadecimalText(final String text) {
        String hexadecimalText = "";
        for (final byte characterByte : text.getBytes())
            hexadecimalText += String.format("%02x", characterByte) + " ";
        return hexadecimalText;
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
        String enumValuesString;
        if (enumValues != null) {
            enumValuesString = "<EnumValues>";
            for (final String enumValue : enumValues)
                enumValuesString += "<Val>" + enumValue + "</Val>";
            enumValuesString += "</EnumValues>";
        } else
            enumValuesString = "<EnumValues />";

        // Set the readable form of the expected metadata xml to allow easy comparison in the checkMetadataXML method.
        readableMetadataXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                              + "<ValueMetadata><Version>3.02</Version><CreationDateTime>"
                              + "--- --- -- --:--:-- CET 2015"
                              + "</CreationDateTime>" + "<TestID>" + ITEM_OID + "</TestID>" +
                              "<TestName>" + ITEM_NAME + "</TestName><DataType>" + dataType + "</DataType>" +
                              "<CodeType>GRP</CodeType><Loinc>1</Loinc><Flagstouse /><Oktousevalues>N</Oktousevalues>" +
                              "<MaxStringLength /><LowofLowValue /><HighofLowValue /><LowofHighValue />" +
                              "<HighofHighValue /><LowofToxicValue /><HighofToxicValue />" + enumValuesString +
                              "<CommentsDeterminingExclusion><Com />" + "</CommentsDeterminingExclusion>" +
                              "<UnitValues><NormalUnits>N/A</NormalUnits>" + "<EqualUnits>N/A</EqualUnits><ExcludingUnits />" +
                              "<ConvertingUnits><Units /><MultiplyingFactor /></ConvertingUnits></UnitValues>" +
                              "<Analysis><Enums /><Counts /><New /></Analysis></ValueMetadata>";

        return Pattern.quote("<?xml version=\"1.0\" encoding=\"UTF-8\"?>") +
               LINE_SEPARATOR +
               Pattern.quote("<ValueMetadata><Version>3.02</Version><CreationDateTime>") +
               "[a-zA-Z0-9: ]*" +
               Pattern.quote("</CreationDateTime>" + "<TestID>" + ITEM_OID + "</TestID>" +
                             "<TestName>" + ITEM_NAME + "</TestName><DataType>" + dataType + "</DataType>" +
                             "<CodeType>GRP</CodeType><Loinc>1</Loinc><Flagstouse /><Oktousevalues>N</Oktousevalues>" +
                             "<MaxStringLength /><LowofLowValue /><HighofLowValue /><LowofHighValue />" +
                             "<HighofHighValue /><LowofToxicValue /><HighofToxicValue />" + enumValuesString +
                             "<CommentsDeterminingExclusion><Com />" + "</CommentsDeterminingExclusion>" +
                             "<UnitValues><NormalUnits>N/A</NormalUnits>" + "<EqualUnits>N/A</EqualUnits><ExcludingUnits />" +
                             "<ConvertingUnits><Units /><MultiplyingFactor /></ConvertingUnits></UnitValues>" +
                             "<Analysis><Enums /><Counts /><New /></Analysis></ValueMetadata>") +
               LINE_SEPARATOR;
    }
}
