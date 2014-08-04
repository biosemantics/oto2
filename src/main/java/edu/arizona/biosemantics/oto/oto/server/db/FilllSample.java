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
		
		List<Bucket> buckets = new LinkedList<Bucket>();
		Bucket b = new Bucket();
		Term t1 = new Term();
		t1.setTerm("test");
		Context c1 = new Context();
		c1.setSource("source");
		c1.setSentence("sentence");
		Context c2 = new Context();
		c2.setSource("source");
		c2.setSentence("sentence");
		t1.addContext(c1);
		t1.addContext(c2);
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
		l2.setName("label1");
		
		Label l3 = new Label();
		l3.setName("label1");
		
		labels.add(l1);
		labels.add(l2);
		labels.add(l3);
		collection.setLabels(labels);
		
		collection.setSecret("my secret");
		DAOManager daoManager = new DAOManager();
		daoManager.getCollectionDAO().insert(collection);
	}

}
