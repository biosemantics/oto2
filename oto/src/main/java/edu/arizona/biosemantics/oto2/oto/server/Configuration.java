package edu.arizona.biosemantics.oto2.oto.server;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import au.com.bytecode.opencsv.CSVReader;

public class Configuration {

	/** Database **/
	public static String databaseName;
	public static String databaseUser;
	public static String databasePassword;
	public static String databaseHost;
	public static String databasePort;
	
	/** Bioportal **/
	public static String bioportalUrl;
	public static String bioportalApiKey;
	
	/** Default Categories **/
	private static List<Label> defaultCategories = new LinkedList<Label>();
	
	static {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Properties properties = new Properties(); 
			properties.load(loader.getResourceAsStream("config.properties"));
			
			databaseName = properties.getProperty("databaseName");
			databaseUser = properties.getProperty("databaseUser");
			databasePassword = properties.getProperty("databasePassword");
			databaseHost = properties.getProperty("databaseHost");
			databasePort = properties.getProperty("databasePort");
			
			bioportalUrl = properties.getProperty("bioportalUrl");
			bioportalApiKey = properties.getProperty("bioportalApiKey");
			
			try(CSVReader reader = new CSVReader(new InputStreamReader(loader.getResourceAsStream("defaultCategories.csv")))) {
				List<String[]> lines = reader.readAll();
				for(String[] line : lines) {
					defaultCategories.add(new Label(line[0], line[1]));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<Label> getDefaultCategories() {
		return new LinkedList<Label>(defaultCategories);
	}
}
