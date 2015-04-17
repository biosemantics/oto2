package edu.arizona.biosemantics.oto2.steps.shared.model.toontology;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Bucket implements Serializable {

	public static int ID;
	
	private int id = ID++;
	private String name = "";
	
	public Bucket() { }
	
	public Bucket(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String text) {
		this.name = text;
	}
	public int getId() {
		return id;
	}
	
}