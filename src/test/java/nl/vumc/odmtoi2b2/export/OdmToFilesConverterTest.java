package nl.vumc.odmtoi2b2.export;

import com.recomdata.odm.ODMLoader;
import org.cdisk.odm.jaxb.ODM;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Unit tests for the OdmToFilesConverter class.
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blonde</a>
 */
public class OdmToFilesConverterTest {
    /**
     * The minimal test ODM file.
     */
    private static final String MINIMAL_ODM_XML_PATH = Paths.get("src", "test", "resources", "examples",
            "CDISC_ODM_example_minim.xml") + File.separator;

    /**
     * The output directory in which the tabular test file will be created.
     */
    private static final String OUTPUT_DIRECTORY = Paths.get("src", "test", "resources", "output") + File.separator;

    /**
     * The resources directory for the export package.
     */
    private static final String EXPORT_DIRECTORY = Paths.get(
            "src", "test", "resources", "nl", "vumc", "odmtoi2b2", "export") + File.separator;

    private static final String PROPERTIES_FILE_PATH = EXPORT_DIRECTORY + "filled-configuration.properties";

    private File minimalClinicalDataFile;
    private File minimalColumnsFile;
    private File minimalWordMapFile;
    private OdmToFilesConverter odmHandler;
    private ODMLoader odmLoader;
    private ODM minimalOdm;
    private File minimalXmlFile;

    @Before
    public void setUp() throws JAXBException {
        minimalClinicalDataFile = new File(OUTPUT_DIRECTORY + "MINIMAL_ODM_FILE_clinical_data.txt");
        minimalColumnsFile = new File(OUTPUT_DIRECTORY + "MINIMAL_ODM_FILE_columns.txt");
        minimalWordMapFile = new File(OUTPUT_DIRECTORY + "MINIMAL_ODM_FILE_word_map.txt");
        if (minimalClinicalDataFile.exists()) {
            assertTrue(minimalClinicalDataFile.delete());
        }
        if (minimalColumnsFile.exists()) {
            assertTrue(minimalColumnsFile.delete());
        }
        if (minimalWordMapFile.exists()) {
            assertTrue(minimalWordMapFile.delete());
        }
        odmHandler = new OdmToFilesConverter();
        odmLoader = new ODMLoader();
        minimalXmlFile = new File(MINIMAL_ODM_XML_PATH);
        minimalOdm = odmLoader.unmarshall(minimalXmlFile);
    }

    @Test
    public void testProcessODM() throws JAXBException, IOException {
        odmHandler.processODM(minimalOdm, OUTPUT_DIRECTORY, false, PROPERTIES_FILE_PATH);
        odmHandler.closeExportWriters();
        assertTrue(minimalClinicalDataFile.exists());
        assertEquals(105, minimalClinicalDataFile.length());
        assertTrue(minimalColumnsFile.exists() && minimalColumnsFile.length() == 0);
        assertTrue(minimalWordMapFile.exists() && minimalWordMapFile.length() == 0);
    }

    @Test
    public void testCloseExportWriters() throws IOException, JAXBException {
        odmHandler.processODM(minimalOdm, OUTPUT_DIRECTORY, false, PROPERTIES_FILE_PATH);
        odmHandler.closeExportWriters();
        // todo: add asserts?
    }

    @Test
    public void testProcessODMLight() throws JAXBException, IOException {
        odmHandler.processODM(minimalOdm, OUTPUT_DIRECTORY, true, PROPERTIES_FILE_PATH);
        odmHandler.closeExportWriters();
        assertTrue(minimalColumnsFile.exists() && minimalColumnsFile.length() == 0);
        assertTrue(minimalWordMapFile.exists() && minimalWordMapFile.length() == 0);
        assertTrue(minimalClinicalDataFile.exists());
        assertEquals(105, minimalClinicalDataFile.length());
    }

    @Test
    public void testCloseExportWritersLight() throws IOException, JAXBException {
        odmHandler.processODM(minimalOdm, OUTPUT_DIRECTORY, true, PROPERTIES_FILE_PATH);
        odmHandler.closeExportWriters();
    }
}
