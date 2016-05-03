package edu.arizona.biosemantics.oto2.ontologize2.shared;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

@RemoteServiceRelativePath("ontologize2_collection")
public interface ICollectionService extends RemoteService {
	
	public Collection insert(Collection collection) throws Exception;;
	
	public Collection get(int id, String secret) throws Exception;
	
	public void update(Collection collection) throws Exception;
	
	public List<GwtEvent<?>> createTerm(int collectionId, String secret, String term, 
			String partDisambiguator, String classDisambiguator) throws Exception;
	
	public List<GwtEvent<?>> removeTerm(int collectionId, String secret, List<Term> term) throws Exception;
	
	public List<GwtEvent<?>> createPart(int collectionId, String secret, Term parent, List<Term> parts) throws Exception;
	
	public List<GwtEvent<?>> createPart(int id, String secret, Term leadTerm, Term term, boolean disambiguate) throws Exception;
	
	public List<GwtEvent<?>> createSubclass(int collectionId, String secret, Term superclass, List<Term> subclasses) throws Exception;

	//public List<GwtEvent<?>> createSubclass(int collectionId, String secret, Term superclass, Term subclass) throws Exception;
	
	public List<GwtEvent<?>> createSynonym(int collectionId, String secret, Term preferredTerm, List<Term> synonyms) throws Exception;
	
	//public List<GwtEvent<?>> createSynonym(int collectionId, String secret, Term preferredTerm, Term synonyms) throws Exception;
	
	public List<GwtEvent<?>> removePart(int collectionId, String secret, Term parent, List<Term> parts) throws Exception;
	
	public List<GwtEvent<?>> removeSubclass(int collectionId, String secret, Term superclass, List<Term> subclasses) throws Exception;
	
	public List<GwtEvent<?>> removeSynonym(int collectionId, String secret, Term preferredTerm, List<Term> synonyms) throws Exception;

	public boolean hasParents(int collectionId, String secret, Term term) throws Exception;
	
	public boolean hasSuperclasses(int collectionId, String secret, Term term) throws Exception;
	
	public boolean hasPreferredTerms(int collectionId, String secret, Term term) throws Exception;

	public List<Term> getParents(int collectionId, String secret, Term term) throws Exception;
	
	public List<Term> getSuperclasses(int collectionId, String secret, Term term) throws Exception;
	
	public List<Term> getPreferredTerms(int collectionId, String secret, Term term) throws Exception;
	
}
