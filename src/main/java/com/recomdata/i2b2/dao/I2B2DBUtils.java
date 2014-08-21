package com.recomdata.i2b2.dao;

/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 * JDBC DB Connection and utilities for I2B2 database.
 * @author Alex Wu
 * @date August 26, 2011
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import com.recomdata.config.Config;


public class I2B2DBUtils
{
	private static Connection conn = null;
	
	private I2B2DBUtils(){}
	
	public static void init(Config config) throws ClassNotFoundException, SQLException {
		String jdbcDriver = config.getProperty("chb.i2b2.jdbc.driver");
		String jdbcUrl = config.getProperty("chb.i2b2.jdbc.url");
		String dbUser = config.getProperty("chb.i2b2.jdbc.dbuser");
		String dbPass = config.getProperty("chb.i2b2.jdbc.dbpasswd");
		
		init(jdbcUrl, dbUser, dbPass, jdbcDriver);
	}
	
	public static void init(String jdbcUrl, String dbUser, String dbPass, String jdbcDriver)
	throws ClassNotFoundException, SQLException {
		String url = jdbcUrl;
		
		Properties props = new Properties();
		props.setProperty("user", dbUser);
		props.setProperty("password", dbPass);
		props.setProperty("ssl", "false");

		Class.forName(jdbcDriver);
		conn = DriverManager.getConnection(url, props);
	}
	
	public static void shutdown() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Error closing connection");
			e.printStackTrace();
		}
	}

	/** Get DB Connection using JDBC Driver and URL
	*/
	public static Connection getI2B2DBConnection() {
		if (conn == null) {
			throw new RuntimeException("Connection not initialized. Call init() first.");
		}
		
		return conn;
	}

	/**
	* Convert util.Date into sql Date
	*/

	public static java.sql.Date getSQLDateFromUtilDate(java.util.Date dt)
	{
		java.sql.Date dte = null;
		
		try {

			dte = new java.sql.Date(dt.getTime());

		}catch(Exception e){
			e.printStackTrace();
		}

		return dte;
	}

	/**
	* Convert Date String into sql Date
	*/

	public static java.sql.Date getSQLDateFromStr(String s)
	{
	   	java.sql.Date dte=null;
	   	try{
		   	String str = s;
		   	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		   	java.util.Date dt = formatter.parse(str);
		   	dte=new java.sql.Date(dt.getTime());
	   	}catch(Exception e){
	   		e.printStackTrace();
	  	}

	  	return dte;
	}
}
