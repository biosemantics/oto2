package edu.arizona.biosemantics.oto2.oto.client.categorize;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.oto2.oto.client.categorize.event.CategorizeCopyRemoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.LabelCreateEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.LabelModifyEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.LoadEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.SaveEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.SynonymRemovalEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.TermRenameEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label.AddResult;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.RPCCallback;

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
		eventBus.addHandler(LabelCreateEvent.TYPE, new LabelCreateEvent.CreateLabelHandler() {
			@Override
			public void onCreate(Label label) {
				saveCollection();
			}
		});
		eventBus.addHandler(CategorizeCopyTermEvent.TYPE, new CategorizeCopyTermEvent.CategorizeCopyTermHandler() {
			@Override
			public void onCategorize(List<Term> terms, Label sourceCategory, List<Label> targetCategories) {
				saveCollection();
			}
		});
		eventBus.addHandler(CategorizeCopyRemoveTermEvent.TYPE, new CategorizeCopyRemoveTermEvent.CategorizeCopyRemoveTermHandler() {
			@Override
			public void onRemove(List<Term> terms, Label label) {
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
			public void onSynonymCreation(Label label, Term mainTerm, List<Term> synonymTerms) {
				saveCollection();
			}
		});
		eventBus.addHandler(SynonymRemovalEvent.TYPE, new SynonymRemovalEvent.SynonymRemovalHandler() {
			@Override
			public void onSynonymRemoval(Label label, Term mainTerm, List<Term> synonyms) {
				saveCollection();
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(List<Term> terms, List<Label> categories) {
				saveCollection();
			}
		});
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			
			@Override
			public void onUncategorize(Term term, List<Label> oldCategories) {
				saveCollection();
			}
		});
		eventBus.addHandler(SaveEvent.TYPE, new SaveEvent.SaveHandler() {
			@Override
			public void onSave(Collection collection) {
				saveCollection();
			}
		});
		eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(Label destination, List<Label> sources,	Map<Term, AddResult> addResults) {
				saveCollection();
			}
		});
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(Label label) {
				saveCollection();
			}
		});
		eventBus.addHandler(LabelModifyEvent.TYPE, new LabelModifyEvent.ModifyLabelHandler() {
			@Override
			public void onModify(Label label) {
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
		collectionService.update(collection, new RPCCallback<Void>() {
			@Override
			public void onSuccess(Void result) {	}
		});
	}

	public void loadCollection(int collectionId, String secret) {		
		Collection collection = new Collection();
		collection.setId(collectionId);
		collection.setSecret(secret);
		collectionService.get(collection, new RPCCallback<Collection>() {
			@Override
			public void onSuccess(Collection result) {
				CategorizePresenter.this.collection = result;
				view.setCollection(result);
			}
		});
	}
}
