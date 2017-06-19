/*package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectPartOfEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class SelectMultiplePartOfView implements IsWidget {
	
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);

	private VerticalLayoutContainer formContainer;

	private ListView<PartOf, PartOf> listView;
	private ListStore<PartOf> store;
	private TextButton addButton = new TextButton("Add");
	private TextButton removeButton = new TextButton("Remove");
	private TextButton clearButton = new TextButton("Clear");

	protected Collection collection;
	private EventBus eventBus;
	private AddPartOfDialog addPartOfDialog = new AddPartOfDialog();
	
	public SelectMultiplePartOfView(EventBus eventBus) {
		this.eventBus = eventBus;
		store = new ListStore<PartOf>(new ModelKeyProvider<PartOf>() {
			@Override
			public String getKey(PartOf item) {
				return item.getLabel();
			}
	    });
	 	listView = new ListView<PartOf, PartOf>(store, new IdentityValueProvider<PartOf>());	    
	    listView.setCell(new AbstractCell<PartOf>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	PartOf value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromTrustedString("<div qtip=\"" + value.toString() + "\">" + (value.hasLabel() ? value.getLabel() : value.toString()) + "</div>"));
			}
	    });
		 
	    formContainer = new VerticalLayoutContainer();
		HorizontalLayoutContainer horizontalLayoutContainer = new HorizontalLayoutContainer();
		horizontalLayoutContainer.add(listView, new HorizontalLayoutData(0.7, 75));
	    VerticalLayoutContainer partOfVertical = new VerticalLayoutContainer();
	    horizontalLayoutContainer.add(partOfVertical, new HorizontalLayoutData(0.3, -1));
	    partOfVertical.add(addButton, new VerticalLayoutData(1, -1));
	    partOfVertical.add(removeButton, new VerticalLayoutData(1, -1));
	    partOfVertical.add(clearButton, new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(horizontalLayoutContainer, "Part Of (IRI or term)"), new VerticalLayoutData(1, 75));
	    
	    bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				SelectMultiplePartOfView.this.collection = event.getCollection();
			}
		});
		eventBus.addHandler(SelectPartOfEvent.TYPE, new SelectPartOfEvent.Handler() {
			@Override
			public void onSelect(SelectPartOfEvent event) {
				addPartOfToStore(event.getSubmission());
			}
		});
		addButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				addPartOfDialog.setCollection(collection);
				addPartOfDialog.show();
				addPartOfDialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						PartOf partOf = new PartOf();
						if(addPartOfDialog.getValue().startsWith("http")) {
							partOf.setIri(addPartOfDialog.getValue());
						} else {
							partOf.setLabel(addPartOfDialog.getValue());
						}
						addPartOfToStore(partOf);
						addPartOfDialog.hide();
					}
				});
			}
		});
		removeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for(PartOf remove : listView.getSelectionModel().getSelectedItems()) {
					store.remove(remove);
				}
			}
		});
		clearButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				clear();
			}
		});
	}

	public void addPartOfToStore(OntologyClassSubmission submission) {
		store.add(new PartOf(submission));
	}

	public void addPartOfToStore(final PartOf partOf) {
		if(!partOf.hasIri())
			store.add(partOf);
		else if(!partOf.hasLabel()) {
			final MessageBox box = Alerter.startLoading();
			toOntologyService.getClassLabel(collection, partOf.getIri(), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToGetClassLabel();
					Alerter.stopLoading(box);
				}
				@Override
				public void onSuccess(String result) {
					partOf.setLabel(result);
					store.add(partOf);
					Alerter.stopLoading(box);
				}
			});
		} else {
			store.add(partOf);
		}
	}
	
	private void addTypeToSupreclassStore(Type type) {
		this.store.add(new PartOf(type.getIRI(), type.getLabel()));
	}

	@Override
	public Widget asWidget() {
		return formContainer;
	}

	public List<PartOf> getPartsOfs() {
		return new ArrayList<PartOf>(store.getAll());
	}

	public void clear() {
		store.clear();
	}

	public void setPartOfs(List<PartOf> partOfs) {
		clear();
		store.addAll(partOfs);
	}
}*/