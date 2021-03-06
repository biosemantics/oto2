package edu.arizona.biosemantics.oto2.ontologize.server.persist.db;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.ontology.search.FileSearcher;
import edu.arizona.biosemantics.common.ontology.search.Searcher;
import edu.arizona.biosemantics.common.ontology.search.TaxonGroupOntology;
import edu.arizona.biosemantics.common.ontology.search.model.Ontology;
import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry;
import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry.Type;
import edu.arizona.biosemantics.oto2.ontologize.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Color;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Colorable;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Comment;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Commentable;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Status;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.StatusEnum;

public class CollectionDAO {
		
	private TermDAO termDAO;
	private OntologyClassSubmissionDAO ontologyClassSubmissionDAO;
	private OntologySynonymSubmissionDAO ontologySynonymSubmissionDAO;

	public CollectionDAO() {} 
	
	public boolean isValidSecret(int id, String secret) throws QueryException  {
		try(Query query = new Query("SELECT secret FROM ontologize_collection WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				String validSecret = result.getString(1);
				return validSecret.equals(secret);
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return false;
	}
	
	public Collection get(int id) throws Exception  {
		Collection collection = null;
		try(Query query = new Query("SELECT * FROM ontologize_collection WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				collection = createCollection(result);
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		
		try(Query query = new Query("UPDATE ontologize_collection SET lastretrieved = ? WHERE id = ?")) {
			query.setParameter(2, id);
			Date date = new Date();
			query.setParameter(1, new Timestamp(date.getTime()));
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
		
		return collection;
	}
	
	private Collection createCollection(ResultSet result) throws Exception {
		int id = result.getInt("id");
		String name = result.getString("name");
		String taxonGroupString = result.getString("taxongroup");
		String secret = result.getString("secret");
		List<Term> terms = termDAO.getTerms(id);
		
		TaxonGroup taxonGroup = null;
		try {
			taxonGroup = TaxonGroup.valueOf(taxonGroupString);
		} catch(IllegalArgumentException e) { }
		
		Collection deserializedCollection = deserialize(id);
		Map<Commentable, List<Comment>> comments = new HashMap<Commentable, List<Comment>>();
		Map<Colorable, Color> colorizations = new HashMap<Colorable, Color>();
		List<Color> colors = new LinkedList<Color>();
		if(deserializedCollection != null) {
			comments = deserializedCollection.getComments();
			colorizations = deserializedCollection.getColorizations();
			colors = deserializedCollection.getColors();
		}
		
		Map<Term, Set<Object>> usedTerms = new HashMap<Term, Set<Object>>();
		List<OntologyClassSubmission> classSubmissions = ontologyClassSubmissionDAO.getByCollectionId(id);
		for(OntologyClassSubmission submission : classSubmissions) {
			if(submission.hasTerm()) {
				if(!usedTerms.containsKey(submission.getTerm()))
					usedTerms.put(submission.getTerm(), new HashSet<Object>());
				usedTerms.get(submission.getTerm()).add(submission);
			}
		}
		List<OntologySynonymSubmission> synonymSubmissions = ontologySynonymSubmissionDAO.getByCollectionId(id);
		for(OntologySynonymSubmission submission : synonymSubmissions) {
			if(submission.hasTerm()) {
				if(!usedTerms.containsKey(submission.getTerm()))
					usedTerms.put(submission.getTerm(), new HashSet<Object>());
				usedTerms.get(submission.getTerm()).add(submission);
			}
		}
		
		return new Collection(id, name, taxonGroup, secret, terms, comments, colorizations, colors, usedTerms);
	}

	public Collection insert(Collection collection) throws QueryException, IOException  {
		if(collection.hasId()) 
			this.remove(collection);
		try(Query insert = new Query("INSERT INTO `ontologize_collection` (`name`, `taxongroup`, `secret`) VALUES(?, ?, ?)")) {
			insert.setParameter(1, collection.getName().trim());
			insert.setParameter(2, collection.getTaxonGroup().toString());
			insert.setParameter(3, collection.getSecret());
			insert.execute();
			ResultSet generatedKeys = insert.getGeneratedKeys();
			generatedKeys.next();
			int id = generatedKeys.getInt(1);
			collection.setId(id);
			
			for(Term term : collection.getTerms()) 
				termDAO.insert(term, collection.getId());
			
			serialize(collection);
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return collection;
	}
	
	public void update(Collection collection) throws QueryException, IOException  {		
		try(Query query = new Query("UPDATE ontologize_collection SET name = ?, taxongroup = ?, secret = ? WHERE id = ?")) {
			query.setParameter(1, collection.getName());
			query.setParameter(2, collection.getTaxonGroup().toString());
			query.setParameter(3, collection.getSecret());
			query.setParameter(4, collection.getId());
			query.execute();
			for(Term term : collection.getTerms()) 
				termDAO.update(term, collection.getId());
			
			serialize(collection);
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}
	
	private Collection deserialize(int collectionId) throws IOException {
		String file = Configuration.collectionOntologyDirectory + File.separator + collectionId
				+ File.separator + "collection.ser";
		try(ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			return (Collection) input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			log(LogLevel.ERROR, "Deserialization of user failed", e);
			throw new IOException(e);
		}
	}
	
	private void serialize(Collection collection) throws IOException {
		String path = Configuration.collectionOntologyDirectory + File.separator + collection.getId()
				+ File.separator + "collection.ser";
		File file = new File(path);
		file.getParentFile().mkdirs();
		try (ObjectOutput output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			output.writeObject(collection);
		} catch (IOException e) {
			log(LogLevel.ERROR, "Serialization of user failed", e);
			throw e;
		}
	}

	public void remove(Collection collection) throws QueryException  {
		try(Query query = new Query("DELETE FROM ontologize_collection WHERE id = ?")) {
			query.setParameter(1, collection.getId());
			query.execute();
			for(Term term : collection.getTerms())
				termDAO.remove(term);
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}
	
	public List<Collection> getCollections(TaxonGroup taxonGroup) throws Exception {
		List<Collection> collections = new LinkedList<Collection>();
		try(Query query = new Query("SELECT id FROM ontologize_collection WHERE taxongroup = ?")) {
			query.setParameter(1, taxonGroup.toString());
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				Collection collection = get(id);
				if(collection != null)
					collections.add(collection);
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return collections;
	}

	public Set<TaxonGroup> getTypes() throws QueryException {
		Set<TaxonGroup> taxonGroups = new HashSet<TaxonGroup>();
		try(Query query = new Query("SELECT DISTINCT taxongroup FROM ontologize_collection")) {
			ResultSet result = query.execute();
			while(result.next()) {
				String taxonGroupString = result.getString(1);
				TaxonGroup taxonGroup = null;
				try {
					taxonGroup = TaxonGroup.valueOf(taxonGroupString);
				} catch(IllegalArgumentException e) { }
				taxonGroups.add(taxonGroup);
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return taxonGroups;
	}

	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}

	public void setOntologyClassSubmissionDAO(
			OntologyClassSubmissionDAO ontologyClassSubmissionDAO) {
		this.ontologyClassSubmissionDAO = ontologyClassSubmissionDAO;
	}

	public void setOntologySynonymSubmissionDAO(
			OntologySynonymSubmissionDAO ontologySynonymSubmissionDAO) {
		this.ontologySynonymSubmissionDAO = ontologySynonymSubmissionDAO;
	}
	
}
