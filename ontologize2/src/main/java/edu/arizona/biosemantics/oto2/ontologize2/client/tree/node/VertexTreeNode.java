package edu.arizona.biosemantics.oto2.ontologize2.client.tree.node;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class VertexTreeNode extends TextTreeNode {

	private Vertex vertex;

	public VertexTreeNode(Vertex vertex) {
		super(vertex.getValue());
		this.vertex = vertex;
	}
	
	@Override
	public String getText() {
		return vertex.getValue();
	}
	
	public Vertex getVertex() {
		return vertex;
	}
	
	@Override
	public String toString() {
		return vertex.toString();
	}
}
