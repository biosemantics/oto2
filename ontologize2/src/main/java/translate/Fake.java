package translate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class Fake {
	
	public static void main(String[] args) throws Exception {
		Fake fake = new Fake();
		fake.run();
	}

	private void run() throws Exception {
		OntologyGraph g = new OntologyGraph(Type.values());
		for(int i=0; i<20; i=i+2) {
			OntologyGraph.Edge e = new OntologyGraph.Edge(g.getRoot(Type.SUBCLASS_OF), 
					new Vertex("m" + i), Type.SUBCLASS_OF, Origin.USER);
			g.addRelation(e);	
			if(i>=1) {
				e = new OntologyGraph.Edge(new Vertex("m" + i), 
						new Vertex("m" + (i+1)), Type.SUBCLASS_OF, Origin.USER);
				g.addRelation(e);	
			}
		}
		
		Collection c = new Collection();
		c.setGraph(g);
		c.setId(0);
		c.setSecret("secret");
		c.setTaxonGroup(TaxonGroup.SPIDER);
		c.setName("steven");		
		c.add(new Candidate("a"));
		c.add(new Candidate("b"));
		c.add(new Candidate("c"));
		c.add(new Candidate("d"));
		c.add(new Candidate("e"));
		c.add(new Candidate("f"));
		c.add(new Candidate("g"));
		c.add(new Candidate("h"));
		c.add(new Candidate("i"));
		c.add(new Candidate("z"));
		
		serializeCollection(c);
	}

	private void serializeCollection(Collection collection) {
		//String collectionDir = Configuration.collectionsDirectory + File.separator + collection.getId();
		String collectionDir = "collections/0";
		
		File collectionDirectory = new File(collectionDir);
		if(!collectionDirectory.exists())
			collectionDirectory.mkdir();
		File owlDirectory = new File(collectionDir + File.separator + "owl");
		if(!owlDirectory.exists())
			owlDirectory.mkdir();
		
		try(ObjectOutputStream collectionOutput = new ObjectOutputStream(new FileOutputStream(
				collectionDir + File.separator + "collection.ser"))) {
			collectionOutput.writeObject(collection);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
