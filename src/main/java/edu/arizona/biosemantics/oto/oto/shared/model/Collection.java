package edu.arizona.biosemantics.oto.oto.shared.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Collection implements Serializable {

	private int id = -1;
	private String name;
	private String secret;
	private List<Bucket> buckets = new LinkedList<Bucket>();
	private List<Label> labels = new LinkedList<Label>();

	public Collection() {
		
	}
	
	public Collection(int id, String name, String secret) {
		super();
		this.id = id;
		this.name = name;
		this.secret = secret;
	}

	public int getId() {
		return id;
	}
	
	public void setBuckets(List<Bucket> buckets) {
		/*for(Bucket bucket : buckets)
			bucket.setCollection(this);*/
		this.buckets = buckets;
	}
	
	public void setLabels(List<Label> labels) {
		/*for(Label label : labels)
			label.setCollection(this);*/
		this.labels = labels;
	}

	public List<Bucket> getBuckets() {
		return buckets;
	}

	public List<Label> getLabels() {
		return labels;
	}
	
	public boolean hasId() {
		return id != -1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void add(Bucket bucket) {
		//bucket.setCollection(this);
		buckets.add(bucket);
	}

	public void add(Label label) {
		//label.setCollection(this);
		labels.add(label);
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}	
	
	
	
}
