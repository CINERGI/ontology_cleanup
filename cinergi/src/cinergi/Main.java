package cinergi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class Main {

	public static void main(String[] args) throws Exception {
		OutputStream os = new FileOutputStream(new File("cinergi.owl"));
		PrintWriter writer = new PrintWriter("description.txt", "UTF-8");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntologyManager facets_manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = manager.getOWLDataFactory();		
		//OWLOntology ont = manager.loadOntologyFromOntologyDocument(new File("cinergi_7_7_2015.owl"));
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl"));
		OWLOntology ont_facets = facets_manager.loadOntologyFromOntologyDocument(new File("cinergi_facets.owl"));
		
		System.out.println("done loading documents");
		
		//Tester.test(manager, ont, df, writer);
		//CinergiLabel labeller = new CinergiLabel(os, writer, manager, ont, df);
		CinergiFacet faceter = new CinergiFacet(os, writer, manager, facets_manager, ont, ont_facets, df);
		//CinergiParent parenter = 
		
		//faceter.convertFacetsToBoolean(); // change all cinergiFacets to Boolean True/False
		faceter.removeFacets();
		
		manager.saveOntology(ont, os);
		os.close();
		writer.close();
	}

}
