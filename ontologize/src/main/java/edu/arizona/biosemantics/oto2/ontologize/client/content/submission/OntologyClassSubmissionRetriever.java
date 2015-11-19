package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.Collection;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.HasLabelAndIri;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;

public class OntologyClassSubmissionRetriever {

	public OntologyClassSubmission getSubmissionOfLabelOrIri(HasLabelAndIri hasLabelAndIri, Collection<OntologyClassSubmission> submissions) {
		for(OntologyClassSubmission submission : submissions) {
			if(hasLabelAndIri.hasLabel()) {
				if(submission.getSubmissionTerm().equals(hasLabelAndIri.getLabel())) {
					return submission;
				}
			}
			if(hasLabelAndIri.hasIri()) {
				if(submission.getClassIRI().equals(hasLabelAndIri.getIri())) {
					return submission;
				}
			}
		}
		return null;
	}
	
}
