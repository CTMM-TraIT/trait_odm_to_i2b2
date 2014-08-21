package com.recomdata.redcap.odm;

/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 * RedcapODMUtils class resolves between the definition and the reference for
 * ODM classes and other handy ODM util handling.
 * This is a singleton class.
 * @author Alex Wu
 * @date October 4, 2011
 */
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class RedcapODMUtils {

    //to make this class singleton
	private static RedcapODMUtils thisInstance;

	static {
		thisInstance = new RedcapODMUtils();
	}

	public static RedcapODMUtils getInstance() {
		return thisInstance;
	}
	
	/**
	 * Convert java.util.Date to XMLGregorianCalendar for ODM
	 * @param date
	 * @return
	 * @throws DatatypeConfigurationException 
	 * @throws DatatypeConfigurationException 
	 */
	public static XMLGregorianCalendar getXMLGregorianCalendar(Date date) throws DatatypeConfigurationException 
	{
		XMLGregorianCalendar xmlCalendar = null;
		DatatypeFactory dataTypeFactory;

		dataTypeFactory = DatatypeFactory.newInstance();
		GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
		cal.setTime(date);
		xmlCalendar = dataTypeFactory.newXMLGregorianCalendar(cal);
		
		return xmlCalendar;
	}
}