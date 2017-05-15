package edu.arizona.biosemantics.oto2.ontologize.client.content.candidates;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.data.shared.IconProvider;

import edu.arizona.biosemantics.oto2.ontologize.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.TermTreeNode;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.TextTreeNode;

public class TermTreeNodeIconProvider implements IconProvider<TextTreeNode> {

	private static TermStatusImages termStatusImages = GWT.create(TermStatusImages.class);

	public TermTreeNodeIconProvider() {
	}

	@Override
	public ImageResource getIcon(TextTreeNode node) {
		if(node instanceof TermTreeNode) {
			TermTreeNode termTreeNode = (TermTreeNode)node;
			if(ModelController.getCollection() != null && ModelController.getCollection().isUsed(termTreeNode.getTerm())) {
				return termStatusImages.green();
			} else if(ModelController.getCollection() != null && ModelController.getCollection().hasExistingIRI(termTreeNode.getTerm())) {
				return termStatusImages.yellow();
			} else if(termTreeNode.getTerm().isRemoved()) {
				return termStatusImages.gray();
			}
			return null;
		}
		return null;
	}

}
