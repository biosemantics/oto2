package edu.arizona.biosemantics.oto2.ontologize.server.persist.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
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
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.ClassExistsException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyFileException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyNotFoundException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.UnsatisfiableClassesException;

public class OntologyFileDAO extends PermanentOntologyFileDAO {
	
	private Collection collection;

	public OntologyFileDAO(Collection collection, OntologyDAO ontologyDBDAO) throws OntologyFileException {
		super(ontologyDBDAO);
		this.collection = collection;
		try {
			for(Ontology ontology : ontologyDBDAO.getAllOntologiesForCollection(collection)) {
				if(!permanentOntologies.containsKey(ontology)) {
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
	}

	public void insertOntology(Ontology ontology, boolean createFile) throws OntologyFileException {	
		if(createFile) {
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
			
		//file already exists, update IRI and load to ontologymanager
		} else {
			File file = getCollectionOntologyFile(ontology);
			try {
				updateOwlOntologyIRI(file, ontology, collection);
			} catch (JDOMException | IOException e) {
				log(LogLevel.ERROR, "Couldn't update owl ontology IRI", e);
				throw new OntologyFileException(e);
			}
			
			log(LogLevel.INFO, "Loading " + file.getAbsolutePath() + " ...");
			try {
				owlOntologyManager.getIRIMappers().add(createMapper(ontology));
				OWLOntology owlOntology = owlOntologyManager.loadOntologyFromOntologyDocument(file);
			} catch (OWLOntologyCreationException | OWLOntologyInputSourceException | UnloadableImportException e) {
				Logger.getLogger(OntologyFileDAO.class).error("Could not load ontology", e);
				throw new OntologyFileException(e);
			}
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
			axiomManager.addPartOfs(collection, owlOntology, submission.getOntology(), owlClass, submission.getPartOfs());
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
		
		if(submission.hasSuperclasses() && !extractedSuperclassModule){
			checkConsistentSuperclassHierarchyWithQualityEntity(submission, owlOntology);
			
			List<Superclass> superclasses = new LinkedList<Superclass>(submission.getSuperclasses());			
			axiomManager.addSuperclasses(collection, owlOntology, submission.getOntology(), owlClass, superclasses,
					submission.getType());
		}
	}

	private void checkConsistentSuperclassHierarchyWithQualityEntity(OntologyClassSubmission submission, OWLOntology owlOntology) throws OntologyFileException {
		List<Superclass> superclasses = submission.getSuperclasses();
		for(Superclass superclass : superclasses) {
			OWLClass superOwlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(superclass.getIri())); 
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
				axiomManager.addPartOfs(collection, targetOwlOntology, submission.getOntology(), owlClass, submission.getPartOfs());
			} catch (Exception e) {
				throw new OntologyFileException(e);
			}
	}

	private void updateSuperclasses(OWLOntology targetOwlOntology, OntologyClassSubmission submission, OWLClass owlClass) throws OntologyFileException, OntologyNotFoundException {
		axiomManager.removeSuperclasses(targetOwlOntology, owlClass);
		List<Superclass> superclassIRIs = new LinkedList<Superclass>(submission.getSuperclasses());
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
		List<OWLClass> affectedClasses = new ArrayList<OWLClass>();
		OWLOntology targetOwlOntology;
		try {
			targetOwlOntology = owlOntologyManager.getOntology(IRI.create(submission.getOntology().getIri()));
			OWLClass owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(submission.getClassIRI()));
			determineAndSetSubmissionType(submission);
			boolean isContained = containsOwlClass(targetOwlOntology, owlClass);
			String label = annotationsManager.get(collection, owlClass, labelProperty);
			if(isContained && label != null && !label.equals(submission.getSubmissionTerm())) {
				//class exists in current/imported ontology => add syn
				affectedClasses.add(owlClass);
					
				axiomManager.addSynonym(targetOwlOntology, owlClass, new Synonym(submission.getSubmissionTerm()));
			} else if(!isContained){
				//an external class does not exist => add class, then add syn
				owlClass = createModuleForSubmissionsClass(collection, submission);  
				axiomManager.addSynonym(targetOwlOntology, owlClass, new Synonym(submission.getSubmissionTerm()));
			}
		} catch(Exception e) {
			throw new OntologyFileException(e);
		}
		
		//add other additional synonyms
		for(Synonym synonym : submission.getSynonyms()){
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

	public void determineAndSetSubmissionType(OntologySynonymSubmission submission) throws OntologyNotFoundException, OntologyFileException {
		OWLClass owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(submission.getClassIRI()));
		OWLOntology classOwlOntology = owlOntologyRetriever.getOWLOntology(collection, owlClass);
		if(ontologyReasoner.isSubclass(classOwlOntology, owlClass, qualityClass)) {
			submission.setType(Type.QUALITY);
		} else if(ontologyReasoner.isSubclass(classOwlOntology, owlClass, entityClass)) {
			submission.setType(Type.ENTITY);
		} else {
			throw new OntologyFileException("Class IRI has to be a subclass of either quality or entity.");
		}
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
						
						axiomManager.removeSynonym(targetOwlOntology, owlClass, new Synonym(submission.getSubmissionTerm()));
					} else if(!isContained){
						//an external class does not exist => add class, then add syn	
						removeModuleOfClass(collection, submission, submission);  
						axiomManager.removeSynonym(targetOwlOntology, owlClass, new Synonym(submission.getSubmissionTerm()));
					}
				} catch(OntologyNotFoundException e) {
					throw new OntologyFileException(e);
				}
			}
		}

		//add other additional synonyms
		for(Synonym synonym : submission.getSynonyms()) {
			if(!synonym.getSynonym().isEmpty()) {
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
	
	public static void main(String[] args) throws JDOMException, IOException {
		/*File file = new File("input/asdf.owl");
		Ontology ontology = new Ontology();
		ontology.setAcronym("muh");
		Collection collection = new Collection();
		collection.setId(333);
		updateOwlOntologyIRI(file, ontology, collection);
		*/
	}
	
	private void updateOwlOntologyIRI(File file, Ontology ontology, Collection collection) throws JDOMException, IOException {
		SAXBuilder sax = new SAXBuilder();
		Document doc = sax.build(file);
		Element root = doc.getRootElement();
		String etcNamespacePrefix = "http://www.etc-project.org/owl/ontologies/";
		
		Namespace xmlNamespace = Namespace.getNamespace("xml", "http://www.w3.org/XML/1998/namespace");
		Namespace owlNamespace = Namespace.getNamespace("owl", "http://www.w3.org/2002/07/owl#");
		Namespace rdfNamespace = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		Namespace rdfsNamespace = Namespace.getNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		List<Namespace> toRemove = new LinkedList<Namespace>();
		for(Namespace namespace : root.getAdditionalNamespaces()) 
			if(namespace.getURI().startsWith(etcNamespacePrefix))
				toRemove.add(namespace);
		for(Namespace remove : toRemove)
			root.removeNamespaceDeclaration(remove);
		
		String newNamespaceUrl = etcNamespacePrefix + collection.getId() + "/" + 
				ontology.getAcronym();
		Namespace newNamespace = Namespace.getNamespace(newNamespaceUrl + "#");
		root.addNamespaceDeclaration(newNamespace);
		Attribute baseAttribute = root.getAttribute("base", xmlNamespace);
		if(baseAttribute != null)
			baseAttribute.setValue(newNamespaceUrl);
		Element ontologyElement = root.getChild("Ontology", owlNamespace);
		if(ontologyElement != null) {
			Attribute aboutAttribute = ontologyElement.getAttribute("about", rdfNamespace);
			if(aboutAttribute != null)
				aboutAttribute.setValue(newNamespaceUrl);
		}
		List<Element> classElements = root.getChildren("Class", owlNamespace);
		for(Element classElement : classElements) {
			Attribute aboutAttribute = classElement.getAttribute("about", rdfNamespace);
			if(aboutAttribute != null && aboutAttribute.getValue().startsWith(etcNamespacePrefix)) {
				Element labelElement = classElement.getChild("label", rdfsNamespace);
				if(labelElement != null) {
					String label = labelElement.getValue();
					aboutAttribute.setValue(newNamespaceUrl + "#" + label);
				}
			}
		}
		
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		xout.output(doc, new FileOutputStream(file));
	}
	
	@Override
	public String getClassLabel(String classIRI) throws OntologyNotFoundException {
		OWLClass owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(classIRI));
		String label = annotationsManager.get(collection, owlClass, labelProperty);
		return label;
	}

}
