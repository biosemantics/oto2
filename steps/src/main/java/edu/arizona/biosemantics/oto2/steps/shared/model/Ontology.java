package edu.arizona.biosemantics.oto2.steps.shared.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import edu.arizona.biosemantics.common.biology.TaxonGroup;

public class Ontology implements Serializable, Comparable<Ontology> {

	private int id = -1;
	private String externalId;
	private String name;
	private String prefix;
	private Set<TaxonGroup> taxonGroups = new HashSet<TaxonGroup>();
	private String browseURL;
	private int collectionId = -1;
	
	public Ontology() { }
	
	public Ontology(int id, String externalId, String name, String prefix,
			Set<TaxonGroup> taxonGroups, String browseURL, int collectionId) {
		super();
		this.id = id;
		this.externalId = externalId;
		this.name = name;
		this.prefix = prefix;
		this.taxonGroups = taxonGroups;
		this.browseURL = browseURL;
		this.collectionId = collectionId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Set<TaxonGroup> getTaxonGroups() {
		return taxonGroups;
	}

	public void setTaxonGroups(Set<TaxonGroup> taxonGroups) {
		this.taxonGroups = taxonGroups;
	}

	public int getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}
	
	public boolean hasCollectionId() {
		return collectionId != -1;
	}
	
	public boolean hasId() {
		return id != -1;
	}
	
	public void setBrowseURL(String browseURL) {
		this.browseURL = browseURL;
	}

	public String getBrowseURL() {
		return browseURL;
	}

	@Override
	public int compareTo(Ontology o) {
		return this.getName().compareTo(o.getName());
	}
		
}
