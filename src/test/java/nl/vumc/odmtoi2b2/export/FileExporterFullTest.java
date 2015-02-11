package nl.vumc.odmtoi2b2.export;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Unit test for the FileExporterFull class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class FileExporterFullTest {
	/**
	 * The output directory.
	 */
	private static final String OUTPUT_DIRECTORY = Paths.get("src", "test", "resources", "output") + File.separator;

	/**
	 * The resources directory for the export package.
	 */
	private static final String EXPORT_DIRECTORY = Paths.get(
			"src", "test", "resources", "nl", "vumc", "odmtoi2b2", "export"
	) + File.separator;

	/**
	 * Test the storeColumn method.
	 */
    @Test
	public void testWriteExportColumns() throws IOException {
        final Configuration configuration = new Configuration(EXPORT_DIRECTORY + "filled-configuration.properties");
        final FileExporterFull fileExporter = new FileExporterFull(OUTPUT_DIRECTORY, "my-study-name", configuration);

        final StringWriter columnsWriter = new StringWriter();
        fileExporter.setColumnsWriter(columnsWriter);
        fileExporter.storeColumn("abc+cde", "", "", "", "", "preferred-item-name", "oid-path");
        fileExporter.storeColumn("abc", "", "cde", "efg", "", "preferred-item-name2", "oid-path");
        final String expectedOutput =
                "Filename\tCategory Code\tColumn Number\tData Label\tData Label Source\tControl Vocab Cd\n" +
                "my-study-name_clinical_data.txt\t\t1\tSUBJ_ID\t\t\n" +
                "my-study-name_clinical_data.txt\tSubset selection type\t2\ttype (patient, event or repeat)\t\t\n" +
                "my-study-name_clinical_data.txt\tSubset selection type\t3\tassociated patient id\t\t\n" +
                "my-study-name_clinical_data.txt\tSubset selection type\t4\tassociated event id\t\t\n" +
                "my-study-name_clinical_data.txt\tSubset selection type\t5\tevent number\t\t\n" +
                "my-study-name_clinical_data.txt\tSubset selection type\t6\trepeat number\t\t\n" +
                "my-study-name_clinical_data.txt\tabc and cde\t7\tpreferred-item-name\t\t\n" +
                "my-study-name_clinical_data.txt\tabc+cde+efg\t8\tpreferred-item-name2\t\t\n";
        assertEquals(expectedOutput, columnsWriter.toString());
	}

	/**
	 * Test the storeWord method.
	 */
    @Test
	public void testWriteExportWordMap() throws IOException {
        final Configuration configuration = new Configuration(EXPORT_DIRECTORY + "filled-configuration.properties");
        final FileExporterFull fileExporter = new FileExporterFull(OUTPUT_DIRECTORY, "my-study-name", configuration);

        final StringWriter wordMapWriter = new StringWriter();
        fileExporter.setWordMapWriter(wordMapWriter);
        fileExporter.storeColumn("abc+cde", "", "", "", "", "preferred-item-name", "oid-path");
        fileExporter.storeWord("myWordValue");
        fileExporter.storeWord("myWordValue2");
        fileExporter.storeColumn("abc", "", "", "", "", "preferred-item-name2", "oid-path");
        fileExporter.storeColumn("fghij", "", "", "", "", "preferred-item-name3", "oid-path");
        fileExporter.storeWord("myWordValue3");
        final String expectedOutput =
                "Filename\tColumn Number\tOriginal Data Value\tNew Data Values\n" +
                "my-study-name_clinical_data.txt\t7\t1\tmyWordValue\n" +
                "my-study-name_clinical_data.txt\t7\t2\tmyWordValue2\n" +
                "my-study-name_clinical_data.txt\t9\t1\tmyWordValue3\n";
        assertEquals(expectedOutput, wordMapWriter.toString());
	}

	/**
	 * Test the storeClinicalDataInfo method.
	 */
	@SuppressWarnings("SpellCheckingInspection")
    @Test
	public void testWriteExportClinicalDataInfoNoRepeats() throws IOException {
        final Map<String, Map<String, String>> expectedClinicalDataMap = new HashMap<>();
        final Map<String, String> entityDataMap1 = new HashMap<>();
        final Map<String, String> entityDataMap2 = new HashMap<>();
        expectedClinicalDataMap.put("patient-id1", entityDataMap1);
        entityDataMap1.put("firstColumnIdWithEntityIds", "patient-id1");
        entityDataMap1.put("secondColumnIdWithType", "patient");
        entityDataMap1.put("column-id1", "data-value1");
        entityDataMap1.put("column-id2", "data-value2");
        expectedClinicalDataMap.put("patient-id2", entityDataMap2);
        entityDataMap2.put("firstColumnIdWithEntityIds", "patient-id2");
        entityDataMap2.put("secondColumnIdWithType", "patient");
        entityDataMap2.put("column-id1", "data-value3");

		final Configuration configuration = new Configuration(EXPORT_DIRECTORY + "filled-configuration.properties");
		final FileExporterFull fileExporter = new FileExporterFull(OUTPUT_DIRECTORY, "study-name", configuration);

		fileExporter.storeClinicalDataInfo("column-id1", "data-value1", "patient-id1", "event-id", null,
                "item-group-id", null);
		fileExporter.storeClinicalDataInfo("column-id2", "data-value2", "patient-id1", "event-id", null,
                "item-group-id", null);
		fileExporter.storeClinicalDataInfo("column-id1", "data-value3", "patient-id2", "event-id", null,
                "item-group-id", null);

		assertEquals(expectedClinicalDataMap, fileExporter.getClinicalDataMap());
	}

    /**
     * Test the storeClinicalDataInfo method.
     */
    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void testWriteExportClinicalDataInfoOnlyEventRepeat() throws IOException {
        final Map<String, Map<String, String>> expectedClinicalDataMap = new HashMap<>();
        final Map<String, String> entityDataMap = new HashMap<>();
        expectedClinicalDataMap.put("patient-id_E1_Revent-repeat-key", entityDataMap);
        entityDataMap.put("firstColumnIdWithEntityIds", "patient-id_E1_Revent-repeat-key");
        entityDataMap.put("secondColumnIdWithType", "event");
        entityDataMap.put("thirdColumnIdWithAssocPatientIds", "patient-id");
        entityDataMap.put("fifthColumnIdWithEventNr", "event-repeat-key");
        entityDataMap.put("column-id", "data-value");

        final Configuration configuration = new Configuration(EXPORT_DIRECTORY + "filled-configuration.properties");
        final FileExporterFull fileExporter = new FileExporterFull(OUTPUT_DIRECTORY, "study-name", configuration);

        fileExporter.storeClinicalDataInfo("column-id", "data-value", "patient-id", "event-id",
                "event-repeat-key", "item-group-id", null);

        assertEquals(expectedClinicalDataMap, fileExporter.getClinicalDataMap());
    }

	/**
	 * Test the storeClinicalDataInfo method.
	 */
	@SuppressWarnings("SpellCheckingInspection")
    @Test
	public void testWriteExportClinicalDataInfoOnlyItemGroupRepeat() throws IOException {
        final Map<String, Map<String, String>> expectedClinicalDataMap = new HashMap<>();
        final Map<String, String> entityDataMap = new HashMap<>();
        expectedClinicalDataMap.put("patient-id_E1_IG1_Ritem-group-repeat-key", entityDataMap);
        entityDataMap.put("firstColumnIdWithEntityIds", "patient-id_E1_IG1_Ritem-group-repeat-key");
        entityDataMap.put("secondColumnIdWithType", "repeat");
        entityDataMap.put("thirdColumnIdWithAssocPatientIds", "patient-id");
        entityDataMap.put("sixthColumnIdWithIgNr", "item-group-repeat-key");
        entityDataMap.put("column-id", "data-value");

		final Configuration configuration = new Configuration(EXPORT_DIRECTORY + "filled-configuration.properties");
		final FileExporterFull fileExporter = new FileExporterFull(OUTPUT_DIRECTORY, "study-name", configuration);

		fileExporter.storeClinicalDataInfo("column-id", "data-value", "patient-id", "event-id", null,
                "item-group-id", "item-group-repeat-key");

		assertEquals(expectedClinicalDataMap, fileExporter.getClinicalDataMap());
	}

    /**
     * Test the storeClinicalDataInfo method.
     */
    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void testWriteExportClinicalDataInfoDoubleRepeat() throws IOException {
        final Map<String, Map<String, String>> expectedClinicalDataMap = new HashMap<>();
        final Map<String, String> entityDataMap = new HashMap<>();
        expectedClinicalDataMap.put("patient-id_E1_Revent-repeat-key_IG1_Ritem-group-repeat-key", entityDataMap);
        entityDataMap.put("firstColumnIdWithEntityIds", "patient-id_E1_Revent-repeat-key_IG1_Ritem-group-repeat-key");
        entityDataMap.put("secondColumnIdWithType", "repeat");
        entityDataMap.put("thirdColumnIdWithAssocPatientIds", "patient-id");
        entityDataMap.put("fourthColumnIdWithAssocEventIds", "patient-id_E1_Revent-repeat-key");
        entityDataMap.put("fifthColumnIdWithEventNr", "event-repeat-key");
        entityDataMap.put("sixthColumnIdWithIgNr", "item-group-repeat-key");
        entityDataMap.put("column-id", "data-value");

        final Configuration configuration = new Configuration(EXPORT_DIRECTORY + "filled-configuration.properties");
        final FileExporterFull fileExporter = new FileExporterFull(OUTPUT_DIRECTORY, "study-name", configuration);

        fileExporter.storeClinicalDataInfo("column-id", "data-value", "patient-id", "event-id",
                "event-repeat-key", "item-group-id", "item-group-repeat-key");

        assertEquals(expectedClinicalDataMap, fileExporter.getClinicalDataMap());
    }
}
