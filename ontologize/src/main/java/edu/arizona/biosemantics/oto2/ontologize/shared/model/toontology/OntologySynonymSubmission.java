package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.Colorable;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Commentable;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;

public class OntologySynonymSubmission implements Serializable, Colorable, Commentable, 
		OntologySubmission, Comparable<OntologySynonymSubmission> {
	
	private int id = -1;
	private int collectionId;
	private Term term;
	private String submissionTerm = "";
	private Ontology ontology;
	private String classIRI = "";
	private String classLabel = "";
	private List<Synonym> synonyms = new LinkedList<Synonym>(); 
	private String source = "";
	private String sampleSentence = "";
	private String user;
	private List<OntologySynonymSubmissionStatus> submissionStatuses = new LinkedList<OntologySynonymSubmissionStatus>();
	private Type type;

	public OntologySynonymSubmission() { }
	
	public OntologySynonymSubmission(int id, int collectionId, Term term, String submissionTerm, Ontology ontology, 
			String classIRI, String classLabel, List<Synonym> synonyms, String source, String sampleSentence,  
			String user, List<OntologySynonymSubmissionStatus> submissionStatuses) { 
		this.id = id;
		this.collectionId = collectionId;
		this.term = term;
		this.submissionTerm = submissionTerm == null ? "" : submissionTerm;
		this.ontology = ontology;
		this.classIRI = classIRI == null ? "" : classIRI;
		this.classLabel = classLabel == null ? "" : classLabel;
		this.source = source == null ? "" : source;
		this.sampleSentence = sampleSentence == null ? "" : sampleSentence;
		this.user = user;
		this.submissionStatuses = submissionStatuses;
		this.synonyms = synonyms;
	}
	
	public OntologySynonymSubmission(int collectionId, Term term, String submissionTerm, Ontology ontology, 
			String classIRI, String classLabel, List<Synonym> synonyms, String source, String sampleSentence, String user) { 
		this.collectionId = collectionId;
		this.term = term;
		this.submissionTerm = submissionTerm == null ? "" : submissionTerm;
		this.ontology = ontology;
		this.classIRI = classIRI == null ? "" : classIRI;
		this.classLabel = classLabel == null ? "" : classLabel;
		this.source = source == null ? "" : source;
		this.sampleSentence = sampleSentence == null ? "" : sampleSentence;
		this.user = user;
		this.synonyms = synonyms;	
	}

	public int getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}

	public boolean hasId() {
		return id != -1;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public String getSubmissionTerm() {
		return submissionTerm;
	}

	public void setSubmissionTerm(String submissionTerm) {
		this.submissionTerm = submissionTerm;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	public String getClassIRI() {
		return classIRI;
	}

	public void setClassIRI(String classIRI) {
		this.classIRI = classIRI;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSampleSentence() {
		return sampleSentence;
	}

	public void setSampleSentence(String sampleSentence) {
		this.sampleSentence = sampleSentence;
	}

	public List<OntologySynonymSubmissionStatus> getSubmissionStatuses() {
		return submissionStatuses;
	}

	public void setSubmissionStatuses(
			List<OntologySynonymSubmissionStatus> submissionStatuses) {
		this.submissionStatuses = submissionStatuses;
	}

	public boolean hasSampleSentence() {
		return this.sampleSentence != null && !sampleSentence.trim().isEmpty();
	}

	public boolean hasSource() {
		return this.source != null && !source.trim().isEmpty();
	}

	public boolean hasSynonyms() {
		return !this.synonyms.isEmpty();
	}

	public boolean hasClassIRI() {
		return this.classIRI != null && !this.getClassIRI().trim().isEmpty();
	}
		
	public List<Synonym> getSynonyms() {
		return synonyms;
	}

	public void addSynonym(Synonym synonym) {
		this.synonyms.add(synonym);
	}
		
	public void clearSynonyms() {
		this.synonyms.clear();
	}
	
	@Override
	public int compareTo(OntologySynonymSubmission o) {
		return this.getId() - o.getId();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OntologySynonymSubmission other = (OntologySynonymSubmission) obj;
		if (id != other.id)
			return false;
		return true;
	}	
	
	public boolean hasOntology() {
		return ontology != null;
	}

	public boolean hasTerm() {
		return term != null;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public Type getType() {
		return type;
	}

	public String getClassLabel() {
		return classLabel;
	}

	public boolean hasClassLabel() {
		return classLabel != null && !classLabel.isEmpty();
	}
}
