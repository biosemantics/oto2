package edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SubmissionType;

public interface IToOntologyServiceAsync {

	public void getOntologies(Collection collection, AsyncCallback<List<Ontology>> callback);

	public void createClassSubmission(Collection collection, OntologyClassSubmission submission, AsyncCallback<List<OntologyClassSubmission>> callback);

	public void createSynonymSubmission(Collection collection, OntologySynonymSubmission submission, AsyncCallback<OntologySynonymSubmission> callback);

	public void getClassSubmissions(Collection collection, AsyncCallback<List<OntologyClassSubmission>> callback);

	public void getSynonymSubmissions(Collection collection, AsyncCallback<List<OntologySynonymSubmission>> callback);

	public void createOntology(Collection collection, Ontology ontology, boolean createFile, AsyncCallback<Ontology> callback);
	
	public void refreshSubmissionStatuses(Collection collection, AsyncCallback<Void> callback);
	
	public void updateClassSubmission(Collection collection, OntologyClassSubmission ontologyClassSubmissions, AsyncCallback<List<OntologyClassSubmission>> callback);

	public void updateSynonymSubmission(Collection collection, OntologySynonymSubmission ontologySynonymSubmissions, AsyncCallback<OntologySynonymSubmission> callback);
	
	public void removeClassSubmission(Collection collection, OntologyClassSubmission submission, AsyncCallback<Void> callback);

	public void removeSynonymSubmission(Collection collection, OntologySynonymSubmission submission, AsyncCallback<Void> callback);
	
	public void getPermanentOntologies(Collection collection, AsyncCallback<List<Ontology>> callback);
	
	public void getLocalOntologies(Collection collection, AsyncCallback<List<Ontology>> callback);

	public void getClassSubmissions(Collection collection, Ontology ontology,
			AsyncCallback<List<OntologyClassSubmission>> callback);

	public void getClassSubmissions(Collection collection, java.util.Collection<Ontology> ontologies, 
			AsyncCallback<List<OntologyClassSubmission>> callback);

	public void getClassLabel(Collection collection, String iri, AsyncCallback<String> callback);
	
	public void sendBioportalSubmissions(Collection collection, AsyncCallback<Void> callback);

	public void storeLocalOntologiesToFile(Collection collection, AsyncCallback<Void> callback);
	
	public void isSupportedIRI(Collection collection, String iri, AsyncCallback<Boolean> callback);

	public void getClassSubmissions(Collection collection, FilterPagingLoadConfig loadConfig, SubmissionType submissionType, AsyncCallback<PagingLoadResult<OntologyClassSubmission>> callback);

	public void getSynonymSubmissions(Collection collection, FilterPagingLoadConfig loadConfig, SubmissionType submissionType, AsyncCallback<PagingLoadResult<OntologySynonymSubmission>> callback);

}
