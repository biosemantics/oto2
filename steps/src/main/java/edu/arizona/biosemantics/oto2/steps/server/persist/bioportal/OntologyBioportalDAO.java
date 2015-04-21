package edu.arizona.biosemantics.oto2.steps.server.persist.bioportal;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import edu.arizona.biosemantics.bioportal.client.BioPortalClient;
import edu.arizona.biosemantics.bioportal.model.ProvisionalClass;
import edu.arizona.biosemantics.oto2.steps.client.OtoSteps;
import edu.arizona.biosemantics.oto2.steps.server.Configuration;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;

public class OntologyBioportalDAO {

	
	public void insertClassSubmission(Collection collection, OntologyClassSubmission submission) {
		try(BioPortalClient bioPortalClient = new BioPortalClient(Configuration.bioportalUrl, Configuration.bioportalApiKey);) {
			bioPortalClient.open();
			bioPortalClient.postProvisionalClass(createProvisionalClass(collection, submission)));
			
		}
	}

	private ProvisionalClass createProvisionalClass(Collection collection, OntologyClassSubmission submission) {
		ProvisionalClass provisionalClass = new ProvisionalClass();
		provisionalClass.setLabel(submission.getSubmissionTerm());
		List<String> definitions = new LinkedList<String>();
		definitions.add(submission.getDefinition());
		provisionalClass.setDefinition(definitions);
		provisionalClass.setSubclassOf(submission.getSuperclassIRI());
		List<String> synonyms = new LinkedList<String>();
		synonyms.add(submission.getSynonyms());
		provisionalClass.setSynonym(synonyms);
		List<String> ontologies = new LinkedList<String>();
		ontologies.add(submission.getOntology().getIri());
		provisionalClass.setOntology(ontologies);
		provisionalClass.setCreator(OtoSteps.user);
		
		// interact with the server
		Future<ProvisionalClass> result = bioPortalClient.postProvisionalClass(provisionalClass);
		String temporaryId = result.get().getId();
		
		// modify local database
		submission.setTmpID(temporaryId);
		return temporaryId;
	}

	public void insertSynonymSubmission(Collection collection, OntologySynonymSubmission submission) {
		try(BioPortalClient bioPortalClient = new BioPortalClient(Configuration.bioportalUrl, Configuration.bioportalApiKey);) {
			bioPortalClient.open();
			bioPortalClient.postProvisionalClass(createProvisionalClass(collection, submission)));
			
		}
	}

	private ProvisionalClass createProvisionalClass(Collection collection,
			OntologySynonymSubmission submission) {
		// TODO Auto-generated method stub
		return null;
	}

}
