package cinergi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class Main {

	public static void main(String[] args) throws Exception {
		OutputStream os = new FileOutputStream(new File("cinergiExtensions.owl"));
		PrintWriter writer = new PrintWriter("description.txt", "UTF-8");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntologyManager new_manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = manager.getOWLDataFactory();	
		
		IRI cinergi = IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl");
		IRI cinergi_Extensions = IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl");
		
		OWLOntology extensionsOntology = manager.loadOntologyFromOntologyDocument(new File("cinergiExtensions1.owl"));
		OWLOntology cinergiOntology = new_manager.loadOntologyFromOntologyDocument(new File("cinergi.owl"));
		//OWLOntology ont = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl"));
		//OWLOntology ont_facets = facets_manager.loadOntologyFromOntologyDocument(new File("cinergi_facets.owl"));
		
		System.out.println("done loading documents");
		OWLOntology new_ont = new_manager.createOntology(cinergi_Extensions);
		
		OWLImportsDeclaration importDeclaraton =  df.getOWLImportsDeclaration(cinergi); 
		new_manager.applyChange(new AddImport(new_ont, importDeclaraton));
		
		CinergiClassFixer fixer = new CinergiClassFixer(os, writer, manager, new_manager, cinergiOntology, extensionsOntology, new_ont, df);
		fixer.fixClasses();
		//Tester.test(manager, ont, df, writer);
		//CinergiLabel labeller = new CinergiLabel(os, writer, manager, ont, df);
		//CinergiFacet faceter = new CinergiFacet(os, writer, manager, facets_manager, ont, ont_facets, df);
		//CinergiParent parenter = 
	
		//labeller.removeDuplicateLabels(); // go through all classes, if that class is not in 
		//labeller.fixFacetLabels();
		
		//faceter.convertFacetsToBoolean(); // change all cinergiFacets to Boolean True/False
		//faceter.removeFacets();
		
		manager.saveOntology(new_ont, os);
		os.close();
		writer.close();
	}

}
