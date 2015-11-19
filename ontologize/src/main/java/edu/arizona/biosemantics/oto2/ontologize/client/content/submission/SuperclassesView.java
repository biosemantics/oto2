package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import edu.arizona.biosemantics.oto2.ontologize.client.content.ontologyview.OntologyView;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.BucketTreeNode;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.TermTreeNode;

public class SuperclassesView implements IsWidget {

	private OntologyClassSubmissionProperties ontologyClassSubmissionProperties = GWT.create(OntologyClassSubmissionProperties.class);
	
	private VerticalLayoutContainer verticalLayoutContainer;
	private OntologyView ontologyView;
	private TextField superclassTextField = new TextField();
	
	private ListStore<OntologyClassSubmission> listStore;
	private ListView<OntologyClassSubmission, String> listView;

	private EventBus eventBus;

	private Collection collection; 

	public SuperclassesView(EventBus eventBus) {
		this.eventBus = eventBus;
		this.ontologyView = new OntologyView(eventBus);
		this.ontologyView.setPartOfChecked(false);
		this.ontologyView.setSynonymChecked(false);
		this.ontologyView.setSuperclassChecked(true);
		this.listStore = new ListStore<OntologyClassSubmission>(ontologyClassSubmissionProperties.key());
		this.listView = new ListView<OntologyClassSubmission, String>(listStore, ontologyClassSubmissionProperties.submissionTerm());
		this.verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.getScrollSupport().setScrollMode(ScrollMode.AUTO);
		
		verticalLayoutContainer.add(new FieldLabel(ontologyView, "Ontology View"), new VerticalLayoutData(1, 550));
		verticalLayoutContainer.add(new FieldLabel(listView, "Candidate Superclasses"), new VerticalLayoutData(1, 150));
		verticalLayoutContainer.add(new FieldLabel(superclassTextField, "New Superclass"), new VerticalLayoutData(1, 1));
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				SuperclassesView.this.collection = event.getCollection();
			}
		});
		eventBus.addHandler(RefreshOntologyClassSubmissionsEvent.TYPE, new RefreshOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onSelect(RefreshOntologyClassSubmissionsEvent event) {
				listStore.clear();
				listStore.addAll(event.getOntologyClassSubmissions());
			}
		});
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
		return superclassTextField.getValue().trim();
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
