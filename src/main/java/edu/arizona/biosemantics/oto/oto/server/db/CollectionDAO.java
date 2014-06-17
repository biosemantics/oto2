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

public class CollectionDAO {

	private static CollectionDAO instance;
	
	public static CollectionDAO getInstance() {
		if(instance == null)
			instance = new CollectionDAO();
		return instance;
	}
	
	public boolean isValidSecret(int id, String secret) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("SELECT * FROM collection WHERE id = ?");
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
		Query query = new Query("SELECT * FROM collection WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			collection = createCollection(result);
		}
		
		List<Bucket> buckets = BucketDAO.getInstance().getBuckets(collection);
		for(Bucket bucket : buckets)
			bucket.setCollection(collection);
		collection.setBuckets(BucketDAO.getInstance().getBuckets(collection));
		List<Label> labels = LabelDAO.getInstance().getLabels(collection);
		for(Label label : labels)
			label.setCollection(collection);
		collection.setLabels(LabelDAO.getInstance().getLabels(collection));
		
		query.close();
		
		query = new Query("UPDATE collection SET lastretrieved = ? WHERE id = ?");
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
			Query insert = new Query("INSERT INTO `collection` (`name`, `secret`) VALUES(?, ?)");
			insert.setParameter(1, collection.getName());
			insert.setParameter(2, collection.getSecret());
			insert.execute();
			ResultSet generatedKeys = insert.getGeneratedKeys();
			generatedKeys.next();
			int id = generatedKeys.getInt(1);
			insert.close();
			
			collection.setId(id);
			
			for(Bucket bucket : collection.getBuckets())
				BucketDAO.getInstance().insert(bucket);
					
			for(Label label : collection.getLabels())
				LabelDAO.getInstance().insert(label);
		}
		return collection;
	}
	
	public void update(Collection collection) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("UPDATE collection SET name = ?, secret = ? WHERE id = ?");
		query.setParameter(1, collection.getName());
		query.setParameter(2, collection.getSecret());
		query.setParameter(3, collection.getId());
		query.executeAndClose();
		
		//buckets can actually only be the old ones so update should be sufficient
		for(Bucket oldBucket : BucketDAO.getInstance().getBuckets(collection)) 
			BucketDAO.getInstance().remove(oldBucket);
		
		for(Bucket bucket : collection.getBuckets()) {
			BucketDAO.getInstance().insert(bucket);
		}
		
		//labels can be new and old
		for(Label oldLabel : LabelDAO.getInstance().getLabels(collection)) 
			LabelDAO.getInstance().remove(oldLabel);
		
		for(Label label : collection.getLabels())
			LabelDAO.getInstance().insert(label);
	}
	
	public void remove(Collection collection) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("DELETE FROM collection WHERE id = ?");
		query.setParameter(1, collection.getId());
		query.executeAndClose();
		
		for(Bucket bucket : collection.getBuckets())
			BucketDAO.getInstance().remove(bucket);
		
		for(Label label : collection.getLabels())
			LabelDAO.getInstance().remove(label);
	}	
}
