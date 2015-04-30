package edu.arizona.biosemantics.oto2.steps.server.persist.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
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

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.steps.client.OtoSteps;
import edu.arizona.biosemantics.oto2.steps.server.Configuration;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologyDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.EntityQualityClass;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.ClassExistsException;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.OntologyFileException;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.OntologyNotFoundException;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.UnsatisfiableClassesException;

public class OntologyDAO2 {

	private static Map<Ontology, OWLOntology> permanentOntologies = new HashMap<Ontology, OWLOntology>();
	
	static {
		OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
		try {
			for(Ontology ontology : new edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologyDAO().getPermanentOntologies()) {
				File file = getPermanentOntologyFile(ontology);
				try {
					owlOntologyManager.getIRIMappers().add(createMapper(ontology));
					OWLOntology owlOntology = owlOntologyManager.loadOntologyFromOntologyDocument(file);
					
					System.out.println(file.getName());
					if(file.getName().equals("ro.owl")) {
						System.out.println(containsOwlClass(owlOntology, owlOntologyManager.getOWLDataFactory().getOWLClass(
								IRI.create("http://purl.obolibrary.org/obo/CARO_0001001"))));
					}
					permanentOntologies.put(ontology, owlOntology);
				} catch (OWLOntologyCreationException e) {
					Logger.getLogger(OntologyDAO2.class).error("Could not load ontology", e);
				}
			}
		} catch(QueryException e) {
			Logger.getLogger(OntologyDAO2.class).error("Could not get permanent ontologies", e);
		}
	}
		
	static OWLOntologyIRIMapper createMapper(Ontology ontology) {
		if(ontology.hasCollectionId())
			return new SimpleIRIMapper(createOntologyIRI(ontology), getLocalOntologyIRI(ontology));
		else
			return new SimpleIRIMapper(createOntologyIRI(ontology), getLocalOntologyIRI(ontology));
	}

	static IRI createClassIRI(OntologyClassSubmission submission) {
		return IRI.create(Configuration.etcOntologyBaseIRI + submission.getOntology().getCollectionId() + "/" +  
				submission.getOntology().getAcronym() + "#" + submission.getSubmissionTerm());
	}
	
	static IRI createOntologyIRI(OntologySubmission submission) {
		return IRI.create(submission.getOntology().getIri());
	}
	
	static IRI createOntologyIRI(Ontology ontology) {
		return IRI.create(ontology.getIri());
	}
	
	static IRI getLocalOntologyIRI(Ontology ontology) {
		if(ontology.hasCollectionId()) {
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
		return new File(Configuration.collectionOntologyDirectory + File.separator + ontology.getCollectionId() + File.separator + ontology.getAcronym());
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

	public OntologyDAO2(Collection collection, OntologyDAO ontologyDBDAO) throws OntologyFileException {
		this.collection = collection;
		this.ontologyDBDAO = ontologyDBDAO;
		this.owlOntologyManager = OWLManager.createOWLOntologyManager();
		try {
			for(Ontology ontology : ontologyDBDAO.getRelevantOntologiesForCollection(collection)) {
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
		axiomManager.addOntologyAxioms(owlOntology);
		
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
		OWLClass newOwlClass = null;		
		if(submission.hasClassIRI()) {
			try {
				OWLOntology ontology = owlOntologyManager.getOntology(createOntologyIRI(submission));
				System.out.println(containsOwlClass(ontology, 
						owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(
						"http://purl.obolibrary.org/obo/CARO_0001001"))));
				newOwlClass = addModuleOfClass(collection, submission, submission);
			} catch (OWLOntologyCreationException | OWLOntologyStorageException	| OntologyNotFoundException e) {
				throw new OntologyFileException(e);
			}
		} else {
			newOwlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(createClassIRI(submission));
		}
		
		OWLOntology targetOwlOntology = owlOntologyManager.getOntology(createOntologyIRI(submission));
		if(containsOwlClass(targetOwlOntology, newOwlClass)) {
			try {
				String definition = annotationsManager.get(collection, newOwlClass, definitionProperty);
				throw new ClassExistsException("class '"+ submission.getSubmissionTerm() + 
						"' exists and defined as:" + definition);
			} catch (OntologyNotFoundException e) {
				throw new ClassExistsException("class '"+ submission.getSubmissionTerm() + 
						"' exists.");
			}
		}
		
		axiomManager.addSuperClassModuleAxioms(targetOwlOntology, submission, newOwlClass);
		axiomManager.addSynonymAxioms(targetOwlOntology, submission, newOwlClass);
		axiomManager.addSuperclassAxioms(collection, submission, newOwlClass);
		axiomManager.addPartOfAxioms(collection, submission, newOwlClass);
		
		try {
			ontologyReasoner.checkConsistency(targetOwlOntology);
		} catch (UnsatisfiableClassesException e) {
			throw new OntologyFileException(e);
		}
		try {
			owlOntologyManager.saveOntology(targetOwlOntology, getLocalOntologyIRI(submission.getOntology()));
		} catch (OWLOntologyStorageException e) {
			log(LogLevel.ERROR, "Couldn't save ontology", e);
			throw new OntologyFileException(e);
		}
		
		return newOwlClass.getIRI().toString();
	}
	
	public void updateClassSubmission(OntologyClassSubmission submission) throws ClassExistsException, OntologyFileException {
		this.removeClassSubmission(submission);
		this.insertClassSubmission(submission);
	}
	
	public void removeClassSubmission(OntologyClassSubmission submission) throws OntologyFileException {
		OWLOntology owlOntology = owlOntologyManager.getOntology(createOntologyIRI(submission));
		this.setDepreceated(createClassIRI(submission), owlOntology);
		try {
			owlOntologyManager.saveOntology(owlOntology, getLocalOntologyIRI(submission.getOntology()));
		} catch (OWLOntologyStorageException e) {
			log(LogLevel.ERROR, "Couldn't save ontology", e);
			throw new OntologyFileException(e);
		}
	}
	
	private OWLClass addModuleOfClass(Collection collection, OntologySubmission submission, EntityQualityClass isEntityQuality) throws OWLOntologyCreationException, OWLOntologyStorageException, OntologyNotFoundException {
		OWLClass newOwlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(isEntityQuality.getClassIRI()));
		OWLOntology targetOwlOntology = owlOntologyManager.getOntology(createOntologyIRI(submission));
		OWLOntology moduleOwlOntology = moduleCreator.createModuleFromOwlClass(collection, submission, newOwlClass);
		//make all added class subclass of quality/entity
		if (isEntityQuality.isEntity()) {
			if (ontologyReasoner.isSubclass(targetOwlOntology, newOwlClass, entityClass)) {
				//result.setMessage(result.getMessage()
				//		+ " Can not add the quality term '" + newTerm
				//		+ "' as a child to entity term '" + newTerm + "'.");
			} else {
				for (OWLClass owlClass : moduleOwlOntology.getClassesInSignature()) {
					axiomManager.addEntitySubclassAxiom(targetOwlOntology, owlClass);
				}
			}
		}
		if (isEntityQuality.isQuality()) {
			if (ontologyReasoner.isSubclass(targetOwlOntology, newOwlClass, qualityClass)) {
				//result.setMessage(result.getMessage()
				//		+ " Can not add the entity term '" + newTerm
				//		+ "' as a child to quality term '" + newTerm + "'.");
			} else {
				for (OWLClass owlClass : moduleOwlOntology.getClassesInSignature()) {
					axiomManager.addQualitySubclassAxiom(targetOwlOntology, owlClass);
				}
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
			String[] classIRIs = submission.getClassIRI().split("\\s*,\\s*");
			for(String classIRI : classIRIs){ //add syn to each of the classes in current ontology class signature
				if(classIRI.isEmpty()) 
					continue;
				
				try {
					OWLClass owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(classIRI));
					boolean isContained = containsOwlClass(targetOwlOntology, owlClass);
					String label = annotationsManager.get(collection, owlClass, labelProperty);
					if(isContained && label != null && !label.equals(submission.getSubmissionTerm())) {
						//class exists in current/imported ontology => add syn
						affectedClasses.add(owlClass);
						
						axiomManager.addSynonymAxioms(targetOwlOntology, submission.getSubmissionTerm(), owlClass);
					} else if(!isContained){
						//an external class does not exist => add class, then add syn	
						owlClass = addModuleOfClass(collection, submission, submission);  
						axiomManager.addSynonymAxioms(targetOwlOntology, submission.getSubmissionTerm(), owlClass);
					}
				} catch(OntologyNotFoundException | OWLOntologyCreationException | OWLOntologyStorageException e) {
					throw new OntologyFileException(e);
				} 
			}
		}

		//add other additional synonyms
		String [] synonyms = submission.getSynonyms().split("\\s*,\\s*");
		for(String synonym : synonyms){
			if(!synonym.isEmpty()) {
				for(OWLClass affectedClass : affectedClasses){
					axiomManager.addSynonymAxioms(targetOwlOntology, synonym, affectedClass);
				}
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
			String[] classIRIs = submission.getClassIRI().split("\\s*,\\s*");
			for(String classIRI : classIRIs){ //add syn to each of the classes in current ontology class signature
				if(classIRI.isEmpty()) 
					continue;
				
				try {
					OWLClass owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(classIRI));
					boolean isContained = containsOwlClass(targetOwlOntology, owlClass);
					String label = annotationsManager.get(collection, owlClass, labelProperty);
					if(isContained && label != null && !label.equals(submission.getSubmissionTerm())) {
						//class exists in current/imported ontology => add syn
						affectedClasses.add(owlClass);
						
						axiomManager.removeSynonymAxioms(targetOwlOntology, submission.getSubmissionTerm(), owlClass);
					} else if(!isContained){
						//an external class does not exist => add class, then add syn	
						removeModuleOfClass(collection, submission, submission);  
						axiomManager.removeSynonymAxioms(targetOwlOntology, submission.getSubmissionTerm(), owlClass);
					}
				} catch(OntologyNotFoundException e) {
					throw new OntologyFileException(e);
				} 
			}
		}

		//add other additional synonyms
		String [] synonyms = submission.getSynonyms().split("\\s*,\\s*");
		for(String synonym : synonyms){
			if(!synonym.isEmpty()) {
				for(OWLClass affectedClass : affectedClasses){
					axiomManager.removeSynonymAxioms(targetOwlOntology, synonym, affectedClass);
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
		axiomManager.addDepreciatedAxiom(iri, owlOntology);
	}
	
	public Collection getCollection() {
		return collection;
	}
}
