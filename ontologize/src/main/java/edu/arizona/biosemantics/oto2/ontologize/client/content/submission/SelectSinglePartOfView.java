package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.StoreDataChangeHandler;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreHandlers;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent.StoreUpdateHandler;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectPartOfEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class SelectSinglePartOfView implements IsWidget {
	
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);

	private VerticalLayoutContainer formContainer;

	private ListView<PartOf, PartOf> listView;
	private ListStore<PartOf> store;
	private TextButton setButton = new TextButton("Add");
	private TextButton removeButton = new TextButton("Clear");

	protected Collection collection;
	private EventBus eventBus;
	private AddPartOfDialog addPartOfDialog;

	private Map<Integer, OntologyClassSubmission> classSubmissions = new HashMap<Integer, OntologyClassSubmission>();
	
	public SelectSinglePartOfView(EventBus eventBus) {
		this.eventBus = eventBus;
		addPartOfDialog = new AddPartOfDialog(eventBus);
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
		horizontalLayoutContainer.add(listView, new HorizontalLayoutData(0.7, 25));
	    //VerticalLayoutContainer partOfVertical = new VerticalLayoutContainer();
	    //horizontalLayoutContainer.add(partOfVertical, new HorizontalLayoutData(0.3, -1));
	    horizontalLayoutContainer.add(setButton, new HorizontalLayoutData(0.15, -1));
	    horizontalLayoutContainer.add(removeButton, new HorizontalLayoutData(0.15, -1));
	    formContainer.add(new FieldLabel(horizontalLayoutContainer, "Part Of (IRI or term)"), new VerticalLayoutData(1, 75));
	    
	    bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(LoadOntologyClassSubmissionsEvent.TYPE, new LoadOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onSelect(LoadOntologyClassSubmissionsEvent event) {
				classSubmissions.clear();
				for(OntologyClassSubmission submission : event.getOntologyClassSubmissions())
					classSubmissions.put(submission.getId(), submission);
			}
		});
		eventBus.addHandler(CreateOntologyClassSubmissionEvent.TYPE, new CreateOntologyClassSubmissionEvent.Handler() {
			@Override
			public void onSubmission(CreateOntologyClassSubmissionEvent event) {
				for(OntologyClassSubmission submission : event.getClassSubmissions())
					classSubmissions.put(submission.getId(), submission);
			}
		});
		eventBus.addHandler(RemoveOntologyClassSubmissionsEvent.TYPE, new RemoveOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onRemove(RemoveOntologyClassSubmissionsEvent event) {
				for(OntologyClassSubmission submission : event.getOntologyClassSubmissions())
					classSubmissions.remove(submission.getId());
			}
		});
		
		store.addStoreDataChangeHandler(new StoreDataChangeHandler<PartOf>() {
			@Override
			public void onDataChange(StoreDataChangeEvent<PartOf> event) {
				if(store.size() == 0) {
					setButton.setEnabled(true);
					removeButton.setEnabled(false);
				} else {
					setButton.setEnabled(false);
					removeButton.setEnabled(true);
				}
			}
		});
		store.addStoreUpdateHandler(new StoreUpdateHandler<PartOf>() {

			@Override
			public void onUpdate(StoreUpdateEvent<PartOf> event) {
				if(store.size() == 0) {
					setButton.setEnabled(true);
					removeButton.setEnabled(false);
				} else {
					setButton.setEnabled(false);
					removeButton.setEnabled(true);
				}
			}
			
		});
		store.addStoreHandlers(new StoreHandlers<PartOf>() {
			@Override
			public void onAdd(StoreAddEvent<PartOf> event) {
				enableButtons();
			}
			@Override
			public void onRemove(StoreRemoveEvent<PartOf> event) {
				enableButtons();
			}
			@Override
			public void onFilter(StoreFilterEvent<PartOf> event) {
				enableButtons();
			}
			@Override
			public void onClear(StoreClearEvent<PartOf> event) {
				enableButtons();
			}
			@Override
			public void onUpdate(StoreUpdateEvent<PartOf> event) {
				enableButtons();
			}
			@Override
			public void onDataChange(StoreDataChangeEvent<PartOf> event) {
				enableButtons();
			}
			@Override
			public void onRecordChange(StoreRecordChangeEvent<PartOf> event) {
				enableButtons();
			}
			@Override
			public void onSort(StoreSortEvent<PartOf> event) {
				enableButtons();
			}
		});
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				SelectSinglePartOfView.this.collection = event.getCollection();
			}
		});
		eventBus.addHandler(SelectPartOfEvent.TYPE, new SelectPartOfEvent.Handler() {
			@Override
			public void onSelect(SelectPartOfEvent event) {
				addPartOfToStore(new PartOf(event.getSubmission()));
			}
		});
		
		setButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				addPartOfDialog.show();
			}
		});
		addPartOfDialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final String value = addPartOfDialog.getValue();
				if(value == null || value.isEmpty())
					Alerter.inputRequired();
				else {
					final PartOf partOf = new PartOf(); 
					if(value.startsWith("http")) {
						final MessageBox box = Alerter.startLoading();
						toOntologyService.isSupportedIRI(collection, value, new AsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.failedToCheckIRI(caught);
								Alerter.stopLoading(box);
							}
							@Override
							public void onSuccess(Boolean result) {
								if(result) {
									partOf.setIri(value);
									addPartOfToStore(partOf);
									addPartOfDialog.hide();
								} else {
									Alerter.unsupportedIRI();
								}
								Alerter.stopLoading(box);
							}
						});
					/*} else if(ontologyClassSubmissionRetriever.getSubmissionOfLabelOrIri(partOf, classSubmissions) != null) {
						partOf.setLabel(value);
						addPartOfToStore(superclass);
						addPartOfDialog.hide();
					} else {
						Alerter.unupportedPartOf();
					}*/
					} else {
						partOf.setLabel(value);
						addPartOfToStore(partOf);
						addPartOfDialog.hide();
					}
				}
			}
		});
		removeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				store.clear();
			}
		});
	}

	protected void enableButtons() {
		if(store.size() == 0) {
			setButton.setEnabled(true);
			removeButton.setEnabled(false);
		} else {
			setButton.setEnabled(false);
			removeButton.setEnabled(true);
		}
	}

	public void addPartOfToStore(final PartOf partOf) {
		store.clear();
		if(!partOf.hasIri()) {
			OntologyClassSubmission ontologyClassSubmission = OntologyClassSubmissionRetriever.getSubmissionOfLabelOrIri(partOf, classSubmissions.values());
			if(ontologyClassSubmission != null)
				partOf.setIri(ontologyClassSubmission.getIri());
			store.add(partOf);
		} else if(!partOf.hasLabel()) {
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

	@Override
	public Widget asWidget() {
		return formContainer;
	}

	public List<PartOf> getPartsOfs() {
		return new ArrayList<PartOf>(store.getAll());
	}

	public void setPartOfs(List<PartOf> partOfs) {
		store.clear();
		for(PartOf partOf : partOfs) 
			this.addPartOfToStore(partOf);
	}

	public void clear() {
		store.clear();
	}

	public void setSubmissionType(Type type) {
		this.addPartOfDialog.setSubmissionType(type);
	}
}