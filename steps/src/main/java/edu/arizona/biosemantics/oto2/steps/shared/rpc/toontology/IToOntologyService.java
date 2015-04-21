package edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology;

import java.util.List;

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
	
	public void submitClass(OntologyClassSubmission submission) throws OntologyFileException, ClassExistsException;

	public void submitSynonym(OntologySynonymSubmission submission) throws OntologyFileException;
	
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection);

	public List<OntologySynonymSubmission> getSynonymSubmissions(Collection collection);
	
	public void createOntology(Collection collection, Ontology ontology) throws OntologyFileException;
	
}
