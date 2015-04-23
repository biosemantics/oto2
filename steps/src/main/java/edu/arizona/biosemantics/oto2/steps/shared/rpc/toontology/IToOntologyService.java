package edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;

@RemoteServiceRelativePath("toOntology")
public interface IToOntologyService extends RemoteService {
	
	public List<Ontology> getOntologies(Collection collection);
	
	public void submitClass(OntologyClassSubmission submission) throws OntologyFileException, ClassExistsException, OntologyBioportalException;

	public void submitSynonym(OntologySynonymSubmission submission) throws OntologyFileException, OntologyBioportalException;
	
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection);

	public List<OntologySynonymSubmission> getSynonymSubmissions(Collection collection);
	
	public void createOntology(Collection collection, Ontology ontology) throws OntologyFileException, OntologyExistsException;
	
	public void refreshOntologySubmissionStatuses(Collection collection);
	
	public void updateOntologyClassSubmissions(Collection collection, 
			java.util.Collection<OntologyClassSubmission> ontologyClassSubmissions);

	public void updateOntologySynonymSubmissions(Collection collection, 
			java.util.Collection<OntologySynonymSubmission> ontologySynonymSubmissions);
	
	public void removeOntologyClassSubmissions(Collection collection, 
			java.util.Collection<OntologyClassSubmission> ontologyClassSubmissions);
	
	public void removeOntologySynonymSubmissions(Collection collection, 
			java.util.Collection<OntologySynonymSubmission> ontologySynonymSubmissions);
}
