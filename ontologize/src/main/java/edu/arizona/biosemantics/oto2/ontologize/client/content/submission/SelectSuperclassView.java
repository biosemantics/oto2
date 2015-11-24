package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

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
import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSuperclassEvent;
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
	protected Collection collection;
	private EventBus eventBus;
	private Type defaultSuperclass;
	private AddSuperclassDialog addSuperclassDialog;
	protected List<OntologyClassSubmission> classSubmissions;
	private boolean showDefaultSuperclasses;
	
	public SelectSuperclassView(EventBus eventBus, boolean showDefaultSuperclasses) {
		this.eventBus = eventBus;
		this.showDefaultSuperclasses = showDefaultSuperclasses;
		
		addSuperclassDialog = new AddSuperclassDialog(eventBus);
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
	    superclassVertical.add(clear, new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(horizontalLayoutContainer, "Superclass (IRI or term)"), new VerticalLayoutData(1, 75));
	    
	    bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				SelectSuperclassView.this.collection = event.getCollection();
			}
		});
		eventBus.addHandler(SelectSuperclassEvent.TYPE, new SelectSuperclassEvent.Handler() {
			@Override
			public void onSelect(SelectSuperclassEvent event) {
				addSuperClassToStore(new Superclass(event.getSubmission()));
			}
		});
		eventBus.addHandler(RefreshOntologyClassSubmissionsEvent.TYPE, new RefreshOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onSelect(RefreshOntologyClassSubmissionsEvent event) {
				SelectSuperclassView.this.classSubmissions = event.getOntologyClassSubmissions();
			}
		});
		add.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				addSuperclassDialog.show();
			}
		});
		addSuperclassDialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final String value = addSuperclassDialog.getValue();
				final Superclass superclass = new Superclass();
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
								superclass.setIri(value);
								addSuperClassToStore(superclass);
								addSuperclassDialog.hide();
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
					addSuperclassDialog.hide();
				}
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

	public void addSuperClassToStore(final Superclass superclass) {
		if(!superclass.hasIri()) {
			OntologyClassSubmission ontologyClassSubmission = OntologyClassSubmissionRetriever.getSubmissionOfLabelOrIri(superclass, classSubmissions);
			if(ontologyClassSubmission != null) 
				superclass.setIri(OntologyClassSubmissionRetriever.getSubmissionOfLabelOrIri(superclass, classSubmissions).getIri());
			store.add(superclass);
		} else if(!superclass.hasLabel()) {
			final MessageBox box = Alerter.startLoading();
			toOntologyService.getClassLabel(collection, superclass.getIri(), new AsyncCallback<String>() {
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
		this.addSuperClassToStore(new Superclass(type.getIRI(), type.getLabel()));
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
		setSuperclassType(type);
		addSuperclassDialog.setSubmissionType(type);
	}

	private void setSuperclassType(Type type) {
		//remove old
		for(Superclass superclass : new ArrayList<Superclass>(this.store.getAll())) {
			if(defaultSuperclass != null && superclass.getIri().equals(defaultSuperclass.getIRI())) 
				store.remove(superclass);
		}
		//add new
		this.defaultSuperclass = type;
		addTypeToSupreclassStore(defaultSuperclass);
	}

}