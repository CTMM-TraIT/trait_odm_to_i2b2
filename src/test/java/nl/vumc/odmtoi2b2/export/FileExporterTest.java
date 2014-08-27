package nl.vumc.odmtoi2b2.export;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for the FileExporter class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class FileExporterTest {
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
	 * Test the writeExportClinicalDataInfo method.
	 */
	@Test
	public void testWriteExportClinicalDataInfo() throws IOException {
        final Map<String, Map<String, String>> map1 = new HashMap<>();
        final Map<String, String> map2 = new HashMap<>();
        //noinspection SpellCheckingInspection
        map1.put("patient-id_E1_Revent-repeat-key_IG1_Ritem-group-repeat-key", map2);
        //noinspection SpellCheckingInspection
        map2.put("firstColumnIdWithSubjectIds", "patient-id_E1_Revent-repeat-key_IG1_Ritem-group-repeat-key");
        map2.put("secondColumnIdWithType", "repeat");
        map2.put("thirdColumnIdWithAssocPatientIds", "patient-id");
        //noinspection SpellCheckingInspection
        map2.put("fourthColumnIdWithAssocEventIds", "patient-id_E1_Revent-repeat-key");
        map2.put("fifthColumnIdWithEventNr", "event-repeat-key");
        map2.put("sixthColumnIdWithIgNr", "item-group-repeat-key");
        map2.put("column-id", "data-value");

//		@SuppressWarnings("SpellCheckingInspection")
//		final Map<String, Map<String, String>> expectedClinicalDataMap
//				= ImmutableMap.of("patient-id_E1_Revent-repeat-key_IG1_Ritem-group-repeat-key",
//				(Map<String, String>) new ImmutableMap.Builder<String, String>()
//						.put("firstColumnIdWithSubjectIds", "patient-id_E1_Revent-repeat-key_IG1_Ritem-group-repeat-key")
//						.put("secondColumnIdWithType", "repeat")
//						.put("thirdColumnIdWithAssocPatientIds", "patient-id")
//						.put("fourthColumnIdWithAssocEventIds", "patient-id_E1_Revent-repeat-key")
//						.put("fifthColumnIdWithEventNr", "event-repeat-key")
//						.put("sixthColumnIdWithIgNr", "item-group-repeat-key")
//						.put("column-id", "data-value")
//						.build()
//		);

		final Configuration configuration = new Configuration(EXPORT_DIRECTORY + "filled-configuration.properties");
		final FileExporter fileExporter = new FileExporter(OUTPUT_DIRECTORY, "study-name", configuration);

		fileExporter.writeExportClinicalDataInfo("column-id", "data-value", "patient-id", "event-id",
				"event-repeat-key", "item-group-id", "item-group-repeat-key");

		assertEquals(map1, fileExporter.getClinicalDataMap());
//		assertEquals(map1, expectedClinicalDataMap);
//		assertEquals(expectedClinicalDataMap, fileExporter.getClinicalDataMap());
	}
}
