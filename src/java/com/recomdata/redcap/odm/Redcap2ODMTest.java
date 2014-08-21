/**
* Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
* This class retrieve data from REDCap RESTful web service API and
* map to CDISC - ODM xml format and create ODM XML.
* @author: Alex Wu
* @date: October 27, 2011
*/
package com.recomdata.redcap.odm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.cdisk.odm.jaxb.ODM;

import com.recomdata.config.Config;
import com.recomdata.odm.ODMLoader;

/**
 * Redcap2ODMTest.java
 *
 * Class maps CHB Redcap web service API data to ODM Study and Clinical Data definitions.
 */
public class Redcap2ODMTest
{

	/**main method to get RedCap data from CHB API through RESTful WS and create ODM XML file with metadata
	 * Study and ClinicalData
	*/
	public static void main(String[] args) throws Exception
	{
		try
		{
			String str = "abc/def/gh/ijkl/mno";
			String result = str.replaceAll("/", "");
			System.out.println(str + ", " + result);
			System.out.println("%%%ODM creation START!!!");

			//hardcoded for testing only! This project array should come from Grails web app
			String projectID = "540";
			String projectName = "Test540";
			String projectDesc = "Test540";
			String token = "C37C75D724ACE43EC78234EBEF3A9191";

			Config config = Config.getConfig();

			String baseUrl = config.getProperty("org.chb.redcap.ws.base.url");
			String metaDataUrl = config.getProperty("org.chb.redcap.ws.metadata");
			String recordUrl = config.getProperty("org.chb.redcap.ws.record");

			Redcap2ODM redcapOdm = new Redcap2ODM();
			//TODO: Update to pass correct parameters
			ODM odm = redcapOdm.buildODM(
					projectID, projectName, projectDesc,
					baseUrl, metaDataUrl, recordUrl, token);

			//file path
			File odmFile = new File("CHB_REDCap_" + projectName + ".xml");
			BufferedWriter out = new BufferedWriter(new FileWriter(odmFile));

			ODMLoader odmLoader = new ODMLoader();
			odmLoader.marshall(odm, out);

			out.close();

			if(odm != null){
				System.out.println("%%%Done!!!");
			}
		}
		catch (Exception ex)
		{
		  ex.printStackTrace();
		}
	}
}