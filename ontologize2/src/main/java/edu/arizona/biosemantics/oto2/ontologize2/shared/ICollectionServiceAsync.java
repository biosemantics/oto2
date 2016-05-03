package edu.arizona.biosemantics.oto2.ontologize2.shared;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ICollectionServiceAsync {	

	public void insert(Collection collection, AsyncCallback<Collection> callback);
	
	public void get(int id, String secret, AsyncCallback<Collection> callback);
	
	public void update(Collection collection, AsyncCallback<Void> callback);

	public void createTerm(int collectionId, String secret, String term, 
			String partDisambiguator, String classDisambiguator, 
			AsyncCallback<List<GwtEvent<?>>> callback);
	
	public void removeTerm(int collectionId, String secret, List<Term> terms, AsyncCallback<List<GwtEvent<?>>> callback);
	
	public void createPart(int collectionId, String secret, Term parent, List<Term> parts, AsyncCallback<List<GwtEvent<?>>> callback);
	
	public void createPart(int id, String secret, Term leadTerm, Term term, boolean disambiguate, AsyncCallback<List<GwtEvent<?>>> callback);
	
	public void createSubclass(int collectionId, String secret, Term superclass, List<Term> subclasses, AsyncCallback<List<GwtEvent<?>>> callback);
	
	//public void createSubclass(int collectionId, String secret, Term superclass, Term subclass, AsyncCallback<List<GwtEvent<?>>> callback);
	
	public void createSynonym(int collectionId, String secret, Term preferredTerm, List<Term> synonyms, AsyncCallback<List<GwtEvent<?>>> callback);
	
	//public void createSynonym(int collectionId, String secret, Term preferredTerm, Term synonym, AsyncCallback<List<GwtEvent<?>>> callback);
	
	public void removePart(int collectionId, String secret, Term parent, List<Term> parts, AsyncCallback<List<GwtEvent<?>>> callback);
	
	public void removeSubclass(int collectionId, String secret, Term superclass, List<Term> subclasses, AsyncCallback<List<GwtEvent<?>>> callback);
	
	public void removeSynonym(int collectionId, String secret, Term preferredTerm, List<Term> synonyms, AsyncCallback<List<GwtEvent<?>>> callback);

	public void hasParents(int collectionId, String secret, Term term, AsyncCallback<Boolean> callback);
	
	public void hasSuperclasses(int collectionId, String secret, Term term, AsyncCallback<Boolean> callback);
	
	public void hasPreferredTerms(int collectionId, String secret, Term term, AsyncCallback<Boolean> callback);
	
	public void getParents(int collectionId, String secret, Term term, AsyncCallback<List<Term>> callback);
	
	public void getSuperclasses(int collectionId, String secret, Term term, AsyncCallback<List<Term>> callback);
	
	public void getPreferredTerms(int collectionId, String secret, Term term, AsyncCallback<List<Term>> callback);

		
}
