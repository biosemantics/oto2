package edu.arizona.biosemantics.oto2.steps.client.toontology;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelAppearance;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelBottomAppearance;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import edu.arizona.biosemantics.oto2.steps.client.common.Alerter;
import edu.arizona.biosemantics.oto2.steps.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.OntologyClassSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.OntologySynonymSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.RefreshSubmissionsEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.SubmitClassEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.SubmitSynonymEvent;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmissionProperties;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmissionStatusProperties;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmissionProperties;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmissionStatusProperties;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.IToOntologyServiceAsync;

public class SubmissionsView implements IsWidget {

	private EventBus eventBus;
	private Collection collection;
	
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	private OntologyClassSubmissionProperties ontologyClassSubmissionProperties = GWT.create(OntologyClassSubmissionProperties.class);
	private OntologySynonymSubmissionProperties ontologySynonymSubmissionProperties = GWT.create(OntologySynonymSubmissionProperties.class);
	private OntologySynonymSubmissionStatusProperties ontologySynonymSubmissionStatusProperties = GWT.create(OntologySynonymSubmissionStatusProperties.class);
	private OntologyClassSubmissionStatusProperties ontologyClassSubmissionStatusProperties = GWT.create(OntologyClassSubmissionStatusProperties.class);
	
	private ListStore<OntologyClassSubmission> classSubmissionStore =
			new ListStore<OntologyClassSubmission>(ontologyClassSubmissionProperties.key());
	private ListStore<OntologySynonymSubmission> synonymSubmissionStore =
			new ListStore<OntologySynonymSubmission>(ontologySynonymSubmissionProperties.key());
	private TabPanel tabPanel;
	private ClassSubmissionsGrid classSubmissionsGrid;
	private SynonymSubmissionsGrid synonymSubmissionsGrid;
	
	public SubmissionsView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		tabPanel = new TabPanel(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		classSubmissionsGrid = createOntologyClassSubmissionGrid();
		synonymSubmissionsGrid = createOntologySynonymSubmissionGrid();
		tabPanel.add(createOntologyClassSubmissionGrid(), "Class");
		tabPanel.add(createOntologySynonymSubmissionGrid(), "Synonym");
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				collection = event.getCollection();
				refreshClassSubmissions();
				refreshSynonymSubmissions();
			}
		});
		eventBus.addHandler(SubmitClassEvent.TYPE, new SubmitClassEvent.Handler() {
			@Override
			public void onSubmission(SubmitClassEvent event) {
				refreshClassSubmissions();
			}
		});
		eventBus.addHandler(SubmitSynonymEvent.TYPE, new SubmitSynonymEvent.Handler() {
			@Override
			public void onSubmission(SubmitSynonymEvent event) {
				refreshSynonymSubmissions();
			}
		});
		eventBus.addHandler(RefreshSubmissionsEvent.TYPE, new RefreshSubmissionsEvent.Handler() {
			@Override
			public void onSelect(RefreshSubmissionsEvent event) {
				refreshSynonymSubmissions();
				refreshClassSubmissions();
			}
		});
		classSubmissionsGrid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<OntologyClassSubmission>() {

			@Override
			public void onSelectionChanged(
					SelectionChangedEvent<OntologyClassSubmission> event) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		classSubmissionsGrid.getSelectionModel().addSelectionHandler(new SelectionHandler<OntologyClassSubmission>() {
			@Override
			public void onSelection(SelectionEvent<OntologyClassSubmission> event) {
				eventBus.fireEvent(new OntologyClassSubmissionSelectEvent(event.getSelectedItem()));
			}
		});
		synonymSubmissionsGrid.getSelectionModel().addSelectionHandler(new SelectionHandler<OntologySynonymSubmission>() {
			@Override
			public void onSelection(SelectionEvent<OntologySynonymSubmission> event) {
				eventBus.fireEvent(new OntologySynonymSubmissionSelectEvent(event.getSelectedItem()));
			}
		});
	}

	protected void refreshClassSubmissions() {
		final MessageBox loadingBox = Alerter.startLoading();
		toOntologyService.getClassSubmissions(collection, new AsyncCallback<List<OntologyClassSubmission>>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.stopLoading(loadingBox);
				Alerter.failedToRefreshSubmissions();
			}
			@Override
			public void onSuccess(List<OntologyClassSubmission> result) {
				classSubmissionStore.clear();
				classSubmissionStore.addAll(result);
				Alerter.stopLoading(loadingBox);
			}
		});
	}

	protected void refreshSynonymSubmissions() {
		final MessageBox loadingBox = Alerter.startLoading();
		toOntologyService.getSynonymSubmissions(collection, new AsyncCallback<List<OntologySynonymSubmission>>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.stopLoading(loadingBox);
				Alerter.failedToRefreshSubmissions();
			}
			@Override
			public void onSuccess(List<OntologySynonymSubmission> result) {
				synonymSubmissionStore.clear();
				synonymSubmissionStore.addAll(result);
				Alerter.stopLoading(loadingBox);
			}
		});
	}

	private SynonymSubmissionsGrid createOntologySynonymSubmissionGrid() {
		return new SynonymSubmissionsGrid(eventBus, synonymSubmissionStore);
	}

	private ClassSubmissionsGrid createOntologyClassSubmissionGrid() {
		return new ClassSubmissionsGrid(eventBus, classSubmissionStore);
	}

	@Override
	public Widget asWidget() {
		return tabPanel;
	}

}
