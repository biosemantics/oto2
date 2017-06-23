package edu.arizona.biosemantics.oto2.ontologize2.client.candidate;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.candidate.CandidateView.CellTemplate;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.RelationSelectionDialog.RelationSelectionView;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.cell.CellImages;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.CandidateTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.EdgeTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.PatternTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.TextTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.CandidatePatternResult;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class CreateRelationsFromCandidateDialog extends Dialog {
	
	public static class CreateRelationsFromCandidateView implements IsWidget {

		public interface CellTemplate extends SafeHtmlTemplates {
			@SafeHtmlTemplates.Template(""
					+ "<div style=\"color:{1}\">"
					+ "{0}"
					+ "</div>"
					)
			SafeHtml cell(String value,	String textColor);
		}
		
		protected static CellTemplate cellTemplate = GWT.create(CellTemplate.class);
		
		private TreeStore<TextTreeNode> store;
		private Tree<TextTreeNode, TextTreeNode> tree;
		private VerticalLayoutContainer vlc;
		private List<CandidatePatternResult> patterns;
		private Set<String> existedRelations;

		public CreateRelationsFromCandidateView(Candidate candidate, List<CandidatePatternResult> patterns) {
			this.patterns = patterns;
			store = new TreeStore<TextTreeNode>(new ModelKeyProvider<TextTreeNode>() {
				@Override
				public String getKey(TextTreeNode item) {
					return item.getId();
				}
			});
			existedRelations = new HashSet();
			tree = new Tree<TextTreeNode, TextTreeNode>(store, new IdentityValueProvider());
			tree.setCheckable(true);
			tree.setCheckStyle(CheckCascade.TRI);
			tree.setCell(new AbstractCell<TextTreeNode>() {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context,
						TextTreeNode value, SafeHtmlBuilder sb) {
					OntologyGraph g = ModelController.getCollection().getGraph();
					String textColor = "black";
					
					if(value instanceof PatternTreeNode) {//pattern tree node
						List<TextTreeNode> children = store.getChildren(value);
						boolean allExist = true;
						boolean noneExists = true;
						for(TextTreeNode child : children) {
							if(child instanceof EdgeTreeNode) {
								if(g.existsRelation(((EdgeTreeNode) child).getEdge()))
									noneExists = false;
								else
									allExist = false;
							}
						}
						if(allExist)
							textColor = "gray";
						if(noneExists)
							textColor = "green";
						if(!allExist && !noneExists)
							textColor = "blue";
					}else if(value instanceof EdgeTreeNode) {//leave nodes
						if(g.existsRelation(((EdgeTreeNode) value).getEdge())){
							existedRelations.add(value.getId());
							textColor = "gray";
						}			
						else
							textColor = "green";
					}
					sb.append(cellTemplate.cell(value.getText(), textColor));
				}
				
			});
			
			CandidateTreeNode cNode = new CandidateTreeNode(candidate);
			store.add(cNode);
			for(CandidatePatternResult p : patterns) {
				PatternTreeNode pNode = new PatternTreeNode(p);
				store.add(cNode, pNode);
				for(Edge e : p.getRelations()) {
					//do not recommend default relations
					//https://github.com/biosemantics/etc-site/issues/627
					if(!(e.getType().equals(Type.SUBCLASS_OF)&&
							(e.getDest().getValue().equals("material anatomical entity")||
									e.getDest().getValue().equals("non-specific material anatomical entity")||
									e.getDest().getValue().equals("quality")||
									e.getDest().getValue().equals("imported")
									)))
						store.add(pNode, new EdgeTreeNode(e));
				}
			}
			tree.setAutoExpand(true);
			//tree.getSelectionModel().setLocked(true);
			vlc = new VerticalLayoutContainer();
			vlc.add(new HTML("Gray text means the relation has already existed. Green text means the relation is new."), new VerticalLayoutData(1, -1));
			vlc.add(tree, new VerticalLayoutData(1, 1));
			//vlc.getScrollSupport().setScrollMode(ScrollMode.AUTOY);
			/*
			final TreeSelectionModel<TextTreeNode> selectionModel = tree.getSelectionModel();
	        selectionModel.addSelectionHandler(new SelectionHandler<TextTreeNode>() {
	            @Override
	            public void onSelection(SelectionEvent<TextTreeNode> event) {
	            	TextTreeNode treeNode = event.getSelectedItem();
	                if(existedRelations.contains(treeNode.getId())){
	                	//Alerter.showAlert("existed", treeNode.getId()+":"+treeNode.getText());
	                	//tree.setChecked(treeNode, CheckState.UNCHECKED);
	                	selectionModel.deselect(treeNode);
	                 }
	                
	            }
	        });*/
		}

		@Override
		public Widget asWidget() {
			return vlc;
		}

		public List<EdgeTreeNode> getSelection() {
			List<EdgeTreeNode> result = new LinkedList<EdgeTreeNode>();
			for(TextTreeNode node : tree.getCheckedSelection()) {
				//if(node instanceof CandidateTreeNode) {
				//	result.addAll()
				//} else if(node instanceof EdgeTreeNode) {
					
				//}
				if(node instanceof EdgeTreeNode && !existedRelations.contains(node.getId()))
					result.add((EdgeTreeNode)node);
			}
			return result;
		}
	}

	private CreateRelationsFromCandidateView view;

	public CreateRelationsFromCandidateDialog(Candidate candidate, List<CandidatePatternResult> patterns) {
		super();
		view = new CreateRelationsFromCandidateView(candidate, patterns);
		//this.setTitle("Select Relations");
		//this.setHeadingText("Select Relations");
		this.setTitle("Select the relations you want to create.");
		this.setHeading("Select the relations you want to create.");
		this.setHeight(600);
		this.setWidget(view);
		this.setMaximizable(true);
		this.setHideOnButtonClick(true);		
		this.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
	}

	public List<EdgeTreeNode> getSelection() {
		return view.getSelection();
	}

	public List<Edge> getSelectedEdges() {
		List<Edge> edges = new LinkedList<Edge>();
		for(EdgeTreeNode n : this.getSelection()) {
			edges.add(n.getEdge());
		}
		return edges;
	}
	
}