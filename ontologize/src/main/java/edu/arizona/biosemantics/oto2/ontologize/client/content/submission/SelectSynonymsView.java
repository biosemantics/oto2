package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SynonymProperties;

public class SelectSynonymsView implements IsWidget {

	private SynonymProperties synonymProperties = GWT.create(SynonymProperties.class);
	private VerticalLayoutContainer formContainer;
	private EventBus eventBus;
	
	private ListView<Synonym, String> listView;
	private ListStore<Synonym> store;
	private TextButton add = new TextButton("Add");
	private TextButton remove = new TextButton("Remove");
	private TextButton clear = new TextButton("Clear");
	protected Collection collection;
	private AddSynonymDialog addSynonymDialog;

	public SelectSynonymsView(EventBus eventBus) {
		this.eventBus = eventBus;
		this.addSynonymDialog = new AddSynonymDialog(eventBus);

		formContainer = new VerticalLayoutContainer();
		store = new ListStore<Synonym>(new ModelKeyProvider<Synonym>() {
			@Override
			public String getKey(Synonym item) {
				return item.getSynonym();
			}
	    });
		listView = new ListView<Synonym, String>(store, synonymProperties.synonym());
		    
		HorizontalLayoutContainer synonymHorizontal = new HorizontalLayoutContainer();
	    synonymHorizontal.add(listView, new HorizontalLayoutData(0.715, 75));
	    VerticalLayoutContainer synonymVertical = new VerticalLayoutContainer();
	    synonymHorizontal.add(synonymVertical, new HorizontalLayoutData(0.3, -1));
	    synonymVertical.add(add, new VerticalLayoutData(1, -1));
	    synonymVertical.add(remove, new VerticalLayoutData(1, -1));
	    synonymVertical.add(clear, new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(synonymHorizontal, "Synonyms"), new VerticalLayoutData(1, 75));
	    formContainer.setScrollMode(ScrollMode.AUTOY);
	    formContainer.setAdjustForScroll(true);
	    
	    bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				SelectSynonymsView.this.collection = event.getCollection();
			}
		});
		
		add.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				addSynonymDialog.setCollection(collection);
				addSynonymDialog.show();
			}
		});
		addSynonymDialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				store.add(new Synonym(addSynonymDialog.getValue()));
				addSynonymDialog.hide();
			}
		});
		remove.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for(Synonym remove : listView.getSelectionModel().getSelectedItems())
					store.remove(remove);
			}
		});
		clear.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				clear();
			}
		});
	}

	@Override
	public Widget asWidget() {
		return formContainer;
	}

	public List<Synonym> getSynonyms() {
		return new ArrayList<Synonym>(store.getAll());
	}

	public void clear() {
		store.clear();
	}

	public void setSynonyms(List<Synonym> synonyms) {
		store.clear();
		store.addAll(synonyms);
	}
	
	public void setSubmissionType(Type type) {
		addSynonymDialog.setSubmissionType(type);
	}
}
