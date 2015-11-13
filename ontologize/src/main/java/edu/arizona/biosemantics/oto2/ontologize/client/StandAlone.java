package edu.arizona.biosemantics.oto2.ontologize.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.container.Viewport;

public class StandAlone implements EntryPoint {

	public void onModuleLoad() {
		int collectionId = 115;
		String secret = "";
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
	}

}
