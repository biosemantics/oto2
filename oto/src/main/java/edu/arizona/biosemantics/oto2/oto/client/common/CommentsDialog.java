package edu.arizona.biosemantics.oto2.oto.client.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.StartEditEvent;
import com.sencha.gxt.widget.core.client.event.StartEditEvent.StartEditHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.oto.client.Oto;
import edu.arizona.biosemantics.oto2.oto.client.event.CommentEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Comment;
import edu.arizona.biosemantics.oto2.oto.shared.model.CommentProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.RPCCallback;

public class CommentsDialog extends Dialog {

	private static class BucketLabelTermComment {
		private Bucket bucket;
		private List<Label> labels;
		private Term term;
		private Comment comment;

		public BucketLabelTermComment(Bucket bucket, List<Label> labels, Term term, Comment comment) {
			this.bucket = bucket;
			this.labels = new ArrayList<Label>(labels);
			Collections.sort(this.labels);
			this.term = term;
			this.comment = comment;
		}
		public Bucket getBucket() {
			return bucket;
		}
		public List<Label> getLabels() {
			return labels;
		}
		public Term getTerm() {
			return term;
		}
		public Comment getComment() {
			return comment;
		}
	}
	
	private static class BucketLabelTermCommentProperties implements PropertyAccess<BucketLabelTermComment> {
		@Path("id")
		ModelKeyProvider<BucketLabelTermComment> key() {
			return new ModelKeyProvider<BucketLabelTermComment>() {
				@Override
				public String getKey(BucketLabelTermComment item) {
					return String.valueOf(item.getComment().getId() + "-" + item.getBucket().getId() + "-" + 
							item.getTerm().getId());
				}
			};
		}

		@Path("user")
		ValueProvider<BucketLabelTermComment, String> user() {
			return new ValueProvider<BucketLabelTermComment, String>() {
				@Override
				public String getValue(BucketLabelTermComment object) {
					return object.getComment().getUser();
				}
				@Override
				public void setValue(BucketLabelTermComment object, String value) {	}
				@Override
				public String getPath() {
					return "user";
				}
			};
		}

		@Path("comment")
		ValueProvider<BucketLabelTermComment, String> text() {
			return new ValueProvider<BucketLabelTermComment, String>() {
				@Override
				public String getValue(BucketLabelTermComment object) {
					return object.getComment().getComment();
				}
				@Override
				public void setValue(BucketLabelTermComment object, String value) {	
					object.getComment().setComment(value);
				}
				@Override
				public String getPath() {
					return "comment";
				}
			};
		}
		
		@Path("bucket")
		ValueProvider<BucketLabelTermComment, String> bucket() {
			return new ValueProvider<BucketLabelTermComment, String>() {
				@Override
				public String getValue(BucketLabelTermComment object) {
					return object.getBucket().getName();
				}
				@Override
				public void setValue(BucketLabelTermComment object, String value) {	}
				@Override
				public String getPath() {
					return "bucket";
				}
			};
		}

		@Path("label")
		ValueProvider<BucketLabelTermComment, String> label() {
			return new ValueProvider<BucketLabelTermComment, String>() {
				@Override
				public String getValue(BucketLabelTermComment object) {
					String labelString = "";
					for(Label label : object.getLabels()) 
						labelString += label.getName() + ", ";
					return labelString.substring(0, labelString.length() - 2);
				}
				@Override
				public void setValue(BucketLabelTermComment object, String value) { }
				@Override
				public String getPath() {
					return "label";
				}
			};
		}
		@Path("term")
		ValueProvider<BucketLabelTermComment, String> term() {
			return new ValueProvider<BucketLabelTermComment, String>() {
				@Override
				public String getValue(BucketLabelTermComment object) {
					return object.getTerm().getTerm();
				}
				@Override
				public void setValue(BucketLabelTermComment object, String value) { }
				@Override
				public String getPath() {
					return "term";
				}
			};
		}
	}
	
	private EventBus eventBus;
	private Collection collection;
	private ListStore<BucketLabelTermComment> commentStore;
	private BucketLabelTermCommentProperties bucketLabelTermCommentProperties = new BucketLabelTermCommentProperties();
	private Grid<BucketLabelTermComment> grid;
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);

	//by category, by initial bucket
	public CommentsDialog(final EventBus eventBus, Collection collection) {
		this.eventBus = eventBus;
		this.collection = collection;
				
		IdentityValueProvider<BucketLabelTermComment> identity = new IdentityValueProvider<BucketLabelTermComment>();
		final CheckBoxSelectionModel<BucketLabelTermComment> checkBoxSelectionModel = new CheckBoxSelectionModel<BucketLabelTermComment>(
				identity);

		checkBoxSelectionModel.setSelectionMode(SelectionMode.MULTI);

		ColumnConfig<BucketLabelTermComment, String> termCol = new ColumnConfig<BucketLabelTermComment, String>(
				bucketLabelTermCommentProperties.term(), 100, "Term");
		ColumnConfig<BucketLabelTermComment, String> labelCol = new ColumnConfig<BucketLabelTermComment, String>(
				bucketLabelTermCommentProperties.label(), 100, "Categories");
		ColumnConfig<BucketLabelTermComment, String> bucketCol = new ColumnConfig<BucketLabelTermComment, String>(
				bucketLabelTermCommentProperties.bucket(), 100, "Bucket");
		ColumnConfig<BucketLabelTermComment, String> userCol = new ColumnConfig<BucketLabelTermComment, String>(
				bucketLabelTermCommentProperties.user(), 100, "User");
		final ColumnConfig<BucketLabelTermComment, String> textCol = new ColumnConfig<BucketLabelTermComment, String>(
				bucketLabelTermCommentProperties.text(), 400, "Comment");

		List<ColumnConfig<BucketLabelTermComment, ?>> columns = new ArrayList<ColumnConfig<BucketLabelTermComment, ?>>();
		columns.add(checkBoxSelectionModel.getColumn());
		columns.add(userCol);
		columns.add(bucketCol);
		columns.add(termCol);
		columns.add(labelCol);
		columns.add(textCol);
		ColumnModel<BucketLabelTermComment> cm = new ColumnModel<BucketLabelTermComment>(columns);

		commentStore = new ListStore<BucketLabelTermComment>(bucketLabelTermCommentProperties.key());
		commentStore.setAutoCommit(true);
		
		List<BucketLabelTermComment> comments = createComments();
		for (BucketLabelTermComment comment : comments)
			commentStore.add(comment);

		final GroupingView<BucketLabelTermComment> groupingView = new GroupingView<BucketLabelTermComment>();
		groupingView.setShowGroupedColumn(false);
		groupingView.setForceFit(true);
		groupingView.groupBy(userCol);

		grid = new Grid<BucketLabelTermComment>(commentStore, cm);
		grid.setView(groupingView);
		grid.setContextMenu(createContextMenu());
		grid.setSelectionModel(checkBoxSelectionModel);
		grid.getView().setAutoExpandColumn(textCol);
		grid.setBorders(false);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		
		//StringFilter<BucketLabelTermComment> termFilter = new StringFilter<BucketLabelTermComment>(
		//		bucketLabelTermCommentProperties.term());
		//StringFilter<BucketLabelTermComment> labelFilter = new StringFilter<BucketLabelTermComment>(
		//		bucketLabelTermCommentProperties.label());
		//StringFilter<BucketLabelTermComment> bucketFilter = new StringFilter<BucketLabelTermComment>(
		//		bucketLabelTermCommentProperties.bucket());
		StringFilter<BucketLabelTermComment> userFilter = new StringFilter<BucketLabelTermComment>(
				bucketLabelTermCommentProperties.user());
		StringFilter<BucketLabelTermComment> commentFilter = new StringFilter<BucketLabelTermComment>(
				bucketLabelTermCommentProperties.text());

		ListStore<String> termFilterStore = new ListStore<String>(
				new ModelKeyProvider<String>() {
					@Override
					public String getKey(String item) {
						return item;
					}
				});
		for(Label label : collection.getLabels())
			termFilterStore.add(label.getName());
		ListFilter<BucketLabelTermComment, String> termFilter = new ListFilter<BucketLabelTermComment, String>(
				bucketLabelTermCommentProperties.term(), termFilterStore);
		
		ListStore<String> labelFilterStore = new ListStore<String>(
				new ModelKeyProvider<String>() {
					@Override
					public String getKey(String item) {
						return item;
					}
				});
		for(Label label : collection.getLabels())
			labelFilterStore.add(label.getName());
		ListFilter<BucketLabelTermComment, String> labelFilter = new ListFilter<BucketLabelTermComment, String>(
				bucketLabelTermCommentProperties.label(), labelFilterStore);
		
		ListStore<String> bucketFilterStore = new ListStore<String>(
				new ModelKeyProvider<String>() {
					@Override
					public String getKey(String item) {
						return item;
					}
				});
		for(Bucket bucket : collection.getBuckets())
			bucketFilterStore.add(bucket.getName());
		ListFilter<BucketLabelTermComment, String> bucketFilter = new ListFilter<BucketLabelTermComment, String>(
				bucketLabelTermCommentProperties.bucket(), bucketFilterStore);

		GridFilters<BucketLabelTermComment> filters = new GridFilters<BucketLabelTermComment>();
		filters.initPlugin(grid);
		filters.setLocal(true);

		filters.addFilter(termFilter);
		filters.addFilter(labelFilter);
		filters.addFilter(bucketFilter);
		filters.addFilter(userFilter);
		filters.addFilter(commentFilter);

		GridInlineEditing<BucketLabelTermComment> editing = new GridInlineEditing<BucketLabelTermComment>(grid) {
			@Override
			public void startEditing(final GridCell cell) {
				BucketLabelTermComment comment = commentStore.get(cell.getRow());
				if(comment.getComment().getUser().equals(Oto.user))
					super.startEditing(cell);
			}
		};
		final TextField editor = new TextField();
		editing.addEditor(textCol, editor);
		//final SetValueValidator setValueValidator = new SetValueValidator(model);	
		editing.addCompleteEditHandler(new CompleteEditHandler<BucketLabelTermComment>() {
			@Override
			public void onCompleteEdit(CompleteEditEvent<BucketLabelTermComment> event) {			
				GridCell cell = event.getEditCell();
				final BucketLabelTermComment comment = grid.getStore().get(cell.getRow());
				ColumnConfig<BucketLabelTermComment, String> config = grid.getColumnModel().getColumn(cell.getCol());
				/*if(config.equals(textCol)) {
					switch(comment.getType()) {
						case taxonCharacterValueType:
							Value oldValue = (Value)comment.getObject();
							Character character = model.getTaxonMatrix().getCharacter(oldValue);
							Taxon taxon = model.getTaxonMatrix().getTaxon(oldValue);
							String value = config.getValueProvider().getValue(comment);
							
							ValidationResult validationResult = setValueValidator.validValue(value, character);
							if(validationResult.isValid()) {
								Value newValue = new Value(value);
								comment.setObject(newValue);
								eventBus.fireEvent(new SetValueEvent(taxon, character, oldValue, newValue));
								subModelBus.fireEvent(new SetValueEvent(taxon, character, oldValue, newValue));
							} else {
								AlertMessageBox alert = new AlertMessageBox("Set value failed", "Can't set value " +
										value + " for " + character.getName() + " of " +  taxon.getFullName() + ". Control mode " + 
										model.getControlMode(character).toString().toLowerCase() + " was selected for " + character.getName());
								alert.show();
							}
							
							break;
						default:
							break;
					}
				}*/
				if(config.equals(textCol)) {
					Comment newComment = new Comment(Oto.user, editor.getValue());
					collectionService.addComment(newComment, comment.getTerm().getId(), new RPCCallback<Comment>() {
						@Override
						public void onSuccess(Comment result) {
							eventBus.fireEvent(new CommentEvent(comment.getTerm(), result));
							String comment = Format.ellipse(editor.getValue(), 80);
							String message = Format.substitute("'{0}' saved", new Params(comment));
							Info.display("Comment", message);
						}
					});
				}
			}
		});

		setBodyBorder(false);
		setHeadingText("Comments");
		setWidth(800);
		setHeight(600);
		setHideOnButtonClick(true);
		setModal(true);
		setMaximizable(true);

		ContentPanel panel = new ContentPanel();
		panel.add(grid);
		this.add(panel);
	}

	private Menu createContextMenu() {
		Menu menu = new Menu();
		MenuItem removeItem = new MenuItem("Remove");
		menu.add(removeItem);
		removeItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				for (BucketLabelTermComment comment : grid.getSelectionModel()
						.getSelectedItems()) {
					commentStore.remove(comment);
					eventBus.fireEvent(new CommentEvent(comment.getTerm(), new Comment(Oto.user, null)));
				}
			}
		});
		return menu;
	}

	private List<BucketLabelTermComment> createComments() {
		List<BucketLabelTermComment> comments = new LinkedList<BucketLabelTermComment>();
		for(Bucket bucket : collection.getBuckets()) {
			for(Term term : bucket.getTerms()) {
				for(Comment comment : term.getComments()) {
					comments.add(new BucketLabelTermComment(bucket, collection.getLabels(term), term, comment));
				}
			}
		}
		
		return comments;
	}

}
