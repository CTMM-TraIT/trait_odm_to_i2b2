/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 *
 * This is a JUnit test class to test parsing and saving ODM meta and clinical data into i2b2
 * testing class I2B2ODMStudyHandlerClient.
 *
 * @author: Alex Wu
 * @date: November 11, 2011
 */

package com.recomdata.i2b2;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;

/**
 * I2B2ODMStudyHandlerClientTest.java
 *
 * @author awu
 *
 */
public class I2B2ODMStudyHandlerClientTest {
	/**
	 * The logger for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(I2B2ODMStudyHandlerClientTest.class);

	/**
	* The examples directory.
	*/
	private static final String EXAMPLES_DIRECTORY = Paths.get("src", "test", "resources", "examples") + File.separator;

	/**
	* The output directory.
	*/
	private static final String OUTPUT_DIRECTORY = Paths.get("src", "test", "resources", "output") + File.separator;

	/**
	* The properties directory.
	*/
	private static final String PROPERTIES_DIRECTORY = Paths.get("src", "test", "resources") + File.separator;

	/**
	* Test method for {@link com.recomdata.i2b2.I2B2ODMStudyHandlerCMLClient}.
	*/
	@Test
	public void testLoadODMFile2I2B2() throws Exception {
		logger.info("JUnit test for export ODM to i2b2 start...");
		final long start = System.currentTimeMillis();

		final String odmProperty = System.getProperty("odmpath");
		final String odmXmlPath = odmProperty != null ? odmProperty : EXAMPLES_DIRECTORY + "CDISC_ODM_example_3.xml";
		Assert.assertTrue(StringUtils.isNotEmpty(odmXmlPath));
		Assert.assertTrue(new File(odmXmlPath).exists());

		final String exportProperty = System.getProperty("exportpath");
		final String exportFilePath = exportProperty != null ? exportProperty : OUTPUT_DIRECTORY;
		Assert.assertTrue(StringUtils.isNotEmpty(exportFilePath));
		//Assert.assertTrue(new File(exportFilePath).exists());

		I2B2ODMStudyHandlerCMLClient client = new I2B2ODMStudyHandlerCMLClient();
		client.loadODMFile2I2B2(new File(odmXmlPath).getAbsolutePath(),
								new File(exportFilePath).getAbsolutePath() + File.separator,
								new File(PROPERTIES_DIRECTORY + "ODM-to-i2b2.properties").getAbsolutePath());

		final long end = System.currentTimeMillis();
		logger.warn("Finish...[" + (end - start) / 1000.00 + "] secs");
	}
}
