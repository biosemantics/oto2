package edu.arizona.biosemantics.oto2.ontologize2.server.pattern;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize2.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.PredefinedVertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;

/**
 * in: {candidate with an existing relation known}
 * out: known existing relation
 *  
 * e.g. 
 * candidate: white
 * out: coloration -> white
 * @author rodenhausen
 */
public class PredefinedRelationsPattern implements CandidatePattern {

	private HashMap<Type, LinkedHashMap<String, List<PredefinedVertex>>> existingRelations;

	public PredefinedRelationsPattern() {
		existingRelations = new HashMap<Type, LinkedHashMap<String, List<PredefinedVertex>>>();
		ObjectMapper mapper = new ObjectMapper();
		TypeFactory typeFactory = mapper.getTypeFactory();
		MapType mapType = typeFactory.constructMapType(LinkedHashMap.class, typeFactory.constructType(String.class), 
				typeFactory.constructCollectionType(List.class, PredefinedVertex.class));
		try {
			existingRelations.put(Type.SUBCLASS_OF, (LinkedHashMap<String, List<PredefinedVertex>>)mapper.readValue(new File(Configuration.existingClassesFile), mapType));
			existingRelations.put(Type.PART_OF, (LinkedHashMap<String, List<PredefinedVertex>>)mapper.readValue(new File(Configuration.existingPartsFile), mapType));
			existingRelations.put(Type.SYNONYM_OF, (LinkedHashMap<String, List<PredefinedVertex>>)mapper.readValue(new File(Configuration.existingSynonymsFile), mapType));
		} catch(Exception e) {
			log(LogLevel.ERROR, "Could not read json", e);
		}
	}

	@Override
	public List<Edge> getRelations(Collection collection, Candidate c) {
		OntologyGraph g = collection.getGraph();
		List<Edge> result = new LinkedList<Edge>();
		for(String p : c.getText().split("[-\\s]")) {
			for(Type type : existingRelations.keySet()) {
				for(String superclass : existingRelations.get(type).keySet())  {
					for(PredefinedVertex subclass : existingRelations.get(type).get(superclass)) {
						Edge e = new Edge(new Vertex(superclass), new Vertex(subclass.getValue()), 
								Type.SUBCLASS_OF, Origin.USER);
						if(subclass.isRequiresCandidate() && p.equals(subclass.getValue())) {
							result.add(e);
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public String getName() {
		return "Predefined Relations Pattern";
	}

}
