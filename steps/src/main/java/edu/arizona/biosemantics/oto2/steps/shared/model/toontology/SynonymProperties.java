package edu.arizona.biosemantics.oto2.steps.shared.model.toontology;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface SynonymProperties  extends PropertyAccess<Synonym> {

	  @Path("id")
	  ModelKeyProvider<Synonym> key();
	   
	  @Path("synonym")
	  LabelProvider<Synonym> nameLabel();
	  
	  ValueProvider<Synonym, Integer> submission();
	  
	  ValueProvider<Synonym, String> synonym();

}
