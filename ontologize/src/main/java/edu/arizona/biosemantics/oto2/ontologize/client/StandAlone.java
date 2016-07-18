package edu.arizona.biosemantics.oto2.ontologize.client;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class StandAlone implements EntryPoint {

	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	
	public void onModuleLoad() {
		
		/*ListStore<String> store = new ListStore<String>(new ModelKeyProvider<String>() {
			@Override
			public String getKey(String item) {
				return item;
			}
		});
		ListView<String, String> list = new ListView<String, String>(store, new IdentityValueProvider<String>());
		for(int i=0; i<10000; i++)
			store.add("i" +i);
		
		BorderLayoutContainer borderLayoutContainer = new BorderLayoutContainer();
		borderLayoutContainer.setWestWidget(list);
		
		RootLayoutPanel.get().add(borderLayoutContainer);*/
		
		
		int collectionId = 0;
		String secret = "secret";//"my secret";
		
		/*Collection collection = new Collection();
		collection.setId(collectionId);
		collection.setSecret(secret);
		
		Ontology ontology = new Ontology();
		ontology.setAcronym("test2");
		ontology.setBioportalOntology(false);
		ontology.setName("test2");
		Set<TaxonGroup> taxa = new HashSet<TaxonGroup>();
		taxa.add(TaxonGroup.PLANT);
		ontology.setTaxonGroups(taxa);
		
		toOntologyService.createOntology(collection, ontology, true, new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Object result) {
				// TODO Auto-generated method stub
				
			}
		
		});*/
		
		
		Ontologize ontologize = new Ontologize();
		Viewport v = new Viewport();
		v.add(ontologize.getView().asWidget());
		RootPanel.get().add(v);
		ontologize.setUser("Standalone user");
		/*ontologize.getEventBus().addHandler(SaveEvent.TYPE, new SaveEvent.SaveHandler() {
			@Override
			public void onSave(SaveEvent event) {
				System.out.println("save called");
			}
		});*/
		ontologize.loadCollection(collectionId, secret);
		
		/*List<Term> terms = new LinkedList<Term>();
		terms.add(new Term("leaf", null, "/structure", "structure"));
		terms.add(new Term("stem", null, "/structure", "structure"));
		terms.add(new Term("absent", null, "/character/coloration", "coloration"));
		terms.add(new Term("blue", null, "/character/coloration", "coloration"));
		Collection collection = new Collection("name", TaxonGroup.PLANT, "my secret", terms);
		
		collectionService.insert(collection, new AsyncCallback<Collection>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Collection result) {
				// TODO Auto-generated method stub
				
			}
			
		});*/
		
		/*Collection collection = new Collection();
		collection.setId(116);
		
		Ontology ontology = new Ontology();
		ontology.setAcronym("test");
		ontology.setBioportalOntology(false);
		ontology.setName("test");
		Set<TaxonGroup> taxa = new HashSet<TaxonGroup>();
		taxa.add(TaxonGroup.PLANT);
		
		toOntologyService.createOntology(collection, ontology, true, new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Object result) {
				// TODO Auto-generated method stub
				
			}
		
		});*/
	}

}
