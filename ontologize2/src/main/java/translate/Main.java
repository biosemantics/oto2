package translate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.oto2.ontologize2.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class Main {
	
	private class ClassSub {

		private int id;
		private String term;
		private String iri;
				
		public ClassSub(int id, String term, String iri) {
			super();
			this.id = id;
			this.term = term;
			this.iri = iri;
		}

		public String getTerm() {
			return term;
		}

		public String getIri() {
			return iri;
		}

		public Integer getId() {
			return id;
		}
		
	}
	
	private class SynSub {

		private int id;
		private String term;
		private String iri;
				
		public SynSub(int id, String term, String iri) {
			super();
			this.id = id;
			this.term = term;
			this.iri = iri;
		}

		public String getTerm() {
			return term;
		}

		public String getIri() {
			return iri;
		}

		public Integer getId() {
			return id;
		}
		
	}

	public static void main(String[] args) throws Exception {
		Main main = new Main();
		main.run();
	}

	private void run() throws Exception {
		OntologyGraph g = new OntologyGraph(Type.values());
		Map<Integer, ClassSub> csMap = new HashMap<Integer, ClassSub>();
		Map<String, ClassSub> iriCsMap = new HashMap<String, ClassSub>();
		Map<Integer, SynSub> ssMap = new HashMap<Integer, SynSub>();
		Map<String, SynSub> iriSsMap = new HashMap<String, SynSub>();
		
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("termsuser");
		dataSource.setPassword("termspassword");
		dataSource.setServerName("localhost");
		dataSource.setDatabaseName("steven_temp");
		
		Connection conn = dataSource.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM ontologize_ontologyclasssubmission");
		while(rs.next()) {
			ClassSub cs = createClassSub(rs);
			csMap.put(cs.getId(), cs);
			iriCsMap.put(cs.getIri(), cs);
		}
		rs.close();
		stmt.close();		
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT * FROM ontologize_ontologysynonymsubmission");
		while(rs.next()) {
			SynSub ss = createSynSub(rs);
			ssMap.put(ss.getId(), ss);
			iriSsMap.put(ss.getIri(), ss);
		}
		rs.close();
		stmt.close();
		
		OntologyGraph.Edge e2 = new OntologyGraph.Edge(g.getRoot(Type.SUBCLASS_OF), 
				new Vertex("material anatomical entity"), Type.SUBCLASS_OF, Origin.USER);	
		g.addRelation(e2);
		System.out.println("add relation: " + e2);
		iriCsMap.put("http://purl.obolibrary.org/obo/CARO_0000006", new ClassSub(-1, "material anatomical entity", "http://purl.obolibrary.org/obo/CARO_0000006"));
		
		OntologyGraph.Edge e3 = new OntologyGraph.Edge(g.getRoot(Type.SUBCLASS_OF), 
				new Vertex("quality"), Type.SUBCLASS_OF, Origin.USER);	
		g.addRelation(e3);
		System.out.println("add relation: " + e3);
		iriCsMap.put("http://purl.obolibrary.org/obo/PATO_0000001", new ClassSub(-1, "quality", "http://purl.obolibrary.org/obo/PATO_0000001"));
		
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT * FROM ontologize_ontologyclasssubmission_superclass");
		while(rs.next()) {
			int csId = rs.getInt(2);
			String superclassIRI = rs.getString(3);
			
			if(!iriCsMap.containsKey(superclassIRI)) {
				System.out.println("test: " + superclassIRI);
			}
			
			OntologyGraph.Edge e = new OntologyGraph.Edge(new Vertex(iriCsMap.get(superclassIRI).getTerm()), 
					new Vertex(csMap.get(csId).getTerm()), Type.SUBCLASS_OF, Origin.USER);
			System.out.println("add relation: " + e);
			g.addRelation(e);
		}
		rs.close();
		stmt.close();
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT * FROM ontologize_ontologyclasssubmission_partof");
		while(rs.next()) {
			int csId = rs.getInt(2);
			String partofIRI = rs.getString(3);
			
			OntologyGraph.Edge e = new OntologyGraph.Edge(new Vertex(iriCsMap.get(partofIRI).getTerm()), 
					new Vertex(csMap.get(csId).getTerm()), Type.PART_OF, Origin.USER);
			System.out.println("add relation: " + e);
			g.addRelation(e);
			
		}
		rs.close();
		stmt.close();
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT * FROM ontologize_ontologyclasssubmission_synonym");
		while(rs.next()) {
			int csId = rs.getInt(2);
			String synonym = rs.getString(3);
			
			OntologyGraph.Edge e = new OntologyGraph.Edge(g.getRoot(Type.SYNONYM_OF), 
					new Vertex(csMap.get(csId).getTerm()), Type.SYNONYM_OF, Origin.USER);
			System.out.println("add relation: " + e);
			g.addRelation(e);
			
			e = new OntologyGraph.Edge(new Vertex(csMap.get(csId).getTerm()), 
					new Vertex(synonym), Type.SYNONYM_OF, Origin.USER);
			System.out.println("add relation: " + e);
			g.addRelation(e);
		}
		rs.close();
		stmt.close();
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT * FROM ontologize_ontologysynonymsubmission_synonym");
		while(rs.next()) {
			int ssId = rs.getInt(2);
			String synonym = rs.getString(3);
			
			OntologyGraph.Edge e = new OntologyGraph.Edge(new Vertex(ssMap.get(ssId).getTerm()), 
					new Vertex(synonym), Type.SYNONYM_OF, Origin.USER);
			System.out.println("add relation: " + e);
			g.addRelation(e);
		}
		rs.close();
		stmt.close();
		
		
		for(ClassSub cs : csMap.values()) {
			if(!g.containsVertex(new Vertex(cs.getTerm()))) {
				OntologyGraph.Edge e = new OntologyGraph.Edge(g.getRoot(Type.SUBCLASS_OF), 
						new Vertex(cs.getTerm()), Type.SUBCLASS_OF, Origin.USER);
				System.out.println("add relation: " + e);
				g.addRelation(e);
			}
		}
		
		for(Vertex src : g.getVertices()) {
			if(!src.equals(g.getRoot(Type.PART_OF)) && !src.equals(g.getRoot(Type.SUBCLASS_OF)) && 
					!src.equals(g.getRoot(Type.SYNONYM_OF))) {
				if(g.getInRelations(src).isEmpty()) {
					for(Type type : Type.values()) {
						if(!g.getOutRelations(src, type).isEmpty()) {
							OntologyGraph.Edge e = new OntologyGraph.Edge(g.getRoot(type), 
									src, type, Origin.USER);
							System.out.println("add relation: " + e);
							g.addRelation(e);
						}
					}
				}
			}
		}
		
		Collection c = new Collection();
		c.setGraph(g);
		c.setId(0);
		c.setSecret("secret");
		c.setTaxonGroup(TaxonGroup.SPIDER);
		c.setName("steven");
		
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT * FROM ontologize_term");
		while(rs.next()) {
			c.add(new Candidate(rs.getString(2), rs.getString(5)));
		}
		rs.close();
		stmt.close();
		
		conn.close();
		
		serializeCollection(c);
	}

	private ClassSub createClassSub(ResultSet rs) throws SQLException {
		return new ClassSub(rs.getInt(1), rs.getString(4), rs.getString(6));
	}
	
	private SynSub createSynSub(ResultSet rs) throws SQLException {
		return new SynSub(rs.getInt(1), rs.getString(4), rs.getString(6));
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
