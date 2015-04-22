package edu.arizona.biosemantics.oto2.steps.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sencha.gxt.data.shared.ListStore;

import edu.arizona.biosemantics.common.biology.TaxonGroup;

public class Collection implements Serializable, Comparable<Collection> {

	private int id = -1;
	private String name = "";
	private TaxonGroup taxonGroup;
	private String secret = "";
	private List<Term> terms = new LinkedList<Term>();
	private Map<Object, List<Comment>> comments = new HashMap<Object, List<Comment>>();
	private Map<Object, Color> colorizations = new HashMap<Object, Color>();	
	private List<Color> colors = new ArrayList<Color>();
	
	public Collection() { }
	
	public Collection(String name, TaxonGroup taxonGroup, String secret, List<Term> terms) {
		this.name = name;
		this.taxonGroup = taxonGroup;
		this.secret = secret;
		this.setTerms(terms);
	}
	
	public Collection(int id, String name, TaxonGroup taxonGroup, String secret, List<Term> terms, 
			Map<Object, List<Comment>> comments, Map<Object, Color> colorizations, List<Color> colors) {
		super();
		this.id = id;
		this.name = name;
		this.taxonGroup = taxonGroup;
		this.secret = secret;
		this.setTerms(terms);
		this.comments = comments;
		this.colorizations = colorizations;
		this.colors = colors;
	}

	public int getId() {
		return id;
	}
	
	public void setTerms(List<Term> terms) {
		this.terms = terms;
		for(Term term : terms)
			term.setCollectionId(id);
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

	public void setId(int id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public List<Term> getTerms() {
		return terms;
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

	public TaxonGroup getTaxonGroup() {
		return taxonGroup;
	}

	public void setTaxonGroup(TaxonGroup taxonGroup) {
		this.taxonGroup = taxonGroup;
	}

	@Override
	public int compareTo(Collection o) {
		return this.getId() - o.getId();
	}

	public Color getColorization(Object object) {
		return colorizations.get(object);
	}

	public void setColorization(Object object, Color color) {
		colorizations.put(object, color);
	}
	
	public Map<Object, Color> getColorizations() {
		return colorizations;
	}
	
	public List<Comment> getComment(Object object) {
		return comments.get(object);
	}
	
	public void addComment(Object object, Comment comment) {
		if(!comments.containsKey(object))
			comments.put(object, new LinkedList<Comment>());
		this.comments.get(object).add(comment);
	}
	
	public void removeComment(Object object, Comment comment) {
		if(comments.containsKey(object))
			comments.get(object).remove(comment);
	}

	public List<Color> getColors() {
		return colors;
	}

	public Map<Object, List<Comment>> getComments() {
		return comments;
	}

		
}
