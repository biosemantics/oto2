package edu.arizona.biosemantics.oto2.ontologize2.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreatePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSubclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemovePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSubclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.IncidenceMatrix;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermDisambiguator;

/**
 * Disambiguation notes: 
 * Subclasses: We only allow one maximum meaning of a term.
 * If they assign a term to multiple parents where they have a different meaning we force them to create a new term with a different name.
 * 
 * Parts: We allow multiple meanings of a term. 
 * For parts it can never be the case that the same named term has multiple parents with different meanings of the parts name.
 * So the answer of the user should never be no to whether they are duplicates.
 * 
 * Synonyms: We dont allow multiple parents for a synonym so this is not an issue.
 * 
 * @author rodenhausen
 *
 */
public class CollectionService extends RemoteServiceServlet implements ICollectionService {

	private int currentCollectionId = 0;
	
	public CollectionService() {
		File file = new File(Configuration.collectionsDirectory);
		if(!file.exists())
			file.mkdirs();
		
		for(File collectionFile : file.listFiles()) {
			try {
				int id = Integer.parseInt(collectionFile.getName());
				if(id >= currentCollectionId)
					currentCollectionId = id + 1;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public synchronized Collection insert(Collection collection) throws Exception {
		collection.setId(currentCollectionId++);
		serializeCollection(collection);
		return collection;
	}

	private void serializeCollection(Collection collection) {
		File collectionDirectory = new File(Configuration.collectionsDirectory + File.separator + collection.getId());
		if(!collectionDirectory.exists())
			collectionDirectory.mkdir();
		
		try(ObjectOutputStream collectionOutput = new ObjectOutputStream(new FileOutputStream(
				Configuration.collectionsDirectory + File.separator + collection.getId() + File.separator + "collection.ser"))) {
			collectionOutput.writeObject(collection);
			IncidenceMatrix incidenceMatrix = IncidenceMatrix.fromCollection(collection);
			try(ObjectOutputStream matrixOutput = new ObjectOutputStream(new FileOutputStream(
					Configuration.collectionsDirectory + File.separator + collection.getId() + File.separator + "incidence-matrix.ser"))) {
				matrixOutput.writeObject(incidenceMatrix);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Collection get(int collectionId, String secret) throws Exception {
		try(ObjectInputStream is = new ObjectInputStream(new FileInputStream(
				Configuration.collectionsDirectory + File.separator + collectionId + File.separator + "collection.ser"))) {
			Object object = is.readObject();
			if(object instanceof Collection) {
				Collection collection = (Collection)object;
				if(collection.getSecret().equals(secret))
					return collection;
			}
		}
		throw new Exception("Could not read collection");
	}
	
	private IncidenceMatrix getIncidenceMatrix(int collectionId, String secret) throws Exception {
		this.get(collectionId, secret); //test secret
		try(ObjectInputStream is = new ObjectInputStream(new FileInputStream(
				Configuration.collectionsDirectory + File.separator + collectionId + File.separator + "incidence-matrix.ser"))) {
			Object object = is.readObject();
			if(object instanceof IncidenceMatrix)
				return (IncidenceMatrix)object;
		}
		throw new Exception("Could not read collection");
	}

	@Override
	public void update(Collection collection) throws Exception {
		Collection storedCollection = this.get(collection.getId(), collection.getSecret());
		if(storedCollection.getSecret().equals(collection.getSecret())) {
			serializeCollection(collection);
		}
	}
	
	@Override
	public List<GwtEvent<?>> createTerm(int collectionId, String secret, String term, 
			String partDisambiguator, String classDisambiguator) throws Exception {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		Collection collection = this.get(collectionId, secret);
		if(!collection.hasTerm(term)) {
			Term newTerm = new Term(term, partDisambiguator, classDisambiguator);
			collection.createTerm(newTerm);
			result.add(new CreateTermEvent(newTerm));
		}
		serializeCollection(collection);
		return result;
	}

	@Override
	public List<GwtEvent<?>> removeTerm(int collectionId, String secret, List<Term> terms) throws Exception {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		Collection collection = this.get(collectionId, secret);
		for(Term term : terms) {
			if(!collection.hasTerm(term.getDisambiguatedValue())) {
				collection.removeTerms(term);
				result.add(new RemoveTermEvent(term));
			}
		}
		serializeCollection(collection);
		return result;
	}

	@Override
	public List<GwtEvent<?>> createPart(int collectionId, String secret, Term parent, List<Term> parts) throws Exception {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		Collection collection = this.get(collectionId, secret);
		if(!collection.hasTerm(parent.getDisambiguatedValue()))
			throw new Exception("Parent does not exist: " + parent.getDisambiguatedValue());
		for(Term part : parts)
			if(!collection.hasTerm(part.getDisambiguatedValue()))
				throw new Exception("Part does not exist: " + part.getDisambiguatedValue());
		collection.createPart(parent, parts);
		result.add(new CreatePartEvent(parent, parts));
		serializeCollection(collection);
		return result;
	}
	
	@Override
	public List<GwtEvent<?>> createPart(int collectionId, String secret, Term parent, Term part, boolean disambiguate) throws Exception {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		Collection collection = this.get(collectionId, secret);
		if(!collection.hasTerm(parent.getDisambiguatedValue()))
			throw new Exception("Parent does not exist: " + parent.getDisambiguatedValue());
		if(!collection.hasTerm(part.getDisambiguatedValue()))
			throw new Exception("Part does not exist: " + part.getDisambiguatedValue());
		
		IncidenceMatrix incidenceMatrix = this.getIncidenceMatrix(collectionId, secret);
		if(!incidenceMatrix.getParents(part.getDisambiguatedValue()).isEmpty() && disambiguate) {
			result.addAll(disambiguatePart(collection, parent, part, incidenceMatrix));
		} else {
			collection.createPart(parent, part);
			incidenceMatrix.createPart(parent.getDisambiguatedValue(), part.getDisambiguatedValue());
			result.add(new CreatePartEvent(parent, part));
		}
		serializeCollection(collection);	
		return result;
	}
	
	private List<GwtEvent<?>> disambiguatePart(Collection collection, Term parentTerm, Term partTerm, IncidenceMatrix incidenceMatrix) throws Exception {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		List<String> parents = incidenceMatrix.getParents(partTerm.getDisambiguatedValue());
		if(parents.size() >= 1) {
			List<Term> newlyCreatedClasses = new ArrayList<Term>(parents.size());
			for(String aParent : parents) {
				Term aParentTerm = collection.getTerm(aParent);
				
				Term disambiguatedTerm = TermDisambiguator.disambiguatePart(partTerm, aParentTerm);
				result.addAll(this.createTerm(collection.getId(), collection.getSecret(), disambiguatedTerm.getValue(), 
						disambiguatedTerm.getPartDisambiguator(), disambiguatedTerm.getClassDisambiguator()));
				result.addAll(this.createPart(collection.getId(), collection.getSecret(), parentTerm, disambiguatedTerm, false));
				newlyCreatedClasses.add(disambiguatedTerm);
				result.addAll(this.createSubclass(collection.getId(), collection.getSecret(), partTerm, Arrays.asList(new Term[] { disambiguatedTerm })));
				result.addAll(this.removePart(collection.getId(), collection.getSecret(), parentTerm, Arrays.asList(new Term[]{ partTerm })));
			}
			for(Term newlyCreatedClass : newlyCreatedClasses) 
				for(Term subclass : collection.getParts(newlyCreatedClass)) 
					result.addAll(disambiguatePart(collection, newlyCreatedClass, subclass, incidenceMatrix));
		}
		for(Term partsPartTerm : collection.getParts(partTerm)) 
			result.addAll(disambiguatePart(collection, partTerm, partsPartTerm, incidenceMatrix));
		return result;
	}

	@Override
	public List<GwtEvent<?>> createSubclass(int collectionId, String secret, Term superclass,
			List<Term> subclasses) throws Exception {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		Collection collection = this.get(collectionId, secret);
		if(!collection.hasTerm(superclass.getDisambiguatedValue()))
			throw new Exception("Superclass does not exist: " + superclass.getDisambiguatedValue());
		for(Term subclass : subclasses)
			if(!collection.hasTerm(subclass.getDisambiguatedValue()))
				throw new Exception("Subclass does not exist: " + subclass.getDisambiguatedValue());
		collection.createSubclass(superclass, subclasses);
		result.add(new CreateSubclassEvent(superclass, subclasses));
		serializeCollection(collection);
		return result;
	}

	@Override
	public List<GwtEvent<?>> createSynonym(int collectionId, String secret, Term preferredTerm,
			List<Term> synonyms) throws Exception {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		Collection collection = this.get(collectionId, secret);
		if(!collection.hasTerm(preferredTerm.getDisambiguatedValue()))
			throw new Exception("Preferred does not exist: " + preferredTerm.getDisambiguatedValue());
		for(Term synonym : synonyms)
			if(!collection.hasTerm(synonym.getDisambiguatedValue()))
				throw new Exception("Synonym does not exist: " + synonym.getDisambiguatedValue());
		collection.createSynonym(preferredTerm, synonyms);
		result.add(new CreateSynonymEvent(preferredTerm, synonyms));
		serializeCollection(collection);
		return result;
	}

	@Override
	public List<GwtEvent<?>> removePart(int collectionId, String secret, Term parent, List<Term> parts) throws Exception {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		Collection collection = this.get(collectionId, secret);
		if(!collection.hasTerm(parent.getDisambiguatedValue()))
			throw new Exception("Parent does not exist: " + parent.getDisambiguatedValue());
		for(Term part : parts)
			if(!collection.hasTerm(part.getDisambiguatedValue()))
				throw new Exception("Part does not exist: " + part.getDisambiguatedValue());
		collection.removePart(parent, parts);
		result.add(new RemovePartEvent(parent, parts));
		serializeCollection(collection);
		return result;
	}

	@Override
	public List<GwtEvent<?>> removeSubclass(int collectionId, String secret, Term superclass,
			List<Term> subclasses) throws Exception {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		Collection collection = this.get(collectionId, secret);
		if(!collection.hasTerm(superclass.getDisambiguatedValue()))
			throw new Exception("Superclass does not exist: " + superclass.getDisambiguatedValue());
		for(Term subclass : subclasses)
			if(!collection.hasTerm(subclass.getDisambiguatedValue()))
				throw new Exception("Subclass does not exist: " + subclass.getDisambiguatedValue());
		collection.removeSubclass(superclass, subclasses);
		result.add(new RemoveSubclassEvent(superclass, subclasses));
		serializeCollection(collection);
		return result;
	}

	@Override
	public List<GwtEvent<?>> removeSynonym(int collectionId, String secret, Term preferredTerm,
			List<Term> synonyms) throws Exception {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		Collection collection = this.get(collectionId, secret);
		if(!collection.hasTerm(preferredTerm.getDisambiguatedValue()))
			throw new Exception("Preferred term does not exist: " + preferredTerm.getDisambiguatedValue());
		for(Term synonym : synonyms)
			if(!collection.hasTerm(synonym.getDisambiguatedValue()))
				throw new Exception("Synonym does not exist: " + synonym.getDisambiguatedValue());
		collection.removeSynonym(preferredTerm, synonyms);
		result.add(new RemoveSynonymEvent(preferredTerm, synonyms));
		serializeCollection(collection);
		return result;
	}

	@Override
	public boolean hasParents(int collectionId, String secret, Term part) throws Exception {
		return !this.getParents(collectionId, secret, part).isEmpty();
	}

	@Override
	public boolean hasSuperclasses(int collectionId, String secret, Term subclass) throws Exception {
		return !this.getSuperclasses(collectionId, secret, subclass).isEmpty();
	}

	@Override
	public boolean hasPreferredTerms(int collectionId, String secret, Term synonym) throws Exception {
		return !this.getPreferredTerms(collectionId, secret, synonym).isEmpty();
	}

	@Override
	public List<Term> getParents(int collectionId, String secret, Term term) throws Exception {
		Collection collection = this.get(collectionId, secret);
		IncidenceMatrix matrix = this.getIncidenceMatrix(collectionId, secret);
		List<String> parents = matrix.getParents(term.getDisambiguatedValue());
		List<Term> result = new ArrayList<Term>(parents.size());
		for(String parent : parents) {
			result.add(collection.getTerm(parent));
		}
		return result;
	}

	@Override
	public List<Term> getSuperclasses(int collectionId, String secret, Term term) throws Exception {
		Collection collection = this.get(collectionId, secret);
		IncidenceMatrix matrix = this.getIncidenceMatrix(collectionId, secret);
		List<String> superclasses = matrix.getParents(term.getDisambiguatedValue());
		List<Term> result = new ArrayList<Term>(superclasses.size());
		for(String superclass : superclasses) {
			result.add(collection.getTerm(superclass));
		}
		return result;
	}

	@Override
	public List<Term> getPreferredTerms(int collectionId, String secret, Term term) throws Exception {
		Collection collection = this.get(collectionId, secret);
		IncidenceMatrix matrix = this.getIncidenceMatrix(collectionId, secret);
		List<String> preferredTerms = matrix.getParents(term.getDisambiguatedValue());
		List<Term> result = new ArrayList<Term>(preferredTerms.size());
		for(String preferredTerm : preferredTerms) {
			result.add(collection.getTerm(preferredTerm));
		}
		return result;
	}

	
	
}
