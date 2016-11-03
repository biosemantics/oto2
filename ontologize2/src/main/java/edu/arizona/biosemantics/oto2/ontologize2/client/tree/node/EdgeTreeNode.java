package edu.arizona.biosemantics.oto2.ontologize2.client.tree.node;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;

public class EdgeTreeNode extends TextTreeNode {
	
	private Edge edge;

	public EdgeTreeNode(Edge edge) {
		super(edge.getDest() + " " + edge.getType().getTargetLabel() + " of " + edge.getSrc());
		this.edge = edge;
	}

	public Edge getEdge() {
		return edge;
	}
	
	
	
	
}
