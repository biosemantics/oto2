package edu.arizona.biosemantics.oto2.oto.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface OntologyProperties extends PropertyAccess<Ontology> {
  @Path("category")
  ModelKeyProvider<Ontology> key();
   
  @Path("category")
  LabelProvider<Ontology> nameLabel();
 
  ValueProvider<Ontology, String> category();
   
  ValueProvider<Ontology, String> definition();
}