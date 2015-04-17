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

public interface OntologySynonymSubmissionStatusProperties extends PropertyAccess<OntologySynonymSubmissionStatus> {

	  @Path("id")
	  ModelKeyProvider<OntologySynonymSubmissionStatus> key();
	 
	  ValueProvider<OntologySynonymSubmissionStatus, Integer> ontologyClassSubmissionId();
	  
	  ValueProvider<OntologySynonymSubmissionStatus, String> status();

	  ValueProvider<OntologySynonymSubmissionStatus, String> externalId();
}