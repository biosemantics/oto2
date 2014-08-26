package edu.arizona.biosemantics.oto2.oto.server.db;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import edu.arizona.biosemantics.bioportal.client.BioPortalClient;
import edu.arizona.biosemantics.bioportal.model.Search;
import edu.arizona.biosemantics.bioportal.model.SearchResultPage;
import edu.arizona.biosemantics.bioportal.model.SearchResultPage.SearchResult;
import edu.arizona.biosemantics.oto2.oto.server.Configuration;
import edu.arizona.biosemantics.oto2.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyEntry;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class OntologyDAO {
	
	private BioPortalClient bioportalClient = new BioPortalClient(Configuration.bioportalUrl, Configuration.bioportalApiKey);
	private List<Ontology> lastRetrievedOntologies = new LinkedList<Ontology>();
	private Map<String, Ontology> lastRetrievedOntologiesMap = new HashMap<String, Ontology>();
	
	protected OntologyDAO() {}

	public List<OntologyEntry> get(Term term) {
		Search search = new Search();
		search.setQuery(term.getTerm());
		search.setExactMatch(true);
		search.setRequiresDefinition(true);
		search.setIncludeObsolete(false);
		return getOntologyEntries(search);
	}
	
	private List<OntologyEntry> getOntologyEntries(Search search) {
		bioportalClient.open();
		List<OntologyEntry> result = new LinkedList<OntologyEntry>();
		try {
			SearchResultPage searchResultPage = bioportalClient.searchClasses(search).get();
			addOntologyEntries(result, searchResultPage);
			int currentPage = 1;
			while(searchResultPage.getNextPage() != null && currentPage++ < searchResultPage.getPageCount()) {
				searchResultPage = bioportalClient.getSearchResultPage(searchResultPage.getNextPage()).get();
				addOntologyEntries(result, searchResultPage);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		bioportalClient.close();
		return result;
	}

	private void addOntologyEntries(List<OntologyEntry> result, SearchResultPage searchResultPage) {
		for(SearchResult searchResult : searchResultPage.getSearchResults()) {
			if(searchResult.getDefinitions() == null || searchResult.getDefinitions().isEmpty())
				result.add(new OntologyEntry(searchResult.getId() + "-server-" + result.size(), getOntologyAcronym(searchResult.getOntology()),
						searchResult.getLabel(), "", searchResult.getId()));
			else
				for(String definition : searchResult.getDefinitions()) //id's in result are not unique
					result.add(new OntologyEntry(searchResult.getId() + "-server-" + result.size(), getOntologyAcronym(searchResult.getOntology()),
							searchResult.getLabel(), definition, searchResult.getId()));
		}
	}

	private String getOntologyAcronym(String ontologyId) {
		if(this.lastRetrievedOntologiesMap.containsKey(ontologyId)) 
			return lastRetrievedOntologiesMap.get(ontologyId).getAcronym();
		return ontologyId;
	}

	public List<OntologyEntry> get(Term term, List<Ontology> ontologies) {
		Search search = new Search();
		search.setQuery(term.getTerm());
		search.setOntologies(createBioportalOntologies(ontologies));
		return getOntologyEntries(search);
	}

	private List<edu.arizona.biosemantics.bioportal.model.Ontology> createBioportalOntologies(List<Ontology> ontologies) {
		List<edu.arizona.biosemantics.bioportal.model.Ontology> result = new LinkedList<edu.arizona.biosemantics.bioportal.model.Ontology>();
		for(Ontology ontology : ontologies)
			result.add(new edu.arizona.biosemantics.bioportal.model.Ontology(ontology.getId(), ontology.getAcronym(), ontology.getName()));
		return result;
	}

	public List<Ontology> getOntologies() {
		bioportalClient.open();
		List<edu.arizona.biosemantics.bioportal.model.Ontology> ontologies = new LinkedList<edu.arizona.biosemantics.bioportal.model.Ontology>();
		try {
			ontologies = bioportalClient.getOntologies().get();
		} catch(Exception e) {
			e.printStackTrace();
		}
		bioportalClient.close();
		return createOntologies(ontologies);
	}

	private List<Ontology> createOntologies(List<edu.arizona.biosemantics.bioportal.model.Ontology> ontologies) {
		List<Ontology> result = new LinkedList<Ontology>();
		Map<String, Ontology> map = new HashMap<String, Ontology>();
		for(edu.arizona.biosemantics.bioportal.model.Ontology ontology : ontologies) {
			Ontology newOntology = new Ontology(ontology.getId(), ontology.getAcronym(), ontology.getName());
			result.add(new Ontology(ontology.getId(), ontology.getAcronym(), ontology.getName()));
			map.put(ontology.getId(), newOntology);
		}

		this.lastRetrievedOntologiesMap = map;
		this.lastRetrievedOntologies = result;
		return result;
	}
	
}
