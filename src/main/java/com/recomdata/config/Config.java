package com.recomdata.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	private static Config config = null;
	
	private Properties configProps = null;
	
	public static Config getConfig() throws IOException {
		if (config == null) {
			config = new Config();
		}
		
		return config;
	}
	
	private Config() throws IOException {
		InputStream is = ClassLoader.getSystemResourceAsStream("config.properties");
		configProps = new Properties();
		configProps.load(is);
		is.close();
	}
	
	public String getProperty(String name) {
		String value = System.getProperty(name);
	
		return (value == null) ? configProps.getProperty(name) : value;
	}	
	
	public String getProperty(String name, String defaultValue) {
		String value = getProperty(name);
		
		return (value == null) ? defaultValue : value;
	}
}
