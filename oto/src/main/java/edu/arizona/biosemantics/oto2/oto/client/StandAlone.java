package edu.arizona.biosemantics.oto2.oto.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.container.Viewport;

import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.event.DownloadEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent.SaveHandler;

public class StandAlone implements EntryPoint {

	public void onModuleLoad() {
		int collectionId = 9;
		String secret = "30";
		try {			
			Oto oto = new Oto();
			Viewport v = new Viewport();
			v.add(oto.getView().asWidget());
			RootPanel.get().add(v);
			oto.setUser("Standalone user");
			oto.getEventBus().addHandler(DownloadEvent.TYPE, new DownloadEvent.DownloadHandler() {
				@Override
				public void onDownload(DownloadEvent event) {
					System.out.println("save called");
				}
			});
			oto.loadCollection(collectionId, secret, false);
		} catch(Throwable t) {
			Alerter.alertCouldNotBeLoaded(t, collectionId, secret);
		}
	}

}
