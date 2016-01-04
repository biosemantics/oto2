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
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;

public class OntologyFileDAOTest {

	public static void main(String[] args) throws Exception {
		ConnectionPool connectionPool = new ConnectionPool();
		Query.connectionPool = connectionPool;
		PermanentOntologyFileDAO.loadPermanentOntologies();
		
		List<Term> terms = new LinkedList<Term>();
		Term term = new Term(999, "term", "term", "iri", "bucket", "category", false, 999);
		
		Map<Commentable, List<Comment>> comments = new HashMap<Commentable, List<Comment>>();
		Map<Colorable, Color> colorizations = new HashMap<Colorable, Color>();
		List<Color> colors = new LinkedList<Color>();
		Map<Term, Set<Object>> usedTerms = new HashMap<Term, Set<Object>>();
		
		Collection collection = new Collection(999, "name", TaxonGroup.PLANT, 
				"secret", terms, comments, colorizations, colors, usedTerms);
		OntologyDAO dbDAO = new OntologyDAO();
		OntologyFileDAO ontologyFileDAO = new OntologyFileDAO(collection, dbDAO);
		
		Set<TaxonGroup> taxonGroups = new HashSet<TaxonGroup>();
		taxonGroups.add(TaxonGroup.PLANT);
		Ontology ontology = new Ontology(999, "http://etc-project.org/999", "ont-name", 
				"on", taxonGroups, "http://browse", false, 999);
		ontologyFileDAO.insertOntology(ontology);
		
		/*
		 * int id, int collectionId, Term term, String submissionTerm, Ontology ontology, String classIRI,
			List<Superclass> superclasses, String definition, List<Synonym> synonyms, String source, String sampleSentence, 
			List<PartOf> partOfs, String user, List<OntologyClassSubmissionStatus> submissionStatuses, Date lastUpdated, Date created
		 */
		List<Superclass> superclasses = new LinkedList<Superclass>();
		Superclass s = new Superclass(999, Type.ENTITY.getIRI());
		superclasses.add(s);
		List<Synonym> synonyms = new LinkedList<Synonym>();
		List<PartOf> partOfs = new LinkedList<PartOf>();
		List<OntologyClassSubmissionStatus> status = new LinkedList<OntologyClassSubmissionStatus>();
		Date lastUpdated = new Date();
		Date created = new Date();

		OntologyClassSubmission cs = new OntologyClassSubmission(999, 999, term, "term", ontology, 
				"http://www.etc-project.org/owl/ontologies/999/on#999",
				superclasses, "def", synonyms, "src", "sample", partOfs, "user", status, lastUpdated, created);
		
		
		ontologyFileDAO.insertClassSubmission(cs);
		
	}
	
}
