package edu.arizona.biosemantics.oto2.oto.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.container.Viewport;

public class Start implements EntryPoint {

	public void onModuleLoad() {
		int collectionId = 1;
		String secret = "my secret";
		
		Oto oto = new Oto(collectionId, secret);
		Viewport v = new Viewport();
		v.add(oto.getView().asWidget());
		RootPanel.get().add(v);
	}

}
