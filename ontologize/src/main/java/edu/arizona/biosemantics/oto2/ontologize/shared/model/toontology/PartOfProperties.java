package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface PartOfProperties  extends PropertyAccess<PartOf> {

	  @Path("id")
	  ModelKeyProvider<PartOf> key();
	   
	  @Path("iri")
	  LabelProvider<PartOf> nameLabel();
	  
	  ValueProvider<PartOf, Integer> ontologyClassSubmission();
	  
	  ValueProvider<PartOf, String> iri();

	  ValueProvider<PartOf, String> value();
}
