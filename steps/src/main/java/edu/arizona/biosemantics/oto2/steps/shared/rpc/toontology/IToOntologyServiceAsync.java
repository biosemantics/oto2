package edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;

public interface IToOntologyServiceAsync {

	public void getOntologies(Collection collection, AsyncCallback<List<Ontology>> callback);

	public void submitClass(OntologyClassSubmission submission, AsyncCallback<Void> callback);

	public void submitSynonym(OntologySynonymSubmission submission, AsyncCallback<Void> callback);

	public void getClassSubmissions(Collection collection, AsyncCallback<List<OntologyClassSubmission>> callback);

	public void getSynonymSubmissions(Collection collection, AsyncCallback<List<OntologySynonymSubmission>> callback);

	public void createOntology(Collection collection, Ontology ontology, AsyncCallback<Void> asyncCallback);
	
	public void refreshOntologySubmissionStatuses(Collection collection, AsyncCallback<Void> callback);
	
	public void updateOntologyClassSubmissions(Collection collection, 
			java.util.Collection<OntologyClassSubmission> ontologyClassSubmissions, 
			AsyncCallback<Void> callback);

	public void updateOntologySynonymSubmissions(Collection collection, 
			java.util.Collection<OntologySynonymSubmission> ontologySynonymSubmissions, 
			AsyncCallback<Void> callback);
	
	public void removeOntologyClassSubmissions(Collection collection, 
			java.util.Collection<OntologyClassSubmission> ontologyClassSubmissions, 
			AsyncCallback<Void> callback);
	
	public void removeOntologySynonymSubmissions(Collection collection, 
			java.util.Collection<OntologySynonymSubmission> ontologySynonymSubmissions, 
			AsyncCallback<Void> callbackw);
}
