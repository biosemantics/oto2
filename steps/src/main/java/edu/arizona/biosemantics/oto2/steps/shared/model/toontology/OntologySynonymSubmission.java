package edu.arizona.biosemantics.oto2.steps.shared.model.toontology;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.Term;

public class OntologySynonymSubmission implements Serializable, HasOntology, EntityQualityClass {
	
	private int id = -1;
	private Term term;
	private String submissionTerm = "";
	private Ontology ontology;
	private String classIRI = "";
	private String synonyms = "";
	private String source = "";
	private String sampleSentence = "";
	private boolean entity;
	private boolean quality;
	private List<OntologySynonymSubmissionStatus> submissionStatuses = new LinkedList<OntologySynonymSubmissionStatus>();

	public OntologySynonymSubmission() { }
	
	public OntologySynonymSubmission(int id, Term term, String submissionTerm, Ontology ontology, 
			String classIRI, String synonyms, String source, String sampleSentence, boolean entity, boolean quality, 
			List<OntologySynonymSubmissionStatus> submissionStatuses) { 
		this.id = id;
		this.term = term;
		this.submissionTerm = submissionTerm;
		this.ontology = ontology;
		this.classIRI = classIRI;
		this.synonyms = synonyms;
		this.source = source;
		this.sampleSentence = sampleSentence;
		this.entity = entity;
		this.quality = quality;
		this.submissionStatuses = submissionStatuses;
	}
	
	public OntologySynonymSubmission(Term term, String submissionTerm, Ontology ontology, 
			String classIRI, String synonyms, String source, String sampleSentence, boolean entity, boolean quality) { 
		this.term = term;
		this.submissionTerm = submissionTerm;
		this.ontology = ontology;
		this.classIRI = classIRI;
		this.synonyms = synonyms;
		this.source = source;
		this.sampleSentence = sampleSentence;
		this.entity = entity;
		this.quality = quality;
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

	public String getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(String synonyms) {
		this.synonyms = synonyms;
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
		return this.synonyms != null && !this.synonyms.trim().isEmpty();
	}

	public boolean hasClassIRI() {
		return this.classIRI != null && !this.getClassIRI().trim().isEmpty();
	}

	@Override
	public boolean isEntity() {
		return this.entity;
	}

	@Override
	public boolean isQuality() {
		return this.quality;
	}

	public void setEntity(boolean entity) {
		this.entity = entity;
	}

	public void setQuality(boolean quality) {
		this.quality = quality;
	}	
}
