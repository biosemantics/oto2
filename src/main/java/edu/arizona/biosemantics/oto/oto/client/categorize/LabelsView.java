package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer;
import com.google.gwt.event.shared.EventBus;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelCreateEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.model.TextTreeNode;

public class LabelsView extends PortalLayoutContainer {
	
	public static class LabelTreeNode extends TextTreeNode {
		
		private Label label;

		public LabelTreeNode(Label label) {
			this.label = label;
		}

		@Override
		public String getText() {
			return label.getName();
		}
		
		public Label getLabel() {
			return label;
		}

		@Override
		public String getId() {
			return "label-" + label.getId();
		}
		
	}

	private EventBus eventBus;
	private int portalColumnCount;
	private Map<Label, LabelPortlet> labelPortletsMap = new HashMap<Label, LabelPortlet>();
	private Collection collection;
	
	public LabelsView(EventBus eventBus, int portalColumnCount) {
		super(portalColumnCount);
		this.eventBus = eventBus;
		this.portalColumnCount = portalColumnCount;
		double portalColumnWidth = 1.0 / portalColumnCount;
		for(int i=0; i<portalColumnCount; i++) {
			this.setColumnWidth(i, portalColumnWidth);
		}
		this.getElement().getStyle().setBackgroundColor("white");
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(Label destination, Set<Label> sources) {
				for(Label source : sources) {
					LabelPortlet sourcePortlet = labelPortletsMap.remove(source);
					sourcePortlet.removeFromParent();
					//LabelsView.this.remove(sourcePortlet, LabelsView.this.getPortletColumn(sourcePortlet));
					LabelPortlet destinationPortlet = labelPortletsMap.get(destination);
					for(Term term : source.getTerms())
						destinationPortlet.addMainTerm(term);
				}
			}
		});
		eventBus.addHandler(LabelCreateEvent.TYPE, new LabelCreateEvent.CreateLabelHandler() {
			@Override
			public void onCreate(Label label) {
				LabelPortlet labelPortlet = new LabelPortlet(eventBus, label, collection);
				add(labelPortlet, labelPortletsMap.size() % portalColumnCount);
				labelPortletsMap.put(label, labelPortlet);
				collection.addLabel(label);
			}
		});
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(Label label) {
				LabelPortlet portlet = labelPortletsMap.remove(label);
				LabelsView.this.remove(portlet, LabelsView.this.getPortletColumn(portlet));
				portlet.removeFromParent();
				collection.removeLabel(label);
			}
		});
		
	}

	public void setCollection(Collection collection) {
		clear();
		this.collection = collection;
		for(Label label : collection.getLabels()) {
			LabelPortlet labelPortlet = new LabelPortlet(eventBus, label, collection);
			add(labelPortlet, labelPortletsMap.size() % portalColumnCount);
			labelPortletsMap.put(label, labelPortlet);
		}
	}
}