package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class CollectionDAO {
	
	private BucketDAO bucketDAO;
	private LabelDAO labelDAO;
	private TermDAO termDAO;
	
	
	
	public void setBucketDAO(BucketDAO bucketDAO) {
		this.bucketDAO = bucketDAO;
	}

	public void setLabelDAO(LabelDAO labelDAO) {
		this.labelDAO = labelDAO;
	}

	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}

	public boolean isValidSecret(int id, String secret) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("SELECT * FROM oto_collection WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			String validSecret = result.getString(3);
			return validSecret.equals(secret);
		}
		return false;
	}
	
	public Collection get(int id) throws ClassNotFoundException, SQLException, IOException {
		Collection collection = null;
		Query query = new Query("SELECT * FROM oto_collection WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			collection = createCollection(result);
		}
		
		List<Bucket> buckets = bucketDAO.getBuckets(collection);
		for(Bucket bucket : buckets)
			bucket.setCollection(collection);
		collection.setBuckets(buckets);
		List<Label> labels = labelDAO.getLabels(collection);
		for(Label label : labels)
			label.setCollection(collection);
		collection.setLabels(labels);
		
		query.close();
		
		query = new Query("UPDATE oto_collection SET lastretrieved = ? WHERE id = ?");
		query.setParameter(2, id);
		Date date = new Date();
		query.setParameter(1, new Timestamp(date.getTime()));
		query.executeAndClose();
		
		return collection;
	}
	
	private Collection createCollection(ResultSet result) throws SQLException {
		int id = result.getInt(1);
		String name = result.getString(2);
		String secret = result.getString(3);
		return new Collection(id, name, secret);
	}

	public Collection insert(Collection collection) throws ClassNotFoundException, SQLException, IOException {
		if(!collection.hasId()) {
			Query insert = new Query("INSERT INTO `oto_collection` (`name`, `secret`) VALUES(?, ?)");
			insert.setParameter(1, collection.getName());
			insert.setParameter(2, collection.getSecret());
			insert.execute();
			ResultSet generatedKeys = insert.getGeneratedKeys();
			generatedKeys.next();
			int id = generatedKeys.getInt(1);
			insert.close();
			
			collection.setId(id);
			
			for(Bucket bucket : collection.getBuckets())
				bucketDAO.insert(bucket);
					
			for(Label label : collection.getLabels())
				labelDAO.insert(label);
		}
		return collection;
	}
	
	public void update(Collection collection) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("UPDATE oto_collection SET name = ?, secret = ? WHERE id = ?");
		query.setParameter(1, collection.getName());
		query.setParameter(2, collection.getSecret());
		query.setParameter(3, collection.getId());
		query.executeAndClose();
		
		for(Term term : termDAO.getTerms(collection)) {
			termDAO.remove(term);
		}
		
		//buckets can actually only be the old ones so update should be sufficient
		for(Bucket oldBucket : bucketDAO.getBuckets(collection))
			bucketDAO.remove(oldBucket);
		
		for(Bucket bucket : collection.getBuckets()) {
			bucketDAO.insert(bucket);
			for(Term term : bucket.getTerms()) {
				termDAO.insert(term);
			}
		}
		
		//labels can be new and old
		for(Label oldLabel : labelDAO.getLabels(collection)) 
			labelDAO.remove(oldLabel);
		
		for(Label label : collection.getLabels()) {
			labelDAO.insert(label);
			for(Term term : label.getTerms()) {
				termDAO.insert(term);
				labelDAO.insert(term, label);
			}
		}
	}
	
	public void remove(Collection collection) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("DELETE FROM oto_collection WHERE id = ?");
		query.setParameter(1, collection.getId());
		query.executeAndClose();
		
		for(Bucket bucket : collection.getBuckets())
			bucketDAO.remove(bucket);
		
		for(Label label : collection.getLabels())
			labelDAO.remove(label);
	}	
}
