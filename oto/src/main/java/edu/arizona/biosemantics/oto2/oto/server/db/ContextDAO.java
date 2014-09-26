package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.arizona.biosemantics.oto2.oto.server.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Context;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext.Type;

public class ContextDAO {

	protected ContextDAO() {} 
	
	public Context get(int id)  {
		Context context = null;
		try(Query query = new Query("SELECT * FROM oto_context WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				context = createContext(result);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return context;
	}
	
	public List<Context> get(Collection collection)  {
		List<Context> contexts = new LinkedList<Context>();
		try(Query query = new Query("SELECT * FROM oto_context WHERE collectionId = ?")) {
			query.setParameter(1, collection.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				Context context = createContext(result);
				contexts.add(context);
			}
		} catch(Exception e) {
			e.printStackTrace();
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
		if(!context.hasId()) {
			try(Query insert = new Query("INSERT INTO `oto_context` " +
					"(`collection`, `source`, `text`) VALUES (?, ?, ?)")) {
				insert.setParameter(1, context.getCollectionId());
				insert.setParameter(2, context.getSource());
				insert.setParameter(3, context.getText());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				context.setId(id);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return context;
	}
	
	public void update(Context context)  {
		try(Query query = new Query("UPDATE oto_context "
				+ "SET collectionId = ?, source = ?, text = ? WHERE id = ?")) {
			query.setParameter(1, context.getCollectionId());
			query.setParameter(2, context.getSource());
			query.setParameter(3, context.getText());
			query.setParameter(4, context.getId());
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}
	}
	
	public void remove(Context context)  {
		try(Query query = new Query("DELETE FROM oto_context WHERE id = ?")) {
			query.setParameter(1, context.getId());
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}
	}	
	
	public void remove(int collectionId)  {
		try(Query query = new Query("DELETE FROM oto_context WHERE collectionId = ?")) {
			query.setParameter(1, collectionId);
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}
	}
	
	//http://stackoverflow.com/questions/2839441/mysql-query-problem
	//don't want to use LIKE %term% because it will be slow... so just approximate
	public List<TypedContext> get(Collection collection, Term term)  {
		List<Context> contexts = new LinkedList<Context>();
		List<String> searches = new LinkedList<String>();
		String searchTerm = term.getTerm().trim();
		searches.add(searchTerm);
		searches.add(searchTerm.replaceAll(" ", "-"));
		searches.add(searchTerm.replaceAll(" ", "_"));
		for(String search : searches) 
			try(Query query = new Query("SELECT * FROM oto_context WHERE collection = ? AND MATCH (text) AGAINST (? IN NATURAL LANGUAGE MODE)")) {
				query.setParameter(1, collection.getId());
				query.setParameter(2, search);	
				ResultSet result = query.execute();
				while(result.next()) {
					Context context = createContext(result);
					contexts.add(context);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		
		if(term.hasChangedSpelling()) {
			searches = new LinkedList<String>();
			searchTerm = term.getOriginalTerm().trim();
			searches.add(searchTerm);
			searches.add(searchTerm.replaceAll(" ", "-"));
			searches.add(searchTerm.replaceAll(" ", "_"));
			
			try(Query query = new Query("SELECT * FROM oto_context WHERE collection = ? AND MATCH (text) AGAINST (? IN NATURAL LANGUAGE MODE)")) {
				query.setParameter(1, collection.getId());
				query.setParameter(2, searchTerm);
				ResultSet result = query.execute();
				while(result.next()) {
					Context context = createContext(result);
					contexts.add(context);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		List<TypedContext> typedContexts = createHighlightedAndShortenedTypedContexts(contexts, term);
		return typedContexts;
	}

	private List<TypedContext> createHighlightedAndShortenedTypedContexts(List<Context> contexts, Term term) {
		//shorten the context to be a number of characters before and after ... some text with WORD that appears in text ...
		//could appear multiple times in text, have to split into multiple contexts
		List<TypedContext> result = new LinkedList<TypedContext>();
		
		Set<Context> contextsSet = new HashSet<Context>();
		contextsSet.addAll(contexts);
		
		for(Context context : contextsSet) {
			Pattern originalPattern = Pattern.compile("\\b(?i)" + term.getOriginalTerm() + "\\b");
			result.addAll(extract(originalPattern, term.getOriginalTerm(), context, Type.original));
			if(term.hasChangedSpelling()) {
				Pattern currentPattern = Pattern.compile("\\b(?i)" + term.getTerm() + "\\b");
				result.addAll(extract(currentPattern, term.getTerm(), context, Type.updated));
			}
		}
		return result;
	}

	private List<TypedContext> extract(Pattern pattern, String replaceTerm, Context context, Type type) {	
		List<TypedContext> result = new LinkedList<TypedContext>();
		Matcher matcher = pattern.matcher(context.getText());
		
		int id = 0;
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
	    	String fullText = context.getText().replaceAll("(?i)" + replaceTerm, "<b>" + replaceTerm + "</b>");
	    	TypedContext typedContext = new TypedContext(String.valueOf(context.getId()) + "-" + type.toString() + "-" + id++, 
	    			context.getCollectionId(), context.getSource(), extractedText, fullText, type);
	    	result.add(typedContext);
	    }
	    return result;
	}	
	
}
