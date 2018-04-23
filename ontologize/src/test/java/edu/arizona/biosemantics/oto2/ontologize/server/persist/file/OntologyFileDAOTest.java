package edu.arizona.biosemantics.oto2.ontologize.server.persist.file;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.ConnectionPool;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.OntologyDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Color;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Colorable;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Comment;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Commentable;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionStatus;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmissionStatus;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;

public class OntologyFileDAOTest {

/*	private static Collection collection;
	private static OntologyDAO dbDAO;
	private static OntologyFileDAO ontologyFileDAO;
	private static Ontology ontology;
	private static Term futbol;
	private static Term football;
	private static OntologyClassSubmission cs1;
	private static OntologyClassSubmission cs2;
	private static OntologySynonymSubmission ss1;
	private static OntologyClassSubmission cs0;
	private static OntologySynonymSubmission ss2;

	public static void main(String[] args) throws Exception {
		ConnectionPool connectionPool = new ConnectionPool();
		Query.connectionPool = connectionPool;
		PermanentOntologyFileDAO.loadPermanentOntologies();
		
		List<Term> terms = new LinkedList<Term>();
		futbol = new Term(999, "futbol", "futbol", "futbol", "bucket", "category", false, 999);
		football = new Term(1000, "football", "football", "football", "bucket", "category", false, 999);
		
		Map<Commentable, List<Comment>> comments = new HashMap<Commentable, List<Comment>>();
		Map<Colorable, Color> colorizations = new HashMap<Colorable, Color>();
		List<Color> colors = new LinkedList<Color>();
		Map<Term, Set<Object>> usedTerms = new HashMap<Term, Set<Object>>();
		
		collection = new Collection(999, "name", TaxonGroup.PLANT, 
				"secret", terms, comments, colorizations, colors, usedTerms);
		dbDAO = new OntologyDAO();
		ontologyFileDAO = new OntologyFileDAO(collection, dbDAO);
		
		
		Set<TaxonGroup> taxonGroups = new HashSet<TaxonGroup>();
		taxonGroups.add(TaxonGroup.PLANT);
		ontology = new Ontology(-1, 
				"iri",
				"ont-name", 
				"on", taxonGroups, "http://browse", false, 999);
		ontology.setIri(Configuration.etcOntologyBaseIRI + collection.getId() + "/" + ontology.getAcronym());
		ontology = dbDAO.insert(ontology);
		ontologyFileDAO.insertOntology(ontology);
		
		createClassSubmission1();
		createClassSubmission2();
		createClassSubmission0();
		
		createSynonymSubmission();
		createSynonymSubmission2();
	}

	private static void createSynonymSubmission2() throws Exception {
		List<Synonym> synonyms = new LinkedList<Synonym>();
		synonyms.add(new Synonym("add syn5"));
		
		ss2 = new OntologySynonymSubmission(999, 999, football, 
				"football", ontology, 
				"http://purl.obolibrary.org/obo/CARO_0000003", 
				"", 
				synonyms, 
				"", "", "", new LinkedList<OntologySynonymSubmissionStatus>(), new Date(0), new Date());
		ontologyFileDAO.insertSynonymSubmission(ss2);
	}
	
	private static void createSynonymSubmission() throws Exception {
		List<Synonym> synonyms = new LinkedList<Synonym>();
		synonyms.add(new Synonym("add syn1"));
		synonyms.add(new Synonym("add syn2"));
		synonyms.add(new Synonym("add syn3"));
		
		ss1 = new OntologySynonymSubmission(999, 999, football, 
				"football", ontology, 
				cs1.getClassIRI(), 
				"", 
				synonyms, 
				"", "", "", new LinkedList<OntologySynonymSubmissionStatus>(), new Date(0), new Date());
		ontologyFileDAO.insertSynonymSubmission(ss1);
	}

	private static void createClassSubmission2() throws Exception {
		List<Superclass> superclasses = new LinkedList<Superclass>();
		Superclass s = new Superclass(999, Type.ENTITY.getIRI());
		Superclass s2 = new Superclass(999, "http://purl.obolibrary.org/obo/PATO_0000012");
		superclasses.add(s);
		superclasses.add(s2);
		List<Synonym> synonyms = new LinkedList<Synonym>();
		List<PartOf> partOfs = new LinkedList<PartOf>();
		List<OntologyClassSubmissionStatus> status = new LinkedList<OntologyClassSubmissionStatus>();
		Date lastUpdated = new Date();
		Date created = new Date();
		
		partOfs.add(new PartOf(cs1));
		partOfs.add(new PartOf(999, Type.ENTITY.getIRI()));
		cs2 = new OntologyClassSubmission(999, 999, futbol, "futbol2", ontology, 
				"http://www.etc-project.org/owl/ontologies/" + collection.getId() + "/on#901",
				superclasses, "def", synonyms, "src", "sample", partOfs, "user", status, lastUpdated, created);
		
		ontologyFileDAO.insertClassSubmission(cs2);
	}
	
	private static void createClassSubmission0() throws Exception {
		List<Superclass> superclasses = new LinkedList<Superclass>();
		Superclass s = new Superclass(999, Type.ENTITY.getIRI());
		Superclass s2 = new Superclass(999, "http://purl.obolibrary.org/obo/PATO_0000012");
		superclasses.add(s);
		superclasses.add(s2);
		List<Synonym> synonyms = new LinkedList<Synonym>();
		List<PartOf> partOfs = new LinkedList<PartOf>();
		partOfs.add(new PartOf(cs1));
		List<OntologyClassSubmissionStatus> status = new LinkedList<OntologyClassSubmissionStatus>();
		Date lastUpdated = new Date();
		Date created = new Date();

		cs0 = new OntologyClassSubmission(999, 999, futbol, "futbol", ontology, 
				"http://purl.obolibrary.org/obo/PATO_0001085",
				superclasses, "def", synonyms, "src", "sample", partOfs, "user", status, lastUpdated, created);
		cs0.addSynonym(new Synonym("hello"));
		
		ontologyFileDAO.insertClassSubmission(cs0);
	}

	private static void createClassSubmission1() throws Exception {
		List<Superclass> superclasses = new LinkedList<Superclass>();
		Superclass s = new Superclass(999, Type.ENTITY.getIRI());
		superclasses.add(s);
		List<Synonym> synonyms = new LinkedList<Synonym>();
		List<PartOf> partOfs = new LinkedList<PartOf>();
		
		List<OntologyClassSubmissionStatus> status = new LinkedList<OntologyClassSubmissionStatus>();
		Date lastUpdated = new Date();
		Date created = new Date();

		cs1 = new OntologyClassSubmission(999, 999, futbol, "futbol", ontology, 
				"http://www.etc-project.org/owl/ontologies/" + collection.getId() + "/on#900",
				superclasses, "def", synonyms, "src", "sample", partOfs, "user", status, lastUpdated, created);
		cs1.addSynonym(new Synonym("hello"));
		
		ontologyFileDAO.insertClassSubmission(cs1);
	}
*/	
}
