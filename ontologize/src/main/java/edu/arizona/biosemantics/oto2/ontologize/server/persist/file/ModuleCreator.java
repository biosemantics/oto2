package edu.arizona.biosemantics.oto2.ontologize.server.persist.file;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;
import edu.arizona.biosemantics.oto2.ontologize.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySubmission;

public class ModuleCreator  {
	
	private OWLOntologyManager owlOntologyManager;
	private OWLOntologyRetriever owlOntologyRetriever;
	private AnnotationsManager annotationsManager;
	private StructuralReasonerFactory owlReasonerFactory;
	private ConsoleProgressMonitor progressMonitor;
	private SimpleConfiguration owlReasonerConfig;
	private OWLAnnotationProperty labelProperty;

	public ModuleCreator(OWLOntologyManager owlOntologyManager, OWLOntologyRetriever owlOntologyRetriever, 
			AnnotationsManager annotationsManager) {
		this.owlOntologyManager = owlOntologyManager;
		this.owlOntologyRetriever = owlOntologyRetriever;
		this.annotationsManager = annotationsManager;
		owlReasonerFactory = new StructuralReasonerFactory();
		progressMonitor = new ConsoleProgressMonitor();
		owlReasonerConfig = new SimpleConfiguration(progressMonitor);
		labelProperty = owlOntologyManager.getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	}
	
	public OWLOntology createModuleFromOwlClass(Collection collection, OWLClass owlClass, Ontology targetOntology) throws Exception {
		OWLOntology owlClassOntology = owlOntologyRetriever.getOWLOntology(collection, owlClass);
		OWLOntology targetOwlOntology = owlOntologyManager.getOntology(OntologyFileDAO.createOntologyIRI(targetOntology));
		Set<OWLEntity> seeds = new HashSet<OWLEntity>();
		seeds.add(owlClass);
		
		File moduleFile = new File(OntologyFileDAO.getCollectionOntologyDirectory(targetOntology), 
				"module." + annotationsManager.get(collection, owlClass, labelProperty) + "." + owlClass.getIRI().getShortForm() + ".owl");
		IRI moduleIRI = IRI.create(moduleFile);
		
		// remove the existing module -- in effect replace the old module with the new one.
		if (moduleFile.exists())
			moduleFile.delete();
		if(owlOntologyManager.getOntology(moduleIRI) != null)
			owlOntologyManager.removeOntology(owlOntologyManager.getOntology(moduleIRI));
			
		SyntacticLocalityModuleExtractor syntacticLocalityModuleExtractor = new SyntacticLocalityModuleExtractor(
				owlOntologyManager, owlClassOntology, ModuleType.STAR);
		OWLReasoner owlReasoner = owlReasonerFactory.createReasoner(targetOwlOntology, owlReasonerConfig);
		OWLOntology moduleOntology = syntacticLocalityModuleExtractor.extractAsOntology(seeds, moduleIRI, -1, 0, owlReasoner); //take all superclass and no subclass into the seeds.
		owlOntologyManager.saveOntology(moduleOntology, moduleIRI);
		OWLImportsDeclaration importDeclaration = owlOntologyManager.getOWLDataFactory().getOWLImportsDeclaration(moduleIRI);
		owlOntologyManager.applyChange(new AddImport(targetOwlOntology, importDeclaration));
		owlOntologyManager.loadOntology(moduleIRI);
		return moduleOntology;
	}
}
