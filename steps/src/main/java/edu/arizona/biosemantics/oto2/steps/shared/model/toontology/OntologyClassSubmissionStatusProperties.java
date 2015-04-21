package edu.arizona.biosemantics.oto2.steps.shared.model.toontology;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.Term;

public interface OntologyClassSubmissionStatusProperties extends PropertyAccess<OntologyClassSubmissionStatus> {

	  @Path("id")
	  ModelKeyProvider<OntologyClassSubmissionStatus> key();
	 
	  ValueProvider<OntologyClassSubmissionStatus, Integer> ontologyClassSubmissionId();
	  
	  ValueProvider<OntologyClassSubmissionStatus, String> status();

	  ValueProvider<OntologyClassSubmissionStatus, String> iri();
}