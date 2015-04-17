package edu.arizona.biosemantics.oto2.steps.server.persist.file;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;
import edu.arizona.biosemantics.oto.steps.shared.rpc.RPCResult;
import edu.arizona.biosemantics.oto2.steps.server.Configuration;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.OntologyFileException;

public class OntologyDAO {

	private edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologyDAO ontologyDAO; 
	
	private static Map<String, OWLOntology> referencedOntologies = new HashMap<String, OWLOntology>();
	
	//one manager manages all the ontologies files, check if an ontology is already being managed before load it, 
	//create ontologyIRI and documentIRI mapping for each loaded ontology.
	private static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private static OWLDataFactory factory = manager.getOWLDataFactory();
	private static OWLClass entity = null;
	private static OWLClass quality = null;
	private static OWLObjectProperty partOf = null;
	private static OWLAnnotationProperty label = null;
	private static OWLAnnotationProperty synAnnotation = null;
	private static OWLAnnotationProperty defAnnotation =null;
	private static OWLAnnotationProperty creationDateAnnotation = null;
	private static OWLAnnotationProperty createdByAnnotation = null;
	private static OWLAnnotationProperty rSynAnnotation = null;
	private static OWLAnnotationProperty nSynAnnotation = null;
	private static OWLAnnotationProperty eSynAnnotation = null;
	private static OWLAnnotationProperty bSynAnnotation = null;
	
	static{
		entity = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/CARO_0000006")); //material anatomical entity
		quality = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/PATO_0000001")); //quality
		partOf = factory.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/BFO_0000050"));
		label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		synAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym"));
		defAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000115"));
		creationDateAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#creation_date"));
		createdByAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#created_by"));
		rSynAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym"));
		nSynAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym"));
		eSynAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym"));
		bSynAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym"));
		
		// loading ontologies takes time, preload them
		for (File file : new File(Configuration.permanentOntologyDirectory).listFiles()) {
			OWLOntology ontology;
			try {
				ontology = manager.loadOntologyFromOntologyDocument(file);
			} catch (OWLOntologyCreationException e) {
				log(LogLevel.ERROR, "Couldn't load ontology", e);
			}
			referencedOntologies.put(FilenameUtils.removeExtension(file.getName()), ontology);
			SimpleIRIMapper mapper = new SimpleIRIMapper(IRI.create(Configuration.oboOntologyBaseIRI + file.getName()), IRI.create(file));
			manager.addIRIMapper(mapper);
		}
	}
	
	private File getCollectionOntologyFile(Collection collection, Ontology ontology) {
		File collectionFile = new File(Configuration.collectionOntologyDirectory, String.valueOf(collection.getId()));
		collectionFile.mkdirs();
		return new File(collectionFile, ontology.getPrefix() + ".owl");
	}
	
	private File getPermanentOntologyFile(Ontology ontology) {
		return new File(Configuration.permanentOntologyDirectory, ontology.getPrefix() + ".owl");
	}

	public void insert(Collection collection, Ontology ontology) throws OntologyFileException {
		File ontologyFile = getCollectionOntologyFile(collection, ontology);
		IRI ontologyIRI = IRI.create(Configuration.etcOntologyBaseIRI + ontology.getPrefix().toLowerCase() + ".owl");
		IRI documentIRI = IRI.create(ontologyFile);
		SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
		manager.addIRIMapper(mapper);
		OWLOntology ont;
		try {
			ont = manager.createOntology(ontologyIRI);
		} catch (OWLOntologyCreationException e) {
			log(LogLevel.ERROR, "Couldn't create ontology", e);
			throw new OntologyFileException(e);
		}
		
		List<Ontology> relevantOntologies = ontologyDAO.getRelevantOntologiesForCollection(collection);
		for(Ontology relevantOntology : relevantOntologies) {
			if(relevantOntology.hasCollectionId()) {
				//upload files from server, add live access to ontology url later
				File relevantOntologyFile = this.getCollectionOntologyFile(collection, relevantOntology);
				IRI toImport = IRI.create(relevantOntologyFile);
				mapper = new SimpleIRIMapper(IRI.create(Configuration.oboOntologyBaseIRI + relevantOntology.getPrefix()), 
						toImport);
				manager.addIRIMapper(mapper);
				OWLImportsDeclaration importDeclaraton = factory.getOWLImportsDeclaration(toImport);
				manager.applyChange(new AddImport(ont, importDeclaraton));
				if (manager.getOntology(toImport) == null)
					try {
						manager.loadOntology(toImport);
					} catch (OWLOntologyCreationException e) {
						log(LogLevel.ERROR, "Couldn't load ontology", e);
						throw new OntologyFileException(e);
					}
			}
		}

		//add annotation properties
		//OWLAnnotationProperty label = factory
		//		.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		/*
		    <owl:AnnotationProperty rdf:about="http://purl.obolibrary.org/obo/IAO_0000115">
	        	<rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">definition</rdfs:label>
	    	</owl:AnnotationProperty>
		 */
		//OWLAnnotationProperty annotation = factory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000115"));
		OWLLiteral literal = factory.getOWLLiteral("definition");
		OWLAnnotation anno = factory.getOWLAnnotation(label,literal);
		OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(defAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*<owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym">
        <rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">has_broad_synonym</rdfs:label>
    	</owl:AnnotationProperty>*/
		
		literal = factory.getOWLLiteral("has_broad_synonym");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(bSynAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#hasExactSynonym">
	        <rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">has_exact_synonym</rdfs:label>
	    </owl:AnnotationProperty>*/
		literal = factory.getOWLLiteral("has_exact_synonym");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(eSynAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym">
	        <rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">has_narrow_synonym</rdfs:label>
	    </owl:AnnotationProperty>*/
		literal = factory.getOWLLiteral("has_narrow_synonym");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(nSynAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym">
	        <rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">has_related_synonym</rdfs:label>
	    </owl:AnnotationProperty>*/
		literal = factory.getOWLLiteral("has_related_synonym");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(rSynAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#created_by"/>*/
		literal = factory.getOWLLiteral("created_by");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(createdByAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#creation_date"/>*/
		literal = factory.getOWLLiteral("creation_date");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(creationDateAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		//entity and quality classes and part_of, has_part relations are imported from ro, a "general" ontology
		//PrefixManager pm = new DefaultPrefixManager(
		//		Configuration.etc_ontology_baseIRI+prefix.toLowerCase()+"#");

		
		/*OWLClass entity = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/CARO_0000006")); //material anatomical entity
		OWLLiteral clabel = factory.getOWLLiteral("material anatomical entity", "en");
		axiom = factory.getOWLDeclarationAxiom(entity);
		manager.addAxiom(ont, axiom);
		axiom = factory.getOWLAnnotationAssertionAxiom(entity.getIRI(), factory.getOWLAnnotation(label, clabel));
		manager.addAxiom(ont, axiom);

		OWLClass quality = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/PATO_0000001")); //quality
		clabel = factory.getOWLLiteral("quality", "en");
		axiom = factory.getOWLDeclarationAxiom(entity);
		manager.addAxiom(ont, axiom);
		axiom = factory.getOWLAnnotationAssertionAxiom(entity.getIRI(), factory.getOWLAnnotation(label, clabel));
		manager.addAxiom(ont, axiom);

		//has_part/part_of inverse object properties
		OWLObjectProperty hasPart = factory.getOWLObjectProperty(":has_part", pm);
		OWLObjectProperty partOf = factory.getOWLObjectProperty(":part_of", pm);
		manager.addAxiom(ont,
				factory.getOWLInverseObjectPropertiesAxiom(hasPart, partOf));

		manager.addAxiom(ont, factory.getOWLTransitiveObjectPropertyAxiom(partOf));
		manager.addAxiom(ont, factory.getOWLTransitiveObjectPropertyAxiom(hasPart));
		*/
		//disjoint entity and quality classes
		axiom = factory.getOWLDisjointClassesAxiom(entity, quality);
		manager.addAxiom(ont, axiom);
		//save ontology to file
		try {
			manager.saveOntology(ont, documentIRI);
		} catch (OWLOntologyStorageException e) {
			log(LogLevel.ERROR, "Couldn't save ontology", e);
			throw new OntologyFileException(e);
		}
	}


	public void update(Collection collection, OntologyClassSubmission submission) {	
		String ontologyPrefix = submission.getOntology().getPrefix();
		File ontologyFile = getCollectionOntologyFile(collection, submission.getOntology());
		OWLOntology ontology = null;
		if(!referencedOntologies.containsKey(ontologyPrefix)) {
			IRI ontologyIRI = IRI.create(Configuration.etcOntologyBaseIRI + ontologyPrefix + ".owl");
			IRI documentIRI = IRI.create(ontologyFile); //"file:/"+Configuration.fileBase+File.separator+filename+".owl");
			SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
			manager.addIRIMapper(mapper);
			ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
			referencedOntologies.put(ontologyPrefix, ontology);
		}else{
			ontology = referencedOntologies.get(ontologyPrefix);
		}
		PrefixManager pm = new DefaultPrefixManager(Configuration.etcOntologyBaseIRI + ontologyPrefix + "#");
		updateOntology(ontology, submission, manager, pm);

		/*int version = 0;
		IRI versionIRI = IRI.create(ontologyIRI + "/version"+version);
        OWLOntologyID newOntologyID = new OWLOntologyID(ontologyIRI, versionIRI);
        SetOntologyID setOntologyID = new SetOntologyID(ont, newOntologyID);
        manager.applyChange(setOntologyID);*/
		manager.saveOntology(ontology, IRI.create(ontologyFile.toURI()));
	}

	private void updateOntology(OWLOntology ont, OntologySubmission submission, OWLOntologyManager manager, PrefixManager pm) {
		 //initialize a reasoner
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		OWLReasonerConfiguration config = new SimpleConfiguration(
				progressMonitor);
		OWLReasoner reasoner = reasonerFactory.createReasoner(ont, config);

		boolean asSynonym = submission.getSubmitAsSynonym();
		if(asSynonym){
			updateOntologyWithNewSynonym(ont, submission,  pm, result, reasoner);
		}else{
			updateOntologyWithNewClass(ont, submission,  pm, result, reasoner);
		}
		
		//consistency checking, if not consistent, the problem need to be fixed manually. 
		reasoner.precomputeInferences();
		boolean consistent = reasoner.isConsistent();
		if(!consistent){
			Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
			Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
			StringBuffer sb = new StringBuffer("");
			if (!unsatisfiable.isEmpty()) {
				sb.append("Warning: After the additions, the following classes have become unsatisfiable. Edit the ontology in protege to correct the problems. \n");
				for (OWLClass cls : unsatisfiable) {
					sb.append("    " + cls+"\n");
				}
				result.setData(null);
				result.setMessage(result.getMessage()+" "+sb.toString());
				result.setSucceeded(true);
				return;
			} 
		}
		return;
	}

	public void setOntologyDAO(
			edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologyDAO ontologyDAO) {
		this.ontologyDAO = ontologyDAO;
	}	
	
}
