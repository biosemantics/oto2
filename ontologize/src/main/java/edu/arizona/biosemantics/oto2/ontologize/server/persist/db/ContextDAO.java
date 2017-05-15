package edu.arizona.biosemantics.oto2.ontologize.server.persist.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.arizona.biosemantics.common.ling.know.IPOSKnowledgeBase;
import edu.arizona.biosemantics.common.ling.know.SingularPluralProvider;
import edu.arizona.biosemantics.common.ling.know.lib.WordNetPOSKnowledgeBase;
import edu.arizona.biosemantics.common.ling.transform.IInflector;
import edu.arizona.biosemantics.common.ling.transform.lib.SomeInflector;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Context;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.TypedContext;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.TypedContext.Type;

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
	
	public Context get(int id)  {
		Context context = null;
		try(Query query = new Query("SELECT * FROM ontologize_context WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				context = createContext(result);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return context;
	}
	
	public List<Context> get(Collection collection)  {
		List<Context> contexts = new LinkedList<Context>();
		try(Query query = new Query("SELECT * FROM ontologize_context WHERE collectionId = ?")) {
			query.setParameter(1, collection.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				Context context = createContext(result);
				contexts.add(context);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return contexts;
	}
	
	private Context createContext(ResultSet result) throws SQLException  {
		int id = result.getInt(1);
		int collectionId = result.getInt(2);
		String source = result.getString(3);
		String sentence = result.getString(4);
		return new Context(id, collectionId, source, sentence);
	}

	public Context insert(Context context)  {
		if(context.hasId()) 
			this.remove(context);
		try(Query insert = new Query("INSERT INTO `ontologize_context` " +
				"(`collection`, `source`, `text`) VALUES (?, ?, ?)")) {
			insert.setParameter(1, context.getCollectionId());
			insert.setParameter(2, context.getSource().trim());
			insert.setParameter(3, context.getText().trim());
			insert.execute();
			ResultSet generatedKeys = insert.getGeneratedKeys();
			generatedKeys.next();
			int id = generatedKeys.getInt(1);
			context.setId(id);
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return context;
	}
	
	public void update(Context context)  {
		try(Query query = new Query("UPDATE ontologize_context "
				+ "SET collectionId = ?, source = ?, text = ? WHERE id = ?")) {
			query.setParameter(1, context.getCollectionId());
			query.setParameter(2, context.getSource());
			query.setParameter(3, context.getText());
			query.setParameter(4, context.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}
	
	public void remove(Context context)  {
		try(Query query = new Query("DELETE FROM ontologize_context WHERE id = ?")) {
			query.setParameter(1, context.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}	
	
	public void remove(int collectionId)  {
		try(Query query = new Query("DELETE FROM ontologize_context WHERE collectionId = ?")) {
			query.setParameter(1, collectionId);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}
	
	//http://stackoverflow.com/questions/2839441/mysql-query-problem
	//don't want to use LIKE %term% because it will be slow... so just approximate
	public List<TypedContext> get(Collection collection, Term term)  {
		List<Context> contexts = new LinkedList<Context>();
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
		/* Method utilizing sql natural language search
		 * Advantage: Faster than regular expression search for all context entries
		 * Disadvantages:
		 * Per default, the minimum word size that is indexed is 4. Because of this I noticed that for example "m" or "cm" are not found.
		 * Also there is a default stopword list which stops indexing the words listed here
		 * http://dev.mysql.com/doc/refman/5.5/en/fulltext-stopwords.html
		 * Both can be customized, however it has to be customized for the mysql installation, and wouldn't be part of the application source
		 */
		 /* for(Search search : searches) 
			try(Query query = new Query("SELECT * FROM ontologize_context WHERE collection = ? AND MATCH (text) AGAINST (? IN NATURAL LANGUAGE MODE)")) {
				query.setParameter(1, collection.getId());
				query.setParameter(2, search.getSearch());	
				ResultSet result = query.execute();
				while(result.next()) {
					Context context = createContext(result);
					contexts.add(context);
				}
			} catch(Exception e) {
				log(LogLevel.ERROR, "Query Exception", e);
			}
		*/
		
		Set<Integer> foundIds = new HashSet<Integer>();
		String whereClause = "";
		for(Search search : searches) {
			whereClause += "text RLIKE ? OR ";
		}
		whereClause = whereClause.substring(0, whereClause.length() - 4);
		
		try(Query query = new Query("SELECT * FROM ontologize_context WHERE collection = ? AND ( " + 
				whereClause + " )")) {
			query.setParameter(1, collection.getId());
			int i=2;
			for(Search search : searches)
				query.setParameter(i++, "^(.*[^a-zA-Z])?" + search.getSearch() + "([^a-zA-Z].*)?$");
			ResultSet result = query.execute();
			while(result.next()) {
				Context context = createContext(result);
				if(!foundIds.contains(context.getId())) {
					contexts.add(context);
					foundIds.add(context.getId());
				}
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		List<TypedContext> typedContexts = createHighlightedAndShortenedTypedContexts(contexts, searches);
		
		return typedContexts;
	}

	private List<TypedContext> createHighlightedAndShortenedTypedContexts(List<Context> contexts, java.util.Collection<Search> searches) {
		//shorten the context to be a number of characters before and after ... some text with WORD that appears in text ...
		//could appear multiple times in text, have to split into multiple contexts
		List<TypedContext> result = new LinkedList<TypedContext>();
		
		Set<Context> contextsSet = new HashSet<Context>();
		contextsSet.addAll(contexts);
		
		int j=0;
		for(Context context : contextsSet) {
			List<TypedContext> contextResult = new LinkedList<TypedContext>();
			for(Search search : searches) {
				Pattern pattern = Pattern.compile("\\b(?i)" + search.getSearch() + "\\b");
				result.addAll(extract(pattern, search.getSearch(), context, search.getType(), j++));
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

	private List<TypedContext> extract(Pattern pattern, String replaceTerm, Context context, Type type, int j) {	
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
		    	String text = "..." + context.getText().substring(startText, endText) + "...";
		    	String fullText = context.getText();
		    	//extractedText = extractedText.replaceAll(pattern.toString(), "<b>" + replaceTerm + "</b>");
		    	String highlightedExtractedText = text.replaceAll("(?i)" + replaceTerm, "<b>" + replaceTerm + "</b>");
		    	String highlightedFullText = fullText.replaceAll("(?i)" + replaceTerm, "<b>" + replaceTerm + "</b>").replaceAll("\n", "</br>");
		    	
		    	String idString = String.valueOf(context.getId()) + "-" + type.toString() + "-" + id++ + "-" + j;
		    	TypedContext typedContext = new TypedContext(idString, context.getCollectionId(), context.getSource(), 
		    			text, fullText, highlightedExtractedText, highlightedFullText, type);
		    	result.add(typedContext);
		    }
		}
	    return result;
	}	
	
}
