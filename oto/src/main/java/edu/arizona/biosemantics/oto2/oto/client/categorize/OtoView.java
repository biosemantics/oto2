package edu.arizona.biosemantics.oto2.oto.client.categorize;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sencha.gxt.widget.core.client.menu.Item;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.NorthSouthContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DualListField;
import com.sencha.gxt.widget.core.client.form.DualListField.Mode;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBar;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.oto.client.categorize.event.LoadEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.OntologiesSelectEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.SaveEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.IOntologyService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.IOntologyServiceAsync;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.RPCCallback;

public class OtoView implements IsWidget {

	public class MenuView extends MenuBar {

		private final OntologyProperties ontologyProperties = GWT.create(OntologyProperties.class);

		public class SelectOntologiesDialog extends Dialog {
			
			public SelectOntologiesDialog() {
				setHeadingText("Ontologies to Search");
				setPredefinedButtons(PredefinedButton.OK);
				setBodyStyleName("pad-text");
				getBody().addClassName("pad-text");
				setHideOnButtonClick(false);
				setWidth(500);
				setHeight(500);
												
				final DualListField<Ontology, String> dialListField = new DualListField<Ontology, String>(
						unselectedListStore, selectedListStore, ontologyProperties.acronym(), new TextCell());
				dialListField.setMode(Mode.INSERT);
				dialListField.addValidator(new Validator<List<Ontology>>() {
					@Override
					public List<EditorError> validate(Editor<List<Ontology>> editor, List<Ontology> value) {
						 if (value.size() <= 10) // || value.containsAll(ontologies) ||) 
							 return null;
						 else {
						      List<EditorError> errors = new ArrayList<EditorError>();
						      // errors.add(new DefaultEditorError(editor, "You have to select either all, or <= 10 ontologies.", ""));
						       errors.add(new DefaultEditorError(editor, "You can't select more than 10 ontologies.", ""));
						      return errors;
						 }
					}
				});
				dialListField.setEnableDnd(true);
				add(dialListField);

				this.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						if(dialListField.validate()) {
							List<Ontology> selectedOntologies = selectedListStore.getAll();
							Set<Ontology> ontologies = new LinkedHashSet<Ontology>(selectedOntologies.size());
							ontologies.addAll(selectedOntologies);
							eventBus.fireEvent(new OntologiesSelectEvent(ontologies));
							hide();
						}
					}
				});
			}

		}
		
		private final IOntologyServiceAsync ontologyService = GWT.create(IOntologyService.class);
		private ListStore<Term> termStore = new ListStore<Term>(termProperties.key());
		private EventBus eventBus;
		protected Set<Ontology> ontologies;
		private ListStore<Ontology> unselectedListStore = new ListStore<Ontology>(ontologyProperties.key());
		private ListStore<Ontology> selectedListStore = new ListStore<Ontology>(ontologyProperties.key());
		private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);

		public MenuView(final EventBus eventBus) {
			this.eventBus = eventBus;
			addStyleName(ThemeStyles.get().style().borderBottom());

			Menu sub = new Menu();
			MenuBarItem item = new MenuBarItem("Collection", sub);
			MenuItem resetItem = new MenuItem("Reset");
			resetItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					collectionService.reset(collection, new RPCCallback<Collection>() {
						@Override
						public void onSuccess(Collection result) {
							eventBus.fireEvent(new LoadEvent(collection));
						}
					});
				}
			});
			MenuItem saveItem = new MenuItem("Save");
			saveItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					eventBus.fireEvent(new SaveEvent(collection));
				}
			});
			sub.add(resetItem);
			sub.add(saveItem);
			add(item);

			sub = new Menu();
			final ComboBox<Term> searchCombo = new ComboBox<Term>(termStore,
					termProperties.nameLabel());
			searchCombo.setForceSelection(true);
			searchCombo.setTriggerAction(TriggerAction.ALL);
			searchCombo.addSelectionHandler(new SelectionHandler<Term>() {
				@Override
				public void onSelection(SelectionEvent<Term> arg0) {
					eventBus.fireEvent(new TermSelectEvent(arg0
							.getSelectedItem()));
				}
			});
			sub.add(searchCombo);
			item = new MenuBarItem("Search", sub);
			add(item);
			
			sub = new Menu();
			MenuItem selectOntologies = new MenuItem("Select");
			selectOntologies.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					Dialog dialog = new SelectOntologiesDialog();
					dialog.show();
				}
			});
			sub.add(selectOntologies);
			item = new MenuBarItem("Ontologies", sub);
			add(item);

			sub = new Menu();
			MenuBarItem questionsItem = new MenuBarItem("?", sub);
			MenuItem helpItem = new MenuItem("Help");
			helpItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> arg0) {
					final Dialog dialog = new Dialog();
					dialog.setBodyBorder(false);
					dialog.setHeadingText("Help");
					dialog.setHideOnButtonClick(true);
					dialog.setWidget(new HelpView());
					dialog.setWidth(400);
					dialog.setHeight(225);
					dialog.setResizable(false);
					dialog.setShadow(true);
					dialog.show();
				}
			});
			sub.add(helpItem);
			add(questionsItem);
			
			selectedListStore.addSortInfo(new StoreSortInfo<Ontology>(ontologyProperties.acronym(), SortDir.ASC));
			unselectedListStore.addSortInfo(new StoreSortInfo<Ontology>(ontologyProperties.acronym(), SortDir.ASC));
		}

		public void setCollection(Collection collection) {
			termStore.clear();
			termStore.addAll(collection.getTerms());
			termStore.addSortInfo(new StoreSortInfo<Term>(
					new Term.TermComparator(), SortDir.ASC));
			
			//already store ontologies, otherwise delay when requested on button press
			ontologyService.getOntologies(new RPCCallback<Set<Ontology>>() {
				@Override
				public void onSuccess(Set<Ontology> result) {
					ontologies = result;
					unselectedListStore.clear();
					unselectedListStore.addAll(result);
					eventBus.fireEvent(new OntologiesSelectEvent(new LinkedHashSet<Ontology>()));
				}
			});
		}
	}

	public static class CategorizeView extends BorderLayoutContainer {

		private int portalColumnCount = 6;
		private TermsView termsView;
		private LabelsView labelsView;
		private TermInfoView termInfoView;

		public CategorizeView(EventBus eventBus) {
			termsView = new TermsView(eventBus);
			labelsView = new LabelsView(eventBus, portalColumnCount);
			termInfoView = new TermInfoView(eventBus);

			ContentPanel cp = new ContentPanel();
			cp.setHeadingText("Uncategorized Terms");
			cp.add(termsView);
			BorderLayoutData d = new BorderLayoutData(.20);
			// d.setMargins(new Margins(0, 1, 1, 1));
			d.setCollapsible(true);
			d.setSplit(true);
			d.setCollapseMini(true);
			setWestWidget(cp, d);

			cp = new ContentPanel();
			cp.setHeadingText("Categorization");
			cp.add(labelsView);
			cp.setContextMenu(labelsView.new LabelsMenu());
			d = new BorderLayoutData();
			d.setMargins(new Margins(0, 0, 0, 0));
			setCenterWidget(cp, d);

			cp = new ContentPanel();
			cp.setHeadingText("Term Information");
			cp.add(termInfoView);
			d = new BorderLayoutData(.40);
			d.setMargins(new Margins(0, 0, 20, 0));
			d.setCollapsible(true);
			d.setSplit(true);
			d.setCollapseMini(true);
			setSouthWidget(cp, d);

			// cp = new ContentPanel();
			/*
			 * cp.setHeadingText("Search"); d = new BorderLayoutData(.20);
			 * //d.setMargins(new Margins(1)); d.setCollapsible(true);
			 * d.setSplit(true); d.setCollapseMini(true);
			 * setNorthWidget(getMenu(), d);
			 */
		}

		public void setCollection(Collection collection) {
			termsView.setCollection(collection);
			labelsView.setCollection(collection);
			termInfoView.setCollection(collection);
		}

	}
	
	private EventBus eventBus;

	private MenuView menuView;
	private CategorizeView categorizeView;
	private Collection collection;
	private VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
	private static final TermProperties termProperties = GWT
			.create(TermProperties.class);

	public OtoView(EventBus eventBus) {
		this.eventBus = eventBus;
		categorizeView = new CategorizeView(eventBus);
		menuView = new MenuView(eventBus);

		verticalLayoutContainer.add(menuView,new VerticalLayoutData(1,-1));
		verticalLayoutContainer.add(categorizeView,new VerticalLayoutData(1,1));
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
		categorizeView.setCollection(collection);
		menuView.setCollection(collection);
	}

	@Override
	public Widget asWidget() {
		return verticalLayoutContainer;
	}

}
