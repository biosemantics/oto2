package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
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
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.DualListField;
import com.sencha.gxt.widget.core.client.form.DualListField.Mode;
import com.sencha.gxt.widget.core.client.form.Validator;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.RemoveMode;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.TextTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class CreateSynonymsDialog extends Dialog {

	/*public static class SynonymCluster {
		
		private Vertex preferredTerm;
		private List<Edge> synonymEdges;

		public SynonymCluster(Vertex preferredTerm, List<Edge> synonymEdges) {
			this.preferredTerm = preferredTerm;
			this.synonymEdges = synonymEdges;
		}

		public Vertex getPreferredTerm() {
			return preferredTerm;
		}

		public void setPreferredTerm(Vertex preferredTerm) {
			this.preferredTerm = preferredTerm;
		}

		public List<Edge> getSynonymEdges() {
			return synonymEdges;
		}

		public void setSynonymEdges(List<Edge> synonymEdges) {
			this.synonymEdges = synonymEdges;
		}		
	}*/
	
	public interface VertexProperties extends PropertyAccess<Vertex> {
		  @Path("value")
		  ModelKeyProvider<Vertex> key();
		   
		  @Path("value")
		  LabelProvider<Vertex> nameLabel();
		 
		  ValueProvider<Vertex, String> value();
	}
	
	private static final VertexProperties vertexProperties = GWT.create(VertexProperties.class);
	
	/*static ModelKeyProvider<SynonymCluster> keyProvider = new ModelKeyProvider<SynonymCluster>() {
		@Override
		public String getKey(SynonymCluster item) {
			return item.getPreferredTerm().getValue();
		}
	};
	static ValueProvider<SynonymCluster, String> textProvider = new ValueProvider<SynonymCluster, String>() {
		@Override
		public String getValue(SynonymCluster object) {
			return object.getPreferredTerm().getValue();
		}
		@Override
		public void setValue(SynonymCluster object, String value) { }

		@Override
		public String getPath() {
			return "value";
		}
	};*/
	
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
			candidates.remove(row.getLead());
			this.setCandidates(candidates);
			this.setPreferredTerm(row.getLead());
			
			unselectedListStore.addSortInfo(new StoreSortInfo<Vertex>(vertexProperties.value(), SortDir.ASC));
			
			vlc = new VerticalLayoutContainer();
			HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
			hlc.add(new Label("Available users"), new HorizontalLayoutData(0.5, 20));
			hlc.add(new Label(""), new HorizontalLayoutData(40, 20));
			hlc.add(new Label("Shared with"), new HorizontalLayoutData(0.5, 20));
			vlc.add(hlc, new VerticalLayoutData(1, 20));
			final DualListField<Vertex, String> dualListField = new DualListField<Vertex, String>(
					unselectedListStore, selectedListStore,
					vertexProperties.value(), new TextCell());
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
		
		public CompositeModifyEvent getModifyEvent(boolean reattach) {
			List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
			OntologyGraph g = ModelController.getCollection().getGraph();
			Vertex preferredTerm = this.getPreferredTerm();
			
			
			List<Edge> in = g.getInRelations(preferredTerm, Type.SYNONYM_OF);
			if(in.isEmpty()) 
				result.add(new CreateRelationEvent(new Edge(g.getRoot(Type.SYNONYM_OF), preferredTerm, Type.SYNONYM_OF, Origin.USER)));
			
			for(Vertex synonym : this.getSynonyms()) {		
				boolean preferredTermAndSynonymHaveParents = isPreferredTermAndSynonymHaveParents(preferredTerm, synonym);
				if(!preferredTermAndSynonymHaveParents) {
					result.addAll(createEventsSynonymReduction(preferredTerm, synonym, reattach));
				} else {
					result.addAll(createEventsNonSpecificSynonyms(preferredTerm, synonym, reattach));
				}
				result.add(new CreateRelationEvent(new Edge(preferredTerm, synonym, Type.SYNONYM_OF, Origin.USER)));		
			}
			return new CompositeModifyEvent(result);
		}

		private Collection<? extends GwtEvent<?>> createEventsNonSpecificSynonyms(Vertex preferredTerm, Vertex synonym, boolean reattach) {
			List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
			OntologyGraph g = ModelController.getCollection().getGraph();
			
			//result.addAll(createDisambiguatedTerm(preferredTerm, preferredTerm));
			//result.addAll(createDisambiguatedTerm(preferredTerm, synonym));
			
			Vertex disambiguatedPreferred = new Vertex(this.getNonRootParent(preferredTerm).getValue() + " " + preferredTerm.getValue());
			Edge disambiguatePreferredEdge = new Edge(preferredTerm, 
					disambiguatedPreferred, 
					Type.SUBCLASS_OF, 
					Origin.USER);
			List<Edge> inSubclass = g.getInRelations(disambiguatedPreferred, Type.SUBCLASS_OF);
			List<Edge> inPartOf = g.getInRelations(disambiguatedPreferred, Type.PART_OF);
			Edge disambiguatedPreferredPartOfRootEdge = new Edge(g.getRoot(Type.PART_OF), disambiguatedPreferred, Type.PART_OF, Origin.USER);
			if(inPartOf.isEmpty())
				result.add(new CreateRelationEvent(disambiguatedPreferredPartOfRootEdge));
			if(inSubclass.size() == 1 && inSubclass.get(0).getSrc().equals(g.getRoot(Type.SUBCLASS_OF))) {
				result.add(new ReplaceRelationEvent(inSubclass.get(0), preferredTerm));
			} if(inSubclass.size() == 0) {
				result.add(new CreateRelationEvent(disambiguatePreferredEdge));
			} else {
				if(!g.existsRelation(disambiguatePreferredEdge)) {
					result.add(new CreateRelationEvent(disambiguatePreferredEdge));
				}
			}
			
			Vertex disambiguatedSynonym = new Vertex(this.getNonRootParent(synonym).getValue() + " " + preferredTerm.getValue());
			Edge disambiguateSynonymEdge = new Edge(preferredTerm, 
					disambiguatedSynonym, 
					Type.SUBCLASS_OF, 
					Origin.USER);
			inSubclass = g.getInRelations(disambiguatedSynonym, Type.SUBCLASS_OF);
			inPartOf = g.getInRelations(disambiguatedSynonym, Type.PART_OF);
			Edge disambiguatedSynonymPartOfRootEdge = new Edge(g.getRoot(Type.PART_OF), disambiguatedSynonym, Type.PART_OF, Origin.USER);
			if(inPartOf.isEmpty())
				result.add(new CreateRelationEvent(disambiguatedSynonymPartOfRootEdge));
			if(inSubclass.size() == 1 && inSubclass.get(0).getSrc().equals(g.getRoot(Type.SUBCLASS_OF))) {
				result.add(new ReplaceRelationEvent(inSubclass.get(0), preferredTerm));
			} if(inSubclass.size() == 0) {
				result.add(new CreateRelationEvent(disambiguateSynonymEdge));
			} else {
				if(!g.existsRelation(disambiguateSynonymEdge)) {
					result.add(new CreateRelationEvent(disambiguateSynonymEdge));
				}
			}
			
			
			//synonm
			//synonym subclass
			for(Edge e : g.getOutRelations(synonym, Type.SUBCLASS_OF)) {
				if(reattach) {
					Edge newEdge = new Edge(disambiguatedSynonym, e.getDest(), Type.SUBCLASS_OF, Origin.USER);
					if(!g.existsRelation(newEdge) && !e.getDest().equals(disambiguatedSynonym))
						result.add(new ReplaceRelationEvent(e, disambiguatedSynonym));
				} else {
					result.add(new RemoveRelationEvent(RemoveMode.NONE, e));
				}
			}
			Set<Vertex> exceptionsIn = new HashSet<Vertex>();
			exceptionsIn.add(preferredTerm);
			for(Edge e : g.getInRelations(synonym, Type.SUBCLASS_OF)) {
				if(reattach) {
					Edge newEdge = new Edge(e.getSrc(), disambiguatedSynonym, Type.SUBCLASS_OF, Origin.USER);
					if(!g.existsRelation(newEdge) && !exceptionsIn.contains(e.getSrc())) {
						List<Edge> in = g.getInRelations(disambiguatedSynonym);
						if(in.size() == 1 && in.get(0).getSrc().equals(g.getRoot(Type.SUBCLASS_OF))) {
							result.add(new ReplaceRelationEvent(in.get(0), e.getSrc()));
						} else {
							result.add(new CreateRelationEvent(new Edge(e.getSrc(), disambiguatedSynonym, Type.SUBCLASS_OF, Origin.USER)));
						}
					}
				}
				result.add(new RemoveRelationEvent(RemoveMode.NONE, e));
			}
			
			
			//synonym parts
			for(Edge e : g.getOutRelations(synonym, Type.PART_OF)) {
				if(reattach) {
					Edge newEdge = new Edge(disambiguatedSynonym, e.getDest(), Type.PART_OF, Origin.USER);
					if(!g.existsRelation(newEdge) && !e.getDest().equals(disambiguatedSynonym))
						result.add(new ReplaceRelationEvent(e, disambiguatedSynonym));
				} else {
					result.add(new RemoveRelationEvent(RemoveMode.NONE, e));
				}
			}
			exceptionsIn = new HashSet<Vertex>();
			//exceptionsIn.add(this.getNonRootParent(synonym));
			exceptionsIn.add(preferredTerm);
			for(Edge e : g.getInRelations(synonym, Type.PART_OF)) {
				if(reattach) {
					//Edge newEdge = new Edge(e.getSrc(), disambiguatedSynonym, Type.PART_OF, Origin.USER);
					//if(!g.existsRelation(newEdge)) {// && !exceptionsIn.contains(e.getSrc())) {
						//List<Edge> in = g.getInRelations(disambiguatedSynonym);
						//if(in.size() == 1 && in.get(0).getSrc().equals(g.getRoot(Type.PART_OF))) {
						result.add(new ReplaceRelationEvent(disambiguatedSynonymPartOfRootEdge, e.getSrc()));
						//} else {
						//	result.add(new CreateRelationEvent(new Edge(e.getSrc(), disambiguatedSynonym, Type.PART_OF, Origin.USER)));
						//}
					//}
				}
				result.add(new RemoveRelationEvent(RemoveMode.NONE, e));
			}
			
			
			//preferred term
			//preferred term subclass
			reattach = true;
			Set<Vertex> exceptionsOut = new HashSet<Vertex>();
			exceptionsOut.add(synonym);
			for(Edge e : g.getOutRelations(preferredTerm, Type.SUBCLASS_OF)) {
				if(!e.equals(disambiguatePreferredEdge)) {
					if(reattach && ! exceptionsOut.contains(e.getDest())) {
						Edge newEdge = new Edge(disambiguatedPreferred, e.getDest(), Type.SUBCLASS_OF, Origin.USER);
						if(!g.existsRelation(newEdge) && !e.getDest().equals(disambiguatedPreferred))
							result.add(new ReplaceRelationEvent(e, disambiguatedPreferred));
					} else {
						result.add(new RemoveRelationEvent(RemoveMode.NONE, e));
					}
				}
			}
			for(Edge e : g.getInRelations(preferredTerm, Type.SUBCLASS_OF)) {
				if(reattach) {
					Edge newEdge = new Edge(e.getSrc(), disambiguatedPreferred, Type.SUBCLASS_OF, Origin.USER);
					if(!g.existsRelation(newEdge)) {
						List<Edge> in = g.getInRelations(disambiguatedPreferred);
						if(in.size() == 1 && in.get(0).getSrc().equals(g.getRoot(Type.SUBCLASS_OF))) {
							result.add(new ReplaceRelationEvent(in.get(0), e.getSrc()));
						} else {
							result.add(new CreateRelationEvent(new Edge(e.getSrc(), disambiguatedPreferred, Type.SUBCLASS_OF, Origin.USER)));
						}
					}
				}
				result.add(new ReplaceRelationEvent(e, g.getRoot(Type.SUBCLASS_OF)));
			}
			
			//preferred term parts
			exceptionsOut = new HashSet<Vertex>();
			exceptionsOut.add(synonym);
			for(Edge e : g.getOutRelations(preferredTerm, Type.PART_OF)) {
				if(reattach && !exceptionsOut.contains(e.getDest())) {
					Edge newEdge = new Edge(disambiguatedPreferred, e.getDest(), Type.PART_OF, Origin.USER);
					if(!g.existsRelation(newEdge) && !e.getDest().equals(disambiguatedPreferred))
						result.add(new ReplaceRelationEvent(e, disambiguatedPreferred));
				} else {
					result.add(new RemoveRelationEvent(RemoveMode.NONE, e));
				}
			}
			//exceptionsIn = new HashSet<Vertex>();
			//exceptionsIn.add(this.getNonRootParent(synonym));
			for(Edge e : g.getInRelations(preferredTerm, Type.PART_OF)) {
				if(reattach) {
					//Edge newEdge = new Edge(e.getSrc(), disambiguatedPreferred, Type.PART_OF, Origin.USER);
					//if(!g.existsRelation(newEdge)) {// && !exceptionsIn.contains(e.getSrc())) {
					//	List<Edge> in = g.getInRelations(disambiguatedPreferred);
					//	if(in.size() == 1 && in.get(0).getSrc().equals(g.getRoot(Type.PART_OF))) {
							result.add(new ReplaceRelationEvent(disambiguatedPreferredPartOfRootEdge, e.getSrc()));
					//	} else {
					//		result.add(new CreateRelationEvent(new Edge(e.getSrc(), disambiguatedPreferred, Type.PART_OF, Origin.USER)));
					//	}
					//}
				}
				result.add(new ReplaceRelationEvent(e, g.getRoot(Type.PART_OF))); //RemoveMode.NONE, e));
			}
			
			Edge synonymSubclassOfRootEdge = new Edge(g.getRoot(Type.SUBCLASS_OF), synonym, Type.SUBCLASS_OF, Origin.USER);
			Edge synonymPartOfRootEdge = new Edge(g.getRoot(Type.PART_OF), synonym, Type.PART_OF, Origin.USER);
			result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, synonymSubclassOfRootEdge));
			result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, synonymPartOfRootEdge));
			
			
			/*Set<Edge> exceptionsIn = new HashSet<Edge>();
			Set<Vertex> excpetionVerticesIn = new HashSet<Vertex>(Arrays.asList(new Vertex[] { preferredTerm, this.getNonRootParent(synonym) }));
			Set<Edge>exceptionsOut = new HashSet<Edge>(); 
			Set<Vertex> excpetionVerticesOut = new HashSet<Vertex>();
			result.addAll(reattachRelations(reattach, synonym, disambiguatedSynonym, exceptionsIn, excpetionVerticesIn, exceptionsOut, excpetionVerticesOut));
			
			exceptionsIn = new HashSet<Edge>();
			excpetionVerticesIn = new HashSet<Vertex>();
			exceptionsOut = new HashSet<Edge>(Arrays.asList(new Edge[] { disambiguatePreferredEdge })); 
			excpetionVerticesOut = new HashSet<Vertex>(Arrays.asList(new Vertex[] { synonym }));
			result.addAll(reattachRelations(reattach, preferredTerm, disambiguatedPreferred, exceptionsIn, excpetionVerticesIn, exceptionsOut, excpetionVerticesOut));
			*/
			
			return result;
		}

		/*private Collection<? extends GwtEvent<?>> createDisambiguatedTerm(Vertex useToDisambiguate, Vertex toDisambiguate) {
			List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
			OntologyGraph g = ModelController.getCollection().getGraph();
			Vertex disambiguated = new Vertex(this.getNonRootParent(useToDisambiguate).getValue() + " " + toDisambiguate.getValue());
			Edge disambiguatedEdge = new Edge(useToDisambiguate, 
					disambiguated, 
					Type.SUBCLASS_OF, 
					Origin.USER);
			List<Edge> in = g.getInRelations(disambiguated);
			if(in.size() == 1 && in.get(0).getSrc().equals(g.getRoot(Type.SUBCLASS_OF))) {
				result.add(new ReplaceRelationEvent(in.get(0), useToDisambiguate));
			} else {
				if(!g.existsRelation(disambiguatedEdge))
					result.add(new CreateRelationEvent(disambiguatedEdge));
			}
			return result;
		}*/

		/*private List<GwtEvent<?>> reattachRelations(boolean reattach, Vertex oldTarget, Vertex newTarget, Set<Edge> exceptionEdgesIn, Set<Vertex> exceptionVerticesIn, Set<Edge> exceptionEdgesOut, 
				Set<Vertex> exceptionVerticesOut) {
			List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
			OntologyGraph g = ModelController.getCollection().getGraph();
			for(Type type : new Type[] {Type.SUBCLASS_OF, Type.PART_OF}) {
				for(Edge e : g.getOutRelations(oldTarget, type)) {
					if(!exceptionEdgesOut.contains(e)) {
						if(reattach) {
							Edge newEdge = new Edge(newTarget, e.getDest(), type, Origin.USER);
							if(!exceptionVerticesOut.contains(e.getDest())) {
								if(!g.existsRelation(newEdge) && !e.getDest().equals(newTarget))
									result.add(new ReplaceRelationEvent(e, newTarget));
							} else {
								result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, e));
							}
						} else {
							result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, e));
						}
					}
				}
				for(Edge e : g.getInRelations(oldTarget, type)) {
					if(!exceptionEdgesIn.contains(e)) {
						if(reattach) {
							Edge newEdge = new Edge(e.getSrc(), newTarget, type, Origin.USER);
							if(!g.existsRelation(newEdge) && !exceptionVerticesIn.contains(e.getSrc())) {
								List<Edge> in = g.getInRelations(newTarget);
								if(in.size() == 1 && in.get(0).getSrc().equals(g.getRoot(type))) {
									result.add(new ReplaceRelationEvent(in.get(0), e.getSrc()));
								} else {
									if(!e.getDest().equals(newTarget))
										result.add(new CreateRelationEvent(new Edge(e.getSrc(), newTarget, type, Origin.USER)));
								}
							}
						}
						result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, e));
					}
				}
			}
			return result;
		}*/

		private Collection<? extends GwtEvent<?>> createEventsSynonymReduction(Vertex preferredTerm, Vertex synonym, boolean reattach) {
			//return reattachRelations(reattach, synonym, preferredTerm, new HashSet<Edge>(), new HashSet<Vertex>(Arrays.asList(new Vertex[] { preferredTerm })), 
			//		new HashSet<Edge>(), new HashSet<Vertex>());
			List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
			OntologyGraph g = ModelController.getCollection().getGraph();
			for(Type type : new Type[] {Type.SUBCLASS_OF, Type.PART_OF}) {
				for(Edge e : g.getOutRelations(synonym, type)) {
					if(reattach) {
						Edge newEdge = new Edge(preferredTerm, e.getDest(), type, Origin.USER);
						if(!g.existsRelation(newEdge) && !e.getDest().equals(preferredTerm))
							result.add(new ReplaceRelationEvent(e, preferredTerm));
					} else {
						result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, e));
					}
				}
				for(Edge e : g.getInRelations(synonym, type)) {
					if(reattach) {
						Edge newEdge = new Edge(e.getSrc(), preferredTerm, type, Origin.USER);
						if(!g.existsRelation(newEdge) && !e.getSrc().equals(preferredTerm)) {
							List<Edge> preferredIn = g.getInRelations(preferredTerm);
							if(preferredIn.size() == 1 && preferredIn.get(0).getSrc().equals(g.getRoot(type))) {
								result.add(new ReplaceRelationEvent(preferredIn.get(0), e.getSrc()));
							} else {
								result.add(new CreateRelationEvent(new Edge(e.getSrc(), preferredTerm, type, Origin.USER)));
							}
						}
					}
					result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, e));
				}
			}
			
			Edge synonymSubclassOfRootEdge = new Edge(g.getRoot(Type.SUBCLASS_OF), synonym, Type.SUBCLASS_OF, Origin.USER);
			Edge synonymPartOfRootEdge = new Edge(g.getRoot(Type.PART_OF), synonym, Type.PART_OF, Origin.USER);
			result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, synonymSubclassOfRootEdge));
			result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, synonymPartOfRootEdge));
			return result;
		}
		
		private Vertex getNonRootParent(Vertex v) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			List<Edge> in = g.getInRelations(v, Type.PART_OF);
			if(in.size() == 1 && !in.get(0).getSrc().equals(g.getRoot(Type.PART_OF)))
				return in.get(0).getSrc();
			return null;
		}

		private boolean isPreferredTermAndSynonymHaveParents(Vertex preferredTerm, Vertex synonym) {
			return getNonRootParent(preferredTerm) != null && getNonRootParent(synonym) != null;
		}

		public List<Vertex> getSynonyms() {
			List<Vertex> result = new LinkedList<Vertex>();
			for(int i=1; i<selectedListStore.size(); i++) 
				result.add(selectedListStore.get(i));
			return result;
		}
	}
	
	public CreateSynonymsDialog(final TermsGrid termsGrid, Row row, Vertex preselectedPreferred, Type type) {
		super();
		final CreateSynonymsView createSynonymsView = new CreateSynonymsView(row, preselectedPreferred, type);
		this.setWidget(createSynonymsView);
		this.setSize("600", "400");
		this.setMaximizable(true);
		this.setHideOnButtonClick(true);
		
		this.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
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
						}
					});
					box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							CompositeModifyEvent compositeModifyEvent = createSynonymsView.getModifyEvent(false);
							termsGrid.fire(compositeModifyEvent);
							CreateSynonymsDialog.this.hide();
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
		});
	}

}
