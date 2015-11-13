package edu.arizona.biosemantics.oto2.ontologize.client.toontology;

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

import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.OntologyClassSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.OntologySynonymSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectPartOfEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSuperclassEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologySynonymsSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.toontology.submit.SubmitBioportalView;
import edu.arizona.biosemantics.oto2.ontologize.client.toontology.submit.SubmitLocalView;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionStatusProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmissionProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmissionStatusProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class SubmissionsView implements IsWidget {

	private EventBus eventBus;
	private Collection collection;
	
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);

	private TabPanel tabPanel;
	private SubmissionsLocalView submissionsLocalView;
	private SubmissionsBioportalView submissionsBioportalView;
	
	public SubmissionsView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		//tabPanel = new TabPanel(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		/*tabPanel = new TabPanel();
		classSubmissionsGrid = createOntologyClassSubmissionGrid();
		synonymSubmissionsGrid = createOntologySynonymSubmissionGrid();
		tabPanel.add(createOntologyClassSubmissionGrid(), "Class");
		tabPanel.add(createOntologySynonymSubmissionGrid(), "Synonym");*/
		
		tabPanel = new TabPanel();
		
		submissionsLocalView = new SubmissionsLocalView(eventBus);
		submissionsBioportalView = new SubmissionsBioportalView(eventBus);
		
		tabPanel.add(submissionsLocalView, "Local");
		tabPanel.add(submissionsBioportalView, "Bioportal");
		
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
		eventBus.addHandler(CreateOntologyClassSubmissionEvent.TYPE, new CreateOntologyClassSubmissionEvent.Handler() {
			@Override
			public void onSubmission(CreateOntologyClassSubmissionEvent event) {
				refreshClassSubmissions();
			}
		});
		eventBus.addHandler(CreateOntologySynonymSubmissionEvent.TYPE, new CreateOntologySynonymSubmissionEvent.Handler() {
			@Override
			public void onSubmission(CreateOntologySynonymSubmissionEvent event) {
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
		
		eventBus.addHandler(RemoveOntologyClassSubmissionsEvent.TYPE, new RemoveOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onRemove(RemoveOntologyClassSubmissionsEvent event) {
				refreshClassSubmissions();
			}
		});
		eventBus.addHandler(RemoveOntologySynonymSubmissionsEvent.TYPE, new RemoveOntologySynonymSubmissionsEvent.Handler() {
			@Override
			public void onRemove(RemoveOntologySynonymSubmissionsEvent event) {
				refreshSynonymSubmissions();
			}
		});
		eventBus.addHandler(UpdateOntologyClassSubmissionsEvent.TYPE, new UpdateOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onUpdate(UpdateOntologyClassSubmissionsEvent event) {
				refreshClassSubmissions();
			}
		});
		eventBus.addHandler(UpdateOntologySynonymsSubmissionsEvent.TYPE, new UpdateOntologySynonymsSubmissionsEvent.Handler() {
			@Override
			public void onRemove(UpdateOntologySynonymsSubmissionsEvent event) {
				refreshSynonymSubmissions();
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
				eventBus.fireEvent(new RefreshOntologyClassSubmissionsEvent(result));
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
				Alerter.stopLoading(loadingBox);
				eventBus.fireEvent(new RefreshOntologySynonymSubmissionsEvent(result));
			}
		});
	}

	@Override
	public Widget asWidget() {
		return tabPanel;
	}

}
