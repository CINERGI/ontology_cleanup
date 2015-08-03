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
//		OutputStream os = new FileOutputStream(new File("cinergi.owl"));
		PrintWriter writer = new PrintWriter("description.txt", "UTF-8");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = manager.getOWLDataFactory();	
		
//		IRI cinergi = IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl");
//		IRI cinergi_Extensions = IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl");
		
		OWLOntology extensionsOntology = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl"));
//		OWLOntology cinergiOntology = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl"));
		
		//CinergiLabelFixer labelFixer = new CinergiLabelFixer(os, writer, manager1, manager2, df, cinergiOntology, old_ontology);
//		CinergiLabelFixer labelFixer = new CinergiLabelFixer(os, writer, manager, df, cinergiOntology);
//		labelFixer.fixLabels();
		
//		manager.saveOntology(cinergiOntology, os);
//		os.close();
		Tester.printPreferredLabels(writer, extensionsOntology, manager, df);
		writer.close();
	}

}
