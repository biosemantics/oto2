package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Context;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class FilllSample {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("TRUNCATE TABLE  `oto_collection`");
		query.executeAndClose();
		query = new Query("TRUNCATE TABLE  `oto_bucket`");
		query.executeAndClose();
		query = new Query("TRUNCATE TABLE  `oto_label`");
		query.executeAndClose();
		query = new Query("TRUNCATE TABLE  `oto_labeling`");
		query.executeAndClose();
		query = new Query("TRUNCATE TABLE  `oto_synonym`");
		query.executeAndClose();
		query = new Query("TRUNCATE TABLE  `oto_context`");
		query.executeAndClose();
		query = new Query("TRUNCATE TABLE  `oto_term`");
		query.executeAndClose();
		
		List<Bucket> buckets = new LinkedList<Bucket>();
		Bucket b = new Bucket();
		Term t1 = new Term();
		t1.setTerm("test");
		Term t2 = new Term();
		t2.setTerm("test1");
		Term t3 = new Term();
		t3.setTerm("test2");
		b.addTerm(t1);
		b.addTerm(t2);
		b.addTerm(t3);
		buckets.add(b);
		b.setName("bucketName");
		
		Collection collection = new Collection();
		collection.setName("My test");
		collection.setBuckets(buckets);
		
		List<Label> labels = new LinkedList<Label>();
		Label l1 = new Label();
		l1.setName("label1");
		
		Label l2 = new Label();
		l2.setName("label2");
		
		Label l3 = new Label();
		l3.setName("label3");
		
		labels.add(l1);
		labels.add(l2);
		labels.add(l3);
		collection.setLabels(labels);
		
		collection.setSecret("my secret");
		DAOManager daoManager = new DAOManager();
		daoManager.getCollectionDAO().insert(collection);
		
		
		Context c1 = new Context("source1", "sentence1");
		Context c2 = new Context("source2", "sentence2");
		
		Context c3 = new Context("source3", "sentence3");
		Context c4 = new Context("source4", "sentence4");
		daoManager.getContextDAO().insert(c1, t1.getId());
		daoManager.getContextDAO().insert(c2, t2.getId());
		daoManager.getContextDAO().insert(c3, t1.getId());
		daoManager.getContextDAO().insert(c4, t2.getId());
		
		
	}

}