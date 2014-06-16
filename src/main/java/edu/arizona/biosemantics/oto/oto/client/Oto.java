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

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//Client client = new Client(GWT.getModuleBaseURL());
		ICollectionServiceAsync client = GWT.create(ICollectionService.class);
		
		Collection collection = new Collection();
		collection.setId(collectionId);
		collection.setSecret("my secret");
		client.get(collection, new RPCCallback<Collection>() {
			@Override
			public void onSuccess(Collection collection) {
				List<Bucket> buckets = collection.getBuckets();
				List<Label> labels = collection.getLabels();

				CategorizeView categorizeView = new CategorizeView();
				Viewport v = new Viewport();
				v.add(categorizeView.asWidget());
				RootPanel.get().add(v);

				categorizeView.setCollection(collection);
			}
		});
	}

	/*private List<Label> getInitialCategories() {
		List<Label> categories = new LinkedList<Label>();
		Label structures = new Label("structures", "all structures");
		List<Term> terms = new LinkedList<Term>();
		terms.add(new Term("a"));
		terms.add(new Term("b"));
		structures.setTerms(terms);
		
		Label characters = new Label("characters", "all characters");
		terms = new LinkedList<Term>();
		terms.add(new Term("c"));
		terms.add(new Term("d"));
		characters.setTerms(terms);
		
		Label others = new Label("others", "all others");
		terms = new LinkedList<Term>();
		terms.add(new Term("e"));
		terms.add(new Term("f"));
		others.setTerms(terms);
		
		categories.add(structures);
		categories.add(characters);
		categories.add(others);
		
		return categories;
	}*/
}
