package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

import edu.arizona.biosemantics.oto2.oto.server.Configuration;
import edu.arizona.biosemantics.oto2.oto.server.db.Query.QueryException;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.Categorization;

public class CollectionDAO {
	
	private BucketDAO bucketDAO;
	private LabelDAO labelDAO;
	private LabelingDAO labelingDAO;
	private TermDAO termDAO;
	private SynonymDAO synonymDAO;
	
	protected CollectionDAO() {} 
	
	public void setBucketDAO(BucketDAO bucketDAO) {
		this.bucketDAO = bucketDAO;
	}

	public void setLabelDAO(LabelDAO labelDAO) {
		this.labelDAO = labelDAO;
	}
	
	public void setLabelingDAO(LabelingDAO labelingDAO) {
		this.labelingDAO = labelingDAO;
	}
	
	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}

	public void setSynonymDAO(SynonymDAO synonymDAO) {
		this.synonymDAO = synonymDAO;
	}

	public boolean isValidSecret(int id, String secret)  {
		try(Query query = new Query("SELECT secret FROM oto_collection WHERE id = ?")) {
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
		try(Query query = new Query("SELECT * FROM oto_collection WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				collection = createCollection(result);
			}
			
			List<Bucket> buckets = bucketDAO.getBuckets(collection);
			collection.setBuckets(buckets);
			List<Label> labels = labelDAO.getLabels(collection);
			// ensure to return same term objects in bucket and labels, so operations are performed
			// on the same object, e.g. rename
			List<Term> termsToSend = new LinkedList<Term>();
			for(Bucket bucket : buckets)
				termsToSend.addAll(bucket.getTerms());
			for(Label label : labels) {
				List<Term> oldMainTerms = label.getMainTerms();
				Map<Integer, List<Term>> oldSynonymTermsMap = label.getMainTermSynonymsMap();
				Map<Integer, List<Term>> newSynonymTermsMap = label.getMainTermSynonymsMap();
				
				List<Term> newMainLabelTerms = new LinkedList<Term>();
				for(Term mainLabelTerm : label.getMainTerms()) {
					Term mainTermToSend = null;
					int index = termsToSend.indexOf(mainLabelTerm);
					if(index != -1) {
						mainTermToSend = termsToSend.get(index);
					} else {
						mainTermToSend = mainLabelTerm;
					}
					newMainLabelTerms.add(mainTermToSend);
					
					List<Term> newSynonymTerms = new LinkedList<Term>();
					for(Term synonymTerm : label.getSynonyms(mainLabelTerm)) {
						Term synonymTermToSend = termsToSend.get(termsToSend.indexOf(synonymTerm));
						newSynonymTerms.add(synonymTermToSend);
					}
					
					newSynonymTermsMap.put(mainTermToSend.getId(), newSynonymTerms);
				}
				label.setMainTerms(newMainLabelTerms);		
				label.setMainTermSynonymsMap(newSynonymTermsMap);
			}
			//
			collection.setLabels(labels);
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		try(Query query = new Query("UPDATE oto_collection SET lastretrieved = ? WHERE id = ?")) {
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
		int id = result.getInt(1);
		String name = result.getString(2);
		String type = result.getString(3);
		String secret = result.getString(4);
		return new Collection(id, name, type, secret);
	}

	public Collection insert(Collection collection)  {
		if(!collection.hasId()) {
			try(Query insert = new Query("INSERT INTO `oto_collection` (`name`, `type`, `secret`) VALUES(?, ?, ?)")) {
				insert.setParameter(1, collection.getName().trim());
				insert.setParameter(2, collection.getType());
				insert.setParameter(3, collection.getSecret());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				
				collection.setId(id);
				
				for(Bucket bucket : collection.getBuckets()) {
					bucketDAO.insert(bucket, collection.getId());
					bucket.setCollection(id);
				}
						
				for(Label label : collection.getLabels()) {
					labelDAO.insert(label, collection.getId());
					label.setCollection(id);
				}
			} catch(Exception e) {
				log(LogLevel.ERROR, "Query Exception", e);
			}
		}
		return collection;
	}
	
	public void update(Collection collection, boolean storeAsFallback)  {
		/*System.out.println("update collection");
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		try {
			System.out.println(writer.writeValueAsString(collection));
		} catch (Exception e1) {
			e1.printStackTrace();
		}*/
		
		try(Query query = new Query("UPDATE oto_collection SET name = ?, type = ?, secret = ? WHERE id = ?")) {
			query.setParameter(1, collection.getName());
			query.setParameter(2, collection.getType());
			query.setParameter(3, collection.getSecret());
			query.setParameter(4, collection.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		bucketDAO.ensure(collection);
		labelDAO.ensure(collection);
		
		//charaparser makes the first update call setting the initial categorizations from glossary. Save those
		//decisions for resetting only user decisions later.
		File collectionFile = new File(Configuration.files + File.separator + collection.getId() + ".ser");
		if(storeAsFallback) {
			try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
					collectionFile))) {
				out.writeObject(collection);
			} catch(Exception e) {
				log(LogLevel.ERROR, "Couldn't store glossaryDownload locally", e);
			}
		}
	}
	
	public void remove(Collection collection)  {
		try(Query query = new Query("DELETE FROM oto_collection WHERE id = ?")) {
			query.setParameter(1, collection.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		for(Bucket bucket : collection.getBuckets())
			bucketDAO.remove(bucket);
		
		for(Label label : collection.getLabels())
			labelDAO.remove(label);
	}

	public Collection reset(Collection collection, boolean resetAll)  {
		labelingDAO.remove(collection);
		synonymDAO.remove(collection);
		termDAO.resetTerms(collection);
	
		if(!resetAll) {
			File collectionFile = new File(Configuration.files + File.separator + collection.getId() + ".ser");
			if(collectionFile.exists()) {
				try(ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(Configuration.files + 
						File.separator + collection.getId() + ".ser"))) {
					Collection deserializedCollection = (Collection) objectIn.readObject();
					this.update(deserializedCollection, false);
				} catch(Exception e) {
					log(LogLevel.ERROR, "Couldn't store glossaryDownload locally", e);
				}
			}
		}
		return this.get(collection.getId());
	}

	public List<Collection> getCollections(String type) {
		List<Collection> collections = new LinkedList<Collection>();
		try(Query query = new Query("SELECT id FROM oto_collection WHERE type = ?")) {
			query.setParameter(1, type);
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

	public Set<String> getTypes() {
		Set<String> types = new HashSet<String>();
		try(Query query = new Query("SELECT DISTINCT type FROM oto_collection")) {
			ResultSet result = query.execute();
			while(result.next()) {
				String type = result.getString(1);
				types.add(type);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return types;
	}	
}
