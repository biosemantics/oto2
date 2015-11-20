package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.content.candidates.TermTreeNodeIconProvider;
import edu.arizona.biosemantics.oto2.ontologize.client.content.ontologyview.OntologyView;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOfTreeNode;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOfTreeNodeProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class PartsOfsView implements IsWidget {

	private static final PartOfTreeNodeProperties partOfTreeNodeProperties = GWT.create(PartOfTreeNodeProperties.class);
	
	private EventBus eventBus;
	
	private VerticalLayoutContainer verticalLayoutContainer;
	private OntologyView ontologyView;
	private TextField partOfTextField = new TextField();
	private TreeStore<PartOfTreeNode> store;
	private Tree<PartOfTreeNode, String> tree;
	private OntologyClassSubmissionRetriever ontologyClassSubmissionRetriever = new OntologyClassSubmissionRetriever();
	private Collection collection;
	
	public PartsOfsView(EventBus eventBus) {
		this.eventBus = eventBus;
		store = new TreeStore<PartOfTreeNode>(partOfTreeNodeProperties.key());
		store.setAutoCommit(true);
		store.addSortInfo(new StoreSortInfo<PartOfTreeNode>(new Comparator<PartOfTreeNode>() {
			@Override
			public int compare(PartOfTreeNode o1, PartOfTreeNode o2) {
				return o1.getText().compareTo(o2.getText());
			}
		}, SortDir.ASC));
		tree = new Tree<PartOfTreeNode, String>(store, partOfTreeNodeProperties.text()); //new IdentityValueProvider<PartOfTreeNode>());
		//tree.setIconProvider(new PartOfNodeIconProvider(collection));
		/*termTree.setCell(new AbstractCell<TextTreeNode>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	TextTreeNode textTreeNode, SafeHtmlBuilder sb) {
				if(textTreeNode instanceof PartOfTreeNode) {
					PartOfTreeNode partOfTreeNode = (PartOfTreeNode)textTreeNode;
					PartOf partOf = partOfTreeNode.getPartOf();
					String text = partOf.getLabel();
					
					String iris = "";
					for(String iri : collection.getExistingIRIs(term)) {
						iris += iri + ", ";
					}
					if(!iris.isEmpty())
						iris = iris.substring(0, iris.length() - 2);
					if(!iris.isEmpty())
						text += " (" + iris + ")";
					if(collection.hasColorization(term)) {
						String colorHex = collection.getColorization(term).getHex();
						sb.append(SafeHtmlUtils.fromTrustedString("<div style='padding-left:5px; padding-right:5px; background-color:#" + colorHex + 
								"'>" + 
								text + "</div>"));
					} else {
						sb.append(SafeHtmlUtils.fromTrustedString("<div style='padding-left:5px; padding-right:5px'>" + text +
								"</div>"));
					}
				} else {
					sb.append(SafeHtmlUtils.fromTrustedString("<div style='padding-left:5px; padding-right:5px'>" + textTreeNode.getText() +
							"</div>"));
				}
			}
		});*/
		tree.getElement().setAttribute("source", "partsview");
		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		this.ontologyView = new OntologyView(eventBus);
		this.ontologyView.setPartOfChecked(true);
		this.ontologyView.setSynonymChecked(false);
		this.ontologyView.setSuperclassChecked(false);
		this.verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.getScrollSupport().setScrollMode(ScrollMode.AUTO);
		
		verticalLayoutContainer.add(new FieldLabel(ontologyView, "Ontology View"), new VerticalLayoutData(1, 400));
		verticalLayoutContainer.add(new FieldLabel(tree, "Candidate Bearers"), new VerticalLayoutData(1, 100));
		verticalLayoutContainer.add(new FieldLabel(partOfTextField, "New Bearer"), new VerticalLayoutData(1, 1));
		
		bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				PartsOfsView.this.collection = event.getCollection();
			}
		});
		eventBus.addHandler(RefreshOntologyClassSubmissionsEvent.TYPE, new RefreshOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onSelect(RefreshOntologyClassSubmissionsEvent event) {
				loadParts(event.getOntologyClassSubmissions());
			}
		});
		tree.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<PartOfTreeNode>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<PartOfTreeNode> event) {
				partOfTextField.setValue(event.getSelection().get(0).getPartOf().getLabel());
			}
		});
	}

	protected void loadParts(List<OntologyClassSubmission> result) {
		store.clear();
		Map<String, PartOfTreeNode> nodes = new HashMap<String, PartOfTreeNode>();
		for(OntologyClassSubmission submission : result) {
			PartOfTreeNode partOfTreeNode = new PartOfTreeNode(new PartOf(submission));
			nodes.put(partOfTreeNode.getId(), partOfTreeNode);
			
			for(PartOf partOf : submission.getPartOfs()) {
				PartOfTreeNode child = new PartOfTreeNode(partOf);
				nodes.put(child.getId(), child);
			}
		}
		for(OntologyClassSubmission submission : result) {
			popuplateStore(submission, result, nodes);
		}
	}

	private void popuplateStore(OntologyClassSubmission submission, List<OntologyClassSubmission> submissions, Map<String, PartOfTreeNode> nodes) {
		PartOfTreeNode partOfTreeNode = new PartOfTreeNode(new PartOf(submission));
		if(store.findModel(nodes.get(partOfTreeNode.getId())) != null) {
			return;
		}
		if(submission.getPartOfs().isEmpty()) {
			store.add(nodes.get(partOfTreeNode.getId()));
		} else {
			for(PartOf partOf : submission.getPartOfs()) {
				PartOfTreeNode parent = new PartOfTreeNode(partOf);
				OntologyClassSubmission parentSubmission = ontologyClassSubmissionRetriever.getSubmissionOfLabelOrIri(
						parent.getPartOf(), submissions);
				popuplateStore(parentSubmission, submissions, nodes);
				store.add(nodes.get(parent.getId()), nodes.get(partOfTreeNode.getId()));
			}			
		}
	}

	public String getValue() {
		return partOfTextField.getValue();
	}

	@Override
	public Widget asWidget() {
		return verticalLayoutContainer;
	}

	public void setSubmissionType(Type type) {
		switch(type) {
		case ENTITY:
			ontologyView.setEntityChecked(true);
			ontologyView.setQualityChecked(false);
			break;
		case QUALITY:
			ontologyView.setEntityChecked(false);
			ontologyView.setQualityChecked(true);
			break;
		default:
			ontologyView.setEntityChecked(true);
			ontologyView.setQualityChecked(true);
			break;
		}
	}

}
