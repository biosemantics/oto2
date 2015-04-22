package edu.arizona.biosemantics.oto2.steps.shared.model.toontology;

import java.io.Serializable;

public class Status implements Serializable, Comparable<Status> {

	private int id = -1;
	private String name;
	
	public Status() { }
	
	public Status(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasId() {
		return id != -1;
	}

	@Override
	public int compareTo(Status o) {
		return this.name.compareTo(o.getName());
	}
	
}
