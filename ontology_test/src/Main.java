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
	public static void main(String[] args) throws Exception
	{
		OutputStream os = new FileOutputStream("cinergi_7_7_2015.owl");
		OutputStream os2 = new FileOutputStream("facet_ontology.owl");
		PrintWriter writer = new PrintWriter("description.txt", "UTF-8");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//		OWLOntology ont = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl"));
		OWLDataFactory df = manager.getOWLDataFactory();		
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(new File("cinergi_7_6_2015.owl"));
//		OWLOntology ont = manager.loadOntologyFromOntologyDocument(new File("cinergi_new1.owl"));
//		
//		
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		//OWLDataFactory df2 = manager2.getOWLDataFactory();
		OWLOntology ont_facets = manager2.loadOntologyFromOntologyDocument(new File("cinergi_facets_7_6_2015.owl")); 
		System.out.println("done loading documents");
//	    
	 //   LabelTestNew.correctLabels(os, writer, manager, ont, df);
     //   CinergiFacet.addMissingFacets(os, writer, manager, ont, df);
	 //	CinergiParent.addParents(os, writer, manager, ont, df);	
		
   	 //	FacetOntology.addLabelsToFacet(os2, writer, manager2, ont_facets, df);
//		CinergiParent.makeCinergiParentsBasedOnFacetFile(os, writer, manager, manager2, ont, ont_facets, df, df2);
		CinergiParent.makeCinergiParentsBasedOnFacetFile(os, writer, manager, manager2, ont, ont_facets, df);
		CinergiParent.addParents(os, writer, manager, ont, df);
		FacetOntology.createFacetOntology(os2, ont, df, manager, writer);
		manager.saveOntology(ont, os);
//		manager2.saveOntology(ont_facets, os2);
//		
		os.close();
		os2.close();
//		
		writer.close();
	}
}
