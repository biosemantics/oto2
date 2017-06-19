package edu.arizona.biosemantics.oto2.ontologize2.client;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.google.gwt.event.shared.EventBus;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.common.context.shared.Context;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.UserLogEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.IContextService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.IContextServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

import com.sencha.gxt.data.shared.ModelKeyProvider;

public class StandAlone implements EntryPoint {

	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private IContextServiceAsync contextService = GWT.create(IContextService.class);
	
	@Override
	public void onModuleLoad() {

//		TreeStore<String> store = new TreeStore<String>(new ModelKeyProvider<String>() {
//			@Override
//			public String getKey(String item) {
//				return item;
//			}
//		});
//		Tree<String, String> tree = new Tree<String, String>(store, new IdentityValueProvider());
//		RootPanel.get().add(tree);
//		store.add("test");
//		store.add("test2");
//		store.add("test3");
//		
//		store.add("test", "a");
//		store.add("test", "b");
//		store.add("test", "c");
//		store.add("test", "d");
//		store.add("test", "e");
		
		
		
		
//		
		final Collection c = new Collection("my collection", TaxonGroup.PLANT, "");
		
		List<String> terms = Arrays.asList(new String[] { "leaf", "stem", "plant anatomical entity", "plant structure", 
			"plant anatomical space", "portion of plant substance", "flower", "tip", "surface", "leaflet", 
			"pedical", "apex", "polen", "area", "pedicel"
		});
		
		for(String term : terms) {
			c.add(new Candidate(term));
		}
		Candidate cson = new Candidate("son");
		cson.setPath("father1");
		c.add(cson);
		Candidate cson2 = new Candidate("son");
		cson2.setPath("father2");
		c.add(cson2);
		Candidate cson3 = new Candidate("son");
		cson3.setPath("father3");
		c.add(cson3);
		try {
			/*c.getGraph().addRelation(new Relation(
					c.getGraph().getRoot(Type.SUBCLASS_OF), 
					new Vertex("plant anatomical entity"), 
					new Edge(Type.SUBCLASS_OF, Source.IMPORT)));
			c.getGraph().addRelation(new Relation(
					new Vertex("plant anatomical entity"), 
					new Vertex("plant structure"), 
					new Edge(Type.SUBCLASS_OF, Source.IMPORT)));
			c.getGraph().addRelation(new Relation(
					new Vertex("plant structure"), 
					new Vertex("plant anatomical space"),
					new Edge(Type.SUBCLASS_OF, Source.IMPORT)));
			c.getGraph().addRelation(new Relation(
					new Vertex("plant anatomical space"),
					new Vertex("portion of plant substance"), 
					new Edge(Type.SUBCLASS_OF, Source.IMPORT)));
			
			c.getGraph().addRelation(new Relation(
					c.getGraph().getRoot(Type.PART_OF), 
					new Vertex("leaf"),
					new Edge(Type.PART_OF, Source.IMPORT)));
			c.getGraph().addRelation(new Relation(
					new Vertex("leaf"),
					new Vertex("tip"), 
					new Edge(Type.PART_OF, Source.IMPORT)));*/
			
			/*
			collectionService.insert(c, new AsyncCallback<Collection>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
				@Override
				public void onSuccess(final Collection c) {
					System.out.println("success");
				
					List<Context> contexts = new LinkedList<Context>();
					contexts.add(new Context(0, "some source 1", "leaf stems with wide flowers and tips"));
					contexts.add(new Context(1, "some source 2", "plant anatomical entity are described as either "
							+ "plant structure, plant anatomical space or portion of plant substance"));
					contexts.add(new Context(2, "some source 3", "flowers are green and yellow"));
					contextService.insert(0, "", contexts, new AsyncCallback<List<Context>>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(List<Context> result) {
							System.out.println("success");
						}
					});
					
//					collectionService.add(c.getId(), c.getSecret(), new Relation(v2, v1, new Edge(Type.SUBCLASS_OF, Source.USER)), 
//							new AsyncCallback<Boolean>() {
//								@Override
//								public void onFailure(Throwable caught) {
//									
//								}
//								@Override
//								public void onSuccess(Boolean result) {
//									System.out.println(result);
//								}
//					}); 

				}
			});
			
			*/
			
			Ontologize ontologize = new Ontologize("st_user|rec");
			
			//ontologize.setUser("st_user|rec");
			Viewport v = new Viewport();
			v.add(ontologize);
			RootPanel.get().add(v);
			
			final EventBus eventBus = ontologize.getEventBus();		
			Timer timer = new Timer() {
				@Override
				public void run() {
					collectionService.get(33, "", new AsyncCallback<Collection>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(Collection result) {
							MessageBox box = Alerter.startLoading();
							eventBus.fireEvent(new LoadCollectionEvent(result));
							Alerter.stopLoading(box);
						}
						
					});
				}
				
			};
			timer.schedule(1000);

		} catch(Exception e) {
			e.printStackTrace();
		}
			
		
	}

}
