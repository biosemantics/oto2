package edu.arizona.biosemantics.oto2.ontologize.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface TypedContextProperties extends PropertyAccess<TypedContext> {

	@Path("id")
	ModelKeyProvider<TypedContext> key();

	@Path("source")
	LabelProvider<TypedContext> nameLabel();

	ValueProvider<TypedContext, String> source();

	ValueProvider<TypedContext, String> text();
	
	ValueProvider<TypedContext, String> fullText();
	
	ValueProvider<TypedContext, String> highlightedText();
	
	ValueProvider<TypedContext, String> highlightedFullText();
	
	ValueProvider<TypedContext, String> typeString();
}