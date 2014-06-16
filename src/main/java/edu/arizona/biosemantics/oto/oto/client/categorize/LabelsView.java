package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.List;

import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer;

import com.google.gwt.event.shared.EventBus;

import edu.arizona.biosemantics.oto.oto.shared.model.Label;
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
		
	}

	private EventBus eventBus;
	private int portalColumnCount;
	
	public LabelsView(EventBus eventBus, int portalColumnCount) {
		super(portalColumnCount);
		this.eventBus = eventBus;
		this.portalColumnCount = portalColumnCount;
		double portalColumnWidth = 1.0 / portalColumnCount;
		for(int i=0; i<portalColumnCount; i++) {
			this.setColumnWidth(i, portalColumnWidth);
		}
		this.getElement().getStyle().setBackgroundColor("white");
	}

	public void setLabels(List<Label> labels) {
		clear();
		int i = 0;
		for(Label label : labels) {
			LabelPortlet categoryPortlet = new LabelPortlet(eventBus, label);
			add(categoryPortlet, i);
			i = (i + 1) % portalColumnCount;
		}
	}
}