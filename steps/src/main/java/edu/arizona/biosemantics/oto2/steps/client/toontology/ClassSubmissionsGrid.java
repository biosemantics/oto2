package edu.arizona.biosemantics.oto2.steps.client.toontology;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.cell.core.client.form.CheckBoxCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;

import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.Term;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmissionProperties;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmissionStatusProperties;

public class ClassSubmissionsGrid extends Grid<OntologyClassSubmission> {

	private static ColumnConfig<OntologyClassSubmission, String> ontologyCol;
	private static OntologyClassSubmissionProperties ontologyClassSubmissionProperties = GWT.create(OntologyClassSubmissionProperties.class);
	private static OntologyClassSubmissionStatusProperties ontologyClassSubmissionStatusProperties = GWT.create(OntologyClassSubmissionStatusProperties.class);
	private static CheckBoxSelectionModel<OntologyClassSubmission> checkBoxSelectionModel;
	
	private ListStore<OntologyClassSubmission> classSubmissionStore =
			new ListStore<OntologyClassSubmission>(ontologyClassSubmissionProperties.key());
	
	public ClassSubmissionsGrid(ListStore<OntologyClassSubmission> classSubmissionStore) {
		super(classSubmissionStore, createColumnModel());
		
		final GroupingView<OntologyClassSubmission> groupingView = new GroupingView<OntologyClassSubmission>();
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

	private static ColumnModel<OntologyClassSubmission> createColumnModel() {
		IdentityValueProvider<OntologyClassSubmission> identity = new IdentityValueProvider<OntologyClassSubmission>();
		checkBoxSelectionModel = new CheckBoxSelectionModel<OntologyClassSubmission>(
				identity);
		checkBoxSelectionModel.setSelectionMode(SelectionMode.MULTI);

		/*ColorableCell colorableCell = new ColorableCell(eventBus, model);
		colorableCell.setCommentColorizableObjectsStore(articulationsStore, new CommentColorizableObjectsProvider() {
			@Override
			public Object provide(Object source) {
				return source;
			}
		}); */
		ValueProvider<OntologyClassSubmission, String> termValueProvider = new ValueProvider<OntologyClassSubmission, String>() {
			@Override
			public String getValue(OntologyClassSubmission object) {
				return object.getTerm().getTerm();
			}
			@Override
			public void setValue(OntologyClassSubmission object, String value) {	}
			@Override
			public String getPath() {
				return "term-term";
			}
		};
		final ColumnConfig<OntologyClassSubmission, String> termCol = new ColumnConfig<OntologyClassSubmission, String>(
				termValueProvider, 200, "Candidate Term");

		
		final ColumnConfig<OntologyClassSubmission, String> submissionTermCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.submissionTerm(), 200, "Term");
		//taxonACol.setCell(colorableCell);
		
		ValueProvider<OntologyClassSubmission, String> categoryValueProvider = new ValueProvider<OntologyClassSubmission, String>() {
			@Override
			public String getValue(OntologyClassSubmission object) {
				return object.getTerm().getCategory();
			}
			@Override
			public void setValue(OntologyClassSubmission object, String value) {	}
			@Override
			public String getPath() {
				return "term-category";
			}
		};
		final ColumnConfig<OntologyClassSubmission, String> categoryCol = new ColumnConfig<OntologyClassSubmission, String>(
				categoryValueProvider, 200, "Category");

		//relationCol.setCell(colorableCell);
		ValueProvider<OntologyClassSubmission, String> ontlogyPrefixValueProvider = new ValueProvider<OntologyClassSubmission, String>() {
			@Override
			public String getValue(OntologyClassSubmission object) {
				return object.getOntology().getPrefix();
			}
			@Override
			public void setValue(OntologyClassSubmission object, String value) {	}
			@Override
			public String getPath() {
				return "ontology-prefix";
			}
		};
		ontologyCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontlogyPrefixValueProvider, 200, "Ontology");
		/*ontologyCol = new ColumnConfig<OntologyClassSubmission, Ontology>(
				ontologyClassSubmissionProperties.targetOntology(), 200, "Ontology");
		ontologyCol.setCell(new AbstractCell<Ontology>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					Ontology value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromSafeConstant(value.getName()));
			}
		}); */
		final ColumnConfig<OntologyClassSubmission, String> superClassCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.superclassIRI(), 200, "Superclass");
		final ColumnConfig<OntologyClassSubmission, String> definitionCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.definition(), 200, "Defintion");
		final ColumnConfig<OntologyClassSubmission, String> synonymsCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.synonyms(), 200, "Synonyms");
		final ColumnConfig<OntologyClassSubmission, String> sourceCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.source(), 200, "Source");
		final ColumnConfig<OntologyClassSubmission, String> sampleCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.sampleSentence(), 200, "Sample Sentence");
		final ColumnConfig<OntologyClassSubmission, String> partOfCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.partOfIRI(), 200, "Part Of");
		final ColumnConfig<OntologyClassSubmission, Boolean> entityCol = new ColumnConfig<OntologyClassSubmission, Boolean>(
				ontologyClassSubmissionProperties.entity(), 200, "Entityt");
		entityCol.setCell(new CheckBoxCell());
		final ColumnConfig<OntologyClassSubmission, Boolean> qualityCol = new ColumnConfig<OntologyClassSubmission, Boolean>(
				ontologyClassSubmissionProperties.quality(), 200, "Quality");
		qualityCol.setCell(new CheckBoxCell());
		
		
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
		
		List<ColumnConfig<OntologyClassSubmission, ?>> columns = new ArrayList<ColumnConfig<OntologyClassSubmission, ?>>();
		columns.add(checkBoxSelectionModel.getColumn());
		columns.add(termCol);
		columns.add(submissionTermCol);
		columns.add(categoryCol);
		columns.add(ontologyCol);
		columns.add(superClassCol);
		columns.add(definitionCol);
		columns.add(synonymsCol);
		columns.add(sourceCol);
		columns.add(sampleCol);
		columns.add(partOfCol);
		columns.add(entityCol);
		columns.add(qualityCol);

		ColumnModel<OntologyClassSubmission> cm = new ColumnModel<OntologyClassSubmission>(columns);
		return cm;
	}
	
}
