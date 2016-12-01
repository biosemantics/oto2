package translate;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.PredefinedVertex;

public class CreateExistingClasses {

	public static void main(String[] args) throws IOException {
		Map<String, List<PredefinedVertex>> map = new LinkedHashMap<String, List<PredefinedVertex>>();
		map.put(Type.SUBCLASS_OF.getRootLabel(), Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("material anatomical entity", false),
				new PredefinedVertex("quality", false),
				new PredefinedVertex("modifier", false)
		}));
		map.put("quality", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("coloration", false),
				new PredefinedVertex("reflection", false)
		}));
		map.put("material anatomical entity", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("non-specific material anatomical entity", false)
		}));
		map.put("modifier", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("certainty_modifiers", false),
				new PredefinedVertex("coverage_modifiers", false),
				new PredefinedVertex("degree_modifiers", false),
				new PredefinedVertex("frequency_modifiers", false)
		}));
		map.put("certainty_modifiers", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("less than 25% certain", false),
				new PredefinedVertex("around 25% certain", false),
				new PredefinedVertex("around 50% certain", false),
				new PredefinedVertex("around 75% certain", false),
				new PredefinedVertex("greater than 75% certain", false)
		}));
		map.put("coverage_modifiers", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("less than 25% coverage", false),
				new PredefinedVertex("around 25% coverage", false),
				new PredefinedVertex("around 50% coverage", false),
				new PredefinedVertex("around 75% coverage", false),
				new PredefinedVertex("greater than 75% coverage", false)
		}));
		map.put("degree_modifiers", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("less than 25% degree", false),
				new PredefinedVertex("around 25% degree", false),
				new PredefinedVertex("around 50% degree", false),
				new PredefinedVertex("around 75% degree", false),
				new PredefinedVertex("greater than 75% degree", false)
		}));
		map.put("frequency_modifiers", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("less than 25% frequency", false),
				new PredefinedVertex("around 25% frequency", false),
				new PredefinedVertex("around 50% frequency", false),
				new PredefinedVertex("around 75% frequency", false),
				new PredefinedVertex("greater than 75% frequency", false)
		}));
		map.put("coloration", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("red", false), 
				new PredefinedVertex("blue", false),
				new PredefinedVertex("green", false),
				new PredefinedVertex("yellow", false),
				new PredefinedVertex("white", false),
				new PredefinedVertex("black", false),
				new PredefinedVertex("brown", false),
				new PredefinedVertex("gray", false),
				new PredefinedVertex("pink", false),
				new PredefinedVertex("orange", false),
				new PredefinedVertex("violet", false)
		}));
		map.put("reflection", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("bright", false), 
				new PredefinedVertex("translucent", false),
				new PredefinedVertex("opaque", false)
		}));
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File("src/main/resources/edu/arizona/biosemantics/oto2/ontologize2/classes.json"), map);
		
		map = new LinkedHashMap<String, List<PredefinedVertex>>();
		map.put(Type.PART_OF.getRootLabel(), Arrays.asList(new PredefinedVertex[] {
		}));
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File("src/main/resources/edu/arizona/biosemantics/oto2/ontologize2/parts.json"), map);
		
		map = new LinkedHashMap<String, List<PredefinedVertex>>();
		map.put(Type.SYNONYM_OF.getRootLabel(), Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("less than 25% certain", false),
				new PredefinedVertex("around 25% certain", false),
				new PredefinedVertex("around 50% certain", false),
				new PredefinedVertex("around 75% certain", false),
				new PredefinedVertex("greater than 75% certain", false),
				new PredefinedVertex("less than 25% coverage", false),
				new PredefinedVertex("around 25% coverage", false),
				new PredefinedVertex("around 50% coverage", false),
				new PredefinedVertex("around 75% coverage", false),
				new PredefinedVertex("greater than 75% coverage", false),
				new PredefinedVertex("less than 25% degree", false),
				new PredefinedVertex("around 25% degree", false),
				new PredefinedVertex("around 50% degree", false),
				new PredefinedVertex("around 75% degree", false),
				new PredefinedVertex("greater than 75% degree", false),
				new PredefinedVertex("less than 25% frequency", false),
				new PredefinedVertex("around 25% frequency", false),
				new PredefinedVertex("around 50% frequency", false),
				new PredefinedVertex("around 75% frequency", false),
				new PredefinedVertex("greater than 75% frequency", false)
		}));
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File("src/main/resources/edu/arizona/biosemantics/oto2/ontologize2/synonyms.json"), map);
	}
}
