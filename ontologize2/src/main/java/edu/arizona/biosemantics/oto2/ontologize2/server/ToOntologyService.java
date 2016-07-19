package edu.arizona.biosemantics.oto2.ontologize2.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.oto2.ontologize2.server.persist.OntologyFileDAO;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology.Synonym;

public class ToOntologyService extends RemoteServiceServlet implements IToOntologyService {
	
	private ICollectionService collectionService = new CollectionService();
	
	/**
	 * covert the collection into a list of OntologyClassSubmissions
	 * 
	 * @param collection
	 * @return
	 * @throws Exception 
	 */
	public List<OntologyClassSubmission> getClassSubmission(
			Collection collection, Ontology ontology) throws Exception {
		//1, classes to OntologyClass
		Map<String, List<String>> subclasses = collection.getSubclasses();
		Set<String> classNames = extractDistinctItems(subclasses);
		
		List<OntologyClassSubmission> classSubmissions = new ArrayList();
		
		int classId = 1;
		//2, get other information about an ontology class
		for(String className : classNames){
			
			Term term = collection.getTerm(className);
			String submissionTerm = className;
			String classIRI = null;
			
			List<Term> termSuperclasses = collectionService.getSuperclasses(collection.getId(), collection.getSecret(), term);
			List<Superclass> superclasses = new LinkedList();
			for(Term superclass : termSuperclasses){//convert to superclass, get superclass IRI?
				superclasses.add(new Superclass());
			}
			
			//TODO: get synonyms or get preferred Names?
			List<Term> termSynonyms = collectionService.getSynonyms(collection.getId(), collection.getSecret(), term);
			List<Synonym> synonyms = new LinkedList<Synonym>();
			for(Term synonym:termSynonyms){
				synonyms.add(new Synonym());
			}
			//private String source = "";
			//private String sampleSentence = "";
			//TODO: get parents or get partofterms of this term?
			List<Term> termPartOf = collectionService.getParents(collection.getId(), collection.getSecret(), term);
			List<PartOf> partOfs = new LinkedList<PartOf>();
			
			OntologyClassSubmission classSubmission = new OntologyClassSubmission(term, submissionTerm, ontology, classIRI, superclasses, synonyms, partOfs);
			classSubmissions.add(classSubmission);
			classSubmission.setClassIRI(createClassIRI(classSubmission, classId++));
			
		}
		
		return classSubmissions;
	}


	public List<OntologySynonymSubmission> getOntologySynonymSubmission(
			Collection collection, Ontology ontology) {
		//1, classes to OntologyClass
		Map<String, List<String>> synonyms = collection.getSynonyms();
		
		Set<String> synonymNames = extractDistinctItems(synonyms);
		
		List<OntologySynonymSubmission> synonymSubmissions = new ArrayList();
		
		for(String synonymName : synonymNames){
			
		}
		return synonymSubmissions;
	}
	
	
	/**
	 * get all the classes,
	 * use the subclass relationships rather than the terms, because some terms could not appear in the final ontology?
	 * @return
	 */
	public Set<String> extractDistinctItems(Map<String, List<String>> itemMap) {
		Set<String> disambiguatedTermNames = new HashSet<String>();
		Set<String> parentClassNames = itemMap.keySet();
		for(String parentClass: parentClassNames){
			disambiguatedTermNames.add(parentClass);
			List<String> children = itemMap.get(parentClass);
			for(String child : children){
				disambiguatedTermNames.add(child);
			}
		}
		return disambiguatedTermNames;
	}
	
	@Override
	public void storeLocalOntologiesToFile(Collection collection) throws Exception {
		OntologyFileDAO ontologyFileDAO = new OntologyFileDAO(collection);
		
		Ontology ontology = new Ontology();
		String etcNamespacePrefix = "http://www.etc-project.org/owl/ontologies/";
		ontology.setIri(Configuration.etcOntologyBaseIRI+"/Plant.owl");
		ontology.setName("Plant");
		ontology.setAcronym("Plant");
		ontology.setCreatedInCollectionId(0);
		
		ontologyFileDAO.insertOntology(ontology);
		
		
		//classes
		List<OntologyClassSubmission> classSubmissions = getClassSubmission(collection,ontology);
		for(OntologyClassSubmission classSubmission : classSubmissions) {
			//if(!classSubmission.getOntology().isBioportalOntology()) {
				String classIRI = ontologyFileDAO.insertClassSubmission(classSubmission);
			//}
		}
		
		//synonyms
		List<OntologySynonymSubmission> synonymSubmissions = getOntologySynonymSubmission(collection,ontology);
		for(OntologySynonymSubmission synonymSubmission : synonymSubmissions) {
			//if(!synonymSubmission.getOntology().isBioportalOntology()) {
				String classIRI = ontologyFileDAO.insertSynonymSubmission(synonymSubmission);
			//}
		}
	}
	
	
	public String createClassIRI(OntologyClassSubmission ontologyClassSubmission, int submissionIdInCollection) {
		if(ontologyClassSubmission.hasClassIRI())
			return ontologyClassSubmission.getClassIRI();
		return Configuration.etcOntologyBaseIRI + ontologyClassSubmission.getOntology().getAcronym() + "#" + submissionIdInCollection;
	}
	
	public static void main(String[] args) throws Exception {
		ToOntologyService service = new ToOntologyService();
		CollectionService collectionService = new CollectionService();
		Collection defaultCollection = collectionService.get(0, "secret");
		service.storeLocalOntologiesToFile(defaultCollection);
	}
}
