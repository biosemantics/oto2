package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.util.Date;
import java.util.List;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;

public interface OntologySynonymSubmissionProperties extends PropertyAccess<OntologySynonymSubmission> {

	  @Path("id")
	  ModelKeyProvider<OntologySynonymSubmission> key();
	 
	  ValueProvider<OntologySynonymSubmission, Term> term();
	  
	  ValueProvider<OntologySynonymSubmission, String> submissionTerm();
	  
	  ValueProvider<OntologySynonymSubmission, Ontology> ontology();
	  
	  ValueProvider<OntologySynonymSubmission, String> classIRI();
	  
	  ValueProvider<OntologySynonymSubmission, String> classLabel();
	  
	  ValueProvider<OntologySynonymSubmission, List<Synonym>> synonyms();
	  
	  ValueProvider<OntologySynonymSubmission, String> source();
	  
	  ValueProvider<OntologySynonymSubmission, String> sampleSentence();
	  
	  ValueProvider<OntologySynonymSubmission, String> user();
	  	  
	  ValueProvider<OntologySynonymSubmission, List<OntologySynonymSubmissionStatus>> submissionStatuses();
	  
	  ValueProvider<OntologyClassSubmission, Date> lastUpdated();

	  ValueProvider<OntologyClassSubmission, Date> created();

}