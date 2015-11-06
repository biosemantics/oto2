package edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;

public interface IToOntologyServiceAsync {

	public void getOntologies(Collection collection, AsyncCallback<List<Ontology>> callback);

	public void createClassSubmission(Collection collection, OntologyClassSubmission submission, AsyncCallback<List<OntologyClassSubmission>> callback);

	public void createSynonymSubmission(Collection collection, OntologySynonymSubmission submission, AsyncCallback<OntologySynonymSubmission> callback);

	public void getClassSubmissions(Collection collection, AsyncCallback<List<OntologyClassSubmission>> callback);

	public void getSynonymSubmissions(Collection collection, AsyncCallback<List<OntologySynonymSubmission>> callback);

	public void createOntology(Collection collection, Ontology ontology, boolean createFile, AsyncCallback<Ontology> callback);
	
	public void refreshSubmissionStatuses(Collection collection, AsyncCallback<Void> callback);
	
	public void updateClassSubmission(Collection collection, OntologyClassSubmission ontologyClassSubmissions, AsyncCallback<Void> callback);

	public void updateSynonymSubmission(Collection collection, OntologySynonymSubmission ontologySynonymSubmissions, AsyncCallback<Void> callback);
	
	public void removeClassSubmission(Collection collection, OntologyClassSubmission submission, AsyncCallback<Void> callback);

	public void removeSynonymSubmission(Collection collection, OntologySynonymSubmission submission, AsyncCallback<Void> callback);
	
	public void getPermanentOntologies(Collection collection, AsyncCallback<List<Ontology>> callback);
	
	public void getLocalOntologies(Collection collection, AsyncCallback<List<Ontology>> callback);

	public void getClassSubmissions(Collection collection, Ontology ontology,
			AsyncCallback<List<OntologyClassSubmission>> callback);

	public void getClassSubmissions(Collection collection, java.util.Collection<Ontology> ontologies, 
			AsyncCallback<List<OntologyClassSubmission>> callback);

	public void getClassLabel(Collection collection, String iri, AsyncCallback<String> callback);

}
