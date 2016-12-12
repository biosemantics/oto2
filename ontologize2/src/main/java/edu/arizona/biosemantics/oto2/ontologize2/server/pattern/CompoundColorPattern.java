package edu.arizona.biosemantics.oto2.ontologize2.server.pattern;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.PredefinedVertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

/**
 * in: coloration -> {part} or reflection -> {part}; for all parts
 * out: {part} -> {candidate}; for all parts 
 * 
 * e.g. 
 * candidate: bright white gray
 * in: coloration -> white, gray; reflection -> bright
 * out: white -> bright white gray
 * out: gray -> bright white gray
 * out: bright -> bright white gray
 * @author rodenhausen
 */
public class CompoundColorPattern implements CandidatePattern {

	private Set<String> colorOrReflections = new HashSet<String>();
	private Set<String> colors = new HashSet<String>();
	private Set<String> reflections = new HashSet<String>();

	public CompoundColorPattern() {
		ObjectMapper mapper = new ObjectMapper();
		TypeFactory typeFactory = mapper.getTypeFactory();
		MapType mapType = typeFactory.constructMapType(LinkedHashMap.class, typeFactory.constructType(String.class), 
				typeFactory.constructCollectionType(List.class, PredefinedVertex.class));
		try {
			LinkedHashMap<String, List<PredefinedVertex>> subclasses = (LinkedHashMap<String, List<PredefinedVertex>>)mapper.readValue(new File(Configuration.existingClassesFile), mapType);
			for(PredefinedVertex c : subclasses.get("coloration")) {
				colorOrReflections.add(c.getValue());
				colors.add(c.getValue());
			}
			for(PredefinedVertex r : subclasses.get("reflection")) {
				colorOrReflections.add(r.getValue());
				reflections.add(r.getValue());
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Could not read json", e);
		}
	}
	


	private void updateForCollection(Collection collection, Set<String> colors, Set<String> reflections, Set<String> colorOrReflections) {
		for(Vertex v : collection.getGraph().getDestinations(new Vertex("coloration"), Type.SUBCLASS_OF)) {
			colors.add(v.getValue());
			colorOrReflections.add(v.getValue());
		}
		for(Vertex v : collection.getGraph().getDestinations(new Vertex("reflection"), Type.SUBCLASS_OF)) {
			reflections.add(v.getValue());
			colorOrReflections.add(v.getValue());
		}
	}

	@Override
	public List<Edge> getRelations(Collection collection, Candidate c) {
		OntologyGraph g = collection.getGraph();
		Set<String> colors = new HashSet<String>(this.colors);
		Set<String> reflections = new HashSet<String>(this.reflections);
		Set<String> colorOrReflections = new HashSet<String>(this.colorOrReflections);
		this.updateForCollection(collection, colors, reflections, colorOrReflections);
		
		List<Edge> result = new LinkedList<Edge>();		
		String normalizedFull = c.getText().replaceAll("[-_\\s]", " ");
		String[] parts = c.getText().split("[-_\\s]");
		boolean allColorOrReflections = true;
		for(String p : parts) {
			if(!colorOrReflections.contains(p)) {
				allColorOrReflections = false;
				break;
			}
		}
		
		if(allColorOrReflections) {
			if(parts.length > 1) {
				for(String superclass : parts) {
					Edge e = new Edge(new Vertex(superclass), new Vertex(normalizedFull), Type.SUBCLASS_OF, Origin.USER);
					result.add(e);
				}
			}
		}
		return result;
	}

	@Override
	public String getName() {
		return "Compound Pattern";
	}
}
