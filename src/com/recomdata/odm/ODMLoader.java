package com.recomdata.odm;

/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 * Class reads ODM XML file and create ODM object (unmashelling)
 * Writer ODM object back to ODM XML as String
 * @author Alex Wu
 * @date August 30, 2011
 */

import java.io.File;
import java.io.Writer;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.cdisk.odm.jaxb.*;

public class ODMLoader {

	/**unmashell ODM XML File to a ODM object
	 */
	public ODM unmarshall(File xml) throws JAXBException {
		ODM odm = new ODM();
		try {
			JAXBContext context = JAXBContext.newInstance("org.cdisk.odm.jaxb");

			Unmarshaller unmarshaller = context.createUnmarshaller();
			odm = (ODM) unmarshaller.unmarshal(xml);

		} catch (JAXBException jaxbEx) {
			jaxbEx.printStackTrace();
		}

		return odm;
	}

	/**mashall a ODM xml object to StringWriter
	*/
	public void marshall(Object odm, Writer writer) throws JAXBException {
		try {
			JAXBContext context = JAXBContext.newInstance("org.cdisk.odm.jaxb");

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

			//marshaller.marshal(odm, System.out);
			marshaller.marshal(odm, writer);

		} catch (JAXBException jaxbEx) {
			jaxbEx.printStackTrace();
		}
	}

	/**
	 * @return A String data type containing the ODM in XML format
	 */
	public String getODMXMLString(Object odm) {
		StringWriter strWriter = null;

		try {
			strWriter = new StringWriter();
			marshall(odm, strWriter);

		} catch (JAXBException jaxbEx) {
			jaxbEx.printStackTrace();
		}

		return strWriter.toString();
	}

}
