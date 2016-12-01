package edu.arizona.biosemantics.oto2.ontologize2.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import edu.arizona.biosemantics.common.log.Logger;

public class Configuration {

	private final static Logger logger = Logger.getLogger(Configuration.class);
	private static Properties properties;

	/** **/
	public static String collectionsDirectory;
	public static String ontologyGraphs;
	public static String permanentOntologyDirectory;
	
	public static String etcOntologyBaseIRI;
	
	public static String context;
	public static int contextMaxHits;
	public static String wordNetSource;
	
	public static String existingClassesFile;
	public static String existingPartsFile;
	public static String existingSynonymsFile;
	public static String modifierOntology;

	static {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			properties = new Properties(); 
			properties.load(loader.getResourceAsStream("edu/arizona/biosemantics/oto2/ontologize2/config.properties"));
			
			collectionsDirectory = properties.getProperty("collectionsDirectory");
			ontologyGraphs = properties.getProperty("ontologyGraphs");
			permanentOntologyDirectory = properties.getProperty("permanentOntologyDirectory");
			
			etcOntologyBaseIRI = properties.getProperty("etcOntologyBaseIRI");
			
			context = properties.getProperty("context");
			contextMaxHits = Integer.valueOf(properties.getProperty("contextMaxHits"));
			wordNetSource = properties.getProperty("wordNetSource");
			
			existingClassesFile = properties.getProperty("existing_classes_file");
			existingPartsFile = properties.getProperty("existing_parts_file");
			existingSynonymsFile = properties.getProperty("existing_synonyms_file");
			
			modifierOntology = properties.getProperty("modifier_ontology");
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
