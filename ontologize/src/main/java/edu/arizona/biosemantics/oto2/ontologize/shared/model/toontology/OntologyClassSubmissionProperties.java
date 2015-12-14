package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.util.Date;
import java.util.List;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;

public interface OntologyClassSubmissionProperties extends PropertyAccess<OntologyClassSubmission> {

	  @Path("id")
	  ModelKeyProvider<OntologyClassSubmission> key();
	 
	  ValueProvider<OntologyClassSubmission, Term> term();
	  
	  ValueProvider<OntologyClassSubmission, String> submissionTerm();
	  
	  ValueProvider<OntologyClassSubmission, Ontology> ontology();
	  
	  ValueProvider<OntologyClassSubmission, List<Superclass>> superclassIRIs();
	  
	  ValueProvider<OntologyClassSubmission, String> definition();
	  
	  ValueProvider<OntologyClassSubmission, List<Synonym>> synonyms();
	  
	  ValueProvider<OntologyClassSubmission, String> source();
	  
	  ValueProvider<OntologyClassSubmission, String> sampleSentence();
	  
	  ValueProvider<OntologyClassSubmission, List<PartOf>> partOfIRIs();

	  ValueProvider<OntologyClassSubmission, Boolean> entity();
	  
	  ValueProvider<OntologyClassSubmission, Boolean> quality();
	  
	  ValueProvider<OntologyClassSubmission, String> user();
	  
	  ValueProvider<OntologyClassSubmission, List<OntologyClassSubmissionStatus>> submissionStatuses();

	  ValueProvider<OntologyClassSubmission, String> classIRI();

	  ValueProvider<OntologyClassSubmission, Type> type();
	  
	  ValueProvider<OntologyClassSubmission, Date> lastUpdated();

	  ValueProvider<OntologyClassSubmission, Date> created();
	
}