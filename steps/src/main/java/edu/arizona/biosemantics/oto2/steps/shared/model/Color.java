package edu.arizona.biosemantics.oto2.steps.shared.model;

import java.io.Serializable;

public class Color implements Serializable, Comparable<Color> {

	private static int ID = 0;
	
	private int id = ID++;
	private String hex;
	private String use;
	
	public Color() { }
	
	public Color(String hex, String use) {
		super();
		this.hex = hex;
		this.use = use;
	}
	public String getHex() {
		return hex;
	}
	public void setHex(String hex) {
		this.hex = hex;
	}
	public String getUse() {
		return use;
	}
	public void setUse(String use) {
		this.use = use;
	}
	
	@Override
	public String toString() {
		return hex + ":" + use;
	}

	public int getId() {
		return id;
	}

	@Override
	public int compareTo(Color o) {
		return this.hex.compareTo(o.hex);
	}	

}
