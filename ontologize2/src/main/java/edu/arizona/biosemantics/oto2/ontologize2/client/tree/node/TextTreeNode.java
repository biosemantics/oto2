package edu.arizona.biosemantics.oto2.ontologize2.client.tree.node;

public abstract class TextTreeNode implements Comparable<TextTreeNode>{

	private static int currentId = 0;
	private String text;
	protected int id;
	
	public TextTreeNode(String text) {
		this.id = currentId++;
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public String getId() { 
		return String.valueOf(id);
	}
	
	@Override
	public int compareTo(TextTreeNode o) {
		return this.getText().compareTo(o.getText());
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
		TextTreeNode other = (TextTreeNode) obj;
		if (id != other.id)
			return false;
		return true;
	}
}