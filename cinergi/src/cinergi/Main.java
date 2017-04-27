package cinergi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;

public class Main {

	public static void main(String[] args) throws Exception {
	//	OutputStream os = new FileOutputStream(new File("cinergi_elsst.owl"));
	//	OutputStream os = new FileOutputStream(new File("cinergiExtensions.owl"));
		OutputStream os = new FileOutputStream(new File("cinergi.owl"));
	/*	PrintWriter writer1 = new PrintWriter("mappings ids unsorted.txt", "UTF-8");
		PrintWriter writer2 = new PrintWriter("preferredLabels unsorted.txt", "UTF-8");
		PrintWriter writer3 = new PrintWriter("facets unsorted.txt", "UTF-8");
		PrintWriter writer4 = new PrintWriter("mappings unsorted.txt", "UTF-8");
		PrintWriter writer5 = new PrintWriter("parentEdges unsorted.txt", "UTF-8");
	*/	
		PrintWriter writer = new PrintWriter("description.txt", "UTF-8");
	//	PrintWriter writer2 = new PrintWriter("description2.txt", "UTF-8");
	//	PrintWriter writer2 = new PrintWriter("remove list.txt", "UTF-8");
	//	PrintWriter filterList = new PrintWriter("filter.txt", "UTF-8");
	//	PrintWriter nullPaths = new PrintWriter("nullPaths.txt", "UTF-8");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	//	OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = manager.getOWLDataFactory();
		System.out.println(manager.getClass());
	//	OWLDataFactory df2 = manager2.getOWLDataFactory();	
		manager.setSilentMissingImportsHandling(true);
		
	System.out.println("loading...");
		
		
//		OWLOntology ont = manager.loadOntologyFromOntologyDocument(new File("C:/eclipse/workspace/Cinergi/ontologies/cinergiExtensions.owl"));
//		OWLOntology ont = manager.loadOntologyFromOntologyDocument(IRI.create("http://www.rsc.org/ontologies/CMO/CMO_OWL.owl"));
//		OWLOntology ont = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl"));
		OWLOntology cinergi_ont = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl"));
	//	OWLOntology elsst_ont = manager.createOntology(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi_elsst.owl"));
	//	OWLOntology extensions_ont = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl"));
//		OWLOntology geochronology2 = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/geochronology2.owl"));
//		OWLOntology geochronologyImpure = manager2.loadOntologyFromOntologyDocument(new File("C:/Users/Adam/Desktop/geochronologyImpure.owl"));
//		OWLOntology extensions = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl"));
		//CinergiLabelFixer labelFixer = new CinergiLabelFixer(os, writer, manager1, manager2, df, cinergiOntology, old_ontology);
//		CinergiLabelFixer labelFixer = new CinergiLabelFixer(os, writer, manager, df, cinergiOntology);
//		labelFixer.fixLabels();
	System.out.println("loaded");	
//		Tester.printPreferredLabels(writer, extensionOntology, manager, df);
//		Tester.makeThirdLevelFacetAnnotations(os, writer, manager, extensionsOntology, df);
//		Tester.printThirdLevelFacets(os, writer, manager, ont, df);	
//		Tester.fixEquivalentFacets(os, writer, manager, extensionsOntology, df);

		
		// each of these functions take the extensions ontology as parameter
//		Tester.makeThirdLevelFacetAnnotationsFixed(os, writer1, writer4, manager, extensions_ont, df);
//		Tester.printCinergiParentEdges(extensions_ont, writer5, manager, df);
//		Tester.printPreferredLabels(writer2, extensions_ont, manager, df);		
//		Tester.printFacets(writer, extensions_ont, manager, df);

//		Tester.printEquivalentClasses(writer, extensions, manager, df);
//		Tester.addMissingFacets(writer, extensions_ont, manager, df);
		
//		Tester.geologicTimeImpure(os, writer, manager, manager2, geochronology2, geochronologyImpure, df, df2);
//		Tester.printTop3Levels(writer, ont, manager, df);
//		Tester.printAllCaps(writer, ont, manager, df);
//		Tester.correctElements(writer, ont, manager, df, os);
		
		// get the facets that have nothing mapped to them
//		Tester.printFacetsNotMappedTo(writer, ont, manager, df, os);
//		Tester.addSynonymsToGeologicTimes(writer, os, cinergi_ont, manager,df);
//		Tester.synonymsToLabels(writer, os, cinergi_ont, manager, df);
//		manager.saveOntology(extensions_ont, os);
		//manager2.saveOntology(geochronologyImpure, os);		
		
	//	Tester.printSameLabels(writer, filterList, nullPaths, cinergi_ont, manager, df);
	//	Tester.printUnassignedTerms(writer,cinergi_ont, manager, df);
		//Tester.printEquivalentFacets(writer,cinergi_ont, manager, df);

	//	Tester.printTermsNotInTheOntology(writer, writer2);

		
	//	Tester.addToOntology(writer, elsst_ont, manager, df);
	//	manager.saveOntology(elsst_ont, new OWLXMLOntologyFormat(), os);
	//	manager.saveOntology(extensions_ont, os);
		
		
	//	Tester.secondLevelFacetPrint(manager, df, writer);
	//	writer.println("\n\nfinished");
	
	
	//	Tester.addDatasetSynonyms(manager, cinergi_ont, df, writer);
	//	Tester.addHyphenSynonyms(manager, cinergi_ont, df, writer);
		manager.saveOntology(cinergi_ont, new OWLXMLOntologyFormat(), os);
	//	os.close();	
		writer.close();
	
		//	writer2.close();
	//	filterList.close();
	//	nullPaths.close(); 
/*		writer1.close();
		writer2.close();
		writer3.close();
		writer4.close();
		writer5.close();
*/
	}

}
