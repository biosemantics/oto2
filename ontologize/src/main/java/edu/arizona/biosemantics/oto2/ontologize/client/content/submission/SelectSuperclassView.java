package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreHandlers;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
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
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.OntologyClassSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSuperclassEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.layout.ModelController;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class SelectSuperclassView implements IsWidget {
	
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);

	private VerticalLayoutContainer formContainer;

	private ListView<Superclass, Superclass> listView;
	private ListStore<Superclass> store;
	private TextButton add = new TextButton("Add");
	private TextButton remove = new TextButton("Remove");
	private TextButton clear = new TextButton("Clear");
	private EventBus eventBus;
	private Type defaultSuperclass;
	private boolean showDefaultSuperclasses;

	protected OntologyClassSubmission selectedClassSubmission;

	private Type submissionType;
	
	public SelectSuperclassView(EventBus eventBus, boolean showDefaultSuperclasses) {
		this.eventBus = eventBus;
		this.showDefaultSuperclasses = showDefaultSuperclasses;
		
		store = new ListStore<Superclass>(new ModelKeyProvider<Superclass>() {
			@Override
			public String getKey(Superclass item) {
				return item.getLabel();
			}
	    });
	 	listView = new ListView<Superclass, Superclass>(store, new IdentityValueProvider<Superclass>());	    
	    listView.setCell(new AbstractCell<Superclass>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	Superclass value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromTrustedString("<div qtip=\"" + value.toString() + "\">" + (value.hasLabel() ? value.getLabel() : value.toString()) + "</div>"));
			}
	    });
		 
	    formContainer = new VerticalLayoutContainer();
		HorizontalLayoutContainer horizontalLayoutContainer = new HorizontalLayoutContainer();
		horizontalLayoutContainer.add(listView, new HorizontalLayoutData(0.7, 75));
	    VerticalLayoutContainer superclassVertical = new VerticalLayoutContainer();
	    horizontalLayoutContainer.add(superclassVertical, new HorizontalLayoutData(0.3, -1));
	    superclassVertical.add(add, new VerticalLayoutData(1, -1));
	    superclassVertical.add(remove, new VerticalLayoutData(1, -1));
	    remove.setEnabled(false);
	    superclassVertical.add(clear, new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(horizontalLayoutContainer, "Superclass (IRI or term)"), new VerticalLayoutData(1, 75));
	    
	    bindEvents();
	}
	
	private void bindEvents() {
		listView.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<Superclass>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<Superclass> event) {
				remove.setEnabled(!event.getSelection().isEmpty());
			}
		});
		store.addStoreHandlers(new StoreHandlers<Superclass>() {
			@Override
			public void onAdd(StoreAddEvent<Superclass> event) {
				enableButtons();
			}
			@Override
			public void onRemove(StoreRemoveEvent<Superclass> event) {
				enableButtons();
			}
			@Override
			public void onFilter(StoreFilterEvent<Superclass> event) {
				enableButtons();
			}
			@Override
			public void onClear(StoreClearEvent<Superclass> event) {
				enableButtons();
			}
			@Override
			public void onUpdate(StoreUpdateEvent<Superclass> event) {
				enableButtons();
			}
			@Override
			public void onDataChange(StoreDataChangeEvent<Superclass> event) {
				enableButtons();
			}
			@Override
			public void onRecordChange(StoreRecordChangeEvent<Superclass> event) {
				enableButtons();
			}
			@Override
			public void onSort(StoreSortEvent<Superclass> event) {
				enableButtons();
			}
		});
		eventBus.addHandler(SelectSuperclassEvent.TYPE, new SelectSuperclassEvent.Handler() {
			@Override
			public void onSelect(SelectSuperclassEvent event) {
				addSuperClassToStore(new Superclass(event.getSubmission()));
			}
		});
		
		add.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final AddSuperclassDialog dialog = new AddSuperclassDialog(eventBus, selectedClassSubmission, submissionType);
				dialog.show();
				
				dialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						final String value = dialog.getValue();
						if(value == null || value.isEmpty()) 
							Alerter.inputRequired();
						else {
							final Superclass superclass = new Superclass();
							if(value.startsWith("http")) {
								final MessageBox box = Alerter.startLoading();
								toOntologyService.isSupportedIRI(ModelController.getCollection(), value, new AsyncCallback<Boolean>() {
									@Override
									public void onFailure(Throwable caught) {
										Alerter.failedToCheckIRI(caught);
										Alerter.stopLoading(box);
									}
									@Override
									public void onSuccess(Boolean result) {
										if(result) {
											superclass.setIri(value);
											addSuperClassToStore(superclass);
											dialog.hide();
										} else {
											Alerter.unsupportedIRI();
										}
										Alerter.stopLoading(box);
									}
								});
							/*} else if(ontologyClassSubmissionRetriever.getSubmissionOfLabelOrIri(superclass, classSubmissions) != null) {
								superclass.setLabel(value);
								addSuperClassToStore(superclass);
								addSuperclassDialog.hide();
							} else {
								Alerter.unupportedSuperclass();
							}*/
							
							//allow creation of superclass submissions 'on the fly'
							} else {
								superclass.setLabel(value);
								addSuperClassToStore(superclass);
								dialog.hide();
							}
						}
					}
				});
			}
		});
		eventBus.addHandler(OntologyClassSubmissionSelectEvent.TYPE, new OntologyClassSubmissionSelectEvent.Handler() {
			@Override
			public void onSelect(OntologyClassSubmissionSelectEvent event) {
				SelectSuperclassView.this.selectedClassSubmission = event.getOntologyClassSubmission();
			}
		});
		remove.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for(Superclass remove : listView.getSelectionModel().getSelectedItems()) {
					if(defaultSuperclass != null && remove.getIri().equals(defaultSuperclass.getIRI())) {
						Alerter.cannotRemoveEntityOrQualitySuperclass();
					} else {
						store.remove(remove);
					}
				}
			}
		});
		clear.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				clearSuperclassesExceptHigherLevelClass();
			}
		});
	}

	protected void enableButtons() {
		if(store.size() == 0) {
			add.setEnabled(true);
			clear.setEnabled(false);
		} else {
			clear.setEnabled(true);
		}
	}

	public void addSuperClassToStore(final Superclass superclass) {
		if(!superclass.hasIri()) {
			OntologyClassSubmission ontologyClassSubmission = OntologyClassSubmissionRetriever.getSubmissionOfLabelOrIri(superclass, ModelController.getClassSubmissions().values());
			if(ontologyClassSubmission != null) 
				superclass.setIri(OntologyClassSubmissionRetriever.getSubmissionOfLabelOrIri(superclass, ModelController.getClassSubmissions().values()).getIri());
			store.add(superclass);
		} else if(!superclass.hasLabel()) {
			final MessageBox box = Alerter.startLoading();
			toOntologyService.getClassLabel(ModelController.getCollection(), superclass.getIri(), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToGetClassLabel();
					Alerter.stopLoading(box);
				}
				@Override
				public void onSuccess(String result) {
					superclass.setLabel(result);
					store.add(superclass);
					Alerter.stopLoading(box);
				}
			});
		} else {
			store.add(superclass);
		}
	}

	public void clearSuperclassesExceptHigherLevelClass() {
		store.clear();
		if(defaultSuperclass != null)
			addTypeToSupreclassStore(defaultSuperclass);
	}
	
	private void addTypeToSupreclassStore(Type type) {
		this.addSuperClassToStore(new Superclass(type));
	}

	@Override
	public Widget asWidget() {
		return formContainer;
	}
	
	public List<Superclass> getSuperclasses() {
		return new ArrayList<Superclass>(store.getAll());
	}

	public void setSuperclasses(List<Superclass> superclasses) {
		clearSuperclassesExceptHigherLevelClass();
		for(Superclass superclass : superclasses)
			if(defaultSuperclass != null && !superclass.getIri().equals(this.defaultSuperclass.getIRI()))
				this.addSuperClassToStore(superclass);
	}

	public void setSubmissionType(Type type) {
		this.submissionType = type;
		setSuperclassType(type);
	}

	private void setSuperclassType(Type type) {
		//remove old
		for(Superclass superclass : new ArrayList<Superclass>(this.store.getAll())) {
			if(defaultSuperclass != null && superclass.getIri() != null && superclass.getIri().equals(defaultSuperclass.getIRI())) 
				store.remove(superclass);
		}
		//add new
		this.defaultSuperclass = type;
		addTypeToSupreclassStore(defaultSuperclass);
	}

}