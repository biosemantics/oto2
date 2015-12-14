package edu.arizona.biosemantics.oto2.ontologize.client.layout;

import java.util.List;

import org.eclipse.jdt.core.dom.ThisExpression;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.content.BioportalSubmissionsView;
import edu.arizona.biosemantics.oto2.ontologize.client.content.CreateBioportalSubmissionView;
import edu.arizona.biosemantics.oto2.ontologize.client.content.CreateLocalSubmissionView;
import edu.arizona.biosemantics.oto2.ontologize.client.content.LocalSubmissionsView;
import edu.arizona.biosemantics.oto2.ontologize.client.content.ontologyview.OntologyView;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologySynonymsSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.hierarchy.HierarchyView;
import edu.arizona.biosemantics.oto2.ontologize.client.info.TermInfoView;
import edu.arizona.biosemantics.oto2.ontologize.client.order.OrderView;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class ContentView extends BorderLayoutContainer {

	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	private int portalColumnCount = 9;
	//private TermsView termsView;
	//private LabelsView labelsView;
	private TermInfoView termInfoView;
	private EventBus eventBus;
	private TabPanel tabPanel;
	private CreateLocalSubmissionView createLocalSubmissionView;
	private LocalSubmissionsView localSubmissionsView;
	private OntologyView ontologyView;
	private CreateBioportalSubmissionView createBioportalSubmissionView;
	private BioportalSubmissionsView bioportalSubmissionsView;

	public ContentView(EventBus eventBus) {
		this.eventBus = eventBus;
		/*SelectionHandler<Widget> handler = new SelectionHandler<Widget>() {
			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				TabPanel panel = (TabPanel) event.getSource();
				Widget w = event.getSelectedItem();
				TabItemConfig config = panel.getConfig(w);
			}
		};*/

		ontologyView = new OntologyView(eventBus);
		tabPanel = new TabPanel();
		createLocalSubmissionView = new CreateLocalSubmissionView(eventBus);
		localSubmissionsView = new LocalSubmissionsView(eventBus);
		createBioportalSubmissionView = new CreateBioportalSubmissionView(eventBus);
		bioportalSubmissionsView = new BioportalSubmissionsView(eventBus);
		
		//tabPanel.addSelectionHandler(handler);
		tabPanel.setWidth(450);
		TabItemConfig createLocalSubmission = new TabItemConfig("Create Local Submission");
		createLocalSubmission.setEnabled(true);
		tabPanel.add(createLocalSubmissionView, createLocalSubmission);
		
		TabItemConfig localSubmissions = new TabItemConfig("Local Submissions");
		localSubmissions.setEnabled(true);
		tabPanel.add(localSubmissionsView, localSubmissions);
		
		TabItemConfig viewLocalSubmission = new TabItemConfig("View Local Ontology");
		viewLocalSubmission.setEnabled(true);
		tabPanel.add(ontologyView, viewLocalSubmission);
		
		TabItemConfig createBioportalSubmission = new TabItemConfig("Create Bioportal Submission");
		createBioportalSubmission.setEnabled(true);
		tabPanel.add(createBioportalSubmissionView, createBioportalSubmission);
		
		TabItemConfig bioportalSubmissions = new TabItemConfig("Bioportal Submissions");
		bioportalSubmissions.setEnabled(true);
		tabPanel.add(bioportalSubmissionsView, bioportalSubmissions);
		
		/*TabItemConfig hierarchyConfig = new TabItemConfig("Hierarchy");
		hierarchyConfig.setEnabled(false);
		tabPanel.add(new HierarchyView(eventBus), hierarchyConfig);
		TabItemConfig orderConfig = new TabItemConfig("Orders");
		orderConfig.setEnabled(false);
		tabPanel.add(new OrderView(eventBus), orderConfig);*/
		/*TabItemConfig ontologyViewConfig = new TabItemConfig("Ontology View");
		ontologyViewConfig.setEnabled(true);
		tabPanel.add(ontologyView, ontologyViewConfig);
		*//*tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				if(event.getSelectedItem().equals(ontologyView.asWidget())) {
					ontologyView.refresh();
				}
			}
		});*/
		
		this.setWidget(tabPanel);
		
		//termsView = new TermsView(eventBus);
		//labelsView = new LabelsView(eventBus, portalColumnCount);
		termInfoView = new TermInfoView(eventBus);

		/*ContentPanel cp = new ContentPanel();
		cp.setHeadingText("Terms to be Categorized");
		cp.add(termsView);
		BorderLayoutData d = new BorderLayoutData(.20);
		// d.setMargins(new Margins(0, 1, 1, 1));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		setWestWidget(cp, d);*/

		ContentPanel cp = new ContentPanel();
		cp.setHeadingText("Ontologize");
		cp.add(tabPanel);
		BorderLayoutData d = new BorderLayoutData();
		d.setMargins(new Margins(0, 0, 0, 0));
		setCenterWidget(cp, d);

		cp = new ContentPanel();
		cp.setHeadingText("Term Information");
		cp.add(termInfoView);
		d = new BorderLayoutData(.40);
		d.setMargins(new Margins(0, 0, 20, 0));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		setSouthWidget(cp, d);

		// cp = new ContentPanel();
		/*
		 * cp.setHeadingText("Search"); d = new BorderLayoutData(.20);
		 * //d.setMargins(new Margins(1)); d.setCollapsible(true);
		 * d.setSplit(true); d.setCollapseMini(true);
		 * setNorthWidget(getMenu(), d);
		 */
		
		bindEvents();
	}

	private void bindEvents() {
		//http://stackoverflow.com/questions/24915856/gxt-3-1-0-sorting-dropdown-missing-from-grid-header
		tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				if(event.getSelectedItem().equals(localSubmissionsView.asWidget())) {
					localSubmissionsView.refreshGridHeaders();
				} else if(event.getSelectedItem().equals(bioportalSubmissionsView.asWidget())) {
					bioportalSubmissionsView.refreshGridHeaders();
				}
			}
		});
		/*eventBus.addHandler(RefreshSubmissionsEvent.TYPE, new RefreshSubmissionsEvent.Handler() {
			@Override
			public void onSelect(RefreshSubmissionsEvent event) {
				refreshSynonymSubmissions();
				refreshClassSubmissions();
			}
		});*/
	}
}