package edu.arizona.biosemantics.oto2.oto.server.db;

import java.util.LinkedList;
import java.util.List;
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
	
	protected OntologyDAO() {}

	public List<OntologyEntry> get(Term term) throws InterruptedException, ExecutionException {
		Search search = new Search();
		search.setQuery(term.getTerm());
		search.setExactMatch(true);
		search.setRequiresDefinition(true);
		search.setIncludeObsolete(false);
		return getOntologyEntries(search);
	}
	
	private List<OntologyEntry> getOntologyEntries(Search search) throws InterruptedException, ExecutionException {
		bioportalClient.open();
		SearchResultPage searchResultPage = bioportalClient.searchClasses(search).get();
		List<OntologyEntry> result = new LinkedList<OntologyEntry>();
		addOntologyEntries(result, searchResultPage);
		int currentPage = 1;
		while(searchResultPage.getNextPage() != null && currentPage++ < searchResultPage.getPageCount()) {
			searchResultPage = bioportalClient.getSearchResultPage(searchResultPage.getNextPage()).get();
			addOntologyEntries(result, searchResultPage);
		}
		bioportalClient.close();
		return result;
	}

	private void addOntologyEntries(List<OntologyEntry> result, SearchResultPage searchResultPage) {
		for(SearchResult searchResult : searchResultPage.getSearchResults()) {
			if(searchResult.getDefinitions() == null || searchResult.getDefinitions().isEmpty())
				result.add(new OntologyEntry(searchResult.getId() + "-server-" + result.size(), searchResult.getLabel(), "", searchResult.getId()));
			else
				for(String definition : searchResult.getDefinitions()) //id's in result are not unique
					result.add(new OntologyEntry(searchResult.getId() + "-server-" + result.size(), searchResult.getLabel(), definition, searchResult.getId()));
		}
	}

	public List<OntologyEntry> get(Term term, List<Ontology> ontologies) throws InterruptedException, ExecutionException {
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

	public List<Ontology> getOntologies() throws InterruptedException, ExecutionException {
		bioportalClient.open();
		List<edu.arizona.biosemantics.bioportal.model.Ontology> ontologies = bioportalClient.getOntologies().get();
		bioportalClient.close();
		return createOntologies(ontologies);
	}

	private List<Ontology> createOntologies(List<edu.arizona.biosemantics.bioportal.model.Ontology> ontologies) {
		List<Ontology> result = new LinkedList<Ontology>();
		for(edu.arizona.biosemantics.bioportal.model.Ontology ontology : ontologies)
			result.add(new Ontology(ontology.getId(), ontology.getAcronym(), ontology.getName()));
		return result;
	}
	
}
