/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 * This is a JUnit test class to test parsing and saving ODM meta and clinical data into i2b2
 * testing class I2B2ODMStudyHandlerClient.
 * @author: Alex Wu
 * @date: November 11, 2011
 */

package com.recomdata.i2b2;

import java.nio.file.FileSystems;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * I2B2ODMStudyHandlerClientTest.java
 *
 * @author awu
 *
 */
public class I2B2ODMStudyHandlerClientTest {
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Logger rootLogger = Logger.getRootLogger();
		if (!rootLogger.getAllAppenders().hasMoreElements()) {
			rootLogger.setLevel(Level.WARN);
			rootLogger.addAppender(new ConsoleAppender(new PatternLayout(
					"%d{ISO8601} [%p] %c{1}:%L - %m%n")));
		}
	}

	/**
	 * Test method for {@link com.recomdata.i2b2.I2B2ODMStudyHandlerCMLClient}.
	 */
    @Ignore
	@Test
	public void testLoadODMFile2I2B2() throws Exception {
		System.out.println("JUnit test for export ODM to i2b2 start...");
		Logger.getRootLogger().info("JUnit test for export ODM to i2b2 start...");
		long start = System.currentTimeMillis();

        String odmProperty = System.getProperty("odmpath");
        String odmXmlPath = odmProperty != null
                            ? odmProperty
                            : FileSystems.getDefault().getPath("examples", "CDISC_ODM_example_3.xml").toString();
		Assert.assertNotNull(odmXmlPath);

        final String exportProperty = System.getProperty("exportpath");
        String exportFilePath = exportProperty != null
                                ? exportProperty
                                : FileSystems.getDefault().getPath("output").toString();
		Assert.assertNotNull(exportFilePath);

        final I2B2ODMStudyHandlerCMLClient client = new I2B2ODMStudyHandlerCMLClient();
		
		if (!odmXmlPath.equals("") && !exportFilePath.equals("")) {
			client.loadODMFile2I2B2(odmXmlPath, exportFilePath);
		}

		long end = System.currentTimeMillis();

		Logger.getRootLogger().warn("Finish...[" + (end - start) / 1000.00 + "] secs");
	}
}
