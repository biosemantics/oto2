package edu.arizona.biosemantics.oto2.ontologize.server.rpc;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.ontology.search.FileSearcher;
import edu.arizona.biosemantics.common.ontology.search.Searcher;
import edu.arizona.biosemantics.common.ontology.search.TaxonGroupOntology;
import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry;
import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry.Type;
import edu.arizona.biosemantics.oto2.ontologize.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.DAOManager;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.StatusEnum;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionService;

public class CollectionService extends RemoteServiceServlet implements ICollectionService {

	private DAOManager daoManager = new DAOManager();
	
	@Override
	public Collection insert(Collection collection) throws Exception {
		try {
			return daoManager.getCollectionDAO().insert(collection);
		} catch (QueryException e) {
			throw new Exception();
		}
	}

	@Override
	public Collection get(int id, String secret) throws Exception {
		try {
			if(daoManager.getCollectionDAO().isValidSecret(id, secret)) {
				Collection collection = daoManager.getCollectionDAO().get(id);
				
				//TODO: IS SLOW
				//collection.setTermExistingIRIMap(getExistingIRI(collection.getTerms()));
				return collection;
			}
			return null;
		} catch (QueryException e) {
			throw new Exception();
		}
	}

	private Map<Term, List<String>> getExistingIRI(List<Term> terms) throws QueryException, IOException {
		Map<Term, List<String>> existingIRIs = new HashMap<Term, List<String>>();
		for(Term term : terms) {
			if(!existingIRIs.containsKey(term))
				existingIRIs.put(term, new LinkedList<String>());
			Collection collection = daoManager.getCollectionDAO().get(term.getCollectionId());
						
			//if(term.getTerm().equals("absent")) {
			existingIRIs.get(term).addAll(getPermanentOntologyIRIs(term, collection));
			existingIRIs.get(term).addAll(getBioportalAndLocalOntologyIRIs(term, collection));
			//}
		}
		return existingIRIs;
	}
	
	private List<String> getBioportalAndLocalOntologyIRIs(Term term, Collection collection) throws QueryException {
		List<String> iris = new LinkedList<String>();
		List<OntologyClassSubmission> classSubmissions = daoManager.getOntologyClassSubmissionDAO().get(collection, StatusEnum.ACCEPTED, term.getTerm());
		for(OntologyClassSubmission classSubmission : classSubmissions) 
			iris.add(classSubmission.getClassIRI());
		List<OntologySynonymSubmission> synonymSubmissions = daoManager.getOntologySynonymSubmissionDAO().get(collection, StatusEnum.ACCEPTED, term.getTerm());
		for(OntologySynonymSubmission synonymSubmission : synonymSubmissions) 
			iris.add(synonymSubmission.getClassIRI());
		classSubmissions = daoManager.getOntologyClassSubmissionDAO().get(collection, StatusEnum.PENDING, term.getTerm());
		for(OntologyClassSubmission classSubmission : classSubmissions) 
			iris.add(classSubmission.getClassIRI());;
		synonymSubmissions = daoManager.getOntologySynonymSubmissionDAO().get(collection, StatusEnum.PENDING, term.getTerm());
		for(OntologySynonymSubmission synonymSubmission : synonymSubmissions) 
			iris.add(synonymSubmission.getClassIRI());
		return iris;
	}

	private List<String> getPermanentOntologyIRIs(Term term, Collection collection) {
		List<String> iris = new LinkedList<String>();
		
		LinkedList<Searcher> permanentOntologySearchers = new LinkedList<Searcher>();
		for(edu.arizona.biosemantics.common.ontology.search.model.Ontology ontology : TaxonGroupOntology.getOntologies(collection.getTaxonGroup())) 
			permanentOntologySearchers.add(new FileSearcher(ontology, Configuration.permanentOntologyDirectory, Configuration.wordNetSource));
		
		for(Searcher searcher : permanentOntologySearchers) {
			Type type = Type.QUALITY;
			if(term.getBuckets().contains("structure"))
				type = Type.ENTITY;
			if(term.getBuckets().contains("character"))
				type = Type.QUALITY;
						
			List<OntologyEntry> ontologyEntries = searcher.getEntries(term.getTerm(), type);
			if(!ontologyEntries.isEmpty()) {
				log(LogLevel.DEBUG, "Highest scored ontology entity" + ontologyEntries.get(0).getScore());
				if(ontologyEntries.get(0).getScore() == 1.0) {
					iris.add(ontologyEntries.get(0).getClassIRI());
				}
			}
		}
		
		return iris;
	}

	@Override
	public void update(Collection collection) throws Exception {
		try {
			daoManager.getCollectionDAO().update(collection);
		} catch (QueryException e) {
			throw new Exception();
		}
	}
	
	private List<Ontology> getLocalOntologiesForCollection(int collectionId, String secret) throws Exception {
		return daoManager.getOntologyDBDAO().getLocalOntologiesForCollection(daoManager.getCollectionDAO().get(collectionId));
	}
	
	private void insertLocalOntologiesForCollection(int collectionId, String secret, List<Ontology> ontologies) throws Exception {
		daoManager.getOntologyDBDAO().insertLocalOntologiesForCollection(collectionId, ontologies);
	}

}
