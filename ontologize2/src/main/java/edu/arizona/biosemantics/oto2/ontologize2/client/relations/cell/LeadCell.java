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
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.cell.CellImages;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SelectTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.VisualizationConfigurationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;

public class LeadCell extends MenuExtendedCell<Vertex> {

	private static CellImages cellImages = GWT.create(CellImages.class);
	
	interface Templates extends SafeHtmlTemplates {
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
				+ "<div style=\"right:0px; top:0px; width:5px; height:5px; background-image: {7}\" ></div>"
				+ "<div style=\"right:0px; top:5px; width:5px; height:5px; background-image: {8}\" ></div>"
				+ "<div style=\"right:0px; top:10px; width:5px; height:5px; background-image: {9}\" ></div>"
				+ ""
				+ ""
				+ "</div>"
				+ "</div>"
				)
		SafeHtml cell(String grandParentStyleClass, String parentStyleClass,
				String aStyleClass, String value, String quickTipText, String colorHex, String backgroundImage,
				String icon1, String icon2, String icon3);
	}
	
	interface MenuCreator {
		Menu create(int rowIndex);
	}
	
	private ValueProvider<Vertex, String> valueProvider;
	private MenuCreator menuCreator;
	private TermsGrid termsGrid;
	protected static Templates templates = GWT.create(Templates.class);
	private boolean highlight;
	private EventBus eventBus;
	
	public LeadCell(EventBus eventBus, TermsGrid termsGrid, ValueProvider<Vertex, String> valueProvider, MenuCreator menuCreator) {
		this.eventBus = eventBus;
		this.termsGrid = termsGrid;
		this.valueProvider = valueProvider;
		this.menuCreator = menuCreator;
		
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
	protected Menu createContextMenu(int column, int rowIndex) {
		return menuCreator.create(rowIndex);
	}	
	
	@Override
	public void render(Context context, Vertex value, SafeHtmlBuilder sb) {
		boolean closed = highlight && ModelController.getCollection().getGraph().isClosedRelations(value, termsGrid.getType());
		boolean ordered = highlight && ModelController.getCollection().getGraph().hasOrderedEdges(value, termsGrid.getType());
		boolean multipleInComing = highlight && ModelController.getCollection().getGraph().getInRelations(value, 
						termsGrid.getType()).size() > 1;
				
		String icon1 = "", icon2 = "", icon3 = "";
		if(value.getValue().equals("m0"))
			System.out.println();
		if(closed)
			icon1 = "url(" + cellImages.blue().getSafeUri().asString() + ")";
		if(ordered)
			icon2 = "url(" + cellImages.red().getSafeUri().asString() + ")";
		if(multipleInComing)
			icon3 = "url(" + cellImages.green().getSafeUri().asString() + ")";
		
		SafeHtml rendered = templates.cell("", columnHeaderStyles.headInner(),
				columnHeaderStyles.headButton(), valueProvider.getValue(value), "", "", "", icon1, icon2, icon3);
		sb.append(rendered);
	}

}
