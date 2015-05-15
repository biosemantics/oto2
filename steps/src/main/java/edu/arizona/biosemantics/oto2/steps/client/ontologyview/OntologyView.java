package edu.arizona.biosemantics.oto2.steps.client.ontologyview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import edu.arizona.biosemantics.oto2.steps.client.common.Alerter;
import edu.arizona.biosemantics.oto2.steps.client.event.CreateOntologyEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.RefreshOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.RefreshOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.SelectOntologyEvent;
import edu.arizona.biosemantics.oto2.steps.client.ontologyview.GraphDemo.Bundle;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.OntologyProperties;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySubmission.Type;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.IToOntologyServiceAsync;

public class OntologyView implements IsWidget {
	
	private static class Node extends JavaScriptObject {
		
		protected Node() {
			super();
		}
		
		public final native String name(String name) /*-{
			return this.name = name;
		}-*/;
	
	    public final native String name() /*-{
			return this.name;
	    }-*/;
	    
		public final native int group(int group) /*-{
			return this.group = group;
		}-*/;
	
	    public final native int group() /*-{
			return this.group;
	    }-*/;
	}
	
	private static class Link extends JavaScriptObject {
		
		protected Link() {
			super();
		}

		public final native String type(String type) /*-{
			return this.type = type;
		}-*/;
	
	    public final native String type() /*-{
			return this.type;
	    }-*/;
		
		public final native int source(int source) /*-{
			return this.source = source;
		}-*/;
		
		public final native int target(int target) /*-{
			return this.target = target;
		}-*/;

	    public final native int target() /*-{
			return this.target;
	    }-*/;

	    public final native int source() /*-{
			return this.source;
	    }-*/;
	    
		public final native double value(double value) /*-{
			return this.value = value;
		}-*/;
	
	    public final native double value() /*-{
			return this.value;
	    }-*/;

	}
	
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	private OntologyProperties ontologyProperties = GWT.create(OntologyProperties.class);
	private FlowLayoutContainer visualizationContainer = new FlowLayoutContainer();
	private EventBus eventBus;
	private Collection collection;
	private List<OntologyClassSubmission> classSubmissions;
	private List<OntologySynonymSubmission> synonymSubmissions;
	private VerticalLayoutContainer verticalLayoutContainer;
	private ListStore<Ontology> ontologyStore;
	private ComboBox<Ontology> ontologyComboBox;
	private CheckBox superClassCheckBox;
	private CheckBox partOfCheckBox;
	private CheckBox synonymCheckBox;
	protected Ontology ontology;
	
	public OntologyView(EventBus eventBus) {
		this.eventBus = eventBus;

		verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.add(createToolBar(),new VerticalLayoutData(1,-1));
		verticalLayoutContainer.add(visualizationContainer, new VerticalLayoutData(1, 1));	
	    visualizationContainer.setScrollMode(ScrollMode.ALWAYS);	
		
				
		bindEvents();
	}

	private ToolBar createToolBar() {
		ontologyStore = new ListStore<Ontology>(ontologyProperties.key());
		ontologyComboBox = new ComboBox<Ontology>(ontologyStore, ontologyProperties.nameLabel());
		ontologyComboBox.setForceSelection(true);
		ontologyComboBox.setTriggerAction(TriggerAction.ALL);
		superClassCheckBox = new CheckBox();
		superClassCheckBox.setBoxLabel("Super-class");
		superClassCheckBox.setValue(true);
		partOfCheckBox = new CheckBox();
		partOfCheckBox.setBoxLabel("Part of");
		partOfCheckBox.setValue(true);
		synonymCheckBox = new CheckBox();
		synonymCheckBox.setBoxLabel("Synonym");
		synonymCheckBox.setValue(true);
		
		ToolBar toolBar = new ToolBar();
		//toolBar.add(new FillToolItem());
		toolBar.add(new com.google.gwt.user.client.ui.Label("Ontology:"));
		toolBar.add(ontologyComboBox);
		toolBar.add(superClassCheckBox);
		toolBar.add(partOfCheckBox);
		toolBar.add(synonymCheckBox);
		return toolBar;
	}

	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				collection = event.getCollection();
				
				toOntologyService.getLocalOntologies(collection, new AsyncCallback<List<Ontology>>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.getOntologiesFailed(caught);
					}

					@Override
					public void onSuccess(List<Ontology> result) {
						ontologyStore.clear();
						ontologyStore.addAll(result);
					}
				});
			}
		});
		eventBus.addHandler(CreateOntologyEvent.TYPE, new CreateOntologyEvent.Handler() {
			@Override
			public void onCreate(CreateOntologyEvent event) {
				ontologyStore.add(event.getOntology());
			}
		});
		eventBus.addHandler(SelectOntologyEvent.TYPE, new SelectOntologyEvent.Handler() {
			@Override
			public void onSelect(SelectOntologyEvent event) {
				ontologyComboBox.select(event.getOntology());
			}
		});
		eventBus.addHandler(RefreshOntologyClassSubmissionsEvent.TYPE, new RefreshOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onSelect(RefreshOntologyClassSubmissionsEvent event) {
				classSubmissions = event.getOntologyClassSubmissions();
			}
		});
		eventBus.addHandler(RefreshOntologySynonymSubmissionsEvent.TYPE, new RefreshOntologySynonymSubmissionsEvent.Handler() {
			@Override
			public void onSelect(RefreshOntologySynonymSubmissionsEvent event) {
				synonymSubmissions = event.getOntologySynonymSubmissions();
			}
		});
		ontologyComboBox.addValueChangeHandler(new ValueChangeHandler<Ontology>() {
			@Override
			public void onValueChange(ValueChangeEvent<Ontology> event) {
				ontology = event.getValue();
				refresh();
			}
		});
		superClassCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				refresh();
			}
		});
		partOfCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				refresh();
			}
		});
		synonymCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				refresh();
			}
		});
	}

	protected void refresh() {
		visualizationContainer.getElement().removeAllChildren();
		
		JsArray<Node> nodes = JavaScriptObject.createArray().cast();
		JsArray<Link> links = JavaScriptObject.createArray().cast();
		
		createNodesAndLinks(ontology, nodes, links);
		createForceDirectedGraph(visualizationContainer.getElement(), nodes, links);
	}

	private void createNodesAndLinks(Ontology ontology, JsArray<Node> nodes, JsArray<Link> links) {
		Map<String, OntologySubmission> iriNodeIds = new HashMap<String, OntologySubmission>();
		Map<Object, Integer> nodeIds = new HashMap<Object, Integer>();
		createNodesOfSubmissions(ontology, nodeIds, iriNodeIds, nodes);
		if(superClassCheckBox.getValue())
			createSuperclassLinks(ontology, nodeIds, iriNodeIds, links);
		if(partOfCheckBox.getValue())
			createPartOfLinks(ontology, nodeIds, iriNodeIds, links);
		if(synonymCheckBox.getValue())
			createSynonymLinks(ontology, nodeIds, iriNodeIds, links);
	}

	private void createNodesOfSubmissions(Ontology ontology, Map<Object, Integer> nodeIds,	Map<String, OntologySubmission> iriNodeIds, JsArray<Node> nodes) {
		for(OntologyClassSubmission submission : classSubmissions) {
			if(submission.getOntology().equals(ontology)) {
				Node node = Node.createObject().cast();
				node.name(submission.getSubmissionTerm());
				if(submission.getType().equals(Type.ENTITY))
					node.group(0);
				if(submission.getType().equals(Type.QUALITY))
					node.group(6);
				nodes.push(node);
				
				nodeIds.put(submission, nodes.length() - 1);
				iriNodeIds.put(submission.getClassIRI(), submission);
			}
		}
		
		for(OntologySynonymSubmission submission : synonymSubmissions) {
			if(submission.getOntology().equals(ontology)) {
				Node node = Node.createObject().cast();
				node.name(submission.getSubmissionTerm());
				if(submission.getType().equals(Type.ENTITY))
					node.group(0);
				if(submission.getType().equals(Type.QUALITY))
					node.group(2);
				
				nodes.push(node);
				nodeIds.put(submission, nodes.length() - 1);
			}
		}
	}

	private void createPartOfLinks(Ontology ontology, Map<Object, Integer> nodeIds, Map<String, OntologySubmission> iriNodeIds, JsArray<Link> links) {
		for(OntologyClassSubmission submission : classSubmissions) {
			if(submission.getOntology().equals(ontology)) {
				for(String iri : submission.getPartOfIRIs()) {
					if(iriNodeIds.containsKey(iri)) {
						OntologySubmission partOf = iriNodeIds.get(iri);
						
						Link link = Link.createObject().cast();
						link.source(nodeIds.get(submission));
						link.target(nodeIds.get(partOf));
						link.type("partof");
						links.push(link);
					}
				}
			}
		}
	}

	private void createSynonymLinks(Ontology ontology, Map<Object, Integer> nodeIds, Map<String, OntologySubmission> iriNodeIds, JsArray<Link> links) {
		for(OntologySynonymSubmission submission : synonymSubmissions) {
			if(submission.getOntology().equals(ontology)) {
				Link link = Link.createObject().cast();
				link.source(nodeIds.get(submission));
				link.target(nodeIds.get(iriNodeIds.get(submission.getClassIRI())));
				link.type("synonym");
				links.push(link);
			}
		}
	}

	private void createSuperclassLinks(Ontology ontology, Map<Object, Integer> nodeIds, Map<String, OntologySubmission> iriNodeIds, JsArray<Link> links) {				
		for(OntologyClassSubmission submission : classSubmissions) {
			if(submission.getOntology().equals(ontology)) {
				if(submission.hasSuperclassIRI()) {
					for(String superclassIRI : submission.getSuperclassIRIs()) {
						Link link = Link.createObject().cast();
						link.source(nodeIds.get(submission));
						link.target(nodeIds.get(iriNodeIds.get(superclassIRI)));
						link.type("superclass");
						links.push(link);
					}
				}
			}
		}
	}

	@Override
	public Widget asWidget() {
		return verticalLayoutContainer;
	}
	
	private native void createBarchart(Element target, JsArrayNumber jsData)/*-{
		$wnd.barchart(target, jsData);
	}-*/;
	
	private native void createForceDirectedGraph(Element target, JsArray<Node> nodes, JsArray<Link> links)/*-{
		$wnd.force_directed_graph2(target, nodes, links);
	}-*/;

}
