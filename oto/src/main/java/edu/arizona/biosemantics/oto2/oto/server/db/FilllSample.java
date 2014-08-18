package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Context;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

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
		Bucket b2 = new Bucket();
		Bucket b3 = new Bucket();
		Term t1 = new Term();
		t1.setTerm("leaf");
		Term t2 = new Term();
		t2.setTerm("stem");
		Term t3 = new Term();
		t3.setTerm("apex");
		Term t4 = new Term();
		t4.setTerm("root");
		Term t5 = new Term();
		t5.setTerm("sepal");
		b.addTerm(t1);
		b.addTerm(t2);
		b.addTerm(t3);
		b.addTerm(t4);
		b.addTerm(t5);
		buckets.add(b);
		b.setName("structures");
		Term c1 = new Term("length");
		Term c2 = new Term("color");
		b2.addTerm(c1);
		b2.addTerm(c2);
		b2.setName("characters");
		b3.setName("others");
		Term o1 = new Term("asdfg");
		b3.addTerm(o1);
		buckets.add(b2);
		buckets.add(b3);
		
		Collection collection = new Collection();
		collection.setName("My test");
		collection.setBuckets(buckets);
		
		List<Label> labels = new LinkedList<Label>();
		Label l0 = new Label();
		l0.setName("structure");
		
		Label l1 = new Label();
		l1.setName("arrangement");
		
		Label l2 = new Label();
		l2.setName("architecture");
		
		Label l3 = new Label();
		l3.setName("coloration");		
		
		labels.add(l0);
		labels.add(l1);
		labels.add(l2);
		labels.add(l3);
		collection.setLabels(labels);
		
		collection.setSecret("my secret");
		DAOManager daoManager = new DAOManager();
		daoManager.getCollectionDAO().insert(collection);
		
		
		Context con1 = new Context("source1", "sentence1");
		Context con2 = new Context("source2", "sentence2");
		
		Context con3 = new Context("source3", "sentence3");
		Context con4 = new Context("source4", "sentence4");
		daoManager.getContextDAO().insert(con1, t1.getId());
		daoManager.getContextDAO().insert(con2, t2.getId());
		daoManager.getContextDAO().insert(con3, t1.getId());
		daoManager.getContextDAO().insert(con4, t2.getId());
		
		
	}

}