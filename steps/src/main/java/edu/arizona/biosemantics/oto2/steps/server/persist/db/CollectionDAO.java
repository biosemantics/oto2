package edu.arizona.biosemantics.oto2.steps.server.persist.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Term;

public class CollectionDAO {
		
	private TermDAO termDAO;

	public CollectionDAO() {} 
	
	public boolean isValidSecret(int id, String secret)  {
		try(Query query = new Query("SELECT secret FROM otosteps_collection WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				String validSecret = result.getString(1);
				return validSecret.equals(secret);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return false;
	}
	
	public Collection get(int id)  {
		Collection collection = null;
		try(Query query = new Query("SELECT * FROM otosteps_collection WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				collection = createCollection(result);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		try(Query query = new Query("UPDATE otosteps_collection SET lastretrieved = ? WHERE id = ?")) {
			query.setParameter(2, id);
			Date date = new Date();
			query.setParameter(1, new Timestamp(date.getTime()));
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		return collection;
	}
	
	private Collection createCollection(ResultSet result) throws SQLException {
		int id = result.getInt("id");
		String name = result.getString("name");
		String taxonGroupString = result.getString("taxongroup");
		String secret = result.getString("secret");
		List<Term> terms = termDAO.getTerms(id);
		
		TaxonGroup taxonGroup = null;
		try {
			taxonGroup = TaxonGroup.valueOf(taxonGroupString);
		} catch(IllegalArgumentException e) { }
		
		return new Collection(id, name, taxonGroup, secret, terms);
	}

	public Collection insert(Collection collection)  {
		if(!collection.hasId()) {
			try(Query insert = new Query("INSERT INTO `otosteps_collection` (`name`, `taxongroup`, `secret`) VALUES(?, ?, ?)")) {
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
			} catch(Exception e) {
				log(LogLevel.ERROR, "Query Exception", e);
			}
		}
		return collection;
	}
	
	public void update(Collection collection)  {		
		try(Query query = new Query("UPDATE otosteps_collection SET name = ?, taxongroup = ?, secret = ? WHERE id = ?")) {
			query.setParameter(1, collection.getName());
			query.setParameter(2, collection.getTaxonGroup().toString());
			query.setParameter(3, collection.getSecret());
			query.setParameter(4, collection.getId());
			query.execute();
			for(Term term : collection.getTerms()) 
				termDAO.update(term, collection.getId());
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}
	
	public void remove(Collection collection)  {
		try(Query query = new Query("DELETE FROM otosteps_collection WHERE id = ?")) {
			query.setParameter(1, collection.getId());
			query.execute();
			for(Term term : collection.getTerms())
				termDAO.remove(term);
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}
	
	public List<Collection> getCollections(TaxonGroup taxonGroup) {
		List<Collection> collections = new LinkedList<Collection>();
		try(Query query = new Query("SELECT id FROM otosteps_collection WHERE taxongroup = ?")) {
			query.setParameter(1, taxonGroup.toString());
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				Collection collection = get(id);
				if(collection != null)
					collections.add(collection);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return collections;
	}

	public Set<TaxonGroup> getTypes() {
		Set<TaxonGroup> taxonGroups = new HashSet<TaxonGroup>();
		try(Query query = new Query("SELECT DISTINCT taxongroup FROM otosteps_collection")) {
			ResultSet result = query.execute();
			while(result.next()) {
				String taxonGroupString = result.getString(1);
				TaxonGroup taxonGroup = null;
				try {
					taxonGroup = TaxonGroup.valueOf(taxonGroupString);
				} catch(IllegalArgumentException e) { }
				taxonGroups.add(taxonGroup);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return taxonGroups;
	}

	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}	
}
