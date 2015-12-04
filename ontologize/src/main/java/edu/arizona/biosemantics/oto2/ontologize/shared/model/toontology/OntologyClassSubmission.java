package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto2.ontologize.shared.log.TypeFromSuperclasses;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Colorable;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Commentable;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;

public class OntologyClassSubmission implements HasLabelAndIri, Serializable, Colorable, Commentable, 
		OntologySubmission, Comparable<OntologyClassSubmission> {
	
	private int id = -1;
	private int collectionId;
	private Term term;
	private String submissionTerm = "";
	private Ontology ontology;
	private String classIRI = "";
	private List<Superclass> superclasses = new LinkedList<Superclass>();
	private String definition = "";
	private List<Synonym> synonyms = new LinkedList<Synonym>();
	private String source = "";
	private String sampleSentence = "";
	private List<PartOf> partOfs = new LinkedList<PartOf>();
	private String user;
	private List<OntologyClassSubmissionStatus> submissionStatuses = new LinkedList<OntologyClassSubmissionStatus>();
	
	public OntologyClassSubmission() { }
	
	public OntologyClassSubmission(int id, int collectionId, Term term, String submissionTerm, Ontology ontology, String classIRI,
			List<Superclass> superclasses, String definition, List<Synonym> synonyms, String source, String sampleSentence, 
			List<PartOf> partOfs, String user, List<OntologyClassSubmissionStatus> submissionStatuses) { 
		this.id = id;
		this.collectionId = collectionId;
		this.term = term;
		this.submissionTerm = submissionTerm == null ? "" : submissionTerm;
		this.ontology = ontology;
		this.classIRI = classIRI == null ? "" : classIRI;
		this.superclasses = superclasses;
		this.definition = definition == null ? "" : definition;
		this.synonyms = synonyms;
		this.source = source == null ? "" : source;
		this.sampleSentence = sampleSentence == null ? "" : sampleSentence;
		this.partOfs = partOfs;
		this.user = user;
		this.submissionStatuses = submissionStatuses;
	}
	
	public OntologyClassSubmission(int collectionId, Term term, String submissionTerm, Ontology ontology, String classIRI,
			List<Superclass> superclasses, String definition, List<Synonym> synonyms, String source, String sampleSentence, 
			List<PartOf> partOfs, String user) { 
		this.collectionId = collectionId;
		this.term = term;
		this.submissionTerm = submissionTerm == null ? "" : submissionTerm;
		this.ontology = ontology;
		this.classIRI = classIRI == null ? "" : classIRI;
		this.superclasses = superclasses;
		this.definition = definition == null ? "" : definition;
		this.synonyms = synonyms;
		this.source = source == null ? "" : source;
		this.sampleSentence = sampleSentence == null ? "" : sampleSentence;
		this.partOfs = partOfs;
		this.user = user;
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

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
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

	public Type getType() {
		return TypeFromSuperclasses.getType(this.getSuperclasses());
	}
	
	public List<OntologyClassSubmissionStatus> getSubmissionStatuses() {
		return submissionStatuses;
	}

	public void setSubmissionStatuses(
			List<OntologyClassSubmissionStatus> submissionStatuses) {
		this.submissionStatuses = submissionStatuses;
	}

	public String getClassIRI() {
		return classIRI;
	}
	
	public boolean hasClassIRI() {
		return classIRI != null && !classIRI.trim().isEmpty();
	}

	public void setClassIRI(String classIRI) {
		this.classIRI = classIRI;
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

	public boolean hasPartOfs() {
		return !this.partOfs.isEmpty();
	}

	public boolean hasSuperclasses() {
		return !this.superclasses.isEmpty();
	}
	
	public List<Superclass> getSuperclasses() {
		return superclasses;
	}

	public List<Synonym> getSynonyms() {
		return synonyms;
	}

	public List<PartOf> getPartOfs() {
		return partOfs;
	}
		
	public void setSuperclasses(List<Superclass> superclasses) {
		this.superclasses = superclasses;
	}

	public void setSynonyms(List<Synonym> synonyms) {
		this.synonyms = synonyms;
	}

	public void setPartOfs(List<PartOf> partOfs) {
		this.partOfs = partOfs;
	}

	public void addSynonym(Synonym synonym) {
		this.synonyms.add(synonym);
	}
	
	public void addPartOf(PartOf partOf){
		this.partOfs.add(partOf);
	}
	
	public void addSuperclass(Superclass superclass) {
		this.superclasses.add(superclass);
	}
	
	public void clearSynonyms() {
		this.synonyms.clear();
	}
	
	public void clearPartOfs() {
		this.partOfs.clear();
	}
	
	public void clearSuperclasses() {
		this.superclasses.clear();
	}
		
	public int getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}

	@Override
	public int compareTo(OntologyClassSubmission o) {
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
		OntologyClassSubmission other = (OntologyClassSubmission) obj;
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

	@Override
	public boolean hasLabel() {
		return this.hasSubmissionTerm();
	}

	public boolean hasSubmissionTerm() {
		return this.submissionTerm != null && !this.submissionTerm.isEmpty();
	}

	@Override
	public String getLabel() {
		return this.getSubmissionTerm();
	}

	@Override
	public String getIri() {
		return this.getClassIRI();
	}

	@Override
	public boolean hasIri() {
		return this.hasClassIRI();
	}

	@Override
	public String getLabelAlternativelyIri() {
		if(hasLabel())
			return getLabel();
		if(hasIri())
			return getIri();
		return "";
	}	

}
