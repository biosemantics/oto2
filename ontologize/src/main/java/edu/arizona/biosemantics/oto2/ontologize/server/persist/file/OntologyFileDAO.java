package edu.arizona.biosemantics.oto2.ontologize.server.persist.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyInputSourceException;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.OntologyCopy;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.model.UnloadableImportException;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.client.Ontologize;
import edu.arizona.biosemantics.oto2.ontologize.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.OntologyDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySubmission.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.ClassExistsException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyFileException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyNotFoundException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.UnsatisfiableClassesException;

public class OntologyFileDAO {

	private static Map<Ontology, OWLOntology> permanentOntologies = new HashMap<Ontology, OWLOntology>();
	
	public static void loadPermanentOntologies() {
		OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
		try {
			for(Ontology ontology : new edu.arizona.biosemantics.oto2.ontologize.server.persist.db.OntologyDAO().getBioportalOntologies()) {
				File file = getPermanentOntologyFile(ontology);
				Logger.getLogger(OntologyFileDAO.class).info("Loading " + file.getAbsolutePath() + " ...");
				try {
					owlOntologyManager.getIRIMappers().add(createMapper(ontology));
					OWLOntology owlOntology = owlOntologyManager.loadOntologyFromOntologyDocument(file);
					permanentOntologies.put(ontology, owlOntology);
				} catch (OWLOntologyCreationException | OWLOntologyInputSourceException | UnloadableImportException e) {
					Logger.getLogger(OntologyFileDAO.class).error("Could not load ontology", e);
				}
			}
		} catch(QueryException e) {
			Logger.getLogger(OntologyFileDAO.class).error("Could not get permanent ontologies", e);
		}
	}
		
	static OWLOntologyIRIMapper createMapper(Ontology ontology) {
		if(!ontology.isBioportalOntology())
			return new SimpleIRIMapper(createOntologyIRI(ontology), getLocalOntologyIRI(ontology));
		else
			return new SimpleIRIMapper(createOntologyIRI(ontology), getLocalOntologyIRI(ontology));
	}

	static IRI createClassIRI(OntologyClassSubmission submission) {
		return IRI.create(Configuration.etcOntologyBaseIRI + submission.getOntology().getCreatedInCollectionId() + "/" +  
				submission.getOntology().getAcronym() + "#" + submission.getSubmissionTerm());
	}
	
	static IRI createOntologyIRI(OntologySubmission submission) {
		return IRI.create(submission.getOntology().getIri());
	}
	
	static IRI createOntologyIRI(Ontology ontology) {
		return IRI.create(ontology.getIri());
	}
		
	static IRI getLocalOntologyIRI(Ontology ontology) {
		if(!ontology.isBioportalOntology()) {
			return IRI.create(getCollectionOntologyFile(ontology));
		} else 
			return IRI.create(getPermanentOntologyFile(ontology));
	}
	
	static File getPermanentOntologyFile(Ontology ontology) {
		return new File(Configuration.permanentOntologyDirectory, ontology.getAcronym().toLowerCase() + ".owl");
	}
	
	static File getCollectionOntologyFile(Ontology ontology) {
		return new File(getCollectionOntologyDirectory(ontology), ontology.getAcronym().toLowerCase() + ".owl");
	}
	
	static File getCollectionOntologyDirectory(Ontology ontology) {
		return new File(Configuration.collectionOntologyDirectory + File.separator + ontology.getCreatedInCollectionId() + File.separator + ontology.getAcronym());
	}	
	
	static boolean containsOwlClass(OWLOntology owlOntology, OWLClass owlClass) {
		return owlOntology.containsEntityInSignature(owlClass);
	}
	
	private Collection collection;
	private OntologyDAO ontologyDBDAO;
	private OWLOntologyManager owlOntologyManager;

	private AxiomManager axiomManager;
	private ModuleCreator moduleCreator;
	private OWLOntologyRetriever owlOntologyRetriever;
	private AnnotationsManager annotationsManager;
	private OntologyReasoner ontologyReasoner;
	
	//OWL entities
	private OWLClass entityClass;
	private OWLClass qualityClass;
	private OWLAnnotationProperty labelProperty;
	private OWLAnnotationProperty definitionProperty;

	public OntologyFileDAO(Collection collection, OntologyDAO ontologyDBDAO) throws OntologyFileException {
		this.collection = collection;
		this.ontologyDBDAO = ontologyDBDAO;
		this.owlOntologyManager = OWLManager.createOWLOntologyManager();
		try {
			for(Ontology ontology : ontologyDBDAO.getAllOntologiesForCollection(collection)) {
				if(permanentOntologies.containsKey(ontology)) {
					owlOntologyManager.getIRIMappers().add(createMapper(ontology));
					//OWLOntology clonedOwlOntology = owlOntologyManager.createOntology(createOntologyIRI(ontology));
					OWLOntology clonedOwlOntology = clone(permanentOntologies.get(ontology));
				} else {
					owlOntologyManager.getIRIMappers().add(createMapper(ontology));
					//try {
						owlOntologyManager.loadOntologyFromOntologyDocument(getCollectionOntologyFile(ontology));
					//} catch(UnloadableImportException e) { }
				}
			}
		} catch (QueryException | OWLOntologyCreationException e) {
			log(LogLevel.ERROR, "Failed to initialize ontology manager. Relevant ontologies could not be retrieved or created.", e);
			throw new OntologyFileException(e);
		}
		
		labelProperty = owlOntologyManager.getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		entityClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create("http://purl.obolibrary.org/obo/CARO_0000006")); //material anatomical entity
		qualityClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create("http://purl.obolibrary.org/obo/PATO_0000001")); //quality
		definitionProperty = owlOntologyManager.getOWLDataFactory().getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000115"));
		ontologyReasoner = new OntologyReasoner();
		owlOntologyRetriever = new OWLOntologyRetriever(owlOntologyManager, ontologyDBDAO);
		annotationsManager = new AnnotationsManager(owlOntologyRetriever);
		moduleCreator = new ModuleCreator(owlOntologyManager, owlOntologyRetriever, annotationsManager);
		axiomManager = new AxiomManager(owlOntologyManager, moduleCreator, ontologyReasoner);
	}

	//according to http://answers.semanticweb.com/questions/25651/how-to-clone-a-loaded-owl-ontology
	private OWLOntology clone(OWLOntology originalOwlOntology) throws OWLOntologyCreationException {
		return owlOntologyManager.copyOntology(originalOwlOntology, OntologyCopy.DEEP);
		/*try {
			OWLOntology clonedOwlOntology = owlOntologyManager.createOntology(originalOwlOntology.getOntologyID().getOntologyIRI());
			owlOntologyManager.addAxioms(clonedOwlOntology, originalOwlOntology.getAxioms());
			
			for(OWLImportsDeclaration owlImportsDeclaration : originalOwlOntology.getImportsDeclarations()) {
				owlOntologyManager.applyChange(new AddImport(clonedOwlOntology, owlImportsDeclaration));
			}
			
			for(OWLOntology importedOntology : originalOwlOntology.getImports()) {
				clone(importedOntology);
			}
			return clonedOwlOntology;
		} catch(OWLOntologyAlreadyExistsException e) { }
		return null;*/
		
	}

	public void insertOntology(Ontology ontology) throws OntologyFileException {	
		OWLOntology owlOntology = null;
		try {
			owlOntology = owlOntologyManager.createOntology(IRI.create(ontology.getIri()));
		} catch (OWLOntologyCreationException e) {
			log(LogLevel.ERROR, "Couldn't create ontology", e);
			throw new OntologyFileException(e);
		}
		addDefaultImportOntologies(collection, owlOntology);
		axiomManager.initializeOntology(owlOntology);
		
		try {
			owlOntologyManager.saveOntology(owlOntology, getLocalOntologyIRI(ontology));
		} catch (OWLOntologyStorageException e) {
			log(LogLevel.ERROR, "Couldn't save ontology", e);
			throw new OntologyFileException(e);
		}
	}
	
	private void addImportDeclaration(OWLOntology owlOntology, Ontology ontology) throws OntologyFileException {
		IRI relevantIRI = IRI.create(ontology.getIri());
		OWLImportsDeclaration importDeclaraton = owlOntologyManager.getOWLDataFactory().getOWLImportsDeclaration(relevantIRI);
		owlOntologyManager.applyChange(new AddImport(owlOntology, importDeclaraton));
	}

	private void addDefaultImportOntologies(Collection collection, OWLOntology owlOntology) throws OntologyFileException {
		List<Ontology> relevantOntologies = new LinkedList<Ontology>();
		try {
			relevantOntologies = ontologyDBDAO.getRelevantOntologiesForCollection(collection);
		} catch (QueryException e) {
			log(LogLevel.ERROR, "Could not add relevant ontologies", e);
		}
		for(Ontology relevantOntology : relevantOntologies) {
			//only import RO per default at this time
			if(relevantOntology.getIri().equals("http://purl.bioontology.org/obo/OBOREL")) {
				addImportDeclaration(owlOntology, relevantOntology);
			}
			//if(!relevantOntology.hasCollectionId()) {
			//	addImportDeclaration(owlOntology, relevantOntology);
			//}
		}
	}

	public void removeOntology(Ontology ontology) throws OntologyFileException {
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(ontology.getIri()));
		if(owlOntology != null) 
			owlOntologyManager.removeOntology(owlOntology);
		try {
			owlOntologyManager.saveOntology(owlOntology, getLocalOntologyIRI(ontology));
		} catch (OWLOntologyStorageException e) {
			log(LogLevel.ERROR, "Couldn't save ontology", e);
			throw new OntologyFileException(e);
		}
	}

	public String insertClassSubmission(OntologyClassSubmission submission) throws OntologyFileException, ClassExistsException {	
		OWLOntology owlOntology = owlOntologyManager.getOntology(createOntologyIRI(submission));
		OWLClass owlClass = null;	
		boolean extractSuperclassModule = submission.hasClassIRI();
		
		if(extractSuperclassModule) {
			try {
				owlClass = createModuleForSubmissionsClass(collection, submission);
			} catch (Exception e) {
				throw new OntologyFileException(e);
			}
		} else {
			owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(createClassIRI(submission));
		}
		
		if(containsOwlClass(owlOntology, owlClass)) {
			try {
				String definition = annotationsManager.get(collection, owlClass, definitionProperty);
				throw new ClassExistsException("class '"+ submission.getSubmissionTerm() + 
						"' exists and defined as:" + definition);
			} catch (OntologyNotFoundException e) {
				throw new OntologyFileException(e);
			}
		}
		
		if(!extractSuperclassModule) {
			axiomManager.addDeclaration(owlOntology, owlClass);
			axiomManager.addDefinition(owlOntology, owlClass, submission.getDefinition());
			axiomManager.addLabel(owlOntology, owlClass, owlOntologyManager.getOWLDataFactory().getOWLLiteral(
					submission.getSubmissionTerm(), "en"));
			axiomManager.addCreatedBy(owlOntology, owlClass);
			axiomManager.addCreationDate(owlOntology, owlClass);
			axiomManager.addSourceSampleComment(owlOntology, submission, owlClass);
		}
		
		try {
			addSuperclasses(submission, owlClass);
		} catch (OntologyNotFoundException e) {
			throw new OntologyFileException(e);
		}
		axiomManager.addSynonyms(owlOntology, owlClass, submission.getSynonyms());
		try {
			axiomManager.addPartOfs(collection, owlOntology, submission.getOntology(), owlClass, submission.getPartOfIRIs());
		} catch (Exception e) {
			throw new OntologyFileException(e);
		}
				
		try {
			ontologyReasoner.checkConsistency(owlOntology);
		} catch (UnsatisfiableClassesException e) {
			throw new OntologyFileException(e);
		}
		try {
			owlOntologyManager.saveOntology(owlOntology, getLocalOntologyIRI(submission.getOntology()));
		} catch (OWLOntologyStorageException e) {
			log(LogLevel.ERROR, "Couldn't save ontology", e);
			throw new OntologyFileException(e);
		}
		
		return owlClass.getIRI().toString();
	}
	
	private void addSuperclasses(OntologyClassSubmission submission, OWLClass owlClass) throws OntologyFileException, OntologyNotFoundException {
		boolean extractedSuperclassModule = submission.hasClassIRI();
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(submission.getOntology().getIri()));
		
		if(submission.hasSuperclassIRI() && !extractedSuperclassModule){
			checkConsistentSuperclassHierarchyWithQualityEntity(submission, owlOntology);
			
			List<String> superclassIRIs = new LinkedList<String>(submission.getSuperclassIRIs());
			switch(submission.getType()) {
				case ENTITY:
					superclassIRIs.add(entityClass.getIRI().toString());
					break;
				case QUALITY:
					superclassIRIs.add(qualityClass.getIRI().toString());
					break;
				default:
					break;
			}
			
			axiomManager.addSuperclasses(collection, owlOntology, submission.getOntology(), owlClass, superclassIRIs,
					submission.getType());
		}
	}

	private void checkConsistentSuperclassHierarchyWithQualityEntity(OntologyClassSubmission submission, OWLOntology owlOntology) throws OntologyFileException {
		List<String> superclasses = submission.getSuperclassIRIs();
		for(String superclass : superclasses) {
			OWLClass superOwlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(superclass)); 
			if(submission.getType().equals(Type.QUALITY)) 
				if(ontologyReasoner.isSubclass(owlOntology, superOwlClass, entityClass)) 
					throw new OntologyFileException("Can not add the quality term '" + submission.getSubmissionTerm() + 
							"' as a child to entity term '" + superclass + "'.");
			if(submission.getType().equals(Type.ENTITY)) 
				if(ontologyReasoner.isSubclass(owlOntology, superOwlClass, qualityClass)) 
					throw new OntologyFileException("Can not add the entity term '" + submission.getSubmissionTerm() + 
							"' as a child to quality term '" + superclass + "'.");
		}
	}

	public void updateClassSubmission(OntologyClassSubmission submission) throws OntologyFileException {
		OWLOntology targetOwlOntology = owlOntologyManager.getOntology(createOntologyIRI(submission));
		OWLClass owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(createClassIRI(submission));
		//other fiels of submission, other than the ones treated below, are not allowed to change
		try {
			updateSuperclasses(targetOwlOntology, submission, owlClass);
		} catch(OntologyNotFoundException e) {
			throw new OntologyFileException(e);
		}
		updatePartOfs(targetOwlOntology, submission, owlClass);
		updateDefinition(targetOwlOntology, submission, owlClass);
		updateSampleSentence(targetOwlOntology, submission, owlClass);
		updateSource(targetOwlOntology, submission, owlClass);
		updateSynonyms(targetOwlOntology, submission, owlClass);
		try {
			owlOntologyManager.saveOntology(targetOwlOntology, getLocalOntologyIRI(submission.getOntology()));
		} catch (OWLOntologyStorageException e) {
			log(LogLevel.ERROR, "Couldn't save ontology", e);
			throw new OntologyFileException(e);
		}
	}
	
	private void updateSynonyms(OWLOntology targetOwlOntology, OntologyClassSubmission submission, OWLClass owlClass) {
		axiomManager.removeSynonyms(targetOwlOntology, owlClass);
		axiomManager.addSynonyms(targetOwlOntology, owlClass, submission.getSynonyms());
	}

	private void updateSampleSentence(OWLOntology targetOwlOntology, OntologyClassSubmission submission, OWLClass owlClass) {
		axiomManager.removeSourceSampleComment(targetOwlOntology, owlClass);
		axiomManager.addSourceSampleComment(targetOwlOntology, submission, owlClass);
	}

	private void updateDefinition(OWLOntology targetOwlOntology, OntologyClassSubmission submission, OWLClass owlClass) {
		axiomManager.removeDefinition(targetOwlOntology, owlClass);
		axiomManager.addDefinition(targetOwlOntology, owlClass, submission.getDefinition());
	}

	private void updatePartOfs(OWLOntology targetOwlOntology, OntologyClassSubmission submission, OWLClass owlClass) throws OntologyFileException {
		axiomManager.removePartOfs(targetOwlOntology, owlClass);
		if(submission.getType().equals(Type.ENTITY)) 
			try {
				axiomManager.addPartOfs(collection, targetOwlOntology, submission.getOntology(), owlClass, submission.getPartOfIRIs());
			} catch (Exception e) {
				throw new OntologyFileException(e);
			}
	}

	private void updateSuperclasses(OWLOntology targetOwlOntology, OntologyClassSubmission submission, OWLClass owlClass) throws OntologyFileException, OntologyNotFoundException {
		axiomManager.removeSuperclasses(targetOwlOntology, owlClass);
		List<String> superclassIRIs = submission.getSuperclassIRIs();
		switch(submission.getType()) {
			case ENTITY:
				superclassIRIs.add(entityClass.getIRI().toString());
				break;
			case QUALITY:
				superclassIRIs.add(qualityClass.getIRI().toString());
				break;
			default:
				break;
		}
		axiomManager.addSuperclasses(collection, targetOwlOntology, submission.getOntology(), owlClass, superclassIRIs, submission.getType());
	}

	private void updateSource(OWLOntology targetOwlOntology, OntologyClassSubmission submission, OWLClass owlClass) {
		axiomManager.removeSourceSampleComment(targetOwlOntology, owlClass);
		axiomManager.addSourceSampleComment(targetOwlOntology, submission, owlClass);
	}

	/*private void removeClassSubmission(OntologyClassSubmission submission) throws OntologyFileException {
		OWLClass owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(submission.getClassIRI()));
		OWLOntology owlOntology = owlOntologyManager.getOntology(createOntologyIRI(submission));
		
		Set<OWLAxiom> axiomsToRemove = new HashSet<OWLAxiom>();
        for (OWLAxiom axiom : owlOntology.getAxioms())
            if (axiom.getSignature().contains(owlClass)) 
                axiomsToRemove.add(axiom);
        
		owlOntologyManager.removeAxioms(owlOntology, axiomsToRemove);
		createClassIRI(submission);
		
		try {
			owlOntologyManager.saveOntology(owlOntology, getLocalOntologyIRI(submission.getOntology()));
		} catch (OWLOntologyStorageException e) {
			log(LogLevel.ERROR, "Couldn't save ontology", e);
			throw new OntologyFileException(e);
		}
	}*/

	public void depcrecateClassSubmission(OntologyClassSubmission submission) throws OntologyFileException {
		OWLOntology owlOntology = owlOntologyManager.getOntology(createOntologyIRI(submission));
		this.setDepreceated(createClassIRI(submission), owlOntology);
		try {
			owlOntologyManager.saveOntology(owlOntology, getLocalOntologyIRI(submission.getOntology()));
		} catch (OWLOntologyStorageException e) {
			log(LogLevel.ERROR, "Couldn't save ontology", e);
			throw new OntologyFileException(e);
		}
	}
	
	private OWLClass createModuleForSubmissionsClass(Collection collection, OntologySubmission submission) throws Exception {
		OWLClass newOwlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(submission.getClassIRI()));
		OWLOntology targetOwlOntology = owlOntologyManager.getOntology(createOntologyIRI(submission));
		OWLOntology moduleOwlOntology = moduleCreator.createModuleFromOwlClass(collection, newOwlClass, submission.getOntology());
		if (submission.getType().equals(Type.ENTITY)) {
			if (!ontologyReasoner.isSubclass(targetOwlOntology, newOwlClass, entityClass)) {
				for (OWLClass owlClass : moduleOwlOntology.getClassesInSignature()) {
					axiomManager.addEntitySubclass(targetOwlOntology, owlClass);
				}	
			} else {
				throw new Exception("class can not be of type entity and at the same time not be subclass of entity");
			}
		}
		if (submission.getType().equals(Type.QUALITY)) {
			if (!ontologyReasoner.isSubclass(targetOwlOntology, newOwlClass, qualityClass)) {
				for (OWLClass owlClass : moduleOwlOntology.getClassesInSignature()) {
					axiomManager.addQualitySubclass(targetOwlOntology, owlClass);
				}
			} else {
				throw new Exception("class can not be of type quality and at the same time not be subclass of quality");
			}
		}
		return newOwlClass;
	}	
	
	private void removeModuleOfClass(Collection collection, OntologySynonymSubmission submission, OntologySynonymSubmission isEntityQuality) {

	}

	public String insertSynonymSubmission(OntologySynonymSubmission submission) throws OntologyFileException { 
		OWLOntology targetOwlOntology = owlOntologyManager.getOntology(IRI.create(submission.getOntology().getIri()));
		List<OWLClass> affectedClasses = new ArrayList<OWLClass>();
		if(submission.hasClassIRI()) {
			try {
				OWLClass owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(submission.getClassIRI()));
				boolean isContained = containsOwlClass(targetOwlOntology, owlClass);
				String label = annotationsManager.get(collection, owlClass, labelProperty);
				if(isContained && label != null && !label.equals(submission.getSubmissionTerm())) {
					//class exists in current/imported ontology => add syn
					affectedClasses.add(owlClass);
					
					axiomManager.addSynonym(targetOwlOntology, owlClass, submission.getSubmissionTerm());
				} else if(!isContained){
					//an external class does not exist => add class, then add syn	
					owlClass = createModuleForSubmissionsClass(collection, submission);  
					axiomManager.addSynonym(targetOwlOntology, owlClass, submission.getSubmissionTerm());
				}
			} catch(Exception e) {
				throw new OntologyFileException(e);
			}
		}

		//add other additional synonyms
		for(String synonym : submission.getSynonyms()){
			for(OWLClass affectedClass : affectedClasses){
				axiomManager.addSynonym(targetOwlOntology, affectedClass, synonym);
			}
		}
		
		//fill in more info to submission so the UI can present complete matching info.
		StringBuilder termString = new StringBuilder();
		StringBuilder defString = new StringBuilder();
		StringBuilder idString = new StringBuilder();
		StringBuilder superString = new StringBuilder();
		for(OWLClass affectedClass : affectedClasses) {
			try {
				//OWLOntology affectedOwlOntology = this.getOWLOntology(affectedClass);
				
				termString.append(annotationsManager.get(collection, affectedClass, labelProperty) + ";");
				defString.append(annotationsManager.get(collection, affectedClass, definitionProperty) + ";");
				idString.append(affectedClass.getIRI().toString() + ";");
				superString.append(getSuperClassesString(collection, affectedClass) + ";");
			} catch(OntologyNotFoundException e) {
				throw new OntologyFileException(e);
			}
		}
		
		
		//submission.setClassID(termString.replaceFirst(";$", ""));
		//submission.setDefinition(defString.replaceFirst(";$", ""));
		//submission.setPermanentID(idString.replaceFirst(";$", ""));
		//submission.setSuperClass(superString.replaceFirst(";$", ""));	
		//submission.setTmpID(""); 
		
		try {
			owlOntologyManager.saveOntology(targetOwlOntology, getLocalOntologyIRI(submission.getOntology()));
		} catch (OWLOntologyStorageException e) {
			log(LogLevel.ERROR, "Couldn't save ontology", e);
			throw new OntologyFileException(e);
		}
		return submission.getClassIRI().toString();
	}	

	public void updateSynonymSubmission(OntologySynonymSubmission submission) throws OntologyFileException {
		this.removeSynonymSubmission(submission);
		this.insertSynonymSubmission(submission);
	}	

	public void removeSynonymSubmission(OntologySynonymSubmission submission) throws OntologyFileException {
		OWLOntology targetOwlOntology = owlOntologyManager.getOntology(IRI.create(submission.getOntology().getIri()));
		List<OWLClass> affectedClasses = new ArrayList<OWLClass>();
		if(submission.hasClassIRI()) {
			String classIRI = submission.getClassIRI();
			if(!classIRI.isEmpty()) {
				try {
					OWLClass owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(classIRI));
					boolean isContained = containsOwlClass(targetOwlOntology, owlClass);
					String label = annotationsManager.get(collection, owlClass, labelProperty);
					if(isContained && label != null && !label.equals(submission.getSubmissionTerm())) {
						//class exists in current/imported ontology => add syn
						affectedClasses.add(owlClass);
						
						axiomManager.removeSynonym(targetOwlOntology, owlClass, submission.getSubmissionTerm());
					} else if(!isContained){
						//an external class does not exist => add class, then add syn	
						removeModuleOfClass(collection, submission, submission);  
						axiomManager.removeSynonym(targetOwlOntology, owlClass, submission.getSubmissionTerm());
					}
				} catch(OntologyNotFoundException e) {
					throw new OntologyFileException(e);
				}
			}
		}

		//add other additional synonyms
		for(String synonym : submission.getSynonyms()) {
			if(!synonym.isEmpty()) {
				for(OWLClass affectedClass : affectedClasses){
					axiomManager.removeSynonym(targetOwlOntology, affectedClass, synonym);
				}
			}
		}
		
		try {
			owlOntologyManager.saveOntology(targetOwlOntology, getLocalOntologyIRI(submission.getOntology()));
		} catch (OWLOntologyStorageException e) {
			log(LogLevel.ERROR, "Couldn't save ontology", e);
			throw new OntologyFileException(e);
		}
	}
	
	private String getSuperClassesString(Collection collection, OWLClass owlClass) throws OntologyNotFoundException {
		java.util.Collection<OWLClassExpression> supers = 
				EntitySearcher.getSuperClasses(owlClass, owlOntologyRetriever.getOWLOntology(collection, owlClass));
		Iterator<OWLClassExpression> it = supers.iterator();
		String superClassesString ="";
		while(it.hasNext()){
			OWLClassExpression owlClassExpression = it.next();
			if(owlClassExpression instanceof OWLClass){ 
				superClassesString += annotationsManager.get(collection, (OWLClass)owlClassExpression, labelProperty) + ",";
			}
		}
		return superClassesString;
	}
	
	private void setDepreceated(IRI iri, OWLOntology owlOntology) {
		axiomManager.deprecate(iri, owlOntology);
	}
	
	public Collection getCollection() {
		return collection;
	}
}
