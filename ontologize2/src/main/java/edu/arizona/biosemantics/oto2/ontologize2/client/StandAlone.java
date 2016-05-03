package edu.arizona.biosemantics.oto2.ontologize2.client;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.google.gwt.event.shared.EventBus;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class StandAlone implements EntryPoint {

	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	
	@Override
	public void onModuleLoad() {
//		List<String> terms = new LinkedList<String>();
//		//terms.add("stem");
//		terms.add("leaf");
//		terms.add("leaflet");
//		terms.add("stem");
//		terms.add("fruit");
//		terms.add("flower leaf");
//		terms.add("organ");
//		terms.add("structure");
//		
//		//terms.add("fruit");
//		//terms.add("leaflet");
//		//terms.add("trunk");
//		//terms.add("apex");
//		//terms.add("bark");
//		//terms.add("petal");
//		/*terms.add("flower");
//		terms.add("box");
//		terms.add("margin");
//		terms.add("layer");*/
//		//terms.add("frame");
//		//terms.add("dot");
//		String defaultbucket = "/bucket";
//		final Collection collection = new Collection("name", TaxonGroup.PLANT, "secret", terms, defaultbucket);
//		
////		collection.createPart(collection.getTerm("leaf"), collection.getTerm("leaflet"));
////		collection.createPart(collection.getTerm("stem"), collection.getTerm("leaf"));
////		collection.createPart(collection.getTerm("fruit"), collection.getTerm("leaf"));
////		
////		collection.createSubclass(collection.getTerm("leaf"), collection.getTerm("flower leaf"));
////		collection.createSubclass(collection.getTerm("organ"), collection.getTerm("leaf"));
////		collection.createSubclass(collection.getTerm("structure"), collection.getTerm("leaf"));
//		collectionService.insert(collection, new AsyncCallback<Collection>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				caught.printStackTrace();
//			}
//			@Override
//			public void onSuccess(Collection result) {
//				System.out.println("success");
//			}
//		});
		
		
		
//		
		Ontologize ontologize = new Ontologize();
		Viewport v = new Viewport();
		v.add(ontologize);
		RootPanel.get().add(v);
		
		final EventBus eventBus = ontologize.getEventBus();		
		Timer timer = new Timer() {
			@Override
			public void run() {
				collectionService.get(1, "secret", new AsyncCallback<Collection>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(Collection result) {
						eventBus.fireEvent(new LoadCollectionEvent(result));
					}
					
				});
			}
		};
		timer.schedule(1000);
	}
	
	public static void main(String[] args) {
		
	}

}
