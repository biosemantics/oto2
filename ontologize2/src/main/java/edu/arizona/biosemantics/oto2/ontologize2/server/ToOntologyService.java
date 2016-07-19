package edu.arizona.biosemantics.oto2.ontologize2.server;

import java.util.ArrayList;
import java.util.HashMap;
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
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology.Synonym;

public class ToOntologyService extends RemoteServiceServlet implements IToOntologyService {
	
	private ICollectionService collectionService = new CollectionService();
	
	private Map<String, OntologyClassSubmission> classSubmissionMap = new HashMap();
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
		
		Map<String, Integer> classIRIIDMap = generateClassIRIID(classNames);
		
		//2, get other information about an ontology class
		for(String className : classNames){
			
			Term term = collection.getTerm(className);
			String submissionTerm = className;
			String classIRI = null;
			
			//TODO: get synonyms or get preferred Names?
			//TODO: check whether getPreferredTerms works wrong
			List<Term> termSynonyms = collection.getSynonyms(term);
			List<Synonym> synonyms = new LinkedList<Synonym>();
			for(Term synonym:termSynonyms){
				synonyms.add(new Synonym(synonym.getDisambiguatedValue()));
			}
			//private String source = "";
			//private String sampleSentence = "";
			
			OntologyClassSubmission classSubmission = new OntologyClassSubmission(term, submissionTerm, ontology, classIRI, null, synonyms, null);
			classSubmissions.add(classSubmission);
			classSubmission.setClassIRI(createClassIRI(classSubmission, classIRIIDMap.get(className)));
			
			classSubmissionMap.put(className, classSubmission);
		}
		
		
		//attach parents && partof
		for(String className : classNames){
			OntologyClassSubmission classSubmission =  classSubmissionMap.get(className);
			Term term = collection.getTerm(className);
			List<Term> termSuperclasses = collectionService.getSuperclasses(collection.getId(), collection.getSecret(), term);
			
			List<Superclass> superclasses = new LinkedList();
			for(Term superclass : termSuperclasses){//convert to superclass, get superclass IRI?
				OntologyClassSubmission superClassSubmission = classSubmissionMap.get(superclass.getDisambiguatedValue());
				superclasses.add(new Superclass(superClassSubmission));
			}
			classSubmission.setSuperclasses(superclasses);
			
			//a kind of subclass, Part of: http://purl.obolibrary.org/obo/BFO_0000050
			List<Term> termPartOf = collectionService.getParents(collection.getId(), collection.getSecret(), term);
			List<PartOf> partOfs = new LinkedList<PartOf>();
			for(Term partOf:termPartOf){
				OntologyClassSubmission superClassSubmission = classSubmissionMap.get(partOf.getDisambiguatedValue());
				partOfs.add(new PartOf(superClassSubmission));
			}
			classSubmission.setPartOfs(partOfs);
		}
		
		return classSubmissions;
	}

	/**
	 * generate IDS for all the classes
	 * will IDs change in each export process?
	 * 
	 * @param classNames
	 * @return
	 */
	public Map<String, Integer> generateClassIRIID(Set<String> classNames) {
		Map<String, Integer> classIRIIDMap = new HashMap();
		int classId = 1;
		for(String className : classNames){
			classIRIIDMap.put(className, classId++);
		}
		return classIRIIDMap;
	}


	public List<OntologySynonymSubmission> getOntologySynonymSubmission(
			Collection collection, Ontology ontology) {
		//1, classes to OntologyClass
		Map<String, List<String>> synonyms = collection.getSynonyms();
		
		List<OntologySynonymSubmission> synonymSubmissions = new ArrayList();
		
		Set<String> keys = synonyms.keySet();
		for(String keyName : keys){
			Term term = collection.getTerm(keyName);
			OntologyClassSubmission classSubmission = classSubmissionMap.get(keyName);
			String submissionTerm = keyName;
			String classIRI = classSubmission.getClassIRI();
			String classLabel = classSubmission.getLabel();
			
			List<String> synonymOfTerm = synonyms.get(keyName);
			
			List<Synonym> synonymList = new LinkedList<Synonym>();
			for(String synonymStr:synonymOfTerm){
				synonymList.add(new Synonym(synonymStr));
			}
			
			//Term term, String submissionTerm, Ontology ontology, String classIRI, String classLabel, List<Synonym> synonyms
			OntologySynonymSubmission oss = new OntologySynonymSubmission(term, submissionTerm, ontology, classIRI, classLabel, synonymList);
			synonymSubmissions.add(oss);
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
