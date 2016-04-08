package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryparser.classic.ParseException;

import edu.arizona.biosemantics.common.context.server.FileIndex;
import edu.arizona.biosemantics.common.context.shared.Context;
import edu.arizona.biosemantics.common.ling.know.SingularPluralProvider;
import edu.arizona.biosemantics.common.ling.know.lib.WordNetPOSKnowledgeBase;
import edu.arizona.biosemantics.common.ling.transform.lib.SomeInflector;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.server.Configuration;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext.Type;
import edu.stanford.nlp.util.StringUtils;

public class ContextDAO {

	private static class Search {
		private String search;
		private Type type;

		public Search(String search, Type type) {
			this.search = search;
			this.type = type;
		}

		public String getSearch() {
			return search;
		}

		public Type getType() {
			return type;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((search == null) ? 0 : search.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Search other = (Search) obj;
			if (search == null) {
				if (other.search != null)
					return false;
			} else if (!search.equals(other.search))
				return false;
			return true;
		}
	}
	
	private SingularPluralProvider singularPluralProvider = new SingularPluralProvider();
	private SomeInflector inflector;
	
	public ContextDAO() {
		try {
			WordNetPOSKnowledgeBase wordNetPOSKnowledgeBase = new WordNetPOSKnowledgeBase(Configuration.wordNetSource, false);
			inflector = new SomeInflector(wordNetPOSKnowledgeBase, singularPluralProvider.getSingulars(), singularPluralProvider.getPlurals());
		} catch (Exception e) {
			log(LogLevel.ERROR, "Could not load WordNetPOSKnowledgeBase.", e);
		}
	} 
	
	public void insert(int collectionId, List<Context> contexts) throws IOException {
		FileIndex index = new FileIndex(Configuration.context + File.separator + collectionId);
		index.open();
		for(Context context : contexts)
			index.add(context);
		index.close();
	}
	
	public void insert(int collectionId, Context context) throws IOException {
		FileIndex index = new FileIndex(Configuration.context + File.separator + collectionId);
		index.open();
		index.add(context);
		index.close();
	}

	public List<TypedContext> get(Collection collection, Term term) throws ParseException, IOException {
		Set<Search> searches = new HashSet<Search>();
		String searchTerm = term.getTerm().trim();
		String singularSearchTerm = searchTerm;
		String pluralSearchTerm = searchTerm;
		
		String originalSearchTerm = term.getOriginalTerm().trim();
		String singularOriginalSearchTerm = originalSearchTerm;
		String pluralOriginalSearchTerm = originalSearchTerm;
		
		if(inflector.isPlural(searchTerm)) {
			singularSearchTerm = inflector.getSingular(searchTerm);
		} else { 
			pluralSearchTerm = inflector.getPlural(searchTerm);
		}
		
		if(inflector.isPlural(originalSearchTerm)) {
			singularOriginalSearchTerm = inflector.getSingular(originalSearchTerm);
		} else { 
			pluralOriginalSearchTerm = inflector.getPlural(originalSearchTerm);
		}	
		
		if(term.hasChangedSpelling()) {
			searches.add(new Search(singularSearchTerm, Type.updated));
			searches.add(new Search(pluralSearchTerm, Type.updated));
			searches.add(new Search(singularSearchTerm.replaceAll(" ", "-"), Type.updated));
			searches.add(new Search(singularSearchTerm.replaceAll(" ", "_"), Type.updated));
			searches.add(new Search(singularSearchTerm.replaceAll("_", "-"), Type.updated));
			searches.add(new Search(pluralSearchTerm.replaceAll(" ", "-"), Type.updated));
			searches.add(new Search(pluralSearchTerm.replaceAll(" ", "_"), Type.updated));
			searches.add(new Search(pluralSearchTerm.replaceAll("_", "-"), Type.updated));

			searches.add(new Search(singularOriginalSearchTerm, Type.original));
			searches.add(new Search(pluralOriginalSearchTerm, Type.original));
			searches.add(new Search(singularOriginalSearchTerm.replaceAll(" ", "-"), Type.original));
			searches.add(new Search(singularOriginalSearchTerm.replaceAll(" ", "_"), Type.original));
			searches.add(new Search(singularOriginalSearchTerm.replaceAll("_", "-"), Type.original));
			searches.add(new Search(pluralOriginalSearchTerm.replaceAll(" ", "-"), Type.original));
			searches.add(new Search(pluralOriginalSearchTerm.replaceAll(" ", "_"), Type.original));
			searches.add(new Search(pluralOriginalSearchTerm.replaceAll("_", "-"), Type.original));
		} else {
			searches.add(new Search(singularSearchTerm, Type.original));
			searches.add(new Search(pluralSearchTerm, Type.original));
			searches.add(new Search(singularSearchTerm.replaceAll(" ", "-"), Type.original));
			searches.add(new Search(singularSearchTerm.replaceAll(" ", "_"), Type.original));
			searches.add(new Search(singularSearchTerm.replaceAll("_", "-"), Type.original));
			searches.add(new Search(pluralSearchTerm.replaceAll(" ", "-"), Type.original));
			searches.add(new Search(pluralSearchTerm.replaceAll(" ", "_"), Type.original));
			searches.add(new Search(pluralSearchTerm.replaceAll("_", "-"), Type.original));
		}
		
		String query = "";
		for(Search search : searches) {
			query += search.getSearch() + " OR ";
		}
		query= query.substring(0, query.length() - 4);
		FileIndex index = new FileIndex(Configuration.context + File.separator + collection.getId());
		index.open();
		List<Context> contexts = index.search(query, Configuration.contextMaxHits);
		index.close();

		List<TypedContext> typedContexts = createHighlightedAndShortenedTypedContexts(collection, contexts, searches);
		return typedContexts;
	}	

	private List<TypedContext> createHighlightedAndShortenedTypedContexts(Collection collection, List<Context> contexts, java.util.Collection<Search> searches) {
		//shorten the context to be a number of characters before and after ... some text with WORD that appears in text ...
		//could appear multiple times in text, have to split into multiple contexts
		List<TypedContext> result = new LinkedList<TypedContext>();
		
		Set<Context> contextsSet = new HashSet<Context>();
		contextsSet.addAll(contexts);
		
		for(Context context : contextsSet) {
			List<TypedContext> contextResult = new LinkedList<TypedContext>();
			for(Search search : searches) {
				Pattern pattern = Pattern.compile("\\b(?i)" + search.getSearch() + "\\b");
				result.addAll(extract(collection, pattern, search.getSearch(), context, search.getType()));
			}
			
			/*if(contextResult.isEmpty()) {
				//String fullText = context.getText().replaceAll("(?i)" + search.getSearch(), "<b>" + search.getSearch() + "</b>").replaceAll("\n", "</br>");
				String fullText = context.getText();
				
				String idString = String.valueOf(context.getId()) + "-" + type.toString() + "-" + id++;
				TypedContext typedContext = new TypedContext(idString, context.getCollectionId(), context.getSource(), fullText, fullText, type);
		    	result.add(typedContext);
			}*/
			result.addAll(contextResult);
		}
		
		return result;
	}

	private List<TypedContext> extract(Collection collection, Pattern pattern, String replaceTerm, Context context, Type type) {	
		List<TypedContext> result = new LinkedList<TypedContext>();
		Matcher matcher = pattern.matcher(context.getText());
		
		int id = 0;
		if(matcher.find()) {
			matcher.reset();
		    while (matcher.find()) {
		    	int startText = matcher.start() - 100;
		    	int endText = matcher.end() + 100;
		    	if(startText < 0)
		    		startText = 0;
		    	if(endText > context.getText().length())
		    		endText = context.getText().length();
		    	String extractedText = "..." + context.getText().substring(startText, endText) + "...";
		    	//extractedText = extractedText.replaceAll(pattern.toString(), "<b>" + replaceTerm + "</b>");
		    	extractedText = extractedText.replaceAll("(?i)" + replaceTerm, "<b>" + replaceTerm + "</b>");
		    	String fullText = context.getText().replaceAll("(?i)" + replaceTerm, "<b>" + replaceTerm + "</b>").replaceAll("\n", "</br>");
		    	
		    	String idString = String.valueOf(context.getId()) + "-" + type.toString() + "-" + id++;
		    	TypedContext typedContext = new TypedContext(idString, collection.getId(), context.getSource(), extractedText, fullText, type);
		    	result.add(typedContext);
		    }
		}
	    return result;
	}

}