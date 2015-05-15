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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

import edu.arizona.biosemantics.oto2.steps.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.RefreshOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.RefreshOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.SelectOntologyEvent;
import edu.arizona.biosemantics.oto2.steps.client.ontologyview.GraphDemo.Bundle;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;

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
	
	private GraphDemo axisComponent;
	private FlowLayoutContainer c = new FlowLayoutContainer();
	private EventBus eventBus;
	private Collection collection;
	private List<OntologyClassSubmission> classSubmissions;
	private List<OntologySynonymSubmission> synonymSubmissions;
	private Ontology ontology;
	
	public OntologyView(EventBus eventBus) {
		this.eventBus = eventBus;
	    c.setScrollMode(ScrollMode.ALWAYS);
		
		//axisComponent = new GraphDemo();
		//axisComponent.start();		
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				collection = event.getCollection();
			}
		});
		eventBus.addHandler(SelectOntologyEvent.TYPE, new SelectOntologyEvent.Handler() {
			@Override
			public void onSelect(SelectOntologyEvent event) {
				ontology = event.getOntology();
				refresh(ontology);
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
	}

	protected void refresh(Ontology ontology) {
		c.getElement().removeAllChildren();
		
		JsArray<Node> nodes = JavaScriptObject.createArray().cast();
		JsArray<Link> links = JavaScriptObject.createArray().cast();
		
		Map<OntologyClassSubmission, Integer> nodeIds = new HashMap<OntologyClassSubmission, Integer>();
		for(OntologyClassSubmission submission : classSubmissions) {
			if(submission.getOntology().equals(ontology)) {
				Node node = Node.createObject().cast();
				node.name(submission.getSubmissionTerm());
				nodes.push(node);
				nodeIds.put(submission, nodes.length() - 1);
			}
		}
		
		Map<String, Set<OntologyClassSubmission>> superclassLinkCandidates = new HashMap<String, 
				Set<OntologyClassSubmission>>();
		for(OntologyClassSubmission submission : classSubmissions) {
			if(submission.getOntology().equals(ontology)) {
				if(submission.hasSuperclassIRI()) {
					for(String superclassIRI : submission.getSuperclassIRIs()) {
						if(!superclassLinkCandidates.containsKey(superclassIRI))	
							superclassLinkCandidates.put(superclassIRI, new HashSet<OntologyClassSubmission>());
						superclassLinkCandidates.get(superclassIRI).add(submission);
					}
				}
			}
		}
		
		for(OntologyClassSubmission submission : classSubmissions) {
			if(submission.getOntology().equals(ontology)) {
				if(superclassLinkCandidates.containsKey(submission.getClassIRI())) {
					for(OntologyClassSubmission candidateSubmission : superclassLinkCandidates.get(submission.getClassIRI())) {
						Link link = Link.createObject().cast();
						link.source(nodeIds.get(submission));
						link.target(nodeIds.get(candidateSubmission));
						link.type("suit");
						links.push(link);
					}
				}
			}
		}
		
		createForceDirectedGraph(c.getElement(), nodes, links);
	}

	@Override
	public Widget asWidget() {
		return c;
	}
	
	private native void createBarchart(Element target, JsArrayNumber jsData)/*-{
		$wnd.barchart(target, jsData);
	}-*/;
	
	private native void createForceDirectedGraph(Element target, JsArray<Node> nodes, JsArray<Link> links)/*-{
		$wnd.force_directed_graph2(target, nodes, links);
	}-*/;

}
