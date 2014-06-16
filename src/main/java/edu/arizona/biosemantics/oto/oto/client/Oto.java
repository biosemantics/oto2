package edu.arizona.biosemantics.oto.oto.client;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.Viewport;

import edu.arizona.biosemantics.oto.oto.client.categorize.CategorizeView;
import edu.arizona.biosemantics.oto.oto.client.rest.Client;
import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.model.rpc.ICollectionService;
import edu.arizona.biosemantics.oto.oto.shared.model.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto.oto.shared.model.rpc.RPCCallback;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Oto implements EntryPoint {
	
	private int collectionId = 1;
	private String secret = "my secret";

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		ICollectionServiceAsync client = GWT.create(ICollectionService.class);
		Collection collection = new Collection();
		collection.setId(collectionId);
		collection.setSecret(secret);
		client.get(collection, new RPCCallback<Collection>() {
			@Override
			public void onSuccess(Collection collection) {
				CategorizeView categorizeView = new CategorizeView();
				Viewport v = new Viewport();
				v.add(categorizeView.asWidget());
				RootPanel.get().add(v);

				categorizeView.setCollection(collection);
			}
		});
	}
}
