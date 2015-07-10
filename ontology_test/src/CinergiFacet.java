import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;


public class CinergiFacet {

	public static void addMissingFacets(OutputStream os, PrintWriter writer,
			OWLOntologyManager manager, OWLOntology ont, OWLDataFactory df) throws Exception
	{
		Set<OWLOntology> ontologies = manager.getOntologies();
	    
	    OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
	    HashSet<OWLClass> classes = new HashSet<OWLClass>();
        HashSet<OWLAnnotation> labels = new HashSet<OWLAnnotation>();
        
        OWLOntologyWalkerVisitor<Object> visitor = 
        		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c) // visit all classes, give each of them the cinergiFacet property and set to false
	        	{
	        		
	        		if (!classes.contains(c))//hasCinergiFacet(c))	        
	        			classes.add(c);//addFacet(c); // creates cinergiFacet = false annotation
	        		return null;
	        	}
        	}; 
        	
        	
        	walker.walkStructure(visitor);
        	
        	for (OWLClass c : classes) // add cinergiFacets to all classes missing them
        	{
        		if (c.getIRI().getShortForm().equals("Thing"))
        			continue;
				if (!hasCinergiFacet(ont, manager, c, df))
				{
					addFacet(writer, ont, manager, c, df);
				}
        	}
        	
            
            //saveAndUpdate(os, manager, ont);
	} 
	
	public static void correctFacets(OutputStream os, PrintWriter writer, OWLOntologyManager manager, 
			OWLOntology ont, OWLOntology ont_facets, OWLDataFactory df)
	{
		
	}
	
	public static void addFacet(PrintWriter writer, OWLOntology ont, OWLOntologyManager manager, OWLClass c, OWLDataFactory df) 
	{
		
		OWLAnnotation cinergiAnnot = df.getOWLAnnotation(			
				df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiFacet")),
					df.getOWLLiteral("True",
							df.getOWLDatatype(IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral"))) );
		
		addAnnotation(ont, manager, c, cinergiAnnot, df);
		writer.println("cinergiFacet added to class: " + c.getIRI().getShortForm());
	}
	
	public static boolean hasCinergiFacet(OWLOntology ont, OWLOntologyManager manager, OWLClass c, OWLDataFactory df)
	{
		boolean hasFacet = false;
		
		for (OWLOntology o : manager.getOntologies())
		{
			if (c.getAnnotations(o, df.getOWLAnnotationProperty(
					IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiFacet"))).isEmpty() == false) 
			{	
				hasFacet = true;
			}						
		}
		if (!hasFacet)
			System.out.println(c.getIRI().getShortForm() + " does not have cinergiFacet");
		return hasFacet;
	}
	
	public static void addAnnotation(OWLOntology ont, OWLOntologyManager manager, OWLClass c, OWLAnnotation annot, OWLDataFactory df)
	{
		OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), annot);
		manager.applyChange(new AddAxiom(ont, axiom));
	}
	
	public static String annotToString(OWLAnnotation a)
	{
		String str = a.toString().substring(1 + a.toString().indexOf('"'),a.toString().lastIndexOf('"'));
		return str;
	}
	
	public static boolean isCinergiFacet(OWLAnnotation a)
	{
		return (a.getProperty().getIRI().getShortForm().toString().equals("cinergiFacet"));
	}
	
	public static boolean cinergiFacetTrue(OWLAnnotation a)
	{
		return a.getValue().toString().equals("\"True\"");
	}

	public static boolean hasTrueCinergiFacet(OWLClass c, OWLOntologyManager m)
	{
		boolean has = false;
		for (OWLOntology o : m.getOntologies())
		{
			for (OWLAnnotation a : c.getAnnotations(o))
			{
				if (isCinergiFacet(a))
					if (cinergiFacetTrue(a))
						has = true;
			}
		}
		return has;
	}
}
