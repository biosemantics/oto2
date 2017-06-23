package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreHandlers;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.MoveEvent;
import com.sencha.gxt.widget.core.client.event.MoveEvent.MoveHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.DualListField;
import com.sencha.gxt.widget.core.client.form.DualListField.Mode;
import com.sencha.gxt.widget.core.client.form.Validator;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.CompositeModifyEventForSynonymCreator;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.RemoveMode;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ShowRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.TextTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class CreateSynonymsDialog extends Dialog {
	
	public interface VertexProperties extends PropertyAccess<Vertex> {
		  @Path("value")
		  ModelKeyProvider<Vertex> key();
		   
		  @Path("value")
		  LabelProvider<Vertex> nameLabel();
		 
		  ValueProvider<Vertex, String> value();
	}
	
	private static final VertexProperties vertexProperties = GWT.create(VertexProperties.class);
	
	public static class CreateSynonymsView implements IsWidget {

		private ListStore<Vertex> unselectedListStore = new ListStore<Vertex>(vertexProperties.key());
		private ListStore<Vertex> selectedListStore = new ListStore<Vertex>(vertexProperties.key());
		private VerticalLayoutContainer vlc;
		private List<Edge> originalSynonymRelations;
		private Row row;
		private Type type;
		private Vertex preselectedPreferred;
		
		public CreateSynonymsView(Row row, Vertex preselectedPreferred, Type type) {
			this.row = row;
			this.preselectedPreferred = preselectedPreferred;
			this.type = type;
			
			List<Vertex> candidates = new ArrayList<Vertex>(row.getAll());
			candidates.remove(preselectedPreferred);
			this.setCandidates(candidates);
			this.setPreferredTerm(preselectedPreferred);
			
			unselectedListStore.addSortInfo(new StoreSortInfo<Vertex>(vertexProperties.value(), SortDir.ASC));
			
			vlc = new VerticalLayoutContainer();
			HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
			hlc.add(new Label("Available terms"), new HorizontalLayoutData(0.5, 20));
			hlc.add(new Label(""), new HorizontalLayoutData(40, 20));
			hlc.add(new Label("Synonyms"), new HorizontalLayoutData(0.5, 20));
			vlc.add(hlc, new VerticalLayoutData(1, 20));
			final DualListField<Vertex, String> dualListField = new DualListField<Vertex, String>(
					unselectedListStore, selectedListStore,
					vertexProperties.value(), new TextCell());
			dualListField.getToView().setCell(new AbstractCell<String>() {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context,	String value, SafeHtmlBuilder sb) {
					if(selectedListStore.getAll().get(0).getValue().equals(value)) {
						sb.append(SafeHtmlUtils.fromTrustedString("<div style=\"color:#3d4486\"><b>" + value + " (preferred term) </b></div>"));
					} else {
						sb.append(SafeHtmlUtils.fromTrustedString(value));
					}
					/*if(context.getIndex() == 0) {
						sb.append(SafeHtmlUtils.fromTrustedString(value + " (preferred term)"));
					} else {
						sb.append(SafeHtmlUtils.fromTrustedString(value));
					}*/
				}
			});
			dualListField.getToStore().addStoreHandlers(new StoreHandlers<Vertex>() {
				@Override
				public void onAdd(StoreAddEvent<Vertex> event) {
					dualListField.getToView().refresh();
				}
				@Override
				public void onRemove(StoreRemoveEvent<Vertex> event) {
					dualListField.getToView().refresh();
				}
				@Override
				public void onFilter(StoreFilterEvent<Vertex> event) {
					dualListField.getToView().refresh();
				}
				@Override
				public void onClear(StoreClearEvent<Vertex> event) {
					dualListField.getToView().refresh();
				}
				@Override
				public void onUpdate(StoreUpdateEvent<Vertex> event) {
					dualListField.getToView().refresh();
				}
				@Override
				public void onDataChange(StoreDataChangeEvent<Vertex> event) {
					dualListField.getToView().refresh();
				}
				@Override
				public void onRecordChange(StoreRecordChangeEvent<Vertex> event) {
					dualListField.getToView().refresh();
				}
				@Override
				public void onSort(StoreSortEvent<Vertex> event) {
					dualListField.getToView().refresh();
				}
			});
			dualListField.setMode(Mode.INSERT);
			/*dualListField.addValidator(new Validator<List<Vertex>>() {
				@Override
				public List<EditorError> validate(Editor<List<Vertex>> editor, List<Vertex> value) {
					
					return null;
				}
			});*/
			
			dualListField.setEnableDnd(true);
			vlc.add(dualListField, new VerticalLayoutData(1, 1));
			//add(dualListField);
		}
		
		private void setPreferredTerm(Vertex preferredTerm) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			selectedListStore.clear();
			selectedListStore.add(preferredTerm);
		}

		private void setCandidates(Collection<Vertex> candidates) {
			unselectedListStore.clear();
			unselectedListStore.addAll(candidates);
		}

		@Override
		public Widget asWidget() {
			return vlc;
		}
		
		public Vertex getPreferredTerm() {
			return selectedListStore.get(0);
		}
		
		public List<Vertex> getSynonyms() {
			List<Vertex> result = new LinkedList<Vertex>();
			for(int i=1; i<selectedListStore.size(); i++) 
				result.add(selectedListStore.get(i));
			return result;
		}
		
		public CompositeModifyEvent getModifyEvent(Set<Edge> reattach) {
			List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
			CompositeModifyEventForSynonymCreator compositeModifyEventForSynonymCreator = new CompositeModifyEventForSynonymCreator();
			OntologyGraph g = ModelController.getCollection().getGraph();
			Vertex preferredTerm = this.getPreferredTerm();
			for(Vertex synonym : this.getSynonyms()) {		
				result.add(compositeModifyEventForSynonymCreator.create(preferredTerm, synonym, reattach));
			}
			return new CompositeModifyEvent(result);
		}
	}
	
	public CreateSynonymsDialog(final TermsGrid termsGrid, Row row, Vertex preselectedPreferred, Type type) {
		super();
		final CreateSynonymsView createSynonymsView = new CreateSynonymsView(row, preselectedPreferred, type);
		this.setTitle("Make synonyms");
		this.setHeading("Make Synonyms");
		this.setWidget(createSynonymsView);
		this.setSize("600", "400");
		this.setMaximizable(true);
		this.setHideOnButtonClick(true);
		
		/*this.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for(Vertex synonym : createSynonymsView.getSynonyms()) {
					MessageBox box = Alerter.showYesNoCancelConfirm("Keep relations involving " + synonym, 
							"Do you want to re-attach all subclass and part relations involving " + synonym + " to " + createSynonymsView.getPreferredTerm() + "?");
					box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							CompositeModifyEvent compositeModifyEvent = createSynonymsView.getModifyEvent(true);
							termsGrid.fire(compositeModifyEvent);
							CreateSynonymsDialog.this.hide();
							termsGrid.fire(new ShowRelationsEvent(Type.SYNONYM_OF));
						}
					});
					box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							CompositeModifyEvent compositeModifyEvent = createSynonymsView.getModifyEvent(false);
							termsGrid.fire(compositeModifyEvent);
							CreateSynonymsDialog.this.hide();
							termsGrid.fire(new ShowRelationsEvent(Type.SYNONYM_OF));
						}
					});
					box.getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							CreateSynonymsDialog.this.hide();
						}
					});
				}
			}
		});*/
	}

}
