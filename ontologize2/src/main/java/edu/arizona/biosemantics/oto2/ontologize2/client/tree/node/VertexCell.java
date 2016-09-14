package edu.arizona.biosemantics.oto2.ontologize2.client.tree.node;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.cell.CellImages;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.VisualizationConfigurationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.TreeView;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class VertexCell extends AbstractCell<Vertex> {

	private static CellImages cellImages = GWT.create(CellImages.class);
	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template(""
				+ "<div id=\"wide\" style=\"float: right; width: calc(100% - 10px);\">"
				+ "<div qtip=\"{2}\" style=\"background-color:{1};\">{0}</div>"
				+ "</div>"
				+ ""
				+ ""
				+ "<div id=\"narrow\" style=\"float: left; width: 10px;\">"
				+ "<div style=\"right:0px; top:0px; width:4px; height:4px; background-image: {3}\" ></div>"
				+ "<div style=\"right:0px; top:5px; width:4px; height:4px; background-image: {4}\" ></div>"
				+ "<div style=\"right:0px; top:10px; width:4px; height:4px; background-image: {5}\" ></div>"
				+ "</div>")
		SafeHtml cell(String value, String background, String quickTipText, String icon1, String icon2,
				String icon3);
	}

	protected static Templates templates = GWT.create(Templates.class);
	private Type type;
	private EventBus eventBus;
	private TreeView treeView;
	private boolean highlight = false;
	
	public VertexCell(EventBus eventBus, TreeView treeView, Type type) {
		this.type = type;
		this.treeView = treeView;
		this.eventBus = eventBus;
		
		bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(VisualizationConfigurationEvent.TYPE, new VisualizationConfigurationEvent.Handler() {
			@Override
			public void onConfig(VisualizationConfigurationEvent event) {
				highlight = event.isHighlightMultipleIncomingEdges();
			}
		});
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, Vertex value, SafeHtmlBuilder sb) {
		String background = "";
		if(value.getValue().equals("f"))
			System.out.println();
		
		boolean closed = highlight && ModelController.getCollection().getGraph().isClosedRelations(value, type);
		boolean ordered = highlight && ModelController.getCollection().getGraph().hasOrderedEdges(value, type);
		boolean multipleInComing = highlight && ModelController.getCollection().getGraph().getInRelations(value, 
				type).size() > 1;
		
		String icon1 = "", icon2 = "", icon3 = "";
		if(closed)
			icon1 = "url(" + cellImages.blue().getSafeUri().asString() + ")";
		if(ordered)
			icon2 = "url(" + cellImages.red().getSafeUri().asString() + ")";
		if(multipleInComing)
			icon3 = "url(" + cellImages.green().getSafeUri().asString() + ")";
		
		SafeHtml rendered = templates.cell(value.getValue(), background, "", icon1, icon2, icon3);
		sb.append(rendered);
	}
}