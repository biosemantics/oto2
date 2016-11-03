package edu.arizona.biosemantics.oto2.ontologize2.client.candidate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
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

		public CreateRelationsFromCandidateView(Candidate candidate, List<CandidatePatternResult> patterns) {
			this.patterns = patterns;
			store = new TreeStore<TextTreeNode>(new ModelKeyProvider<TextTreeNode>() {
				@Override
				public String getKey(TextTreeNode item) {
					return item.getId();
				}
			});
			tree = new Tree<TextTreeNode, TextTreeNode>(store, new IdentityValueProvider());
			tree.setCheckable(true);
			tree.setCheckStyle(CheckCascade.TRI);
			tree.setCell(new AbstractCell<TextTreeNode>() {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context,
						TextTreeNode value, SafeHtmlBuilder sb) {
					OntologyGraph g = ModelController.getCollection().getGraph();
					String textColor = "black";
					if(value instanceof EdgeTreeNode) {
						if(g.existsRelation(((EdgeTreeNode) value).getEdge()))
							textColor = "green";
						else
							textColor = "red";
					} else if(value instanceof PatternTreeNode) {
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
							textColor = "green";
						if(noneExists)
							textColor = "red";
						if(!allExist && !noneExists)
							textColor = "blue";
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
					store.add(pNode, new EdgeTreeNode(e));
				}
			}
			tree.setAutoExpand(true);
			vlc = new VerticalLayoutContainer();
			vlc.add(new Label("Select the relations you want to re-attach from the synonym to the preferred term."), new VerticalLayoutData(1, -1));
			vlc.add(tree, new VerticalLayoutData(1, 1));
			//vlc.getScrollSupport().setScrollMode(ScrollMode.AUTOY);
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
				if(node instanceof EdgeTreeNode)
					result.add((EdgeTreeNode)node);
			}
			return result;
		}
	}

	private CreateRelationsFromCandidateView view;

	public CreateRelationsFromCandidateDialog(Candidate candidate, List<CandidatePatternResult> patterns) {
		super();
		view = new CreateRelationsFromCandidateView(candidate, patterns);
		this.setTitle("Select Relations");
		this.setHeadingText("Select Relations");
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