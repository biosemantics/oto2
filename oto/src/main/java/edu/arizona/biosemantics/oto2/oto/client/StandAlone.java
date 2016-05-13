package edu.arizona.biosemantics.oto2.oto.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

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
	protected Collection collection;
	
	public void onModuleLoad() {
		int collectionId = 87;
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
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		TextButton saveButton = new TextButton("Save");
		saveButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(collection != null) {
					collectionService.update(collection, false, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							
						}
						@Override
						public void onSuccess(Void result) {
							Alerter.showInfo("Saved successfully", "Saved successfully");
						}
					});
				}
			}
		});
		vlc.add(saveButton, new VerticalLayoutData(1, -1));
		vlc.add(oto.getView().asWidget(), new VerticalLayoutData(1, 1));
		v.add(vlc);
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
					StandAlone.this.collection = result;
					oto.getEventBus().fireEvent(new LoadEvent(result, false));
				}
			});
		} catch(Throwable t) {
			Alerter.alertCouldNotBeLoaded(t, collectionId, secret);
		}
	}

}
