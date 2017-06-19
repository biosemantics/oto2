package edu.arizona.biosemantics.oto2.ontologize.client.content.ontologyview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.AddEvent;
import com.sencha.gxt.widget.core.client.event.AddEvent.AddHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.content.ontologyview.GraphDemo.Bundle;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologiesEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectOntologyEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.OntologyProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

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
	private VerticalLayoutContainer verticalLayoutContainer;
	private ListStore<Ontology> ontologyStore;
	//private ComboBox<Ontology> ontologyComboBox;
	private CheckBox superClassCheckBox;
	private CheckBox partOfCheckBox;
	private CheckBox synonymCheckBox;
	private CheckBox entityCheckBox;
	private CheckBox qualityCheckBox;
	protected Ontology ontology;
	private TextButton refreshButton = new TextButton("Refresh");
	private EventBus eventBus;
	
	public OntologyView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.add(createToolBar(),new VerticalLayoutData(1,-1));
		verticalLayoutContainer.add(visualizationContainer, new VerticalLayoutData(1, 1));	
	    visualizationContainer.setScrollMode(ScrollMode.ALWAYS);	
	    
	    //ontologyStore.addAll(ModelController.getOntologies());
		//setOntology(ModelController.getOntologies().get(0));
	    
		bindEvents();
	}
	
	public void setEntityChecked(boolean value) {
		this.entityCheckBox.setValue(value);
	}
	
	public void setQualityChecked(boolean value) {
		this.qualityCheckBox.setValue(value);
	}
	
	public void setSuperclassChecked(boolean value) {
		this.superClassCheckBox.setValue(value);
	}
	
	public void setPartOfChecked(boolean value) {
		this.partOfCheckBox.setValue(value);
	}

	public void setSynonymChecked(boolean value) {
		this.synonymCheckBox.setValue(value);
	}

	private ToolBar createToolBar() {
		ontologyStore = new ListStore<Ontology>(ontologyProperties.key());
		/*ontologyComboBox = new ComboBox<Ontology>(ontologyStore, ontologyProperties.prefixLabel());
		ontologyComboBox.setForceSelection(true);
		ontologyComboBox.setTriggerAction(TriggerAction.ALL);*/
		entityCheckBox = new CheckBox();
		entityCheckBox.setBoxLabel("Entity");
		entityCheckBox.setValue(true);
		qualityCheckBox = new CheckBox();
		qualityCheckBox.setBoxLabel("Quality");
		qualityCheckBox.setValue(true);
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
		toolBar.add(new com.google.gwt.user.client.ui.Label("Show:"));
		//toolBar.add(ontologyComboBox);
		toolBar.add(entityCheckBox);
		toolBar.add(qualityCheckBox);
		toolBar.add(superClassCheckBox);
		toolBar.add(partOfCheckBox);
		toolBar.add(synonymCheckBox);
		toolBar.add(refreshButton);
		return toolBar;
	}

	private void bindEvents() {
		eventBus.addHandler(LoadOntologiesEvent.TYPE, new LoadOntologiesEvent.Handler() {
			@Override
			public void onLoad(LoadOntologiesEvent event) {
				ontologyStore.clear();
				ontologyStore.addAll(event.getOntologies());
				if(!event.getOntologies().isEmpty())
					setOntology(event.getOntologies().get(0));
			}
		});
		/*ontologyComboBox.addValueChangeHandler(new ValueChangeHandler<Ontology>() {
			@Override
			public void onValueChange(ValueChangeEvent<Ontology> event) {
				setOntology(event.getValue());
			}
		});*/
		superClassCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				//refresh();
			}
		});
		partOfCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				//refresh();
			}
		});
		synonymCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				//refresh();
			}
		});
		refreshButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				refresh();
			}
		});
	}
	
	private void setOntology(Ontology ontology) {
		OntologyView.this.ontology = ontology;
		//ontologyComboBox.setValue(ontology, false);
		//refresh();
	}

	public void refresh() {
		visualizationContainer.getElement().removeAllChildren();
		
		JsArray<Node> nodes = JavaScriptObject.createArray().cast();
		JsArray<Link> links = JavaScriptObject.createArray().cast();
		
		createNodesAndLinks(ontology, nodes, links);
		//TODO make responsive, e.g. http://stackoverflow.com/questions/9400615/whats-the-best-way-to-make-a-d3-js-visualisation-layout-responsive
		createForceDirectedGraph(visualizationContainer.getElement(), nodes, links);
	}

	private void createNodesAndLinks(Ontology ontology, JsArray<Node> nodes, JsArray<Link> links) {
		Map<String, Integer> nodeIds = new HashMap<String, Integer>();
		createNodesOfSubmissions(ontology, nodeIds, nodes);
		if(superClassCheckBox.getValue())
			createSuperclassLinks(ontology, nodeIds, links);
		if(partOfCheckBox.getValue())
			createPartOfLinks(ontology, nodeIds, links);
		if(synonymCheckBox.getValue())
			createSynonymLinks(ontology, nodeIds, links);
	}

	private void createNodesOfSubmissions(Ontology ontology, Map<String, Integer> nodeIds, JsArray<Node> nodes) {
		for(OntologyClassSubmission submission : ModelController.getClassSubmissions().values()) {
			if(submission.getOntology().equals(ontology)) {
				if(submissionTypeValid(submission)) {
					Node node = Node.createObject().cast();
					node.name(submission.getSubmissionTerm());
					if(submission.getType().equals(Type.ENTITY))
						node.group(0);
					if(submission.getType().equals(Type.QUALITY))
						node.group(6);
					
					addNode(submission.getClassIRI(), node, nodeIds, nodes);
	
					for(Superclass superclass : submission.getSuperclasses()) {
						node = Node.createObject().cast();
						node.name(superclass.hasLabel() ? superclass.getLabel() : superclass.getIri());
						//node.group(0);
						addNode(superclass.getIri(), node, nodeIds, nodes);
					}
					for(PartOf partOf : submission.getPartOfs()) {
						node = Node.createObject().cast();
						node.name(partOf.hasLabel() ? partOf.getLabel() : partOf.getIri());
						//node.group(0);
						addNode(partOf.getIri(), node, nodeIds, nodes);
					}
					for(Synonym synonym : submission.getSynonyms()) {
						node = Node.createObject().cast();
						node.name(synonym.getSynonym());
						//node.group(0);
						addNode(submission.getClassIRI() + "_" + synonym.getSynonym(), node, nodeIds, nodes);
					}
				}
			}
		}
		
		for(OntologySynonymSubmission submission : ModelController.getSynonymSubmissions().values()) {
			if(submission.getOntology().equals(ontology)) {
				if(nodeIds.containsKey(submission.getClassIRI())) {
					Node node = Node.createObject().cast();
					node.name(submission.getSubmissionTerm());
					/*if(submission.getType().equals(Type.ENTITY))
						node.group(0);
					if(submission.getType().equals(Type.QUALITY))
						node.group(2);*/
					addNode(submission.getClassIRI() + "_" + submission.getId(), node, nodeIds, nodes);
					
					for(Synonym synonym : submission.getSynonyms()) {
						node = Node.createObject().cast();
						node.name(synonym.getSynonym());
						addNode(submission.getClassIRI() + "_" + submission.getId() + "_" + synonym.getSynonym(), node, nodeIds, nodes);
					}
				}
			}
		}
	}

	private boolean submissionTypeValid(OntologyClassSubmission submission) {
		return submission.getType().equals(Type.ENTITY) && this.entityCheckBox.getValue() ||
				submission.getType().equals(Type.QUALITY) && this.qualityCheckBox.getValue();
	}

	private void addNode(String iri, Node node, Map<String, Integer> nodeIds, JsArray<Node> nodes) {
		if(!nodeIds.containsKey(iri)) {
			nodes.push(node);
			nodeIds.put(iri, nodes.length() - 1);
		}
	}

	private void createPartOfLinks(Ontology ontology, Map<String, Integer> nodeIds, JsArray<Link> links) {
		for(OntologyClassSubmission submission : ModelController.getClassSubmissions().values()) {
			if(submission.getOntology().equals(ontology)) {
				if(submissionTypeValid(submission)) {
					for(PartOf partOf : submission.getPartOfs()) {
						if(nodeIds.containsKey(partOf.getIri())) {
							Link link = Link.createObject().cast();
							link.source(nodeIds.get(submission.getClassIRI()));
							link.target(nodeIds.get(partOf.getIri()));
							link.type("partof");
							links.push(link);
						}
					}
				}
			}
		}
	}

	private void createSynonymLinks(Ontology ontology, Map<String, Integer> nodeIds, JsArray<Link> links) {
		for(OntologySynonymSubmission submission : ModelController.getSynonymSubmissions().values()) {
			if(submission.getOntology().equals(ontology)) {
				if(nodeIds.containsKey(submission.getClassIRI())) {
					Link link = Link.createObject().cast();
					link.source(nodeIds.get(submission.getClassIRI()));
					link.target(nodeIds.get(submission.getClassIRI() + "_" + submission.getId()));
					links.push(link);
					for(Synonym synonym : submission.getSynonyms()) {
						link = Link.createObject().cast();
						link.source(nodeIds.get(submission.getClassIRI()));
						link.target(nodeIds.get(submission.getClassIRI() + "_" + submission.getId() + "_" + synonym.getSynonym()));
						link.type("synonym");
						links.push(link);
					}
				}
			}
		}
		for(OntologyClassSubmission submission : ModelController.getClassSubmissions().values()) {
			if(submission.getOntology().equals(ontology)) {
				if(submissionTypeValid(submission)) {
					for(Synonym synonym : submission.getSynonyms()) {
						Link link = Link.createObject().cast();
						link.source(nodeIds.get(submission.getClassIRI()));
						link.target(nodeIds.get(submission.getClassIRI() + "_" + synonym.getSynonym()));
						link.type("synonym");
						links.push(link);
					}
				}
			}
		}
	}

	private void createSuperclassLinks(Ontology ontology, Map<String, Integer> nodeIds, JsArray<Link> links) {				
		for(OntologyClassSubmission submission : ModelController.getClassSubmissions().values()) {
			if(submission.getOntology().equals(ontology)) {
				if(submissionTypeValid(submission)) {
					if(submission.hasSuperclasses()) {
						for(Superclass superclass : submission.getSuperclasses()) {
							Link link = Link.createObject().cast();
							link.source(nodeIds.get(submission.getClassIRI()));
							link.target(nodeIds.get(superclass.getIri()));
							link.type("superclass");
							links.push(link);
						}
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
