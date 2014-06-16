package edu.arizona.biosemantics.oto.oto.shared.model;

import java.util.List;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface CategoryProperties extends PropertyAccess<Label> {

	  @Path("text")
	  ModelKeyProvider<Label> key();
	   
	  @Path("text")
	  LabelProvider<Label> nameLabel();
	 
	  ValueProvider<Label, String> text();
	   
	  ValueProvider<Label, String> description();
	  
	  ValueProvider<Label, List<Term>> terms();
	
}
