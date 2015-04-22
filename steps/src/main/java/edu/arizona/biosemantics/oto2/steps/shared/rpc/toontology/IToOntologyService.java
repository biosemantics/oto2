package edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;

@RemoteServiceRelativePath("toOntology")
public interface IToOntologyService extends RemoteService {
	
	public List<Ontology> getOntologies(Collection collection);
	
	public void submitClass(OntologyClassSubmission submission) throws OntologyFileException, ClassExistsException, InterruptedException, ExecutionException;

	public void submitSynonym(OntologySynonymSubmission submission) throws OntologyFileException, InterruptedException, ExecutionException;
	
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection);

	public List<OntologySynonymSubmission> getSynonymSubmissions(Collection collection);
	
	public void createOntology(Collection collection, Ontology ontology) throws OntologyFileException;
	
	public void refreshOntologySubmissionStatuses(Collection collection);
	
	public void updatehOntologyClassSubmission(Collection collection, OntologyClassSubmission ontologyClassSubmission);

	public void updatehOntologySynonymSubmission(Collection collection, OntologySynonymSubmission ontologySynonymSubmission);
	
	public void removeOntologyClassSubmission(Collection collection, OntologyClassSubmission ontologyClassSubmission);
	
	public void removeOntologySynonymSubmission(Collection collection, OntologySynonymSubmission ontologySynonymSubmission);
}
