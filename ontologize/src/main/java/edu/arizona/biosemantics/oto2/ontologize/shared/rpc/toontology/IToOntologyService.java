package edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;

@RemoteServiceRelativePath("ontologize_toOntology")
public interface IToOntologyService extends RemoteService {
	
	public List<Ontology> getOntologies(Collection collection) throws Exception;
	
	public List<OntologyClassSubmission> createClassSubmission(Collection collection, OntologyClassSubmission submission) throws Exception;

	public OntologySynonymSubmission createSynonymSubmission(Collection collection, OntologySynonymSubmission submission) throws Exception;
	
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection) throws  Exception;

	public List<OntologySynonymSubmission> getSynonymSubmissions(Collection collection) throws Exception;
	
	public Ontology createOntology(Collection collection, Ontology ontology, boolean createFile) throws Exception;
	
	public void refreshSubmissionStatuses(Collection collection) throws Exception;
	
	public List<OntologyClassSubmission> updateClassSubmission(Collection collection, OntologyClassSubmission ontologyClassSubmissions) throws Exception;

	public void updateSynonymSubmission(Collection collection, OntologySynonymSubmission ontologySynonymSubmissions) throws Exception;
	
	public void removeClassSubmission(Collection collection, OntologyClassSubmission submission) throws Exception;

	public void removeSynonymSubmission(Collection collection, OntologySynonymSubmission submission) throws Exception;

	public List<Ontology> getPermanentOntologies(Collection collection) throws Exception;

	public List<Ontology> getLocalOntologies(Collection collection) throws Exception;
	
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection, Ontology ontology) throws Exception;
	
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection, java.util.Collection<Ontology> ontologies) throws Exception;

	public String getClassLabel(Collection collection, String iri) throws Exception;
}
