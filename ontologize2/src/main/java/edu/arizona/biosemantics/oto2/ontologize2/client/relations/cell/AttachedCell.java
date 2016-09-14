package edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.cell.CellImages;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SelectTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.VisualizationConfigurationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent.FilterTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class AttachedCell extends MenuExtendedCell<Row> {

	private static CellImages cellImages = GWT.create(CellImages.class);
	
	public interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template(""
				+ "<div style=\"height: 17px; padding: 2px 2px 0px 2px;\">"
				+ "<div id=\"wide\" style=\"float: right; width: calc(100% - 10px);\">"
				+ ""
				+ ""
				+ "<div class=\"{0}\" qtip=\"{4}\">" 
				+ "<div class=\"{1}\" " 
				+ "style=\"" 
				+ "width: calc(100% - 9px); " 
				+ "height:14px; " 
				+ "background: no-repeat 0 0;" 
				+ "background-image:{6};" 
				+ "background-color:{5};" 
				+ "\">"
				+ "<b>{3}</b><a class=\"{2}\" style=\"height: 15px;\"></a>" 
				+ "</div>" 				
				+ "</div>"
				+ ""
				+ ""
				+ "</div>"
				+ "<div id=\"narrow\"  style=\"float: left; width: 10px;\">"
				+ ""
				+ ""
				+ "<div style=\"right:0px; top:0px; width:4px; height:4px; background-image: {7}\" ></div>"
				+ "<div style=\"right:0px; top:5px; width:4px; height:4px; background-image: {8}\" ></div>"
				+ "<div style=\"right:0px; top:10px; width:4px; height:4px; background-image: {9}\" ></div>"
				+ "<div style=\"right:0px; top:15px; width:4px; height:4px; background-image: {10}\" ></div>"
				+ ""
				+ ""
				+ "</div>"
				+ "</div>"
				)
		SafeHtml cell(String grandParentStyleClass, String parentStyleClass,
				String aStyleClass, String value, String quickTipText, String colorHex, String backgroundImage,
				String icon1, String icon2, String icon3, String icon4);
	}
	
	protected EventBus eventBus;
	private int i;
	private TermsGrid termsGrid;
	protected static Templates templates = GWT.create(Templates.class);
	private boolean highlight = false;

	public AttachedCell(EventBus eventBus, TermsGrid termsGrid, int i) {
		this.eventBus = eventBus;
		this.termsGrid = termsGrid;
		this.i = i;
		
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

	/**
	 * create context menu items for cells such as Remove items
	 */
	@Override
	protected Menu createContextMenu(int columnIndex, int rowIndex) {
		final Row row = termsGrid.getRow(rowIndex);
		final Edge r = row.getAttached().get(columnIndex - 1);
		Menu menu = new Menu();
		MenuItem filterItem = new MenuItem("Filter: " + r.getDest());
		Menu filterMenu = new Menu();
		filterItem.setSubMenu(filterMenu);
		
		for(final FilterTarget filterTarget : FilterTarget.values()) {
			MenuItem menuItem = new MenuItem(filterTarget.getDisplayName());
			menuItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					eventBus.fireEvent(new FilterEvent(r.getDest().getValue(), filterTarget, Type.values()));
				}
			});
			filterMenu.add(menuItem);
		}
		MenuItem removeItem = new MenuItem("Remove this " + termsGrid.getType().getTargetLabel());
		removeItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				OntologyGraph g = ModelController.getCollection().getGraph();
				if(g.isClosedRelations(r.getDest(), termsGrid.getType())) {
					Alerter.showAlert("Create Relation", "Can not create relation for a closed row.");
					return;
				}
				
				if(g.getInRelations(r.getDest(), termsGrid.getType()).size() <= 1) {
					if(g.getOutRelations(r.getDest(), termsGrid.getType()).isEmpty()) {
						termsGrid.fire(new RemoveRelationEvent(false, r));
					} else {
						doAskForRecursiveRemoval(r);
					}
				} else {
					termsGrid.fire(new RemoveRelationEvent(false, r));
				}
			}
		});
		MenuItem context = new MenuItem("Show Term Context");
		context.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				termsGrid.fire(new SelectTermEvent(r.getDest().getValue()));
			}
		});
		menu.add(removeItem);
		menu.add(filterItem);
		menu.add(context);
		return menu;
	}
	
	protected void doAskForRecursiveRemoval(final Edge relation) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		List<Vertex> targets = new LinkedList<Vertex>();
		for(Edge r : g.getOutRelations(relation.getDest(), termsGrid.getType())) 
			targets.add(r.getDest());
		final MessageBox box = Alerter.showYesNoCancelConfirm("Remove " + termsGrid.getType().getTargetLabel(), 
				"You are about to remove " + termsGrid.getType().getTargetLabel() + "<i>" + relation.getDest() + "</i>"
				+ " from <i>" + relation.getSrc() + "</i>.\n" +
				"Do you want to remove all " + termsGrid.getType().getTargetLabelPlural() + " of <i>" + relation.getDest() + "</i> (Yes)" +
				" or, where they do not have multiple " + termsGrid.getType().getSourceLabelPlural() + ", make them instead a "
						+ "" + termsGrid.getType().getTargetLabel() + " of <i>" + relation.getSrc() + "</i> (No)?");
		box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				termsGrid.fire(new RemoveRelationEvent(true, relation));
				/*for(GwtEvent<Handler> e : createRemoveEvents(true, relation)) {
					termsGrid.fire(e);
				}*/
				box.hide();
			}
		});
		box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				termsGrid.fire(new RemoveRelationEvent(false, relation));
				/*for(GwtEvent<Handler> e : createRemoveEvents(false, relation)) {
					termsGrid.fire(e);
				}*/
				box.hide();
			}
		});
		box.getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				box.hide();
			}
		});
	}

	/*private List<GwtEvent> createRemoveEvents(boolean recursive, Relation relation) {
		List<GwtEvent> result = new LinkedList<GwtEvent>();
		if(recursive) {
			OntologyGraph graph = ModelController.getCollection().getGraph();
			
			List<Edge> targetOutEdges = graph.getOutEdges(relation.getTarget(), termsGrid.getType());
			if(targetOutEdges.isEmpty()) {
				result.add(new RemoveRelationEvent(relation));
			} else {
				for(Edge edge : targetOutEdges) {
					result.addAll(createRemoveEvents(recursive, graph.getRelation(edge)));
				}
			}
		} else {
			OntologyGraph graph = ModelController.getCollection().getGraph();
			
			List<Edge> targetOutEdges = graph.getOutEdges(relation.getTarget(), termsGrid.getType());
			if(targetOutEdges.isEmpty()) {
				result.add(new RemoveRelationEvent(relation));
			} else {
				for(Edge edge : targetOutEdges) {
					Relation childRelation = graph.getRelation(edge);
					childRelation.getTarget();
					result.add(new CreateRelationEvent(new Relation(relation.getSource(), childRelation.getTarget(), edge)));
					result.add(new RemoveRelationEvent(relation));
				}
			}
		}
		return result;
	}*/

	@Override
	public void render(Context context, Row value, final SafeHtmlBuilder sb) {
		if(value.getAttached().isEmpty() || value.getAttached().size() <= i)
			return;
		final Edge r = value.getAttached().get(i);
		String backgroundColor = "";// "#FFFFFF";
		OntologyGraph g = ModelController.getCollection().getGraph();
		
		if(r.getDest().getValue().equals("f"))
			System.out.println();
		
		
		boolean closed = highlight && ModelController.getCollection().getGraph().isClosedRelations(r.getDest(), termsGrid.getType());
		boolean ordered = highlight && ModelController.getCollection().getGraph().hasOrderedEdges(r.getDest(), termsGrid.getType());
		boolean multipleInComing = highlight && ModelController.getCollection().getGraph().getInRelations(r.getDest(), 
				termsGrid.getType()).size() > 1;
		
		
				
		String icon1 = "", icon2 = "", icon3 = "", icon4 = "";
		if(closed)
			icon1 = "url(" + cellImages.blue().getSafeUri().asString() + ")";
		if(ordered)
			icon2 = "url(" + cellImages.red().getSafeUri().asString() + ")";
		if(multipleInComing)
			icon3 = "url(" + cellImages.green().getSafeUri().asString() + ")";
		switch(r.getOrigin()) {
			case IMPORT:
				icon4 =  "url(" + cellImages.black().getSafeUri().asString() + ")";
				break;
			case USER:
				break;
			default:
				break;
		}
		SafeHtml rendered = templates.cell("", columnHeaderStyles.headInner(),
				columnHeaderStyles.headButton(), r.getDest().getValue(), "", "", backgroundColor, 
				icon1, icon2, icon3, icon4);
		sb.append(rendered);
	}
}
