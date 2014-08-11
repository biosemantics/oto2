package edu.arizona.biosemantics.oto.oto.shared.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class Collection implements Serializable {

	private int id = -1;
	private String name;
	private String secret;
	private LinkedHashSet<Bucket> buckets = new LinkedHashSet<Bucket>();
	private LinkedHashSet<Label> labels = new LinkedHashSet<Label>();

	public Collection() { }
	
	public Collection(int id, String name, String secret) {
		super();
		this.id = id;
		this.name = name;
		this.secret = secret;
	}

	public int getId() {
		return id;
	}
	
	public void setBuckets(LinkedHashSet<Bucket> buckets) {
		for(Bucket bucket : buckets)
			bucket.setCollection(this.getId());
		this.buckets = buckets;
	}
	
	public void setLabels(LinkedHashSet<Label> labels) {
		for(Label label : labels)
			label.setCollection(this.getId());
		this.labels = labels;
	}

	public LinkedHashSet<Bucket> getBuckets() {
		return buckets;
	}

	public LinkedHashSet<Label> getLabels() {
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
		bucket.setCollection(this.getId());
		buckets.add(bucket);
	}

	public void add(Label label) {
		label.setCollection(this.getId());
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Collection other = (Collection) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void addLabel(Label label) {
		label.setCollection(this.id);
		this.labels.add(label);
	}

	public void removeLabel(Label label) {
		this.labels.remove(label);
	}

	public void removeLabels(Set<Label> labels) {
		this.labels.removeAll(labels);
	}

	public LinkedHashSet<Label> getLabels(Term term) {
		LinkedHashSet<Label> result = new LinkedHashSet<Label>();
		for(Label label : labels)
			if(label.getMainTerms().contains(term))
				result.add(label);
		return result;
	}	
	
}
