package edu.arizona.biosemantics.oto2.oto.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.container.Viewport;

import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.event.DownloadEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LoadEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent.SaveHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;

public class StandAlone implements EntryPoint {

	ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	
	public void onModuleLoad() {
		int collectionId = 1;
		String secret = "30";
				
		final Oto oto = new Oto();
		oto.setUser("Standalone user");
		oto.getEventBus().addHandler(DownloadEvent.TYPE, new DownloadEvent.DownloadHandler() {
			@Override
			public void onDownload(DownloadEvent event) {
				System.out.println("save called");
			}
		});
		Viewport v = new Viewport();
		v.add(oto.getView().asWidget());
		RootPanel.get().add(v);
		
		try {			
			collectionId = Integer.parseInt(Location.getParameter("id"));
			secret = Location.getParameter("secret");
			
			collectionService.get(collectionId, secret, new AsyncCallback<Collection>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
				@Override
				public void onSuccess(Collection result) {
					oto.getEventBus().fireEvent(new LoadEvent(result, false));
				}
			});
		} catch(Throwable t) {
			Alerter.alertCouldNotBeLoaded(t, collectionId, secret);
		}
	}

}
