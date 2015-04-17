package edu.arizona.biosemantics.oto2.steps.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import edu.arizona.biosemantics.common.log.Logger;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class Configuration {

	private final static Logger logger = Logger.getLogger(Configuration.class);

	/** Ontologies **/
	public static String etcOntologyBaseIRI;
	public static String oboOntologyBaseIRI;
	public static String permanentOntologyDirectory;
	public static String collectionOntologyDirectory;
	
	/** Database **/
	public static String databaseName;
	public static String databaseUser;
	public static String databasePassword;
	public static String databaseHost;
	public static String databasePort;
	
	/** Temporary Storage **/
	public static String files;
	
	/** Bioportal **/
	public static String bioportalUrl;
	public static String bioportalApiKey;
	
	/** OTO **/
	public static String otoClientUrl;
	
	private static Properties properties;

	static {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			properties = new Properties(); 
			properties.load(loader.getResourceAsStream("edu/arizona/biosemantics/oto2/steps/config.properties"));
			
			etcOntologyBaseIRI = properties.getProperty("etcOntologyBaseIRI");
			oboOntologyBaseIRI = properties.getProperty("oboOntologyBaseIRI");
			permanentOntologyDirectory = properties.getProperty("permanentOntologyDirectory");
			collectionOntologyDirectory = properties.getProperty("collectionOntologyDirectory");
			
			databaseName = properties.getProperty("databaseName");
			databaseUser = properties.getProperty("databaseUser");
			databasePassword = properties.getProperty("databasePassword");
			databaseHost = properties.getProperty("databaseHost");
			databasePort = properties.getProperty("databasePort");
			
			files = properties.getProperty("files");
			
			bioportalUrl = properties.getProperty("bioportalUrl");
			bioportalApiKey = properties.getProperty("bioportalApiKey");
			
			otoClientUrl = properties.getProperty("otoClientUrl");
		} catch(Exception e) {
			logger.error("Couldn't read configuration", e);
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		/*try(CSVReader reader = new CSVReader(new FileReader(new File("src/main/resources/defaultCategories.csv")))) {
			List<String[]> lines = reader.readAll();
			List<String[]> newLines = new ArrayList<String[]>();
			for(String[] line : lines) {
				List<String> newLineList = new ArrayList<String>(Arrays.asList(line));
				if(line[0].startsWith("structure") || line[0].equals("substance") || line[0].equals("taxon_name")) {
					newLineList.add("y");
				} else
					newLineList.add("n");
				newLines.add(newLineList.toArray(new String[newLineList.size()]));
			}
			try(CSVWriter writer = new CSVWriter(new FileWriter(new File("src/main/resources/defCategories.csv")))) {
				writer.writeAll(newLines);
			}
		}*/
	}
	
	public static String asString() {
		try {
			ObjectMapper mapper  = new ObjectMapper();
			ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
			return writer.writeValueAsString(properties);
		} catch (Exception e) {
			//log(LogLevel.ERROR, "Problem writing object as String", e);
			return null;
		}
	}
}
