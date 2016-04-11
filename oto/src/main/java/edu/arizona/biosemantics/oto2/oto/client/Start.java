//package edu.arizona.biosemantics.oto2.oto.client;
//
//import com.google.gwt.core.client.EntryPoint;
//import com.google.gwt.user.client.ui.RootPanel;
//import com.sencha.gxt.widget.core.client.container.Viewport;
//
//import edu.arizona.biosemantics.oto2.oto.client.event.DownloadEvent;
//import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent;
//
//public class Start implements EntryPoint {
//
//	public void onModuleLoad() {
//		int collectionId = 1;
//		String secret = "my secret";
//		
//		Oto oto = new Oto();
//		Viewport v = new Viewport();
//		v.add(oto.getView().asWidget());
//		RootPanel.get().add(v);
//		oto.setUser("UserB");
//		oto.getEventBus().addHandler(DownloadEvent.TYPE, new DownloadEvent.DownloadHandler() {
//			@Override
//			public void onDownload(DownloadEvent event) {
//				System.out.println("download called");
//			}
//		});
//		oto.loadCollection(collectionId, secret, false);
//	}
//
//}
