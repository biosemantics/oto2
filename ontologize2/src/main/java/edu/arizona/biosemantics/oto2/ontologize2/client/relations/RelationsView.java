package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.ShowRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class RelationsView extends SimpleContainer {

	private EventBus eventBus;
	private TabPanel tabPanel;
	private PartsGrid partsGrid;
	private SubclassesGrid subclassGrid;
	private SynonymsGrid synonymGrid;

	public RelationsView(final EventBus eventBus) {
		this.eventBus = eventBus;
		
		partsGrid = new PartsGrid(eventBus);
		subclassGrid = new SubclassesGrid(eventBus);
		synonymGrid = new SynonymsGrid(eventBus);
		
		/*VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(subclassGrid.asWidget(), new VerticalLayoutData(1, 0.33));
		vlc.add(partsGrid.asWidget(), new VerticalLayoutData(1, 0.33));
		vlc.add(synonymGrid.asWidget(), new VerticalLayoutData(1, 0.33));
		this.add(vlc);*/
		
		tabPanel = new TabPanel();
		/*tabPanel.addSelectionHandler(new SelectionHandler<Widget>() { //bug https://www.sencha.com/forum/showthread.php?285982-Grid-ColumnHeader-Menu-missing
	            @Override
	            public void onSelection(SelectionEvent<Widget> event) {
	            	if(event.getSelectedItem().equals(subclassGrid.asWidget()))
	            		subclassGrid.refreshHeader();
	            	if(event.getSelectedItem().equals(partsGrid.asWidget()))
	            		partsGrid.refreshHeader();
	            	if(event.getSelectedItem().equals(synonymGrid.asWidget()))
	            		synonymGrid.refreshHeader();
	            }
	        });*/
		tabPanel.setTabScroll(true);
		tabPanel.setAnimScroll(true);
		tabPanel.add(subclassGrid, new TabItemConfig("Categories", false));
		tabPanel.add(partsGrid, new TabItemConfig("Parts", false));
		tabPanel.add(synonymGrid, new TabItemConfig("Synonyms", false));
		this.add(tabPanel);
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(ShowRelationsEvent.TYPE, new ShowRelationsEvent.Handler() {
			@Override
			public void onShow(ShowRelationsEvent event) {
				switch(event.getType()) {
				case PART_OF:
					tabPanel.setActiveWidget(partsGrid);
					break;
				case SUBCLASS_OF:
					tabPanel.setActiveWidget(subclassGrid);
					break;
				case SYNONYM_OF:
					tabPanel.setActiveWidget(synonymGrid);
					break;
				default:
					break;
				}
			}
		});
	}

}
