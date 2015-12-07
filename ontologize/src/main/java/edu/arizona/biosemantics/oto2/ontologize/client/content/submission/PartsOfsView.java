package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.ontologize.client.content.ontologyview.OntologyView;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.OntologyClassSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;

public class PartsOfsView implements IsWidget {
	
	public static class Node {
		
		public String value;
		
		public Node(String value) {
			this.value = value;
		}
		
	}
	
	public static interface NodeProperties extends PropertyAccess<Node> {

		  @Path("value")
		  ModelKeyProvider<Node> key();
		  
		  @Path("value")
		  LabelProvider<Node> label();
		 
		  ValueProvider<Node, String> value();
		
	}
	
	private static final NodeProperties nodeProperties = GWT.create(NodeProperties.class);
	
	private EventBus eventBus;
	
	private VerticalLayoutContainer verticalLayoutContainer;
	private OntologyView ontologyView;
	private TextField partOfTextField = new TextField();
	private TreeStore<Node> store;
	private Tree<Node, String> tree;
	private Collection collection;
	
	public PartsOfsView(EventBus eventBus) {
		this.eventBus = eventBus;
		store = new TreeStore<Node>(nodeProperties.key());
		store.setAutoCommit(true);
		store.setEnableFilters(true);
		store.addSortInfo(new StoreSortInfo<Node>(new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return o1.value.compareTo(o2.value);
			}
		}, SortDir.ASC));
		tree = new Tree<Node, String>(store, nodeProperties.value()); //new IdentityValueProvider<PartOfTreeNode>());
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
		eventBus.addHandler(LoadOntologyClassSubmissionsEvent.TYPE, new LoadOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onSelect(LoadOntologyClassSubmissionsEvent event) {
				store.clear();
				for(OntologyClassSubmission submission : event.getOntologyClassSubmissions()) {
					addNode(submission);
				}
			}
		});
		eventBus.addHandler(CreateOntologyClassSubmissionEvent.TYPE, new CreateOntologyClassSubmissionEvent.Handler() {
			@Override
			public void onSubmission(CreateOntologyClassSubmissionEvent event) {
				for(OntologyClassSubmission submission : event.getClassSubmissions()) {
					addNode(submission);
				}
			}
		});
		eventBus.addHandler(RemoveOntologyClassSubmissionsEvent.TYPE, new RemoveOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onRemove(RemoveOntologyClassSubmissionsEvent event) {
				for(OntologyClassSubmission submission : event.getOntologyClassSubmissions()) {
					removeNode(submission);
				}
			}
		});
		eventBus.addHandler(UpdateOntologyClassSubmissionsEvent.TYPE, new UpdateOntologyClassSubmissionsEvent.Handler() {	
			@Override
			public void onUpdate(UpdateOntologyClassSubmissionsEvent event) {
				for(OntologyClassSubmission submission : event.getOntologyClassSubmissions()) {
					updateNode(submission);
				}
			}
		});
		
		tree.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<Node>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<Node> event) {
				if(!event.getSelection().isEmpty())
					partOfTextField.setValue(event.getSelection().get(0).value);//.getPartOf().getLabel());
			}
		});
		eventBus.addHandler(OntologyClassSubmissionSelectEvent.TYPE, new OntologyClassSubmissionSelectEvent.Handler() {
			@Override
			public void onSelect(final OntologyClassSubmissionSelectEvent event) {
				store.removeFilters();
				store.addFilter(new StoreFilter<Node>() {
					@Override
					public boolean select(Store<Node> store, Node parent, Node item) {
						Node node = new Node(event.getOntologyClassSubmission().getLabel());
						Node child = item;
						while((parent = tree.getStore().getParent(child)) != null) {
							if(parent.value.equals(node.value))
								return false;
							child = parent;
						}
						return !item.value.equals(node.value);
					}
				});
			}
		});
	}

	protected void updateNode(OntologyClassSubmission submission) {
		Node node = new Node(submission.getLabel());
		store.update(node);
	}

	protected void removeNode(OntologyClassSubmission submission) {
		Node node = new Node(submission.getLabel());
		store.remove(node);
	}

	protected void addNode(OntologyClassSubmission submission) {
		Node node = new Node(submission.getLabel());
		if(store.findModel(node) != null) {
			return;
		} else {
			if(submission.getPartOfs().isEmpty()) {
				store.add(node);
			} else {
				for(PartOf partOf : submission.getPartOfs()) {
					Node parent = new Node(partOf.getLabel());
					store.add(parent, node);
				}
			}
		}
	}

	public String getValue() {
		return partOfTextField.getValue() == null ? "" : partOfTextField.getValue().trim();
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
