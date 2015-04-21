package edu.arizona.biosemantics.oto2.steps.client.toontology;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.cell.core.client.form.CheckBoxCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;

import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.Term;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmissionStatus;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmissionProperties;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmissionStatus;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmissionStatusProperties;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;

public class SynonymSubmissionsGrid extends Grid<OntologySynonymSubmission> {

	private static ColumnConfig<OntologySynonymSubmission, String> ontologyCol;
	private static OntologySynonymSubmissionProperties ontologySynonymSubmissionProperties = GWT.create(OntologySynonymSubmissionProperties.class);
	private static OntologySynonymSubmissionStatusProperties ontologySynonymSubmissionStatusProperties = GWT.create(OntologySynonymSubmissionStatusProperties.class);
	private static CheckBoxSelectionModel<OntologySynonymSubmission> checkBoxSelectionModel;
	
	private ListStore<OntologySynonymSubmission> synonymSubmissionStore =
			new ListStore<OntologySynonymSubmission>(ontologySynonymSubmissionProperties.key());
	
	public SynonymSubmissionsGrid(ListStore<OntologySynonymSubmission> synonymSubmissionStore) {
		super(synonymSubmissionStore, createColumnModel());
		
		final GroupingView<OntologySynonymSubmission> groupingView = new GroupingView<OntologySynonymSubmission>();
		groupingView.setShowGroupedColumn(false);
		groupingView.setForceFit(true);
		groupingView.groupBy(ontologyCol);
		
		setView(groupingView);
		//grid.setContextMenu(createArticulationsContextMenu());
	
		setSelectionModel(checkBoxSelectionModel);
		//grid.getView().setAutoExpandColumn(taxonBCol);
		//grid.setBorders(false);
		getView().setStripeRows(true);
		getView().setColumnLines(true);
	}

	private static ColumnModel<OntologySynonymSubmission> createColumnModel() {
		IdentityValueProvider<OntologySynonymSubmission> identity = new IdentityValueProvider<OntologySynonymSubmission>();
		checkBoxSelectionModel = new CheckBoxSelectionModel<OntologySynonymSubmission>(
				identity);
		checkBoxSelectionModel.setSelectionMode(SelectionMode.MULTI);

		/*ColorableCell colorableCell = new ColorableCell(eventBus, model);
		colorableCell.setCommentColorizableObjectsStore(articulationsStore, new CommentColorizableObjectsProvider() {
			@Override
			public Object provide(Object source) {
				return source;
			}
		}); */
		ValueProvider<OntologySynonymSubmission, String> termValueProvider = new ValueProvider<OntologySynonymSubmission, String>() {
			@Override
			public String getValue(OntologySynonymSubmission object) {
				return object.getTerm().getTerm();
			}
			@Override
			public void setValue(OntologySynonymSubmission object, String value) {	}
			@Override
			public String getPath() {
				return "term-term";
			}
		};
		final ColumnConfig<OntologySynonymSubmission, String> termCol = new ColumnConfig<OntologySynonymSubmission, String>(
				termValueProvider, 200, "Candidate Term");

		
		final ColumnConfig<OntologySynonymSubmission, String> submissionTermCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontologySynonymSubmissionProperties.submissionTerm(), 200, "Term");
		//taxonACol.setCell(colorableCell);
		
		ValueProvider<OntologySynonymSubmission, String> categoryValueProvider = new ValueProvider<OntologySynonymSubmission, String>() {
			@Override
			public String getValue(OntologySynonymSubmission object) {
				return object.getTerm().getCategory();
			}
			@Override
			public void setValue(OntologySynonymSubmission object, String value) {	}
			@Override
			public String getPath() {
				return "term-category";
			}
		};
		final ColumnConfig<OntologySynonymSubmission, String> categoryCol = new ColumnConfig<OntologySynonymSubmission, String>(
				categoryValueProvider, 200, "Category");
		//relationCol.setCell(colorableCell);
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
		/*ontologyCol = new ColumnConfig<OntologyClassSubmission, Ontology>(
				ontologyClassSubmissionProperties.targetOntology(), 200, "Ontology");
		ontologyCol.setCell(new AbstractCell<Ontology>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					Ontology value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromSafeConstant(value.getName()));
			}
		}); */
		
		final ColumnConfig<OntologySynonymSubmission, String> classCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontologySynonymSubmissionProperties.classIRI(), 200, "Superclass");
		final ColumnConfig<OntologySynonymSubmission, String> synonymsCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontologySynonymSubmissionProperties.synonyms(), 200, "Synonyms");
		final ColumnConfig<OntologySynonymSubmission, String> sourceCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontologySynonymSubmissionProperties.source(), 200, "Source");
		final ColumnConfig<OntologySynonymSubmission, String> sampleCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontologySynonymSubmissionProperties.sampleSentence(), 200, "Sample Sentence");
		final ColumnConfig<OntologySynonymSubmission, String> statusCol = new ColumnConfig<OntologySynonymSubmission, String>(
				new ValueProvider<OntologySynonymSubmission, String>() {
					@Override
					public String getValue(OntologySynonymSubmission object) {
						String status = "";
						for(OntologySynonymSubmissionStatus ontologyClassSubmissionStatus : object.getSubmissionStatuses()) {
							//if(edu.arizona.biosemantics.oto2.steps.shared.model.toontology.Status.valueOf(ontologyClassSubmissionStatus.getStatus().getName().toUpperCase())
							//		.equals(edu.arizona.biosemantics.oto2.steps.shared.model.toontology.Status.ACCEPTED))
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
		final ColumnConfig<OntologySynonymSubmission, String> iriCol = new ColumnConfig<OntologySynonymSubmission, String>(
				new ValueProvider<OntologySynonymSubmission, String>() {
					@Override
					public String getValue(OntologySynonymSubmission object) {
						for(OntologySynonymSubmissionStatus ontologyClassSubmissionStatus : object.getSubmissionStatuses()) {
							if(edu.arizona.biosemantics.oto2.steps.shared.model.toontology.Status.valueOf(ontologyClassSubmissionStatus.getStatus().getName().toUpperCase())
									.equals(edu.arizona.biosemantics.oto2.steps.shared.model.toontology.Status.ACCEPTED))
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
		final ColumnConfig<OntologySynonymSubmission, String> userCol = new ColumnConfig<OntologySynonymSubmission, String>(
				ontologySynonymSubmissionProperties.user(), 200, "User");
		
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
		columns.add(classCol);
		columns.add(synonymsCol);
		columns.add(sourceCol);
		columns.add(sampleCol);
		columns.add(statusCol);
		columns.add(iriCol);
		columns.add(userCol);
		
		ColumnModel<OntologySynonymSubmission> cm = new ColumnModel<OntologySynonymSubmission>(columns);
		return cm;
	}
	
}
