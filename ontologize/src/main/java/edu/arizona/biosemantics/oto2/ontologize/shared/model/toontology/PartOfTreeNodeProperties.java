package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface PartOfTreeNodeProperties extends PropertyAccess<PartOfTreeNode> {

	  @Path("id")
	  ModelKeyProvider<PartOfTreeNode> key();
	   
	  @Path("text")
	  LabelProvider<PartOfTreeNode> nameLabel();
	 
	  ValueProvider<PartOfTreeNode, String> text();
	  
	  ValueProvider<PartOfTreeNode, PartOf> partOf();
	
}