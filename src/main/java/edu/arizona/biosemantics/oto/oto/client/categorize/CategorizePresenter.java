package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LoadEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelRenameEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermRenameEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.SaveEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.SynonymRemovalEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.model.rpc.ICollectionService;
import edu.arizona.biosemantics.oto.oto.shared.model.rpc.ICollectionServiceAsync;

public class CategorizePresenter {

	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private Collection collection;
	private CategorizeView view;
	private EventBus eventBus;
	
	public CategorizePresenter(EventBus eventBus, CategorizeView view) {
		this.eventBus = eventBus;
		this.view = view;
		bindEvents();
	}

	private void bindEvents() {
		//save triggers
		eventBus.addHandler(CategorizeCopyTermEvent.TYPE, new CategorizeCopyTermEvent.CategorizeCopyTermHandler() {
			@Override
			public void onCategorize(List<Term> terms, Label sourceCategory, Label targetCategory) {
				saveCollection();
			}
		});
		eventBus.addHandler(CategorizeMoveTermEvent.TYPE, new CategorizeMoveTermEvent.CategorizeMoveTermHandler() {
			@Override
			public void onCategorize(List<Term> terms, Label sourceCategory,
					Label targetCategory) {
				saveCollection();
			}
		});
		eventBus.addHandler(SynonymCreationEvent.TYPE, new SynonymCreationEvent.SynonymCreationHandler() {
			@Override
			public void onSynonymCreation(Term term, Term mainTerm) {
				saveCollection();
			}
		});
		eventBus.addHandler(SynonymRemovalEvent.TYPE, new SynonymRemovalEvent.SynonymRemovalHandler() {
			@Override
			public void onSynonymCreation(Term term, Term oldMainTerm) {
				saveCollection();
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(List<Term> terms, Label category) {
				saveCollection();
			}
		});
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			
			@Override
			public void onUncategorize(List<Term> terms, Label oldCategory) {
				saveCollection();
			}
		});
		eventBus.addHandler(SaveEvent.TYPE, new SaveEvent.SaveHandler() {
			@Override
			public void onSave(Collection collection) {
				saveCollection();
			}
		});
		/*eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(Label source, Label destination) {
				saveCollection();
			}
		});*/
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(Label label) {
				saveCollection();
			}
		});
		eventBus.addHandler(LabelRenameEvent.TYPE, new LabelRenameEvent.RenameLabelHandler() {
			@Override
			public void onRename(Label label) {
				saveCollection();
			}
		});
		eventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(Term term) {
				saveCollection();
			}
		});
		
		eventBus.addHandler(LoadEvent.TYPE, new LoadEvent.LoadHandler() {
			@Override
			public void onLoad(Collection collection) {
				loadCollection(collection.getId(), collection.getSecret());
			}
		});
	}

	private void saveCollection() {
		collectionService.update(collection, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(Void result) {	}
		});
	}

	public void loadCollection(int collectionId, String secret) {		
		Collection collection = new Collection();
		collection.setId(collectionId);
		collection.setSecret(secret);
		collectionService.get(collection, new AsyncCallback<Collection>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(Collection result) {
				CategorizePresenter.this.collection = result;
				view.setCollection(result);
			}
		});
	}
}
