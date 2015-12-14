package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import edu.arizona.biosemantics.oto2.ontologize.client.content.ontologyview.OntologyView;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.OntologyClassSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.layout.ModelController;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.BucketTreeNode;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOfTreeNode;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.TermTreeNode;


public class SuperclassesView implements IsWidget {
	private OntologyClassSubmissionProperties ontologyClassSubmissionProperties = GWT.create(OntologyClassSubmissionProperties.class);
	
	private VerticalLayoutContainer verticalLayoutContainer;
	private OntologyView ontologyView;
	private TextField superclassTextField = new TextField();
	
	private ListStore<OntologyClassSubmission> listStore;
	private ListView<OntologyClassSubmission, String> listView;

	private OntologyClassSubmission selected;

	public SuperclassesView(EventBus eventBus, final OntologyClassSubmission selected, Type type) {
		this.selected = selected;
		this.ontologyView = new OntologyView(eventBus);
		this.ontologyView.setPartOfChecked(false);
		this.ontologyView.setSynonymChecked(false);
		this.ontologyView.setSuperclassChecked(true);
		this.listStore = new ListStore<OntologyClassSubmission>(ontologyClassSubmissionProperties.key());
		listStore.setEnableFilters(true);
		this.listView = new ListView<OntologyClassSubmission, String>(listStore, ontologyClassSubmissionProperties.submissionTerm());
		this.verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.getScrollSupport().setScrollMode(ScrollMode.AUTO);
		
		verticalLayoutContainer.add(new FieldLabel(ontologyView, "Ontology View"), new VerticalLayoutData(1, 400));
		verticalLayoutContainer.add(new FieldLabel(listView, "Candidate Superclasses"), new VerticalLayoutData(1, 100));
		verticalLayoutContainer.add(new FieldLabel(superclassTextField, "New Superclass"), new VerticalLayoutData(1, 1));
		
		listStore.addAll(ModelController.getClassSubmissions().values());
		listStore.addFilter(new StoreFilter<OntologyClassSubmission>() {
			@Override
			public boolean select(Store<OntologyClassSubmission> store,	OntologyClassSubmission parent,	OntologyClassSubmission item) {
				return item.getId() != selected.getId();
			}
		});
		this.setSubmissionType(type);
		
		bindEvents();
	}

	private void bindEvents() {		
		listView.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<OntologyClassSubmission>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<OntologyClassSubmission> event) {
				if(event.getSelection().size() == 1)
					superclassTextField.setValue(event.getSelection().get(0).getSubmissionTerm());
			}
		});
	}
	
	@Override
	public Widget asWidget() {
		return verticalLayoutContainer;
	}

	public String getValue() {
		return superclassTextField.getValue() == null ? "" : superclassTextField.getValue().trim();
	}

	public void setSubmissionType(Type type) {
		switch(type) {
		case ENTITY:
			ontologyView.setEntityChecked(true);
			ontologyView.setQualityChecked(false);
			break;
		case QUALITY:
			ontologyView.setEntityChecked(false);
			ontologyView.setQualityChecked(true);
			break;
		default:
			ontologyView.setEntityChecked(true);
			ontologyView.setQualityChecked(true);
			break;
		}
	}

}
