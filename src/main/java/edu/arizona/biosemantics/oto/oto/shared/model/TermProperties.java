package edu.arizona.biosemantics.oto.oto.shared.model;

import java.util.List;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface TermProperties extends PropertyAccess<Term> {

	  @Path("text")
	  ModelKeyProvider<Term> key();
	   
	  @Path("text")
	  LabelProvider<Term> nameLabel();
	 
	  ValueProvider<Term, String> text();
	   
	  ValueProvider<Term, List<Term>> synonyms();
	
}
