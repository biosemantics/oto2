package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class BucketDAO {

	private static BucketDAO instance;
	
	public static BucketDAO getInstance() {
		if(instance == null)
			instance = new BucketDAO();
		return instance;
	}
	
	public Bucket getBucket(int id) throws SQLException, ClassNotFoundException, IOException {
		Bucket bucket = null;
		Query query = new Query("SELECT * FROM bucket WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			id = result.getInt(1);
			int collectionId = result.getInt(2);
			String name = result.getString(3);
			String description = result.getString(4);
			bucket = new Bucket(id, CollectionDAO.getInstance().get(collectionId), name, description);
			bucket.setTerms(TermDAO.getInstance().getTerms(bucket));
		}
		query.close();
		return bucket;
	}
	
	public Bucket insert(Bucket bucket) throws SQLException, ClassNotFoundException, IOException {
		if(!bucket.hasId()) {
			Query insert = new Query("INSERT INTO `bucket` " +
					"(`collection`, `name`, `description`) VALUES (?, ?, ?)");
			insert.setParameter(1, bucket.getCollection().getId());
			insert.setParameter(2, bucket.getName());
			insert.setParameter(3, bucket.getDescription());
			insert.execute();
			ResultSet generatedKeys = insert.getGeneratedKeys();
			generatedKeys.next();
			int id = generatedKeys.getInt(1);
			insert.close();
			bucket.setId(id);
			
			for(Term term : bucket.getTerms())
				TermDAO.getInstance().insert(term);
		}
		return bucket;
	}

	public void update(Bucket bucket) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("UPDATE bucket SET name = ?, description = ? WHERE id = ?");
		query.setParameter(1, bucket.getName());
		query.setParameter(2, bucket.getDescription());
		query.setParameter(3, bucket.getId());
		
		TermDAO termDAO = TermDAO.getInstance();
		Bucket oldBucket = this.getBucket(bucket.getId());
		for(Term term : oldBucket.getTerms()) {
			termDAO.remove(term);
		}
		for(Term term : bucket.getTerms()) {
			termDAO.insert(term);
		}
		query.executeAndClose();
	}

	public void remove(Bucket bucket) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("DELETE FROM bucket WHERE id = ?");
		query.setParameter(1, bucket.getId());
		query.executeAndClose();
		
		for(Term term :  bucket.getTerms())
			TermDAO.getInstance().remove(term);
	}
	
	public List<Bucket> getBuckets(Collection collection) throws SQLException, ClassNotFoundException, IOException {
		List<Bucket> buckets = new LinkedList<Bucket>();
		Query query = new Query("SELECT * FROM bucket WHERE collection = ?");
		query.setParameter(1, collection.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			int id = result.getInt(1);
			buckets.add(BucketDAO.getInstance().getBucket(id));
		}
		query.close();
		return buckets;		
	}
	
}
