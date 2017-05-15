package edu.arizona.biosemantics.oto2.ontologize.client.content.submissions;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;

import edu.arizona.biosemantics.oto2.ontologize.client.Ontologize;
import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.common.ColorableCell;
import edu.arizona.biosemantics.oto2.ontologize.client.event.AddCommentEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.OntologySynonymSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SetColorEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologySynonymsSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Color;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Colorable;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Comment;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Commentable;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmissionProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmissionStatus;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmissionStatusProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SubmissionType;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class SynonymSubmissionsGrid implements IsWidget {
	
	public static interface SubmissionsFilter {

		public boolean isFiltered(OntologySynonymSubmission ontologySynonymSubmission);
		
	}

	private DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	private ColumnConfig<OntologySynonymSubmission, String> ontologyCol;
	private OntologySynonymSubmissionProperties ontologySynonymSubmissionProperties = GWT.create(OntologySynonymSubmissionProperties.class);
	private CheckBoxSelectionModel<OntologySynonymSubmission> checkBoxSelectionModel;
	
	private EventBus eventBus;
	private Grid<OntologySynonymSubmission> grid;
	protected ColumnConfig<OntologySynonymSubmission, String> termCol;
	protected ColumnConfig<OntologySynonymSubmission, String> submissionTermCol;
	protected ColumnConfig<OntologySynonymSubmission, String> categoryCol;
	protected ColumnConfig<OntologySynonymSubmission, String> synonymsCol;
	protected ColumnConfig<OntologySynonymSubmission, String> classIriCol;
	protected ColumnConfig<OntologySynonymSubmission, String> classLabelCol;
	protected ColumnConfig<OntologySynonymSubmission, String> sampleCol;
	protected ColumnConfig<OntologySynonymSubmission, String> sourceCol;
	protected ColumnConfig<OntologySynonymSubmission, String> statusCol;
	protected ColumnConfig<OntologySynonymSubmission, String> iriCol;
	protected ColumnConfig<OntologySynonymSubmission, String> userCol;
	protected ColumnConfig<OntologySynonymSubmission, String> createdCol;
	protected ColumnConfig<OntologySynonymSubmission, String> updatedCol;
	private ListStore<OntologySynonymSubmission> synonymSubmissionStore =
			new ListStore<OntologySynonymSubmission>(ontologySynonymSubmissionProperties.key());
	private SubmissionType submissionType;
	private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<OntologySynonymSubmission>> remoteLoader;
	private VerticalLayoutContainer verticalLayoutContainer;
	
	public SynonymSubmissionsGrid(EventBus eventBus, final SubmissionType submissionType) {
		this.eventBus = eventBus;
		this.submissionType = submissionType;
		
		RpcProxy<FilterPagingLoadConfig, PagingLoadResult<OntologySynonymSubmission>> rpcProxy = new RpcProxy<FilterPagingLoadConfig, PagingLoadResult<OntologySynonymSubmission>>() {
			@Override
			public void load(FilterPagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<OntologySynonymSubmission>> callback) {
				toOntologyService.getSynonymSubmissions(ModelController.getCollection(), loadConfig, submissionType, callback);
			}
		};
		remoteLoader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<OntologySynonymSubmission>>(rpcProxy);
		remoteLoader.useLoadConfig(new FilterPagingLoadConfigBean());
		remoteLoader.setRemoteSort(true);
		remoteLoader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, 
				OntologySynonymSubmission, PagingLoadResult<OntologySynonymSubmission>>(synonymSubmissionStore));
		remoteLoader.addSortInfo(new SortInfoBean(ontologySynonymSubmissionProperties.submissionTerm(), SortDir.ASC));
		grid = new Grid<OntologySynonymSubmission>(synonymSubmissionStore, createColumnModel(synonymSubmissionStore));
		grid.setLoader(remoteLoader);
		
		final GroupingView<OntologySynonymSubmission> groupingView = new GroupingView<OntologySynonymSubmission>();
		groupingView.setShowGroupedColumn(false);
		groupingView.setForceFit(true);
		groupingView.groupBy(classLabelCol);
		
		grid.setView(groupingView);
		grid.setContextMenu(createSynonymSubmissionsContextMenu());
	
		grid.setSelectionModel(checkBoxSelectionModel);
		//grid.getView().setAutoExpandColumn(taxonBCol);
		//grid.setBorders(false);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		
		StringFilter<OntologySynonymSubmission> submissionTermFilter = new StringFilter<OntologySynonymSubmission>(ontologySynonymSubmissionProperties.submissionTerm());
		//StringFilter<OntologySynonymSubmission> nameFilter = new StringFilter<OntologySynonymSubmission>(ontologySynonymSubmissionProperties.classLabel());
	    
		GridFilters<OntologySynonymSubmission> filters = new GridFilters<OntologySynonymSubmission>(remoteLoader);
	    filters.initPlugin(grid);
	    filters.addFilter(submissionTermFilter);
	    
		final PagingToolBar toolBar = new PagingToolBar(25);
	    toolBar.setBorders(false);
	    toolBar.bind(remoteLoader);
		
		verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.add(grid, new VerticalLayoutData(1, 1));
		verticalLayoutContainer.add(toolBar, new VerticalLayoutData(1, -1));
		
		bindEvents();
	}
	

	private Menu createSynonymSubmissionsContextMenu() {
		final Menu menu = new Menu();
		
		menu.addBeforeShowHandler(new BeforeShowHandler() {
			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				menu.clear();
				final List<OntologySynonymSubmission> selected = checkBoxSelectionModel.getSelectedItems();
				if(!selected.isEmpty()) {
					menu.add(createRemoveItem(selected));
					menu.add(new HeaderMenuItem("Annotation"));
					menu.add(createCommentItem(selected));
					if(!ModelController.getCollection().getColors().isEmpty()) {
						menu.add(createColorizeItem(selected));
					} 
				}
				event.setCancelled(menu.getWidgetCount() == 0);
			}
			
					
			private Widget createColorizeItem(final List<OntologySynonymSubmission> selected) {
				final MenuItem colorizeItem = new MenuItem("Colorize");
					
				Menu colorMenu = new Menu();
				MenuItem offItem = new MenuItem("None");
				offItem.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						ModelController.getCollection().setColorizations((java.util.Collection)selected, null);
						collectionService.update(ModelController.getCollection(), new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.failedToSetColor();
							}
							@Override
							public void onSuccess(Void result) {
								eventBus.fireEvent(new SetColorEvent(selected, null, true));
							}
						});
					}
				});
				colorMenu.add(offItem);
				for(final Color color : ModelController.getCollection().getColors()) {
					MenuItem colorItem = new MenuItem(color.getUse());
					colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
					colorItem.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							ModelController.getCollection().setColorizations((java.util.Collection)selected, color);
							collectionService.update(ModelController.getCollection(), new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
									Alerter.failedToSetColor();
								}
								@Override
								public void onSuccess(Void result) {
									eventBus.fireEvent(new SetColorEvent(selected, color, true));
								}
							});
						}
					});
					colorMenu.add(colorItem);
				}
				
				colorizeItem.setSubMenu(colorMenu);
				return colorizeItem;
			}


			private Widget createCommentItem(final List<OntologySynonymSubmission> selected) {
				MenuItem comment = new MenuItem("Comment");
				final OntologySynonymSubmission ontologySynonymSubmission = selected.get(0);
				comment.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");
						box.getTextArea().setValue(getUsersComment(ontologySynonymSubmission));
						box.addHideHandler(new HideHandler() {
							@Override
							public void onHide(HideEvent event) {
								final Comment newComment = new Comment(Ontologize.user, box.getValue());
								ModelController.getCollection().addComments((java.util.Collection)selected, newComment);
								collectionService.update(ModelController.getCollection(), new AsyncCallback<Void>() {
									@Override
									public void onFailure(Throwable caught) {
										Alerter.addCommentFailed(caught);
									}
									@Override
									public void onSuccess(Void result) {
										eventBus.fireEvent(new AddCommentEvent(
												(java.util.Collection)selected, newComment));
										String comment = Format.ellipse(box.getValue(), 80);
										String message = Format.substitute("'{0}' saved", new Params(comment));
										Info.display("Comment", message);
									}
								});
							}
						});
						box.show();
					}

					private String getUsersComment(
							OntologySynonymSubmission ontologySynonymSubmission) {
						// TODO Auto-generated method stub
						return null;
					}
				});
				return comment;
			}


			private Widget createRemoveItem(List<OntologySynonymSubmission> selected) {
				MenuItem deleteItem = new MenuItem("Remove");
				deleteItem.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						String resultText = "";
						for(OntologySynonymSubmission submission : grid.getSelectionModel().getSelectedItems()) {
							resultText += submission.getSubmissionTerm() + "<br>";
							toOntologyService.removeSynonymSubmission(ModelController.getCollection(), 
									submission, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Alerter.failedToRemoveOntologyClassSubmission();
								}
								@Override
								public void onSuccess(Void result) {
									eventBus.fireEvent(new RemoveOntologySynonymSubmissionsEvent(grid.getSelectionModel().getSelectedItems()));
								}
							});
						}
						Info.display(SafeHtmlUtils.fromSafeConstant("Synonym removed"), SafeHtmlUtils.fromSafeConstant(resultText));
					}
				});
				return deleteItem;
			}
		});
		return menu;
	}

	private void bindEvents() {
		eventBus.addHandler(CreateOntologySynonymSubmissionEvent.TYPE, new CreateOntologySynonymSubmissionEvent.Handler() {
			@Override
			public void onSubmission(CreateOntologySynonymSubmissionEvent event) {
				remoteLoader.load();
			}
		});
		eventBus.addHandler(RemoveOntologySynonymSubmissionsEvent.TYPE, new RemoveOntologySynonymSubmissionsEvent.Handler() {
			@Override
			public void onRemove(RemoveOntologySynonymSubmissionsEvent event) {
				remoteLoader.load();
			}
		});
		eventBus.addHandler(UpdateOntologySynonymsSubmissionsEvent.TYPE, new UpdateOntologySynonymsSubmissionsEvent.Handler() {
			@Override
			public void onUpdate(UpdateOntologySynonymsSubmissionsEvent event) {
				remoteLoader.load();
			}
		});
		
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				remoteLoader.load();
			}
		});
		getSelectionModel().addSelectionHandler(new SelectionHandler<OntologySynonymSubmission>() {
			@Override
			public void onSelection(SelectionEvent<OntologySynonymSubmission> event) {
				eventBus.fireEvent(new OntologySynonymSubmissionSelectEvent(event.getSelectedItem()));
			}
		});
		
		eventBus.addHandler(SetColorEvent.TYPE, new SetColorEvent.SetColorEventHandler() {
			@Override
			public void onSet(SetColorEvent event) {
				for(Object object : event.getObjects()) {
					if(object instanceof OntologySynonymSubmission) {
						OntologySynonymSubmission ontologySynonymSubmission = (OntologySynonymSubmission)object;
						if(grid.getStore().findModel(ontologySynonymSubmission) != null)
							grid.getStore().update(ontologySynonymSubmission);
					}
				}
			}
		});
	}


	private ColumnModel<OntologySynonymSubmission> createColumnModel(ListStore<OntologySynonymSubmission> synonymSubmissionStore) {
		IdentityValueProvider<OntologySynonymSubmission> identity = new IdentityValueProvider<OntologySynonymSubmission>();
		checkBoxSelectionModel = new CheckBoxSelectionModel<OntologySynonymSubmission>(
				identity);
		checkBoxSelectionModel.setSelectionMode(SelectionMode.MULTI);

		ColorableCell colorableCell = new ColorableCell();
		colorableCell.setCommentColorizableObjectsStore(synonymSubmissionStore, new ColorableCell.CommentableColorableProvider() {
			@Override
			public Colorable provideColorable(Object source) {
				return (OntologySynonymSubmission)source;
			}
			@Override
			public Commentable provideCommentable(Object source) {
				return (OntologySynonymSubmission)source;
			}
		});
		ValueProvider<OntologySynonymSubmission, String> termValueProvider = new ValueProvider<OntologySynonymSubmission, String>() {
			@Override
			public String getValue(OntologySynonymSubmission object) {
				if(!object.hasTerm()) 
					return "";
				return object.getTerm().getTerm();
			}
			@Override
			public void setValue(OntologySynonymSubmission object, String value) {	}
			@Override
			public String getPath() {
				return "term-term";
			}
		};
		termCol = new ColumnConfig<OntologySynonymSubmission, String>(
				termValueProvider, 200, "Candidate Term");
		termCol.setCell(colorableCell);

		
		submissionTermCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontologySynonymSubmissionProperties.submissionTerm(), 200, "Term");
		submissionTermCol.setCell(colorableCell);
		
		ValueProvider<OntologySynonymSubmission, String> categoryValueProvider = new ValueProvider<OntologySynonymSubmission, String>() {
			@Override
			public String getValue(OntologySynonymSubmission object) {
				if(!object.hasTerm())
					return "";
				return object.getTerm().getCategory();
			}
			@Override
			public void setValue(OntologySynonymSubmission object, String value) {	}
			@Override
			public String getPath() {
				return "term-category";
			}
		};
		categoryCol = new ColumnConfig<OntologySynonymSubmission, String>(
				categoryValueProvider, 200, "Category");
		categoryCol.setCell(colorableCell);
		ValueProvider<OntologySynonymSubmission, String> ontlogyAcronymValueProvider = new ValueProvider<OntologySynonymSubmission, String>() {
			@Override
			public String getValue(OntologySynonymSubmission object) {
				return object.getOntology().getAcronym();
			}
			@Override
			public void setValue(OntologySynonymSubmission object, String value) {	}
			@Override
			public String getPath() {
				return "ontology-prefix";
			}
		};
		ontologyCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontlogyAcronymValueProvider, 200, "Ontology");
		ontologyCol.setCell(colorableCell);
		/*ontologyCol = new ColumnConfig<OntologyClassSubmission, Ontology>(
				ontologyClassSubmissionProperties.targetOntology(), 200, "Ontology");
		ontologyCol.setCell(new AbstractCell<Ontology>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					Ontology value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromSafeConstant(value.getName()));
			}
		}); */
		
		classIriCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontologySynonymSubmissionProperties.classIRI(), 200, "Class IRI");
		classIriCol.setCell(colorableCell);
		classLabelCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontologySynonymSubmissionProperties.classLabel(), 200, "Class");
		classLabelCol.setCell(colorableCell);
		synonymsCol = new ColumnConfig<OntologySynonymSubmission, String>(
				new ValueProvider<OntologySynonymSubmission, String>() {
					@Override
					public String getValue(OntologySynonymSubmission object) {
						String result = "";
						for(Synonym synonym : object.getSynonyms()) {
							result += synonym.getSynonym() + ", ";
						}
						if(result.length() > 0)
							return result.substring(0, result.length() -2);
						return result;
					}
					@Override
					public void setValue(OntologySynonymSubmission object, String value) {	}
					@Override
					public String getPath() {
						return "synonyms";
					}
					
				}, 200, "Synonyms");
		synonymsCol.setCell(colorableCell);
		sourceCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontologySynonymSubmissionProperties.source(), 200, "Source");
		sourceCol.setCell(colorableCell);
		sampleCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontologySynonymSubmissionProperties.sampleSentence(), 200, "Sample Sentence");
		sampleCol.setCell(colorableCell);
		statusCol = new ColumnConfig<OntologySynonymSubmission, String>(
				new ValueProvider<OntologySynonymSubmission, String>() {
					@Override
					public String getValue(OntologySynonymSubmission object) {
						String status = "";
						for(OntologySynonymSubmissionStatus ontologyClassSubmissionStatus : object.getSubmissionStatuses()) {
							//if(edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Status.valueOf(ontologyClassSubmissionStatus.getStatus().getName().toUpperCase())
							//		.equals(edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Status.ACCEPTED))
								status += ontologyClassSubmissionStatus.getStatus().getName() + ", ";
						}
						return status.length() >= 2 ? status.substring(0, status.length() - 2) : "";
					}
					@Override
					public void setValue(OntologySynonymSubmission object, String value) {	}
					@Override
					public String getPath() {
						return "status";
					}
				}, 200, "Status");
		statusCol.setCell(colorableCell);
		iriCol = new ColumnConfig<OntologySynonymSubmission, String>(
				new ValueProvider<OntologySynonymSubmission, String>() {
					@Override
					public String getValue(OntologySynonymSubmission object) {
						for(OntologySynonymSubmissionStatus ontologyClassSubmissionStatus : object.getSubmissionStatuses()) {
							if(edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.StatusEnum.valueOf(ontologyClassSubmissionStatus.getStatus().getName().toUpperCase())
									.equals(edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.StatusEnum.ACCEPTED))
								return ontologyClassSubmissionStatus.getIri();
						}
						return "";
					}
					@Override
					public void setValue(OntologySynonymSubmission object, String value) {	}
					@Override
					public String getPath() {
						return "status";
					}
				}, 200, "IRI");
		iriCol.setCell(colorableCell);
		userCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontologySynonymSubmissionProperties.user(), 200, "User");
		userCol.setCell(colorableCell);
		
		createdCol = new ColumnConfig<OntologySynonymSubmission, String>(
				new ValueProvider<OntologySynonymSubmission, String>() {
					@Override
					public String getValue(OntologySynonymSubmission object) {
						return dateTimeFormat.format(object.getCreated());
					}
					@Override
					public void setValue(OntologySynonymSubmission object, String value) {	}
					@Override
					public String getPath() {
						return "created";
					}
					
				}, 200, "Created");
		createdCol.setCell(colorableCell);
		updatedCol = new ColumnConfig<OntologySynonymSubmission, String>(
				new ValueProvider<OntologySynonymSubmission, String>() {
					@Override
					public String getValue(OntologySynonymSubmission object) {
						return dateTimeFormat.format(object.getCreated());
					}
					@Override
					public void setValue(OntologySynonymSubmission object, String value) {	}
					@Override
					public String getPath() {
						return "lastUpdated";
					}
					
				}, 200, "Updated");
		updatedCol.setCell(colorableCell);

		
//		ValueProvider<Articulation, String> commentValueProvider = new ValueProvider<Articulation, String>() {
//			@Override
//			public String getValue(Articulation object) {
//				if(model.hasComment(object))
//					return model.getComment(object);
//				return "";
//			}
//			@Override
//			public void setValue(Articulation object, String value) {
//				model.setComment(object, value);
//			}
//			@Override
//			public String getPath() {
//				return "comment";
//			}
//		};
//		
//		final ColumnConfig<Articulation, String> commentCol = new ColumnConfig<Articulation, String>(
//				commentValueProvider, 400, "Comment");
//		commentCol.setCell(colorableCell);
		
//		StringFilter<Articulation> createdFilter = new StringFilter<Articulation>(new ArticulationProperties.CreatedStringValueProvder());
//		StringFilter<Articulation> taxonAFilter = new StringFilter<Articulation>(new ArticulationProperties.TaxonAStringValueProvider());
//		StringFilter<Articulation> taxonBFilter = new StringFilter<Articulation>(new ArticulationProperties.TaxonBStringValueProvider());
//		StringFilter<Articulation> commentFilter = new StringFilter<Articulation>(commentValueProvider);
//		
//		ListFilter<Articulation, ArticulationType> relationFilter = new ListFilter<Articulation, ArticulationType>(
//				articulationProperties.type(), this.allTypesStore);
//
//		GridFilters<Articulation> filters = new GridFilters<Articulation>();
//		filters.addFilter(createdFilter);
//		filters.addFilter(taxonAFilter);
//		filters.addFilter(taxonBFilter);
//		filters.addFilter(relationFilter);
//		filters.addFilter(commentFilter);
//		filters.setLocal(true);
//		filters.initPlugin(grid);
//
//		GridInlineEditing<Articulation> editing = new GridInlineEditing<Articulation>(grid);
//		
//		ComboBox<ArticulationType> relationCombo = createRelationCombo();
//		
//		if(this.relationEditEnabled)
//			editing.addEditor(relationCol, relationCombo);
//		editing.addEditor(commentCol, new TextField());
//		editing.addStartEditHandler(new StartEditHandler<Articulation>() {
//			@Override
//			public void onStartEdit(StartEditEvent<Articulation> event) {
//				Articulation articulation = grid.getStore().get(event.getEditCell().getRow());
//				List<ArticulationType> availableTypes = getAvailableTypes(articulation);
//				availableTypesStore.clear();
//				availableTypesStore.addAll(availableTypes);
//			}
//		});
//		/*editing.addBeforeStartEditHandler(new BeforeStartEditHandler<Articulation>() {
//
//			@Override
//			public void onBeforeStartEdit(
//					BeforeStartEditEvent<Articulation> event) {
//				event.get
//			}
//			
//		}); */
//		editing.addCompleteEditHandler(new CompleteEditHandler<Articulation>() {
//			@Override
//			public void onCompleteEdit(CompleteEditEvent<Articulation> event) {			
//				GridCell cell = event.getEditCell();
//				Articulation articulation = grid.getStore().get(cell.getRow());
//				ColumnConfig<Articulation, ?> config = grid.getColumnModel().getColumn(cell.getCol());
//				if(config.equals(relationCol)) {
//					ArticulationType type = (ArticulationType)config.getValueProvider().getValue(articulation);
//					eventBus.fireEvent(new ModifyArticulationEvent(articulation, type));
//				}
//				if(config.equals(commentCol)) {
//					String comment = (String)config.getValueProvider().getValue(articulation);
//					eventBus.fireEvent(new SetCommentEvent(articulation, comment));
//				}
//			}
//		});
//		
				
		List<ColumnConfig<OntologySynonymSubmission, ?>> columns = new ArrayList<ColumnConfig<OntologySynonymSubmission, ?>>();
		columns.add(checkBoxSelectionModel.getColumn());
		columns.add(termCol);
		columns.add(submissionTermCol);
		columns.add(categoryCol);
		columns.add(ontologyCol);
		columns.add(classLabelCol);
		columns.add(classIriCol);
		columns.add(statusCol);
		columns.add(iriCol);
		columns.add(sampleCol);
		columns.add(sourceCol);
		columns.add(synonymsCol);
		columns.add(userCol);
		columns.add(createdCol);
		columns.add(updatedCol);
		
		setHiddenColumns();
		
		ColumnModel<OntologySynonymSubmission> cm = new ColumnModel<OntologySynonymSubmission>(columns);
		return cm;
	}

	@Override
	public Widget asWidget() {
		return verticalLayoutContainer;
	}


	public GridSelectionModel<OntologySynonymSubmission> getSelectionModel() {
		return grid.getSelectionModel();
	}
	
	protected void setHiddenColumns() {
		termCol.setHidden(true);
		submissionTermCol.setHidden(false);
		categoryCol.setHidden(true);
		classLabelCol.setHidden(false);
		classIriCol.setHidden(true);
		iriCol.setHidden(true);
		sampleCol.setHidden(true);
		sourceCol.setHidden(true);
		synonymsCol.setHidden(false);
		userCol.setHidden(true);
		statusCol.setHidden(true);
		ontologyCol.setHidden(true);
		createdCol.setHidden(false);
		updatedCol.setHidden(true);
	}


	public void refreshHeader() {
		if (grid.getView() != null
				&& grid.getView().getHeader() != null)
			grid.getView().getHeader().refresh();
	}
}
