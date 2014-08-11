package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Context;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class FilllSample {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
		LinkedList<Bucket> buckets = new LinkedList<Bucket>();
		Bucket b = new Bucket();
		b.setName("bucketName");
		buckets.add(b);
		
		LinkedList<Term> terms = new LinkedList<Term>();
		Term t1 = new Term();
		t1.setTerm("test");
		Term t2 = new Term();
		t2.setTerm("test1");
		Term t3 = new Term();
		t3.setTerm("test2");
		terms.add(t1);
		terms.add(t2);
		terms.add(t3);
		
		Collection collection = new Collection();
		collection.setName("My test");
		collection.setSecret("my secret");
		//collection.setBuckets(buckets);
		
		LinkedList<Label> labels = new LinkedList<Label>();
		Label l1 = new Label("label1", "descr1");		
		Label l2 = new Label("label2", "descr2");	
		Label l3 = new Label("label3", "descr3");
		labels.add(l1);
		labels.add(l2);
		labels.add(l3);
		
		//collection.setLabels(labels);
		
		DAOManager daoManager = new DAOManager();
		collection = daoManager.getCollectionDAO().insert(collection);
		for(Bucket bucket : buckets) {
			bucket = daoManager.getBucketDAO().insert(bucket, collection.getId());
			for(Term term : terms) 
				term = daoManager.getTermDAO().insert(term, bucket.getId());
		}
		for(Label label : labels) 
			label = daoManager.getLabelDAO().insert(label, collection.getId());
		
		
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
