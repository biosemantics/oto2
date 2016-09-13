package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class OntologyGraphReducer {

	private static class Path extends LinkedList<Vertex> {
		
	}
	
	public static void main(String[] args) throws Exception {
		OntologyGraphReducer reducer = new OntologyGraphReducer();
		
		OntologyGraph g = new OntologyGraph(Type.values());
		g.addRelation(new Edge(new Vertex("Thing"), new Vertex("m"), Type.SUBCLASS_OF, Origin.USER));
		g.addRelation(new Edge(new Vertex("m"), new Vertex("b"), Type.SUBCLASS_OF, Origin.USER));
		g.addRelation(new Edge(new Vertex("b"), new Vertex("c"), Type.SUBCLASS_OF, Origin.USER));
		g.addRelation(new Edge(new Vertex("m"), new Vertex("c"), Type.SUBCLASS_OF, Origin.USER));
		
		//System.out.println(g);
		reducer.reduce(g);
		//System.out.println(g);
		
		/*Path l = new Path();
		l.add(new Vertex("a"));
		l.add(new Vertex("b"));
		l.add(new Vertex("c"));
		Path s = new Path();
		s.add(new Vertex("c"));
		System.out.println(getRedundantPathPrefix(s, l));*/
	}
	
	public void reduce(OntologyGraph g) {
		for(Type type : Type.values()) {
			for(Vertex v : g.getVerticesBFS(type)) {
				/*if(v.getValue().equals("nonparasitic form female T5 notch") && type.equals(Type.SUBCLASS_OF))
					System.out.println();
				if(v.getValue().equals("female T5 notch") && type.equals(Type.SUBCLASS_OF))
					System.out.println();
				if(v.getValue().equals("T5 notch") && type.equals(Type.SUBCLASS_OF))
					System.out.println();
				if(v.getValue().equals("notch") && type.equals(Type.SUBCLASS_OF))
					System.out.println();*/
				List<Path> pathsToRoot = getPathsToRoot(g, type, v);
				Collections.sort(pathsToRoot, new Comparator<Path>() {
					@Override
					public int compare(Path o1, Path o2) {
						return o1.size() - o2.size();
					}
				});
				for(Path path : pathsToRoot) {
					removeRedundantPaths(g, type, path, pathsToRoot);
				}
			}
		}
	}

	private void removeRedundantPaths(OntologyGraph g, Type type, Path path, List<Path> pathsToRoot) {
		for(Path pathToRoot : pathsToRoot) {
			if(!path.equals(pathToRoot)) {
				Path redundantPrefix = getRedundantPathPrefix(path, pathToRoot);
				if(redundantPrefix.size() >= 2) {
					//for(int i = 0; i < redundantPrefix.size() - 1; i++) {
						Vertex src = redundantPrefix.get(0);
						Vertex dest = redundantPrefix.get(1);
						System.out.println("remove edge " + src + " --> " + dest);
						g.removeEdge(new Edge(src, dest, type, Origin.USER));
						g.removeEdge(new Edge(src, dest, type, Origin.IMPORT));
					//}
				}
			}
		}
	}

	private Path getRedundantPathPrefix(Path shortPath, Path longPath) {
		Path result = new Path();
		if(shortPath.size() < 2 || longPath.size() <2 || shortPath.size() > longPath.size())
			return new Path();
		if(!shortPath.getFirst().equals(longPath.getFirst()))
			return new Path();
		if(!shortPath.getLast().equals(longPath.getLast()))
			return new Path();
		
		boolean inPrefix = true;
		int longIndex = 0;
		for(int shortIndex = 0; shortIndex < shortPath.size(); shortIndex++) {
			Vertex shortVertex = shortPath.get(shortIndex);
			if(longIndex >= longPath.size())
				return new Path();
			Vertex longVertex = longPath.get(longIndex++);
			
			if(shortVertex.equals(longVertex)) {
				if(inPrefix) 
					result.add(0, shortVertex);
				continue;
			} else {
				while(!shortVertex.equals(longVertex)) {
					if(longIndex >= longPath.size())
						return new Path();
					longVertex = longPath.get(longIndex++);
					if(inPrefix)
						result.add(0, shortVertex);
					inPrefix = false;
				}
			}
		}
		if(longIndex == longPath.size())
			return result;
		return new Path();
	}

	public List<Path> getPathsToRoot(OntologyGraph g, Type type, Vertex v) {
		List<Path> result = new LinkedList<Path>();
		if(v.equals(g.getRoot(type))) {
			Path path = new Path();
			path.add(v);
			result.add(path);
			return result;
		}
			
		List<Vertex> sources = g.getSources(v, type);
		for(Vertex src : sources) {
			List<Path> paths = getPathsToRoot(g, type, src);
			for(Path p : paths) {
				p.add(0, v);
			}
			result.addAll(paths);
		}
		return result;
	}
	
}
