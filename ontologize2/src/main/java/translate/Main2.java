package translate;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraphReducer;

public class Main2 {

	public static void main(String[] args) {
		String collectionDir = "collections/0/";
		
		try(ObjectInputStream collectionIn = new ObjectInputStream(new FileInputStream(
				collectionDir + File.separator + "collection.ser"))) {
			Object object = collectionIn.readObject();
			if(object instanceof Collection) {
				Collection c = (Collection)object;
				OntologyGraph g = c.getGraph();
				
				Vertex a = g.getVertex("nonparasitic form female T5 notch");
				System.out.println(g.getInRelations(a, Type.SUBCLASS_OF));
				for(Edge i : g.getInRelations(a, Type.SUBCLASS_OF)) {
					System.out.println(g.getInRelations(i.getSrc(), Type.SUBCLASS_OF));
					for(Edge i2 : g.getInRelations(i.getSrc(), Type.SUBCLASS_OF)) {
						System.out.println(g.getInRelations(i2.getSrc(), Type.SUBCLASS_OF));
						for(Edge i3 : g.getInRelations(i2.getSrc(), Type.SUBCLASS_OF)) {
							System.out.println(g.getInRelations(i3.getSrc(), Type.SUBCLASS_OF));
						}
					}
				}
				
				OntologyGraphReducer reducer = new OntologyGraphReducer();
				reducer.reduce(g);
				
				a = g.getVertex("nonparasitic form female T5 notch");
				System.out.println(g.getInRelations(a, Type.SUBCLASS_OF));
				for(Edge i : g.getInRelations(a, Type.SUBCLASS_OF)) {
					System.out.println(g.getInRelations(i.getSrc(), Type.SUBCLASS_OF));
					for(Edge i2 : g.getInRelations(i.getSrc(), Type.SUBCLASS_OF)) {
						System.out.println(g.getInRelations(i2.getSrc(), Type.SUBCLASS_OF));
						for(Edge i3 : g.getInRelations(i2.getSrc(), Type.SUBCLASS_OF)) {
							System.out.println(g.getInRelations(i3.getSrc(), Type.SUBCLASS_OF));
						}
					}
				}
				
				for(Vertex v : g.getVertices()) {
					if(v.getValue().equals("nonparasitic form female T5 notch"))
						System.out.println();
					for(Edge.Type type : Edge.Type.values()) {
						if(g.getInRelations(v, type).size() > 1) {
							System.out.println(v);
							System.out.println(g.getInRelations(v, type));
							System.out.println(reducer.getPathsToRoot(g, type, v));
							System.out.println("--------------");
						}
					}
				}
				
				System.out.println(c);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
