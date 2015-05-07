package edu.arizona.biosemantics.oto2.steps.shared.model.toontology;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface SuperclassProperties  extends PropertyAccess<Superclass> {

	  @Path("id")
	  ModelKeyProvider<Superclass> key();
	   
	  @Path("superclass")
	  LabelProvider<Superclass> nameLabel();
	  
	  ValueProvider<Superclass, Integer> ontologyClassSubmission();
	  
	  ValueProvider<Superclass, String> superClass();

}
