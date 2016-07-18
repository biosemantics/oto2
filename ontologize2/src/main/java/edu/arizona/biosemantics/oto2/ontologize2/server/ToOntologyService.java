package edu.arizona.biosemantics.oto2.ontologize2.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.oto2.ontologize2.server.persist.OntologyFileDAO;
import edu.arizona.biosemantics.oto2.ontologize2.shared.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology.OntologySynonymSubmission;

public class ToOntologyService extends RemoteServiceServlet implements IToOntologyService {
	
	@Override
	public void storeLocalOntologiesToFile(Collection collection) throws Exception {
		OntologyFileDAO ontologyFileDAO = new OntologyFileDAO(collection);
		
		//classes
		List<OntologyClassSubmission> classSubmissions = ontologyFileDAO.getClassSubmission(collection);
		for(OntologyClassSubmission classSubmission : classSubmissions) {
			//if(!classSubmission.getOntology().isBioportalOntology()) {
				String classIRI = ontologyFileDAO.insertClassSubmission(classSubmission);
			//}
		}
		
		//synonyms
		List<OntologySynonymSubmission> synonymSubmissions = ontologyFileDAO.getOntologySynonymSubmission(collection);
		for(OntologySynonymSubmission synonymSubmission : synonymSubmissions) {
			//if(!synonymSubmission.getOntology().isBioportalOntology()) {
				String classIRI = ontologyFileDAO.insertSynonymSubmission(synonymSubmission);
			//}
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		ToOntologyService service = new ToOntologyService();
		CollectionService collectionService = new CollectionService();
		Collection defaultCollection = collectionService.get(0, "secret");
		service.storeLocalOntologiesToFile(defaultCollection);
	}
}
