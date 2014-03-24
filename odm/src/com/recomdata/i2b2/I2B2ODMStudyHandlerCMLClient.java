package com.recomdata.i2b2;

/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 * This is a handler's command-line client that can loading other source ODM XML files
 * @author: Alex Wu
 * @date: October 28, 2011
 */

import java.io.File;
import java.io.FileNotFoundException;

import nl.vumc.odmtoi2b2.export.OdmToFilesConverter;
import org.cdisk.odm.jaxb.ODM;

import com.recomdata.config.Config;
import com.recomdata.i2b2.dao.I2B2DBUtils;
import com.recomdata.odm.ODMLoader;

/**
 * This class will be used by both command-line and web app to load ODM files
 * from different sources.
 * 
 * @author awu
 * 
 */
public class I2B2ODMStudyHandlerCMLClient {
	/**
	 * Whether the export should go to the database (true) or to a file (false).
	 */
	public static final boolean EXPORT_TO_DATABASE = false;

	/**
	 * method to process odm xml file and save data into i2b2
	 * 
	 * @param odmXmlPath the ODM file to process.
	 * @param exportFilePath the path of the export file.
     * todo: parameter userDefinedConversionFile is no longer needed?
	 * @throws Exception
	 */
	@SuppressWarnings("UnusedParameters")
    public void loadODMFile2I2B2(String odmXmlPath, String exportFilePath, String userDefinedConversionFile) throws Exception {
		File xmlFile = new File(odmXmlPath);

		if (!xmlFile.exists()) {
			throw new FileNotFoundException(xmlFile.getPath());
		}

		// Load and parse ODM xml here by jaxb
		ODMLoader odmLoader = new ODMLoader();
		ODM odm = odmLoader.unmarshall(xmlFile);

		if (odm == null || odm.getStudy() == null || odm.getStudy().size() == 0) {
			// TODO: Define more specific exception
			throw new Exception("No study definitions were found in ODM file.");
		}

		 // parse ODM XML and save as i2b2 metadata and demodata records
        if (EXPORT_TO_DATABASE) {
            I2B2ODMStudyHandler odmHandler = new I2B2ODMStudyHandler(odm);
            odmHandler.processODM();
        } else {
            OdmToFilesConverter odmHandler = new OdmToFilesConverter();
            odmHandler.processODM(odm, exportFilePath);
            odmHandler.closeExportWriters();
        }
    }

	/**
	 * main method for command-line user
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 2) {
				System.out.println("Please provide the ODM file (plus path) to process, " +
                                   "the path of the export directory (without slash), " +
                                   "and an optional concept mapping file (plus path).");
				return;
			}

			String odmFilePath = args[0];
			String exportFilePath = args[1];
            String userDefinedConversionFile = args.length >= 3 ? args[2] : null;

			if (EXPORT_TO_DATABASE) {
				System.out.println("Initializing database connection...");
				Config config = Config.getConfig();
				I2B2DBUtils.init(config);
			}

			System.out.println("Parsing ODM file ..." + odmFilePath);

			I2B2ODMStudyHandlerCMLClient client = new I2B2ODMStudyHandlerCMLClient();
			client.loadODMFile2I2B2(odmFilePath, exportFilePath, userDefinedConversionFile);

			if (EXPORT_TO_DATABASE) {
				System.out.println("Releasing database connection.");
				I2B2DBUtils.shutdown();
			}

			System.out.println("Processing complete.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
