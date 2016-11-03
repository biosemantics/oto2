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
				new PredefinedVertex("frequency modifier", false),
				new PredefinedVertex("certainty modifier", false),
				new PredefinedVertex("approximation modifier", false),
		}));
		map.put("frequency modifier", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("0% frequency modifier", false),
				new PredefinedVertex("25% frequency modifier", false),
				new PredefinedVertex("50% frequency modifier", false),
				new PredefinedVertex("75% frequency modifier", false),
				new PredefinedVertex("100% frequency modifier", false)
		}));
		map.put("certainty modifier", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("0% certainty modifier", false),
				new PredefinedVertex("25% certainty modifier", false),
				new PredefinedVertex("50% certainty modifier", false),
				new PredefinedVertex("75% certainty modifier", false),
				new PredefinedVertex("100% certainty modifier", false)
		}));
		map.put("approximation modifier", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("0% approximation modifier", false),
				new PredefinedVertex("25% approximation modifier", false),
				new PredefinedVertex("50% approximation modifier", false),
				new PredefinedVertex("75% approximation modifier", false),
				new PredefinedVertex("100% approximation modifier", false)
		}));
		map.put("coloration", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("red"), 
				new PredefinedVertex("blue"),
				new PredefinedVertex("green"),
				new PredefinedVertex("yellow"),
				new PredefinedVertex("white"),
				new PredefinedVertex("black"),
				new PredefinedVertex("brown"),
				new PredefinedVertex("gray"),
				new PredefinedVertex("pink"),
				new PredefinedVertex("orange"),
				new PredefinedVertex("purple")
		}));
		map.put("reflection", Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("bright")
		}));
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File("src/main/resources/edu/arizona/biosemantics/oto2/ontologize2/classes.json"), map);
		
		map = new LinkedHashMap<String, List<PredefinedVertex>>();
		map.put(Type.PART_OF.getRootLabel(), Arrays.asList(new PredefinedVertex[] {
		}));
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File("src/main/resources/edu/arizona/biosemantics/oto2/ontologize2/parts.json"), map);
		
		map = new LinkedHashMap<String, List<PredefinedVertex>>();
		map.put(Type.SYNONYM_OF.getRootLabel(), Arrays.asList(new PredefinedVertex[] {
				new PredefinedVertex("0% frequency modifier", false),
				new PredefinedVertex("25% frequency modifier", false),
				new PredefinedVertex("50% frequency modifier", false),
				new PredefinedVertex("75% frequency modifier", false),
				new PredefinedVertex("100% frequency modifier", false),
				new PredefinedVertex("0% certainty modifier", false),
				new PredefinedVertex("25% certainty modifier", false),
				new PredefinedVertex("50% certainty modifier", false),
				new PredefinedVertex("75% certainty modifier", false),
				new PredefinedVertex("100% certainty modifier", false),
				new PredefinedVertex("0% approximation modifier", false),
				new PredefinedVertex("25% approximation modifier", false),
				new PredefinedVertex("50% approximation modifier", false),
				new PredefinedVertex("75% approximation modifier", false),
				new PredefinedVertex("100% approximation modifier", false)
		}));
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File("src/main/resources/edu/arizona/biosemantics/oto2/ontologize2/synonyms.json"), map);
	}
}
