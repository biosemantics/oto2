package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.io.Serializable;

public class TypedContext implements Serializable {

	public enum Type implements Serializable { original, updated }
	
	private String id = "";
	private int collectionId;
	private String source;
	private String text;
	private Type type;
	
	public TypedContext() { }
	
	public TypedContext(String id, int collectionId, String source, String text, Type type) { 
		this.id = id;
		this.collectionId = collectionId;
		this.source = source;
		this.text = text;
		this.type = type;
	}
	
	public TypedContext(Context context, Type type) {
		this.id = String.valueOf(context.getId());
		this.collectionId = context.getCollectionId();
		this.source = context.getSource();
		this.text = context.getText();
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public int getCollectionId() {
		return collectionId;
	}

	public String getSource() {
		return source;
	}

	public String getText() {
		return text;
	}

	public Type getType() {
		return type;
	}
	
	public String getTypeString() {
		return type.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		TypedContext other = (TypedContext) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
