package cinergi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;

public class Main {

	public static void main(String[] args) throws Exception {
		OutputStream os = new FileOutputStream(new File("cinergiExtensions.owl"));
		PrintWriter writer1 = new PrintWriter("mappings ids unsorted.txt", "UTF-8");
		PrintWriter writer2 = new PrintWriter("preferredLabels unsorted.txt", "UTF-8");
		PrintWriter writer3 = new PrintWriter("facets unsorted.txt", "UTF-8");
		PrintWriter writer4 = new PrintWriter("mappings unsorted.txt", "UTF-8");
		
		PrintWriter writer = new PrintWriter("description.txt", "UTF-8");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = manager.getOWLDataFactory();	
		manager.setSilentMissingImportsHandling(true);
//		OWLOntology ont = manager.loadOntologyFromOntologyDocument(new File("C:/eclipse/workspace/Cinergi/ontologies/cinergiExtensions.owl"));
	//	OWLOntology ont = manager.loadOntologyFromOntologyDocument(IRI.create("http://www.rsc.org/ontologies/CMO/CMO_OWL.owl"));
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl"));
		//OWLOntology extensionsOntology = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl"));
//		OWLOntology cinergiOntology = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl"));
		System.out.println("ontology loaded");
		//CinergiLabelFixer labelFixer = new CinergiLabelFixer(os, writer, manager1, manager2, df, cinergiOntology, old_ontology);
//		CinergiLabelFixer labelFixer = new CinergiLabelFixer(os, writer, manager, df, cinergiOntology);
//		labelFixer.fixLabels();
		
//		Tester.printPreferredLabels(writer, extensionOntology, manager, df);
//		Tester.makeThirdLevelFacetAnnotations(os, writer, manager, extensionsOntology, df);
//		Tester.printThirdLevelFacets(os, writer, manager, ont, df);
		
		
//		Tester.fixEquivalentFacets(os, writer, manager, extensionsOntology, df);
		
		Tester.makeThirdLevelFacetAnnotationsFixed(os, writer1, writer4, manager, ont, df);
		Tester.printPreferredLabels(writer2, ont, manager, df);		
		Tester.printFacets(writer3, ont, manager, df);
		
//		Tester.printTop3Levels(writer, ont, manager, df);
//		Tester.printAllCaps(writer, ont, manager, df);
//		Tester.correctElements(writer, ont, manager, df, os);
		
		// get the facets that have nothing mapped to them
//		Tester.printFacetsNotMappedTo(writer, ont, manager, df, os);
		
		manager.saveOntology(ont, os);		
		os.close();		

//		writer.close();
		writer1.close();
		writer2.close();
		writer3.close();
		writer4.close();

	}

}
